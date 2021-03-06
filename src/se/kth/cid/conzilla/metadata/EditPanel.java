/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Component;
import se.kth.cid.component.Container;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.FiringResource;
import se.kth.cid.component.Resource;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManagerFactory;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.LayerLayout;
import se.kth.nada.kmr.shame.applications.util.FormletStoreSingleton;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class EditPanel extends InfoPanel {
	
	static Log log = LogFactory.getLog(EditPanel.class);
	
    static {
        try {
			FormletStoreSingleton.requireFormletConfigurations("formlets/formlets.rdf");
			FormletStoreSingleton.requireFormletConfigurations("formlets/Simple_Dublin_Core/formlets.rdf");
	        FormletStoreSingleton.requireFormletConfigurations("formlets/ULM/formlets.rdf");
	        FormletStoreSingleton.requireFormletConfigurations("formlets/foaf/formlets.rdf");
		} catch (IOException e) {
			log.error(e);
		}
    }
    
    static public class EditMetadataTool extends Tool {
    
        Component comp;

        public EditMetadataTool(MapController controller) {
            super("EDIT_METADATA", EditMapManagerFactory.class.getName(), controller);
        }


        /**
         * @see se.kth.cid.conzilla.tool.Tool#updateEnabled()
         */
        protected boolean updateEnabled() {
            if (mapEvent.hitType == MapEvent.HIT_NONE) {
                comp = mcontroller.getConceptMap();
            } else if (mapObject != null 
                && mapObject.getConcept() != null){
                comp = mapObject.getConcept();
            } else {
                return false;
            }
            return true;
//            return comp.isEditable();
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            EditPanel.launchEditPanelInFrame(comp, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					mcontroller.getConceptMap().getComponentManager().getUndoManager().makeChange();
				}
            });
        }
    }
    
    static public String dcFormletCId = "http://kmr.nada.kth.se/shame/SDC/formlet#Simple-profile";

    static public String concept_form = "http://kmr.nada.kth.se/shame/ulm/formlet#concept-form";

    static public String context_form = "http://kmr.nada.kth.se/shame/ulm/formlet#context-form";
    
    static public String container_form = "http://kmr.nada.kth.se/shame/ulm/formlet#container-compound";

    static public void launchEditPanelInFrame(final se.kth.cid.component.Component component) {
    	launchEditPanelInFrame(component, null);
    }

    static public void launchEditPanelInFrame(final se.kth.cid.component.Component component, final Action doneAction) {
        final EditPanel editPanel = new EditPanel(component);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                editPanel.finishEdit();
                if (component.isEdited()) {
                    component.getComponentManager().refresh();
                    if (component instanceof FiringResource) {
                        ((FiringResource) component).fireEditEvent(new EditEvent(component, null, Component.ATTRIBUTES_EDITED, null));
                    }
                    if (doneAction != null) {
                    	doneAction.actionPerformed(e);
                    }
                }                
            }
        };
        addDoneFunctionality(frame, editPanel, action);
        frame.setTitle("Info on resource "+ component.getURI());
        frame.pack();
        frame.setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
        frame.setVisible(true);
    }

    static public String getFormletConfigurationIdForResource(Resource r) {
        if (r != null
                && r instanceof ContextMap) {
            return EditPanel.context_form;
        } else if ( r!= null 
                && r instanceof Concept) {
            return EditPanel.concept_form;
        } else if (r!= null 
        		&& r instanceof LayerLayout) {
            return EditPanel.context_form;
        } else if (r!=null 
        		&& r instanceof Container){
        	return EditPanel.container_form;
        } else {
            return EditPanel.dcFormletCId;
        }
    }

    static public String getFormletConfigurationIdForContent(Resource r) {
        if (r != null
                && r instanceof ContextMap) {
            return EditPanel.context_form;
        } else {
            return EditPanel.dcFormletCId;
        }
    }

    /**
     * @param component
     */
    public EditPanel(Component component) {
        super();
        
        setFormletConfigurationId(getFormletConfigurationIdForResource(component));

        allowEditing = true;
        Container  container = null;
        if (component instanceof ContextMap ||
            component instanceof LayerLayout) {
            container = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().getCurrentLayoutContainer();
        } else {
            container = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager().getCurrentConceptContainer();
        }
        editResource(container, component);
    }
}
