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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;

public class HeadDrawer extends MapDrawer
{
  AxonMapObject axonMapObject;
  
  Point[] line;
  boolean filled;
  boolean clearFill;
  float   thickness;
  
  Stroke  stroke;
  Shape   drawnShape;
  
  public HeadDrawer(AxonMapObject axonMapObject)
    {
      super(axonMapObject.getNeuronMapObject().getDisplayer());
      this.axonMapObject = axonMapObject;
    }
  
  protected void detach()
    {
    }

  public boolean getErrorState()
    {
      return axonMapObject.getErrorReport() != null;
    }
  
  public Rectangle getBoundingBox()
    {
	return drawnShape.getBounds();
    }

  protected void doPaint(Graphics2D g)
    {
      Stroke s = g.getStroke();
      if (axonMapObject.getNeuronMapObject().getMark().isLineWidthModified())
	  g.setStroke(LineDrawer.makeStroke("continuous", thickness*
					    axonMapObject.getNeuronMapObject().getMark().lineWidth));
      else
	  g.setStroke(stroke);
      Color col = g.getColor();
      
      if(filled && clearFill)
	  {
	      g.setColor(axonMapObject.getNeuronMapObject().getMark().foregroundColor);
	      g.fill(drawnShape);
	  }
      else if(clearFill)
	{
	    g.setColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_BACKGROUND));
	    g.fill(drawnShape);
	    g.setColor(axonMapObject.getNeuronMapObject().getMark().foregroundColor);
	    g.draw(drawnShape);
	} 
      else
	  {
	      g.setColor(axonMapObject.getNeuronMapObject().getMark().foregroundColor);
	      g.draw(drawnShape);
	  }		
      g.setColor(col);
      g.setStroke(s);
    }
  
  protected void update(Point[] line)
    {
      this.line = line;
      
      AxonType rt = axonMapObject.getAxonType();

      clearFill   = true;
      filled      = true;
      String type = "ellipse";
      int width   = 3;
      int length  = 3;

      thickness = 1;
      
      if(rt != null)
	{
	  NeuronType.HeadType headType = rt.getHeadType();
	  filled      = headType.filled;
	  type        = headType.type;
	  width       = headType.width;
	  length      = headType.length;
	  thickness = rt.getLineType().thickness;
	}

      stroke = LineDrawer.makeStroke("continuous", thickness);

      
      if (line.length < 2 || type.equalsIgnoreCase("none"))
	{
	  drawnShape = new GeneralPath();
	  clearFill = false;
	  return;
	} 

      Point nTip = line[line.length - 2];
      Point tip  = line[line.length - 1];
      int xLen = tip.x - nTip.x;
      int yLen = tip.y - nTip.y;
      
      
      Shape rawShape = null;
      Point2D arrowTip = new Point2D.Double(1, 0.5);
      
      if(type.equalsIgnoreCase("varrow"))
	{
	  clearFill = false;
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
	  path.moveTo(0f, 0f);
	  path.lineTo(1f, 0.5f);
	  path.lineTo(0f, 1f);
	  path.lineTo(1f, 0.5f);
	  rawShape = path;
	}
      else if(type.equalsIgnoreCase("arrow"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
	  path.moveTo(0f, 0f);
	  path.lineTo(1f, 0.5f);
	  path.lineTo(0f, 1f);
	  path.closePath();
	  rawShape = path;
	}
      else if(type.equalsIgnoreCase("sharparrow"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(0, 0);
	  path.lineTo(1f, 0.5f);
	  path.lineTo(0f, 1f);
	  path.lineTo(0.25f, 0.5f);
	  path.closePath();
	  rawShape = path;
	}
      else if(type.equalsIgnoreCase("bluntarrow"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(0.25f, 0f);
	  path.lineTo(1f, 0.5f);
	  path.lineTo(0.25f, 1f);
	  path.lineTo(0f, 0.5f);
	  path.closePath();
	  rawShape = path;
	}
      else if(type.equalsIgnoreCase("diamond"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(0.5f, 0f);
	  path.lineTo(1f, 0.5f);
	  path.lineTo(0.5f, 1f);
	  path.lineTo(0f, 0.5f);
	  path.closePath();
	  rawShape = path;
	}
      else if(type.equalsIgnoreCase("ellipse"))
	{
	  rawShape = new Ellipse2D.Double(0, 0, 1, 1);
	}
      else
	{
	  rawShape = new Ellipse2D.Double(0, 0, 1, 1);
	}
      
      AffineTransform transform = AffineTransform.getTranslateInstance(tip.x, tip.y);
      transform.rotate(Math.atan2(yLen, xLen));
      transform.scale(length*3, width*3);
      transform.translate(-arrowTip.getX(), -arrowTip.getY());

      drawnShape = transform.createTransformedShape(rawShape);
    }
  
}
