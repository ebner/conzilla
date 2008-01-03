/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.InfoPanel;
import se.kth.cid.notions.ContentInformation;
import se.kth.cid.util.Tracer;
import se.kth.nada.kmr.shame.applications.util.FormletStoreSingleton;

/**
 * Basically a convenience method for Content editing using the 
 * {@link se.kth.cid.conzilla.metadata.InfoPanel}.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class ContentEditor extends InfoPanel {
    static {
        try {
			FormletStoreSingleton.requireFormletConfigurations("formlets/notions/formlets.rdf");
			FormletStoreSingleton.requireFormletConfigurations("formlets/formlets.rdf");
	        FormletStoreSingleton.requireFormletConfigurations("formlets/Simple_Dublin_Core/formlets.rdf");
	        FormletStoreSingleton.requireFormletConfigurations("formlets/ULM/formlets.rdf");
		} catch (IOException e) {
			Tracer.debug(e.getMessage());
		}
    }
    
    public static String contentFormletConfigurationId = "http://kmr.nada.kth.se/shame/notions/formlet#DC-content";
    
    static public void launchContentEditorInFrame(
        final Container container,
        final Component contentComponent) {
        JFrame frame = new JFrame();
        frame.setLocationRelativeTo(ConzillaKit.getDefaultKit().getConzilla().getViewManager().getWindow());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Create new content");
        JPanel split = new JPanel();
        split.setLayout(new BoxLayout(split, BoxLayout.Y_AXIS));


        Box predicateBox = Box.createHorizontalBox();
        JComboBox predicate = new JComboBox();
        predicate.addItem("CV.contains");
        predicateBox.add(new JLabel("Relation from Concept: "));
        predicateBox.add(predicate);
        split.add(predicateBox);
                
        final ContentEditor contentEditor = new ContentEditor(contentFormletConfigurationId);
        contentEditor.setContainerChoosable(false);
        contentEditor.setFormletConfigurationChoosable(false);
        contentEditor.editContent(container, contentComponent);
        split.add(contentEditor);
        
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //finish the editor off
                contentEditor.finishEdit();
                //Mark the model as edited, otherwise we can't save it.
                container.setEdited(true);
                contentComponent.getComponentManager().refresh();
            }
        };
        addDoneFunctionality(frame, split, action);
        

        frame.setVisible(true);
        frame.pack();
    }
    
    public ContentEditor(String fcid) {
        super(fcid);
        allowEditing = true;
    }
    
    public void editContent(Container container, Component component) {
        setFormletConfigurationId(EditPanel.getFormletConfigurationIdForContent(component));
        editResource(container, component);
    }
    
    public void editContent(ContentInformation ci, Component c) {
       showContent(ci, c, true);
    }

    public void presentContent(ContentInformation ci, Component c) {
        showContent(ci, c, false);
     }

    private void showContent(ContentInformation ci, Component c, boolean edit) {
        setFormletConfigurationId(EditPanel.getFormletConfigurationIdForContent(c));
        Container container = null;
        try {
            container = ConzillaKit.getDefaultKit().
                getResourceStore().getAndReferenceContainer(URI.create(c.getLoadContainer()));
        } catch (ComponentException e) {
            e.printStackTrace();
        }
        if (container == null) {
            container = ci.getContainer();
        }
        if (edit) {
            editResource(container, c);      
        } else {
            presentResource(container, c);
        }
    }
}
