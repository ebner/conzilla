/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.metadata;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

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
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.LayerLayout;
import se.kth.nada.kmr.shame.formlet.FormletStore;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class EditPanel extends InfoPanel {
    static {
        FormletStore.requireFormletConfigurations("formlets/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/Simple_Dublin_Core/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/ULM/formlets.rdf");
        FormletStore.requireFormletConfigurations("formlets/foaf/formlets.rdf");
    }
    
    static public class EditMetadataTool extends ActionMapMenuTool {
    
        Component comp;

        public EditMetadataTool(MapController controller) {
            super("EDIT_METADATA", EditMapManagerFactory.class.getName(), controller);
        }


        /**
         * @see se.kth.cid.conzilla.tool.ActionMapMenuTool#updateEnabled()
         */
        protected boolean updateEnabled() {
            if (mapEvent.hitType == MapEvent.HIT_NONE) {
                comp = controller.getConceptMap();
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
            EditPanel.launchEditPanelInFrame(comp);
        }
    }
    
    static public String dcFormletCId = "http://kmr.nada.kth.se/shame/SDC/formlet#Simple-profile";

    static public String concept_form = "http://kmr.nada.kth.se/shame/ulm/formlet#concept-form";

    static public String context_form = "http://kmr.nada.kth.se/shame/ulm/formlet#context-form";
    
    static public String container_form = "http://kmr.nada.kth.se/shame/ulm/formlet#container-compound";
    
    static public void launchEditPanelInFrame(final se.kth.cid.component.Component component) {
        final EditPanel editPanel = new EditPanel(component);
        JFrame frame = new JFrame();
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                editPanel.finishEdit();
                if (component.isEdited()) {
                    component.getComponentManager().refresh();
                    if (component instanceof FiringResource) {
                        ((FiringResource) component).fireEditEvent(new EditEvent(component, null, Component.ATTRIBUTES_EDITED, null));
                    }
                }                
            }
        };
        addDoneFunctionality(frame, editPanel, action);
        frame.setTitle("Info on resource "+ component.getURI());
        frame.setVisible(true);
        frame.pack();
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
