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
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;

import java.util.*;


/** An implementation of AxonType to be used by LocalNeuronType.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LocalAxonType implements AxonType
{
  String  type;

  int     minimumMult;
  int     maximumMult;

  NeuronType.LineType lineType;
  NeuronType.HeadType headType;

  LocalNeuronType neuronType;
  
  Vector   datatags;
  
  public LocalAxonType(String type, LocalNeuronType neuronType)
    {
      this.type = type;
      this.neuronType = neuronType;

      this.minimumMult        = 0;
      this.maximumMult        = Integer.MAX_VALUE;

      this.lineType           = new NeuronType.LineType("continuous", 2);
      this.headType           = new NeuronType.HeadType("arrow", true,
							3, 6);

      datatags = new Vector();
    }
  
  

  public NeuronType getNeuronType()
    {
      return neuronType;
    }
  
  public String  getType()
    {
      return type;
    }


  
  public String[] getDataTags()
  {
    String[] tagsArray = new String[datatags.size()];
    datatags.copyInto(tagsArray);
    return tagsArray;
  }
  
  public void     addDataTag(String tag) throws ReadOnlyException
  {
    if (!neuronType.isEditable())
      throw new ReadOnlyException("");

    int index = datatags.indexOf(tag);
    if (index == -1)
      {
	datatags.addElement(tag);
	neuronType.fireEditEvent(new EditEvent(this, DATATAG_ADDED, tag));
      }
  }
  
  public void     removeDataTag(String tag) throws ReadOnlyException
  {
    if (!neuronType.isEditable())
      throw new ReadOnlyException("");

    int index = datatags.indexOf(tag);
    if(index != -1)
      {
	datatags.removeElementAt(index);
	neuronType.fireEditEvent(new EditEvent(this, DATATAG_REMOVED, tag));
      }
  }


  public int     getMinimumMultiplicity()
    {
      return minimumMult;
    }
  
  public void    setMinimumMultiplicity(int mult) throws ReadOnlyException
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");

      if(mult > maximumMult)
	throw new IllegalArgumentException("The minimum multiplicity must not" +
					   " be higher than the maximum");

      this.minimumMult = mult;
      neuronType.fireEditEvent(new EditEvent(this, MINIMUMMULTIPLICITY_EDITED,
					     new Integer(mult)));
    }

  public int     getMaximumMultiplicity()
    {
      return maximumMult;
    }
  
  public void    setMaximumMultiplicity(int mult) throws ReadOnlyException
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");

      if(mult < minimumMult)
	throw new IllegalArgumentException("The maximum multiplicity must not" +
					   " be less than the minimum");

      this.maximumMult = mult;
      neuronType.fireEditEvent(new EditEvent(this, MAXIMUMMULTIPLICITY_EDITED,
					     new Integer(mult)));
    }
  
  

  
  public NeuronType.LineType getLineType()
    {
      return lineType;
    }
  
  public void    setLineType(NeuronType.LineType lineType) throws NeuronException
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");

      if(lineType == null)
	throw new NeuronException("Null line type");
      
      if(lineType.type == null || 
	 lineType.thickness < 1 || lineType.thickness > 10)
	throw new NeuronException("Illegal line type");
      
      this.lineType = lineType;
      
      neuronType.fireEditEvent(new EditEvent(this, LINETYPE_EDITED, lineType));
    }
  
  
  public NeuronType.HeadType getHeadType()
    {
      return headType;
    }

  public void    setHeadType(NeuronType.HeadType headType) throws NeuronException
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");

      if(headType == null)
	throw new NeuronException("Null head type");
      
      if(headType.type == null || 
	 headType.width < 1 || headType.width > 10 ||
	 headType.length < 1 || headType.length > 10)
	throw new NeuronException("Illegal head type");
      
      this.headType = headType;
      

      neuronType.fireEditEvent(new EditEvent(this, HEADTYPE_EDITED, headType));
    }
}
