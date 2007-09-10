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
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;

import java.util.*;

public class LocalAxon implements Axon
{
  String id;
  String type;
  String endURI; // Might be relative...
  LocalNeuron neuron;
  
  private Hashtable vdatavalues;
  // Cache
  private Hashtable datavalues;
  private String[] datatags;
  
  
  protected LocalAxon(String id, String type, String endURI, LocalNeuron neuron)
    throws InvalidURIException
    {
      this.id         = id;
      this.type       = type;               
      this.endURI     = endURI;
      neuron.tryURI(endURI);          
      this.neuron     = neuron;
      
      vdatavalues   = new Hashtable();
      datavalues    = new Hashtable();
    }
 
  public String getID()
    {
      return id;
    }
  
  public String getType()
    {
      return type;
    }

  public String getEndURI()
    {
      return endURI;
    }

  public Neuron getNeuron()
    {
      return neuron;
    }
  

  public String[] getDataTags()
    {
      if (datatags == null)
	datatags = (String[])
	  vdatavalues.keySet().toArray(new String[vdatavalues.size()]);
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
    if(!neuron.isEditable())
      throw new ReadOnlyException("This Neuron is Read-Only!");

    Vector values = (Vector) vdatavalues.get(tag);

    if (values == null)
      {
	values = new Vector();
	vdatavalues.put(tag, values);
	datatags = null;
      }
    else
      datavalues.remove(tag);
    
    values.addElement(value);
    neuron.fireEditEvent(new EditEvent(this, DATAVALUE_ADDED, tag));
  }

  
  public void removeDataValue(String tag, String value)
    throws ReadOnlyException
  {    
    if(!neuron.isEditable())
      throw new ReadOnlyException("This Neuron is Read-Only!");

    int index;
    Vector values = (Vector) vdatavalues.get(tag);
    if (values != null && (index = values.indexOf(value)) != -1)
      {
	values.removeElementAt(index);
	datavalues.remove(tag);
	
	if (values.isEmpty())
	  {
	    vdatavalues.remove(tag);
	    datatags = null;
	  }
	neuron.fireEditEvent(new EditEvent(this, DATAVALUE_REMOVED, tag));
      }
  }

}



