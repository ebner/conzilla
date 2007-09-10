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
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class SaveTool extends Tool implements EditListener
{
  MapController controller;

  boolean saving = false;
  
  public SaveTool(MapController cont)
    {
      super("SAVE", EditMapManagerFactory.class.getName());
      setIcon(new ImageIcon(SaveTool.class.getResource("/graphics/toolbarButtonGraphics/general/Save16.gif")));
      controller = cont;

      MapStoreManager storeManager = controller.getMapScrollPane().getDisplayer().getStoreManager();
      storeManager.addEditListener(this);

      updateEnabled();
    }

  void updateEnabled()
    {
      MapStoreManager storeManager = controller.getMapScrollPane().getDisplayer().getStoreManager();

      boolean enable = false;
      
      ConceptMap cm = storeManager.getConceptMap();
      if(cm.isEdited())
	enable = true;
      NeuronStyle[] ns = cm.getNeuronStyles();
      for(int i = 0; i < ns.length; i++)
	{
	  Neuron n = storeManager.getNeuron(ns[i].getURI());
	  if(n != null && n.isEdited())
	    enable = true;
	  NeuronType nt = storeManager.getNeuronType(ns[i].getURI());
	  if(nt != null && nt.isEdited())
	    enable = true;
	}
      setEnabled(enable);
    }
  
  
  public void componentEdited(EditEvent e)
    {
      if(!saving)
	updateEnabled();
    }
  
    public void actionPerformed(ActionEvent e)
    {
    ComponentStore store=controller.getConzillaKit().getComponentStore();
    ConceptMap cmap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();

    try {
      if (cmap.isEdited())
	store.getHandler().saveComponent(cmap);
    } catch (ComponentException ce)
      {
	ErrorMessage.showError("Save Error",
			       "Failed to save conceptmap\n\n"
			       + cmap.getURI(),
			       ce,
			       controller.getMapScrollPane().getDisplayer());
      }

    saving = true;
    
    NeuronStyle nss[] = cmap.getNeuronStyles();
    Neuron neuron;
    for(int i = 0; i < nss.length; i++)
      {
	neuron = controller.getMapScrollPane().getDisplayer().getStoreManager().getNeuron(nss[i].getURI());
	try {
	  if (neuron != null && neuron.isEdited())
	    {
	      store.getHandler().saveComponent(neuron);
	    }
	} catch(ComponentException ce) {
	  //		  ce.printStackTrace();
	  ErrorMessage.showError("Save Error",
				 "Failed to save neuron\n\n"
				 + nss[i].getNeuronURI(),
				 ce,
				 controller.getMapScrollPane().getDisplayer());
	  
	}
      }
    saving = false;
    updateEnabled();
  }


    public void detach()
  {
    controller.getMapScrollPane().getDisplayer().getStoreManager().removeEditListener(this);
    controller = null;
  }
  
}

