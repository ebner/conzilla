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
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;

import java.util.*;

public class LocalAxonStyle implements ExtendedAxonStyle
{
  LocalNeuronStyle      owner;
  String                subjecturi;
  String                objecturi;
  NeuronStyle           subject;
  NeuronStyle           object;
  ConceptMap.Position[] line;
  int                   pathType;
  GenericConceptMap     conceptmap;
  String                axonID;

  Vector                datatags;

  /** Creates a LocalAxonStyle. Do not use.
   *  Use NeuronStyle.addAxonStyle() instead.
   */
  protected LocalAxonStyle(String axonID,
			   LocalNeuronStyle owner, 
			   String subjecturi, 
			   String objecturi,
			   GenericConceptMap map)
    {
      this.owner      = owner;
      this.subjecturi = subjecturi;
      this.objecturi  = objecturi;
      this.subject    = null;
      this.object     = null;
      this.axonID     = axonID;
      this.conceptmap = map;
      line            = new ConceptMap.Position[2];
      line[0]         = new ConceptMap.Position(0, 0);
      line[1]         = new ConceptMap.Position(0, 0);
      pathType        = AxonStyle.PATH_TYPE_STRAIGHT;
      datatags        = new Vector();
      conceptmap.addAxonStyleEnds(this);
    }

  public void remove()  throws ReadOnlyException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      
      conceptmap.removeAxonStyleEnds(this);
      owner.removeAxon(this);
      conceptmap.fireEditEvent(new EditEvent(conceptmap, owner,
					     NeuronStyle.AXONSTYLE_REMOVED,
					     axonID));
    }

  public ConceptMap.Position[] getLine()
    {
      return line;
    }

  public void setLine(ConceptMap.Position[] line) throws ReadOnlyException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      
      if(line.length < 2)
	throw new IllegalArgumentException("Too few line elements");

      this.line = line;
      conceptmap.fireEditEvent(new EditEvent(conceptmap, this, LINE_EDITED, line));
    }

    public int getPathType()
    {
	return pathType;
    }
    public void setPathType(int pt)
    {
	pathType = pt;
    }

  public String getURI()
    {
      return axonID;
    }
  
  public ConceptMap getConceptMap()
   {
     return conceptmap;
   }

  public NeuronStyle getOwner()
    {
      return owner;
    }

  public NeuronStyle getObject()
    {
      return object;
    }

  public NeuronStyle getSubject()
    {
      return subject;
    }

  public String getObjectURI()
    {
      return objecturi;
    }

  public String getSubjectURI()
    {
      return subjecturi;
    }

  public void setSubject(NeuronStyle subject)
    {
	this.subject = subject;
    }

  public void setObject(NeuronStyle object)
    {
	this.object = object;
    }
  
  public String[] getDataTags()
    {
      return (String[]) datatags.toArray(new String[datatags.size()]);
    }
  
  public void     addDataTag(String tag) throws ReadOnlyException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      
      int index = datatags.indexOf(tag);
      if (index == -1)
	{
	  datatags.addElement(tag);
	  conceptmap.fireEditEvent(new EditEvent(conceptmap, this, DATATAG_ADDED, tag));
	}
    }
  
  public void     removeDataTag(String tag) throws ReadOnlyException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      
      int index = datatags.indexOf(tag);
      
      if(index != -1)
	{
	  datatags.removeElement(tag);
	  conceptmap.fireEditEvent(new EditEvent(conceptmap, this, DATATAG_REMOVED, tag));
	}
    }
}

