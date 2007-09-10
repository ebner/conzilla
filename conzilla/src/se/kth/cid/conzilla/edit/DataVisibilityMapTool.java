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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class DataVisibilityMapTool extends ToolMenu
{  
  class DataItemVisibleTool extends MapTool
  {
    NeuronStyle neuronStyle;
    public DataItemVisibleTool(String name, MapController cont, NeuronStyle ns, boolean activated)
    {
      super(name, Tool.STATE, cont);
      this.activated=activated;
	  
      neuronStyle = ns;
    }
    
    public boolean updateImpl()
    { 
      return true; 
    }
      
    public void activateImpl()
    {
      neuronStyle.addDataTag(this.getName());
    }
      
    public void deactivateImpl()
      {
	  neuronStyle.removeDataTag(this.getName());
      }
  }

  /** Constructs an DataVisibilityMapTool.
   */
  public DataVisibilityMapTool(String name, MapController cont)
  {
    super(name, cont);
  }

  private boolean contains(String [] strs, String str)
  {
    for (int i=0;i<strs.length;i++)
	{
	    if (strs[i].equals(str))
		{
		    return true;
		}
	}
    return false;
  }

  public void update(Object o)
  {
    if ( o != null && o instanceof MapEvent)
      {
	mapEvent=(MapEvent) o;
	if (!choice.getPopupMenu().isVisible() && mapEvent.mapObject!=null  && 
	    mapEvent.mapObject.getNeuronType()!=null )
	    {
		choice.removeAll();
		String [] visible=mapEvent.mapObject.getNeuronStyle().getDataTags();
		String [] strs=mapEvent.mapObject.getNeuronType().getDataTags();
		for (int i=0;i<strs.length;i++)
		    {
			DataItemVisibleTool divt= new DataItemVisibleTool(strs[i], controller,
			      mapEvent.mapObject.getNeuronStyle(),contains(visible,strs[i]));
			choice.addTool(divt);
		    }
		if (choice.getItemCount()!=0)
		    choice.setEnabled(true); 
		else
		    choice.setEnabled(false);
		choice.update(mapEvent);
	    }
	else choice.setEnabled(false);
	    
      }
    else
	choice.setEnabled(false);
  }
}
