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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.view.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.ConceptMap;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.graphics.Mark;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.beans.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.plaf.*;

public class Conzilla implements PropertyChangeListener
{ 
    public static final String MAPMANAGERFACTORY_PROP = "conzilla.mapmanagerfactory.default";
    public static final String MENUFACTORY_PROP = "conzilla.menufactory";
    public static final String VIEWMANAGERS_PROP = "conzilla.viewmanager.list";
    public static final String DEFAULT_VIEWMANAGER_PROP = "conzilla.viewmanager.default";
    public static final String FONT_SIZE_PROP = "conzilla.font.size";
    public static final String PACK_PROP = "conzilla.view.alwayspack";
    public static final String ZOOM_PROP = "conzilla.view.defaultzoom";

    Hashtable viewManagers;
    MenuFactory menuFactory;
    ViewManager viewManager;
    ConzillaKit kit;
    Vector cowins;
    MapManagerFactory defaultMapManagerFactory;
    
    public Conzilla()
    {}
    
    public void initConzilla(ConzillaKit kit) throws IOException
    {
	this.kit = kit;
	cowins = new Vector();

	String fontSize = GlobalConfig.getGlobalConfig().getProperty(FONT_SIZE_PROP, "10");
	int fs = Integer.parseInt(fontSize);
	
	setGlobalFontSize(fs);
	
	loadMapManager();
	loadViewManagers();
	loadMenuFactory();

    }

    void loadMenuFactory() throws IOException
    {
	String menumanager = GlobalConfig.getGlobalConfig().getProperty(MENUFACTORY_PROP, "");
       
	try{
	    menuFactory  = (MenuFactory) Class.forName(menumanager).newInstance();
	    menuFactory.initFactory(kit);
	} catch(ClassNotFoundException e)
	    {
		throw new IOException("Could not find MenuFactory: " + menumanager);
	    }
	catch(InstantiationException e)
	    {
		throw new IOException("Could not make MenuFactory: " + menumanager + "\n " + e.getMessage());
	    }
	catch(IllegalAccessException e)
	    {
		throw new IOException("Could not make MenuFactory: " + menumanager + "\n " + e.getMessage());
	    }
	catch(ClassCastException e)
	    {
		throw new IOException("Could not make MenuFactory: " + menumanager + "\n " + e.getMessage());
	    }
    }

    void loadMapManager() throws IOException
    {
	String mapmanager = GlobalConfig.getGlobalConfig().getProperty(MAPMANAGERFACTORY_PROP, "");
	
	if(mapmanager == null)
	    {
		throw new IOException("MapManager invalid: " + mapmanager);
	    }
	try {
	    MapManagerFactory mf = (MapManagerFactory) Class.forName(mapmanager).newInstance();
	    kit.registerExtra(mf);
	    setDefaultMapManagerFactory(mf);
	} catch(ClassNotFoundException e)
	    {
		throw new IOException("Could not find MapManager: " + mapmanager);
	    }
	catch(InstantiationException e)
	    {
		throw new IOException("Could not make MapManagerFactory: " + mapmanager + "\n " + e.getMessage());
	    }
	catch(IllegalAccessException e)
	    {
		throw new IOException("Could not make MapManagerFactory: " + mapmanager + "\n " + e.getMessage());
	    }
	catch(ClassCastException e)
	    {
		throw new IOException("Could not make MapManagerFactory: " + mapmanager + "\n " + e.getMessage());
	    }
    }

