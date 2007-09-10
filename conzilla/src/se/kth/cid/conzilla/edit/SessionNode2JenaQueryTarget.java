/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.util.ArrayList;
import java.util.List;

import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;
import se.kth.cid.rdf.CV;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.formlet.FormletStore;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.impl.JenaModelQueryTarget;

import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * To be used together with SessionPopupInfo. Returns a meta-data form.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionNode2JenaQueryTarget implements PopupTrigger2QueryTarget {
		
	public SessionNode2JenaQueryTarget() {
	}

	/**
	 * @see se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget#getQueryTarget(java.lang.Object)
	 */
	public QueryTarget getQueryTarget(Object popupTrigger) {
		if (!(popupTrigger instanceof SessionNode)) {
			return null;
		}
		SessionNode node = (SessionNode) popupTrigger;
		Model m = node.getMetaData();
		Resource resource;
		if (m == null) {
			m = new ModelMem();
			resource = m.createResource(node.getURI());
			m.add(m.createStatement(resource, m.createProperty(CV.title), "No information available"));
		} else {
			resource = m.createResource(node.getURI());
		}

		List ontologies = new ArrayList();
		ontologies.addAll(getFormlet(popupTrigger).getOntologies());
		return new JenaModelQueryTarget(m, resource, ontologies);
	}

	/**
	 * @see se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget#getFormlet(java.lang.Object)
	 */
	public Formlet getFormlet(Object popupTrigger) {
		return FormletStore.getInstance().getFormlet(EditPanel.context_form);
	}

	public QueryTarget getCollaborativeQueryTarget(Object popupTrigger) {
		return getCollaborativeQueryTarget(popupTrigger);
	}

	public boolean isCollaborative(Object popupTrigger) {
		return false;
	}
	
}