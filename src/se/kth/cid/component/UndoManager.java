/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.beans.PropertyChangeListener;

/**
 * Allows undo and redo of changed made to a single ContextMap and 
 * it's belonging concepts. Implementations are required to keep a change 
 * history and allow you undo/redo these changes by keeping track of a 
 * position in this history. If undo is called and new changes are recorded, 
 * the changes that where undone is then lost and cannot be redone anymore.
 * 
 * @author matthias
 */
public interface UndoManager {
	/**
	 * Add listener for changes in {@link #canUndo()} or {@link #canRedo()}.
	 */
	void addUndoListener(UndoListener undoListener);

	/**
	 * Remove listener for changes in {@link #canUndo()} or {@link #canRedo()}.
	 */
	void removeUndoListener(UndoListener undoListener);

	/**
	 * @return true if there is changes that can be undone relative to the
	 * current position in the change history.
	 */
	boolean canUndo();
	
	/**
	 * Makes the last change relative the current position in the change history undone. 
	 * If there is no such change, i.e. {@link #canUndo()}
	 * returns false, this method has no effect.
	 * 
	 */
	void undo();

	/**
	 * @return true if there is changes that can be redone relative the current
	 * position in the change history (requiring that changes has been undone 
	 * via {@link #undo()} already).
	 */
	boolean canRedo();
	
	/**
	 * Makes the last change relative the current position in the change history redone.
	 * If there is no such change, i.e. {@link #canRedo()}
	 * returns false, this method has no effect.
	 */	
	void redo();
	
	/**
	 * Starts recording changes that may be undone/redone together.
	 * Requires that {@link #endChange()} is called afterwards.
	 * A typical situation when this is used is when moving multiple concepts at once.
	 */
	void startChange();
	
	/**
	 * Ends the recording of changes that may be undone/redone together.
	 * Requires that {@link #startChange()} has been called before.
	 */
	void endChange();
	
	/**
	 * Calling this method separates previous changes from later changes so that they 
	 * are not undone/redone together.
	 * 
	 * This is typically useful when there is a range of small changes that cannot be
	 * automatically detected to be originating from different operations.
	 * This method can be thought as the opposite of the {@link #startChange()} and 
	 * {@link #endChange()} methods as they group changes together and this function 
	 * splits them.
	 *
	 */
	void makeChange();
	
	/**
	 * Forgets the last change made, making it irrevocabulary done.
	 */
	void forgetLastChange();
}
