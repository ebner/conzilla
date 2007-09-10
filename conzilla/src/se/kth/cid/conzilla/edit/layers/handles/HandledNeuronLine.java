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

public class HandledNeuronLine extends HandledLine
{
  LineTool linetool;
  public HandledNeuronLine()
    {
      linetool=null;
    }
  public HandledNeuronLine(MapEvent m,LineTool linetool)
  {
    super(m,linetool);
    this.linetool=linetool;
  }
  
  protected void loadFromModel()
  {
    Point points[]=mapevent.neuronstyle.getLine();
    for (int i=0; i< points.length;i++)
      handles.addElement(new DefaultHandle(points[i]));
    allhandles = new HandledLine.AllHandle(mapevent);
  }

  protected void saveToModel()
  {
    Enumeration en=handles.elements();
    Point points[]=new Point[handles.size()];
    for (int i=0;en.hasMoreElements();i++)
      points[i]=((DefaultHandle) en.nextElement()).getPosition();
    mapevent.neuronstyle.setLine(points);
    System.out.println("points="+points.toString());
  }
  public void click(MapEvent m)
  {
    if (!m.mouseevent.isShiftDown() && linetool.isActivated())
      {
	Handle ha=chooseHandle(m);
	if (ha==null )
	  handles.insertElementAt(new DefaultHandle(new Point(m.mouseevent.getX(),
							      m.mouseevent.getY())),m.linesegmenthit+1);
	else
	  if (handles.size()>2)
	    handles.removeElement(ha);
	saveToModel();
      }
    else super.click(m);
  }

}
