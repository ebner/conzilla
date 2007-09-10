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
import se.kth.cid.identity.*;
import java.awt.*;
import java.lang.*;
import java.util.*;


public class AxonMapObject implements MapObject
{
  NeuronMapObject neuronMapObject;

  AxonStyle  axonStyle;
  Axon       axon;
  AxonType   axonType;
  
  String     invalidError;

  LineDrawer lineDrawer;
  HeadDrawer headDrawer;

  public AxonMapObject(AxonStyle axonStyle, NeuronMapObject neuronMapObject)
    {
      this.axonStyle = axonStyle;
      this.neuronMapObject = neuronMapObject;

      Neuron neuron = neuronMapObject.getNeuron();
      
      if(neuron != null)
	checkAxon();
      
      lineDrawer = new LineDrawer(this);
      headDrawer = new HeadDrawer(this);
      
      update();

    }

  void checkAxon()
    {
      axon = neuronMapObject.getNeuron().getAxon(axonStyle.getURI());
	  
      if(axon != null)
	{
	  NeuronType neuronType = neuronMapObject.getNeuronType();
	  if(neuronType != null)
	      axonType = neuronType.getAxonType(axon.predicateURI());
	  
	  // Check the end...
	  NeuronStyle end = axonStyle.getObject();
	  if (end == null)
	      return;
	  ConceptMap map = end.getConceptMap();	  
	  URI neuronStyleURI
	    = URIClassifier.parseValidURI(end.getNeuronURI(),
					  map.getURI());
	  
	  URI axonURI = URIClassifier.parseValidURI(axon.objectURI(),
						    neuronMapObject.getNeuron().getURI());
	  
	  if(! (axonURI.equals(neuronStyleURI)))
	    {
	      invalidError = "AxonStyle pointing to: '" + neuronStyleURI + "', while Axon "
		+ "pointing to '" + axonURI + "'.";
	      
	      Tracer.trace(invalidError, Tracer.WARNING);
	    } 
	}
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

  public AxonStyle getAxonStyle()
    {
      return axonStyle;
    }

  public AxonType getAxonType()
    {
      return axonType;
    }


  ///////// Update support //////////
  
  public void update()
    {
      ConceptMap.Position[] styleLine = axonStyle.getLine();
      
      Point line[] = new Point[styleLine.length];
      for (int i = 0; i < line.length; i++)
	line[i] = new Point(styleLine[i].x, styleLine[i].y);
      
      headDrawer.update(line);  
      lineDrawer.update(line);
    }

  public void componentEdited(EditEvent e)
    {
      neuronMapObject.dumpCache();
      switch(e.getEditType())
	{
	case AxonType.LINETYPE_EDITED:
	case AxonType.HEADTYPE_EDITED:
	case AxonStyle.LINE_EDITED:

	  update();
	  break;
	  
	case Neuron.AXON_ADDED:
	case Neuron.AXON_REMOVED:

	  if(axonStyle.getURI().equals(e.getTarget()))
	    checkAxon();
	  break;
	  
	case NeuronType.AXONTYPE_ADDED:
	case NeuronType.AXONTYPE_REMOVED:

	  checkAxon();
	  break;
	  
	case AxonType.MINIMUMMULTIPLICITY_EDITED:
	case AxonType.MAXIMUMMULTIPLICITY_EDITED:
	case AxonStyle.DATATAG_ADDED:
	case AxonStyle.DATATAG_REMOVED:
	case AxonType.DATATAG_ADDED:
	case AxonType.DATATAG_REMOVED:
	case Axon.DATAVALUES_EDITED:
	  break;
	}
    }

    public boolean getVisible()
    {
	if (!neuronMapObject.getVisible())
	    return false;
	
	NeuronMapObject oe = (NeuronMapObject) neuronMapObject.getDisplayer().getNeuronMapObject(axonStyle.getObjectURI());
	return oe.getVisible();
    }
  
  /////////// Painting ///////////


  public void coloredPaint(Graphics g, NeuronMapObject nmo)
    {
      lineDrawer.coloredPaint(g, nmo);
      headDrawer.coloredPaint(g, nmo);
    }
  
  public boolean checkAndFillHit(MapEvent m)
    {
      if(lineDrawer.checkAndFillHit(m))
	{
	  m.mapObject = this;
	  m.hitType   = MapEvent.HIT_AXONLINE;
	  return true;
	}
      
      return false;
    }

  
  public String getErrorReport()
    {
      Neuron neuron = neuronMapObject.getNeuron();
      
      if(neuron == null)
	return "The neuron '" + URIClassifier.parseValidURI(neuronMapObject.getNeuronStyle().getNeuronURI(), neuronMapObject.getNeuronStyle().getConceptMap().getURI()) + "' was not found.";
      

      if(axon == null)
	return "The axon with ID '"  + axonStyle.getURI() + "' was not found in neuron '" + neuron.getURI() + "'.";

      NeuronType neuronType = neuronMapObject.getNeuronType();
      if(neuronType == null)
	return "The neuron type '" + URIClassifier.parseValidURI(neuron.getType(), neuron.getURI()) + "' was not found.";
      
      if(axonType == null)
	return "The axon type '" + axon.predicateURI() + "' was not found in neuron type '" + neuronType.getURI() + "'.";

      if(invalidError != null)
	return invalidError;

      return null;
    }
  
  public Collection getBoundingboxes()
    {
	Collection col = lineDrawer.getBoundingboxes();
	col.add(headDrawer.getBoundingBox());
	return col;
    }

  public void detach()
    {
      lineDrawer.detach();
      lineDrawer = null;
      
      headDrawer.detach();
      headDrawer = null;
    }
  
}

