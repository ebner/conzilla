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
	if (polygon!=null)
	    vect.addElement(polygon.getBounds());
	return vect;
    }
	
  protected void detach() 
    {
    }

  
  protected void doPaint(Graphics2D g)
    {
      if (polygon != null)
	{
	    Stroke s = g.getStroke();
	    
	    //Either an axon or a neuronline, i.e. either axonmapobject or neuronMapOject is non null.
	    Mark mark=axonMapObject!=null ? axonMapObject.getNeuronMapObject().getMark() : neuronMapObject.getMark();
	    if (mark.isLineWidthModified())
		g.setStroke(makeStroke(type, thickness*mark.lineWidth));
	    else
		g.setStroke(stroke);
	  g.draw(polygon);
	  g.setStroke(s);
	}
    }

  protected void update(Point[] line)
    {
      this.line = line;
      type = "dotted";
      thickness = 1;
      
      if(axonMapObject != null)
	{
	  AxonType rt = axonMapObject.getAxonType();
	  if(rt != null)
	    {
	      NeuronType.LineType lt = rt.getLineType();
	      type      = lt.type;
	      thickness = lt.thickness;
	    }
	}
      else
	{
	  NeuronType nt = neuronMapObject.getNeuronType();
	  if(nt != null)
	    {
	      NeuronType.LineType lt = nt.getLineType();
	      type      = lt.type;
	      thickness = lt.thickness;
	    }
	}

      stroke = makeStroke(type, thickness);

      polygon = null;
      if(line != null && line.length >= 2)
	{
	  polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, line.length);
	  polygon.moveTo(line[0].x, line[0].y);
	  
	  for(int i = 1; i < line.length; i++)
	    polygon.lineTo(line[i].x, line[i].y);
	}
    }
  
  protected boolean checkAndFillHit(MapEvent m)
    {
      if(polygon == null)
	return false;
      
      for(int i = 0; i < line.length - 1; i++)
	{
	  if(Line2D.ptSegDist(line[i].x, line[i].y,
			      line[i + 1].x, line[i + 1].y,
			      m.mapX, m.mapY) < 4.1)
	    {
	      m.lineSegmentNumber = i;
	      return true;
	    }
	}
      return false;
    }
}
