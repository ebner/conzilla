/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.cache.ComponentCache;
import se.kth.cid.component.cache.ContainerCache;
import se.kth.cid.component.cache.DiskContainerCache;
import se.kth.cid.component.storage.RemoteStorage;
import se.kth.cid.component.storage.RemoteStorageException;
import se.kth.cid.component.storage.RemoteStorageHelper;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.identity.URIUtil;
import se.kth.cid.rdf.layout.RDFConceptMap;
import se.kth.cid.util.FileOperations;
import se.kth.cid.util.FtpURLWrapper;
import se.kth.cid.util.InputStreamSplitter;
import se.kth.cid.util.InputStreamSplitter.InputStreamConsumer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Matthias Palmer
 * @version $Revision$
 */
public class RDFContainerManager implements ContainerManager {
	
	Log log = LogFactory.getLog(RDFContainerManager.class);

	public static class MyRDFErrorHandler implements RDFErrorHandler {
		public void error(Exception e) {
		}

		public void fatalError(Exception e) {
		}

		public void warning(Exception e) {
		}
	}

	private class ReadContainerAction implements InputStreamConsumer {

		RDFModel model;

		ReadContainerAction(RDFModel rdfM) {
			model = rdfM;
		}

		public void consume(InputStream is) {
			model.read(is, null);
		}
	}

	private class CacheContainerAction implements InputStreamConsumer {

		ContainerCache cache;

		URI containerURI;

		Date lastModificationDate;

		CacheContainerAction(ContainerCache diskCache, URI uri, Date lastMod) {
			cache = diskCache;
			lastModificationDate = lastMod;
			containerURI = uri;
		}

		public void consume(InputStream is) {
			cache.putContainer(containerURI.toString(), lastModificationDate, is);
		}
	}

	public final static MyRDFErrorHandler myRDFErrorHandler = new MyRDFErrorHandler();

	String conceptBaseURI;

	String layoutBaseURI;

	Container currentConceptContainer;

	Container currentLayoutContainer;

	ContainerCache containerCache;

	boolean createTriple = true;

	String reificationType = RDF.Statement.getURI();

	boolean treeTagNodeLoadModelDomination = false;

	boolean includeRequestsAutomaticallyManged = true;

	private Hashtable models;

	private Vector modelsVec;

	private Model union = null;

	private ModelHistoryListenerCentral central;

	// FtpHandler ftpHandler;

	public RDFContainerManager() {
		// this.ftpHandler = new FtpHandler();
		this.models = new Hashtable();
		this.modelsVec = new Vector();
		if (ConzillaKit.getDefaultKit().getConzillaEnvironment().hasLocalDiskAccess()) {
			this.containerCache = new DiskContainerCache();
		}
	}

	public ModelHistoryListenerCentral getModelHistoryListenerCentral() {
		if (this.central == null) {
			this.central = new ModelHistoryListenerCentral();
		}

		return this.central;
	}
	
	/**
	 * Tells wether to stay in the same model as the treenode above when
	 * creating new layouts.
	 */
	public boolean getTreeTagNodeLoadContainerDomination() {
		return this.treeTagNodeLoadModelDomination;
	}

	/**
	 * Change this whenever you wan't to create new layers in new models, e.g.
	 * when you loaded a model (and a map) over http you can add material to it
	 * in another model by setting this value to false. (Instead the
	 * currentmodel is used.)
	 */
	public void setTreeTagNodeLoadContainerDomination(boolean value) {
		this.treeTagNodeLoadModelDomination = value;
	}

	public void setCurrentConceptContainer(Container m) {
	//	Container oldm = this.currentConceptContainer;
		this.currentConceptContainer = m;
//		this.firePropertyChangeEvent(new java.beans.PropertyChangeEvent(this, CURRENT_CONCEPT_CONTAINER_CHANGED, oldm,
	//			this.currentConceptContainer));
	}

	public Container getCurrentLayoutContainer() {
		return this.currentLayoutContainer;
	}

	public void setCurrentLayoutContainer(Container m) {
		// RDFModel oldm = currentLayoutContainer;
		this.currentLayoutContainer = m;
		// firePropertyChangeEvent( new java.beans.PropertyChangeEvent(
		// this, CURRENT_MODEL_CHANGED, oldm, currentConceptContainer));
	}

