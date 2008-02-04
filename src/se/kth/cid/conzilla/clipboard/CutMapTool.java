/*  $Id: StoreEditMapTool.java 1199 2008-01-29 15:57:08Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.tool.DetectSelectionTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/** 
 * Copies selection to clipboard, marks clipboard to be in cut mode, 
 * and then removes the layouts of the selected concepts.
 * 
 *  @author Matthias Palmer
 */
public class CutMapTool extends DetectSelectionTool {
    Clipboard clipboard;
	
    public CutMapTool(MapController cont, Clipboard clipboard) {
        super(Clipboard.CUT, Clipboard.class.getName(), cont, false);
        this.clipboard = clipboard;
    }

	@Override
	protected void handleMultipleSelection(Set dls) {
		clipboard.setDrawerLayouts(new ArrayList(dls));
		clipboard.setClipIsCut(true);
		controller.getConceptMap().getComponentManager().getUndoManager().startChange();
		for (Iterator dlsIt = dls.iterator(); dlsIt.hasNext();) {
			((DrawerLayout) dlsIt.next()).remove();
		}
		controller.getConceptMap().getComponentManager().getUndoManager().endChange();
	}

	@Override
	protected void handleSingleSelection(DrawerLayout drawerLayout, Concept concept) {
		clipboard.setDrawerLayout(drawerLayout);
		clipboard.setClipIsCut(true);
		controller.getConceptMap().getComponentManager().getUndoManager().startChange();
		drawerLayout.remove();
		controller.getConceptMap().getComponentManager().getUndoManager().endChange();        			
	}

	@Override
	protected void handleMap(ContextMap map) {
		//Not used.
	}
}