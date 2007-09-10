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
import se.kth.cid.component.local.*;
import se.kth.cid.neuron.*;


import java.util.*;


/** An implementation of Axon to be used for LocalNeurons
 *
 *  @author Mikael Nilsson, Matthias Palmer
 *  @version $Revision$
 */
public class LocalAxon extends LocalComponent implements Axon
{
  String subjectURI; // Might be relative...
  String predicateURI; // Might be relative...
  String objectURI; // Might be relative...
    
  DataValue[] dataValues;
  
  
  public LocalAxon(URI uri, URI loadURI, MIMEType loadType, 
		   String subjectURI, String predicateURI, String objectURI)
      throws InvalidURIException
    {
	super(uri, loadURI, loadType);

	
	this.subjectURI  = subjectURI;
	this.predicateURI  = predicateURI;
	this.objectURI     = objectURI;
	//	neuron.tryURI(objectURI);
	//	this.neuron     = neuron;      
	
	dataValues = new DataValue[0];
    }
 
  /** 
   */
  public String subjectURI()
    {
	return subjectURI;
    }

  /** 
   */
  public String predicateURI()
    {
	return predicateURI;
    }

  /** 
   */
  public String objectURI()
    {
	return objectURI;
    }


  //Here follows the deprecated functions.
  public String getID()
    {
      return getURI();
    }
  
  public String getType()
    {
      return predicateURI;      
    }

  public String getEndURI()
    {
      return objectURI;
    }

  public Neuron getNeuron()
    {
	return null;
	//      return (Neuron) neuron;
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
      
      dataValues = new DataValueImpl[values.length];
      System.arraycopy(values, 0, dataValues, 0, values.length);

      fireEditEvent(new EditEvent(this, this, DATAVALUES_EDITED, dataValues));
    }

}



