/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.content.ContentEditor;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.metadata.InfoPanel;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.util.Tracer;

/** 
 *  @author Matthias Palm?r
 *  @version $Revision$
 */
public class LinkToContentMapTool extends ActionMapMenuTool {

    Concept concept;
    String kind;
    public static final String LINK_TO_CONTENT = "LINK_TO_CONTENT";
    public static final String LINK_TO_CONTENT_IN_CONTEXT =
        "LINK_TO_CONTENT_IN_CONTEXT";
    MapController mc;
    JFrame bmFrame;
    InfoPanel shame;
    //  Container container;
    ConzillaKit kit = ConzillaKit.getDefaultKit();
    //RDFModel thecontainer;

    public LinkToContentMapTool(MapController cont, String kind) {
        super(kind, EditMapManagerFactory.class.getName(), cont);
        this.kind = kind;
        this.mc = cont;
    }

    protected boolean updateEnabled() {
        Concept c;
        if (mapEvent.hitType != MapEvent.HIT_NONE
            && (c = mapObject.getConcept()) != null) {
            this.concept = c;
            Tracer.debug("Concept is:" + concept.toString());
            return true;
        }
        return false;
    }


    public void actionPerformed(ActionEvent e) {
        Component contentComponent;
        
        if (this.kind.equalsIgnoreCase(LINK_TO_CONTENT)) {
            contentComponent = ManageContentMapTool.createContentOnConceptDialog(concept);
        } else if (this.kind.equalsIgnoreCase(LINK_TO_CONTENT_IN_CONTEXT)) {
            contentComponent = ManageContentMapTool.createContentInContextDialog(
                    mapObject.getDrawerLayout().getConceptMap(), concept);
        } else {
            throw new RuntimeException("Error, neither content on concept or content on concept in context was choosen.");
        }
        
        //If canceled.
        if (contentComponent == null) {
            return;
        }
        //Now edit the content resource.
        Container cContainer =
            kit.getResourceStore().getContainerManager().getCurrentConceptContainer();
        ContentEditor.launchContentEditorInFrame(cContainer, contentComponent);
    }
    
    public boolean saveComponent(se.kth.cid.component.Resource resource) {
        try {
            //Clean up valuemodel in shame by forcing it to edit nothing.
            if (shame != null) {
                shame.finishEdit();
            }

            //Mark the model as edited, otherwise we can't save it.
            //Should not be done here, should be done before this function is called.
            resource.setEdited(true);
            //Now, save it!
            ConzillaKit.getDefaultKit().getResourceStore().getComponentManager().saveResource(resource);
        } catch (ComponentException e3) {
            e3.printStackTrace();
        }
        return true;
    }
}