/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;

import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.Tool;

/**
 * This stub handles storeing (or cpoying if you prefer) components in the
 * (clipboard) library.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class CopyMapTool extends Tool {

    protected Clipboard clipboard;

    public CopyMapTool(MapController cont, Clipboard clipboard) {
        super(Clipboard.COPY, Clipboard.class.getName(), cont);
        this.clipboard = clipboard;
    }

    protected boolean updateEnabled() {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (mapObject != null && mapObject.getConcept() != null)
            clipboard.setDrawerLayout(mapObject.getDrawerLayout());
        else if (mapEvent != null && mapEvent.hitType == MapEvent.HIT_NONE)
            clipboard.setResource(mcontroller.getConceptMap());
    }
}