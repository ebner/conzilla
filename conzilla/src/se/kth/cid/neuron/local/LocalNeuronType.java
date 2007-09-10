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

public class LocalNeuronType extends LocalComponent implements NeuronType
{
  ///////////BoxStyle-variables//
  String   box;          
  int      boxcolor;
  String   linetype;     
  int      linethickness;
  int      linecolor;    
  
  Vector   datatags;
  
  ///////////AxonTypes-variables////
  Hashtable  haxonTypes;    
  AxonType   axontypes[];
  
  public LocalNeuronType(URI uri)
    {
      super(uri);
      haxonTypes = new Hashtable();
      
      axontypes = null;
      datatags = new Vector();
    }  

  ///////////BoxStyle////////////
  
  public String  getBoxType()
    {
      return box;
    }
  
  public void setBoxType(String box) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      this.box = box;
      fireEditEvent(new EditEvent(this, BOXTYPE_EDITED, box));
    }
  
  public int getBoxColor()
    {
      return boxcolor;
    }
  
  public void setBoxColor(int color) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      boxcolor=color;
      fireEditEvent(new EditEvent(this, BOXCOLOR_EDITED, new Integer(color)));
    }
  
  public String  getLineType()
    {
      return linetype;
    }
  
  public void    setLineType(String linetype) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      this.linetype=linetype;
      fireEditEvent(new EditEvent(this, LINETYPE_EDITED, linetype));
    }
  
  public int     getLineThickness()
    {
      return linethickness;
    }
  
  public void setLineThickness(int thick) throws NeuronException, ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      if (thick>10 || thick<0)
	throw new NeuronException("Erraneous line thickness: " + thick);

      linethickness = thick;
      fireEditEvent(new EditEvent(this, LINETHICKNESS_EDITED, new Integer(thick)));
    }
  
  public int getLineColor()
    {
      return linecolor;
    }

  public void    setLineColor(int color) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");

    linecolor = color;
    fireEditEvent(new EditEvent(this, LINECOLOR_EDITED, new Integer(color)));
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

  
  ///////////AxonTypes//////////////
  public int      getDegree()
    {
      return haxonTypes.size();
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
  
  public void removeAxonType(String type) throws ReadOnlyException, NeuronException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      AxonType rt = (AxonType) haxonTypes.get(type);
      if(rt == null)
	throw new NeuronException("No such AxonType to remove: " + type);

      haxonTypes.remove(type);
      fireEditEvent(new EditEvent(this, AXONTYPE_REMOVED, type));
    }
}



