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
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HandledLine extends HandledObject
{
  public class AllHandle implements Handle
  {
    public int radius=0;
    protected boolean selected;
    MapEvent mapevent;
    
    public AllHandle(MapEvent m)
    {
      this.mapevent=m;
      selected=false;
    }
    public void setRadius(int radius) {this.radius=radius;}
    public int getRadius() {return radius;}
    public Point getPosition() {return new Point(0,0);}
    public boolean contains(MapEvent m)
    {
      if (this.mapevent.getHitObject()==m.getHitObject())
	return true;
      else
	return false;
    }
    public Point getOffset(MapEvent m)
    {return null;}
    public void paint(Graphics g)
    {}
    public void drag(int x, int y)
    {
    Enumeration en = handles.elements();
    for (;en.hasMoreElements();)
      ((DefaultHandle) en.nextElement()).dragForced(x,y);
    }
    public boolean isSelected() {return selected;}
    public void setSelected(boolean selected) {this.selected=selected;}
  }

  protected boolean singleselection;
  protected MapEvent mapevent;
  protected AllHandle allhandles;

  protected LineTool linetool;

  public HandledLine()
  {
    super();
    linetool=null;
  }
  
  public HandledLine(MapEvent m, LineTool linetool)
  {
    super();
    mapevent=m;
    this.linetool=linetool;
    setSingleSelection(true);
    loadFromModel();
  }
  
  protected void loadFromModel()
  {
    Point points[]=mapevent.rolestyle.getLine();
    for (int i=0; i< points.length;i++)
      handles.addElement(new DefaultHandle(points[i]));
    allhandles = new AllHandle(mapevent);
  }

  public void setSingleSelection(boolean single)
  {
    singleselection=single;
  }
  
  public Handle chooseHandle(MapEvent m)
  {
    if (singleselection && !m.mouseevent.isShiftDown())
      deSelectAll();
    return super.chooseHandle(m);
  }
  
  protected Point startDragImpl(MapEvent m)
  {
    Tracer.debug("HandledLine: startDrag");
    if (!m.mouseevent.isShiftDown())
      {
	Handle ha=chooseHandle(m);
	if (ha==null) {
	  if ( allhandles.contains(m))
	    allhandles.setSelected(true);
	}
	else
	  return ha.getOffset(m);
      }
    return null;
  }

  public void drag(MapEvent m)
  {
    if (allhandles.isSelected())
      {
	dragdirty=true;
	int x=m.mouseevent.getX()-mapeventold.mouseevent.getX();
	int y=m.mouseevent.getY()-mapeventold.mouseevent.getY();
	allhandles.drag(x,y);
	mapeventold=m;    
      }
    else
      super.drag(m);
  }
  
  public void stopDragImpl(MapEvent m)
  {
    allhandles.setSelected(false);
    if (singleselection && !m.mouseevent.isShiftDown())
      deSelectAll();
    Tracer.debug("StopDrag.");
  }
  
  protected void endDrag(MapEvent m)
  {
    Tracer.debug("Enddrag");
    saveToModel();
  }
  
  protected void saveToModel()
  {
    Enumeration en=handles.elements();
    Point points[]=new Point[handles.size()];
    for (int i=0;en.hasMoreElements();i++)
      {
	points[i]=((DefaultHandle) en.nextElement()).getPosition();
      }
    mapevent.rolestyle.setLine(points);
    System.out.println("points="+points.toString());
  }
  
  public void click(MapEvent m)
  {
    Handle ha=chooseHandle(m);
    if (!m.mouseevent.isShiftDown())
      if (linetool.isActivated())
	{
	  if (ha==null )
	    handles.insertElementAt(new DefaultHandle(new Point(m.mouseevent.getX(),
								m.mouseevent.getY())),m.linesegmenthit+1);
	  else
	    if (handles.size()>2)
	      handles.removeElement(ha);
	  saveToModel();
	}
      else
	if (ha==null &&  allhandles.contains(m))
	  {
	    Enumeration en=handles.elements();
	    for (;en.hasMoreElements();)
	      {
		Handle handle=(Handle) en.nextElement();
		handle.setSelected(!handle.isSelected());
	      }
	  }
  }
}
