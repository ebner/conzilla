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

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.beans.*;
import java.awt.event.*;
import javax.swing.event.*;


/** This class creates BroweMapManagers for a single MapController.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class BrowseMapManager implements MapManager, PropertyChangeListener
{
    Tool suckIn;
    Tool zoomIn;
    Tool zoomOut;
    Tool home;
    Tool defaultHome;
    Browse browse;
    LinearHistoryManager linearHistoryManager;

    ToolsMenu goMenu;
    MapController controller;

    public BrowseMapManager()
    {
    }

    public void install(final MapController c)
    {
	this.controller = c;
	controller.addPropertyChangeListener(this);

	suckIn = new SuckInTool(controller, controller.getMapPanel());
	browse = new Browse(controller);
	zoomIn = new ZoomTool(controller, 1.3);
	zoomOut = new ZoomTool(controller, 1/1.3);
	
	home = new Tool("HOME", BrowseMapManagerFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ev)
		{
		    try {
			controller.getConzillaKit().getConzilla().openMapInOldView(GlobalConfig.getGlobalConfig().getURI(ConzillaEnvironment.STARTMAP_PROP), controller.getConzillaKit().getConzilla().getViewManager().getView(controller));
		    } catch(ControllerException e)
			    {
				ErrorMessage.showError("Cannot load map", "Cannot load map", e, controller.getToolsBar());
			    }
		    catch(MalformedURIException e)
			{
			    ErrorMessage.showError("Cannot load map", "Cannot load map", e, controller.getToolsBar());
			}
		}
	    };
	home.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, Event.ALT_MASK));
	home.setIcon(new ImageIcon(getClass().getResource("/graphics/toolbarButtonGraphics/navigation/Home16.gif")));


	defaultHome = new Tool("DEFAULT_HOME", BrowseMapManagerFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ev)
		{
		    try {
			controller.getConzillaKit().getConzilla().openMapInOldView(URIClassifier.parseValidURI(ConzillaEnvironment.DEFAULT_STARTMAP), controller.getConzillaKit().getConzilla().getViewManager().getView(controller));
		    } catch(ControllerException e)
			{
			    ErrorMessage.showError("Cannot load map", "Cannot load map", e, controller.getToolsBar());
			}
		}
	    };



	ToolsBar bar = controller.getToolsBar();
	
	linearHistoryManager = new LinearHistoryManager(controller);
	linearHistoryManager.createTools(bar);
	bar.addTool(home);
	bar.addTool(zoomIn);
	bar.addTool(zoomOut);
	bar.addTool(suckIn);
	browse.install(controller.getMapScrollPane());

	goMenu = new ToolsMenu("GO", BrowseMapManagerFactory.class.getName());

	updateGoMenu();
	goMenu.getPopupMenu().addPopupMenuListener(new PopupMenuListener()
	    {
		public void popupMenuCanceled(PopupMenuEvent e) {}
		public void popupMenuWillBecomeVisible(PopupMenuEvent e)
		{
		    updateGoMenu();
		}
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
	    });
	controller.addMenu(goMenu, 35);
    }

    public void deInstall(MapController c)
    {
	controller.removeMenu(goMenu);
	goMenu.removeTool(linearHistoryManager.getBackTool());
	goMenu.removeTool(linearHistoryManager.getForwardTool());
	goMenu.removeTool(home);
	goMenu.removeTool(defaultHome);

	goMenu.detach();
	
	ToolsBar bar = controller.getToolsBar();
	
	linearHistoryManager.detachTools(bar);
	linearHistoryManager.detach();
	bar.removeTool(home);
	bar.removeTool(zoomIn);
	bar.removeTool(zoomOut);
	bar.removeTool(suckIn);
	browse.uninstall(controller.getMapScrollPane());
	browse.detach();
	
	controller.removePropertyChangeListener(this);
	controller=null;
    }

    void updateGoMenu()
    {
	goMenu.removeAllTools();
	goMenu.removeAll();
	
	goMenu.addTool(linearHistoryManager.getBackTool(), 100);
	goMenu.addTool(linearHistoryManager.getForwardTool(), 200);
	goMenu.addTool(home, 300);
	goMenu.addTool(defaultHome, 400);
	goMenu.addSeparator(500);
	
	LinearHistory history = controller.getLinearHistory();

	String[] forwardTitles = controller.getLinearHistory().getForwardMapTitles();

	int i;
	for(i = 0; i < forwardTitles.length; i++)
	    {
		final int index = history.getIndex() + 1 + i;
		Tool item = new Tool(forwardTitles[i], null)
		    {
			public void actionPerformed(ActionEvent e)
			{
			    linearHistoryManager.controlledJump(index);
			}
		    };
		goMenu.addTool(item, 700 - i);
	    }
	JMenuItem mi = new JMenuItem(history.getMapTitle(history.getIndex()) + "    <-- current");
	mi.setEnabled(false);
	goMenu.add(mi);
	goMenu.setPriority(mi, 701);
	
	String[] backTitles = controller.getLinearHistory().getBackwardMapTitles();
	
	for(i = 0; i < backTitles.length; i++)
	    {
		final int index = history.getIndex() - 1 - i;
		Tool item = new Tool(backTitles[i], null)
		    {
			public void actionPerformed(ActionEvent e)
			{
			    linearHistoryManager.controlledJump(index);
			}
		    };
		goMenu.addTool(item, 900 + i);
	    }
    }

    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(MapController.MAP_PROPERTY))
	    {
		browse.uninstall((MapScrollPane) e.getOldValue());
		browse.install((MapScrollPane) e.getNewValue());
	    }
    }


}
