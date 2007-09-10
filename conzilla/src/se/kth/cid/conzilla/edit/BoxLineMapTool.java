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
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.awt.event.*;

public class BoxLineMapTool extends ActionMapMenuTool
{

    static final String LINE_VISIBLE = "HIDE_NEURONLINE";
    static final String LINE_INVISIBLE = "SHOW_NEURONLINE";

  String currentName;

  public BoxLineMapTool(MapController cont)
  {
    super(LINE_VISIBLE, EditMapManagerFactory.class.getName(), cont);

    currentName = LINE_VISIBLE;
  }
    

  protected boolean updateEnabled()
    {
	if (mapEvent==null || mapObject==null || mapObject.getNeuronStyle().getLine()==null)
	    currentName = LINE_INVISIBLE;
	else
	    currentName = LINE_VISIBLE;

	ConzillaResourceManager.getDefaultManager().customizeButton(getJMenuItem(), EditMapManagerFactory.class.getName(), currentName);


      if (mapEvent.hitType==MapEvent.HIT_NONE)
	return false;
      return true;
    }

  public void actionPerformed(ActionEvent e)
    {
      if (mapObject.getNeuronStyle().getLine()!=null)
	  if (!mapObject.getNeuronStyle().getBodyVisible() &&
	      mapObject.getNeuronStyle().getAxonStyles().length == 0)
	      {
		  if (JOptionPane.showConfirmDialog(null, 
						    "This neuron will be removed since invisible neurons can't be handled.",
						    "Neuron removal", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
		      mapObject.getNeuronStyle().remove();
	      }
	  else
	      mapObject.getNeuronStyle().setLine(null);
      else
	{

	    ConceptMap.Position [] pos=LayoutUtils.boxLine(mapObject.getNeuronStyle(), mapEvent,((EditMapManager) controller.getManager()).getGridModel());
	    mapObject.getNeuronStyle().setLine(pos);
	}
    }
}

