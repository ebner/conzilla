/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.collaboration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;
import se.kth.cid.rdf.CV;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.impl.JenaModelQueryTarget;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ContainerEntry2JenaQueryTarget implements PopupTrigger2QueryTarget {

	public ContainerEntry2JenaQueryTarget() {
	}

	public QueryTarget getQueryTarget(Object popupTrigger) {
		if (!(popupTrigger instanceof ContainerEntry)) {
			return null;
		}
		ContainerEntry ce = (ContainerEntry) popupTrigger;
		String metadata = ce.getMetadata();
		Model m = ModelFactory.createDefaultModel();
		Resource resource = m.createResource(ce.getContainerURI());
		if (metadata != null) {
			StringReader sr = new StringReader(metadata);
			m.read(sr, ce.getContainerURI());
		} else {
			m.add(m.createStatement(resource, m.createProperty(CV.title), "No information available"));
		}

		List ontologies = new ArrayList();
		ontologies.addAll(getFormlet(popupTrigger).getOntologies());
		
		return new JenaModelQueryTarget(m, resource, ontologies);
	}

	public Formlet getFormlet(Object popupTrigger) {
		return ConzillaKit.getDefaultKit().getFormletStore().getFormlet(EditPanel.container_form);
	}

	public QueryTarget getCollaborativeQueryTarget(Object popupTrigger) {
		return getQueryTarget(popupTrigger);
	}

	public boolean isCollaborative(Object popupTrigger) {
		return false;
	}
}