    void loadViewManagers()
    {
	viewManagers = new Hashtable();
	String viewmanagers = GlobalConfig.getGlobalConfig().getProperty(VIEWMANAGERS_PROP, "");
	
	StringTokenizer st = new StringTokenizer(viewmanagers, ",");
	
	while(st.hasMoreTokens())
	    {
		String vm = st.nextToken();
		
		if(vm == null)
		    {
			Tracer.trace("ViewManager invalid: " + vm, Tracer.WARNING);
			continue;
		    }
		try{
		    registerViewManager((ViewManager) Class.forName(vm).newInstance());
		} catch(ClassNotFoundException e)
		    {
			Tracer.trace("Could not find ViewManager: " + vm, Tracer.WARNING);
		    }
		catch(InstantiationException e)
		    {
			Tracer.trace("Could not make ViewManager: " + vm + "\n " + e.getMessage(), Tracer.WARNING);
		    }
		catch(IllegalAccessException e)
		    {
			Tracer.trace("Could not make ViewManager: " + vm + "\n " + e.getMessage(), Tracer.WARNING);
		    }
		catch(ClassCastException e)
		    {
			Tracer.trace("Could not make ViewManager: " + vm + "\n " + e.getMessage(), Tracer.WARNING);
		    }
	    }
	String viewm = GlobalConfig.getGlobalConfig().getProperty(DEFAULT_VIEWMANAGER_PROP, "");

	ViewManager v = (ViewManager) viewManagers.get(viewm);
	
	if(v == null)
	    {
		ErrorMessage.showError("Could not create ViewManager", "The ViewManager " + viewm + " does not exist.\n",
				       null, null);
		kit.getConzillaEnvironment().exit(1);
	    }
	setViewManager(v);
    }

    public void registerViewManager(ViewManager s)
    {
	viewManagers.put(s.getClass().getName(), s);
    }

    public Enumeration getViewManagers()
    {
	return viewManagers.elements();
    }

    public ViewManager getViewManager()
    {
	return viewManager;
    }

    public void setViewManager(ViewManager manager)
    {
	if(!viewManagers.containsValue(manager))
	    throw new IllegalArgumentException("Invalid ViewManager: " + manager.getClass().getName());
	if(manager == viewManager)
	    return;

	manager.initManager();

	if(viewManager != null)
	    {
		viewManager.removePropertyChangeListener(this);
		Iterator e = viewManager.getViews();
		while(e.hasNext())
		    {
			View v = (View) e.next();
			MapController c = v.getController();
			viewManager.close(v, false);
			manager.newView(c).draw();
			e = viewManager.getViews();
		    }
		viewManager.detachManager();
	    }
	viewManager = manager;
	viewManager.addPropertyChangeListener(this);
	GlobalConfig.getGlobalConfig().setProperty(DEFAULT_VIEWMANAGER_PROP, manager.getClass().getName());
    }
    
    public void setDefaultMapManagerFactory(MapManagerFactory mf)
    {
	defaultMapManagerFactory = mf;
    }

    MapManager newDefaultManager()
    {
	return defaultMapManagerFactory.createManager();
    }

    public View openMapInNewView(URI map, MapController oldcont) throws ControllerException
    {
	return openMapInNewView(map, defaultMapManagerFactory, oldcont);
    }
    
    
    public boolean canChangeMapManager(MapController mc, MapManagerFactory mmf)
	{
	    URI map = URIClassifier.parseValidURI(mc.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI());
	    return mmf.canManage(mc, map);
	}

    public boolean changeMapManagerFactory(MapController mc, MapManagerFactory mmf)
    {
	URI map = URIClassifier.parseValidURI(mc.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI());
	if (!mmf.canManage(mc, map))
	    return false;
	
	mc.setMapManager(mmf.createManager());
	return true;
    }
    
    public View cloneView(View v) throws ControllerException
    {
	MapController controller = new MapController(kit, new ListContentSelector());
	URI map = URIClassifier.parseValidURI(v.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI());
	if(!defaultMapManagerFactory.canManage(controller, map))
	    {
		controller.detach();
		throw new ControllerException("Cannot manage map!");
	    }

	try {
	    openMap(controller, map, null);
	} catch(ControllerException e)
	    {
		controller.detach();
		throw (ControllerException) e.fillInStackTrace();
	    }

	controller.getLinearHistory().copyHistory(v.getController().getLinearHistory());

	menuFactory.addMenus(controller);
	controller.setMapManager(defaultMapManagerFactory.createManager());
	View view = viewManager.newView(controller);
	controller.addPropertyChangeListener(this);	
	controller.setScale(Double.parseDouble(GlobalConfig.getGlobalConfig().getProperty(ZOOM_PROP, "100")) / 100);

	view.draw();
	if("true".equals(GlobalConfig.getGlobalConfig().getProperty(PACK_PROP)))
	    view.pack();

	return view;	
    }

