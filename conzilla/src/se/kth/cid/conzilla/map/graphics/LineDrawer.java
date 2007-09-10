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
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;

public class LineDrawer extends MapDrawer
{
  Stroke stroke;
    int pathType=0;

  Vector path;
  GeneralPath polygon;
  Point[] line;
  float thickness;
  String type;

  AxonMapObject axonMapObject;
  NeuronMapObject neuronMapObject;
  
  LineDrawer(AxonMapObject axonMapObject)
    {
      super(axonMapObject.getNeuronMapObject().getDisplayer());
      this.axonMapObject = axonMapObject;
    }

  LineDrawer(NeuronMapObject neuronMapObject)
    {
      super(neuronMapObject.getDisplayer());
      this.neuronMapObject = neuronMapObject;
    }

  public boolean getErrorState()
    {
      if(axonMapObject != null)
	return axonMapObject.getErrorReport() != null;

      return neuronMapObject.getErrorState();
    }

  public static Stroke makeStroke(String type, float thickness)
    {
      float dist = (float) (5*Math.sqrt(thickness));

      //FIXME 2.5 is rather arbitrary...
      float width = thickness/2.5f;
      if(type.equalsIgnoreCase("continuous"))
	return new BasicStroke(width, BasicStroke.CAP_ROUND,
			       BasicStroke.JOIN_ROUND);

      if(type.equalsIgnoreCase("dotted"))
	{
	  float[] arr = {0, dist};
	  return new BasicStroke(width, BasicStroke.CAP_ROUND,
				 BasicStroke.JOIN_ROUND,
				 0f, arr, 0f);
	}
      
      if(type.equals("dashed"))
	{
	  float[] arr = {dist};
	  return new BasicStroke(width, BasicStroke.CAP_ROUND,
				 BasicStroke.JOIN_ROUND,
				 0, arr, 0);
	}
      
      if(type.equals("dashdot"))
	{
	  float[] arr = {dist, dist, 0, dist};
	  return new BasicStroke(width, BasicStroke.CAP_ROUND,
				 BasicStroke.JOIN_ROUND,
				 0, arr, 0);
	}
      
      if(type.equals("dashdotdot"))
	{
	  float[] arr = {dist, dist, 0, dist, 0, dist};
	  return new BasicStroke(width, BasicStroke.CAP_ROUND,
				 BasicStroke.JOIN_ROUND,
				 0, arr, 0);
	}

      if(type.equals("dashdotdotdot"))
	{
	  float[] arr = {dist, dist, 0, dist, 0, dist, 0, dist};
	  return new BasicStroke(width, BasicStroke.CAP_ROUND,
				 BasicStroke.JOIN_ROUND,
				 0, arr, 0);
	}
      
      return new BasicStroke(width, BasicStroke.CAP_ROUND,
			     BasicStroke.JOIN_ROUND);
    }

  public Collection getBoundingboxes()
    {
	Vector vect = new Vector();
	if (path!=null)
	    {
		Iterator segments = path.iterator();
		while (segments.hasNext())
		    vect.addElement(((Shape) segments.next()).getBounds());
	    }
	return vect;
    }
	
  protected void detach() 
    {
    }

  
  protected void doPaint(Graphics2D g)
    {
      if (path != null)
	{
	    Stroke s = g.getStroke();
	    
	    //Either an axon or a neuronline, i.e. either axonmapobject or neuronMapOject is non null.
	    Mark mark=axonMapObject!=null ? axonMapObject.getNeuronMapObject().getMark() : neuronMapObject.getMark();
	    if (mark.isLineWidthModified())
		g.setStroke(makeStroke(type, thickness*mark.lineWidth));
	    else
		g.setStroke(stroke);

	    Iterator segments = path.iterator();
	    while (segments.hasNext())
		g.draw(((Shape) segments.next()));
	    g.setStroke(s);
	}
    }

