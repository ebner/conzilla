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
    public static final String MAP_PROPERTY = "map";
    public static final String MENUS_PROPERTY = "menus";
    public static final String ZOOM_PROPERTY = "zoom";

  ConzillaKit kit;

  HistoryManager historyManager;
  
  MapManager manager;
  
  MapScrollPane mapScrollPane;

  JPanel mapPanel;

  JPanel rightPanel;
  
  ToolsBar toolBar;
  
    Vector menus;

  ContentSelector selector;
  
  LinearHistory linearHistory;

  PropertyChangeSupport propSupport;
  
  public MapController(ConzillaKit kit, ContentSelector selector)
    {
      this.kit = kit;
      this.historyManager = new HistoryManager(kit.getComponentStore());
      linearHistory = new LinearHistory();

      historyManager.addHistoryListener(new HistoryListener() {
	      public void historyEvent(HistoryEvent e)
	      {
		  switch(e.getType())
		      {
		      case HistoryEvent.MAP:
			  linearHistory.historyEvent(e);
		      }
	      }
	  });
      
      this.selector = selector;
      //      rightPanel = new JPanel();
      //      rightPanel.setLayout(new Borderayout());
      //      rightPanel.add(selector, BorderLayout.CENTER); 

      menus = new Vector();

      propSupport = new PropertyChangeSupport(this);

      selector.setController(this);
      
      toolBar = new ToolsBar("MAP_TOOLS", MapController.class.getName());
      toolBar.setBorder(new EmptyBorder(0, 0, 0, 0));

     mapPanel = new JPanel();
      mapPanel.setLayout(new BorderLayout());

      GlobalConfig.getGlobalConfig().addPropertyChangeListener(new PropertyChangeListener()
	  {
	      public void propertyChange(PropertyChangeEvent e)
	      {
		  if(mapScrollPane != null)
		      mapScrollPane.repaint();
		  if(MapController.this.selector != null)
		      MapController.this.selector.getComponent().repaint();
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

  public void setMapManager(MapManager manager)
    {
	if (this.manager != null)
	    {
		this.manager.deInstall(this);
		getMapScrollPane().getDisplayer().reset();
	    }
	this.manager = manager;
	this.manager.install(this);
    }

  public ConzillaKit getConzillaKit()
    {
      return kit;
    }

  public HistoryManager getHistoryManager()
    {
      return historyManager;
    }
  
  public LinearHistory getLinearHistory()
    {
      return linearHistory;
    }
  
  public ContentSelector getContentSelector()
    {
      return selector;
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

    public JPanel getRightPanel()
    {
	return rightPanel;
    }

  public ToolsBar getToolsBar()
    {
      return toolBar;
    }

    public ToolsMenu[] getMenus()
    {
	return (ToolsMenu[]) menus.toArray(new ToolsMenu[menus.size()]);
    }

    public ToolsMenu getMenu(String name)
    {
	for(int i = 0; i < menus.size(); i++)
	    {
		ToolsMenu m = (ToolsMenu) menus.get(i);
		if(m.getName().equals(name))
		    return m;
	    }
	return null;
    }

    public void addMenu(ToolsMenu menu, int prio)
    {
	menu.putClientProperty("menus_prio", new Integer(prio));
	menus.add(menu);
	sortMenus();
	propSupport.firePropertyChange(MENUS_PROPERTY, null, null);
    }

    public void removeMenu(ToolsMenu menu)
    {
	if(menus.remove(menu))
	    propSupport.firePropertyChange(MENUS_PROPERTY, null, null);
    }

    void sortMenus()
    {
	Collections.sort(menus, new Comparator()
	    {
		public int compare(Object o1, Object o2)
		{
		    int p1 = ((Integer) ((ToolsMenu) o1).getClientProperty("menus_prio")).intValue();
		    int p2 = ((Integer) ((ToolsMenu) o2).getClientProperty("menus_prio")).intValue();
		    return p1 - p2;
		}
	    });
    }			

  public void reload() throws ControllerException
    {
      URI map = URIClassifier.parseValidURI(mapScrollPane.getDisplayer().getStoreManager().getConceptMap().getURI());
      showMap(map);
    }
    
    public void setScale(double newscale)
    {
	double oldscale = mapScrollPane.getDisplayer().getScale();
	mapScrollPane.setScale(newscale);
	propSupport.firePropertyChange(ZOOM_PROPERTY, new Double(oldscale), new Double(newscale));
    }    

    public void zoomMap(double factor)
    {
	setScale(mapScrollPane.getDisplayer().getScale()*factor);
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
	}
      
      mapScrollPane = new MapScrollPane(new MapDisplayer(storeManager));
      mapPanel.add(mapScrollPane, BorderLayout.CENTER);

      mapScrollPane.getDisplayer().setScale(oldScale);
      
      propSupport.firePropertyChange(MAP_PROPERTY, oldPane, mapScrollPane);

      if(oldPane != null)
	{
	  oldPane.getDisplayer().getStoreManager().detach();
	  oldPane.getDisplayer().detach();
	  oldPane.detach();
	}
      
      mapPanel.revalidate();
      mapPanel.repaint();

      toolBar.revalidate();
    }
  
  public void detach()
    {
      if(selector != null)
	selector.selectContentFromSet(null);

      if(manager != null)
	manager.deInstall(this);

      if(mapScrollPane != null)
	{
	  mapScrollPane.getDisplayer().getStoreManager().detach();
	  mapScrollPane.getDisplayer().detach();
	  mapScrollPane.detach();
	  mapPanel.remove(mapScrollPane);
	}
      for(int i = 0; i < menus.size(); i++)
	  ((ToolsMenu) menus.get(i)).detach();
      
      menus = null;
    }
}
