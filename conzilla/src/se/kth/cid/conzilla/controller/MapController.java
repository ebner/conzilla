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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.app.*;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;

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
  
  LinearHistoryManager linearHistoryManager;

  Zoomer zoomManager;

  PropertyChangeSupport propSupport;
  
  public MapController(ConzillaKit kit, HistoryManager historyManager,
		       ContentSelector selector)
    {
      this.kit = kit;
      this.historyManager = historyManager;
      this.selector = selector;
      zoomManager=new Zoomer();

      propSupport = new PropertyChangeSupport(this);

      selector.setController(this);
      
      linearHistoryManager=new LinearHistoryManager(this);
      toolBar = new ToolSetBar("Map tools");
      toolBar.setBorder(new EmptyBorder(0, 0, 0, 0));
      mapPanel = new JPanel();
      mapPanel.setLayout(new BorderLayout());

      ColorManager cm = PropertiesManager.getDefaultPropertiesManager().getColorManager();
      cm.addPropertyChangeListener(null, new PropertyChangeListener()
	  {
	      public void propertyChange(PropertyChangeEvent e)
	      {
		  MapController.this.getMapScrollPane().repaint();
		  MapController.this.getContentSelector().getComponent().repaint();
	      }});
    }

  public void addPropertyChangeListener(PropertyChangeListener l)
    {
      propSupport.addPropertyChangeListener(l);
    }

  public void removePropertyChangeListener(PropertyChangeListener l)
    {
      propSupport.removePropertyChangeListener(l);
    }

  public void setMapManagerFactory(MapManagerFactory factory)
    {
	if (manager!=null)
	    manager.deInitialize(toolBar);
	if (managerFactory!=null)
	    managerFactory.deInstall();
	managerFactory=factory;
	managerFactory.install(this);
	if (mapScrollPane!=null)
	    {
		manager = managerFactory.createManager(mapScrollPane.getDisplayer().getStoreManager().getConceptMap());
		manager.initialize(toolBar);
	    }
    }

  public ConzillaKit getConzillaKit()
    {
      return kit;
    }

  public HistoryManager getHistoryManager()
    {
      return historyManager;
    }
  
  public LinearHistoryManager getLinearHistoryManager()
    {
      return linearHistoryManager;
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
  
  public Zoomer getZoomManager()
    {
      return zoomManager;
    }

  public JToolBar getToolBar()
    {
      return toolBar;
    }

  public void reload() throws ControllerException
    {
      URI map = URIClassifier.parseValidURI(mapScrollPane.getDisplayer().getStoreManager().getConceptMap().getURI());
      showMap(map);
    }
  
  
  public void showMap(URI mapURI)
    throws ControllerException
    {
      MapScrollPane oldPane = mapScrollPane;
      double oldScale=1.0;
      
      MapStoreManager storeManager;
      try {
	storeManager = new MapStoreManager(mapURI, kit.getComponentStore());
	
      } catch (ComponentException e)
	{
	  throw new ControllerException("Could not load map " + mapURI
					+ ":\n " + e.getMessage());
	}

      if(oldPane != null)
	{
	  oldScale=mapScrollPane.getDisplayer().getScale(); 
	  mapPanel.remove(oldPane);
	  manager.deInitialize(toolBar);
	}
      
      Tracer.debug("Loading map: " + mapURI);
      mapScrollPane = new MapScrollPane(new MapDisplayer(storeManager), zoomManager);
      mapPanel.add(mapScrollPane, BorderLayout.CENTER);
      
      Tracer.debug("Done loading map: " + mapURI);

      manager = managerFactory.createManager(mapScrollPane.getDisplayer().getStoreManager().getConceptMap());
      
      manager.initialize(toolBar);

      propSupport.firePropertyChange("map", oldPane, mapScrollPane);

      if(oldPane != null)
	{
	  oldPane.getDisplayer().getStoreManager().detach();
	  oldPane.getDisplayer().detach();
	  oldPane.detach();
	}
      
      mapScrollPane.getDisplayer().setScale(oldScale);
      mapPanel.revalidate();
      mapPanel.repaint();

      toolBar.revalidate();
    }
  
  public void detach()
    {
      if(manager != null)
	manager.deInitialize(toolBar);

      if(managerFactory != null)
	managerFactory.deInstall();
      
      if(mapScrollPane != null)
	{
	  mapScrollPane.getDisplayer().getStoreManager().detach();
	  mapScrollPane.getDisplayer().detach();
	  mapScrollPane.detach();
	  mapPanel.remove(mapScrollPane);
	}
      
      if(selector != null)
	selector.selectContentFromSet(null);
    }
}
