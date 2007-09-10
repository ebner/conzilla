/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;

import java.net.URI;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.RelationSet;
import se.kth.cid.component.Resource;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.tree.TreeTagNode;

/**
 * FIXME old stuff don't work anymore. things are just commented out. This is a
 * class for creating a node representing a filterconcept.
 * 
 * @author Daniel Pettersson
 * @version $Revision$
 */
public class FilterNode {
	String filtertag[];

	Vector refines;

	Vector lifo;

	FilterNode topnode;

	// FilterAction action;

	URI filterURI;

	TreeTagNode filterConcept;

	Vector triples;

	Filter filter;

	Hashtable contents;

	Hashtable bottomUp;

	ResourceStore store;

	final String[] ttriple = { "Filter" };

	/**
	 * Constructs a FilterNode.
	 * 
	 * @param filterURI
	 *            the Concept associated with this FilterNode.
	 * @param store
	 *            the ResourceStore to load from.
	 * @param lifo
	 *            the loop vector associated with this FilterNode.
	 * @param filter
	 * @exception FilterException
	 *                if the filter is looped.
	 */
	public FilterNode(URI filterURI, ResourceStore store, Vector lifo, Filter filter) throws FilterException {
		this.filter = filter;
		contents = new Hashtable();
		bottomUp = new Hashtable();
		this.store = store;
		try {
			filterConcept = store.getComponentManager().loadTree(filterURI);
			// URI typeURI=URIClassifier.parseURI(filterConcept.getType(),
			// filterURI);

			// FIXME ConceptType no longer exists.
			// ConceptType type = store.getAndReferenceConceptType(typeURI);
			// if
			// (!MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
			// "ConceptType", "Conzilla", ttriple))
			// throw new FilterException("Wrong type in concept, expected
			// 'Filter'.");

			// Check the concepttype in metadata, if classification says filter.
			lifo.addElement(filterURI);
			// action = new FilterAction(this, controller);
		} catch (ComponentException ce) {
			throw new FilterException("couldn't load filternode: " + ce.getMessage());
		}

		refines = new Vector();
		triples = new Vector();

		// FIXME need to make use of
		Vector nodes = filterConcept.getChildren();

		for (Iterator nodesIt = nodes.iterator(); nodesIt.hasNext();) {
//			TreeTagNode child = (TreeTagNode) nodesIt.next();
//			URI newURI = null;

			// FIXME: do something with 'refine' and 'subfilter' properties.
			// Do not forget loop detection...
		}

		for (int i = 0; i < refines.size(); i++)
			((FilterNode) refines.elementAt(i)).setTop(this);

		lifo.removeElement(filterURI);
	}

	/**
	 * The filternode is created from a concept.
	 * 
	 * @return a filterconcept.
	 */
	public TreeTagNode getFilterNode() {
		return filterConcept;
	}

	/**
	 * Returns the filtertag of this node.
	 * 
	 * @return the filtertag of this node.
	 */
	public String getFilterTag() {
		/*
		 * MetaData md=filterConcept.getMetaData(); String title =
		 * MetaDataUtils.getLocalizedString(md.get_metametadata_language(),
		 * md.get_general_title()).string; if(title.length() == 0) title =
		 * filterConcept.getURI(); return title;
		 */
		// FIXME filter tags has to be represented in another way.
		return null;
	}

	public String getToolTipText() {/*
									 * MetaData.LangStringType[] desc =
									 * filterConcept.getMetaData().get_general_description();
									 * if (desc!=null) return
									 * MetaDataUtils.getLocalizedString(filterConcept.getMetaData().get_metametadata_language(),
									 * desc[0]).string; else
									 */
		// FIXME just commented old stuff out.
		return null;
	}

	/**
	 * Returns the refine at given index of this node.
	 * 
	 * @return the refine at given index of this node.
	 */
	public FilterNode getRefine(int index) {
		return (FilterNode) refines.elementAt(index);
	}

	/**
	 * Returns the action associated with this node.
	 * 
	 * @return the action associated with this node.
	 * 
	 * public FilterAction getAction() { return action; }
	 */

	/**
	 * Returns the number of refines attached to this node.
	 * 
	 * @return the number of refines attached to this node.
	 */
	public int numOfRefines() {
		return refines.size();
	}

	/**
	 * Returns the node above this node.
	 * 
	 * @return the node above this node.
	 */
	public FilterNode getTop() {
		return topnode;
	}

	/**
	 * Sets the node above this node.
	 * 
	 * @param node
	 *            the node above this node.
	 */
	public void setTop(FilterNode node) {
		topnode = node;
	}

	public void filterThrough(Resource comp) {
		for (int i = 0; i < numOfRefines(); i++)
			getRefine(i).filterThrough(comp);
		getContent(comp);
	}

	public List getContent(Resource comp) {
		Vector contentFiltered = (Vector) contents.get(comp);
		if (contentFiltered != null)
			return contentFiltered;
		else if (getTop() != null)
			return filterContent(comp, getTop().getContent(comp));
		else
			return initContentFromComponent(comp);
	}

	public List initContentFromComponent(Resource comp) {

		Resource[] contentsArr = (new RelationSet(comp, "content", store)).getRelations();

		Vector conts = new Vector();
		for (int i = 0; i < contentsArr.length; i++)
			if (filter.componentPasses(contentsArr[i], this))
				conts.addElement(contentsArr[i]);
		contents.put(comp, conts);
		return conts;
	}

	protected List filterContent(Resource comp, List list) {
		List conts = new Vector();
		Iterator it = list.iterator();
		for (; it.hasNext();) {
			Resource cont = (Resource) it.next();
			if (filter.componentPasses(cont, this))
				conts.add(cont);
		}
		contents.put(comp, conts);
		if (getTop() != null && numOfRefines() == 0)
			getTop().contentPassedRefines(comp, conts);
		return conts;
	}

	public void contentPassedRefines(Resource comp, List conts) {
		Set passed = (Set) bottomUp.get(comp);
		if (passed == null) {
			passed = new HashSet(conts);
			bottomUp.put(comp, passed);
		} else
			passed.addAll(conts);

		if (getTop() != null)
			getTop().contentPassedRefines(comp, conts);
	}

	public Set getContentPassedRefines(Resource comp) {
		return (Set) bottomUp.get(comp);
	}
}
