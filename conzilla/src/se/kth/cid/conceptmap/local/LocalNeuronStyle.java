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

public class LocalNeuronStyle extends LocalMapGroupStyle implements ExtendedNeuronStyle
{
  
  String        neuronURI; // might be relative
  
  String       detailedMap; // Might be relative
  
  boolean                 bodyVisible;
  ConceptMap.BoundingBox  boundingBox;
  ConceptMap.Position[]   line;

  Vector     dataTags;

  Hashtable  axons;
  Vector     objectOfAxons;
  Vector     subjectOfAxons;

  int                   pathType;
  int        horisontalTextAnchor;
  int        verticalTextAnchor;

  /** Creates a LocalNeuronStyle. Do not use.
   *  Use ConceptMap.addNeuronStyle() instead.
   */
  public LocalNeuronStyle(String id, String neuronURI,
			  GenericConceptMap conceptMap)
    throws InvalidURIException
    {
	super(id, conceptMap);
      conceptMap.tryURI(neuronURI);
      this.neuronURI  = neuronURI;
      this.bodyVisible = true;
      this.detailedMap = null;
      boundingBox     = new ConceptMap.BoundingBox(new ConceptMap.Dimension(0, 0),
						   new ConceptMap.Position(0, 0));
      line            = null;

      axons           = new Hashtable();
      objectOfAxons      = new Vector();
      subjectOfAxons      = new Vector();
      dataTags        = new Vector();      
      horisontalTextAnchor = CENTER;
      verticalTextAnchor = CENTER;
    }
  
  
  public void remove() throws ReadOnlyException
    {
      if (!getConceptMap().isEditable())
      	throw new ReadOnlyException("");

      int size = axons.size();
      
      for(;size > 0; size--)
	{
	  Iterator it = axons.values().iterator();
	  ((AxonStyle) it.next()).remove();
	}

      while(objectOfAxons.size() > 0)
	((AxonStyle) objectOfAxons.elementAt(objectOfAxons.size() - 1)).remove();

      GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      cMap.removeNeuronStyle(this);	
      cMap.fireEditEvent(new EditEvent(cMap, cMap, ConceptMap.NEURONSTYLE_REMOVED, id));
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
	GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

      if(uri != null)
	cMap.tryURI(uri);
      
      detailedMap = uri;
      
      cMap.fireEditEvent(new EditEvent(cMap, this, DETAILEDMAP_EDITED, uri));
    }
  
  
  public ConceptMap.BoundingBox getBoundingBox()
    {
      return boundingBox;
    }
  
  public void setBoundingBox(ConceptMap.BoundingBox rect) throws ReadOnlyException
    {
      GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

      if(rect == null)
	throw new IllegalArgumentException("Null BoundingBox");
      
      boundingBox = rect;

      cMap.fireEditEvent(new EditEvent(cMap, this, BOUNDINGBOX_EDITED, rect));
    }

  public void setHorisontalTextAnchor(int value) throws ReadOnlyException
    {
      GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

	horisontalTextAnchor = value;
	cMap.fireEditEvent(new EditEvent(cMap, this, TEXT_ANCHOR_EDITED, new Integer(value)));
    }
  public void setVerticalTextAnchor(int value) throws ReadOnlyException
    {
      GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");
      verticalTextAnchor = value;
      cMap.fireEditEvent(new EditEvent(cMap, this, TEXT_ANCHOR_EDITED, new Integer(value)));
    }

  public int getHorisontalTextAnchor()
    {
	return horisontalTextAnchor;
    }
  public int getVerticalTextAnchor()
    {
	return verticalTextAnchor;
    }

  public boolean getBodyVisible()
    {
      return bodyVisible;
    }
  
  public void setBodyVisible(boolean visible) throws ReadOnlyException
    {
      GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

      this.bodyVisible = visible;

      cMap.fireEditEvent(new EditEvent(cMap, this, BODYVISIBLE_EDITED, new Boolean(visible)));
    }
  
  public String[] getDataTags()
    {
      return (String[]) dataTags.toArray(new String[dataTags.size()]);
    }
  
  public void     addDataTag(String tag) throws ReadOnlyException
    {
     GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
     if (!cMap.isEditable())
      throw new ReadOnlyException("");
    
    int index = dataTags.indexOf(tag);
    if (index == -1)
      {
	dataTags.addElement(tag);
	cMap.fireEditEvent(new EditEvent(cMap, this, DATATAG_ADDED, tag));
      }
  }
  
