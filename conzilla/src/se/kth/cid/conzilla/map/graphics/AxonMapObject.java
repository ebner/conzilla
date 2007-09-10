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
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.lang.*;
import java.util.*;


public class AxonMapObject implements MapObject
{  
  AxonStyle       axonStyle;
  NeuronMapObject neuronMapObject;

  Axon       axon;
  AxonType   axonType;
  
  LineDrawer lineDrawer;
  HeadDrawer headDrawer;

  public AxonMapObject(AxonStyle axonStyle, NeuronMapObject neuronMapObject)
    {
      this.axonStyle = axonStyle;
      this.neuronMapObject = neuronMapObject;

      if(neuronMapObject.getNeuron() != null)
	{
	  axon = neuronMapObject.getNeuron().getAxon(axonStyle.getAxonID());
	  //FIXME kontroll att ends stämmer...
	  if(axon != null && neuronMapObject.getNeuronType() != null)
	    axonType = neuronMapObject.getNeuronType().getAxonType(axon.getType());
	}
      lineDrawer = new LineDrawer();
      headDrawer = new HeadDrawer();
      
      update();
    }
  
  public void detach()
    {
      lineDrawer.detach();
      lineDrawer = null;
      
      headDrawer.detach();
      headDrawer = null;
    }
  
  
  public void update()
    {
      ConceptMap.Position[] styleLine = axonStyle.getLine();
      
      Point line[] = new Point[styleLine.length];
      for (int i = 0; i < line.length; i++)
	line[i] = new Point(styleLine[i].x, styleLine[i].y);
      
      // Notice, it's important to call headdrawer first since 
      // it changes the line to make room for the head.
      headDrawer.update(this, line);  
      lineDrawer.updateFromAxon(this, line);
    }
  
  public void paint(Graphics g, Color over)
    {
      headDrawer.paint(g, over);
      lineDrawer.paint(g, over);
    }  
  
  public int checkHit(MapEvent m)
    {
      //      if(headbBox.contains(x, y))
      //	return HIT_AXONLINE;
      if(lineDrawer.checkHit(m))
	{
	  m.mapObject = this;
	  m.hitType   = MapEvent.HIT_AXONLINE;
	  return m.hitType;
	}
      
      return MapEvent.HIT_NONE;
    }

  public NeuronMapObject getNeuronMapObject()
    {
      return neuronMapObject;
    }
  
  public Neuron getNeuron()
    {
      return neuronMapObject.getNeuron();
    }
  public NeuronStyle getNeuronStyle()
    {
      return neuronMapObject.getNeuronStyle();
    }
  
  public NeuronType getNeuronType()
    {
      return neuronMapObject.getNeuronType();
    }
  
  public Axon getAxon()
    {
      return axon;
    }
  
  public AxonType getAxonType()
    {
      return axonType;
    }
}

