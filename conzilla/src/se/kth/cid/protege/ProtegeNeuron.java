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


package se.kth.cid.protege;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.neuron.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import java.util.*;
import edu.stanford.smi.protege.model.*;


/** An implementation used for wrapping protege Frames.
 *  Used mainly for fetching frame-specific meta-data into
 *  the neuron-model. Should listen to the knowledgebase...
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ProtegeNeuron extends LocalComponent implements Neuron
{
    Axon [] axons;
    Vector axonvec = null;
    Vector axonkeys = null;
    Instance instance;
    ProtegeHelper helper;
    URI typeURI;

  public ProtegeNeuron(URI neuronURI, URI loadURI, MIMEType loadType, URI typeURI)
    {
      super(neuronURI, loadURI, loadType);
      this.typeURI = typeURI;
      setEditable(false);
    }
  
  public void setProtegeInstance(Instance instance, ProtegeHelper helper)
    {
	this.instance = instance;
	this.helper = helper;
	setEditable(true);
    }
  
  public String getType()
    {
	return typeURI.toString();
	//	return helper.getURI(instance.getDirectType());
    }



  public DataValue[] getDataValues()
    {
      Collection slots = instance.getOwnSlots();
      Iterator it  = slots.iterator();
      Vector datas = new Vector();
      while (it.hasNext())
	  {
	      Slot slot = (Slot) it.next();
	      ValueType vt = instance.getOwnSlotValueType(slot);
	      
	      if (vt != ValueType.INSTANCE && vt != ValueType.CLS)
		  {
		      Object o = instance.getOwnSlotValue(slot);
		      if (o !=null)
			  datas.add(new DataValueImpl(helper.getURI(slot),
						  o.toString()));
		      else
			  Tracer.debug("Own slot = "+slot.getName()+
				       " had no value on instance "+
				       instance.getName());
		  }
	  }
      
      DataValue [] dv = new DataValueImpl[datas.size()];
      datas.copyInto(dv);
      return dv;
    }

  public void setDataValues(DataValue[] values)
    throws ReadOnlyException
    {    
      if(!isEditable())
	throw new ReadOnlyException("This Neuron is Read-Only!");
      
      //fill in here.

      fireEditEvent(new EditEvent(this, this, DATAVALUES_EDITED, values));
    }
  
  public Axon[] getAxons()
    {
	return new Axon[0];
	//Wrong approach, think reified.
	/*if (axonvec == null)
	  {
	      Collection slots = instance.getOwnSlots();
	      Iterator it  = slots.iterator();
	      axonvec = new Vector();
	      axonkeys = new Vector();
	      while (it.hasNext())
		  {
		      Slot slot = (Slot) it.next();
		      ValueType vt = instance.getOwnSlotValueType(slot);
		      if (vt == ValueType.INSTANCE || vt == ValueType.CLS)
			  {
			      String key = helper.getURI(slot).toString();
			      try {
				  Tracer.debug("slot "+slot.getName() +" as an axon");
				  LocalAxon la = new LocalAxon(key,
							       //helper.getURI(slot.getDirectType()), 
							       "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
							       helper.getURI((Instance) instance.
									     getOwnSlotValue(slot)), 
							       this);
				  axonvec.add(la);
				  axonkeys.add(key);
			      } catch (InvalidURIException iue)
				  {Tracer.bug(iue.getMessage());}
			  }
		  }
	  }
      if(axons == null)
        axons = (Axon[]) axonvec.toArray(new Axon[axonvec.size()]);
      
	return axons;*/
    }
    
  public Axon getAxon(String id) 
    {
	return null;
	/*
	if (axons == null)
	    getAxons();
	int index = axonkeys.indexOf(id);
	if (index!=-1)
	    return (Axon) axonvec.elementAt(index);
	else
	return null;*/
    }

    //fix this as well...
  public void removeAxon(String id) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

	int index = axonkeys.indexOf(id);
	if (index==-1)
	    throw new IllegalArgumentException("No such Axon to remove!");	    
	
	Axon axon = (Axon) axonvec.elementAt(index);

	axonvec.removeElementAt(index);
	axonkeys.removeElementAt(index);
	
	//Add remove from protegeFrame as well.

	axons = null;
      
      fireEditEvent(new EditEvent(this, this, AXON_REMOVED, id));
    }
  

  /** Used to add an Axon with a known ID. Typically used only
   *  when loading a saved Neuron.
   *
   *  @param id the ID of the Axon.
   *  @param type the type of the Axon.
   *  @param neuronURI the URI of the pointed-to Neuron.
   *
   *  @return the created Axon.
   *  @exception ReadOnlyException if the Neuron was not editable.
   *  @exception NeuronException if the Axon could not be created.
   *  @exception InvalidURIException if the Neuron URI was not valid.
   */
  public Axon addAxon(String id, String type, String neuronURI)
    throws NeuronException, ReadOnlyException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      
      if(axonkeys.contains(id))
	throw new NeuronException("Already have Axon with id '" + id + "'");
      
      Axon axon = new LocalAxon(id, type, neuronURI, this);
      
      axonkeys.addElement(id);
      axonvec.addElement(axon);

      //Add protege changes here.

      axons = null;
      
      fireEditEvent(new EditEvent(this, this, AXON_ADDED, id));
      return axon;
    }
  
  public Axon addAxon(String type, String neuronURI)
    throws NeuronException, ReadOnlyException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      
      String id = createID(axonkeys, type);
      
      Axon axon = new LocalAxon(id, type, neuronURI, this);
      
      axonkeys.addElement(id);
      axonvec.addElement(axon);
      
      //Add protege code here.
      axons = null;
      
      fireEditEvent(new EditEvent(this, this, AXON_ADDED, id));
      return axon;
    }    
}

