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
  
  ///////////RoleTypes-variables////
  Vector   vroletypes;    
  RoleType roletypes[];
  
  public LocalNeuronType()
  {
    vroletypes=new Vector();
    
    roletypes=null;
    datatags=new Vector();
  }  

  ///////////BoxStyle////////////
  
  public String  getBox()
    {
      return box;
    }
  
  public void setBox(String box) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      this.box=box;
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
  
  public void setLineThickness(int thick) throws LineThicknessException, ReadOnlyException
    {
      if (thick>10 || thick<0)
	throw new LineThicknessException("");
      if (!isEditable())
	throw new ReadOnlyException("");
      linethickness=thick;
      fireEditEvent(new EditEvent(this, LINETHICKNESS_EDITED, new Integer(thick)));
    }
  
  public int     getLineColor()
    {
      return linecolor;
    }

  public void    setLineColor(int color) throws ReadOnlyException
  {
    if (!isEditable())
      throw new ReadOnlyException("");
    linecolor=color;
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
    int index=datatags.indexOf(tag);
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

  
  ///////////RoleTypes//////////////
  public int      getDegree()
    {
      return vroletypes.size();
    }
  
  public RoleType[] getRoleTypes()
    {
      if (roletypes==null)
	{
	  roletypes=new RoleType[getDegree()];
	  Enumeration en=vroletypes.elements();
	  for(int i=0; en.hasMoreElements();i++)
	    roletypes[i]=(RoleType) en.nextElement();
	}
      return roletypes;
    }
  
  public RoleType getRoleType(String type)
    {
      int index=vroletypes.indexOf(new RoleType(type));
      if (index != -1)
	return (RoleType) vroletypes.elementAt(index);
      return null;
    }
  
  public void addRoleType(RoleType roletype) throws NeuronException, ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      if (roletype.linethickness > 10 || roletype.linethickness < 0)
	throw new LineThicknessException("");   
      roletypes=null;
      int index=vroletypes.indexOf(roletype);
      if (index != -1)
	vroletypes.setElementAt(roletype,index);
      else
	vroletypes.addElement(roletype);
      fireEditEvent(new EditEvent(this, ROLETYPE_ADDED, roletype));
    }
  
  public void removeRoleType(RoleType roletype) throws ReadOnlyException
    {
      if (!isEditable())
	throw new ReadOnlyException("");
      int index=vroletypes.indexOf(roletype);
      if (index != -1)
	{
	  vroletypes.removeElementAt(index);
	  roletypes=null;
	  fireEditEvent(new EditEvent(this, ROLETYPE_REMOVED, roletype));
	}
    }
}



