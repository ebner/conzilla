/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;
import se.kth.cid.rdf.RDFComponent;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.formlet.FormletStore;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.impl.JenaModelQueryTarget;
import se.kth.nada.kmr.shame.util.RDFUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class Content2JenaQueryTarget implements PopupTrigger2QueryTarget {
	
	static {
		FormletStore.requireFormletConfigurations("formlets/formlets.rdf");
		FormletStore
				.requireFormletConfigurations("formlets/Simple_Dublin_Core/formlets.rdf");
		FormletStore.requireFormletConfigurations("formlets/ULM/formlets.rdf");
	}
	static public String dcFormletCId = "http://kmr.nada.kth.se/shame/SDC/formlet#Simple-profile";

	public static final int CONTEXT_IS_CONTENT = 1;

	public static final int CONTEXT_IS_CONTEXT_MAP = 0;
	
	public Content2JenaQueryTarget() {
	}

	public QueryTarget getCollaborativeQueryTarget(Object popupTrigger) {
		return getQueryTarget(popupTrigger, true);
	}
	
	public QueryTarget getQueryTarget(Object popupTrigger) {
		return getQueryTarget(popupTrigger, false);
	}
	
	private QueryTarget getQueryTarget(Object popupTrigger, boolean collaborative) {
		if (!(popupTrigger instanceof Component)) {
			return null;
		}
		Component component = (Component) popupTrigger;
		Model componentModel;
		ComponentManager rcm = ((Component) component).getComponentManager();
		Set componentModelList = rcm.getLoadedRelevantContainers();
		if (collaborative) {
			if (componentModelList.size() == 1) {
				componentModel = (Model) rcm.getContainer((URI) componentModelList.iterator().next());
			} else {
				componentModel = ModelFactory.createDefaultModel();
				Iterator i = componentModelList.iterator();
				while (i.hasNext()) {
					URI uri = (URI) i.next();
					RDFUtil.getModel((Model) rcm.getContainer(uri), componentModel, componentModel.createResource(component.getURI()), 0);
				}
			}
		} else {
			componentModel = (Model) rcm.getContainer(URI.create(component.getLoadContainer()));			
		}
		
		Resource componentResource = componentModel
				.createResource(((RDFComponent) component).getURI());

		List ontologies = new ArrayList();
		ontologies.addAll(getFormlet(popupTrigger).getOntologies());

		return new JenaModelQueryTarget(componentModel,
				componentResource, ontologies);
	}
	
	public Formlet getFormlet(Object popupTrigger) {
		String formletToUse = null;
		se.kth.cid.component.Resource reComp = (se.kth.cid.component.Resource) popupTrigger;
		formletToUse = EditPanel.getFormletConfigurationIdForContent(reComp);
		return FormletStore.getInstance().getFormlet(formletToUse);
	}

	public boolean isCollaborative(Object popupTrigger) {
		if (!(popupTrigger instanceof Component)) {
			return false;
		}
		Component component = (Component) popupTrigger;
		ComponentManager rcm = ((Component) component).getComponentManager();
		return rcm.getLoadedRelevantContainers().size() > 1;
	}
}
