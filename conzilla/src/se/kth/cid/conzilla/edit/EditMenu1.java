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
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;


public class EditMenu1 extends MapToolsMenu
{
    public EditMenu1(MapController controller, AxonEdit axonEdit)
  {
    super(EditMapManagerFactory.EDIT_MENU_NEURON, EditMapManagerFactory.class.getName(), controller);

    addMapMenuItem(new RemoveNeuronMapTool(controller), 100);

    addSeparator(200);
    
    addMapMenuItem(new AxonShowMapTool(controller, axonEdit), 300);
    addMapMenuItem(new AxonCreateMapTool(controller, axonEdit), 400);

    addSeparator(500);
    
    MapToolsMenu contentMenu = new MapToolsMenu(EditMapManagerFactory.EDIT_MENU_CONTENT, 
						EditMapManagerFactory.class.getName(), controller);
    contentMenu.addMapMenuItem(new CreateContentMapTool(controller), 100);

    controller.getConzillaKit().extendMenu(contentMenu, controller);
    
    addMapMenuItem(contentMenu, 500);
    addMapMenuItem(new DataVisibilityMapTool(controller), 600);
    addMapMenuItem(new NeuronBoxMapTool(controller), 700);
    addMapMenuItem(new MoveToLayerMapTool(controller), 750);
    addMapMenuItem(new BoxLineMapTool(controller), 800);

    addMapMenuItem(new EditDetailedMapMapTool(controller), 900);

    addMapMenuItem(new TextAnchorMapTool(controller), 950);
    addSeparator(1000);

    addMapMenuItem(new ViewAlterationTool("VIEW", EditMapManagerFactory.class.getName(), controller), 1000);

    controller.getConzillaKit().extendMenu(this, controller);
  }    
}
