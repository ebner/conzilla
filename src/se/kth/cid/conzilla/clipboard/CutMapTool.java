/*  $Id: StoreEditMapTool.java 1199 2008-01-29 15:57:08Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.layers.MoveLayer;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.DrawerLayout;

/** 
 *  @author Matthias Palmer
 */
public class CutMapTool extends Tool {
    Clipboard clipboard;
	
    public CutMapTool(MapController cont, Clipboard clipboard) {
        super(Clipboard.CUT, Clipboard.class.getName(), cont);
        this.clipboard = clipboard;
    }

    protected boolean updateEnabled() {
    	if (mapEvent != null) {
    		return mapEvent.hitType != MapEvent.HIT_NONE;
    	} else {
        	HandleStore handleStore = ((EditMapManager) controller.getManager()).getHandleStore();
        	MoveLayer moveLayer = ((EditMapManager) controller.getManager()).moveLayer;
        	MapObject mo = moveLayer.getHandledObject() != null ? moveLayer.getHandledObject().getMapObject() : null;
        	Concept co = mo != null ? mo.getConcept() : null;
            return (!handleStore.getMarkedLayouts().isEmpty()) || co!= null;
    	}
    }

    public void actionPerformed(ActionEvent e) {
    	HandleStore handleStore = ((EditMapManager) controller.getManager()).getHandleStore();
        Set dls = handleStore.getMarkedLayouts();
        if (!dls.isEmpty()) {
            clipboard.setDrawerLayouts(new ArrayList(dls));
            clipboard.setClipIsCut(true);
            controller.getConceptMap().getComponentManager().getUndoManager().startChange();
            for (Iterator dlsIt = dls.iterator(); dlsIt.hasNext();) {
                ((DrawerLayout) dlsIt.next()).remove();
            }
            controller.getConceptMap().getComponentManager().getUndoManager().endChange();
        } else {
        	MoveLayer moveLayer = ((EditMapManager) controller.getManager()).moveLayer;
        	if (moveLayer.getHandledObject() != null) {
        		MapObject mo = moveLayer.getHandledObject().getMapObject();
        		if (mo != null && mo.getConcept() != null) {
        			clipboard.setDrawerLayout(mo.getDrawerLayout());
                    clipboard.setClipIsCut(true);
        			controller.getConceptMap().getComponentManager().getUndoManager().startChange();
        			mo.getDrawerLayout().remove();
                    controller.getConceptMap().getComponentManager().getUndoManager().endChange();        			
        			return;
        		}
        	}
        	if (mapObject != null && mapObject.getConcept() != null) {
        		clipboard.setDrawerLayout(mapObject.getDrawerLayout());
                clipboard.setClipIsCut(true);
    			controller.getConceptMap().getComponentManager().getUndoManager().startChange();
    			mapObject.getDrawerLayout().remove();
                controller.getConceptMap().getComponentManager().getUndoManager().endChange();        			
        	}
        }
    }
}