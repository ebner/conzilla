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


package se.kth.cid.conzilla.edit;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import java.awt.*;
import java.util.*;

public class Layout
{
  public final static int SIMPLE_STYLE=0;
  public final static int PERPENDICULAR_STYLE=1;

  private static int paintstyle=SIMPLE_STYLE;
 public static boolean setLayoutStyle(int style)
    {
      if (style < SIMPLE_STYLE && style > PERPENDICULAR_STYLE)
	  return false;
      paintstyle=style;
      return true;
    }

 public static Point centerOfNeuron(NeuronStyle ns)
    {
      ConceptMap.Rectangle bb=ns.getBoundingBox();
      Enumeration roles=ns.getRoles().elements();
      if (bb!=null)
	{
	  Point p=bb.getLocation();
	  p.translate((int) bb.width/2,(int) bb.height/2);
	  return p;
	}
      else if(roles.hasMoreElements())
	  {
	    Point point=new Point(0,0);
	    int i=0;
	    for (;roles.hasMoreElements();i++)
	      {
	        Point temp[]=((RoleStyle) roles.nextElement()).getLine();
		if (temp.length!=0)
		  point.translate(temp[0].x,temp[0].y);
	      }
	    return new Point((int) point.x/i,(int) point.y/i);
	  }
      return null;
    }
  public static Point[] getRoleLineBetween(NeuronStyle ns1, NeuronStyle ns2)
    {
      switch (paintstyle)
       {
       case PERPENDICULAR_STYLE:
       case SIMPLE_STYLE:
       default:
	   Point line[] = new Point[2];
	   line[0]=centerOfNeuron(ns1);
	   line[1]=centerOfNeuron(ns2);
	   if (line[0]==null || line[1]==null)
	       return null;
	   return line;
       }
    } 
 public static Rectangle getBoundingBox(NeuronStyle ns,MapEvent m)
    {
	if (ns.getBoundingBox()==null)
	    return new Rectangle(m.mouseevent.getX()-30,m.mouseevent.getY()-15,60,30);
	else
	    return ns.getBoundingBox();
    }
  public static Point[] getNeuronLine(NeuronStyle ns,MapEvent m)
  {
    Tracer.debug("inside Layout, getNeuronLine");
	if (ns.getLine().length==0)
	  {
	    Point line[]=new Point[2];
	    line[0]=new Point(m.mouseevent.getX(),m.mouseevent.getY());
	    line[1]=centerOfNeuron(ns);
	    Tracer.debug(line[0].toString()+line[1].toString());
	    return line;
	  }
	else return ns.getLine();
    }
}
