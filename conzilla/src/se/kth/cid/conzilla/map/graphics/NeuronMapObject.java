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
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class NeuronMapObject implements EditListener, MapObject
{
  protected NeuronStyle  neuronStyle;
  protected Neuron       neuron;
  protected NeuronType   neuronType;

  Hashtable    axonMapObjects;

  LineDrawer   lineDrawer;
  BoxDrawer    boxDrawer;
  TitleDrawer  titleDrawer;

    //  Color        mark;
  Vector     marks;
  Vector     responsibles;
  Vector     boundingboxes;

  MapDisplayer displayer;
  PropertyChangeListener colorListener;

  boolean editable = false;
  
  public NeuronMapObject(NeuronStyle neuronStyle, Neuron neuron,
			 NeuronType neuronType, MapDisplayer displayer,
			 JComponent backLayer, JComponent foreLayer)
    {
      this.neuronStyle = neuronStyle;
      this.neuron      = neuron;
      this.neuronType  = neuronType;
      this.displayer   = displayer;

      marks = new Vector();
      responsibles= new Vector();
      axonMapObjects  = new Hashtable();
      
      lineDrawer  = new LineDrawer(this);
      boxDrawer   = new BoxDrawer(this);
      titleDrawer = new TitleDrawer(this, foreLayer);

      if(neuron != null)
	neuron.addEditListener(this);
      if(neuronType != null)
	neuronType.addEditListener(this);

      AxonStyle[] axonStyles = neuronStyle.getAxonStyles();
      
      for(int i = 0; i < axonStyles.length; i++)
	axonMapObjects.put(axonStyles[i].getAxonID(), new AxonMapObject(axonStyles[i], this));
      

      titleDrawer.updateTitle();
      titleDrawer.updateData();
      updateBox();
      updateBoxLine();

      colorListener=new PropertyChangeListener() {
	      public void propertyChange(PropertyChangeEvent evt)
	      {
		  getMark(NeuronMapObject.this).update();
		  colorUpdate();
		  updateBox();
		  updateBoxLine();
	      }};
      if (neuron==null)	  
	  {
	      pushMark(new Mark(ColorManager.MAP_NEURON_ERROR, null, null), this);
	      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_NEURON_ERROR, colorListener);
	  }
      else
	  {
	      pushMark(new Mark(ColorManager.MAP_FOREGROUND, ColorManager.MAP_NEURON_BACKGROUND, 
				ColorManager.MAP_TEXT), this);
	      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_FOREGROUND, colorListener);
;
	      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_NEURON_BACKGROUND, colorListener);
	      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_TEXT, colorListener);
	  }
      colorUpdate();
      boundingboxes=null;
    }  

  public NeuronMapObject getNeuronMapObject()
    {
      return this;
    }
  
  public AxonMapObject getAxonMapObject(String id)
    {
      return (AxonMapObject) axonMapObjects.get(id);
    }
  
  
  public MapDisplayer getDisplayer()
    {
      return displayer;
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
  
  public AxonStyle getAxonStyle()
    {
	return null;
    }

  public AxonType getAxonType()
    {
      return null;
    }

  public boolean getErrorState()
    {
      return neuron == null || neuronType == null;
    }

  public Dimension getPreferredSize()
    {
      return titleDrawer.getPreferredSize();
    }
  
  
  /////////// Update support ///////////  

  void updateBoxLine()
    {
      ConceptMap.Position[] styleLine = neuronStyle.getLine();
      Point[] line = null;
      
      if(styleLine != null)
	{
	  line = new Point[styleLine.length];
	  for (int i = 0; i < line.length; i++)
	    line[i] = new Point(styleLine[i].x, styleLine[i].y);
	}
      

      lineDrawer.update(line);
    }

  void updateBox()
    {
      boxDrawer.update(this);

      titleDrawer.updateBox(boxDrawer.getInnerBoundingBox());
    }


  void addAxonStyle(String id)
    {
      AxonStyle as = neuronStyle.getAxonStyle(id);
      
      axonMapObjects.put(id, new AxonMapObject(as, this));
    }
  
  void removeAxonStyle(String id)
    {
      AxonMapObject amo = (AxonMapObject) axonMapObjects.get(id);

      amo.detach();

      axonMapObjects.remove(id);
    }
      

  void dumpCache()
  {
    boundingboxes = null;    
  }
      
  public void componentEdited(EditEvent e)
    {
      dumpCache();
      if(e.getEditedObject() instanceof Axon)
	{
	  Axon axon = (Axon) e.getEditedObject();
	  AxonMapObject amo = (AxonMapObject) getAxonMapObject(axon.getID());
	  if(amo != null)
	    amo.componentEdited(e);
	  return;
	}

      if(e.getEditedObject() instanceof AxonType)
	{
	  Iterator axons = axonMapObjects.values().iterator();
	  while(axons.hasNext())
	    {
	      AxonMapObject amo = (AxonMapObject) axons.next();
	      amo.componentEdited(e);
	    }
	  return;
	}
      
      switch(e.getEditType())
	{
	case NeuronStyle.DATATAG_ADDED:
	case NeuronStyle.DATATAG_REMOVED:
	case Neuron.DATAVALUES_EDITED:
	case NeuronType.DATATAG_ADDED:
	case NeuronType.DATATAG_REMOVED:
	  titleDrawer.updateData();
	  break;
	case NeuronType.BOXTYPE_EDITED:
	case NeuronStyle.BOUNDINGBOX_EDITED:
	case NeuronStyle.BODYVISIBLE_EDITED:
	  updateBox();
	  break;
	case se.kth.cid.component.Component.METADATA_EDITED:
	  titleDrawer.updateTitle();
	  break;
	case NeuronStyle.LINE_EDITED:
	case NeuronType.LINETYPE_EDITED:
	  updateBoxLine();
	  break;
	case NeuronStyle.AXONSTYLE_ADDED:
      	  addAxonStyle((String) e.getTarget());
	  break;
	case NeuronStyle.AXONSTYLE_REMOVED:
      	  removeAxonStyle((String) e.getTarget());
	  break;
	case Neuron.AXON_ADDED:
	case Neuron.AXON_REMOVED:
	  {
	    AxonMapObject amo = (AxonMapObject) getAxonMapObject((String) e.getTarget());
	    if(amo != null)
	      amo.componentEdited(e);
	    break;
	  }
	case NeuronType.AXONTYPE_ADDED:
	case NeuronType.AXONTYPE_REMOVED:
	  {
	    Iterator axons = axonMapObjects.values().iterator();
	    while(axons.hasNext())
	      {
		AxonMapObject amo = (AxonMapObject) axons.next();
		amo.componentEdited(e);
	      }
	    break;
	  }
	}
    }
  


  /////////// Editing/painting methods //////////////

  
  public void setEditable(boolean editable, MapEvent e)
    { 
      if(editable)
	{
	  if (!neuronStyle.getConceptMap().isEditable())
	    return;
	  
	  titleDrawer.setEditable(true, e);
	}
      else
	  titleDrawer.setEditable(false, e);
      this.editable = editable;
    }

  public boolean getEditable()
    {
      return editable;
    }
  
  
  public void setScale(double scale)
    {
      titleDrawer.setScale(scale);
    }

  public void colorUpdate()
    {
	titleDrawer.colorUpdate(getMark());
    }
  
  public void setDisplayLanguageDiscrepancy(boolean b)
    {
      titleDrawer.setDisplayLanguageDiscrepancy(b);
    }
  
  
  public void paint(Graphics g)
    {
      Color markColor = displayer.getGlobalMarkColor() != null ? displayer.getGlobalMarkColor() : getMark().foregroundColor;

      Iterator axons = axonMapObjects.values().iterator();
      while(axons.hasNext()) 
	  ((AxonMapObject) axons.next()).coloredPaint(g, this); //ignoring global markcolor.
      
      if(neuronStyle.getBodyVisible())
	{
	  lineDrawer.coloredPaint(g, this);
	  boxDrawer.coloredPaint(g, this);
	}
    }
  
  public boolean checkAndFillHit(MapEvent m)
    {
      Iterator axons = axonMapObjects.values().iterator();
      while(axons.hasNext())
	{
	  AxonMapObject amo = (AxonMapObject) axons.next();
	  if(amo.checkAndFillHit(m))
	    return true;
	}
      
      if(neuronStyle.getBodyVisible())
	{
	  if(boxDrawer.didHit(m))
	    {
	      m.mapObject = this;
	      if(titleDrawer.didHit(m))
		  {
		    m.hitType = MapEvent.HIT_BOXTITLE;
		    return true; 
		  }
	      if(titleDrawer.data.didHit(m))
		  {
		    m.hitType = MapEvent.HIT_BOXDATA;
		    return true; 
		  }
	      m.hitType = MapEvent.HIT_BOX;
	      return true;
	    }
	  if(lineDrawer.checkAndFillHit(m))
	    {
	      m.mapObject   = this;
	      m.hitType     = MapEvent.HIT_BOXLINE;
	      return true;
	    }
	}
      return false;
    }

  public void clearMark()
    {
      Object responsible = responsibles.elementAt(0);
      Object mark = marks.elementAt(0);

      marks = new Vector();
      responsibles= new Vector();

      if (responsibles!=null && mark!=null)
	  {
	      responsibles.addElement(responsible);
	      marks.addElement(mark);
	  }

      colorUpdate();
      displayer.repaintMap(getBoundingboxes());
    }      
  public Mark getMark()
    {
	if (marks.size()>0)
	    return (Mark) marks.lastElement();
	return new Mark((String) null, null, null);
    }

  public Mark getMark(Object responsible)
    {
	int index=responsibles.lastIndexOf(responsible);
	if (index!=-1)
	    return (Mark) marks.elementAt(index);
	return null;
    }

  public void pushMark(Mark mark, Object responsible)
    {
	responsibles.addElement(responsible);
	marks.addElement(mark);
	colorUpdate();
	displayer.repaintMap(getBoundingboxes());
    }
  public Mark popMark(Object responsible)
    {
	int index=responsibles.lastIndexOf(responsible);
	if (index!=-1)
	    {
		responsibles.remove(index);
		Mark mark=(Mark) marks.remove(index);
		colorUpdate();
		displayer.repaintMap(getBoundingboxes());
		return mark;
	    }
	return null;
    }
  public void replaceMark(Mark newMark, Object responsible)
    {
	int index=responsibles.lastIndexOf(responsible);
	if (index!=-1)
	  {
	      marks.setElementAt(newMark, index);
	      colorUpdate();
	      displayer.repaintMap(getBoundingboxes());
	  }
    }

    public boolean isDefaultMark()
    {
	return marks.size()==1;
    }

    public Collection getBoundingboxes()
    {
       	if (boundingboxes==null)
	    {
		boundingboxes=new Vector();
		if(neuronStyle.getBodyVisible())
		    {
			boundingboxes.addElement(boxDrawer.getBoundingBox());
			boundingboxes.addAll(lineDrawer.getBoundingboxes());
		    }
		
		Iterator axons = axonMapObjects.values().iterator();      
		while(axons.hasNext())
		    boundingboxes.addAll(((AxonMapObject) axons.next()).getBoundingboxes());
	    }
	return boundingboxes;
    }
	    
    /*  public void setMark(Color mark)
    {
      this.mark = mark;
      colorUpdate();
      displayer.repaint();
    }
    */

  ///////////// Detaching ///////////////

  public void detach()
    {
      clearMark(); //Need to be done before things are detached.

      if(neuron != null)
	  {
	      neuron.removeEditListener(this);
	      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_FOREGROUND, colorListener);
	      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_NEURON_BACKGROUND, colorListener);
	      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_TEXT, colorListener);
	  }
      else
	  ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_NEURON_ERROR, colorListener);
      
      if(neuronType != null)
	neuronType.removeEditListener(this);
      detachAxonMapObjects();

      lineDrawer.detach();
      lineDrawer = null;

      boxDrawer.detach();
      boxDrawer = null;

      titleDrawer.detach();
      titleDrawer = null;

    }
  
  void detachAxonMapObjects()
    {
      Iterator axons = axonMapObjects.values().iterator();

      while(axons.hasNext())
	((AxonMapObject) axons.next()).detach();
      axonMapObjects = null;
    }
}
