/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentFactory;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.cache.ComponentCache;
import se.kth.cid.concept.Concept;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.rdf.layout.RDFConceptLayout;
import se.kth.cid.rdf.layout.RDFConceptMap;
import se.kth.cid.rdf.layout.RDFLiteralStatementLayout;
import se.kth.cid.rdf.layout.RDFResourceLayout;
import se.kth.cid.rdf.layout.RDFStatementLayout;
import se.kth.cid.tree.TreeTagNodeResource;
import se.kth.cid.tree.generic.MemTreeTagManager;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Matthias Palmer
 * @version $Revision$
 */
public class RDFComponentFactory extends MemTreeTagManager implements ComponentFactory {
	
	Log log = LogFactory.getLog(RDFComponentFactory.class);

//	Container currentConceptContainer;

//	Container currentLayoutContainer;

	boolean createTriple = true;

	String reificationType = RDF.Statement.getURI();

	boolean treeTagNodeLoadModelDomination = false;

	boolean includeRequestsAutomaticallyManged = true;

	private ComponentCache cache;

	private RDFContainerManager containerManager;

	public RDFComponentFactory(ComponentCache cache, RDFContainerManager cm) {
		super(null);
		this.cache = cache;
		this.containerManager = cm;
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

	public boolean getIncludeRequestsAutomaticallyManaged() {
		return this.includeRequestsAutomaticallyManged;
	}

	public void setIncludeRequestsAutmaticallyManaged(boolean bo) {
		this.includeRequestsAutomaticallyManged = bo;
	}

	/**
	 * @see se.kth.cid.component.ContainerManager#loadTree(se.kth.cid.identity.URI)
	 */
	public TreeTagNodeResource loadTree(URI uri) throws ComponentException {
		RDFItem item = new RDFItem(uri);
		item.update(new RDFComponentManager(this, uri, null, false));
		return item;
	}

	public RDFComponent findComponent(String suri) {
		try {
			URI uri = new URI(suri);
			return this.findComponent(uri);
		} catch (URISyntaxException mue) {
			return null;
		}
	}

	private RDFComponent findComponent(URI uri) {
		se.kth.cid.component.Resource comp = this.cache.getComponent(uri.toString());

		if (!(comp instanceof RDFComponent))
			return null;

		return (RDFComponent) comp;
	}

	public RDFConcept findConcept(String suri) {
		RDFComponent comp = findComponent(suri);

		if (comp != null)
			if (comp instanceof RDFConcept)
				return (RDFConcept) comp;
			else
				return null;

		try {
			return this.createRDFConcept(URI.create(suri));
		} catch (URISyntaxException mue) {
			return null;
		}
	}

	protected RDFConcept createRDFConcept(URI uri) throws URISyntaxException {
		if (uri == null) {
			uri = URI.create(containerManager.createUniqueURI(containerManager.getBaseURIForConcepts()));
		}
		RDFConcept c = new RDFConcept(uri);
		c.initialize(new RDFComponentManager(this, uri, null, false), (RDFModel) containerManager.currentConceptContainer);
		return c;
	}

	
	public Concept loadConcept(URI uri) throws ComponentException {
		Statement st = this.containerManager.find1(new ResourceImpl(uri.toString()), RDF.type, CV.ContextMap);
		if (st == null) {
			RDFConcept c = new RDFConcept(uri);
			c.update(new RDFComponentManager(this, uri, null, false));
			return c;
		}
		throw new ComponentException("Tried to load a concept with an identifier which is already in use by a context-map. The URI was: "+uri);
	}
	
	public ContextMap loadContextMap(URI uri, boolean collaborative) throws ComponentException {		
		Statement st = this.containerManager.find1(new ResourceImpl(uri.toString()), RDF.type, CV.ContextMap);
		if (st != null) {
			RDFConceptMap cMap = new RDFConceptMap(uri);
			cMap.update(new RDFComponentManager(this, uri, null, collaborative));
			return cMap;
		}
		throw new ComponentException("Could not find the context-map in any of the loaded containers.");			
	}
	// Functions from former RDFFormatHandler.

	/**
	 * Returns a Resource (i.e. a RDFComponent, a ReifiedRDFResource or a
	 * RDFConceptMap) wrapping a RDF-resource. The RDF-resource doesn't need to
	 * be mentioned in any tripples for it to be fetched.
	 * 
	 * <P>
	 * When a component is asked for for the first time it is created on the
	 * basis of existing type information, type information added later won't
	 * affect (in runtime) wich wrapping Resource used. This means that an
	 * RDFComponent won't automatically be upgraded to a.layout if such type
	 * information is created later. However this might be circumvented via the
	 * reload functionality.
	 * </P>
	 * Type 'Statement', a ReifiedRDFResource is created.<br>
	 * Type 'ConceptMap', a RDFConceptMap is created.<br>
	 * Otherwise a RDFComponent is created.
	 * 
	 * @param uri
	 *            the uri of the component to get.
	 * @return a component, never null but may be empty (i.e. no tripple in any
	 *         model currently loaded mentions it).
	 */
	public Component loadComponent(URI uri) throws ComponentException {
		Statement st = this.containerManager.find1(new ResourceImpl(uri.toString()), RDF.type, CV.ContextMap);
		
		if (st != null) {
			RDFConceptMap cMap = new RDFConceptMap(uri);
			cMap.update(new RDFComponentManager(this, uri, null, true));
			return cMap;
		} else {
			RDFConcept c = new RDFConcept(uri);
			c.update(new RDFComponentManager(this, uri, null, false));
			return c;
		}
	}

	public Concept createConcept(URI uri) throws ComponentException {
		RDFConcept rc = null;
		try {
			rc = createRDFConcept(uri);
		} catch (URISyntaxException e) {
			throw new ComponentException(e);
		}

		this.cache.referenceComponent(rc);
		return rc;
	}

	public Component createComponent(URI uri) throws ComponentException {
		RDFComponent comp = new RDFComponent(uri);
		comp.initialize(new RDFComponentManager(this, uri, null, false), (RDFModel) containerManager.currentConceptContainer);
		this.cache.referenceComponent(comp);
		return comp;
	}

	public ContextMap createContextMap(URI uri) throws ComponentException {
		RDFConceptMap cMap = new RDFConceptMap(uri);
		RDFComponentManager rcm = new RDFComponentManager(this, uri, null, false); 
		cMap.initialize(rcm, (RDFModel) containerManager.getCurrentLayoutContainer());
		this.cache.referenceComponent(cMap);
		return cMap;
	}

	/**
	 * Currently supports saving of components to file locally or via ftp. Three
	 * types of components are supported:
	 * <ol>
	 * <li> RDFModels are saved from according to their URI.</li>
	 * <li> For RDFResources that aren't RDFTreeTagNodes all the relevant models
	 * are saved as 1).</li>
	 * <li> For RDFTreeTagNodes the entire tree's relevant models are saved just
	 * like 1)</li>
	 * </ol>
	 * 
	 * @param comp
	 *            need to be either a RDFModel or some subclass of RDFComponent.
	 */
	public void saveResource(se.kth.cid.component.Resource comp) throws ComponentException {
		if (comp instanceof RDFModel) {
			this.containerManager.saveModel((RDFModel) comp, false);
			return;
		}

		if (!(comp instanceof RDFComponent)) {
			throw new ComponentException("Trying to save something that isn't a RDFResource or RDFModel!");
		}

		boolean failedAtLeastOnce = false;
		RDFComponent re = (RDFComponent) comp;
		Iterator it;

		if (re instanceof RDFTreeTagNode) {
			HashSet hs = new HashSet();
			RDFTreeTagNode.getAllRelevantModelsRecursively(hs, (RDFTreeTagNode) re);
			log.debug("relevantmodels recursively nr = " + hs.size());
			it = hs.iterator();
		} else {
			re.getComponentManager().refresh();
			Set s = re.getComponentManager().getLoadedRelevantContainers();
			log.debug("relevantmodels nr = " + s.size());
			it = s.iterator();
		}

		log.debug("RDFFormatHAndler saveComponent....");

		while (it.hasNext()) {
			if (!this.containerManager.saveModel((RDFModel)
					this.containerManager.getContainer(
							((URI) it.next()).toString()),
							false))
				failedAtLeastOnce = true;
		}

		if (!failedAtLeastOnce) {
			this.setComponentUnEdited(re);
		}
	}

	private void setComponentUnEdited(RDFComponent re) {
		if (re instanceof RDFTreeTagNode) {
			RDFTreeTagNode.setUnEditedRecursively((RDFTreeTagNode) re);
		} else {
			re.setEdited(false);
		}
	}

	/**
	 * TODO This function can be optimized a lot, e.g. making the findLayoutType in a smarter way.
	 * 
	 * @param owner
	 * @param uri
	 * @return
	 */
	public ResourceLayout loadResourceLayout(ResourceLayout owner, URI uri) {
		Resource subject = new ResourceImpl(uri.toString());
		Statement st = containerManager.findLayoutType(subject);

		// To avoid fuckups (the type is missing for a RDFResourceLayout... bad
		// code or handedited RDF).
		if (st == null)
			return null;

		RDFResourceLayout os = null;

		try {
			Resource object = st.getResource();

			RDFConceptMap cMap = null;
			if (owner.getConceptMap() != null) {
				cMap = (RDFConceptMap) owner.getConceptMap();
			} else if (owner instanceof RDFConceptMap) {
				cMap = (RDFConceptMap) owner;
			} else {
				log.error("Oups, tried to load a RDFResourceLayout to be a child to a RDFResourceLayout "
						+ "that haven't got a RDFConceptMap and isn't a RDFConceptMap either");
			}

			os = loadLayoutAccordingToType(uri, object, cMap);
			if (os == null) {
				Model m = containerManager.find1Model(subject, RDF.type, null);
				StmtIterator sti = m.listStatements(subject, RDF.type, (RDFNode) null);
				while (sti.hasNext() && os == null) {
					RDFNode node = sti.nextStatement().getObject();
					if (node instanceof Resource) {
						os = loadLayoutAccordingToType(uri, (Resource) node, cMap);
					}
				}
			}
		} catch (URISyntaxException mue) {
			log.error("Failed loading uri = " + uri, mue);
		}
		return os;
	}

	private RDFResourceLayout loadLayoutAccordingToType(URI uri, Resource object, RDFConceptMap cMap)
			throws URISyntaxException {
		RDFResourceLayout rl = null;
		if (object.equals(CV.ConceptLayout)) {
			rl = new RDFConceptLayout(uri, cMap, null);
		} else if (object.equals(CV.NodeLayout)) {
			rl = new RDFResourceLayout(uri, cMap, CV.NodeLayout);
		} else if (object.equals(CV.StatementLayout)) {
			rl = new RDFStatementLayout(uri, cMap);
		} else if (object.equals(CV.LiteralStatementLayout)) {
			rl = new RDFLiteralStatementLayout(uri, cMap, null, null);
		} else {
			return null;
		}
		
		//rl.update((RDFComponentManager) cMap.getComponentManager());
		rl.update(new RDFComponentManager(this, uri, cMap.getComponentManager().getTagManager(), false));
		return rl;
	}

	
	public ContainerManager getContainerManager() {
		return this.containerManager;
	}

}