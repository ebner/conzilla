/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;

import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import se.kth.cid.layout.BookkeepingConceptMap;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerEvent;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.LayerListener;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.tree.generic.MemTreeTagManager;
import se.kth.cid.util.TagManager;
import se.kth.cid.util.Tracer;

/**
 * @author Matthias Palmer
 * @version $Revision$
 */
public class MemLayerManager implements LayerManager {
	LayerLayout layers;

	LayerLayout current;

	Vector listeners;

	TagManager treeTagManager;

	public MemLayerManager(LayerLayout layers, TagManager ttm) {
		this.layers = layers;
		if (ttm != null)
			treeTagManager = ttm;
		else
			treeTagManager = new MemTreeTagManager(null);
		listeners = new Vector();
	}

	public void addLayerListener(LayerListener list) {
		listeners.add(list);
	}

	public void removeLayerListener(LayerListener list) {
		listeners.remove(list);
	}

	public void fireLayerChange(LayerEvent event) {
		Enumeration en = listeners.elements();
		while (en.hasMoreElements())
			((LayerListener) en.nextElement()).layerChange(event);
	}

	/**
	 * Creates a new layer, watch out, the ConceptMap given have to be a
	 * BookkeepingConceptMap.
	 */
	public LayerLayout createLayer(String id, Object tag, ContextMap cMap) {
		if (cMap instanceof BookkeepingConceptMap) {
			LayerLayout ll = createLayerImpl(id, tag, cMap);
			addLayer(ll);
			createLayerImplFinish(ll);
			return ll;
		} else
			return null;
	}

	protected LayerLayout createLayerImpl(String id, Object tag, ContextMap cMap) {
		return new MemGroupLayout(id, (BookkeepingConceptMap) cMap, tag,
				treeTagManager);
	}

	protected void createLayerImplFinish(LayerLayout ll) {
		// initialize and stuff that need the parent set.
	}

	public void addLayer(LayerLayout layer) {
		layers.add(layer);
		current = layer;
		fireLayerChange(new LayerEventImpl(LayerEvent.OBJECTSTYLE_ADDED));
	}

	public void removeLayer(String name) {
		TreeTagNode layer = layers.getChild(name);
		if (layer != null)
			layers.remove(layer);
		if (current == layer)
			current = null;
		fireLayerChange(new LayerEventImpl(LayerEvent.OBJECTSTYLE_REMOVED));
	}

	public void removeLayer(LayerLayout layer) {
		layers.remove(layer);
		if (current == layer)
			current = null;
		fireLayerChange(new LayerEventImpl(LayerEvent.OBJECTSTYLE_REMOVED));
	}

	public void setEditGroupLayout(String name) {
		if (name == null)
			current = null;
		else
			current = (LayerLayout) layers.getChild(name);
	}

	public LayerLayout getEditGroupLayout() {
		return current;
	}

	public void setLayerVisible(String name, boolean visible) {
		if (layers.getChildHidden(name) != !visible) {
			layers.setChildHidden(name, !visible);
			fireLayerChange(new LayerEventImpl(LayerEvent.VISIBILITY_CHANGED));
		}
	}

	public boolean getLayerVisible(String name) {
		return !layers.getChildHidden(name);
	}

	public Vector getLayers() {
		return layers.getChildren();
	}

	public LayerLayout getLayer(String name) {
		TreeTagNode ttn = layers.getChild(name);
		if (ttn instanceof LayerLayout)
			return (LayerLayout) ttn;
		return null;
	}

	public void lowerLayer(LayerLayout layer) {
		layers.lowerChild(layer);
		fireLayerChange(new LayerEventImpl(LayerEvent.ORDER_CHANGED));
	}

	public void raiseLayer(LayerLayout layer) {
		layers.raiseChild(layer);
		fireLayerChange(new LayerEventImpl(LayerEvent.ORDER_CHANGED));
	}

	public int getOrderOfLayer(LayerLayout layer) {
		return layers.getIndex(layer);
	}

	public void setOrderOfLayer(LayerLayout layer, int position) {
		layers.setIndex(layer, position);
		fireLayerChange(new LayerEventImpl(LayerEvent.ORDER_CHANGED));
	}

	public Vector getDrawerLayouts(int visibility) {
		Vector collect = new Vector();
		layers.getChildren(collect, visibility, DrawerLayout.class);
		return collect;
	}

	public ResourceLayout getResourceLayout(String id) {
		return (ResourceLayout) layers.recursivelyGetChild(id);
	}

	public Set IDSet() {
		return layers.IDSet();
	}

	/**
	 * Adds a resourceLayout given a identifier for it's parent. If/when the
	 * parent is found it is registered in the ResourceLayout itself.
	 * 
	 * @param parent the parent where this conceptlayout should be added, if
	 *            null the current editMapGroupStyl is used.
	 */
	public void addResourceLayout(ResourceLayout nw, String parent) {
		TreeTagNode ttn = null;
		if (parent != null)
			ttn = layers.recursivelyGetChild(parent);
		else if (getEditGroupLayout() != null)
			ttn = getEditGroupLayout();
		else {
			Vector layers = getLayers();
			if (layers.size() > 0) {
				ttn = (TreeTagNode) layers.elementAt(0);
			} else {
				parent = nw.getConceptMap().getURI() + "topLayer";
				LayerLayout ls = createLayer(parent, parent, nw.getConceptMap());
				ls.add(nw);
				fireLayerChange(new LayerEventImpl(LayerEvent.OBJECTSTYLE_ADDED));
				return;
			}
		}

		if (!ttn.getAllowsChildren())
			Tracer
					.bug("Trying to add a ConceptLayout to parent that cannot have children");
		ttn.add(nw);
		fireLayerChange(new LayerEventImpl(LayerEvent.OBJECTSTYLE_ADDED));
	}

	public boolean removeResourceLayout(ResourceLayout os) {
		Tracer.debug("inside removeResourceLayout in MemLayerManager1");
		if (!os.getChildren().isEmpty())
			return false;
		Tracer.debug("inside removeResourceLayout in MemLayerManager1");
		boolean bo = layers.recursivelyRemoveChild(os);
		if (bo)
			fireLayerChange(new LayerEventImpl(LayerEvent.OBJECTSTYLE_REMOVED));

		return bo;
	}
}
