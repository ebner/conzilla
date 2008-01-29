/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.layers.MoveLayer;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.GroupLayout;

/** 
 *  @author Matthias Palmer
 */
public class StoreEditMapTool extends StoreMapTool {
        
    public StoreEditMapTool(MapController cont, Clipboard clipboard) {
        super(cont, clipboard);
    }

    protected boolean updateEnabled() {
    	if (mapEvent != null) {
    		return true;
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
    	MoveLayer moveLayer = ((EditMapManager) controller.getManager()).moveLayer;
        Set dls = handleStore.getMarkedLayouts();
        ArrayList orderedDls = new ArrayList();
        System.out.println("number of marked dls "+dls.size());
        if (!dls.isEmpty()) {
            Vector visibleOrderedDrawMapObjects = controller.getConceptMap()
            	.getLayerManager().getDrawerLayouts(GroupLayout.IGNORE_VISIBILITY);
        	for (Iterator iter = visibleOrderedDrawMapObjects.iterator(); iter
					.hasNext();) {
				DrawerLayout dl = (DrawerLayout) iter.next();
				if (dls.contains(dl)) {
					orderedDls.add(dl);
				}
			}
        	
            clipboard.setDrawerLayouts(orderedDls);
        } else {
        	if (moveLayer.getHandledObject() != null) {
        		MapObject mo = moveLayer.getHandledObject().getMapObject();
        		if (mo != null && mo.getConcept() != null) {
        			clipboard.setMapObject(mo);
        			return;
        		}
        	}
            super.actionPerformed(e);
        }
    }
}