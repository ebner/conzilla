/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.MapMenuTool;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.ComponentWithTitle;

/**
 * Displays a submenu of Context-maps wherein the concept or concept-relation
 * occurs (contextual neighbourhood). The current Context-map is grayed out in
 * the list. If only the current Context-map contains the concept, the menuitem
 * will be grayed out as well.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class SurfAlterationTool extends MapMenuTool {
	JMenu choice;

	Browse browse;

	public SurfAlterationTool(MapController cont, Browse browse) {
		super("SURF", BrowseMapManagerFactory.class.getName(), cont);
		choice = new JMenu();
		this.browse = browse;
	}

	public void update(MapEvent e) {
		super.update(e);
		choice.removeAll();

		// In case something is wrong or triggered over background.
		if (mapEvent == null || (mapEvent.mapObject == null || mapEvent.mapObject.getConcept() == null)) {
			JMenuItem mi = new JMenuItem();
			setJMenuItem(mi);
			mi.setEnabled(false);
			return;
		}

		Resource component = mapEvent.mapObject.getConcept();
		ContextMap cMap = controller.getConceptMap();

		Set neighbourHoodMaps = new HashSet();
		ContainerManager containerManager = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
		for (Iterator containers = containerManager.getContainers(Container.COMMON).iterator(); containers.hasNext();) {
			Container container = (Container) containers.next();
			neighbourHoodMaps.addAll(container.getMapsReferencingResource(component.getURI()));
		}

		// If not several alternatives (detailedmap plus contextmaps)
		// then return a menuItem (possible not enabled if no alternative)
		// otherwise a menu is returned.
		if (neighbourHoodMaps.size() == 1) {
			JMenuItem mi = new JMenuItem();
			mi.setEnabled(false);
			setJMenuItem(mi);
		} else {
			/*
			 * JLabel label = new JLabel( manager.getString(
			 * BrowseMapManagerFactory.class.getName(), "NEIGHBORHOOD"));
			 * label.setPreferredSize(new java.awt.Dimension(40, 15));
			 * choice.add(label);
			 */

//			boolean detailedMapIsThere = false;

			TreeSet neigh = new TreeSet();
			for (Iterator nbhmIt = neighbourHoodMaps.iterator(); nbhmIt.hasNext();) {
				String nbUri = (String) nbhmIt.next();

//				String title = null;
				try {
					ContextMap cm = ConzillaKit.getDefaultKit().getResourceStore().getAndReferenceConceptMap(
							new URI(nbUri));

					ComponentWithTitle cwt = new ComponentWithTitle(cm);
					neigh.add(cwt);
				} catch (URISyntaxException urise) {
					urise.printStackTrace();
				} catch (ComponentException ce) {
				}
			}

			for (Iterator neit = neigh.iterator(); neit.hasNext();) {
				final ComponentWithTitle compT = (ComponentWithTitle) neit.next();
				AbstractAction relAction = new AbstractAction(compT.getTitle()) {
					public void actionPerformed(ActionEvent e) {
						surfContextMap(compT.getComponent());
					}
				};

				if (compT.getComponent().getURI().equals(cMap.getURI())) {
					relAction.setEnabled(false);
				}
				choice.add(relAction);
			}
			setJMenuItem(choice);
		}
	}

	/**
	 * This is a surf-command that results in a zoomIn via the controller.
	 * Observe that updateState has to have been succesfully last time called.
	 * Otherwise the surf-action isn't activated and this function isn't called.
	 * 
	 * @see MapController#showMap(URI)
	 */
	public void surfDetailedMap() {
		if (mapEvent.mapObject.getDrawerLayout() == null)
			return;
		try {
			DrawerLayout ns = mapEvent.mapObject.getDrawerLayout();
			ContextMap cMap = controller.getConceptMap();
			controller.showMap(URIClassifier.parseValidURI(mapEvent.mapObject.getDrawerLayout().getDetailedMap(), cMap
					.getURI()));
			controller.getHistoryManager().fireDetailedMapEvent(controller, ns);
			browse.markLastConcept(controller.getView().getMapScrollPane(), URIClassifier.parseValidURI(
					mapEvent.mapObject.getDrawerLayout().getConceptURI(),
					mapEvent.mapObject.getDrawerLayout().getConceptMap().getURI()).toString());
		} catch (ControllerException e) {
			ErrorMessage.showError("Load Error", "Failed to load map\n\n"
					+ mapEvent.mapObject.getDrawerLayout().getDetailedMap(), e, controller.getView().getMapScrollPane());
		}
	}

	public void surfContextMap(Resource comp) {
		ContextMap oldMap = controller.getConceptMap();
		try {
			controller.showMap(new URI(comp.getURI()));
			controller.getHistoryManager().fireOpenNewMapEvent(controller, oldMap,
					new URI(comp.getURI()));
			browse.markLastConcept(controller.getView().getMapScrollPane(), URIClassifier.parseValidURI(
					mapEvent.mapObject.getDrawerLayout().getConceptURI(),
					mapEvent.mapObject.getDrawerLayout().getConceptMap().getURI()).toString());
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		} catch (ControllerException e) {
			ErrorMessage.showError("Load Error", "Failed to load map\n\n" + comp.getURI(), e, controller
					.getView().getMapScrollPane());
		}
	}
}
