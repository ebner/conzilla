/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.layer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import se.kth.cid.component.ComponentManager;
import se.kth.cid.conzilla.layer.LayerControl.LayerEntry;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;
import se.kth.cid.layout.LayerLayout;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.formlet.FormletStore;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.impl.JenaModelQueryTarget;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class LayerEntry2JenaQueryTarget implements PopupTrigger2QueryTarget {
		
	public LayerEntry2JenaQueryTarget() {
	}

	public QueryTarget getQueryTarget(Object popupTrigger) {
		
		if (!(popupTrigger instanceof LayerEntry)) {
			return null;
		}
		LayerEntry ce = (LayerEntry) popupTrigger;
		
		LayerLayout ll = ce.getLayerLayout();
		ComponentManager rcm = ll.getComponentManager();
		Model componentModel = (Model) rcm.getContainer(URI.create(ll.getLoadContainer()));
		
		Resource componentResource = componentModel.createResource(ll.getURI());

		List ontologies = new ArrayList();
		ontologies.addAll(getFormlet(popupTrigger).getOntologies());

		return new JenaModelQueryTarget(componentModel,
				componentResource, ontologies);
	}

	public Formlet getFormlet(Object popupTrigger) {
		return FormletStore.getInstance().getFormlet(EditPanel.context_form);
	}

	public QueryTarget getCollaborativeQueryTarget(Object popupTrigger) {
		return getQueryTarget(popupTrigger);
	}

	public boolean isCollaborative(Object popupTrigger) {
		return false;
	}
}
