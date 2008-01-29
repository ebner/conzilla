/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

public interface UndoListener {
	/**
	 * If called, undo history have changed, check new state of 
	 * {@link UndoManager#canUndo()} or {@link UndoManager#canRedo()}.
	 */
	public void undoStateChanged();
}
