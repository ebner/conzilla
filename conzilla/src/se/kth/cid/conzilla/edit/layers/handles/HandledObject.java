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
import java.awt.*;
import java.util.*;

public abstract class HandledObject
{
  protected Vector handles;
  protected MapEvent mapeventold;
  protected boolean dragdirty;
  
  public HandledObject()
  {
    handles=new Vector();
    dragdirty=false;
  }
  public void paint(Graphics g)
  {
    Enumeration en=handles.elements();
    for (;en.hasMoreElements();)
      ((Handle) en.nextElement()).paint(g);
  }

  public void deSelectAll()
  {
    Enumeration en=handles.elements();
    for (;en.hasMoreElements();)
      ((Handle) en.nextElement()).setSelected(false);
  }
  
  public Handle chooseHandle(MapEvent m)
  {
    //    Tracer.debug("Searching for handles.");
    Handle ha;
    Enumeration en=handles.elements();
    for (;en.hasMoreElements();)
      {
	ha = (Handle) en.nextElement();
	if (ha.contains(m))
	  {
	    //	    Tracer.debug("Found handle.");
	    ha.setSelected(!ha.isSelected());
	    m.consume();
	    return ha;
	  }
      }
    return null;
  }
  
  public void drag(MapEvent m)
  {
    dragdirty=true;
    int x=m.mouseevent.getX()-mapeventold.mouseevent.getX();
    int y=m.mouseevent.getY()-mapeventold.mouseevent.getY();
    Enumeration en=handles.elements();
    for (;en.hasMoreElements();)
      ((Handle) en.nextElement()).drag(x,y);
    mapeventold=m;
    dragImpl(m);
  }
  protected void dragImpl(MapEvent m) {}
  
  //Warning, if you override this method and still use function drag above,
  //do remember to set mapeventold!!!
  public Point startDrag(MapEvent m)
  {
    dragdirty=false;
    mapeventold=m;
    return startDragImpl(m);
  }
  protected abstract Point startDragImpl(MapEvent m);
  
  public void stopDrag(MapEvent m)
  {
    mapeventold=m;
    stopDragImpl(m);
    if (dragdirty)
      endDrag(m);
  }
  protected void stopDragImpl(MapEvent m) {}
  protected void endDrag(MapEvent m) {}
  public void click(MapEvent m) {}
}
