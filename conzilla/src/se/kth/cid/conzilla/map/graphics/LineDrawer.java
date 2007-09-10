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
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.lang.*;
import java.util.*;

public class LineDrawer
{
  public final static int   NONE=0;
  public final static int   UNKNOWN=1;
  public final static int   CONTINUOUS=2;
  public final static int   DOTTED=3;
  public final static int   DASHED=4;

  public int linetype;
  public Color linecolor;
  public int thick, thickhalf;
  public int X[],Y[];
  public Point line[];
  public String linetypestr;


  double distLimit = 4.1;
  int linesegRectAdd = 4;

  Rectangle[] linebBoxes;  
  double[][] ortovectors;
  
  public LineDrawer()
    {}

  public void detach() 
  {
    X=null;
    Y=null;
    line=null;
  }
  public void paint(Graphics g,Color over)
    {
      if (linetype != NONE)
	{
	  if (over==null)
	      g.setColor(linecolor);
	  else
	      g.setColor(over);
	  switch (linetype)
	    {
	    case CONTINUOUS:
	      g.drawPolyline(X,Y,X.length);
	      break;
	    case DOTTED:
	      for (int i=0;i<X.length;i++)
		g.fillRect(X[i],Y[i],thick, thick);
	      break;
	    case DASHED:
		for (int i=0;i<X.length-1;)
		  {
		    g.drawLine(X[i],Y[i],X[i+1],Y[i+1]);
		    i+=2;
		  }
	      break;
	    default:
	      g.drawPolyline(X,Y,X.length);
	      break;
	    }
	}
    }

  public void fixFromRoleStyle(RoleStyle rolestyle, Point[] line)
  {
    this.line=line;
    if (rolestyle.getRoleType()!=null)
      {
	linecolor=new Color(rolestyle.getRoleType().linecolor);
	linetypestr = rolestyle.getRoleType().linetype;
	thick = rolestyle.getRoleType().linethickness;
      }
    else
      {
	linecolor=Color.red;
	linetypestr = "dashed";
	thick = 1;	
      }
    fix();
  }

  public void fixFromNeuronStyle(NeuronStyle neuronstyle, Point[] line)
  {
    this.line=line;
    linecolor=new Color(neuronstyle.getNeuronType().getLineColor());
    linetypestr = neuronstyle.getNeuronType().getLineType();
    thick = neuronstyle.getNeuronType().getLineThickness();
    fix();
  }
  
  public  void fix()
    {
      if (line.length==0 || linetypestr.equalsIgnoreCase("none"))
	linetype=NONE;
      else
	{
	  thickhalf=(int) (((double) thick)/2.0);
	  
	  if (linetypestr.equalsIgnoreCase("continuous"))
	    {
	      linetype=CONTINUOUS;
	      doArraysOfPoints(0);
	    }
	  else if (linetypestr.equalsIgnoreCase("dotted"))
	    {
	      linetype=DOTTED;
	      doArraysOfPoints(5);
	    }
	  else if (linetypestr.equalsIgnoreCase("dashed"))
	    {
	      linetype=DASHED;
	      doArraysOfPoints(3);
	    }
	  else
	    {
	      linetype=CONTINUOUS;
	      doArraysOfPoints(0);   
	    }
	}
    }
  
  private void doArraysOfPoints(double breakup)
    {
      if (breakup!=0)
	{
	  Vector tpoints=new Vector();
	  Point current, diff;
	  double len;
	  for (int i=0; i<line.length - 1;i++)
	    {
	      current=line[i];
	      tpoints.addElement(new Point(current));
	      diff=new Point(line[i+1].x-current.x,line[i+1].y-current.y);
	      len=length(diff);
	      for (int j=1;((double) j*breakup)<len;j++)
		{
		  current=addTo(line[i],diff,(double) breakup*j,len);
		  tpoints.addElement(new Point(current));
		}
	    }
	  tpoints.addElement(line[line.length-1]);

	  X=new int[tpoints.size()];
	  Y=new int[tpoints.size()];
	  Enumeration en=tpoints.elements();
	  for (int i=0;en.hasMoreElements();i++)
	    {
	      Point p=(Point) en.nextElement();
	      X[i]=p.x-thickhalf;
	      Y[i]=p.y-thickhalf;
	    }
	}
      else
	{
	  
	  X=new int[line.length];
	  Y=new int[line.length];
	  for (int i=0;i<line.length;i++)
	    {
	      X[i]=line[i].x-thickhalf;
	      Y[i]=line[i].y-thickhalf;
	    }
	}
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



  public boolean didHit(MapEvent m)
  {
    if(linetype == NONE)
      return false;
    
    for(int i = 0; i < linebBoxes.length; i++)
      {
	if(linebBoxes[i].contains(m.mouseevent.getX(),m.mouseevent.getY()))
	  {
	    if(checkHit(m.mouseevent.getX(), m.mouseevent.getY(), i))
	      {
		m.linesegmenthit=i;
		return true;
	      }

	  }
      }
      return false;
    }
  
  private double prod(double x1, double y1, double x2, double y2)
    {
      return x1*x2 + y1*y2;
    }

  private boolean checkHit(int x, int y, int lineseg)
    {
      double dx = x - line[lineseg + 1].x;
      double dy = y - line[lineseg + 1].y;

      double dist = prod(dx, dy,
			 ortovectors[lineseg][0], ortovectors[lineseg][1]);

      if(Math.abs(dist) <= distLimit)
	{
	  if(lineseg == line.length - 2)
	    {
	      if(prod(dx, dy,
		      line[lineseg + 1].x - line[lineseg].x,
		      line[lineseg + 1].y - line[lineseg].y) > 0)
		return false;
	    }
	  if(lineseg == 0)
	    {
	      if(prod(x - line[0].x,
		      y - line[0].y,
		      line[1].x - line[0].x,
		      line[1].y - line[0].y) < 0)
		return false;
	    }
	  
	  return true;
	}
      return false;
    }

  
  private Rectangle makeBoundingRect(Point p1, Point p2)
    {
      int minx = p1.x;
      int maxx = p2.x;
      if(minx > maxx)
	{
	  maxx = minx;
	  minx = p2.x;
	}
      int miny = p1.y;
      int maxy = p2.y;
      if(miny > maxy)
	{
	  maxy = miny;
	  miny = p2.y;
	}
      return new Rectangle(minx - linesegRectAdd,
			   miny - linesegRectAdd,
			   maxx-minx + linesegRectAdd*2 + 1,
			   maxy-miny + linesegRectAdd*2 + 1);
    }

  private double[] makeOrtoVector(Point p1, Point p2)
    {
      double[] vec = new double[2];
      
      vec[0] = p2.y - p1.y;
      vec[1] = p1.x - p2.x;
      double len = Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1]);
      
      vec[0] /= len;
      vec[1] /= len;

      return vec;
    }

  public void fixHitInfo()
    {
      if(linetype == NONE)
	return;
      
      linebBoxes = new Rectangle[line.length - 1];
      ortovectors = new double[line.length - 1][];
      for(int i = 0; i < line.length - 1; i++)
	{
	  linebBoxes[i] = makeBoundingRect(line[i], line[i+1]);

	  ortovectors[i] = makeOrtoVector(line[i], line[i+1]);
	}
    }
}
