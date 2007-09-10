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

public class LocalAxonStyle implements AxonStyle
{
  LocalNeuronStyle      owner;
  LocalNeuronStyle      end;
  ConceptMap.Position[] line;
  LocalConceptMap       conceptmap;
  String                axonID;

  Vector     datatags;

  /** Creates a LocalAxonStyle. Do not use.
   *  Use NeuronStyle.addAxonStyle() instead.
   */
  protected LocalAxonStyle(LocalNeuronStyle owner, String axonID, 
			LocalNeuronStyle end,
			LocalConceptMap map) 
    {
      this.owner      = owner;
      this.end        = end;
      this.axonID     = axonID;
      this.conceptmap = map;
      line            = new ConceptMap.Position[2];
      line[0]         = new ConceptMap.Position(0, 0);
      line[1]         = new ConceptMap.Position(0, 0);
      datatags        = new Vector();
    }

  public void remove()  throws ReadOnlyException
    {
      if (!conceptmap.isEditable())
	throw new ReadOnlyException("");
      
      end.removeEndOfAxon(this);
      owner.removeAxon(this);

      owner.conceptMap.fireEditEvent(new EditEvent(owner,
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
      conceptmap.fireEditEvent(new EditEvent(this, LINE_EDITED, line));
    }

  public String getAxonID()
    {
      return axonID;
    }
  
  public NeuronStyle getOwner()
    {
      return owner;
    }

  public NeuronStyle getEnd()
    {
      return end;
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
	  conceptmap.fireEditEvent(new EditEvent(this, DATATAG_ADDED, tag));
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
	  conceptmap.fireEditEvent(new EditEvent(this, DATATAG_REMOVED, tag));
	}
    }
}

