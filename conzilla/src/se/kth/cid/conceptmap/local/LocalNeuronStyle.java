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
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import java.awt.*;
import java.util.*;

public class LocalNeuronStyle implements NeuronStyle
{
  
  protected LocalConceptMap conceptMap;

  String        neuronURI; // might be relative

  String        id;
  
  String       detailedMap; // Might be relative
  
  boolean                 bodyVisible;
  ConceptMap.BoundingBox  boundingBox;
  ConceptMap.Position[]   line;

  Vector     dataTags;

  Hashtable  axons;
  Vector     endOfAxons;

  /** Creates a LocalNeuronStyle. Do not use.
   *  Use ConceptMap.addNeuronStyle() instead.
   */
  public LocalNeuronStyle(String id, String neuronURI,
			  LocalConceptMap conceptMap)
    throws InvalidURIException
    {
      this.conceptMap = conceptMap;
      conceptMap.tryURI(neuronURI);
      this.neuronURI  = neuronURI;
      this.id         = id;
      this.bodyVisible = true;
      this.detailedMap = null;
      boundingBox     = new ConceptMap.BoundingBox(new ConceptMap.Dimension(0, 0),
						   new ConceptMap.Position(0, 0));
      line            = null;

      axons           = new Hashtable();
      endOfAxons      = new Vector();
      dataTags        = new Vector();      
    }
  
  
  public void remove() throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
      	throw new ReadOnlyException("");

      int size = axons.size();
      
      for(;size > 0; size--)
	{
	  Iterator it = axons.values().iterator();
	  ((AxonStyle) it.next()).remove();
	}

      while(endOfAxons.size() > 0)
	((AxonStyle) endOfAxons.elementAt(endOfAxons.size() - 1)).remove();

      conceptMap.removeNeuronStyle(this);	
      conceptMap.fireEditEvent(new EditEvent(conceptMap, ConceptMap.NEURONSTYLE_REMOVED, id));
    }


  public ConceptMap getConceptMap()
    {
      return conceptMap;
    }

  public String getID()
    {
      return id;
    }
  
  public String getNeuronURI()
    {
      return neuronURI;
    }

  
  public String getDetailedMap()
    {
      return detailedMap;
    }

  public void setDetailedMap(String uri)
    throws ReadOnlyException, InvalidURIException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      if(uri != null)
	conceptMap.tryURI(uri);
      
      detailedMap = uri;
      
      conceptMap.fireEditEvent(new EditEvent(this, DETAILEDMAP_EDITED, uri));
    }
  
  
  public ConceptMap.BoundingBox getBoundingBox()
    {
      return boundingBox;
    }
  
  public void setBoundingBox(ConceptMap.BoundingBox rect) throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      if(rect == null)
	throw new IllegalArgumentException("Null BoundingBox");
      
      boundingBox = rect;

      conceptMap.fireEditEvent(new EditEvent(this, BOUNDINGBOX_EDITED, rect));
    }

  public boolean getBodyVisible()
    {
      return bodyVisible;
    }
  
  public void setBodyVisible(boolean visible) throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      this.bodyVisible = visible;

      conceptMap.fireEditEvent(new EditEvent(this, BODYVISIBLE_EDITED, new Boolean(visible)));
    }
  
  public String[] getDataTags()
    {
      return (String[]) dataTags.toArray(new String[dataTags.size()]);
    }
  
  public void     addDataTag(String tag) throws ReadOnlyException
  {
    if (!conceptMap.isEditable())
      throw new ReadOnlyException("");
    
    int index = dataTags.indexOf(tag);
    if (index == -1)
      {
	dataTags.addElement(tag);
	conceptMap.fireEditEvent(new EditEvent(this, DATATAG_ADDED, tag));
      }
  }
  
  public void     removeDataTag(String tag) throws ReadOnlyException
  {
    if (!conceptMap.isEditable())
      throw new ReadOnlyException("");
    
    int index = dataTags.indexOf(tag);
    
    if(index != -1)
      {
	dataTags.removeElement(tag);
	conceptMap.fireEditEvent(new EditEvent(this, DATATAG_REMOVED, tag));
      }
  }

  public ConceptMap.Position[] getLine()
    {
      return line;
    }

  public void setLine(ConceptMap.Position[] line) throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      if(line != null && line.length < 2)
	throw new IllegalArgumentException("Too few line elements");
      
      this.line = line;
      
      conceptMap.fireEditEvent(new EditEvent(this, LINE_EDITED, line));
    }
  
  public AxonStyle[] getEndOfAxonStyles()
    {
      return (AxonStyle[]) endOfAxons.toArray(new AxonStyle[endOfAxons.size()]);
    }

  public AxonStyle[] getAxonStyles()
    {
      return (AxonStyle[]) axons.values().toArray(new AxonStyle[axons.size()]);
    }

  public AxonStyle getAxonStyle(String id)
    {
      return (AxonStyle) axons.get(id);
    }

  
  public AxonStyle addAxonStyle(String axonID, NeuronStyle end)
    throws ReadOnlyException, ConceptMapException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      if(!(end instanceof LocalNeuronStyle &&
	   end.getConceptMap() == conceptMap))
	throw new ConceptMapException("Tried to add AxonStyle pointing to NeuronStyle in other map!");
      
      LocalNeuronStyle ns = (LocalNeuronStyle) end;

      if(axons.containsKey(axonID))
	throw new ConceptMapException("Axon is already in map.");  

      AxonStyle as = new LocalAxonStyle(this, axonID, ns, conceptMap);
      
      axons.put(axonID, as);
      
      ns.endOfAxons.add(as);
      
      conceptMap.fireEditEvent(new EditEvent(this, AXONSTYLE_ADDED, axonID));
      return as;
    }

  /** Removes the given AxonStyle.
   *  Do not use. Use AxonStyle.remove() instead.
   *
   *  @param axonstyle the AxonStyle to remove.
   */
  protected void removeAxon(AxonStyle axonstyle) 
    {      
      axons.remove(axonstyle.getAxonID());
    }

  /** Removes the given AxonStyle from this NeuronStyle endOf-list..
   *  Do not use. Use AxonStyle.remove() instead.
   *
   *  @param axonstyle the AxonStyle to remove.
   */
  protected void removeEndOfAxon(AxonStyle axonstyle) 
    {
      endOfAxons.removeElement(axonstyle);
    }
}



