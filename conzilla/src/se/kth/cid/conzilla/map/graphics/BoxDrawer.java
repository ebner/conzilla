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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class BoxDrawer extends MapDrawer
{
  Rectangle bb;
  Rectangle hitBb;
  Rectangle2D innerbb;

  Shape drawnShape;

  Stroke stroke;
  boolean filled;
  float thickness;
  NeuronMapObject neuronMapObject;
  
  public BoxDrawer(NeuronMapObject neuronMapObject)
    {
      super(neuronMapObject.getDisplayer());
      this.neuronMapObject = neuronMapObject;
    }
  
  public void detach()
    {
    }

  public boolean getErrorState()
    {
      return neuronMapObject.getErrorState();
    }
  
  
  public void doPaint(Graphics2D g)
    {
      Stroke s = g.getStroke();
      if (neuronMapObject.getMark().isLineWidthModified())
	  g.setStroke(LineDrawer.makeStroke("continuous", thickness*neuronMapObject.getMark().lineWidth));
      else
	  g.setStroke(stroke);
      Color col=g.getColor();
      if(filled)
	  {
	      g.setColor(neuronMapObject.getMark().foregroundColor);
	      g.fill(drawnShape);
	  }
      else
	  {
	      g.setColor(neuronMapObject.getMark().backgroundColor);
	      g.fill(drawnShape);
	  }	  
      
      g.setColor(neuronMapObject.getMark().foregroundColor);
      g.draw(drawnShape);
      g.setColor(col);
      
      g.setStroke(s);
    }
  
  public Rectangle2D getInnerBoundingBox()
    {
      return innerbb;
    }
  
  public Rectangle getBoundingBox()
    {
      return bb;
    }
  
  
  public void update(NeuronMapObject neuronMapObject)
    {
      ConceptMap.BoundingBox bbox
	= neuronMapObject.getNeuronStyle().getBoundingBox();

      bb = new Rectangle(bbox.pos.x, bbox.pos.y,
			 bbox.dim.width, bbox.dim.height);
      

      hitBb = new Rectangle(bb);
      if (bb.width < 8)
	{
	  hitBb.width = 8;
	  hitBb.x -= (8 - bb.width)/2;
	}

      if (bb.height < 8)
	{
	  hitBb.height = 8;
	  hitBb.y -= (8 - bb.height)/2;
	}

      NeuronType nt = neuronMapObject.getNeuronType();

      filled = false;
      String borderType = "continuous";
      String type = "rectangle";
      thickness = 1;

      if(nt != null)
	{
	  NeuronType.BoxType boxType = nt.getBoxType();
	  type = boxType.type;
	  filled = boxType.filled;
	  borderType = boxType.borderType;
	  thickness = boxType.borderThickness;
	}

      stroke = LineDrawer.makeStroke(borderType, thickness);
      
      if (type.equalsIgnoreCase("rectangle"))
	{
	  drawnShape = new Rectangle2D.Double(bb.x, bb.y, bb.width, bb.height);
	  innerbb = (Rectangle2D) drawnShape;
	}
      else if(type.equalsIgnoreCase("roundrectangle"))
	{
	  //Bosse says: "German design..."
	  /* double f = 0.5;
	     int minDim = bb.width;
	     if(bb.height < minDim)
	       minDim = bb.height;
	       double corner = f*minDim; */
	  double corner = 12;
	  double inset = corner*(2 - Math.sqrt(2))/4;
	  
	  drawnShape = new RoundRectangle2D.Double(bb.x, bb.y, bb.width, bb.height, corner, corner);
	  
	  innerbb = new Rectangle2D.Double(bb.x + inset,
					   bb.y ,
					   bb.width - 2*inset,
					   bb.height);
	}
      else if (type.equalsIgnoreCase("diamond"))
	{
	  int halfx = (int)Math.round(bb.x + bb.width/2.0);
	  int halfy = (int)Math.round(bb.y + bb.height/2.0);
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(bb.x, halfy);
	  path.lineTo(halfx, bb.y);
	  path.lineTo(bb.x + bb.width, halfy);
	  path.lineTo(halfx, bb.y + bb.height);
	  path.closePath();

	  drawnShape = path;
	  innerbb = new Rectangle2D.Double(bb.x + bb.width/4.0,
					   bb.y + bb.height/4.0,
					   bb.width/2.0,
					   bb.height/2.0);
	}
      else if (type.equalsIgnoreCase("ellipse"))
	{
	  drawnShape = new Ellipse2D.Double(bb.x, bb.y, bb.width, bb.height);

	  double insetFactor = (2 - Math.sqrt(2))/4;
	  innerbb = new Rectangle2D.Double(bb.x + bb.width*insetFactor,
					   bb.y + bb.height*insetFactor,
					   bb.width*(1 - 2*insetFactor),
					   bb.height*(1 - 2*insetFactor));
	}
      else if (type.equalsIgnoreCase("flathexagon"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(bb.x+0.15f*bb.width, bb.y);
	  path.lineTo(bb.x+0.85f*bb.width, bb.y);
	  path.lineTo(bb.x+bb.width, bb.y+bb.height/2);
	  path.lineTo(bb.x+0.85f*bb.width, bb.y+bb.height);
	  path.lineTo(bb.x+0.15f*bb.width, bb.y+bb.height);
	  path.lineTo(bb.x, bb.y+bb.height/2);
	  path.closePath();

	  drawnShape = path;

	  innerbb = new Rectangle2D.Double(bb.x+0.15f*bb.width,
					   bb.y,
					   bb.width*0.7f,
					   bb.height);
	}
      else if (type.equalsIgnoreCase("upperfive"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(bb.x+0.15f*bb.width, bb.y);
	  path.lineTo(bb.x+0.85f*bb.width, bb.y);
	  path.lineTo(bb.x+bb.width, bb.y+bb.height);
	  path.lineTo(bb.x, bb.y+bb.height);
	  path.closePath();

	  drawnShape = path;

	  innerbb = new Rectangle2D.Double(bb.x+0.15f*bb.width,
					   bb.y,
					   bb.width*0.7f,
					   bb.height);
	}
      else if (type.equalsIgnoreCase("lowerfive"))
	{
	  GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
	  path.moveTo(bb.x, bb.y);
	  path.lineTo(bb.x+bb.width, bb.y);
	  path.lineTo(bb.x+0.85f*bb.width, bb.y+bb.height);
	  path.lineTo(bb.x+0.15f*bb.width, bb.y+bb.height);
	  path.closePath();

	  drawnShape = path;

	  innerbb = new Rectangle2D.Double(bb.x+0.15f*bb.width,
					   bb.y,
					   bb.width*0.7f,
					   bb.height);
	}
      else if (type.equalsIgnoreCase("invisible"))
	{
	  drawnShape = new GeneralPath();
	  innerbb = new Rectangle2D.Double(bb.x, bb.y, bb.width, bb.height);
	}
      else
	{
	  drawnShape = new Rectangle2D.Double(bb.x, bb.y, bb.width, bb.height);
	  innerbb = (Rectangle2D) drawnShape;
	}
    }
  
  public boolean didHit(MapEvent m)
    {
      return hitBb.contains(m.mapX, m.mapY);
    }
}
