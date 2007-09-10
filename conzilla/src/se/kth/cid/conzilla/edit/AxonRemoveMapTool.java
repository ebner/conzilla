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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.*;

/** 
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class AxonRemoveMapTool extends MapTool
{

  public AxonRemoveMapTool(String name, MapController cont)
  {
    super(name,Tool.ACTION, cont);
  }
    
  protected boolean updateImpl()
    {
      if ((mapEvent.hitType == MapEvent.HIT_AXONLINE ||
	  mapEvent.hitType == MapEvent.HIT_AXONDATA) &&
	  mapObject.getNeuron() != null &&
	  mapObject.getNeuron().isEditable() &&
	  mapObject.getAxon() != null)
	  return true;
      return false;
    }

  public void activateImpl()
    {
      if (mapObject.getNeuronStyle().getAxonStyles().length == 1 &&
	  !mapObject.getNeuronStyle().getBodyVisible())
	  {
	      if (JOptionPane.showConfirmDialog(null, 
						"This neuron will be removed since invisible neurons can't be handled.",
						"Neuron removal", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
		{
		    mapObject.getNeuron().removeAxon(mapObject.getAxon().getID());
		    mapObject.getNeuronStyle().remove();
		}
	      
	  }
      else
	  {
	      mapObject.getNeuron().removeAxon(mapObject.getAxon().getID());
	      mapObject.getAxonStyle().remove();
	  }
    }
}