    public View openMapInNewView(URI map, MapManagerFactory mmf, MapController oldcont) throws ControllerException
    {
	MapController controller = new MapController(kit, new ListContentSelector());
	if(!mmf.canManage(controller, map))
	    {
		controller.detach();
		throw new ControllerException("Cannot manage map!");
	    }
	if(oldcont != null)
	    controller.getLinearHistory().copyHistory(oldcont.getLinearHistory());

	try {
	    openMap(controller, map, null);
	} catch(ControllerException e)
	    {
		controller.detach();
		throw (ControllerException) e.fillInStackTrace();
	    }

	menuFactory.addMenus(controller);
	controller.setMapManager(mmf.createManager());
	View v = viewManager.newView(controller);
	controller.addPropertyChangeListener(this);
	controller.setScale(Double.parseDouble(GlobalConfig.getGlobalConfig().getProperty(ZOOM_PROP, "100")) / 100);

	v.draw();
	if("true".equals(GlobalConfig.getGlobalConfig().getProperty(PACK_PROP)))
	    v.pack();
	return v;
    }

  public void openMapInOldView(URI map, View view) throws ControllerException
    {
	MapController controller = view.getController();
	
	ConceptMap oldMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();

	openMap(controller, map, oldMap);
    }

    void openMap(MapController controller, URI map, ConceptMap oldMap) throws ControllerException 
    {
	controller.showMap(map);
	controller.getHistoryManager().fireOpenNewMapEvent(controller, oldMap, map);
    }

    /*    
  public boolean openMapInNewView(URI map, String type)
    {
	MapController controller = ConzillaFactory.createController(kit, type, map);
	if (controller == null)
	    return false;
	boolean res = openMap(controller, map, null, "Failed to open map in new view");
	
	View view = viewManager.newView(controller);
	if (view == null)
	    {
		controller.detach();
		return false;
	    }

	view.draw();
	return res;
	}


  public boolean openMapInOldView(URI map, View view)
    {
	MapController controller = view.getController();
	if (controller == null)
	    return false;
	
	//FIXME: Simple check, presupposes that browse always exists and works.
	//How to choose secondary MapManagerFactories?? hints in the initial?
		if (!controller.getMapManagerFactory().satisfiesCriterias(controller, map))
	    controller.setMapManagerFactory(ConzillaFactory.createMapManagerFactory("browse"));
	
	ConceptMap oldMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();

	boolean res = openMap(controller, map, oldMap, "Failed to open new map in old view");
	view.draw();
	return res;
    }
    
  
  public boolean cloneView(View view)
    {
	URI map = URIClassifier.parseValidURI(view.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI());

	MapController controller = ConzillaFactory.cloneController(view.getController(), map);
	if (controller == null)
	    return false;

	View newview = viewManager.newView(controller);
	if (newview == null)
	    return false;	
	
	boolean res = openMap(controller, map, null, "Failed to clone view with map");
	newview.draw();
	return res;
    }

    private boolean openMap(MapController controller, URI map, ConceptMap oldMap, String failMessage)
    {
	try {
	    controller.showMap(map);
	    controller.getHistoryManager().fireOpenNewMapEvent(controller,
							       oldMap,
							       map);
	} catch (ControllerException ce) {
	    ErrorMessage.showError("Load Error",
				   failMessage+"\n\n" + map,
				   ce, null);
	    return false;
	}
	return true;
    }
*/  
  public void close(View view)
    {
	viewManager.close(view, true);
    }

  void resetAll()
    {
      Iterator en = viewManager.getViews();
      while(en.hasNext())
	((View) en.next()).getController().getMapScrollPane().getDisplayer().reset();
    }
  
  public void reload()
    {
      Enumeration en = kit.getExtras();
      while (en.hasMoreElements())
	  if (!((Extra) en.nextElement()).saveExtra())
	      return;

      resetAll();

      kit.getComponentStore().getCache().clear();

      en = kit.getExtras();
      while (en.hasMoreElements())
	  ((Extra) en.nextElement()).refreshExtra();
      
      //      kit.getFilterFactory().refresh();

      Iterator it = viewManager.getViews();
      try {
	while(it.hasNext())
	    {
		View view = (View) it.next();
		view.getController().reload();
		/*		if (!view.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().isEditable())
		    if (ConzillaFactory.isViewOfWorkType(view, "edit"))
		    ConzillaFactory.changeWorkView(view, "browse");*/
	    }
      } catch(ControllerException e)
	  {
	      ErrorMessage.showError("Reload error", "Cannot reload all maps.",
				     e, null);
	  }
    }

