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
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.content.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

public class BrowseMenu extends AbstractTool
{
  JPopupMenu choice;
  SurfCommandTool surf;
  InfoCommandTool info;
  StoreCommandTool store;
  ViewCommandTool view;

  /** Listens for clicks in the map.
   */
  MapEventListener pressListener;

  /** Listens for moves in the map.
   */
  MapEventListener cursorListener;

  protected MapController controller;

  /** The manager this tool is attached to.
   */
  protected MapManager manager;

  /** The current cursor.
   */
  int cursor = Cursor.DEFAULT_CURSOR;

  private MapEvent m;
  //  private boolean lock;


  public BrowseMenu(MapManager man, MapController cont)
  {
    super("Browse", Tool.EXCLUSIVE);
    manager=man;
    controller=cont;
    choice= new JPopupMenu();

    surf=new SurfCommandTool(man,cont);
    info=new InfoCommandTool(man,cont);
    store=new StoreCommandTool(man,cont);
    view=new ViewCommandTool(man,cont);
    surf.putToolInMenu(choice);
    info.putToolInMenu(choice);
    store.putToolInMenu(choice);
    view.putToolInMenu(choice);

    pressListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  if (e.mouseevent.getID()==MouseEvent.MOUSE_PRESSED)
	    {
              view.updateAction(e);
	      choice.show(manager.getDisplayer(),e.mouseevent.getX(),
			  e.mouseevent.getY());
	    }
	  /*	  if (!choice.isVisible() &&
	      e.mouseevent.getID()==MouseEvent.MOUSE_PRESSED)
	    {
	      Tracer.debug("NeuronPressed!!");
	      choice.show(manager.getDisplayer(),e.mouseevent.getX(),
			  e.mouseevent.getY());
	      lock=true;
	      m=e;
	      e.consume();
	    }
	  else if (e.mouseevent.getID()==MouseEvent.MOUSE_RELEASED &&
		   !e.mouseevent.getPoint().equals(m.mouseevent.getPoint()))
	    {
	      Tracer.debug("NeuronReleased!!");
	      choice.setVisible(false);
	      MenuSelectionManager.defaultManager().clearSelectedPath();
	      lock=false;
	      e.consume();
	      } */

	}
    };
    cursorListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  updateAction(e);
	}
    };
  }

  protected void updateAction(MapEvent e)
  {
    if (!choice.isVisible())
      {
	boolean bo=surf.updateAction(e);
	info.updateAction(e);
	store.updateAction(e);

	if(bo && cursor == Cursor.DEFAULT_CURSOR)
	  {
	    cursor = Cursor.HAND_CURSOR;
	    manager.getDisplayer().setCursor(new Cursor(cursor));
	  }
	else if(!bo && cursor != Cursor.DEFAULT_CURSOR)
	  {
	    cursor = Cursor.DEFAULT_CURSOR;
	    manager.getDisplayer().setCursor(new Cursor(cursor));
	  }
      }
  }
  protected void activateImpl()
  {
    Tracer.debug("Browse active!");
    //    surf.activate();
    view.activateContentRelated();
    //    info.activate();
    //    store.activate();

    manager.getDisplayer().addMapEventListener(pressListener,
					       MapDisplayer.PRESS_RELEASE);
    manager.getDisplayer().addMapEventListener(cursorListener,
    					       MapDisplayer.MOVE_DRAGG);
  }

  protected void deactivateImpl()
  {
    Tracer.debug("Browse inactive!");
    //    surf.deactivate();
    view.deactivateContentRelated();
    //    info.deactivate();
    //    store.deactivate();

    manager.getDisplayer().removeMapEventListener(pressListener,
						  MapDisplayer.PRESS_RELEASE);
    manager.getDisplayer().removeMapEventListener(cursorListener,
						  MapDisplayer.MOVE_DRAGG);
    //    updateCursor();
  }

  protected void detachImpl()
  {
    surf=null;
    store=null;
    info=null;
    view=null;
    pressListener = null;
    cursorListener = null;
    controller = null;
    manager = null;
  }
}
