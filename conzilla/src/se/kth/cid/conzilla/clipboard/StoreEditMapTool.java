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

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.GroupLayout;

/** 
 *  @author Matthias Palmer
 */
public class StoreEditMapTool extends StoreMapTool {
    
    HandleStore handleStore;
    
    public StoreEditMapTool(MapController cont, HandleStore handleStore, Clipboard clipboard) {
        super(cont, clipboard);
        this.handleStore = handleStore;
    }

    protected boolean updateEnabled() {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
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
        	
            clipboard.setDrawerLayous(orderedDls);
        } else {
            super.actionPerformed(e);
        }
    }
}