	public Container getCurrentConceptContainer() {
		return this.currentConceptContainer;
	}

	public void setBaseURIForConcepts(String base) {
		this.conceptBaseURI = base;
	}

	public String getBaseURIForConcepts() {
		return this.conceptBaseURI;
	}

	public void setBaseURIForLayout(String base) {
		this.layoutBaseURI = base;
	}

	public String getBaseURIForLayout() {
		return this.layoutBaseURI;
	}

	public URI createConceptURI() {
		String base = this.getBaseURIForConcepts() != null ? this.getBaseURIForConcepts() : this
				.getCurrentConceptContainer().getURI();
		String nuri = this.createUniqueURI(base);
		return URI.create(nuri);
	}

	public URI createLayoutURI() {
		String base = this.getBaseURIForLayout() != null ? this.getBaseURIForLayout() : this
				.getCurrentConceptContainer().getURI();
		String nuri = this.createUniqueURI(base);
		return URI.create(nuri);
	}

	public String createUniqueURI(String baseURI) {
		if (baseURI.indexOf('#') != -1) {
			return baseURI + "_" + URIUtil.createUniqueID();
		} else {
			return baseURI + "#" + URIUtil.createUniqueID();
		}
	}

	/**
	 * Answers wether a triple should be created when a Statement is created.
	 */
	public boolean reflectToTriple() {
		return this.createTriple;
	}

	/**
	 * Sets the mode regarding if triples should be created when a Statement is
	 * created.
	 */
	public void setReflectToTriple(boolean trip) {
		this.createTriple = trip;
	}

	public String getRelationType() {
		return this.reificationType;
	}

	public void setRelationType(String rt) {
		this.reificationType = rt;
	}

	public boolean getIncludeRequestsAutomaticallyManaged() {
		return this.includeRequestsAutomaticallyManged;
	}

	public void setIncludeRequestsAutmaticallyManaged(boolean bo) {
		this.includeRequestsAutomaticallyManged = bo;
	}

	public void addContainer(Container container, ComponentCache cache) {
		if (!(container instanceof RDFModel)) {
			return;
		}

		RDFModel m = (RDFModel) container;
		URI curi = URI.create(container.getURI());
		this.union = null;
		this.models.put(m.getURI(), m);
		this.modelsVec.add(m);
		//this.addTag(m);

		ResIterator it = m.listSubjects();
		while (it.hasNext()) {
			String uri = it.nextResource().getURI();
			
			// Only non-anonymous resources updated.
			if (uri != null) {
				// following lines replace "old" findComponent which is in
				// ComponentManager now
				Component comp = cache.getComponent(uri.toString());
				if (comp != null && comp instanceof RDFComponent) {
					if (comp instanceof RDFConceptMap) {
						((RDFConceptMap) comp).refresh();
					} else {
						((RDFComponent) comp).getComponentManager().containerIsRelevant(curi);
					}
				}
			}
		}
		it.close();

		
		NodeIterator ni = m.listObjectsOfProperty(CV.inContextMap);
		while (ni.hasNext()) {
			RDFNode n = ni.nextNode();
			if (n instanceof Resource 
					&& !((Resource) n).isAnon()) {
				String uri = ((Resource) n).getURI();
			Component comp = cache.getComponent(uri);
			if (comp != null && comp instanceof RDFConceptMap) {
				((RDFComponent) comp).getComponentManager().containerIsRelevant(curi);
				((RDFConceptMap) comp).refresh();
				}
			}
		}
		
			
		//Integer newVal = new Integer(modelsVec.indexOf(m));
		//firePropertyChangeEvent(new java.beans.PropertyChangeEvent(m, CONTAINER_ADDED, null, newVal));
	}

	public void removeContainer(Container container, ComponentCache cache) {
		if (!(container instanceof RDFModel)) {
			return;
		}
		RDFModel m = (RDFModel) container;

		union = null;
		models.remove(m);
		modelsVec.remove(m);

		ResIterator it = m.listSubjects();
		while (it.hasNext()) {
			// following lines replace "old" findComponent which is in
			// ComponentManager now
			String uri = it.nextResource().getURI().toString();
			se.kth.cid.component.Resource comp;
			comp = cache.getComponent(uri);

			if (comp instanceof RDFComponent) {
				RDFComponent rc = (RDFComponent) cache.getComponent(uri);
				if (rc != null) {
					//TODO check if this is efficient
					rc.getComponentManager().refreshRelevanceForContainer(URI.create(uri));
					//rc.removeModel(m);
				}
			}
		}
		it.close();

		//firePropertyChangeEvent(new java.beans.PropertyChangeEvent(m, CONTAINER_REMOVED, null, null));
	}

