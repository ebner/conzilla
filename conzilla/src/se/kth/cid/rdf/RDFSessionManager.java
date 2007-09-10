/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.edit.EditMapManagerFactory;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.session.SessionFactory;
import se.kth.cid.conzilla.session.SessionImpl;
import se.kth.cid.conzilla.session.SessionManagerImpl;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author matthias
 */
public class RDFSessionManager extends SessionManagerImpl implements SessionFactory {

	ContainerManager containerManager;

	ResourceStore store;
	
	boolean sessionWasRemoved = false;
	
	private long flushingInterval = 10000; // 10 seconds
	
	private Object mutex = new Object();
	
	private class SessionSaver extends TimerTask implements Runnable {

		public void run() {
			boolean doSave = false;
			Collection sessions = RDFSessionManager.this.getSessions();
			if (sessionWasRemoved) {
				doSave = true;
				sessionWasRemoved = false;
			}
			for (Iterator it = sessions.iterator(); it.hasNext(); ) {
				Session session = (Session) it.next();
				if (session.isModified()) {
					session.setModified(false);
					doSave = true;
				}
			}
			if (doSave) {
				synchronized (mutex) {
					RDFSessionManager.this.saveSessions(EditMapManagerFactory.PROJECTS_URI);
				}
			}
		}

	}

	/**
	 * Initializes including setting a projectFactory. Constructor for
	 * RDFProjectManager.
	 */
	public RDFSessionManager(RDFContainerManager containerManager, ResourceStore store) {
		super(store);
		this.containerManager = containerManager;
		this.store = store;
		setSessionFactory(this);
		
		new Timer().schedule(new SessionSaver(), flushingInterval, flushingInterval);
		Runtime.getRuntime().addShutdownHook(new Thread(new SessionSaver()));
	}

	public boolean setCurrentSession(Session session) {
		Session oldSession = getCurrentSession();
		if (!super.setCurrentSession(session)) {
			return false;
		}
		// ContainerURIConcepts
		Container containerC = containerManager.getContainer(session.getContainerURIForConcepts());
		Container containerL = containerManager.getContainer(session.getContainerURIForLayouts());

		if (containerC == null || containerL == null) {
			if (oldSession != null) {
				this.setCurrentSession(oldSession);
			}

			return false;
		}

		containerManager.setCurrentConceptContainer(containerC);
		containerManager.setCurrentLayoutContainer(containerL); // For layout!!!

		containerManager.setBaseURIForConcepts(session.getBaseURIForConcepts());
		containerManager.setBaseURIForLayout(session.getBaseURIForLayouts());
		return true;
	}

	/**
	 * @see se.kth.cid.conzilla.session.SessionManager#saveSessions(String)
	 */
	public void saveSessions(String containerUri) {
		RDFModel model = resolveURI(containerUri);
		if (model != null) {
			saveSession(model);
			try {
				model.setEdited(true);
				store.getComponentManager().saveResource(model);
			} catch (ComponentException e) {
				e.printStackTrace();
				Tracer.bug("Failed to save project container on uri :" + containerUri);
			}
		} else {
			Tracer.bug("Failed to save project container since model could not be located for uri:\n" + containerUri);
		}
	}

	public static Property title = null;
	static {
		try {
			title = new PropertyImpl("http://purl.org/dc/elements/1.1/title");
		} catch (RDFException re) {
		}
	}

