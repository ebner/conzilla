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

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public abstract class LineHandlesStruct
{
    public Vector handles;

    public LineHandlesStruct()
    {
	handles=new Vector();
    }
    /*    public void insertHandleAt(Handle handle, int pos)
    {
	handles.insertElementAt(handle, pos);
    }

    public void removeHandle(Handle handle)
    {
	handles.remove(handle);
	}*/

    public DefaultHandle getHandle(int nr)
    {
	return (nr>=0 && nr <=handles.size()) ? (DefaultHandle) handles.elementAt(nr) : null;
    }
    
    public DefaultHandle getFirstHandle()
    {
	return handles.isEmpty() ? null : (DefaultHandle) handles.firstElement();
    }
    
    public DefaultHandle getLastHandle()
    {
	return handles.isEmpty() ? null : (DefaultHandle) handles.lastElement();
    }
    public void paint(Graphics2D g)
    {
	Iterator it=handles.iterator();
	while(it.hasNext())
	    {
		Handle handle=(Handle) it.next();
		handle.paint(g);
	    }
    }
}