	public void setIndexOfContainer(Container container, int index) {
	//	Integer oldVal = new Integer(modelsVec.indexOf(container));
		modelsVec.remove(container);
		modelsVec.insertElementAt(container, index);
	//	Integer newVal = new Integer(modelsVec.indexOf(container));
		//firePropertyChangeEvent(new java.beans.PropertyChangeEvent(container, CONTAINER_ORDER_CHANGED, oldVal, newVal));
	}

	public Container getContainer(String uri) {
		if (uri == null) {
			return null;
		}
		return (Container) models.get(uri);
	}

	public List getContainers() {
		return modelsVec;
	}

	// FIXME, not updated when models are edited...
	public Model getTotalRDFModel() {
		if (union != null) {
			return union;
		}

		union = ModelFactory.createDefaultModel();
		Iterator it = models.values().iterator();

		while (it.hasNext()) {
			union.add((Model) it.next());
		}

		return union;
	}

	public void modelEdited(RDFModel m) {
		union = null;
	}

	public RDFModel find1Model(Resource subject, Property predicate, RDFNode object) {
		Iterator it = models.values().iterator();

		while (it.hasNext()) {
			RDFModel m = (RDFModel) it.next();
			if (m.listStatements(new SelectorImpl(subject, predicate, object)).hasNext()) {
				return m;
			}
		}

		return null;
	}
	
	public int isComponentReferredTo(Component comp) {
        return findAllStatements(null, CV.displayResource, new ResourceImpl(comp.getURI())).size();
	}

	public Set findAllStatements(Resource subject, Property predicate, RDFNode object) {
		HashSet stmts = new HashSet();
		Iterator it = models.values().iterator();

		while (it.hasNext()) {
			RDFModel m = (RDFModel) it.next();
			StmtIterator si = m.listStatements(new SelectorImpl(subject, predicate, object));
			while (si.hasNext()) {
				stmts.add(si.next());
			}
		}

		return stmts;
	}

	public Statement find1(Resource subject, Property predicate, RDFNode object) {
		Iterator it = models.values().iterator();

		while (it.hasNext()) {
			Model m = (Model) it.next();
			StmtIterator sti = m.listStatements(new SelectorImpl(subject, predicate, object));
			if (sti.hasNext()) {
				Statement st = sti.nextStatement();
				sti.close();
				return st;
			}
		}

		return null;
	}

	public Statement findLayoutType(Resource subject) {
		Iterator it = getContainers(Container.COMMON).iterator();
		Statement found = null;
		while (it.hasNext()) {
			Model m = (Model) it.next();
			StmtIterator sti = m.listStatements(new SelectorImpl(subject, RDF.type, (RDFNode) null));
			while (sti.hasNext()) {
				Statement st = sti.nextStatement();
				RDFNode o = st.getObject();
				if (o instanceof Resource) {
					if (o.equals(CV.NodeLayout)) {
						found = st;
						break;
					}
					if (o.equals(CV.ConceptLayout)) {
						found = st;
						break;
					}
					// If a StatementLayout is found, look on for a
					// potential LiteralStatementLayout...
					// Ugly and not very general.
					if (o.equals(CV.StatementLayout)) {
						found = st;
					}
					if (o.equals(CV.LiteralStatementLayout)) {
						found = st;
						break;
					}
				}
			}
			sti.close();
			if (found != null) {
				break;
			}
		}
		return found;
	}

	/**
	 * @see se.kth.cid.component.ContainerManager#getContainers(java.lang.String)
	 */
	public List getContainers(String purpose) {
		List purposeContainers = new LinkedList();
		for (Iterator allcont = getContainers().iterator(); allcont.hasNext();) {
			Container container = (Container) allcont.next();
			if (container.getPurpose().equals(purpose)) {
				purposeContainers.add(container);
			}
		}
		return purposeContainers;
	}

