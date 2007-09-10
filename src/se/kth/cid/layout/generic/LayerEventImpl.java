/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;
import se.kth.cid.layout.LayerEvent;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class LayerEventImpl implements LayerEvent
{
    int change;

    public LayerEventImpl(int change)
    {
	this.change = change;
    }
    public int getLayerChange()
    {
	return change;
    }
}
