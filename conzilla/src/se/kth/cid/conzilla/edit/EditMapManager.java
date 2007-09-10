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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;


public class EditMapManager extends LayerManager implements MapManager, PropertyChangeListener
{
    GridTool grid;
    LineTool line;
    TieTool tie;
    Tool save;
    GridModel gridModel;
    CreateTool create;

    //Two default layers.
    protected MoveLayer moveLayer;
    protected GridLayer gridLayer;



    MapToolsMenu menu1;
    MapToolsMenu menu2;
    MapToolsMenu menu3;

    public EditMapManager()
    {
    }  

    public void install(MapController c)
    {
	super.install(c);
	controller.addPropertyChangeListener(this);
	
	ToolsBar bar = c.getToolsBar();

	gridModel = new GridModel(6);
	grid = new GridTool(gridModel);
	line = new LineTool();
	tie  = new TieTool();
	save = new SaveTool(controller);
	create = new CreateTool(controller, this, gridModel);
	gridLayer = new GridLayer(controller);
	moveLayer = new MoveLayer(controller, line, tie);
	
	//GridLayer should be at the bottom, then moveLayer next.
	push(gridLayer);
	push(moveLayer);
	
	AxonEdit axonEdit = new AxonEdit(controller, this); 
	
	menu1     = new EditMenu1(controller, axonEdit);
	menu2     = new EditMenu2(controller, axonEdit);
	menu3     = new EditMenu3(controller, this);

	grid.installYourself(bar);
	bar.addTool(line);
	bar.addTool(tie);
	bar.addTool(save);
	create.installYourself(bar);

	//Initialize layers.
	install(controller.getMapScrollPane());

	controller.getMapScrollPane().getDisplayer().setDisplayLanguageDiscrepancy(true);
    }
    
    public GridModel getGridModel()
    {
	return gridModel;
    }
  
    public void deInstall(MapController c)
    {
	controller.removePropertyChangeListener(this);

	//uninstall layers.
	uninstall(c.getMapScrollPane());
	
	ToolsBar bar = c.getToolsBar();
	
	bar.removeTool(save);
	save.detach();
	
	grid.removeYourself(bar);
	grid.detach();
	
	bar.removeTool(line);
	line.detach();
	
	bar.removeTool(tie);
	tie.detach();      
	
	create.removeYourself(bar);
	create.detach();

	controller.getMapScrollPane().getDisplayer().setDisplayLanguageDiscrepancy(false);
    }

    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(MapController.MAP_PROPERTY))
	    {
		uninstall((MapScrollPane) e.getOldValue());
		install((MapScrollPane) e.getNewValue());
	    }
    }

  public void eventTriggeredImpl(MapEvent m)
  {
      if (m.mouseEvent.isPopupTrigger() && !m.isConsumed())
	  {
	      switch (m.hitType)
		  {
		  case MapEvent.HIT_BOX:
		  case MapEvent.HIT_BOXTITLE:
		  case MapEvent.HIT_BOXDATA:
		      menu1.popup(m);
		      break;
		  case MapEvent.HIT_BOXLINE:
		  case MapEvent.HIT_AXONLINE:
		  case MapEvent.HIT_AXONDATA:		 
		      menu2.popup(m);
		      break;
		  case MapEvent.HIT_NONE:
		      menu3.popup(m);
		      break;
		  }
	      m.consume();
	  }
  }
}
