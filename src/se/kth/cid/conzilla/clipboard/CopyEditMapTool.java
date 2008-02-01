/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.layers.MoveLayer;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.conzilla.map.MapObject;

/** 
 *  @author Matthias Palmer
 */
public class CopyEditMapTool extends CopyMapTool {
	
	Log log = LogFactory.getLog(CopyEditMapTool.class);
        
    public CopyEditMapTool(MapController cont, Clipboard clipboard) {
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
        Set dls = handleStore.getMarkedLayouts();
        log.debug("Number of marked dls: " + dls.size());
        if (!dls.isEmpty()) {
            clipboard.setDrawerLayouts(new ArrayList(dls));
        } else {
        	MoveLayer moveLayer = ((EditMapManager) controller.getManager()).moveLayer;
        	if (moveLayer.getHandledObject() != null) {
        		MapObject mo = moveLayer.getHandledObject().getMapObject();
        		if (mo != null && mo.getConcept() != null) {
        			clipboard.setDrawerLayout(mo.getDrawerLayout());
        			return;
        		}
        	}
            super.actionPerformed(e);
        }
    }
}