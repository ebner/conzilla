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
import se.kth.cid.util.*;
import java.awt.*;
import java.lang.*;
import java.util.*;

public class HeadDrawer
{
  public final static int   NONE=0;
  public final static int   UNKNOWN=1;
  public final static int   ARROW=2;
  public final static int   WIDE_ARROW=3;
  public final static int   NARROW_ARROW=4;
  public final static int   RHOMB_ARROW=5;
  public final static int   CIRCLE_ARROW=6;
  public final static int   V_SHAPE_ARROW=7;

  /** Data retrieved from data-model.
   */
  public Point[] line;
  public boolean filled;
  public Color   headcolor;
  public String  headtypestr;
  public int     headsize;
  /** Help-data for paint algorithm, created by function fix.
   */
  public int     headtype;
  public Polygon headpolygon;
  public Point   circlepoint;
  public int     circlewidth;
  
  public HeadDrawer()
    {}
  public void detach()
  {
    line=null;
  }
  public void paint(Graphics g, Color over)
    {
      if (headtype != NONE)
	{
	  if (over==null)
	      g.setColor(headcolor);
	  else
	      g.setColor(over);
	  switch (headtype)
	    {
	    case UNKNOWN:
	      break;
	    case CIRCLE_ARROW:
	      if (filled)
		g.fillOval(circlepoint.x,circlepoint.y,circlewidth,circlewidth);
	      else
		g.drawOval(circlepoint.x,circlepoint.y,circlewidth,circlewidth);
	      break;
	    default:
	      if (filled)
		g.fillPolygon(headpolygon);
	      else
		g.drawPolygon(headpolygon);
	      
	    }
	}
    }
  
  public void fixFromRoleStyle(RoleStyle rolestyle, Point[] line)
  {
    this.line=line;
    if (rolestyle.getRoleType()!=null)
      {
	filled      = rolestyle.getRoleType().filled;
	headcolor   = new Color(rolestyle.getRoleType().linecolor);
	headtypestr = rolestyle.getRoleType().headtype;
	headsize    = rolestyle.getRoleType().headsize;	
      }
    else
      {
	filled      = false;
	headcolor   = Color.red;
	headtypestr = "arrow";
	headsize    = 12;	
      }
    fix();
  }
  
  protected void fix()
    {
      if (line.length < 2 || headtypestr.equalsIgnoreCase("none"))
	headtype=NONE;
      else
	{
	  double headlength;
	  Point tip=line[line.length-1];
	  Point ntip=line[line.length-2];
	  Point diff=new Point(ntip.x-tip.x,ntip.y-tip.y);
	  double len=length(diff);
	  if ( ((double) headsize) < (len-1.5) )
	    headlength=headsize;
	  else
	    headlength=(double) (len-1.5);
	  
	  double x=((double) diff.x)/len;
	  double y=((double) diff.y)/len;
	  if (headtypestr.equalsIgnoreCase("arrow"))
	    {
	      headtype=ARROW;
	      headpolygon=doArrow(tip,x,y,headlength,headlength,headlength/2);
	      line[line.length-1]=new Point(headpolygon.xpoints[2],headpolygon.ypoints[2]);
	    }
	  else if (headtypestr.equalsIgnoreCase("widearrow"))
	    {
	      headtype=WIDE_ARROW;
	      headpolygon=doArrow(tip,x,y,headlength,headlength,headlength);
	      line[line.length-1]=new Point(headpolygon.xpoints[2],headpolygon.ypoints[2]);
	    }	
	  else if (headtypestr.equalsIgnoreCase("narrowarrow"))
	    {
	      headtype=NARROW_ARROW;
	      headpolygon=doArrow(tip,x,y,headlength,headlength,headlength/4);
	      line[line.length-1]=new Point(headpolygon.xpoints[2],headpolygon.ypoints[2]);
	    }
	  else if (headtypestr.equalsIgnoreCase("rhombarrow"))
	    {
	      headtype=RHOMB_ARROW;
	      headpolygon=doArrow(tip,x,y,headlength/2,headlength,headlength/2);
	      line[line.length-1]=new Point(headpolygon.xpoints[2],headpolygon.ypoints[2]);
	    }
	  else if (headtypestr.equalsIgnoreCase("vshapearrow"))
	    {
	      headtype=V_SHAPE_ARROW;
	      headpolygon=doArrow(tip,x,y,headlength,headlength/2,headlength*2/3);
	      line[line.length-1]=new Point(headpolygon.xpoints[2],headpolygon.ypoints[2]);
	    }
	  else if (headtypestr.equalsIgnoreCase("circlearrow"))
	    {
	      headtype=CIRCLE_ARROW;
	      line[line.length-1]=addTo(tip,diff,headlength,len);
	      circlepoint=addTo(new Point(tip.x - (int)(headlength/2),
                                          tip.y - (int)(headlength/2)),
                                diff,headlength/2,len);
	      circlewidth=(int) headlength;
	    }
	  else
	    {
	      headtype=UNKNOWN;
	      headpolygon=doArrow(tip,x,y,headlength,headlength,headlength/3);
	      line[line.length-1]=new Point(headpolygon.xpoints[2],headpolygon.ypoints[2]);
	    }

	}
    }
  
  protected Polygon doArrow(Point start,double x, double y,double side, double length,double width)
    {
      Polygon pol=new Polygon();      
      pol.addPoint(start.x,start.y);
      pol.addPoint(start.x+ (int) Math.round(x*side+y*width),
		   start.y+ (int) Math.round(y*side-x*width));
      pol.addPoint(start.x+ (int) Math.round(x*length),
		   start.y+ (int) Math.round(y*length));
      pol.addPoint(start.x+ (int) Math.round(x*side-y*width),
		   start.y+ (int) Math.round(y*side+x*width));
      return pol;
    }
  private double length(Point p)
    {
      return Math.sqrt((double) p.x*p.x+p.y*p.y);
    }
  
  private Point addTo(Point start, Point diff,double distans, double len)
    {
      return new Point(start.x + (int) (((double) diff.x)*distans/len),
		       start.y + (int) (((double) diff.y)*distans/len));
    }
}
