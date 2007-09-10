/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.style.StyleManager;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.Tracer;

public class MapStoreManager implements EditListener {
	ContextMap conceptMap;

	HashMap concepts;

	StyleManager styleManager;

	ResourceStore store;

	Vector editListeners;

	public MapStoreManager(URI mapURI, ResourceStore store,
			StyleManager styleManager, Container launchContainer)
			throws ComponentException {
		this.store = store;
		this.styleManager = styleManager;
		// conceptTypes = new Hashtable();

		editListeners = new Vector();

		if (launchContainer != null)
			loadReferenceRequestedContainersFromContainer(launchContainer,
					mapURI.toString());
		conceptMap = store.getAndReferenceConceptMap(mapURI);
		Container lc = store.getAndReferenceContainer(URI.create(conceptMap
				.getLoadContainer()));
		loadReferenceRequestedContainersFromContainer(lc, mapURI.toString());

		referenceConcepts();

		conceptMap.addEditListener(this);
	}

	private void loadReferenceRequestedContainersFromContainer(Container cont,
			String uri) {
		Iterator it = cont.getRequestedContainersForURI(uri).iterator();
		while (it.hasNext())
			try {
				store.getAndReferenceContainer(new URI((String) it.next()));
			} catch (URISyntaxException mue) {
			} catch (ComponentException ce) {
			}
	}

	public void addEditListener(EditListener e) {
		editListeners.add(e);
	}

	public void removeEditListener(EditListener e) {
		editListeners.remove(e);
	}

	public void referenceConcepts() {
		concepts = new HashMap();
		Vector layouts = conceptMap.getLayerManager().getDrawerLayouts(TreeTagNode.IGNORE_VISIBILITY);
		for (Iterator layIt = layouts.iterator(); layIt.hasNext();) {
			DrawerLayout dl = (DrawerLayout) layIt.next();
			referenceConcept(dl);			
		}
	}

	void referenceConcept(DrawerLayout layout) {
		try {
			URI uri = URIClassifier.parseValidURI(layout.getConceptURI(),
					conceptMap.getURI());
			Concept n = store.getAndReferenceConcept(uri);
			concepts.put(layout.getURI(), n);
			n.addEditListener(this);

			// Changed to StyleManager instead of neurontypes.
			/*
			 * uri = URIClassifier.parseValidURI(n.getType(), n.getURI());
			 * ConceptType nt = store.getAndReferenceConceptType(uri);
			 * conceptTypes.put(layout.getURI(), nt); nt.addEditListener(this);
			 */
		} catch (ComponentException e) {
			System.out.println("no concept found");
			Tracer.debug("No Component found: \n" + e.getMessage());
		}
	}

	public Concept getConcept(String layoutID) {
		return (Concept) concepts.get(layoutID);
	}

	public Collection getConcepts() {
		return concepts.values();
	}

	public StyleManager getStyleManager() {
		return styleManager;
	}

	/*
	 * public ConceptType getConceptType(String layoutID) { return (ConceptType)
	 * conceptTypes.get(layoutID); }
	 */

	public void detach() {
		conceptMap.removeEditListener(this);

		Iterator it = concepts.values().iterator();
		while (it.hasNext())
			((Concept) it.next()).removeEditListener(this);

		/*
		 * it = conceptTypes.values().iterator(); while(it.hasNext())
		 * ((ConceptType) it.next()).removeEditListener(this);
		 */

		concepts = null;
		// conceptTypes = null;

	}

	public ResourceStore getStore() {
		return store;
	}

	public ContextMap getConceptMap() {
		return conceptMap;
	}

	public void componentEdited(EditEvent e) {
		if (e.getEditType() == ContextMap.RESOURCELAYOUT_ADDED) {
			ResourceLayout os = (((ResourceLayout) e.getEditedObject())
					.getConceptMap()).getResourceLayout((String) e.getTarget());
			if (os != null && os instanceof DrawerLayout)
				referenceConcept((DrawerLayout) os);
		} else if (e.getEditType() == ContextMap.RESOURCELAYOUT_REMOVED) {
			Concept n = (Concept) concepts.get((String) e.getTarget());
			// ConceptType nt = (ConceptType) conceptTypes.get((String)
			// e.getTarget());

			if (n != null) {
				concepts.remove((String) e.getTarget());
				n.removeEditListener(this);
			}
			/*
			 * if(nt != null) { conceptTypes.remove((String) e.getTarget());
			 * nt.removeEditListener(this); }
			 */
		} else if (e.getEditType() == ContextMap.CONTEXTMAP_REFRESHED) {
			referenceConcepts();
		}
		for (int i = 0; i < editListeners.size(); i++)
			((EditListener) editListeners.get(i)).componentEdited(e);
	}
}
