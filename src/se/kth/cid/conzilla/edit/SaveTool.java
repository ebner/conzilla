/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.rdf.RDFComponent;
import se.kth.cid.rdf.RDFComponentFactory;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.rdf.RDFModel;

public class SaveTool extends Tool implements EditListener {

	Log log = LogFactory.getLog(SaveTool.class);

	MapController controller;

	boolean saving = false;

	public SaveTool(MapController cont) {
		super("SAVE", EditMapManagerFactory.class.getName());
		setIcon(Images.getImageIcon(Images.ICON_SAVE));
		controller = cont;

		MapStoreManager storeManager = controller.getView().getMapScrollPane().getDisplayer().getStoreManager();
		storeManager.addEditListener(this);

		updateEdited();
	}

	private void updateEdited() {
		MapStoreManager storeManager = controller.getView().getMapScrollPane().getDisplayer().getStoreManager();

		boolean enable = false;

		ContextMap cm = storeManager.getConceptMap();
		if (cm.isEdited())
			enable = true;
		DrawerLayout[] ns = cm.getDrawerLayouts();
		for (int i = 0; i < ns.length; i++) {
			Concept n = storeManager.getConcept(ns[i].getURI());
			if (n != null && n.isEdited())
				enable = true;
		}
		setEnabled(enable);
	}

	public void componentEdited(EditEvent e) {
		if (!saving) {
			updateEdited();
		}
	}

	public void actionPerformed(ActionEvent e) {
		saving = true;

		// save all concepts!!!
		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		MapStoreManager storeManager = controller.getView().getMapScrollPane().getDisplayer().getStoreManager();
		ContextMap cmap = storeManager.getConceptMap();
		RDFContainerManager contMan = (RDFContainerManager) store.getContainerManager();
		
		try {
			// ComponentHandler handler = store.getHandler();

			if (contMan.getIncludeRequestsAutomaticallyManaged()) {
				for (int dli = 0; dli < cmap.getDrawerLayouts().length; dli++) {
					DrawerLayout dl = cmap.getDrawerLayouts()[dli];
					if (dl.isEdited()) {
						RDFModel model = (RDFModel) contMan.getContainer(dl.getLoadContainer());
						String uri = dl.getDetailedMap();
						
						if (uri != null) {
							RDFComponent rc = ((RDFComponentFactory) store.getComponentManager()).findComponent(uri);
							if (rc != null) {
								if (!rc.getLoadContainer().equals(model.getURI())) {
									model.addRequestedContainerForURI(uri, rc.getLoadContainer());
								}
							}
						}
					}
				}

				HashSet set = new HashSet();
				Iterator it = storeManager.getConcepts().iterator();

				while (it.hasNext()) {
					Concept c = (Concept) it.next();
					log.debug("concept " + c.getURI());
					set.add(((se.kth.cid.component.Component) c).getLoadContainer());
				}

				// This set of container uris will be added as
				// requestedcontainers for
				// the conceptmap cmap in it's loadContainer, hence this
				// loadContainer shouldn't be listed.
				set.remove(cmap.getLoadContainer());

				if (cmap.isEdited()) {
					Container cont = store.getAndReferenceContainer(URI.create(cmap.getLoadContainer()));

					// Remove old requested containers.
					Iterator it3 = cont.getRequestedContainersForURI(cmap.getURI()).iterator();
					while (it3.hasNext()) {
						cont.removeRequestedContainerForURI(cmap.getURI(), (String) it3.next());
					}

					// Add the load-containers for all referred concepts.
					Iterator it2 = set.iterator();
					while (it2.hasNext()) {
						cont.addRequestedContainerForURI(cmap.getURI(), (String) it2.next());
					}
				}
			}

			Iterator itC = storeManager.getConcepts().iterator();

			while (itC.hasNext()) {
				Concept c = (Concept) itC.next();
				if (c.isEdited()) {
					log.debug("And now saving it");
					store.getComponentManager().saveResource(c);
				}
			}

			if (cmap.isEdited()) {
				log.debug("Saving conceptmap");
				store.getComponentManager().saveResource(cmap);
			}

		} catch (ComponentException ce) {
			log.error("Failed to save layout", ce);
			ErrorMessage.showError("Save Error", "Failed to save layout\n\n" + cmap.getURI(), ce, controller
					.getView().getMapScrollPane().getDisplayer());
		}

		saving = false;
		updateEdited();

		/*
		 * DrawerLayout nss[] = cmap.getDrawerLayouts(); Concept concept;
		 * for(int i = 0; i < nss.length; i++) { concept =
		 * controller.getMapScrollPane().getDisplayer().getStoreManager().getConcept(nss[i].getURI());
		 * try { if (concept != null && concept.isEdited()) {
		 * store.getHandler().saveComponent(concept); } }
		 * catch(ComponentException ce) { // ce.printStackTrace();
		 * ErrorMessage.showError("Save Error", "Failed to save concept\n\n" +
		 * nss[i].getConceptURI(), ce,
		 * controller.getMapScrollPane().getDisplayer()); }
		 */
	}

	public void detach() {
		controller.getView().getMapScrollPane().getDisplayer().getStoreManager().removeEditListener(this);
		controller = null;
	}

}
