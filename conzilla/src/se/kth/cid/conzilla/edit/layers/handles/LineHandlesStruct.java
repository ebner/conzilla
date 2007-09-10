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
import se.kth.cid.util.*;
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

    protected void loadHandles(ConceptMap.Position [] points, int type)
    { 
	switch (type) {
	case AxonStyle.PATH_TYPE_STRAIGHT:
	    for (int i=0; i< points.length;i++)
		handles.addElement(new DefaultHandle(points[i]));
	    break;
	case AxonStyle.PATH_TYPE_QUAD:
	    for (int i=0; i< points.length;i++)
		handles.addElement(new DefaultHandle(points[i]));
	    break;	    
	case AxonStyle.PATH_TYPE_CURVE:
	    CornerHandle corner = new CornerHandle(points[0],null, points[1]);
	    handles.addElement(corner);
	    handles.addElement(corner.control2);
	    
	    for (int i=4; i< points.length;i+=3)
		{
		    corner = new CornerHandle(points[i-1], points[i-2], points[i]);
		    handles.addElement(corner.control1);
		    handles.addElement(corner);
		    handles.addElement(corner.control2);
		}
	    corner = new CornerHandle(points[points.length-1], points[points.length-2], null);
	    handles.addElement(corner.control1);
	    handles.addElement(corner);
	    break;
	}
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

    public abstract Collection getDraggers(boolean withEnds);

    protected Collection getDraggers(int type, boolean withEnds)
    {
	switch (type) {
	case AxonStyle.PATH_TYPE_STRAIGHT:
	case AxonStyle.PATH_TYPE_QUAD:
	    if (withEnds)
		return handles;
	    else
		return handles.subList(1,handles.size()-1);
	case AxonStyle.PATH_TYPE_CURVE:
	    Vector draggers = new Vector();
	    Iterator it = handles.iterator();
	    for (int i=0;it.hasNext();i++)
		{
		    if (i%3 == 0)
			draggers.add(it.next());
		    else
			it.next();
		}
	    if (withEnds)
		return draggers;
	    return draggers.subList(1, draggers.size()-1);
	}
	//should never be reached.
	return null;
    }
    
    public DefaultHandle getFirstHandle()
    {
	return handles.isEmpty() ? null : (DefaultHandle) handles.firstElement();
    }

    public DefaultHandle getSecondHandle()
    {
	return handles.size()<=1 ? null : (DefaultHandle) handles.elementAt(1);
    }
    
    public DefaultHandle getLastHandle()
    {
	return handles.isEmpty() ? null : (DefaultHandle) handles.lastElement();
    }

    public DefaultHandle getSecondLastHandle()
    {
	return handles.size()<=1 ? null : (DefaultHandle) handles.elementAt(handles.size()-2);
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
