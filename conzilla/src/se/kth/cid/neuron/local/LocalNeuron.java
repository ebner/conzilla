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

  HashSet fragments;
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

      fragments = new HashSet();
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
      
      fireEditEvent(new EditEvent(this, this, DATAVALUES_EDITED, dataValues));
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
      
      fireEditEvent(new EditEvent(this, this, AXON_REMOVED, id));
    }
  

  /** Used to add an Axon.
   *
   *  @param axon the Axon to add.
   *
   *  @exception ReadOnlyException if the Neuron was not editable.
   *  @exception NeuronException if the Axon could not be created.
   *  @exception InvalidURIException if the Neuron URI was not valid.
   */
  public void  addAxon(Axon axon)
    throws NeuronException, ReadOnlyException
    {
	String uri = axon.getURI();
      if (!isEditable())
	throw new ReadOnlyException("");
      
      if(axonkeys.contains(uri))
	throw new NeuronException("Axon "+uri+" already contained in Neuron "+getURI());

      avoidFragmentFromURI(uri);
      axonkeys.addElement(uri);
      axonvec.addElement(axon);
      
      axons = null;
      
      fireEditEvent(new EditEvent(this, this, AXON_ADDED, uri));
    }  

    public Axon addAxon(String type, String objecturi)
	throws NeuronException, ReadOnlyException, InvalidURIException
    {
	String axonuri = null;
	try {
	    //Automatically Generated Axon ID (AGAID).
	    axonuri = getURI() +"#"+createID(fragments, "AGAID");
	    URI axonURI = URIClassifier.parseURI(axonuri);
	    Axon axon = new LocalAxon(axonURI, componentLoadURI, componentLoadMIMEType,
				      getURI(), type, objecturi);
	    addAxon(axon);
	    return axon;
	} catch (MalformedURIException me)
	    {
		throw new NeuronException("Failed to create an axon in an automated fashion,"+
					  "didn't manage to construct a URI for it, "+
					  axonuri +me.getMessage());
	    }
    }

    protected void avoidFragmentFromURI(String uri)
    {
	try {
	    String fragment = URIClassifier.parseURI(uri).getFragment();
	    if (fragment != null && fragment.length() >0)
		fragments.add(fragment);
	} catch (MalformedURIException mu)
	    {}
    }    
}
