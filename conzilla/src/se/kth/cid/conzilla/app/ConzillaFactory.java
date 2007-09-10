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


package se.kth.cid.conzilla.app;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

class ConzillaFactory
{
  ConzillaKit kit;

  public ConzillaFactory(ConzillaKit kit)
    {
      this.kit = kit;
    }
  
  public ConzillaWindow createBrowseWindow(URI map)
    throws ControllerException
    {
      MapController controller = new MapController(kit, new HistoryManager(),
						   new ListContentSelector(),
						   new BrowseMapManagerFactory());
      
      ConzillaWindow cw = new ConzillaWindow(controller);

      addMenus(cw);

      controller.showMap(map);

      cw.pack();
      cw.show();
      
      return cw;
    }

  public ConzillaWindow createEditWindow(URI map)
    throws ControllerException
    {
      MapController controller = new MapController(kit, new HistoryManager(),
						   new ListContentSelector(),
						   new BrowseMapManagerFactory());
      
      ConzillaWindow cw = new ConzillaWindow(controller);

      addMenus(cw);

      controller.showMap(map);

      cw.pack();
      cw.show();
      
      return cw;
    }

  private void addMenus(final ConzillaWindow cw)
    {
      JMenu file = new JMenu("File");

      Tracer.debug("cw: " + cw);
      
      file.add(new AbstractAction("Open new map")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Opens a new map in existing window.");
	      //	      conzillawindow.opentool.open();
	    }
	});

      file.add(new AbstractAction("New window")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Create a new window");
	      kit.getConzilla().clone(cw);
	    }
	});
      
      file.add(new AbstractAction("Close this window")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Close this window");
	      kit.getConzilla().close(cw);
	    }
	});
      
      file.add(new AbstractAction("Exit")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Exits the whole application.");
	      kit.getConzilla().exit(0);
	    }
	});
      
      JMenu extras = new JMenu("Extras");
      extras.add(new AbstractAction("Close contentselection view")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Close contentselection view");
	      cw.getController().getContentSelector().selectContent(null);
	    }
	});

      JMenuBar mBar = new JMenuBar();
      mBar.add(file);
      mBar.add(extras);
      cw.setJMenuBar(mBar);
    }
}
