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
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.lang.*;
import java.util.*;

public class HeadDrawer
{
  public final static int   NONE          = 0;
  public final static int   UNKNOWN       = 1;
  public final static int   ARROW         = 2;
  public final static int   WIDE_ARROW    = 3;
  public final static int   NARROW_ARROW  = 4;
  public final static int   RHOMB_ARROW   = 5;
  public final static int   CIRCLE_ARROW  = 6;
  public final static int   V_SHAPE_ARROW = 7;

  /** Data retrieved from data-model.
   */
  Point[] line;
  boolean filled;
  Color   headColor;
  String  headTypeStr;
  int     headSize;
  /** Help-data for paint algorithm, created by function update.
   */

  int     headType;
  Polygon headPolygon;
  Point   circlePoint;
  int     circleWidth;
  
  protected HeadDrawer()
    {}
  
  protected void detach()
    {
      line=null;
    }
  
  protected void paint(Graphics g, Color over)
    {
      if (headType != NONE)
	{
	  if (over == null)
	    g.setColor(headColor);
	  else
	    g.setColor(over);

	  switch (headType)
	    {
	    case UNKNOWN:
	      break;
	    case CIRCLE_ARROW:
	      if (filled)
		g.fillOval(circlePoint.x, circlePoint.y,
			   circleWidth, circleWidth);
	      else
		g.drawOval(circlePoint.x, circlePoint.y,
			   circleWidth, circleWidth);
	      break;
	    default:
	      if (filled)
		g.fillPolygon(headPolygon);
	      else
		g.drawPolygon(headPolygon);
	      
	    }
	}
    }
  
  protected void update(AxonMapObject axonMapObject, Point[] line)
  {
    this.line = line;

    AxonType rt = axonMapObject.getAxonType();

    if(rt != null)
      {
	filled      = rt.getHeadFilled();
	headColor   = new Color(rt.getLineColor());
	headTypeStr = rt.getHeadType();
	headSize    = rt.getHeadSize();	
      }
    else
      {
	filled      = true;
	headColor   = Color.red;
	headTypeStr = "circlearrow";
	headSize    = 10;	
      }
    
    update();
  }
  
  void update()
    {
      if (line.length < 2 || headTypeStr.equalsIgnoreCase("none"))
	headType = NONE;
      else
	{
	  double headLength;
	  Point tip  = line[line.length-1];
	  Point ntip = line[line.length-2];
	  Point diff = new Point(ntip.x - tip.x, ntip.y - tip.y);
	  double len = length(diff);
	  if ( ((double) headSize) < (len - 1.5) )
	    headLength = headSize;
	  else
	    headLength = (double) (len - 1.5);
	  
	  double x = ((double) diff.x)/len;
	  double y = ((double) diff.y)/len;
	  if (headTypeStr.equalsIgnoreCase("arrow"))
	    {
	      headType = ARROW;
	      headPolygon = doArrow(tip, x, y, headLength,
				    headLength, headLength/2);
	      line[line.length - 1] =
		new Point(headPolygon.xpoints[2], headPolygon.ypoints[2]);
	    }
	  else if (headTypeStr.equalsIgnoreCase("widearrow"))
	    {
	      headType = WIDE_ARROW;
	      headPolygon = doArrow(tip, x, y, headLength,
				    headLength, headLength);
	      line[line.length - 1] =
		new Point(headPolygon.xpoints[2], headPolygon.ypoints[2]);
	    }	
	  else if (headTypeStr.equalsIgnoreCase("narrowarrow"))
	    {
	      headType = NARROW_ARROW;
	      headPolygon = doArrow(tip, x, y, headLength,
				    headLength, headLength/4);
	      line[line.length - 1] =
		new Point(headPolygon.xpoints[2], headPolygon.ypoints[2]);
	    }
	  else if (headTypeStr.equalsIgnoreCase("rhombarrow"))
	    {
	      headType = RHOMB_ARROW;
	      headPolygon = doArrow(tip, x, y, headLength/2,
				    headLength, headLength/2);
	      line[line.length - 1] =
		new Point(headPolygon.xpoints[2], headPolygon.ypoints[2]);
	    }
	  else if (headTypeStr.equalsIgnoreCase("vshapearrow"))
	    {
	      headType = V_SHAPE_ARROW;
	      headPolygon = doArrow(tip, x, y, headLength,
				    headLength/2, headLength*2/3);
	      line[line.length - 1] =
		new Point(headPolygon.xpoints[2], headPolygon.ypoints[2]);
	    }
	  else if (headTypeStr.equalsIgnoreCase("circlearrow"))
	    {
	      headType = CIRCLE_ARROW;
	      line[line.length - 1] = addTo(tip, diff, headLength, len);
	      circlePoint = addTo(new Point(tip.x - (int)(headLength/2),
					    tip.y - (int)(headLength/2)),
				  diff, headLength/2,len);
	      circleWidth = (int) headLength;
	    }
	  else
	    {
	      headType = UNKNOWN;
	      headPolygon = doArrow(tip, x, y, headLength,
				    headLength, headLength/3);
	      line[line.length - 1] =
		new Point(headPolygon.xpoints[2], headPolygon.ypoints[2]);
	    } 
	}
    }
  
  Polygon doArrow(Point start, double x, double y,
		  double side, double length, double width)
    {
      Polygon pol = new Polygon();      
      pol.addPoint(start.x, start.y);
      pol.addPoint(start.x + (int) Math.round(x*side + y*width),
		   start.y + (int) Math.round(y*side - x*width));
      pol.addPoint(start.x + (int) Math.round(x*length),
		   start.y + (int) Math.round(y*length));
      pol.addPoint(start.x + (int) Math.round(x*side - y*width),
		   start.y + (int) Math.round(y*side + x*width));
      return pol;
    }
  
  double length(Point p)
    {
      return Math.sqrt((double) p.x*p.x + p.y*p.y);
    }
  
  private Point addTo(Point start, Point diff,double distans, double len)
    {
      return new Point(start.x + (int) (((double) diff.x)*distans/len),
		       start.y + (int) (((double) diff.y)*distans/len));
    }
}
