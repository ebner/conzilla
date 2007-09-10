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
import se.kth.cid.conzilla.install.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.print.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import java.io.*;

public class ConzillaFactory
{
  ConzillaKit kit;

  public ConzillaFactory(ConzillaKit kit)
    {
      this.kit = kit;
    }
  
  public boolean isConzillawindowInEditMode(ConzillaWindow cw)
    {
       return (cw.getController().getMapManagerFactory() instanceof EditMapManagerFactory);
    }

  public ConzillaWindow createBrowseWindow(URI map)
    throws ControllerException
    {
      return createWindow(map, new BrowseMapManagerFactory());
    }
    
  public ConzillaWindow createEditWindow(URI map)
    throws ControllerException
    {
      return createWindow(map, new EditMapManagerFactory());
    }

  private ConzillaWindow createWindow(URI map, MapManagerFactory manager)
    throws ControllerException
    {
      MapController controller = new MapController(kit, new HistoryManager(kit.store),
						   new ListContentSelector());

      ConzillaWindow cw = new ConzillaWindow(controller);
      manager.setConzillaWindow(cw);

      addMenus(cw);

      controller.setMapManagerFactory(manager);

      controller.showMap(map);

      // Wich menus depends on the map....
      // (The manager isn't created until a map is specified.)

      controller.getHistoryManager().fireOpenNewMapEvent(controller, null, map);
      
      cw.setLocation(100, 100);
      cw.setSize(100, 100);
      cw.show();

      cw.pack();
      return cw;
    }

  public void changeToEditWindow(ConzillaWindow cw)
    {
      if (cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().isEditable())
	{
	  cw.getController().getMapScrollPane().getDisplayer().reset();
	  cw.getController().setMapManagerFactory(new EditMapManagerFactory());
	  cw.validate();
	  cw.repaint();
	}
      
      else
	ErrorMessage.showError("Change to Edit error.", "Unable to switch mode to edit, map not editable.", null, cw);
    }

  public void changeToBrowseWindow(ConzillaWindow cw)
    {
      cw.getController().getMapScrollPane().getDisplayer().reset();
      cw.getController().setMapManagerFactory(new BrowseMapManagerFactory());
      cw.validate();
      cw.repaint();
    }

  class MenuAdapter implements MenuListener
  {
      protected ConzillaWindow cw;
      protected AbstractAction aa;
      public MenuAdapter(ConzillaWindow cw, AbstractAction aa)
      {
	  this.cw=cw;
	  this.aa=aa;
      }
      public void menuCanceled(MenuEvent e) {}
      public void menuDeselected(MenuEvent e) {}
      public void menuSelected(MenuEvent e) {}
  }
    
  private void addMenus(final ConzillaWindow cw)
    {
      MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();
      
      JMenu file = createFileMenu(cw, menuManager);
      
      JMenu settings = createSettingsMenu(cw, menuManager);
      
      JMenu tools = createToolsMenu(cw, menuManager);
      
      JMenu help = createHelpMenu(cw, menuManager);
      
      cw.getJMenuBar().add(file);
      cw.getJMenuBar().add(settings);
      cw.getJMenuBar().add(tools);
      cw.getJMenuBar().add(Box.createRigidArea(new Dimension(20, 10)));

      cw.getController().getToolBar().setFloatable(false);
      cw.getJMenuBar().add(cw.getController().getToolBar());


      cw.getJMenuBar().add(Box.createHorizontalGlue());
      //      cw.getJMenuBar().setHelpMenu(help);   Yields an exception, not yet implemented.
      cw.getJMenuBar().add(help);
    }

