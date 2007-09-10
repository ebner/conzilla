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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;


public class EditMenu2 extends MapToolsMenu
{   
  public EditMenu2(MapController controller, AxonEdit axonEdit)
  {
    super(EditMapManagerFactory.EDIT_MENU_AXON, EditMapManagerFactory.class.getName(), controller);


    addMapMenuItem(new RemoveNeuronMapTool(controller), 100);
    
    addSeparator(200);

    addMapMenuItem(new AxonShowMapTool(controller, axonEdit), 300);
    addMapMenuItem(new AxonHideMapTool(controller), 400);
    addMapMenuItem(new AxonCreateMapTool(controller, axonEdit), 500);
    addMapMenuItem(new AxonRemoveMapTool(controller), 600);
    addMapMenuItem(new PathTypeMapTool(controller), 650);

    addSeparator(700);
    
    MapToolsMenu contentMenu = new MapToolsMenu(EditMapManagerFactory.EDIT_MENU_CONTENT,
						EditMapManagerFactory.class.getName(), controller);
    contentMenu.addMapMenuItem(new CreateContentMapTool(controller), 100);

    controller.getConzillaKit().extendMenu(contentMenu, controller);

    addMapMenuItem(contentMenu, 800);
    addMapMenuItem(new NeuronBoxMapTool(controller), 900);
    addMapMenuItem(new MoveToLayerMapTool(controller), 950);
    addMapMenuItem(new BoxLineMapTool(controller), 1000);
    addMapMenuItem(new EditDetailedMapMapTool(controller), 1100);

    addSeparator(1200);
    
    addMapMenuItem(new ViewAlterationTool("VIEW", EditMapManagerFactory.class.getName(), controller), 1300);

    controller.getConzillaKit().extendMenu(this, controller);
    
  }    
}
