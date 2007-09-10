/* $Id$:*/
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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.library.*;
import javax.swing.*;

class InsertClipboardNeuronMapTool extends InsertMapTool
{

  GridModel gridModel;

  public InsertClipboardNeuronMapTool(String name, MapController cont)
  {
    super(name, cont);
    if (! (controller.getManager() instanceof EditMapManager) )
	Tracer.bug("MapManager in controller isn't a EditMapManager despite the fact that we are in edit mode");
    gridModel=((EditMapManager) controller.getManager()).getGridModel();

  }
    
  protected boolean updateImpl()
  {
    ClipboardLibrary cl=controller.getConzillaKit().getConzillaEnvironment().getRootLibrary().getClipboardLibrary();
    return (mapEvent.hitType==MapEvent.HIT_NONE && 
	    cl.getNeuron()!=null);
  }

  public void activateImpl()
  {
      ClipboardLibrary cl=controller.getConzillaKit().getConzillaEnvironment().getRootLibrary().getClipboardLibrary();
      Neuron neuron=cl.getNeuron();
      if (neuron==null)
	  return;
      try {
	  
	  NeuronStyle ns=makeNeuronStyle(neuron);
	
	  java.awt.Dimension dim=controller.getMapScrollPane().getDisplayer().getNeuronMapObject(ns.getID()).getPreferredSize();
	  ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, 
						       mapEvent.mapX,
						       mapEvent.mapY,
						       dim));
	  showAxons(ns, neuron);
      } catch (ReadOnlyException re) {
	  Tracer.bug("Map can't be edited but we are in edit mode... \n"+
		     re.getMessage());
	} catch (InvalidURIException iue) 
	    {
		ErrorMessage.showError("Can't paste neuron.", "Neuron don't seem to have a valid URI."
				       +"Can't create a graphical representation for it.", iue,controller.getMapScrollPane().getDisplayer()); 
	    }
  }
}

