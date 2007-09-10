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


/** An implementation of Neuron to be used for components downloaded
 *  over the web.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalNeuron extends LocalComponent implements Neuron
{
  URI neuronType;


  Vector axonvec;
  Vector axonkeys;

  // Cache
  Axon[]    axons;

  DataValue[] dataValues;

  public LocalNeuron(URI neuronURI, URI loadURI, MIMEType loadType, URI neuronType)
    {
      super(neuronURI, loadURI, loadType);
      
      this.neuronType = neuronType;
      
      //      haxons = new Hashtable();  
      axonvec = new Vector();
      axonkeys = new Vector();

      dataValues = new DataValue[0];
    }
  
  
  public String getType()
    {
      return neuronType.toString();
    }



  public DataValue[] getDataValues()
    {
      return dataValues;
    }

  public void setDataValues(DataValue[] values)
    throws ReadOnlyException
    {    
      if(!isEditable())
	throw new ReadOnlyException("This Neuron is Read-Only!");

      dataValues = new DataValue[values.length];
      System.arraycopy(values, 0, dataValues, 0, values.length);
      
      fireEditEvent(new EditEvent(this, DATAVALUES_EDITED, dataValues));
    }
  
  public Axon[] getAxons()
    {
      if(axons == null)
        axons = (Axon[]) axonvec.toArray(new Axon[axonvec.size()]);
      
      return axons;    
    }
    
  public Axon getAxon(String id) 
    {
	int index = axonkeys.indexOf(id);
	if (index!=-1)
	    return (Axon) axonvec.elementAt(index);
	else
	    return null;
    }

  
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
      
      axons = null;
      
      fireEditEvent(new EditEvent(this, AXON_REMOVED, id));
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

      axons = null;
      
      fireEditEvent(new EditEvent(this, AXON_ADDED, id));
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

      axons = null;
      
      fireEditEvent(new EditEvent(this, AXON_ADDED, id));
      return axon;
    }    
}
