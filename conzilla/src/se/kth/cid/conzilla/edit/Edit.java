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
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.JMenu;

public class Edit extends AbstractTool implements  MapEventListener
{

  protected MapController controller;
  protected MoveLayer moveLayer;
  protected GridLayer gridLayer;

  ToolMenu menu1;
  ToolMenu menu2;
  ToolMenu menu3;

  public Edit(String name, MapController controller, Tool lineTool, Tool tieTool)
  {
    super(name,Tool.EXCLUSIVE);
    this.controller=controller;

    gridLayer = new GridLayer(controller);
    moveLayer = new MoveLayer(controller, (LineTool) lineTool, (TieTool) tieTool);

    AxonEdit axonEdit = new AxonEdit("AxonEdit", controller, this); 
    
    menu1     = (ToolMenu) new EditMenu1(name,controller, axonEdit);
    menu2     = (ToolMenu) new EditMenu2(name,controller, axonEdit);
    menu3     = (ToolMenu) new EditMenu3(name,controller);
  }
  
  protected void activateImpl()
  {
    moveLayer.activate();
    gridLayer.activate();
    controller.getMapScrollPane().getLayeredPane().add(moveLayer, MapScrollPane.EDIT_LAYER);

    MapDisplayer mapDisplayer=controller.getMapScrollPane().getDisplayer();
    //    mapDisplayer.setOpaque(false);
    mapDisplayer.addMapEventListener(this,MapDisplayer.MOVE_DRAG);
    mapDisplayer.addMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    mapDisplayer.addMapEventListener(this,MapDisplayer.CLICK);
  }
  
  protected void deactivateImpl()
  {
    MapDisplayer mapDisplayer=controller.getMapScrollPane().getDisplayer();
    mapDisplayer.removeMapEventListener(this,MapDisplayer.MOVE_DRAG);
    mapDisplayer.removeMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    mapDisplayer.removeMapEventListener(this,MapDisplayer.CLICK);
    //    mapDisplayer.setOpaque(true);

    int ind = controller.getMapScrollPane().getLayeredPane().getIndexOf(moveLayer);
    controller.getMapScrollPane().getLayeredPane().remove(ind);
    gridLayer.deactivate();
    moveLayer.deactivate();
  }

 public void eventTriggered(MapEvent m)
  {
    if (m.mouseEvent.isPopupTrigger() && !m.isConsumed())
       {
	   switch (m.hitType)
	     {
	     case MapEvent.HIT_BOX:
	     case MapEvent.HIT_BOXTITLE:
	     case MapEvent.HIT_BOXDATA:
		 menu1.update(m);
		 menu1.activate();
		 break;
	     case MapEvent.HIT_BOXLINE:
	     case MapEvent.HIT_AXONLINE:
	     case MapEvent.HIT_AXONDATA:		 
		 menu2.update(m);
		 menu2.activate();
		 break;
	     case MapEvent.HIT_NONE:
		 menu3.update(m);



		 menu3.activate();
		 break;
	     }
	   m.consume();
       }

    
    if (!m.isConsumed() ) 
	switch (m.mouseEvent.getID())
	    {
	    case MouseEvent.MOUSE_MOVED:
		moveLayer.mouseMoved(m);
		break;
	    case MouseEvent.MOUSE_DRAGGED:
		moveLayer.mouseDragged(m);
		break;
	    case MouseEvent.MOUSE_PRESSED:
		moveLayer.mousePressed(m);
		break;
	    case MouseEvent.MOUSE_RELEASED:
		moveLayer.mouseReleased(m);
		break;
	    case MouseEvent.MOUSE_CLICKED:
		moveLayer.mouseClicked(m);
		break;
	    }
    
  }

  protected void detachImpl()
  {
    controller=null;
    menu1=null;
    menu2=null;
    menu3=null;
  }
}
