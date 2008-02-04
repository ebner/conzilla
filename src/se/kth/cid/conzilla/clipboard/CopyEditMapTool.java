/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.util.ArrayList;
import java.util.Set;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.tool.DetectSelectionTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/** 
 *  @author Matthias Palmer
 */
public class CopyEditMapTool extends DetectSelectionTool {
        
    private Clipboard clipboard;

	public CopyEditMapTool(MapController cont, Clipboard clipboard) {
        super(Clipboard.COPY, Clipboard.class.getName(), cont, true);
        this.clipboard = clipboard;
    }

	@Override
	protected void handleMultipleSelection(Set dls) {
        clipboard.setDrawerLayouts(new ArrayList(dls));		
	}

	@Override
	protected void handleSingleSelection(DrawerLayout drawerLayout, Concept concept) {
		clipboard.setDrawerLayout(drawerLayout);
	}

	@Override
	protected void handleMap(ContextMap map) {
		 clipboard.setResource(controller.getConceptMap());
	}
}