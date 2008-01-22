/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.RDFComponent;
import se.kth.cid.rdf.RDFModel;
import se.kth.nada.kmr.shame.applications.util.FormletStoreSingleton;
import se.kth.nada.kmr.shame.formlet.Formlet;
import se.kth.nada.kmr.shame.query.QueryTarget;
import se.kth.nada.kmr.shame.query.impl.JenaModelQueryTarget;
import se.kth.nada.kmr.shame.util.RDFUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class MapObject2JenaQueryTarget implements PopupTrigger2QueryTarget {
	
	static Log log = LogFactory.getLog(MapObject2JenaQueryTarget.class);
	
	static {
		try {
			FormletStoreSingleton.requireFormletConfigurations("formlets/formlets.rdf");
			FormletStoreSingleton.requireFormletConfigurations("formlets/ULM/formlets.rdf");
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	public MapObject2JenaQueryTarget() {
	}

	public QueryTarget getCollaborativeQueryTarget(Object popupTrigger) {
		return getQueryTarget(popupTrigger, true);
	}
	
	public QueryTarget getQueryTarget(Object popupTrigger) {
		return getQueryTarget(popupTrigger, false);
	}
	
	private QueryTarget getQueryTarget(Object popupTrigger, boolean collaborative) {
		if (!(popupTrigger instanceof MapObject) && !(popupTrigger instanceof ContextMap)) {
			return null;
		}
		
		ContextMap cMap;
		Component component;
		if (popupTrigger instanceof MapObject) {
			cMap = ((MapObject) popupTrigger).getDrawerLayout().getConceptMap();
			component = ((MapObject) popupTrigger).getConcept();
		} else {
			cMap = (ContextMap) popupTrigger;
			component = cMap;
		}
		
		Model componentModel;
		ComponentManager rcm = ((Component) component).getComponentManager();
		Set componentModelList = rcm.getLoadedRelevantContainers();
		ComponentManager cMan = cMap.getComponentManager();
		if (collaborative) {
			if (componentModelList.size() == 1) {
				componentModel = (Model) rcm.getContainer((URI) componentModelList.iterator().next());
			} else {
				componentModel = ModelFactory.createDefaultModel();
				Iterator i = componentModelList.iterator();
				while (i.hasNext()) {
					URI uri = (URI) i.next();
					if (cMan.getContainerVisible(uri)) {
						RDFUtil.getModel((Model) rcm.getContainer(uri), componentModel, componentModel.createResource(component.getURI()), 0);
					}
				}
			}
		} else {
			componentModel = (Model) rcm.getContainer(URI.create(component.getLoadContainer()));			
		}
		
		Resource componentResource = componentModel
				.createResource(((RDFComponent) component).getURI());

		List ontologies = new ArrayList();
		ontologies.addAll(getFormlet(popupTrigger).getOntologies());

		return new JenaModelQueryTarget(componentModel, componentResource, ontologies);
	}

	public Formlet getFormlet(Object popupTrigger) {
		String formletToUse = null;
		if (popupTrigger instanceof ContextMap) {
			formletToUse = EditPanel.context_form;
		} else {
			formletToUse = EditPanel.concept_form;
		}
		return ConzillaKit.getDefaultKit().getFormletStore().getFormlet(formletToUse);
	}

	public boolean isCollaborative(Object popupTrigger) {
		if (!(popupTrigger instanceof MapObject) && !(popupTrigger instanceof ContextMap)) {
			return false;
		}
				
		if (popupTrigger instanceof MapObject) {
			ContextMap cMap = ((MapObject) popupTrigger).getDrawerLayout().getConceptMap();
			Component component = ((MapObject) popupTrigger).getConcept();
			Iterator it = component.getComponentManager().getLoadedRelevantContainers().iterator();
			boolean one = false;
			while (it.hasNext()) {
				URI uri = (URI) it.next();
				if (cMap.getComponentManager().getContainerVisible(uri)) {
					if (!one) {
						one = true;
					} else {
						return true;
					}
				}
			}
			return false;
			
		} else {
			ContextMap cMap = (ContextMap) popupTrigger;
			ComponentManager rcm = cMap.getComponentManager();
			Iterator it = rcm.getLoadedRelevantContainers().iterator();
			boolean one = false;
			while (it.hasNext()) {
				URI uri = (URI) it.next();
				if (rcm.getContainerVisible(uri)) {
					RDFModel container = (RDFModel) rcm.getContainer(uri);
					if (container.contains(container.createResource(cMap.getURI()), null, (RDFNode) null)) {
						if (!one) {
							one = true;
						} else {
							return true;
						}
					}
				}
			}
			return false;
		}
	}
}
