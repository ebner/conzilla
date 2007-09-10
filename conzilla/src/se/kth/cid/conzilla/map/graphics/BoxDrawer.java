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
  public final static int   RECTANGLE=0;
  public final static int   RAISEDRECT=1;
  public final static int   SUNKENRECT=2;
  public final static int   ROUNDRECT=3;
  public final static int   FILLEDRECT=4;
  public final static int   OVAL=5;
  public final static int   DIAMOND=6;
  public final static int   TYPE=7;
  public final static int   UNKNOWN=8;
  public final static int   INVISIBLE=9;
  public final static int   NONE=10;  

  private Rectangle bb;
  private Rectangle innerbb;
  private int boxtype;
  private Polygon diamondpolygon;
  private Color boxcolor;
  
  public BoxDrawer()
  {
  }

  public void detach()
  {
    bb=null;
    innerbb=null;
    diamondpolygon=null;
  }
  public void paint(Graphics g, Color over)
  {
    if (boxtype != NONE)
      {
	if (over!=null)
	    g.setColor(over);
	
	switch (boxtype)
	  {
	  case RECTANGLE:
	    g.drawRect(bb.x, bb.y, bb.width, bb.height);
	    break;
	  case RAISEDRECT:
	    g.draw3DRect(bb.x,bb.y,bb.width, bb.height, true);
	    break;
	  case SUNKENRECT:
	    g.draw3DRect(bb.x,bb.y,bb.width, bb.height, false);
	    break;
	  case ROUNDRECT:  //the roundness, hardcoded ( ..., 10,10); ) or depending on size of rect?
	    g.drawRoundRect(bb.x, bb.y, bb.width, bb.height, 10,10); 
	    break;
	  case FILLEDRECT:
	    g.fillRect(bb.x, bb.y, bb.width, bb.height);
	    break;	    
	  case OVAL:          
	    g.drawOval(bb.x,bb.y,bb.width, bb.height);
	    break;
	  case DIAMOND:
	    g.drawPolygon(diamondpolygon);
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
    if (boxtype != NONE)
      {
	switch (boxtype)
	  {
	  case ROUNDRECT:  //the roundness, hardcoded ( ..., 10,10); ) or depending on size of rect?
	    innerbb = new Rectangle(bb.x+3,bb.y+3,bb.width-6,bb.height-6);
	    break;
	  case OVAL:
	    innerbb = new Rectangle(bb.x+((int) (bb.width*0.29/2)),
				       bb.y+((int) (bb.height*0.29/2)),
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
      innerbb=null;
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
    return boxtype;
  }
  
  
  public void fixFromNeuronStyle(NeuronStyle neuronstyle)
  {
    this.bb=neuronstyle.getBoundingBox();
    if (neuronstyle.getNeuronType()!=null)
      {
	boxcolor=new Color(neuronstyle.getNeuronType().getBoxColor());
	if (bb==null)
	  boxtype=NONE;
	else
	  {
	    if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("rectangle"))
	      boxtype=RECTANGLE;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("roundrect"))
	      boxtype=ROUNDRECT;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("diamond"))
	      {
		boxtype=DIAMOND;
		int halfx=bb.x+((int) (((float) bb.width)/2.0));
		int halfy=bb.y+((int) (((float) bb.height)/2.0));
		diamondpolygon=new Polygon();
		diamondpolygon.addPoint(halfx,bb.y);
		diamondpolygon.addPoint(bb.x+bb.width,halfy);
		diamondpolygon.addPoint(halfx,bb.y+bb.height);
		diamondpolygon.addPoint(bb.x,halfy);
	      }
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("oval"))
	      boxtype=OVAL;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("raisedrect"))
	      boxtype=RAISEDRECT;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("sunkenrect"))
	      boxtype=SUNKENRECT;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("filledrect"))
	      boxtype=FILLEDRECT;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("type"))
	      boxtype=TYPE;
	    else if (neuronstyle.getNeuronType().getBox().equalsIgnoreCase("invisible"))
	      boxtype=INVISIBLE;
	    else boxtype=UNKNOWN;
	  }
      }
    else
      {
	boxcolor=Color.red;
	boxtype=RECTANGLE;
      }
    calculateInnerBoundingBox();
  }

  public boolean didHit(MapEvent m)
  {
    return bb.contains(m.mouseevent.getX(),m.mouseevent.getY());
  }
}
