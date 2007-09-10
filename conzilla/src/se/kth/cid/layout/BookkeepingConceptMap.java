/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;

import se.kth.cid.component.EditEvent;

/** 
 *  @author Matthias Palmer
 *  @version $Revision$
 */

public interface BookkeepingConceptMap extends ContextMap {
    //    void clearDrawerLayoutCache();
    void removeResourceLayout(BookkeepingResourceLayout os);
    
    /** Fires an EditEvent to all listeners and marks the
     *  component as being edited.
     *
     *  @param e the event to fire.
     */
    void fireEditEvent(EditEvent e);

    /** Fires an EditEvent to all listeners without marking the component as being edited.
     *
     *  @param e the event to fire.
     */
    void fireEditEventNoEdit(EditEvent e);
}
