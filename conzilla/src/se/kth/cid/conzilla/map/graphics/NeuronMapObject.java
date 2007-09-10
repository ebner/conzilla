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
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class NeuronMapObject implements EditListener, MapObject
{
  public final static int   RECTANGLE  = 0;
  public final static int   RAISEDRECT = 1;
  public final static int   SUNKENRECT = 2;
  public final static int   ROUNDRECT  = 3;
  public final static int   OVAL       = 4;
  public final static int   DIAMOND    = 5;
  public final static int   UNKNOWN    = 6;
  public final static int   INVISIBLE  = 7;
  public final static int   NONE       = 8;  

  protected NeuronStyle  neuronStyle;
  protected Neuron       neuron;
  protected NeuronType   neuronType;

  Hashtable    axonMapObjects;
  LineDrawer   lineDrawer;
  BoxDrawer    boxDrawer;
  TitleDrawer  titleDrawer;
  DataDrawer   dataDrawer;

  Color        mark;

  MapDisplayer displayer;
  
  public NeuronMapObject(NeuronStyle neuronStyle, Neuron neuron, NeuronType neuronType, MapDisplayer displayer, MapDisplayer.MapComponentDrawer parent)
    {
      this.neuronStyle = neuronStyle;
      this.neuron      = neuron;
      this.neuronType  = neuronType;
      this.displayer   = displayer;
      
      lineDrawer  = new LineDrawer();
      boxDrawer   = new BoxDrawer();
      titleDrawer = new TitleDrawer(this, parent);
      dataDrawer  = new DataDrawer(this, parent);

      if(neuron != null)
	neuron.addEditListener(this);
      if(neuronType != null)
	neuronType.addEditListener(this);

      axonMapObjects  = new Hashtable();
      AxonStyle[] axonStyles = neuronStyle.getAxons();
      
      for(int i = 0; i < axonStyles.length; i++)
	axonMapObjects.put(axonStyles[i], new AxonMapObject(axonStyles[i], this));
      
      update();
    }

  public void detach()
    {
      neuron.removeEditListener(this);
      neuronType.removeEditListener(this);
      detachAxonMapObjects();

      lineDrawer.detach();
      lineDrawer = null;

      boxDrawer.detach();
      boxDrawer = null;

      titleDrawer.detach();
      titleDrawer = null;

      dataDrawer.detach();
      dataDrawer = null;
    }
  
  public void detachAxonMapObjects()
    {
      Iterator axons = axonMapObjects.values().iterator();
      while(axons.hasNext())
	((AxonMapObject) axons.next()).detach();
      axonMapObjects = null;
    }

  
  public void update()
    {
      updateAxonMapObjects();
      updateSelf();
    }
  
  public void updateAxonMapObjects(EditEvent e)
    {
      if (e.getEditType() == AxonStyle.LINE_EDITED)
	((AxonMapObject) axonMapObjects.get((AxonStyle) e.getEditedObject())).update();
    }
  
  public void updateAxonMapObjects()
    {    
      Iterator axons = axonMapObjects.values().iterator();
      while(axons.hasNext())
	((AxonMapObject) axons.next()).update();    
    }
  
  public void updateSelf(EditEvent e)
  {
    updateSelf();
    /* switch (e.getEditType())
      {
      case BOUNDINGBOX_EDITED:
	break;
      case TITLE_EDITED:
	break;
      case DATATAG_ADDED:
	break;
      case DATATAG_REMOVED:
	break;
      case LINE_EDITED:
	break;
      case DETAILEDMAP_EDITED:
	break;
      }*/
  }
  
  public void updateSelf()
  {
    ConceptMap.Position[] styleLine = neuronStyle.getLine();
    
    Point line[] = new Point[styleLine.length];
    for (int i = 0; i < line.length; i++)
      line[i] = new Point(styleLine[i].x, styleLine[i].y);


    lineDrawer.updateFromNeuron(this, line);
    boxDrawer.update(this);
    
    //Notice, it's important to update titleDrawer before dataDrawer
    //since data is placed depending on the title's height.
    titleDrawer.update(boxDrawer.getInnerBoundingBox(),
				      boxDrawer.getBoxType());
    dataDrawer.update(titleDrawer.getFreeSpace());
  }

  public void prepareEdit(boolean editable)
    { 
      if(editable)
	{
	  if (!neuronStyle.getConceptMap().isEditable())
	    return;
	  
	  titleDrawer.setEditable(true);
	}
      else
	titleDrawer.setEditable(false);
    }  
  
  public void paint(Graphics g)
    {
      Color ctemp = g.getColor();

      Iterator axons = axonMapObjects.values().iterator();

      while(axons.hasNext()) 
	((AxonMapObject) axons.next()).paint(g, mark);
      
      lineDrawer.paint(g, mark);
      boxDrawer.paint(g, mark);
      if (boxDrawer.getBoxType() != BoxDrawer.NONE)
	{
	  titleDrawer.paint(g, mark);
	}
      g.setColor(ctemp);
    }
  
  public int checkHit(int x, int y, MapEvent m)
    {
      Iterator axons = axonMapObjects.values().iterator();
      while(axons.hasNext())
	{
	  AxonMapObject rmo = (AxonMapObject) axons.next();
	  if(rmo.checkHit(m) != MapEvent.HIT_NONE)
	    return m.hitType;
	}
      
      if(boxDrawer.getBoxType() != BoxDrawer.NONE && boxDrawer.checkHit(m))
	{
	  m.mapObject = this;
//	    if (titleDrawer.didHit(m))
//	      {
//		m.hit=MapEvent.HIT_TITLE;
//		return m.hit; 
//	      }
//	    if (datadrawer.didHit(m))
//	      {
//		m.hit=MapEvent.HIT_DATA;
//		return m.hit; 
//	      }
	  m.hitType = MapEvent.HIT_BOX;
	  return m.hitType;
	}

      if(lineDrawer.checkHit(m))
	{
	  m.mapObject   = this;
	  m.hitType     = MapEvent.HIT_BOXLINE;
	  return m.hitType;
	}
      return MapEvent.HIT_NONE;
    }

  public void componentEdited(EditEvent e)
    {
      // It is neccessary to dinstinguish from for
      // example metadata editing and such
      //
      // A lot of uneccessary updating is done here....
      //      if (neuronstyle.isNeuronConnected())  

      update();
    }

  public Color getMark()
    {
      return mark;
    }

  public void setMark(Color mark)
    {
      this.mark = mark;
      displayer.repaint();
    }
  
  
  public Neuron getNeuron()
    {
      return neuron;
    }
  public NeuronStyle getNeuronStyle()
    {
      return neuronStyle;
    }
  
  public NeuronType getNeuronType()
    {
      return neuronType;
    }
  
  public Axon getAxon()
    {
      return null;
    }
  
  public AxonType getAxonType()
    {
      return null;
    }

}