	/**
	 * @see se.kth.cid.component.ContainerManager#isURIUsed(java.lang.String)
	 */
	public boolean isURIUsed(String uri) {
		for (Iterator models = modelsVec.iterator(); models.hasNext();) {
			RDFModel model = (RDFModel) models.next();
			if (model.isURIUsed(uri)) {
				return true;
			}
		}
		return false;
	}

	// Functions from former RDFFormatHandler.

	/**
	 * @see se.kth.cid.component.ContainerManager#isSavable(se.kth.cid.identity.URI)
	 */
	public boolean isSavable(URI uri) {
		return false; // ever used?
	}

//	public void publish(Container container) throws ComponentException {
//		if (!saveModel((RDFModel) container, true)) {
//			throw new ComponentException("Failed publishing container at position " + container.getPublishURL());
//		}
//	}

	protected boolean saveModel(RDFModel m, boolean publish) throws ComponentException {
		String action = publish ? "publish" : "save"; // FIXME: hm. no
		// publishing code in here
		if (!publish && (!m.isEdited() || !m.isEditable())) {
			log.debug("Trying to save model " + m.getURI() + " but it is not edited so it "
					+ "does not have to be published!");
			return true;
		}
		log.debug("Trying to " + action + " model " + m.getURI());

		URI uri = publish ? URI.create(m.getPublishURL()) : m.getLoadURI();
		OutputStream os = null;
		try {
			URI tmpFileURI;
			if (uri.getScheme().equals("file")) {
				tmpFileURI = URI.create(uri + "~");
			} else {
				tmpFileURI = uri;
			}

			// FIXME include offline mode here as well

			os = getOutputStream(tmpFileURI);
			
			if (os == null) {
				return false;
			}

			try {
				m.write(os, "RDF/XML-ABBREV");
				os.close();
				m.setEdited(false);
				log.debug("Succeeded " + action + " in format RDF/XML-ABBREV");
				FileOperations.moveFile(tmpFileURI, uri);
				return true;
			} catch (Exception e) {
				log.warn("Failed " + action + " using RDF/XML-ABBREV, trying N-TRIPLE instead", e);
				try {
					if (os != null) {
						os.close();
					}
					os = getOutputStream(tmpFileURI);
					if (os == null) {
						return false;
					}
					m.write(os, "N-TRIPLE");
					os.close();
					m.setEdited(false);
					log.debug("Succeeded " + action + " in format N-TRIPLE");
					FileOperations.moveFile(tmpFileURI, uri);
					return true;
				} catch (IOException e1) {
					log.error("Failed " + action + " using N-TRIPLE as well. File only half " + action + ", file is probably corrupt!");
				}
			}

		} catch (IOException io) {
			log.error("Failed saving model", io);
		}
		return false;
	}

	// FIXME: this method is just used from within saveModel(), which should
	// just save locally.
	// why is there FTP code? should we rename it to getLocalOutputStream to
	// avoid confusions?
	// the remote locations code (FTP + WebDAV) is replaced by RemoteStorage
	// implementations anyway.
	private OutputStream getOutputStream(URI uri) throws IOException {
		log.debug("Trying to fetch OutputStream for " + uri.toString());
		// If uri resolves to a file: or home:
		File f = getFile(uri);
		if (f != null) {
			return new FileOutputStream(f);
		}

		// If uri resolves to a ftp://
		java.net.URL url = uri.toURL();
		if (url.getProtocol().equalsIgnoreCase("ftp")) {
			FtpURLWrapper furl = new FtpURLWrapper(url);
			return furl.getOutputStream();
			// return ftpHandler.getOutputStream(url);
		}
		return null;
	}

	private File getFile(URI uri) {
		if (uri.getScheme().equals("file")) {
			return new File(uri);
		}
		return null;
	}

	private void readLocalModel(RDFModel model, URI url, boolean createIfMissing) throws ComponentException {
		URI loadURI = null;
		URI origURI = null;

		loadURI = url;
		origURI = URI.create(model.getURI());

		try {
			model.read(url.toString());
		} catch (Exception e2) {
			try {
				model.read(url.toString(), "N-TRIPLE");
			} catch (Exception e3) {
				if (createIfMissing) {
					checkCreateContainer(loadURI);
					setEditable(origURI, loadURI, model);
				} else {
					throw new ComponentException("Error loading URL " + url + " for component " + origURI + ":\n "
							+ e3.getMessage());
				}
			}
		}
	}

