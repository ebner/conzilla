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


package se.kth.cid.conzilla.browse;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

public class BrowseMenu extends Tool
{
  PopupToolSetMenu choice;
  SurfCommandTool  surf;
  //  ViewCommandTool  view;
  //  InfoCommandTool  info;
  //  StoreCommandTool store;

  NeuronMapObject neuronMapObject;
  
  /** Listens for clicks in the map.
   */
  MapEventListener pressListener;

    /** Listens for moves in the map.
   */
  MapEventListener cursorListener;

  protected MapController controller;

  /** The current cursor.
   */
  int cursor = Cursor.DEFAULT_CURSOR;
  
  //  private boolean lock;

  public BrowseMenu(MapController cont)
  {
    super("Browse", Tool.EXCLUSIVE);
    controller = cont;

    choice = new PopupToolSetMenu("Browse");
    surf = new SurfCommandTool(cont);
    //    view = new ViewCommandTool(man,cont);
    //    info = new InfoCommandTool(man,cont);
    //    store = new StoreCommandTool(man,cont);

    choice.addTool(surf);
    //    choice.addTool(view);
    //    choice.addTool(info);
    //    choice.addTool(store);
    
    pressListener = new MapEventListener() {
	public void eventTriggered(MapEvent e)
	{
	  if (e.mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
	    {
	      choice.show(controller.getMapScrollPane().getDisplayer(),
			  e.mouseEvent.getX(),
			  e.mouseEvent.getY());
	    }
	}
    };
    cursorListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  updateState(e);
	}
    };
  }
  
  protected void updateState(MapEvent e)
    {
      NeuronMapObject ne = null;

      if(e.mapObject != null)
	{
	  if(e.mapObject instanceof NeuronMapObject)
	    ne = (NeuronMapObject) e.mapObject;
	  else
	    ne = ((AxonMapObject) e.mapObject).getNeuronMapObject();
	}
      
      if(ne != neuronMapObject)
	{
	  if (neuronMapObject != null)
	    neuronMapObject.setMark(null);
	  neuronMapObject = ne;
	  
	  if(neuronMapObject != null)
	    neuronMapObject.setMark(Color.blue); 
	}
      
      if (!choice.isVisible())
	{
	  surf.updateState(e);
	  //	  view.updateState(e);
	  //	  info.updateState(e);
	  //	  store.updateState(e);

	  boolean isEnabled = surf.isEnabled();
	  
	  if(isEnabled && cursor == Cursor.DEFAULT_CURSOR)
	    {
	      cursor = Cursor.HAND_CURSOR;
	      controller.getMapScrollPane().getDisplayer().setCursor(new Cursor(cursor));
	    }
	  else if(!isEnabled && cursor != Cursor.DEFAULT_CURSOR)
	    {
	      cursor = Cursor.DEFAULT_CURSOR;
	      controller.getMapScrollPane().getDisplayer().setCursor(new Cursor(cursor));
	    }
	}
    }
  
  protected void activateImpl()
    {
      Tracer.debug("Browse active!");
      
      controller.getMapScrollPane().getDisplayer().addMapEventListener(pressListener,
								       MapDisplayer.PRESS_RELEASE);
      controller.getMapScrollPane().getDisplayer().addMapEventListener(cursorListener,
								       MapDisplayer.MOVE_DRAG);
    }
  
  protected void deactivateImpl()
  {
    Tracer.debug("Browse inactive!");

    controller.getMapScrollPane().getDisplayer().removeMapEventListener(pressListener,
									MapDisplayer.PRESS_RELEASE);
    controller.getMapScrollPane().getDisplayer().removeMapEventListener(cursorListener,
									MapDisplayer.MOVE_DRAG);

    neuronMapObject = null;
    //    updateCursor();
  }

  protected void detachImpl()
  {
    surf=null;
    //    view=null;
    //    info=null;
    //    store=null;

    pressListener = null;
    cursorListener = null;
    controller = null;
  }
}