    JMenu createFileMenu(final ConzillaWindow cw, MenuManager menuManager)
    {
      JMenu file = new JMenu();
      menuManager.customizeButton(file, "FILE");
      
      JMenuItem mi=file.add(new AbstractAction()
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      /*	     
		if (cw.tryCloseMap())
		    {
	      */

		if (cw.openMapHandler.openMap()) 
		    {
			if (isConzillawindowInEditMode(cw))
			    {
				Tracer.debug("Change to browse");
				changeToBrowseWindow(cw);
			    }
			cw.openMapHandler.showOpenedMapInSameWindow();
		    }
	      /*	    } */
	    }
	  });
      menuManager.customizeButton(mi, "OPEN_MAP");
      
      mi=file.add(new AbstractAction("New map")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
		Tracer.debug("Create a new map in new window");
		cw.openMapHandler.openNewMap(); 
	    }
	});
      menuManager.customizeButton(mi, "NEW_MAP");

      mi=file.add(new AbstractAction("New window")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Create a new window");
	      kit.getConzilla().clone(cw);
	    }
	});
      menuManager.customizeButton(mi, "NEW_WINDOW");

      file.addSeparator();
      AbstractAction aa=new AbstractAction("Edit")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Change to editor");
	      changeToEditWindow(cw);
	    }
	  };

      file.addMenuListener(new MenuAdapter(cw, aa) {
	      public void menuSelected(MenuEvent me)
	      {
		  this.aa.setEnabled(this.cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().isEditable());
	      }
	  });
      mi=file.add(aa);
      menuManager.customizeButton(mi, "EDIT");
      
      mi=file.add(new AbstractAction("Browse")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Change to browser");
	      changeToBrowseWindow(cw);
	    }
	});
      menuManager.customizeButton(mi, "BROWSE");

      file.addSeparator();

      mi=file.add(new AbstractAction("Print...")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Print");
	      try {
		(new MapPrinter()).print(cw.getController().getMapScrollPane());
	      } catch(PrinterException e)
		{
		  ErrorMessage.showError("Print Error",
					 "Could not print map\n\n" +
					 cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI(), e, cw);
		}
	    }
	});
      menuManager.customizeButton(mi, "PRINT");

      mi=file.add(new AbstractAction("Close")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Close this window");
	      /*if (cw.tryCloseMap())*/
	      kit.getConzilla().close(cw);
	    }
	});
      menuManager.customizeButton(mi, "CLOSE");
      
      mi=file.add(new AbstractAction("Exit")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Exits the whole application.");
	      kit.getConzilla().exit(0);
	    }
	});
      menuManager.customizeButton(mi, "EXIT");
      return file;
    }

    JMenu createSettingsMenu(final ConzillaWindow cw, MenuManager menuManager)
    {
	JMenu settings = new JMenu("Settings");
	JMenuItem mi = settings;
	menuManager.customizeButton(mi, "SETTING");
	
	mi=settings.add(new AbstractAction("Resolver tables...")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      kit.getResolverEditor().show();
	    }
	});
	menuManager.customizeButton(mi, "RESOLVER_TABLES");

      //FIXME  should always have access to ConzillaConfig?
      if (cw.getController().getConzillaKit().getConzillaEnvironment() instanceof ConzillaAppEnv)
	  {
	      mi=settings.add(new AbstractAction("Set as startmap")
		  {
		      public void actionPerformed(ActionEvent ae)
		      {
			  Tracer.debug("Set this map as startmap");
			  String uri=cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI();
			  ConzillaConfig config=((ConzillaAppEnv) kit.getConzillaEnvironment()).getConfig();
			  config.setProperty(ConzillaConfig.PROPERTY_STARTMAP, uri);
			  try {
			      config.store();
			  } catch(IOException e)
			      {
				  ErrorMessage.showError("Save Error", "Cannot save config file.", e, null);
			      }
		      }
		  });
	      menuManager.customizeButton(mi, "SET_AS_START_MAP");
		    
	  }
      
      mi=settings.add(se.kth.cid.conzilla.properties.ColorManager.getDefaultColorManager().getColorMenu());
      menuManager.customizeButton(mi, "COLOR_SETTINGS");


      mi=settings.add(new LocaleMenu(cw));
      menuManager.customizeButton(mi, "LANGUAGE");

      ToolSetMenu zoom=new ToolSetMenu("Zoom");
      

      mi=zoom.addTool(new ZoomDefaultTool(cw.getController()));
      menuManager.customizeButton(mi, "ZOOM_DEFAULT");
      mi=zoom.addTool(new ZoomTool(cw.getController(), 1.3));
      menuManager.customizeButton(mi, "ZOOM_IN");
      mi=zoom.addTool(new ZoomTool(cw.getController(), 1/1.3));
      menuManager.customizeButton(mi, "ZOOM_OUT");
      mi=settings.add(zoom);
      menuManager.customizeButton(mi, "ZOOM");