	private void readResourceModel(RDFModel model, URI loadURI) {
		String url = loadURI.getSchemeSpecificPart().substring(1);
		URL internalURL = getClass().getClassLoader().getResource(url);
		model.read(internalURL.toString());
	}

	private void fetchAndCacheRemoteModel(RDFModel model, String url, RemoteStorage remote, Date lastMod)
			throws ComponentException {
		try {
			InputStream originalInput = remote.get(url);
			InputStreamSplitter splitter = new InputStreamSplitter(originalInput);

			InputStreamConsumer readAction = new ReadContainerAction(model);
			splitter.addConsumer(readAction);

			// We can't cache anything if we run as applet
			if (containerCache != null) {
				InputStreamConsumer cacheAction = new CacheContainerAction(containerCache, URI.create(model.getURI()), lastMod);
				splitter.addConsumer(cacheAction);
			}

			splitter.start();
			splitter.joinConsumerThreads();
		} catch (RemoteStorageException rse) {
			throw new ComponentException(rse);
		}
	}

	private void readRemoteModel(RDFModel model, java.net.URI uri, boolean createIfMissing) throws ComponentException {
		boolean isOnline = ConzillaKit.getDefaultKit().getConzillaEnvironment().isOnline();
		RemoteStorage remote = null;
		InputStream containerStream = null;
		Date lastMod = null;

		if (isOnline) {
			try {
				// toASCIIString() is necessary for non-ASCII characters to work
				String url = uri.toASCIIString();
				remote = RemoteStorageHelper.getRemoteStorage(URI.create(uri.toASCIIString()));
				remote.connect();

				if (containerCache != null) {
					try {
						lastMod = remote.getLastModificationDate(url);
						containerStream = containerCache.getContainer(model.getURI(), lastMod);
					} catch (RemoteStorageException rse2) {
					}
				}

				if (containerStream != null) {
					try {
						model.read(containerStream, null);
						containerStream.close();
					} catch (IOException e) {
						throw new ComponentException(e);
					}
				} else {
					fetchAndCacheRemoteModel(model, url, remote, lastMod);
				}
			} catch (RemoteStorageException rse) {
				log.error(rse);
				throw new ComponentException(rse);
			} finally {
				if (remote != null) {
					try {
						remote.disconnect();
					} catch (RemoteStorageException e) {
					}
				}
			}
		} else {
			// If we are offline we just have the cache
			if (containerCache != null) {
				containerStream = containerCache.getContainer(model.getURI(), lastMod);
			}
			if (containerStream != null) {
				try {
					model.read(containerStream, null);
					containerStream.close();
				} catch (IOException e) {
					throw new ComponentException(e);
				}
			} else {
				throw new ComponentException("Unable to load uncached container: Conzilla is offline");
			}
		}
	}

	public se.kth.cid.component.Container loadContainer(URI origURI, URI loadURI, boolean createIfMissing,
			ComponentCache cache) throws ComponentException {
		RDFModel model = null;
		URI uri = loadURI;

		// Skipping the normalizing part, hope it is not neccessary.
		// URL url2 = new URL (RDFUtil.normalizeURI(url.toString()));
		// model = new RDFModel(totalModel, mm, uri, origuri, MIMEType.RDF);
		model = new RDFModel(this, origURI, loadURI);
		model.getReader().setErrorHandler(myRDFErrorHandler);

		if (RemoteStorageHelper.isRemoteAndSupported(uri)) {
			this.readRemoteModel(model, uri, createIfMissing);
		} else {
			if (uri.getScheme().equals("res")) {
				this.readResourceModel(model, loadURI);
			} else {
				this.readLocalModel(model, uri, createIfMissing);
			}
		}

		setEditable(origURI, loadURI, model);
		log.debug("Model loaded from: " + loadURI.toString());

		// totalModel.addModel(model);
		addContainer(model, cache);

		// setCurrentConceptContainer(model);

		// Loads the metadata for container model, from the container itself
		// (metametadata)
		// It sends in it's own empty metadatacomponent for update.
		// model.getMetaData(model, model.getMetaData());

		return model;
	}

