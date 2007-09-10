/* $Id$*/
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
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import java.util.*;

/** An implementation of NeuronType to be used for components downloaded
 *  over the web.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalNeuronType extends LocalComponent implements NeuronType
{
  ///////////BoxStyle-variables//
  NeuronType.BoxType  boxType;          
  NeuronType.LineType lineType;     
  
  Vector   datatags;
  
  ///////////AxonTypes-variables////
  Hashtable  haxonTypes;    
  AxonType   axontypes[];
  
  public LocalNeuronType(URI uri, URI loadURI, MIMEType loadType)
    {
      super(uri, loadURI, loadType);
      haxonTypes = new Hashtable();

      boxType  = new NeuronType.BoxType("rectangle", false, "continuous", 2);
      lineType = new NeuronType.LineType("continuous", 2);
      
      
      axontypes = null;
      datatags = new Vector();
    }  

  ///////////BoxStyle////////////
  
  public NeuronType.BoxType getBoxType()
    {
      return boxType;
    }
  
  public void setBoxType(NeuronType.BoxType boxType) throws ReadOnlyException, NeuronException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if(boxType == null)
	throw new NeuronException("Null box type");

      if(boxType.type == null || boxType.borderType == null
	 || boxType.borderThickness < 1 || boxType.borderThickness > 10)
	throw new NeuronException("Illegal box type");

      this.boxType = new NeuronType.BoxType(boxType.type, boxType.filled,
					    boxType.borderType,
					    boxType.borderThickness);
      
      fireEditEvent(new EditEvent(this, BOXTYPE_EDITED, boxType));
    }
  
  public NeuronType.LineType getLineType()
    {
      return lineType;
    }
  
  public void setLineType(NeuronType.LineType lineType)
    throws ReadOnlyException, NeuronException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if(lineType == null)
	throw new NeuronException("Null line type");

      if(lineType.type == null || 
	 lineType.thickness < 1 || lineType.thickness > 10)
	throw new NeuronException("Illegal line type");
      
      this.lineType = lineType;
      
      fireEditEvent(new EditEvent(this, LINETYPE_EDITED, lineType));
    }
  

  public String[] getDataTags()
  {
    String[] tagsArray = new String[datatags.size()];
    datatags.copyInto(tagsArray);
    return tagsArray;
  }
  
  public void     addDataTag(String tag) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    int index = datatags.indexOf(tag);
    if (index == -1)
      {
	datatags.addElement(tag);
	fireEditEvent(new EditEvent(this, DATATAG_ADDED, tag));
      }
  }
  
  public void     removeDataTag(String tag) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");

    int index = datatags.indexOf(tag);
    if(index != -1)
      {
	datatags.removeElementAt(index);
	fireEditEvent(new EditEvent(this, DATATAG_REMOVED, tag));
      }
  }

  
  public AxonType[] getAxonTypes()
    {
      if (axontypes == null)
        axontypes = (AxonType[]) haxonTypes.values().toArray(new AxonType[haxonTypes.size()]);
      return axontypes;
    }
  
  public AxonType getAxonType(String type)
    {
      return (AxonType) haxonTypes.get(type);
    }
  
  public AxonType addAxonType(String type) throws NeuronException, ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if(haxonTypes.get(type) != null)
	throw new NeuronException("AxonType already exists: " + type);

      AxonType rt = new LocalAxonType(type, this);
      
      haxonTypes.put(type, rt);

      axontypes = null;
      fireEditEvent(new EditEvent(this, AXONTYPE_ADDED, type));
      return rt;
    }
  
  public void removeAxonType(String type) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      AxonType rt = (AxonType) haxonTypes.get(type);
      if(rt == null)
	throw new IllegalArgumentException("No such AxonType to remove: " + type);

      haxonTypes.remove(type);
      fireEditEvent(new EditEvent(this, AXONTYPE_REMOVED, type));
    }
}



