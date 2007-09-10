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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class EditMapManager implements MapManager, EditListener
{
  LinearHistoryManager historyManager;
    
  ConzillaWindow window;

  MapController controller;
  
  Tool edit;
  GridTool grid;
  Tool line;
  Tool tie;
  Tool save;
  ToolSet toolSet;
  GridModel gridModel;

  public EditMapManager(MapController controller)
  {
    this.controller = controller;
  }
  
  public void setConzillaWindow(ConzillaWindow window)
    {
      this.window=window;
    }

  public void initialize(ToolSetBar bar)
    {
      gridModel = new GridModel(6);
      grid = new GridTool("Grid", gridModel);
      line = new LineTool("Handle");
      tie  = new TieTool("Tie objects");
      edit = new Edit("Edit", controller, line, tie);
      save = new SaveTool("Save",controller);
      
      toolSet = new ToolSet();
      toolSet.addTool(edit);

      addTools(bar);
      //Add menus to Conzillawindow here.
      edit.activate();

      ConceptMap cm = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      
      controller.getConzillaKit().getComponentEdit().editComponent(cm, false);
      cm.addEditListener(this);
      controller.getMapScrollPane().getDisplayer().setDisplayLanguageDiscrepancy(true);
    }

  public GridModel getGridModel()
    {
	return gridModel;
    }
  
  public void deInitialize(ToolSetBar bar)
    {
      //Remove menus from ConzillaWindow here.
      edit.deactivate();
      removeTools(bar);
      
      ConceptMap cm = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      
      controller.getConzillaKit().getComponentEdit().editComponent(cm, false);
      cm.removeEditListener(this);
      controller.getMapScrollPane().getDisplayer().setDisplayLanguageDiscrepancy(false);
    }

  public void componentEdited(EditEvent e)
    {
      ConceptMap cm = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      
      controller.getConzillaKit().getComponentEdit().editComponent(cm, false);
    }

  
  protected void addTools(ToolSetBar bar)
  {
    //      bar.addTool(edit);
    grid.installYourself(bar);
    bar.addTool(line);
    bar.addTool(tie);
    bar.addTool(save);
  }  
  protected void removeTools(ToolSetBar bar)
  {
    bar.removeTool(save);
    save.detach();
    grid.removeYourself(bar);
    grid.detach();
    bar.removeTool(line);
    line.detach();
    bar.removeTool(tie);
    tie.detach();
  }
}
