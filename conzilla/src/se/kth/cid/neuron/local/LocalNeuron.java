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


package se.kth.cid.neuron.local;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import java.util.*;

public class LocalNeuron extends LocalComponent implements Neuron
{
  URI neuronType;


  Hashtable haxons;
  // Cache
  Axon[]    axons;
  
  private Hashtable vdatavalues;
  // Cache
  private Hashtable datavalues;
  private String[] datatags;

  public LocalNeuron(URI neuronURI, URI neuronType)
    {
      super(neuronURI);
      
      this.neuronType = neuronType;
      
      haxons = new Hashtable();
      
      vdatavalues   = new Hashtable();
      datavalues    = new Hashtable();
    }
  
  
  public String getType()
  {
    return neuronType.toString();
  }



  public String[] getDataTags()
  {
    if (datatags==null)
      {
	datatags=new String[vdatavalues.size()];
	Enumeration en=vdatavalues.keys();
	for (int i=0; en.hasMoreElements();i++)
	  datatags[i]= (String) en.nextElement();
      }
    return datatags;
  }

  public String[] getDataValues(String tag)
  {
    String[] savedValues = (String[]) datavalues.get(tag);
    
    if (savedValues == null)
      {
	Vector dataValueVector = (Vector) vdatavalues.get(tag);

	if(dataValueVector == null)
	  return new String[0];

	savedValues = new String[dataValueVector.size()];
	dataValueVector.copyInto(savedValues);

	datavalues.put(tag, savedValues);
      }
    return savedValues;
  }

  
  public void addDataValue(String tag, String value) throws ReadOnlyException
  {
    if(!isEditable())
      throw new ReadOnlyException("This Neuron is Read-Only!");


    Vector values=(Vector) vdatavalues.get(tag);

    if (values==null)
      {
	values=new Vector();
	vdatavalues.put(tag, values);
	datatags=null;
      }
    else
      datavalues.remove(tag);
    
    values.addElement(value);
    fireEditEvent(new EditEvent(this, DATAVALUE_ADDED, tag));
  }

  
  public void removeDataValue(String tag, String value)
    throws ReadOnlyException
  {    
    if(!isEditable())
      throw new ReadOnlyException("This Neuron is Read-Only!");

    int index;
    Vector values=(Vector) vdatavalues.get(tag);
    if (values != null && (index = values.indexOf(value)) != -1)
      {
	values.removeElementAt(index);
	datavalues.remove(tag);
	
	if (values.isEmpty())
	  {
	    vdatavalues.remove(tag);
	    datatags=null;
	  }
	fireEditEvent(new EditEvent(this, DATAVALUE_REMOVED, tag));
      }
  }
  
  public Axon[] getAxons()
    {
    if (axons == null)
      axons = (Axon[]) haxons.values().toArray(new Axon[haxons.size()]);

    return axons;    
  }
    
  public Axon getAxon(String id) 
    {
      return (Axon) haxons.get(id);
    }

  
  public void removeAxon(String id) throws ReadOnlyException, NeuronException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      Axon axon = (Axon) haxons.get(id);

      if(!(haxons.contains(id)))
	throw new NeuronException("No such Axon to remove!");	

      haxons.remove(id);
      
      axons = null;
      
      fireEditEvent(new EditEvent(this, AXON_REMOVED, id));
    }
  


  public Axon addAxon(String id, String type, String neuronURI)
    throws NeuronException, ReadOnlyException, InvalidURIException
    {
    if (!isEditable())
      throw new ReadOnlyException("");
    
    if(haxons.get(id) != null)
      throw new NeuronException("Already have Axon with id '" + id + "'");
    
    Axon axon = new LocalAxon(id, type, neuronURI, this);
    
    haxons.put(id, axon);
    axons = null;
    
    fireEditEvent(new EditEvent(this, AXON_ADDED, id));
    return axon;
  }    
}
