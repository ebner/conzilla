/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

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
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.ResourceLayout;

/**
 * @author Matthias Palmer.
 */
public class Clipboard implements Extra, ClipboardOwner {

	public static final String COPY="COPY";
	public static final String PASTE="PASTE";
	
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
    
    public void setDrawerLayouts(List drawerLayouts) {
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
    	//Make sure there is only one copy tool per controller.
    	StoreEditMapTool semt = (StoreEditMapTool) c.get(COPY);
        if (semt == null) {
        	semt = new StoreEditMapTool(c, this);
        	semt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        	c.put(COPY, semt);
        }
    	//Make sure there is only one paste tool per controller.
        InsertClipboardConceptMapTool iccmt = (InsertClipboardConceptMapTool) c.get(PASTE);
        if (iccmt == null) {
        	iccmt = new InsertClipboardConceptMapTool(c, this);
        	iccmt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
        	c.put(PASTE, iccmt);
        }

        String menuName = menu.getName();
        
    	if (menuName.equals(BrowseMapManagerFactory.BROWSE_MENU)) {
            menu.addSeparator(800);
            menu.addTool(new StoreMapTool(c, this), 810);
        } else if (menuName.equals(EditMapManagerFactory.EDIT_CONCEPT_MENU)
                || menuName.equals(EditMapManagerFactory.EDIT_RELATION_MENU)) {
            menu.addSeparator(800);
            menu.addTool(semt, 805);
        } else if (menuName.equals(EditMapManagerFactory.EDIT_MAP_MENU) ||
        		menuName.equals(EditMapManagerFactory.EDIT_MENU)) {
            menu.addSeparator(800);
            menu.addTool(semt, 805);
            menu.addTool(iccmt, 810);
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