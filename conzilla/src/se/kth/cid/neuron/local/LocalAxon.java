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


/** An implementation of Axon to be used for LocalNeurons
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalAxon implements Axon
{
  String id;
  String type;
  String endURI; // Might be relative...
  LocalNeuron neuron;
  
  Neuron.DataValue[] dataValues;
  
  
  protected LocalAxon(String id, String type, String endURI, LocalNeuron neuron)
    throws InvalidURIException
    {
      this.id         = id;
      this.type       = type;               
      this.endURI     = endURI;
      neuron.tryURI(endURI);          
      this.neuron     = neuron;      

      dataValues = new Neuron.DataValue[0];
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
  

  public Neuron.DataValue[] getDataValues()
    {
      return dataValues;
    }

  public void setDataValues(Neuron.DataValue[] values)
    throws ReadOnlyException
    {    
      if(!neuron.isEditable())
	throw new ReadOnlyException("This Neuron is Read-Only!");
      
      dataValues = new Neuron.DataValue[values.length];
      System.arraycopy(values, 0, dataValues, 0, values.length);

      neuron.fireEditEvent(new EditEvent(this, DATAVALUES_EDITED, dataValues));
    }

}



