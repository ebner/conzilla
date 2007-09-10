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

package se.kth.cid.conzilla.map.graphics;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class BoxDrawer
{
  public final static int   RECTANGLE  = 0;
  public final static int   RAISEDRECT = 1;
  public final static int   SUNKENRECT = 2;
  public final static int   ROUNDRECT  = 3;
  public final static int   FILLEDRECT = 4;
  public final static int   OVAL       = 5;
  public final static int   DIAMOND    = 6;
  public final static int   TYPE       = 7;
  public final static int   UNKNOWN    = 8;
  public final static int   INVISIBLE  = 9;
  public final static int   NONE       = 10;  

  private Rectangle bb;
  private Rectangle innerbb;
  private int       boxType;
  private Polygon   diamondPolygon;
  private Color     boxColor;
  
  public BoxDrawer()
  {
  }

  public void detach()
  {
    bb             = null;
    innerbb        = null;
    diamondPolygon = null;
  }
  
  public void paint(Graphics g, Color over)
    {
      if (boxType != NONE)
	{
	  if (over != null)
	    g.setColor(over);
	  else
	    g.setColor(boxColor);
	  
	  switch (boxType)
	    {
	    case RECTANGLE:
	      g.drawRect(bb.x, bb.y, bb.width, bb.height);
	      break;
	    case RAISEDRECT:
	      g.draw3DRect(bb.x, bb.y, bb.width, bb.height, true);
	      break;
	    case SUNKENRECT:
	      g.draw3DRect(bb.x, bb.y, bb.width, bb.height, false);
	      break;
	    case ROUNDRECT:  //the roundness, hardcoded ( ..., 10,10); ) or depending on size of rect?
	      g.drawRoundRect(bb.x, bb.y, bb.width, bb.height, 10, 10); 
	      break;
	    case FILLEDRECT:
	      g.fillRect(bb.x, bb.y, bb.width, bb.height);
	      break;	    
	    case OVAL:          
	      g.drawOval(bb.x, bb.y, bb.width, bb.height);
	      break;
	    case DIAMOND:
	      g.drawPolygon(diamondPolygon);
	      break;
	    case TYPE:
	      break;
	    case INVISIBLE:
	      break;
	    default:
	      g.drawRect(bb.x, bb.y, bb.width, bb.height);	  
	    }
	}  
    }
  
  private void calculateInnerBoundingBox()
    {
      if (boxType != NONE)
	{
	  switch (boxType)
	    {
	    case ROUNDRECT:  //the roundness, hardcoded ( ..., 10,10); ) or depending on size of rect?
	      innerbb = new Rectangle(bb.x + 3, bb.y + 3,
				      bb.width - 6, bb.height - 6);
	      break;
	    case OVAL:
	      innerbb = new Rectangle(bb.x + ((int) (bb.width*0.29/2)),
				      bb.y + ((int) (bb.height*0.29/2)),
				      ((int) (bb.width*0.7)),
				      ((int) (bb.height*0.7)));
	      break;
	    case DIAMOND:
	      innerbb = new Rectangle(bb.x + bb.width/4, bb.y + bb.height/4,
				      bb.width/2, bb.height/2);
	      break;
	    default:
	      innerbb = new Rectangle(bb);
	    }
	}
      else
	innerbb = null;
    }
  
  public Rectangle getInnerBoundingBox()
    {
      return innerbb;
    }
  
  public Rectangle getBoundingBox()
    {
      return bb;
    }
  
  public int getBoxType()
    {
      return boxType;
    }
  
  
  public void update(NeuronMapObject neuronMapObject)
    {
      ConceptMap.BoundingBox bbox
	= neuronMapObject.getNeuronStyle().getBoundingBox();

      bb = new Rectangle(bbox.pos.x, bbox.pos.y,
			 bbox.dim.width, bbox.dim.height);

      NeuronType nt = neuronMapObject.getNeuronType();
      String box;
      if(nt != null)
	{
	  boxColor = new Color(nt.getBoxColor());
	  box      = nt.getBoxType();
	}
      else
	{
	  boxColor = Color.red;
	  box      = "rectangle";
	}
      
      if (bb == null)
	boxType=NONE;
      else
	{
	  if (box.equalsIgnoreCase("rectangle"))
	    boxType = RECTANGLE;
	  else if (box.equalsIgnoreCase("roundrect"))
	    boxType = ROUNDRECT;
	  else if (box.equalsIgnoreCase("diamond"))
	    {
	      boxType = DIAMOND;
	      int halfx = bb.x + ((int) (((float) bb.width)/2.0));
	      int halfy = bb.y + ((int) (((float) bb.height)/2.0));
	      diamondPolygon = new Polygon();
	      diamondPolygon.addPoint(halfx, bb.y);
	      diamondPolygon.addPoint(bb.x + bb.width, halfy);
	      diamondPolygon.addPoint(halfx, bb.y + bb.height);
	      diamondPolygon.addPoint(bb.x, halfy);
	    }
	  else if (box.equalsIgnoreCase("oval"))
	    boxType = OVAL;
	  else if (box.equalsIgnoreCase("raisedrect"))
	    boxType = RAISEDRECT;
	  else if (box.equalsIgnoreCase("sunkenrect"))
	    boxType = SUNKENRECT;
	  else if (box.equalsIgnoreCase("filledrect"))
	    boxType = FILLEDRECT;
	  else if (box.equalsIgnoreCase("type"))
	    boxType = TYPE;
	  else if (box.equalsIgnoreCase("invisible"))
	    boxType = INVISIBLE;
	  else
	    boxType = UNKNOWN;
	}
      calculateInnerBoundingBox();
    }
  
  public boolean checkHit(MapEvent m)
    {
      return bb.contains(m.mouseEvent.getX(), m.mouseEvent.getY());
    }
}
