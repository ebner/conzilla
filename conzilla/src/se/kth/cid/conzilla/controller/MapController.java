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

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.app.*;

import java.util.*;
import java.awt.*;

import javax.swing.*;

public class MapController
{
  ConzillaKit kit;

  HistoryManager historyManager;
  
  MapManagerFactory managerFactory;

  MapManager manager;
  
  MapScrollPane mapScrollPane;

  JPanel mapPanel;
  
  ToolSetBar toolBar;
  
  ContentSelector selector;
  
  
  public MapController(ConzillaKit kit, HistoryManager historyManager,
		       ContentSelector selector, MapManagerFactory managerFactory)
    {
      this.kit = kit;
      this.historyManager = historyManager;
      this.selector = selector;
      this.managerFactory = managerFactory;

      managerFactory.setController(this);
      
      toolBar = new ToolSetBar("Map tools");
      mapPanel = new JPanel();
      mapPanel.setLayout(new BorderLayout());
      
    }
  
  
  public ConzillaKit getConzillaKit()
    {
      return kit;
    }

  public HistoryManager getHistoryManager()
    {
      return historyManager;
    }
  
  
  public ContentSelector getContentSelector()
    {
      return selector;
    }
  
  public MapManagerFactory getMapManagerFactory()
    {
      return managerFactory;
    }

  public MapManager getManager()
    {
      return manager;
    }
  
  public MapScrollPane getMapScrollPane()
    {
      return mapScrollPane;
    }

  public JPanel getMapPanel()
    {
      return mapPanel;
    }
  
  public JToolBar getToolBar()
    {
      return toolBar;
    }
  
  public void showMap(URI mapURI)
    throws ControllerException
    {
      MapStoreManager storeManager;
      try {
	storeManager = new MapStoreManager(mapURI, kit.getComponentStore());
	
      } catch (ComponentException e)
	{
	  throw new ControllerException("Could not load map " + mapURI
					+ ": " + e.getMessage());
	}

      detach();      

      Tracer.debug("Loading map: " + mapURI);
      mapScrollPane = new MapScrollPane(new MapDisplayer(storeManager));
      mapPanel.add(mapScrollPane, BorderLayout.CENTER);

      Tracer.debug("Done loading map: " + mapURI);

      //FIXME fel/exceptions?
      manager = managerFactory.createManager(mapScrollPane.getDisplayer().getStoreManager().getConceptMap());
      
      manager.addTools(toolBar);
      manager.activate();
    }
  


  void detach()
    {
      if(manager != null)
	{
	  manager.deactivate();
	  manager.removeTools(toolBar);
	}
      
      if(mapScrollPane != null)
	{
	  mapScrollPane.getDisplayer().getStoreManager().detach();
	  mapScrollPane.getDisplayer().detach();
	  mapScrollPane.detach();
	  mapPanel.remove(mapScrollPane);
	}
      
      if(selector != null)
	selector.selectContent(null);
    }
}
