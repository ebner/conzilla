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
 * TODO
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class InsertClipboardContentMapTool extends Tool {
	Clipboard clipboard;

	public InsertClipboardContentMapTool(MapController cont, Clipboard clipboard) {
		super("INSERT_CONTENT_FROM_CLIPBOARD", Clipboard.class.getName(), cont);
		this.clipboard = clipboard;
	}

	protected boolean updateEnabled() {
		if (mapEvent.hitType != MapEvent.HIT_NONE
				&& mapObject.getConcept() != null
				&& clipboard.getComponent() != null) {
			return true;
		}
		return false;
	}

	public void actionPerformed(ActionEvent e) {
	}
}
