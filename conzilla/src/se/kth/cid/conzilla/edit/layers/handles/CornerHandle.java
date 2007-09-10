/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.util.Collection;
import java.util.Vector;

import se.kth.cid.layout.ContextMap;

/** The fixed positions of a cubic splines are cornerhandles.
 *  A cornerhandle can have one or two controlhandles,
 *  depending on if it is a end cornerhandle or not.
 *
 *  @author Matthias Palmer
 */
public class CornerHandle extends DefaultHandle
{
    public ControlHandle control1,control2;
    
    //Konstructor
    public CornerHandle(ContextMap.Position pos, ContextMap.Position pos1, ContextMap.Position pos2)
    {
	super(pos);

	if (pos1!=null)
	    this.control1 = new ControlHandle(pos1, this);
	if (pos2!=null)
	    this.control2 = new ControlHandle(pos2, this);
    }

    public Collection dragForced(int x, int y)
    {
	Collection col = new Vector();
	if (control1 != null)
	    col.addAll(control1.dragForced(x,y));
	if (control2 != null)
	    col.addAll(control2.dragForced(x,y));
	col.addAll(super.dragForced(x,y));
	return col;
    }
}
