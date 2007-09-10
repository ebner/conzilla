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


public class LocalAxonType implements AxonType
{
  String  type;

  int     minimumMult;
  int     maximumMult;
  String  lineType;
  int     lineThickness;
  int     lineColor;
  String  headType;
  boolean headFilled;
  int     headSize;

  LocalNeuronType neuronType;
  
  Vector   datatags;
  
  public LocalAxonType(String type, LocalNeuronType neuronType)
    {
      this.type = type;
      this.neuronType = neuronType;

      this.minimumMult        = 0;
      this.maximumMult        = Integer.MAX_VALUE;
      this.lineType           = "continuous";
      this.lineThickness      = 1;
      this.lineColor          = 0;
      this.headType           = "arrow";
      this.headFilled         = true;
      this.headSize           = 10;
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
  
  public void    setMinimumMultiplicity(int mult) throws ReadOnlyException, NeuronException
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");

      if(mult > maximumMult)
	throw new NeuronException("The minimum multiplicity must not" +
				  " be higher than the maximum");

      this.minimumMult = mult;
      neuronType.fireEditEvent(new EditEvent(this, MINIMUMMULTIPLICITY_EDITED,
					     new Integer(mult)));
    }

  public int     getMaximumMultiplicity()
    {
      return maximumMult;
    }
  
  public void    setMaximumMultiplicity(int mult) throws ReadOnlyException, NeuronException
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");

      if(mult < minimumMult)
	throw new NeuronException("The maximum multiplicity must not" +
				  " be less than the minimum");

      this.maximumMult = mult;
      neuronType.fireEditEvent(new EditEvent(this, MAXIMUMMULTIPLICITY_EDITED,
					     new Integer(mult)));
    }
  
  

  
  public String  getLineType()
    {
      return lineType;
    }
  
  public void    setLineType(String type)
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");
      this.lineType = type;

      neuronType.fireEditEvent(new EditEvent(this, LINETYPE_EDITED, type));
    }
  
  public int     getLineThickness()
    {
      return lineThickness;
    }
  
  public void    setLineThickness(int thickness)
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");
      
      this.lineThickness = thickness;

      neuronType.fireEditEvent(new EditEvent(this, LINETHICKNESS_EDITED, new Integer(thickness)));
    }

  public int     getLineColor()
    {
      return lineColor;
    }
  
  public void    setLineColor(int color)
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");
      
      this.lineColor = color;

      neuronType.fireEditEvent(new EditEvent(this, LINECOLOR_EDITED, new Integer(color)));
    }

  
  public String  getHeadType()
    {
      return headType;
    }

  public void    setHeadType(String type)
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");
      
      this.headType = type;

      neuronType.fireEditEvent(new EditEvent(this, HEADTYPE_EDITED, type));
    }

  public boolean getHeadFilled()
    {
      return headFilled;
    }
  
  public void    setHeadFilled(boolean filled)
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");
      
      this.headFilled = filled;

      neuronType.fireEditEvent(new EditEvent(this, HEADFILLED_EDITED,
					     new Boolean(filled)));
    }

  public int     getHeadSize()
    {
      return headSize;
    }
  
  public void    setHeadSize(int size)
    {
      if(!neuronType.isEditable())
	throw new ReadOnlyException("This NeuronType was not editable");
      
      this.headSize = size;

      neuronType.fireEditEvent(new EditEvent(this, HEADSIZE_EDITED, new Integer(size)));
    }
}
