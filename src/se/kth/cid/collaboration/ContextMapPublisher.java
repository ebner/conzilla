/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.component.storage.RemoteStorage;
import se.kth.cid.component.storage.RemoteStorageException;
import se.kth.cid.component.storage.RemoteStorageHelper;
import se.kth.cid.concept.Concept;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.rdf.RDFModel;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;
import se.kth.nada.kmr.collaborilla.client.CollaborillaException;
import se.kth.nada.kmr.collaborilla.client.CollaborillaServiceClient;
import se.kth.nada.kmr.collaborilla.client.CollaborillaStatefulClient;
import se.kth.nada.kmr.collaborilla.client.CollaborillaException.ErrorCode;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Provides wrapping methods to easily publish context-maps and containers to
 * the collaboration directory.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ContextMapPublisher extends PropertyChangeSupport {

	public static String PROP_PROGRESS_INFO = "publishing.progress.info";

	public static String PROP_PROGRESS_ERROR = "publishing.progress.error";

	public static String PROP_PROGRESS_PERCENTAGE = "publishing.progress.percentage";

	public static String PROP_PROGRESS_FINISHED = "publishing.progress.finished";

	public static String PROP_PROGRESS_CANCELLED = "publishing.progress.cancelled";

	private MapController controller;

	private ContextMap contextMap;

	private Session session;

	private CollaborillaStatefulClient collabClient;

	private int percentage;

	private String information;

	private LocationInformation locationInfo;

	private String mapRDFInfo, infoRDFInfo;

	private String revisionInfo, revisionPres;

	private CollaborillaSupport collabSupport;
	
	private CollaborillaConfiguration collabConfig;

	public ContextMapPublisher(MapController controller, ContextMap contextMap,
			LocationInformation location, String mapRDFInfo, String infoRDFInfo) {
		super("ContextMapPublisher");
		this.controller = controller;
		this.contextMap = contextMap;
		this.session = contextMap.getComponentManager().getEditingSesssion();
		this.locationInfo = location;
		this.mapRDFInfo = mapRDFInfo;
		this.infoRDFInfo = infoRDFInfo;
		this.collabSupport = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		this.collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
		this.collabClient = collabSupport.getStatefulClient();
	}

	/**
	 * Starts the publishing process and takes care of the progress signalling.
	 */
	public void publish() {
		//Update, just in case the session has been changed.
		this.session = contextMap.getComponentManager().getEditingSesssion();
		try {
			// Publish the files to some public space
			publishContainers();

			// Publish to Collaborilla
			printInformation("Connecting to Collaborilla: " + collabConfig.getCollaborillaServer());
			collabClient.connect();
			setPercentage(50);

			publishMapMetaData();
			setPercentage(65);

			publishInformationContainerMetaData();
			setPercentage(80);

			publishPresentationContainerMetaData();
			setPercentage(95);
			
			printInformation("Disconnecting from Collaborilla");
			collabClient.disconnect();
			setPercentage(100);

			// remove entually existing contribution information from the store
			ContributionInformationStore infoStore =
				ContributionInformationDiskStore.getContributionInformationStore();
			infoStore.removeMetaData(session.getContainerURIForConcepts());
			
			// We're done here
			finish();
		} catch (Exception e) {
			printError(e.getMessage());
			// TODO rollback (metadata) comes in here
			cancel();
		}
	}

	/* Publishing internals */

	/**
	 * Publishes the metadata of the context-map.
	 * 
	 * @throws CollaborillaException
	 */
	private void publishMapMetaData() throws CollaborillaException {
		collabClient.setIdentifier(contextMap.getURI(), true);
		collabClient.createRevision();
		collabClient.setType(CollaborillaServiceClient.TYPE_CONTEXTMAP);
		
		printInformation("Publishing meta-data for context-map: " + contextMap.getURI());
		collabClient.setMetaData(mapRDFInfo);

//		printInformation("Cleaning up container dependencies");
//		collabClient.clearRequiredContainers();
//		collabClient.clearOptionalContainers();

		printInformation("Adding container dependencies");
		addDependencies();
		CollaborillaDataSet dataSet = collabClient.getDataSet();
		cacheDataSet(dataSet);
	}

	/**
	 * Checks and possibly marks (in collaborilla) the containers of the current
	 * session as dependent on the current contextmap. The presentation
	 * container is marked as required if it is the
	 * {@link Component#getLoadContainer()} of the contextmap, optional
	 * otherwise. If the information container is dependant, i.e. if it is a
	 * relevant container (see {@link ComponentManager#getLoadedRelevantContainers()}) of
	 * any of the contextmaps concepts, it is marked as dependant. The dependant
	 * mark of the information container follows the presentation containers
	 * dependant mark, i.e. if the presentation container is required, so is the
	 * information container.
	 * 
	 * If in the current session you have included a bunch of concepts that have
	 * been developed in another session (another load and relevant containers)
	 * corresponding containers needs to be made dependant as well.
	 * 
	 * 
	 * @throws CollaborillaException
	 */
	private void addDependencies() throws CollaborillaException {
		String presentationURI = session.getContainerURIForLayouts();
		String informationURI = session.getContainerURIForConcepts();
		if (presentationContainerIsRequired()) {
			//Add the presentation container as required.
			publishRequiredContainer(presentationURI);
			
			//Add the information container as dependant, if it is not the same as the presentation container and
			//it is dependant upon by some of the concepts.
			if (!informationURI.equals(presentationURI)
					&& informationContainerIsDependantUpon()) {
				publishRequiredContainer(informationURI);
			}
			
			//Find containers of concepts included in this map.
			Set<URI> optional = findContainersForIncludedConcepts(false);
			Set<URI> required = findContainersForIncludedConcepts(true);
			optional.removeAll(required);
			//Add all containers that are load containers of included concepts as required.
			for (Iterator iter = required.iterator(); iter.hasNext();) {
				publishRequiredContainer(((URI) iter.next()).toString());
			}

			//Add all containers that are relevant containers (load containers excluded) of included concepts as optional.
			for (Iterator iter = optional.iterator(); iter.hasNext();) {
				publishOptionalContainer(((URI) iter.next()).toString());
			}
		} else {
			//Adds the presentation container of the current session as optional
			publishOptionalContainer(presentationURI);
			
			//Adds the information container of the current session as optional,
			//if it is not the same as the presentation container and some concepts
			//dependns upon it.
			if (!informationURI.equals(presentationURI)
					&& informationContainerIsDependantUpon()) {
				publishOptionalContainer(informationURI);
			}
			
			//Find containers of concepts included in this map.
			Set optional = findContainersForIncludedConcepts(false);

			//Add all containers that are relevant containers of included concepts as optional.
			for (Iterator iter = optional.iterator(); iter.hasNext();) {
				publishOptionalContainer(((URI) iter.next()).toString());
			}			
		}
	}

	/**
	 * Publishes a container as required, if it is already in the optional list it
	 * is removed from there first.
	 * 
	 * @param uri the URI of the container to publish
	 * @throws CollaborillaException 
	 */
	private void publishRequiredContainer(String uri) throws CollaborillaException{
		try {
			Set optionalContainers = new HashSet();
			try {
				optionalContainers = collabClient.getOptionalContainers();
			} catch (CollaborillaException ce) {
				if (ce.getResultCode() != ErrorCode.SC_NO_SUCH_ATTRIBUTE) {
					throw ce;
				}
			}
			if (!optionalContainers.contains(uri)) {
				collabClient.addRequiredContainer(uri);
			} else {
				collabClient.removeOptionalContainer(uri);
				collabClient.addRequiredContainer(uri);
			}
		} catch (CollaborillaException e) {
			if (e.getResultCode() != ErrorCode.SC_ATTRIBUTE_OR_VALUE_EXISTS) {
				throw e;
			}
		}
	}

	/**
	 * Publishes a container as optional, if it already in the list of required 
	 * containers it fails silently.
	 * 
	 * @param uri the URI of the container to publish.
	 * @throws CollaborillaException
	 */
	private void publishOptionalContainer(String uri) throws CollaborillaException {
		try {
			Set requiredContainers = new HashSet();
			try {
				requiredContainers = collabClient.getRequiredContainers();
			} catch (CollaborillaException ce) {
				if (ce.getResultCode() != ErrorCode.SC_NO_SUCH_ATTRIBUTE) {
					throw ce;
				}
			}
			if (!requiredContainers.contains(uri)) {
				collabClient.addOptionalContainer(uri);
			}
		} catch (CollaborillaException e) {
			if (e.getResultCode() != ErrorCode.SC_ATTRIBUTE_OR_VALUE_EXISTS) {
				throw e;
			}
		}
	}

	/**
	 * Publishes the metadata of the information container.
	 * 
	 * @throws CollaborillaException
	 */
	private void publishInformationContainerMetaData() throws CollaborillaException {
		String infoContURI = session.getContainerURIForConcepts();
		printInformation("Publishing meta-data for container: " + infoContURI);
		collabClient.setIdentifier(infoContURI, true);
		collabClient.createRevision();
		collabClient.setType(CollaborillaServiceClient.TYPE_CONTAINER);
		collabClient.setMetaData(infoRDFInfo);
		try {
			//collabClient.addLocation(buildRemoteURL(locationInfo.getPublicAccessLocation(), infoContURI));
			Set<String> infoLocation = new HashSet<String>();
			infoLocation.add(buildRemoteURL(locationInfo.getPublicAccessLocation(), infoContURI));
			collabClient.setLocations(infoLocation);
		} catch (CollaborillaException e) {
			if (e.getResultCode() != ErrorCode.SC_ATTRIBUTE_OR_VALUE_EXISTS) {
				throw e;
			}
		}
		if (revisionInfo != null) {
			collabClient.setContainerRevision(revisionInfo);
		}
		
		CollaborillaDataSet dataSet = collabClient.getDataSet();
		cacheDataSet(dataSet);
	}

	/**
	 * Publishes the metadata of the presentation container.
	 * 
	 * @throws CollaborillaException
	 */
	private void publishPresentationContainerMetaData() throws CollaborillaException {
		String infoContURI = session.getContainerURIForConcepts();
		String presContURI = session.getContainerURIForLayouts();
		if (infoContURI.equals(presContURI)) {
			// the containers are the same entity
			return;
		}
		printInformation("Publishing meta-data for container: " + presContURI);
		collabClient.setIdentifier(presContURI, true);
		collabClient.createRevision();
		collabClient.setType(CollaborillaServiceClient.TYPE_CONTAINER);
		
		// We copy the metadata from the information container to the
		// presentation container. We do it this way because the URIs
		// have to be adapted to this container.
		Model pmodel = ModelFactory.createDefaultModel();
        StringReader sr = new StringReader(infoRDFInfo);
        pmodel.read(sr, null);
        StmtIterator stmts = pmodel.listStatements(pmodel.createResource(infoContURI), null, (RDFNode) null);
        HashSet toAdd = new HashSet();
        while(stmts.hasNext()) {
        	toAdd.add(stmts.next());
        	stmts.remove();
        }
        for (Iterator toAddIt = toAdd.iterator(); toAddIt.hasNext();) {
			Statement st = (Statement) toAddIt.next();
			pmodel.add(pmodel.createStatement(pmodel.createResource(presContURI), st.getPredicate(), st.getObject()));
		}
		StringWriter sw = new StringWriter();
		pmodel.write(sw, "RDF/XML-ABBREV");

		collabClient.setMetaData(sw.toString());
		//If the presentationContainer is distinct from the informationContainer
		//they should be dependent on each other, this allows better UI.
		if (!presContURI.equals(infoContURI)) {
			publishRequiredContainer(infoContURI);
		}
		try {
			//collabClient.addLocation(buildRemoteURL(locationInfo.getPublicAccessLocation(), presContURI));
			Set<String> presLocation = new HashSet<String>();
			presLocation.add(buildRemoteURL(locationInfo.getPublicAccessLocation(), presContURI));
			collabClient.setLocations(presLocation);
		} catch (CollaborillaException e) {
			if (e.getResultCode() != ErrorCode.SC_ATTRIBUTE_OR_VALUE_EXISTS) {
				throw e;
			}
		}
		if (revisionPres != null) {
			collabClient.setContainerRevision(revisionPres);
		}
		
		cacheDataSet(collabClient.getDataSet());
	}

	/**
	 * Publishes the containers to remote storage.
	 * 
	 * @throws RemoteStorageException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private void publishContainers() throws RemoteStorageException, URISyntaxException, IOException {
		revisionInfo = null;
		revisionPres = null;

		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		String conceptContURI = session.getContainerURIForConcepts();
		String layoutContURI = session.getContainerURIForLayouts();
		Container conceptCont = store.getContainerManager().getContainer(conceptContURI);
		Container layoutCont = store.getContainerManager().getContainer(layoutContURI);

		URI remoteBaseURI = new URI(locationInfo.getPublishingLocation());
		RemoteStorage remote = RemoteStorageHelper.getRemoteStorage(remoteBaseURI);
		remote.connect();
		setPercentage(5);

		String remoteInfoCont = buildRemoteURL(locationInfo.getPublishingLocation(), conceptContURI);
		printInformation("Uploading container " + conceptContURI + " to " + remoteInfoCont);
		remote.put(remoteInfoCont, getInputStreamOfContainer(conceptCont));
		if (remote.isVersioned(remoteInfoCont)) {
			revisionInfo = remote.getVersionName(remoteInfoCont);
			printInformation("Added as revision " + revisionInfo);
		}
		setPercentage(20);

		if (!conceptContURI.equals(layoutContURI)) {
			String remotePresCont = buildRemoteURL(locationInfo.getPublishingLocation(), layoutContURI);
			printInformation("Uploading container " + layoutContURI + " to " + remotePresCont);
			remote.put(remotePresCont, getInputStreamOfContainer(layoutCont));
			if (remote.isVersioned(remotePresCont)) {
				revisionPres = remote.getVersionName(remotePresCont);
				printInformation("Added as revision " + revisionPres);
			}
			setPercentage(40);
		}

		remote.disconnect();
		setPercentage(45);
	}
	
	private void cacheDataSet(CollaborillaDataSet dataSet) throws CollaborillaException {
		MetaDataCache cache = ConzillaKit.getDefaultKit().getResourceStore().getMetaDataCache();
		cache.putDataSet(dataSet.getIdentifier(), dataSet);
	}

	/* Helpers */

	/**
	 * Converts a container into an InputStream.
	 * 
	 * @param container
	 *            Container to be read out of.
	 * @return InputStream with the data of the container.
	 * @throws IOException
	 */
	private InputStream getInputStreamOfContainer(final Container container) throws IOException {
		// We could also use piped streams, but this requires
		// threading and leads somehow to a broken pipe... to be done.
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		((RDFModel) container).write(out);
		return new ByteArrayInputStream(out.toByteArray());
	}

	/**
	 * Constructs a full URL out of a base URL and a URI of a container.
	 * 
	 * @param baseURL
	 *            The URL of the location where the container is supposed to get
	 *            published to.
	 * @param containerURI
	 *            URI of the container.
	 * @return URL of the (to be published) remote container.
	 */
	public static String buildRemoteURL(String baseURL, String containerURI) {
		String base = baseURL.trim();
		if (!base.endsWith("/")) {
			base += "/";
		}
		String part = containerURI.replaceAll(":", "_").replaceAll("//", "/").replaceAll("#|\\?|&|=", "-");
		return base + part;
	}

	/**
	 * Checks whether the presentation container of the current session is required for the current map.
	 * 
	 * @return True if the container is required.
	 */
	private boolean presentationContainerIsRequired() {
		String presentationURI = session.getContainerURIForLayouts();
		return presentationURI.equals(contextMap.getLoadContainer());
	}
	
	/**
	 * Checks whether the information container of the current session is required
	 * for the current maps concepts in any way.
	 * 
	 * @return True if the container is required.
	 */
	private boolean informationContainerIsDependantUpon() {
		String informationURI = session.getContainerURIForConcepts();
		Iterator concepts = controller.getView().getMapScrollPane().getDisplayer().getStoreManager().getConcepts().iterator();
		while (concepts.hasNext()) {
			Concept c = (Concept) concepts.next();
			for (Iterator relCont = c.getComponentManager().getLoadedRelevantContainers().iterator(); relCont.hasNext();) {
				if (informationURI.equals(((URI) relCont.next()).toString())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Finds all containers needed for included concepts. 
	 * With an included concept we mean a concept that is added to the 
	 * map in this session that originates in another session. 
	 * I.e. it's loadcontainer is not the container for concepts 
	 * as indicated by the current session.
	 * 
	 * @param strict if true, only include loadContainers of included concepts, otherwise include relevantcontainers.
	 * @return a set of URI:s (set of Strings) for containers.
	 */
	private Set<URI> findContainersForIncludedConcepts(boolean strict) {
		String presentationURI = session.getContainerURIForLayouts();
		HashSet<URI> set = new HashSet<URI>();
		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		DrawerLayout [] dls = contextMap.getDrawerLayouts();
		for (int i = 0; i < dls.length; i++) {
			if (dls[i].getLoadContainer().equals(presentationURI)) {
				try {
					Concept concept = store.getAndReferenceConcept(URI.create(dls[i].getURI()));
					if (strict) {
						set.add(URI.create(concept.getLoadContainer()));
					} else {
						set.addAll(concept.getComponentManager().getLoadedRelevantContainers());
					}
				} catch (ComponentException e) {
				}
			}
		}
		
		set.remove(session.getContainerURIForConcepts());

		return set;
	}
	
	/* 
	 * Signaling
	 */

	/**
	 * Fires a PropertyChangeEvent with some information.
	 * 
	 * @param info
	 *            Information.
	 */
	private void printInformation(String info) {
		String oldValue = this.information;
		this.information = info;
		firePropertyChange(PROP_PROGRESS_INFO, oldValue, this.information);
	}

	/**
	 * Fires a PropertyChangeEvent with an error message.
	 * 
	 * @param error
	 *            Error message.
	 */
	private void printError(String error) {
		String oldValue = this.information;
		this.information = error;
		firePropertyChange(PROP_PROGRESS_ERROR, oldValue, this.information);
	}

	/**
	 * Fires a PropertyChangeEvent to signal that the progress percentage has
	 * changed.
	 * 
	 * @param percentage
	 *            Percentage, int between 0-100.
	 */
	private void setPercentage(int percentage) {
		if ((0 > percentage) || (percentage > 100)) {
			throw new IllegalArgumentException("Percentage cannot be less than 0 or more than 100.");
		}
		int oldValue = this.percentage;
		this.percentage = percentage;
		firePropertyChange(PROP_PROGRESS_PERCENTAGE, oldValue, percentage);
	}

	/**
	 * Fires a PropertyChangeEvent to signal that the process has been finished.
	 */
	private void finish() {
		firePropertyChange(PROP_PROGRESS_FINISHED, "inprogress", "finished");
	}

	/**
	 * Fires a PropertyChangeEvent to signal that the process has been
	 * cancelled.
	 */
	private void cancel() {
		firePropertyChange(PROP_PROGRESS_CANCELLED, "inprogress", "cancelled");
	}

}