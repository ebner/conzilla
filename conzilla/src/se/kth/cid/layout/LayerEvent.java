/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface LayerEvent
{
    int ORDER_CHANGED = 0;
    int VISIBILITY_CHANGED = 1;
    int OBJECTSTYLE_ADDED = 2;
    int OBJECTSTYLE_REMOVED = 2;

    int getLayerChange();
}
