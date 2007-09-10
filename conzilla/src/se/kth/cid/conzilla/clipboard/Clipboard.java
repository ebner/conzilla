/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.List;

import se.kth.cid.component.Resource;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.content.ContentMenu;
import se.kth.cid.conzilla.content.ContentTool;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.EditMapManagerFactory;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.ResourceLayout;

/**
 * @author Matthias Palmer.
 */
public class Clipboard implements Extra, ClipboardOwner {

    private Resource component;

    private MapObject mapObject;
    
    private List drawerLayouts;
    
    public Clipboard() {
    }

    public void lostOwnership(java.awt.datatransfer.Clipboard clipboard,
            Transferable contents) {
    }
    
    public void clearClipBoard() {
        component = null;
        mapObject = null;
        drawerLayouts = null;
    }
    
    public void setDrawerLayous(List drawerLayouts) {
        clearClipBoard();
        this.drawerLayouts = drawerLayouts;
    }
    
    public void setMapObject(MapObject mo) {
        clearClipBoard();
        if (mo.getConcept() == null) {
            return;
        }
        this.mapObject = mo;
    }

    public void setComponent(Resource comp) {
        clearClipBoard();
        component = comp;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(component.getURI()), this);
    }

    public MapObject getMapObject() {
        return mapObject;
    }
    
    public List getDrawerLayouts() {
        return drawerLayouts;
    }

    public Resource getComponent() {
        return component;
    }

    public ContextMap getConceptMap() {
        return component instanceof ContextMap ? (ContextMap) component : null;
    }

    public Concept getConcept() {
        return mapObject != null ? mapObject.getConcept()
                : component instanceof Concept ? (Concept) component : null;
    }

    public ResourceLayout getResourceLayout() {
        return mapObject != null ? mapObject.getDrawerLayout()
                : component instanceof ResourceLayout ? (ResourceLayout) component
                        : null;
    }

    public boolean initExtra(ConzillaKit kit) {
        return true;
    }

    public String getName() {
        return "ClipBoard";
    }

    public void extendMenu(ToolsMenu menu, MapController c) {
        if (menu.getName().equals(BrowseMapManagerFactory.BROWSE_MENU)) {
            ((MapToolsMenu) menu).addSeparator(800);
            ((MapToolsMenu) menu)
                    .addMapMenuItem(new StoreMapTool(c, this), 810);
        }
        /*
         * else
         * if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_CONTENT)) {
         * ((MapToolsMenu) menu).addMapMenuItem(new
         * InsertClipboardContentMapTool(c), 200); }
         */
        else if (menu.getName().equals(EditMapManagerFactory.EDIT_CONCEPT_MENU)
                || menu.getName()
                        .equals(EditMapManagerFactory.EDIT_RELATION_MENU)) {
            ((MapToolsMenu) menu).addSeparator(800);
            ((MapToolsMenu) menu).addMapMenuItem(
                    new StoreEditMapTool(c, ((EditMapManager) c.getManager())
                            .getHandleStore(), this), 805);
        } else if (menu.getName().equals(EditMapManagerFactory.EDIT_MAP_MENU)) {
            
            ((MapToolsMenu) menu).addSeparator(800);
            ((MapToolsMenu) menu).addMapMenuItem(
                    new StoreEditMapTool(c, ((EditMapManager) c.getManager()).
                            getHandleStore(), this), 805);
            ((MapToolsMenu) menu).addMapMenuItem(
                    new InsertClipboardConceptMapTool(c, (EditMapManager) c
                            .getManager(), this), 810);
        } else if (menu.getName().equals(ContentMenu.CONTENT_MENU)) {
            final ContentMenu cm = (ContentMenu) menu;
            final MapController mc = c;

            cm.addTool(new ContentTool("COPY", Clipboard.class.getName()) {
                public void actionPerformed(ActionEvent e) {
                    Resource comp = mc.getContentSelector().getContent(
                            contentIndex);
                    setComponent(comp);
                }
            }, 400);
        }
    }

    public void addExtraFeatures(MapController c) {
    }

    public void refreshExtra() {
    }

    public boolean saveExtra() {
        return true;
    }

    public void exitExtra() {
    }
}