	/**
	 * Empties the given model and saves all project therein. hence beware, all
	 * information in the given model will be overwritten.
	 * 
	 * @param model
	 *            the model to store the projects in.
	 */
	public void saveSession(RDFModel model) {
		model.clear();

		Iterator it = getSessions().iterator();
		while (it.hasNext())
			try {
				Session pro = (Session) it.next();
				Resource re = model.createResource(pro.getURI());
				re.addProperty(RDF.type, CV.Project);
				Resource inter;

				// Title for project
				re.addProperty(title, pro.getTitle());

				// BaseURI for Concepts.
				inter = model.createResource();
				re.addProperty(CV.useBaseURI, inter);
				inter.addProperty(RDF.type, CV.ConceptBaseURI);
				inter.addProperty(RDF.value, model.createLiteral(pro.getBaseURIForConcepts()));

				// BaseURI for Layout.
				inter = model.createResource();
				re.addProperty(CV.useBaseURI, inter);
				inter.addProperty(RDF.type, CV.LayoutBaseURI);
				inter.addProperty(RDF.value, model.createLiteral(pro.getBaseURIForLayouts()));

				// Container for Concepts.
				inter = model.createResource();
				re.addProperty(CV.useContainer, inter);
				inter.addProperty(RDF.type, CV.ConceptContainer);
				inter.addProperty(RDF.value, model.createResource(pro.getContainerURIForConcepts()));

				// Container for Layouts.
				inter = model.createResource();
				re.addProperty(CV.useContainer, inter);
				inter.addProperty(RDF.type, CV.LayoutContainer);
				inter.addProperty(RDF.value, model.createResource(pro.getContainerURIForLayouts()));

				for (Iterator managed = pro.getManaged().iterator(); managed.hasNext();) {
					String element = (String) managed.next();
					re.addProperty(CV.managed, model.createResource(element));
				}

			} catch (RDFException e) {
			}
	}

	/**
	 * @see se.kth.cid.conzilla.session.SessionManager#loadSessions(String)
	 */
	public void loadSessions(String containerUri) {
		RDFModel model = resolveURI(containerUri);
		if (model != null) {
			loadSession(model);
			model.setPurpose(SESSIONS);
		} else {
			Tracer.bug("Failed to load project container from uri :" + containerUri);
		}
	}

	protected RDFModel resolveURI(String containerUri) {
		try {
			Container container = store.getAndReferenceContainer(new URI(containerUri));
			if (container instanceof RDFModel) {
				return (RDFModel) container;
			}
		} catch (ComponentException e) {
		} catch (URISyntaxException e) {
		}
		return null;
	}

	protected void loadSession(RDFModel model) {
		
		try {
			ResIterator rei = model.listSubjectsWithProperty(RDF.type, CV.Project);
			while (rei.hasNext()) {
				Resource re = rei.nextResource();
				Session pro = createAndAddSession(re.getURI());

				// Fetch title for project:
				pro.setTitle(re.getProperty(title).getString());

				// BaseURIs for concepts and layouts.
				NodeIterator baseURIs = model.listObjectsOfProperty(re, CV.useBaseURI);
				while (baseURIs.hasNext()) {
					try {
						RDFNode node = baseURIs.nextNode();
						if (!(node instanceof Resource))
							continue;
						Resource anon = (Resource) node;
						if (anon.hasProperty(RDF.type, CV.ConceptBaseURI))
							pro.setBaseURIForConcepts(anon.getProperty(RDF.value).getLiteral().toString());
						if (anon.hasProperty(RDF.type, CV.LayoutBaseURI))
							pro.setBaseURIForLayouts(anon.getProperty(RDF.value).getLiteral().toString());
					} catch (RDFException rex) {
						rex.printStackTrace();
					}
				}

				// Containers for layouts and concepts
				NodeIterator containerURIs = model.listObjectsOfProperty(re, CV.useContainer);
				while (containerURIs.hasNext()) {
					try {
						RDFNode node = containerURIs.nextNode();
						if (!(node instanceof Resource))
							continue;
						Resource anon = (Resource) node;
						if (anon.hasProperty(RDF.type, CV.ConceptContainer))
							pro.setContainerURIForConcepts(anon.getProperty(RDF.value).getResource().toString());
						if (anon.hasProperty(RDF.type, CV.LayoutContainer))
							pro.setContainerURIForLayouts(anon.getProperty(RDF.value).getResource().toString());
					} catch (RDFException rex) {
						rex.printStackTrace();
					}
				}

				// Fetches managed maps (?) in this project.
				NodeIterator managedList = model.listObjectsOfProperty(re, CV.managed);
				while (managedList.hasNext()) {
					Resource managed = (Resource) managedList.next();
					pro.addManaged(managed.getURI());
				}
			}
		} catch (RDFException re) {
		}
	}

	/**
	 * @see SessionFactory#createSession(String)
	 */
	public Session createSession(String uri) {
		if (uri == null) {
			return new SessionImpl(containerManager.createUniqueURI("http://www.conzilla.org/local/projects"));
		} else {
			return new SessionImpl(uri);
		}
	}

	public void removeSession(Session project) {
		super.removeSession(project);
		sessionWasRemoved = true;
	}
}
