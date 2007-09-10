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
  
  LocalConceptMap conceptMap;

  String        neuronURI; // might be relative

  String        id;
  
  ////////////NeuronStyle-varibles///////////

  String       detailedMap; // Might be relative
  ConceptMap.BoundingBox  boundingBox;
  String       title;  //Should never be null, only of zero length.
  ConceptMap.Position[]   line;

  Vector     dataTags;

  ////////////AxonStyle-varibles/////////////
  Vector     axons;
  Vector     endOfAxons;

  /////constructors and destructors//////////
  public LocalNeuronStyle(String id, String neuronURI,
			  LocalConceptMap conceptMap)
    throws ConceptMapException, InvalidURIException
    {
      this.conceptMap = conceptMap;
      conceptMap.tryURI(neuronURI);
      this.neuronURI  = neuronURI;
      this.id         = id;
      this.detailedMap = null;
      boundingBox     = new ConceptMap.BoundingBox(new ConceptMap.Dimension(0, 0),
						   new ConceptMap.Position(0, 0));
      title           = "";
      line            = new ConceptMap.Position[0];

      axons           = new Vector();
      endOfAxons      = new Vector();
      dataTags        = new Vector();      
    }
  
  
  /** After the neuronstyle is disconnected all data within are freezed,
   *  any try to edit will result in a serious error, a bug.
   */
  public void remove() throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
      	throw new ReadOnlyException("");

      for (;axons.size() > 0;)
	((AxonStyle) axons.elementAt(axons.size() - 1)).remove();

      for (;endOfAxons.size() > 0;)
	((AxonStyle) endOfAxons.elementAt(endOfAxons.size() - 1)).remove();

      conceptMap.removeNeuronStyle(id);	
      conceptMap = null;
    }


  ///////Non-visual stuff///////
  public ConceptMap getConceptMap()
    {
      return conceptMap;
    }

  public String getID()
    {
      return id;
    }
  
  ////////////NeuronStyle////////////////////
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
      boundingBox = rect;

      conceptMap.fireEditEvent(new EditEvent(this, BOUNDINGBOX_EDITED, rect));
    }
  
  public String getTitle()
    {
      return title;
    }
  
  public void setTitle(String title) throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      this.title = title;

      conceptMap.fireEditEvent(new EditEvent(this, TITLE_EDITED, title));
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
      ConceptMap.Position np[] = new ConceptMap.Position[line.length];

      for (int i=0; i<line.length ; i++)
        np[i] = new ConceptMap.Position(line[i].x, line[i].y);
      return np;
    }

  public void setLine(ConceptMap.Position[] line) throws ReadOnlyException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");
      
      this.line = line;
      conceptMap.fireEditEvent(new EditEvent(this, LINE_EDITED, line));
    }
  
  ////////////AxonStyle-varibles/////////////
  public AxonStyle[] getEndOfAxons()
    {
      return (AxonStyle[]) endOfAxons.toArray(new AxonStyle[endOfAxons.size()]);
    }

  public AxonStyle[] getAxons()
    {
      return (AxonStyle[]) axons.toArray(new AxonStyle[axons.size()]);
    }

  
  public AxonStyle addAxonStyle(AxonStyleId id)
    throws ReadOnlyException, ConceptMapException
    {
      if (!conceptMap.isEditable())
	throw new ReadOnlyException("");

      if(!(id.end instanceof LocalNeuronStyle &&
	   id.end.getConceptMap() == conceptMap))
	throw new ConceptMapException("Tried to add AxonStyle pointing to NeuronStyle in other map!");

      LocalNeuronStyle ns = (LocalNeuronStyle) id.end;
      
      for (int i = 0; i < axons.size(); i++)
	{
	  AxonStyle rs = (AxonStyle) axons.get(i);
	  if (rs.getEnd() == ns && rs.getAxonID().equals(id.axonID))
	    throw new ConceptMapException("Axon is already in map.");  
	}
      AxonStyle rs = new LocalAxonStyle(this, id.axonID, ns, conceptMap);
      
      axons.add(rs);
      
      ns.endOfAxons.add(rs);
      
      conceptMap.fireEditEvent(new EditEvent(this, AXONSTYLE_ADDED, id));
      return rs;
    }

  
  protected void removeAxon(AxonStyle axonstyle) 
  {
    axons.removeElement(axonstyle);
    conceptMap.fireEditEvent(new EditEvent(this, AXONSTYLE_REMOVED,
					   new AxonStyleId(axonstyle.getEnd(),
							   axonstyle.getAxonID())));
  }

  protected void removeEndOfAxon(AxonStyle axonstyle) 
  {
    endOfAxons.removeElement(axonstyle);
  }
}