    //FIXME:  HACK!!! 
  public void pushMark(Set set, Mark mark, Object o)
    {
	Iterator en = viewManager.getViews();
	while (en.hasNext())
	    ((View) en.next()).getController().getMapScrollPane().getDisplayer().pushMark(set, mark, o);
    }
  public void popMark(Set set, Object o)
    {
	Iterator en = viewManager.getViews();
	while (en.hasNext())
	    ((View) en.next()).getController().getMapScrollPane().getDisplayer().popMark(set, o);

    }

  public void exit(int result)
    {
      resetAll();

      Enumeration en = kit.getExtras();
      while (en.hasMoreElements())
	  if (!((Extra) en.nextElement()).saveExtra())
	      return;

      en = kit.getExtras();
      while (en.hasMoreElements())
	  ((Extra) en.nextElement()).exitExtra();
      
      viewManager.removePropertyChangeListener(this);

      viewManager.detachManager();
	      
      viewManager = null;

      kit.getConzillaEnvironment().exit(result);
    }

    public void propertyChange(PropertyChangeEvent e)
    {
	if(ViewManager.VIEWS_PROPERTY.equals(e.getPropertyName()))
	    {
		if(!viewManager.getViews().hasNext())
		    exit(0);
	    }
	else if(MapController.MAP_PROPERTY.equals(e.getPropertyName()) || MapController.ZOOM_PROPERTY.equals(e.getPropertyName()))
	    {
		if("true".equals(GlobalConfig.getGlobalConfig().getProperty(PACK_PROP)))
		    viewManager.getView((MapController) e.getSource()).pack();
	    }
    }

    public void setGlobalFontSize(int size)
    {
	FontUIResource menuFont = new FontUIResource("Lucida Sans", Font.BOLD, size);
	FontUIResource textFont = new FontUIResource("Lucida Sans", Font.PLAIN, (int) (size*1.2));
	setGlobalFont(menuFont, textFont);
	GlobalConfig.getGlobalConfig().setProperty(FONT_SIZE_PROP, "" + size);
    }

    void setGlobalFont(FontUIResource menuFont, FontUIResource textFont)
    {
	UIManager.put("Button.font", menuFont);
	UIManager.put("ToggleButton.font", menuFont);
	UIManager.put("RadioButton.font", menuFont);
	//	  UIManager.put("CheckBox.font", font);
	//	  UIManager.put("ColorChooser.font", font);
	UIManager.put("ComboBox.font", menuFont);
	UIManager.put("Label.font", menuFont);
	//	  UIManager.put("List.font", font);
	UIManager.put("MenuBar.font", menuFont);
	UIManager.put("MenuItem.font", menuFont);
	UIManager.put("RadioButtonMenuItem.font", menuFont);
	UIManager.put("CheckBoxMenuItem.font", menuFont);
	UIManager.put("Menu.font", menuFont);
	//	  UIManager.put("PopupMenu.font", font);
	//	    UIManager.put("OptionPane.font", font);
	//	    UIManager.put("Panel.font", font);
	//	    UIManager.put("ProgressBar.font", font);
	//	    UIManager.put("ScrollPane.font", font);
	//	    UIManager.put("Viewport.font", font);
	UIManager.put("TabbedPane.font", menuFont);
	//	    UIManager.put("Table.font", font);
	//	    UIManager.put("TableHeader.font", font);
	UIManager.put("TitledBorder.font", menuFont);
	//	    UIManager.put("ToolBar.font", font);
	//	    UIManager.put("ToolTip.font", font);
	//	    UIManager.put("Tree.font", font);

	UIManager.put("TextField.font", textFont);
	//	    UIManager.put("PasswordField.font", font);
	UIManager.put("TextArea.font", textFont);
	//	    UIManager.put("TextPane.font", font);
	//	    UIManager.put("EditorPane.font", font);

	if(viewManager == null)
	    return;

	Iterator i = viewManager.getViews();
	while(i.hasNext())
	    {
		View v = (View) i.next();
		v.updateFonts();
		if("true".equals(GlobalConfig.getGlobalConfig().getProperty(PACK_PROP)))
		    v.pack();
	    }
    }
}