	private void setEditable(URI origURI, URI resolvedURL, RDFModel model) {
		if (origURI.getScheme().equals("res") || (!RemoteStorageHelper.isRemoteAndSupportsModification(resolvedURL)
				&& !resolvedURL.getScheme().equals("file"))) {
			model.setEditable(false);
		}
	}

	/**
	 * @see se.kth.cid.component.ContainerManager#checkCreateContainer(java.net.URI)
	 */
	public void checkCreateContainer(URI uri) throws ComponentException {
		if (RemoteStorageHelper.isRemoteAndSupported(uri)) {
			RemoteStorage rs = null;
			try {
				rs = RemoteStorageHelper.getRemoteStorage(uri);
				rs.connect();
				if (rs.exists(uri.toString())) {
					throw new ComponentException("File exists already");
				}
				rs.disconnect();
			} catch (RemoteStorageException rse) {
				throw new ComponentException(rse);
			} finally {
				if (rs != null) {
					try {
						rs.disconnect();
					} catch (RemoteStorageException e) {
					}
				}
			}
		} else {
			if (new File(uri.toString()).exists()) {
				throw new ComponentException("File exists already");
			}
		}
	}

	public Container createContainer(URI origURI, URI resolvedURI, ComponentCache cache) throws ComponentException {
		RDFModel m = new RDFModel(this, origURI, resolvedURI);
		setEditable(origURI, resolvedURI, m);
		addContainer(m, cache);
		return m;
	}

	/**
	 * @see ContainerManager#refreshCacheOfContainers()
	 */
	public void refreshCacheOfContainers(ComponentCache cache) {
		Hashtable oldModels = models;
		models = new Hashtable();
		modelsVec = new Vector();

		for (Iterator modelsIt = oldModels.values().iterator(); modelsIt.hasNext();) {
			RDFModel model = (RDFModel) modelsIt.next();
			try {
				Container cont = loadContainer(URI.create(model.getURI()), model.getLoadURI(), false, cache);
				cont.setPurpose(model.getPurpose());
			} catch (ComponentException e) {
				addContainer(model, cache); // Fallback strategy, just keep the
				// old one
			}
		}
		String cccURI = currentConceptContainer != null ? currentConceptContainer.getURI() : null;
		String clcURI = currentLayoutContainer != null ? currentLayoutContainer.getURI() : null;
		if (cccURI != null) {
			currentConceptContainer = getContainer(cccURI);
		}
		if (clcURI != null) {
			currentLayoutContainer = getContainer(clcURI);
		}
	}

	/**
	 * @see se.kth.cid.component.ContainerManager#checkLoadContainer(se.kth.cid.identity.URI)
	 */
	public void checkLoadContainer(URI uri) throws ComponentException {
		if (RemoteStorageHelper.isRemoteAndSupported(uri)) {
			RemoteStorage rs = null;
			try {
				rs = RemoteStorageHelper.getRemoteStorage(uri);
				rs.connect();
				if (!rs.exists(uri.toString())) {
					throw new ComponentException("File does not exist!");
				}
			} catch (RemoteStorageException rse) {
				throw new ComponentException(rse);
			} finally {
				if (rs != null) {
					try {
						rs.disconnect();
					} catch (RemoteStorageException e) {
					}
				}
			}
		} else {
			if (!new File(uri).exists()) {
				throw new ComponentException("File does not exist!");
			}
		}
	}

	public Container loadPublishedContainer(URI uri, ComponentCache cache) throws ComponentException {
		CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		CollaborillaReader collaborillaReader = new CollaborillaReader(cs);
		String requiredContainerURL = collaborillaReader.resolveURI(uri);
		if (requiredContainerURL != null) {
			return loadContainer(uri, URI.create(requiredContainerURL), false, cache);
		} else {
			return null;
		}
	}

	public Container findLoadContainerForResource(se.kth.cid.component.Resource resource) {
        Resource subject = new ResourceImpl(resource.getURI());

		Container container= find1Model(subject, RDF.type, null);
        if (container == null) {
        	container = find1Model(subject, null, null);
        }
        return container;
    }

	public ContainerCache getContainerCache() {
		return containerCache;
	}
}