//	settings.add(new AbstractAction("Change language...")
//	  {
//	    public void actionPerformed(ActionEvent ae)
//	      {
//		Tracer.debug("Set your language");
//		String str=JOptionPane.showInputDialog(cw,"Type in your language, two letters only!");
//		if (str==null || str.length()< 2)
//		  return;
//		Locale.setDefault(new Locale(str.substring(0,2), "", ""));
//		kit.getConzilla().update();
//	      }
//	  });

      settings.add(new AbstractAction("Pack")
	{
	    public void actionPerformed(ActionEvent ae)
	    {
		cw.pack();
	    }
	});

      return settings;
    }

    JMenu createToolsMenu(final ConzillaWindow cw, MenuManager menuManager)
    {
      JMenu tools = new JMenu("Tools");
      JMenuItem mi=tools;
      menuManager.customizeButton(mi, "TOOLS");
      
      mi=tools.add(new AbstractAction("Component editor...")
	  {
	      public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Open component editor");
	      kit.getComponentEdit().show();
	    }
	});
      menuManager.customizeButton(mi, "COMPONENT_EDITOR");

      mi=tools.add(new AbstractAction("Reload all")
	{
	  public void actionPerformed(ActionEvent ae)
	    {
	      Tracer.debug("Reload");
	      if (kit.getComponentEdit().askSaveAll())  
		  cw.getController().getConzillaKit().getConzilla().reload();
	    }
	  });
      menuManager.customizeButton(mi, "RELOAD_ALL");
      
      return tools;
    }
    
    JMenu createHelpMenu(final ConzillaWindow cw, MenuManager menuManager)
    {
      JMenu help = new JMenu("Help");
      JMenuItem mi=help;
      menuManager.customizeButton(mi, "HELP");

      mi=help.add(new AbstractAction("About") {
	      public void actionPerformed(ActionEvent ae)
	      {

		if (cw.openMapHandler.openMap("urn:path:/org/conzilla/builtin/maps/about/About")) 
		    {
			if (isConzillawindowInEditMode(cw))
			    {
				Tracer.debug("Change to browse");
				changeToBrowseWindow(cw);
			    }
			cw.openMapHandler.showOpenedMapInSameWindow();
		    }
	      }
	  });
      menuManager.customizeButton(mi, "ABOUT");

      mi=help.add(new AbstractAction("Local help") {
	      public void actionPerformed(ActionEvent ae)
	      {
		  try {
		      ConceptMap oldMap = cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
		      cw.getController().showMap(URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/help/overview_map"));
		       cw.getController().getHistoryManager().fireOpenNewMapEvent(cw.getController(), oldMap, URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/help/overview_map"));

		  } catch (ControllerException ce)
		      {
			  ErrorMessage.showError("Map error", "Could not open local help map\n", ce, cw);
		      }
	      }
	  });
      menuManager.customizeButton(mi, "LOCAL_HELP");
      mi=help.add(new AbstractAction("Net help") {
	      public void actionPerformed(ActionEvent ae)
	      {
		  try {
		      ConceptMap oldMap = cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
		      cw.getController().showMap(URIClassifier.parseValidURI("urn:path:/org/conzilla/help/startmap_help"));
		       cw.getController().getHistoryManager().fireOpenNewMapEvent(cw.getController(), oldMap, URIClassifier.parseValidURI("urn:path:/org/conzilla/help/startmap_help"));

		  } catch (ControllerException ce)
		      {
			  ErrorMessage.showError("Map error", "Could not open net help map\n", ce, cw);
		      }
	      }
	  });
      menuManager.customizeButton(mi, "NET_HELP");


      mi=help.add(new AbstractAction("Startmap") {
	      public void actionPerformed(ActionEvent ae)
	      {
		  ConzillaConfig config=((ConzillaAppEnv) kit.getConzillaEnvironment()).getConfig();
		  String startmap=config.getProperty(ConzillaConfig.PROPERTY_STARTMAP);

		if (cw.openMapHandler.openMap(startmap)) 
		    {
			if (isConzillawindowInEditMode(cw))
			    {
				Tracer.debug("Change to browse");
				changeToBrowseWindow(cw);
			    }
			cw.openMapHandler.showOpenedMapInSameWindow();
		    }
	      }
	  });
      menuManager.customizeButton(mi, "STARTMAP");
      

      mi=help.add(new AbstractAction("Default Startmap") {
	      public void actionPerformed(ActionEvent ae)
	      {
		  if (cw.openMapHandler.openMap("urn:path:/org/conzilla/builtin/maps/default"))
		    {
			if (isConzillawindowInEditMode(cw))
			    {
				Tracer.debug("Change to browse");
				changeToBrowseWindow(cw);
			    }
			cw.openMapHandler.showOpenedMapInSameWindow();
		    }
	      }
	  });
      menuManager.customizeButton(mi, "DEFAULT_STARTMAP");

      return help;
    }
}
