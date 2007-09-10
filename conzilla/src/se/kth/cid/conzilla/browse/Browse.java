/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JMenu;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapEventListener;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.Tracer;

public class Browse {
	/**
	 * Listens for press and release in the map.
	 */
	MapEventListener pressListener;

	/**
	 * Listens for clicks in the map.
	 */
	MapEventListener klickListener;

	/**
	 * Listens for moves in the map.
	 */
	MapEventListener cursorListener;

	PropertyChangeListener zoomListener;

	/**
	 * The menu to popup.
	 */
	BrowseMenu browseMenu;

	/**
	 * The current cursor.
	 */
	int cursor = Cursor.DEFAULT_CURSOR;

	MapController controller;

	MapObject marked = null;

	/**
	 * A layer on top containing popups in the form of both submaps and
	 * descriptions.
	 */
	PopupLayer popup;

	private HashSet lastConcept;

	public Browse(MapController cont, PopupLayer popup) {
		this.popup = popup;
		browseMenu = new BrowseMenu(cont, this);
		controller = cont;
		setListeners();
		controller.addPropertyChangeListener(zoomListener);
	}

	protected void setListeners() {

		cursorListener = new MapEventListener() {
			public void eventTriggered(MapEvent e) {
				updateBrowse(e);
			}
		};
		pressListener = new MapEventListener() {
			public void eventTriggered(MapEvent e) {
				if (e.mouseEvent.isPopupTrigger()) {
					popup.removeAllPopups();
					browseMenu.popup(e);
					// unMark();
				}
			}
		};

		klickListener = new MapEventListener() {
			public void eventTriggered(MapEvent e) {
				MapScrollPane scroll = controller.getView().getMapScrollPane();
				unmarkLastConcept(scroll);

				if (e.mapObject != null
						&& e.mapObject.getDrawerLayout() != null
						&& e.mapObject.getDrawerLayout().getDetailedMap() != null) {
					if (e.mouseEvent.getClickCount() == 2) {
						// unMark();
						e.consume();
						DrawerLayout ns = e.mapObject.getDrawerLayout();

						try {
							ContextMap cMap = controller.getConceptMap();
							controller.showHyperlinkedMap(URIClassifier
									.parseValidURI(
											e.mapObject.getDrawerLayout()
													.getDetailedMap(), cMap
													.getURI()));
							controller.getHistoryManager()
									.fireDetailedMapEvent(controller, ns);
							markLastConcept(scroll, URIClassifier
									.parseValidURI(ns.getConceptURI(),
											ns.getConceptMap().getURI())
									.toString());
						} catch (ControllerException ce) {
							ErrorMessage.showError("Load Error",
									"Failed to load map\n\n"
											+ e.mapObject.getDrawerLayout()
													.getDetailedMap(), ce,
									scroll);
						}
					}
				}
			}
		};
		zoomListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(View.ZOOM_PROPERTY))
					setScale(((Double) evt.getNewValue()).doubleValue(),
							((Double) evt.getOldValue()).doubleValue());
			}
		};
	}

	void setShownCursor(int type) {
		JComponent c = controller.getView().getMapScrollPane().getLayeredPane();

		if (c.getCursor().getType() != type)
			c.setCursor(new Cursor(type));
	}

	protected void updateBrowse(MapEvent e) {
		// If menu is popup return
		if (((JMenu) browseMenu.getJMenuItem()).getPopupMenu().isVisible())
			return;

		// Mark and HandCursor below.
		if (e.hitType != MapEvent.HIT_NONE) {

			DrawerLayout ns = e.mapObject.getDrawerLayout();
			if (ns.getDetailedMap() != null)
				setShownCursor(Cursor.HAND_CURSOR);
			else
				setShownCursor(Cursor.DEFAULT_CURSOR);

			MapObject newMarked = e.mapObject;
			if (marked != newMarked) {
				unMark();
				marked = newMarked;
				mark();
			}
		} else {
			setShownCursor(Cursor.DEFAULT_CURSOR);

			unMark();
			marked = null;
		}
	}

	private void mark() {
		// The new mark
		Mark overMark = new Mark(BrowseMapManagerFactory.COLOR_MOUSEOVER, null,
				null);
		overMark.setLineWidth((float) 3.5);

		if (marked.getConcept() == null)
			// If no concept present, set.local mark only.
			marked.pushMark(overMark, this);
		else
			// Otherwise set global mark for this concept.
			ConzillaKit.getDefaultKit().getConzilla().pushMark(getMarkSet(),
					overMark, this);
	}

	private void unMark() {
		// If nothing is marked at present nothing has to be unmarked.
		if (marked != null) {
			if (marked.getConcept() == null)
				// If no concept is present no global mark was set.
				marked.popMark(this);
			else
				// Otherwise remove global mark for this concept.
				ConzillaKit.getDefaultKit().getConzilla().popMark(getMarkSet(),
						this);
		}
	}

	/**
	 * Calculates a set of direct and indirect markes from the original mark.
	 * Shouldn't be called if the original mark has no concept.
	 * 
	 * @return a Set of concepts.
	 */
	private Set getMarkSet() {
		HashSet set = new HashSet();
		set.add(marked.getConcept().getURI());

		// getMarkSet(marked, marked.getConcept(), set,
		// controller.getMapScrollPane().getDisplayer());

		return set;

	}

	// FIXME: transitive markings has to be redone.
	/*
	 * private void getMarkSet(MapObject nmo, Concept ne, HashSet set,
	 * MapDisplayer displayer) { ComponentStore store =
	 * controller.getConzillaKit().getComponentStore(); String[] ttriple1 =
	 * {"Mediator"}; String[] ttriple2 = {"Transitive"}; ConceptType type =
	 * null; if (nmo != null) type = nmo.getConceptType(); else try { type =
	 * store.getAndReferenceConceptType(URIClassifier.parseValidURI(ne.getType(),ne.getURI())); }
	 * catch (ComponentException ce) {} if (type !=null &&
	 * MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
	 * "ConceptType", "Conzilla", ttriple1)) { boolean transitive =
	 * MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
	 * "ConceptType", "Conzilla", ttriple2); Concept [] concepts =
	 * ne.getChilds(); for (int i =0 ; i<concepts.length; i++) { Triple triple =
	 * concepts[i].getTriple(); try { URI uri =
	 * URIClassifier.parseURI(triple.objectURI(),
	 * URIClassifier.parseValidURI(marked.getConcept().getURI()));
	 * set.add(uri.toString()); if (transitive) try { Concept next =
	 * store.getAndReferenceConcept(uri); getMarkSet(null, next, set,
	 * displayer); } catch (ComponentException ce) {} } catch
	 * (MalformedURIException e) {} } } }
	 */

	public void install(MapScrollPane pane) {

		popup.activate(pane);
		pane.getDisplayer().addMapEventListener(pressListener,
				MapDisplayer.PRESS_RELEASE);
		pane.getDisplayer().addMapEventListener(klickListener,
				MapDisplayer.CLICK);
		pane.getDisplayer().addMapEventListener(cursorListener,
				MapDisplayer.MOVE_DRAG);

	}

	protected void uninstall(MapScrollPane pane) {

		pane.getDisplayer().removeMapEventListener(pressListener,
				MapDisplayer.PRESS_RELEASE);
		pane.getDisplayer().removeMapEventListener(klickListener,
				MapDisplayer.CLICK);
		pane.getDisplayer().removeMapEventListener(cursorListener,
				MapDisplayer.MOVE_DRAG);

		popup.deactivate(pane);

		unMark();
		marked = null;
		unmarkLastConcept(controller.getView().getMapScrollPane());
	}

	public void markLastConcept(MapScrollPane pane, String uri) {
		Tracer.debug("Marking " + uri);
		unmarkLastConcept(pane);
		if (uri == null)
			return;

		Mark lastMark = new Mark(BrowseMapManagerFactory.COLOR_LASTCONCEPT,
				null, null);
		lastMark.setLineWidth((float) 2.0);

		lastConcept = new HashSet();
		lastConcept.add(uri);
		controller.getView().getMapScrollPane().getDisplayer().pushMark(
				lastConcept, lastMark, this);
	}

	public void unmarkLastConcept(MapScrollPane pane) {
		if (lastConcept != null) {
			controller.getView().getMapScrollPane().getDisplayer().popMark(
					lastConcept, this);
			lastConcept = null;
		}
	}

	public void setScale(double newscale, double oldscale) {
		popup.setScale(newscale, oldscale);
	}
}