  protected void update(Point[] line)
    {
      this.line = line;
      type = "dotted";
      thickness = 1;
      
      //      Tracer.debug("0updateing stuff type is now "+type);
      if(axonMapObject != null)
	{
	    //    Tracer.debug("1updateing stuff type is now "+type);
	  pathType = axonMapObject.getAxonStyle().getPathType();
	  AxonType rt = axonMapObject.getAxonType();
	  if(rt != null)
	    {
		//	    Tracer.debug("2updateing stuff type is now "+type);
	      NeuronType.LineType lt = rt.getLineType();
	      type      = lt.type;
	      thickness = lt.thickness;
	    }
	}
      else
	{
	    //	    Tracer.debug("3updateing stuff type is now "+type);
	  pathType = neuronMapObject.getNeuronStyle().getPathType();
	  NeuronType nt = neuronMapObject.getNeuronType();
	  if(nt != null)
	    {
		//		Tracer.debug("4updateing stuff type is now "+type);
	      NeuronType.LineType lt = nt.getLineType();
	      type      = lt.type;
	      thickness = lt.thickness;
	    }
	}

      stroke = makeStroke(type, thickness);

      path = null;
      if(line != null && line.length >= 2)
	{
	    path = new Vector();
	    //	  polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, line.length);
	    //	  polygon.moveTo(line[0].x, line[0].y);

	  switch (pathType) {
	  case AxonStyle.PATH_TYPE_STRAIGHT:
	      for(int i = 0; i < line.length-1; i++)
		  path.add(new Line2D.Float(line[i].x, line[i].y, line[i+1].x, line[i+1].y));
	      break;
	  case AxonStyle.PATH_TYPE_QUAD:
	      for(int i = 0; i < line.length-1; i+=2)
		  path.add(new QuadCurve2D.Float(line[i].x, line[i].y, line[i+1].x, line[i+1].y, line[i+2].x, line[i+2].y));
		  //		  polygon.quadTo(line[i].x, line[i].y, line[i+1].x, line[i+1].y);	      
	      break;
	  case AxonStyle.PATH_TYPE_CURVE:
	      for(int i = 0; i < line.length-1; i+=3)
		  path.add(new CubicCurve2D.Float(line[i].x, line[i].y, line[i+1].x, line[i+1].y,
						line[i+2].x, line[i+2].y, line[i+3].x, line[i+3].y));
	    //		  polygon.curveTo(line[i].x, line[i].y, line[i+1].x, line[i+1].y, line[i+2].x, line[i+2].y);	      
	      break;
	  }
	}
    }
  
  protected boolean checkAndFillHit(MapEvent m)
    {
      if(path == null)
	return false;
      

      for (int i=0 ; i < path.size();i++)
	  if (((Shape) path.elementAt(i)).intersects(m.mapX-3, m.mapY-3, 6.1, 6.1))
	      {
		  switch (pathType) {
		  case AxonStyle.PATH_TYPE_STRAIGHT:
		  case AxonStyle.PATH_TYPE_QUAD:
		      break;
		  case AxonStyle.PATH_TYPE_CURVE:


		      /*		      FlatteningPathIterator fpi = new FlatteningPathIterator(((Shape) path.elementAt(i)).getPathIterator(null),3.0);
		      float [] farr = new float[6];
		      fpi.currentSegment(farr);
		      float startx =farr[0];
		      float starty =farr[1];
		      */
		      CubicCurve2D.Float curr = (CubicCurve2D.Float) path.elementAt(i);
		      GeneralPath gp = new GeneralPath();

		      gp.append(curr, false);
		      gp.curveTo((float) curr.getCtrlX2(),(float) curr.getCtrlY2(),
				 (float) curr.getCtrlX1(), (float)curr.getCtrlY1(),
				 (float) curr.getX1(),(float) curr.getY1());
		      if (!gp.intersects(m.mapX-2, m.mapY-2, 4.1, 4.1))
			  continue;
		  }
		  m.lineSegmentNumber = i;
		  return true;
	      }
      /*
      for(int i = 0; i < line.length - 1; i++)
	{
	  if(Line2D.ptSegDist(line[i].x, line[i].y,
			      line[i + 1].x, line[i + 1].y,
			      m.mapX, m.mapY) < 4.1)
	    {
	      m.lineSegmentNumber = i;
	      return true;
	    }
	    }*/
      return false;
    }
}
