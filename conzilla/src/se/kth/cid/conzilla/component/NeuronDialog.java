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


package se.kth.cid.conzilla.component;
import javax.swing.*;
import javax.swing.event.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.metadata.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


/** This class holds the basic functionality for editing a component,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentEditor
 */
public class NeuronDialog extends ComponentDialog 
{
  StringPanel typeURIField;
  DataDisplayerPanel data;
  AxonDisplayerPanel axons;

  NeuronType type;
  
  public NeuronDialog(ConzillaKit kit)
    {
      super(kit);
    }

  protected String getComponentString()
    {
      return "Neuron";
    }

  protected void createComponentTab()
    {
      super.createComponentTab();

      typeURIField = new StringPanel(((Neuron) component).getType(),
				     false, false, null, null);

      Neuron neuron = (Neuron) component;

      type = null;
      
      try {
	ComponentStore store = kit.getComponentStore();
	type = store.getAndReferenceNeuronType(URIClassifier.parseValidURI(neuron.getType()));
      } catch (ComponentException ce)
	{}

      data = new DataDisplayerPanel(neuron, type,
				    type != null && type.getDataTags().length > 0,
				    this, "data");

      axons = new AxonDisplayerPanel((Neuron) component, 
				     type,type != null && type.getAxonTypes().length > 0, 
				     this, "axons"); 
				     
      componentPanel.addPanel("Type URI", typeURIField);
      componentPanel.addPanel("Data", data);
      componentPanel.addPanel("Axons", axons);
    }
  
  public void setComponent(se.kth.cid.component.Component component)
  {
    super.setComponent(component);
    if (!(component instanceof Neuron))
      Tracer.bug("Component in NeuronDialog not a Neuron: " + component.getURI());
  }

  protected void setImpl()
  {
    ((Neuron) component).setDataValues(data.getDataValues(true));
  }
    //necessary??????????
  public ComponentDialog copy()
    {
      ComponentDialog cd=new NeuronDialog(kit);
      //maybe copy stuff...
      return cd;
    }

  public void componentEdited(EditEvent e)
    {
      super.componentEdited(e);
      if(e.getEditType() == Neuron.DATAVALUES_EDITED ||
	 e.getEditType() == NeuronType.DATATAG_REMOVED ||
	 e.getEditType() == NeuronType.DATATAG_ADDED)
	{
	  data.detach();
	  axons.detach();
	  componentPanel.removePanel(data);
	  componentPanel.removePanel(axons);
	  data = new DataDisplayerPanel((Neuron) component, type,
					type != null && type.getDataTags().length > 0,
					this, "data");
	  axons = new AxonDisplayerPanel((Neuron) component, 
					 type,type != null && type.getAxonTypes().length > 0, 
					 this, "axons"); 

	  componentPanel.addPanel("Data", data);
	  componentPanel.addPanel("Axons", axons);
	}
    }
  public void detach()
    {
      super.detach();

      if(type != null)
	type.removeEditListener(this);
      
      data.detach();
      data = null;
      axons.detach();
      axons=null;
    }
}
