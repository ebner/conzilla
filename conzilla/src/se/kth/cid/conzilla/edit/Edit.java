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
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.util.*;

public class Edit extends LayerManager
{
  protected MoveLayer moveLayer;
  protected GridLayer gridLayer;

  MapToolsMenu menu1;
  MapToolsMenu menu2;
  MapToolsMenu menu3;

  public Edit(MapController controller, LineTool lineTool, TieTool tieTool, EditMapManager mm)
  {
      super(controller);


      gridLayer = new GridLayer(controller);
      moveLayer = new MoveLayer(controller, lineTool, tieTool);
      
      //GridLayer should be at the bottom, then moveLayer next.
      push(gridLayer);
      push(moveLayer);
      
      AxonEdit axonEdit = new AxonEdit(controller, this); 
      
      menu1     = new EditMenu1(controller, axonEdit);
      menu2     = new EditMenu2(controller, axonEdit);
      menu3     = new EditMenu3(controller, mm);
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
