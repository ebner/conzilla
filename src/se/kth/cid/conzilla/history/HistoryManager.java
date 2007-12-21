/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.history;

import java.net.URI;
import java.util.Vector;

import se.kth.cid.component.Component;
import se.kth.cid.component.Resource;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.AttributeEntryUtil;

public class HistoryManager {
	Vector historyListeners;

	ResourceStore store;

	public HistoryManager(ResourceStore store) {
		this.store = store;
		historyListeners = new Vector();
	}

	URI getURI(Resource comp) {
		if (comp == null)
			return null;

		return URI.create(comp.getURI());
	}

	URI getURI(DrawerLayout ns) {
		return URIClassifier.parseValidURI(ns.getConceptURI(), ns.getConceptMap().getURI());
	}

	URI getDetailedMapURI(DrawerLayout ns) {
		return URIClassifier.parseValidURI(ns.getDetailedMap(), ns.getConceptMap().getURI());
	}

	String getTitle(Component comp) {
		if (comp == null)
			return null;

		
		String title = AttributeEntryUtil.getTitleAsString(comp);
		if (title == null) {
			title = comp.getURI();
		}
		return title;
	}

	String getTitle(DrawerLayout ns) {
		return getTitle(store.getCache().getComponent(getURI(ns).toString()));
	}

	public void fireDetailedMapEvent(MapController source, DrawerLayout ns) {
		ContextMap map = ns.getConceptMap();
		URI destURI = getDetailedMapURI(ns);

		fireHistoryEvent(new HistoryEvent(HistoryEvent.MAP, source, getURI(map), getTitle(map), getURI(ns),
				getTitle(ns), destURI, getTitle(store.getCache().getComponent(destURI.toString()))));
	}

	public void fireOpenNewMapEvent(MapController source, ContextMap oldMap, URI newMap) {
		fireHistoryEvent(new HistoryEvent(HistoryEvent.MAP, source, getURI(oldMap), getTitle(oldMap), null, null,
				newMap, getTitle(store.getCache().getComponent(newMap.toString()))));
	}

	public void fireContentViewEvent(MapController source, ConceptLayout ns, URI content) {
		ContextMap map = source.getView().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();

		fireHistoryEvent(new HistoryEvent(HistoryEvent.CONTENT, source, getURI(map), getTitle(map), getURI(ns),
				getTitle(ns), content, getTitle(store.getCache().getComponent(content.toString()))));
	}

	public void addHistoryListener(HistoryListener l) {
		historyListeners.addElement(l);
	}

	public void removeHistoryListener(HistoryListener l) {
		historyListeners.removeElement(l);
	}

	public void fireHistoryEvent(HistoryEvent e) {
		for (int i = 0; i < historyListeners.size(); i++) {
			((HistoryListener) historyListeners.elementAt(i)).historyEvent(e);
		}
	}

}
