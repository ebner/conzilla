/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package se.kth.cid.conzilla.edit.layers.handles;

import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;

import java.awt.*;
import java.util.*;

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
    public CornerHandle(ConceptMap.Position pos, ConceptMap.Position pos1, ConceptMap.Position pos2)
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