  public void     removeDataTag(String tag) throws ReadOnlyException
  {
     GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
    if (!cMap.isEditable())
      throw new ReadOnlyException("");
    
    int index = dataTags.indexOf(tag);
    
    if(index != -1)
      {
	dataTags.removeElement(tag);
	cMap.fireEditEvent(new EditEvent(cMap, this, DATATAG_REMOVED, tag));
      }
  }

  public ConceptMap.Position[] getLine()
    {
      return line;
    }

  public void setLine(ConceptMap.Position[] line) throws ReadOnlyException
    {
     GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

      if(line != null && line.length < 2)
	throw new IllegalArgumentException("Too few line elements");
      
      this.line = line;
      
      cMap.fireEditEvent(new EditEvent(cMap, this, LINE_EDITED, line));
    }

    public int getPathType()
    {
	return pathType;
    }
    public void setPathType(int pt)
    {
	pathType = pt;
    }

    //START AxonStyle end managment...  
  public AxonStyle[] getObjectOfAxonStyles()
    {
	return (AxonStyle[]) objectOfAxons.toArray(new AxonStyle[objectOfAxons.size()]);
    }

  public AxonStyle[] getSubjectOfAxonStyles()
    {
	return (AxonStyle[]) subjectOfAxons.toArray(new AxonStyle[subjectOfAxons.size()]);
    }
    
  public void addObjectOfAxonStyle(AxonStyle as)
    {
	objectOfAxons.add(as);
    }

  public void addSubjectOfAxonStyle(AxonStyle as)
    {
	subjectOfAxons.add(as);
    }
  public void removeObjectOfAxonStyle(AxonStyle as)
    {
	objectOfAxons.remove(as);
    }

  public void removeSubjectOfAxonStyle(AxonStyle as)
    {
	subjectOfAxons.remove(as);
    }

  public AxonStyle[] getAxonStyles()
    {
      return (AxonStyle[]) axons.values().toArray(new AxonStyle[axons.size()]);
    }

  public AxonStyle getAxonStyle(String id)
    {
      return (AxonStyle) axons.get(id);
    }


  public AxonStyle addAxonStyle(String axonID, NeuronStyle object)
    throws ReadOnlyException, ConceptMapException
    {
	return addAxonStyle(axonID, this.getURI(), object.getURI());
    }
  
  public AxonStyle addAxonStyle(String axonID, String subjectstyleuri, String objectstyleuri)
    throws ReadOnlyException, ConceptMapException
    {
     GenericConceptMap cMap = (GenericConceptMap) getConceptMap();
      if (!cMap.isEditable())
	throw new ReadOnlyException("");

      //Cannot know if neuronstyle belongs to this map or not.....
      /*      if(!(object instanceof LocalNeuronStyle &&
	   object.getConceptMap() == cMap))
	   throw new ConceptMapException("Tried to add AxonStyle pointing to NeuronStyle in other map!");*/
      
      //      LocalNeuronStyle nso = (LocalNeuronStyle) object;
      //      LocalNeuronStyle nss = (LocalNeuronStyle) subject;

      if(axons.containsKey(axonID))
	throw new ConceptMapException("Axon is already in map.");  

      AxonStyle as = new LocalAxonStyle(axonID, this, subjectstyleuri, objectstyleuri, cMap);
      
      axons.put(axonID, as);
      
      //nso.objectOfAxons.add(as);
      //nss.subjectOfAxons.add(as);

      cMap.fireEditEvent(new EditEvent(cMap, this, AXONSTYLE_ADDED, axonID));
      return as;
    }

  /** Removes the given AxonStyle.
   *  Do not use. Use AxonStyle.remove() instead.
   *
   *  @param axonstyle the AxonStyle to remove.
   */
  protected void removeAxon(AxonStyle axonstyle) 
    {      
      axons.remove(axonstyle.getURI());
    }

  /** Removes the given AxonStyle from this NeuronStyle endOf-list..
   *  Do not use. Use AxonStyle.remove() instead.
   *
   *  @param axonstyle the AxonStyle to remove.
   */
  public void removeObjectOfAxon(AxonStyle axonstyle) 
    {
      objectOfAxons.removeElement(axonstyle);
    }
}



