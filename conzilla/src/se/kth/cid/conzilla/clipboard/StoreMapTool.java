/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;

import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;

/**
 * This stub handles storeing (or cpoying if you prefer) components in the
 * (clipboard) library.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class StoreMapTool extends ActionMapMenuTool {

    protected Clipboard clipboard;

    public StoreMapTool(MapController cont, Clipboard clipboard) {
        super("COPY", Clipboard.class.getName(), cont);
        this.clipboard = clipboard;
    }

    protected boolean updateEnabled() {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (mapObject != null && mapObject.getConcept() != null)
            clipboard.setMapObject(mapObject);
        else if (mapEvent.hitType == MapEvent.HIT_NONE)
            clipboard.setComponent(controller.getConceptMap());
    }
}