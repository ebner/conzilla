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
import se.kth.cid.conzilla.edit.NeuronDisplayerMapTool;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

public class BrowseMenu extends ToolMenu
{
  Tool surf;
  Tool view;
  MapTool info;
  MapTool store;  
  MapTool edit;

  public BrowseMenu(String name, MapController cont)
  {
    super(name, cont);

    surf = new SurfAlterationTool("SURF", cont);
    view = new ViewAlterationTool("VIEW",cont);
    info = new InfoMapTool("INFO",cont);
    store = new StoreMapTool("COPY",cont);
    edit = new NeuronDisplayerMapTool("EDIT", cont);

    choice.addTool(surf);
    choice.addTool(view);
    choice.addTool(info);
    choice.addTool(store);
    choice.addTool(edit);
  }
  
  protected void detachImpl()
  {
    super.detachImpl();
    surf.detach();
    view.detach();
    info.detach();
    store.detach();
    edit.detach();

    surf=null;
    view=null;
    info=null;
    store=null;
    edit=null;
  }
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
