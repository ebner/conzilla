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
  
  ////////////ConceptMap-varibles//////
  Dimension dimension;
  int       backgroundColor;

  ////////////NeuronStyle-varibles/////
  private Hashtable neuronStyles;

  public LocalConceptMap(URI mapURI)
    {
      super(mapURI);
      neuronStyles = new Hashtable();
    }


  /////////////ConceptMap/////////////
  public Dimension   getDimension()
  {
    return dimension;
  }

  public void          setDimension(Dimension dim) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      dimension = dim;
      fireEditEvent(new EditEvent(this, DIMENSION_EDITED, dim));
    }
  
  public int           getBackgroundColor()
    {
      return backgroundColor;
    }

  public void          setBackgroundColor(int color) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");      
      backgroundColor=color;
      fireEditEvent(new EditEvent(this, BACKGROUND_EDITED, new Integer(color)));
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

  public NeuronStyle   addNeuronStyle(String mapID, String neuronuri)
    throws ReadOnlyException, ConceptMapException, InvalidURIException
    {
      if (!isEditable())
	throw new ReadOnlyException("");

      if (neuronStyles.contains(mapID))
	throw new ConceptMapException("Neuron with map ID '" + mapID +
				      "' already exists in this map!");
      
      NeuronStyle ns = new LocalNeuronStyle(mapID, neuronuri, this);
      neuronStyles.put(mapID, ns);
      fireEditEvent(new EditEvent(this, NEURONSTYLE_ADDED, mapID));
      return ns;
    }

  /** Not meant to be used directly, call disconnect on the
   *  neuronstyle instead.
   *  @param neuronstyle  a style for a neuron.
   *  @see NeuronStyle
   */
  protected void removeNeuronStyle(String mapID) throws ReadOnlyException
  {
    NeuronStyle ns = (NeuronStyle) neuronStyles.get(mapID);
    if(ns != null)
      {
	neuronStyles.remove(mapID);
	fireEditEvent(new EditEvent(this, NEURONSTYLE_REMOVED, mapID));
      }
  }
}



