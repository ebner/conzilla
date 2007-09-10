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

package se.kth.cid.conzilla.controller;

import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.test.*;

import java.awt.*;
import javax.swing.*;

public class BasicToolFactory implements ToolFactory
{
  Tool suckIn;
  Tool open;

  LinearHistoryManager historyManager;

  Component dialogParent;  
  
  MapController controller;
  
  public BasicToolFactory(MapController cont, Component dialogParent)
  {
    controller = cont;
    this.dialogParent = dialogParent;
    suckIn = new SuckInTool(cont, dialogParent);
    //    open = new OpenTool(cont, dialogParent);
    historyManager = new LinearHistoryManager(cont, dialogParent);
  }

  public void makeTools(MapManager manager)
  {
    manager.addToolBar(new ToolBarFactory("Browse", manager, this));
    manager.addToolBar(new ToolBarFactory("Edit", manager, this));
    manager.setActiveToolBar("Browse");
  }
  
  public ToolSetBar newToolBar(MapManager manager, String name, ToolSetBar tsb)
  {
    ToolSet toolSet = manager.getToolSet();
    ToolSetBar newBar;
    if (tsb==null)
      newBar=new ToolSetBar(name);
    else
      newBar=tsb;
    Tool tool;
    
    
    if(name.equals("BrowseMenu"))
      {
	tool = new BrowseMenu(manager, controller);
	newBar.addTool(tool);
	toolSet.addTool(tool);

	return newBar;
      }
    
    if (name.equals("LinearHistory"))
      {
	historyManager.addTools(newBar);
	return newBar;
      }
	
    if(name.equals("Browse"))
      {
	newToolBar(manager,"BrowseMenu", newBar);
	
	newBar.addTool(suckIn);

//	  tool = new LibraryTool("Library",controller);
//	  newBar.addTool(tool);
//	  toolSet.addTool(tool);

	historyManager.addTools(newBar);

	return newBar;
      }
    
    if(name.equals("Edit"))
      {
	GridTool grid = new GridTool("Grid");
	LineTool line = new LineTool("Line");
	
	tool = (Tool) new EditConceptMapTool("Move",controller,manager,
					     new MoveLayer(controller,manager,grid,line));
	newBar.addTool(tool);
	toolSet.addTool(tool);
	
	newBar.addTool(line);
	toolSet.addTool(line);
	
	newBar.addTool(grid);
	toolSet.addTool(grid);

	tool = (Tool) new TextEditTool("TextEdit",controller ,manager);
	newBar.addTool(tool);
	toolSet.addTool(tool);

//	  tool = new LibraryTool("Library",controller);
//	  newBar.addTool(tool);
//	  toolSet.addTool(tool);
	
	tool = new SaveTool(manager, controller, dialogParent);
	newBar.addTool(tool);
	toolSet.addTool(tool);

	tool = toolSet.getTool("MetaData");
	if(tool != null)
	  newBar.addTool(tool);

	return newBar;
      }
    return null;
  }

  public void detachToolBar(ToolSetBar toolSetBar)
  {
    if(toolSetBar.getName().equals("Browse"))
      {
	historyManager.detachTools(toolSetBar);
      }
  }
}
