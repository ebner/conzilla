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


package se.kth.cid.conceptmap.local;
import se.kth.cid.identity.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.local.*;

import java.util.*;

public class LocalConceptMap extends LocalComponent implements ConceptMap 
{
  
  ConceptMap.Dimension dimension;

  Hashtable neuronStyles;

  /** Creates an empty ConceptMap with the given URI.
   *
   *  @param mapURI the URI of this map.
   *  @param loadURI the URI used to load this map.
   *  @param loadType the MIME type used to load this map.
   */
  public LocalConceptMap(URI mapURI, URI loadURI, MIMEType loadType)
    {
      super(mapURI, loadURI, loadType);
      neuronStyles = new Hashtable();
      dimension = new ConceptMap.Dimension(400, 400);
    }


  /////////////ConceptMap/////////////
  public ConceptMap.Dimension   getDimension()
  {
    return dimension;
  }

  public void setDimension(ConceptMap.Dimension dim) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if(dim == null)
	throw new  IllegalArgumentException("Null dimension.");

      dimension = dim;
      fireEditEvent(new EditEvent(this, DIMENSION_EDITED, dim));
    }
  
  
  /////////////NeuronStyle////////////
  public NeuronStyle[]   getNeuronStyles()
    {
      return (NeuronStyle[]) neuronStyles.values().toArray(new NeuronStyle[neuronStyles.size()]);
    }

  public NeuronStyle   getNeuronStyle(String mapID)
    {
      return (NeuronStyle) neuronStyles.get(mapID);
    }

  /** Used to add a NeuronStyle with a known ID. Typically used only
   *  when loading a saved ConceptMap.
   *  May fail if the Neuron does not exist, but not necessarily.
   *
   *  @param mapID the ID of the NeuronStyle.
   *  @param neuronURI the URI of the Neuron to be represented.
   *
   *  @return the new NeuronStyle.
   *  @exception ReadOnlyException if this ConceptMap was not editable.
   *  @exception ConceptMapException if the ID was aleady in use.
   *  @exception InvalidURIException if the URI was not valid.
   */
  public NeuronStyle   addNeuronStyle(String mapID, String neuronuri)
    throws ReadOnlyException, InvalidURIException, ConceptMapException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if(neuronStyles.containsKey(mapID))
	throw new ConceptMapException("The NeuronStyle with Id '" + mapID
				      + "' was already in this map.");
      
      NeuronStyle ns = new LocalNeuronStyle(mapID, neuronuri, this);
      neuronStyles.put(mapID, ns);
      fireEditEvent(new EditEvent(this, NEURONSTYLE_ADDED, mapID));
      return ns;
    }
      
  
  public NeuronStyle   addNeuronStyle(String neuronuri)
    throws ReadOnlyException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      String mapID = createID(neuronStyles.keySet(), neuronuri);
      
      NeuronStyle ns = new LocalNeuronStyle(mapID, neuronuri, this);
      neuronStyles.put(mapID, ns);
      fireEditEvent(new EditEvent(this, NEURONSTYLE_ADDED, mapID));
      return ns;
    }

  /** Removes the given NeuronStyle.
   *  Do not use. Call NeuronStyle.remove() instead.
   *
   *  @param neuronstyle  a style for a neuron.
   *  @see NeuronStyle
   */
  protected void removeNeuronStyle(NeuronStyle neuronstyle)
    {
      String id = neuronstyle.getID();
      if(neuronStyles.containsKey(id))
	neuronStyles.remove(id);
      else
	  Tracer.bug("No such NeuronStyle when removing: '" + id + "'!");
    }
}



