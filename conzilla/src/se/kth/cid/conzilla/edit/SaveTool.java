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
import se.kth.cid.content.*;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class SaveTool extends AbstractTool
{
  MapManager manager;
  MapController controller;
  
  java.awt.Component dialogParent;
  
  public SaveTool(MapManager man, MapController cont, java.awt.Component dialogParent)
  {
    super("Save", Tool.ACTION);
    manager = man;
    controller = cont;
    this.dialogParent = dialogParent;    
  }

  protected void activateImpl()
  {
    Tracer.debug("Save!");

    ComponentSaver saver =controller.getComponentSaver();
    if(saver != null)
      saveComponents(saver);
  }

  protected void deactivateImpl()
  {}

  protected void detachImpl()
  {
    manager = null;
    controller = null;
  }
  
  void saveComponents(ComponentSaver saver)
  {

    ConceptMap map = manager.getDisplayer().getMap();

    maybeSaveComponent(map, saver);

    Enumeration en = map.getNeuronStyles();

    for(; en.hasMoreElements();)
      {
	NeuronStyle ns = (NeuronStyle) en.nextElement();
	maybeSaveComponent(ns.getNeuron(), saver);
	maybeSaveComponent(ns.getNeuronType(), saver);
      }
  }

  void maybeSaveComponent(se.kth.cid.component.Component c,
			  ComponentSaver saver)
  {
    if(!c.isEdited() ||
       (!c.isEditable() && !c.isEditingPossible()))
      return;
    
    try{
      Tracer.debug("Saving component: " + c.getURI());
      saver.saveComponent(c);

      boolean b = c.isEditable();

      if(!b)
	c.setEditable(true);

      c.setEdited(false);

      if(!b)
	c.setEditable(false);
    } catch(ComponentException e)
      {
	TextOptionPane.showError(dialogParent, "Failed to save component "
				 + c.getURI() + ":\n "
				 + e.getMessage());
      }
  }
}

