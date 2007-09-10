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


package se.kth.cid.conzilla.menu;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.install.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.view.*;
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
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;

public class DefaultMenuFactory implements MenuFactory
{    

    public static final String ABOUT_MAP = "urn:path:/org/conzilla/builtin/maps/about/About";
    public static final String LOCAL_HELP_MAP =  "urn:path:/org/conzilla/builtin/help/overview_map";
    public static final String NET_HELP_MAP = "urn:path:/org/conzilla/help/startmap_help";


    ConzillaKit kit;
    ConzillaResourceManager manager;
    ToolSet viewToolSet;
    ToolSet languageToolSet;
    StateTool alwaysPack;

    public DefaultMenuFactory()
    {
	
    }

    public void initFactory(ConzillaKit kit)
    {
	this.kit = kit;
	manager  = ConzillaResourceManager.getDefaultManager();	
	
	viewToolSet = new ToolSet();
	final Conzilla conzilla = kit.getConzilla();
	Enumeration views = conzilla.getViewManagers();
	ViewManager nowWm = conzilla.getViewManager();

	while(views.hasMoreElements())
	    {
		final ViewManager vm = (ViewManager) views.nextElement();
		final String viewname = vm.getID();
		ExclusiveStateTool tool = new ExclusiveStateTool(viewname, vm.getClass().getName(), vm == nowWm)
		    {
			public void propertyChange(PropertyChangeEvent e)
			{
			    if(e.getPropertyName().equals(StateTool.ACTIVATED) && 
			       ((Boolean) e.getNewValue()).booleanValue())
				conzilla.setViewManager(vm);
			}};
		viewToolSet.addTool(tool);
	    }

	languageToolSet = new ToolSet();
	updateLanguageTools();
	LocaleManager.getLocaleManager().addPropertyChangeListener(new PropertyChangeListener()
	    {
		public void propertyChange(PropertyChangeEvent e)
		{
		    if(e.getPropertyName().equals(LocaleManager.LOCALES_PROPERTY))
			updateLanguageTools();
		}
	    });

	alwaysPack = new StateTool("ALWAYS_PACK", DefaultMenuFactory.class.getName(),
				   "true".equals(GlobalConfig.getGlobalConfig().getProperty(Conzilla.PACK_PROP)))
	    {
		public void propertyChange(PropertyChangeEvent e)
		{
		    if(isActivated())
			GlobalConfig.getGlobalConfig().setProperty(Conzilla.PACK_PROP, "true");
		    else
			GlobalConfig.getGlobalConfig().remove(Conzilla.PACK_PROP);
		}
	    };
    }

    public void addMenus(MapController mc)
    {
	ToolsMenu file = createFileMenu(mc);
	
	ToolsMenu viewm = createViewMenu(mc);
	
	ToolsMenu settings = createSettingsMenu(mc);
	    
	ToolsMenu tools = createToolsMenu(mc);
	
	ToolsMenu help = createHelpMenu(mc);
	
	extend(file, mc, 10);
	extend(viewm, mc, 20);
	extend(settings, mc, 30);
	extend(tools, mc, 40);
	extend(help, mc, 1000);
    }

    void extend(ToolsMenu m, MapController mc, int prio)
    {
	kit.extendMenu(m, mc);
	mc.addMenu(m, prio);
    }

    /*dvoid detach()
    {
	LocaleManager.getLocaleManager().removePropertyChangeListener(this);
    }
    */
    
    
    ToolsMenu createFileMenu(final MapController controller)
    {
	final ToolsMenu file = new ToolsMenu(FILE_MENU, DefaultMenuFactory.class.getName());
	
	final Conzilla conzilla = kit.getConzilla();
	
	Tool t = new Tool("NEW_WINDOW", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    Tracer.debug("Create a new window");
		    try {
			conzilla.cloneView(conzilla.getViewManager().getView(controller));
		    } catch(ControllerException e)
			{
			    ErrorMessage.showError("Cannot load map", "Cannot load map.", e, file);
			}
		}
	    };
	t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
	file.addTool(t, 100);
	
	Vector v = new Vector();
	Enumeration e = kit.getExtras();
	while(e.hasMoreElements())
	    {
		Extra ex = (Extra) e.nextElement();
		if(ex instanceof MapManagerFactory)
		    v.add(ex);
	    }
	if (v.size() > 1)
	    {
		file.addSeparator(200);
		
		for(int i = 0; i < v.size(); i++)
		    {
			final MapManagerFactory mmf = (MapManagerFactory) v.get(i);
			final Tool tool = new Tool(mmf.getName(), mmf.getClass().getName())
			    {
				public void actionPerformed(ActionEvent ae)
				{
				    conzilla.changeMapManagerFactory(controller, mmf);  
				}
			    };
			
			file.getPopupMenu().addPopupMenuListener(new PopupMenuAdapter() {
				public void popupMenuWillBecomeVisible(PopupMenuEvent me)
				{
				    tool.setEnabled(conzilla.canChangeMapManager(controller, mmf));
				}
			    });
			file.addTool(tool, 200 + i*5);
		    }
	    }
	
	file.addSeparator(300);
	
	
	
	t = new Tool("CLOSE", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    //FIXME
		    if (/*view.tryCloseMap()*/ true)
			kit.getConzilla().close(conzilla.getViewManager().getView(controller));
		}
	    };
	t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
	file.addTool(t, 400);
	
	
	t = new Tool("EXIT", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    kit.getConzilla().exit(0);
		}
	    };
	t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
	file.addTool(t, 1000);
	
	return file;
    }
    
    
    
    ToolsMenu createViewMenu(final MapController controller)
    {
	ToolsMenu viewm = new ToolsMenu(VIEW_MENU, DefaultMenuFactory.class.getName());
	
	final ConzillaKit kit = controller.getConzillaKit();
	final Conzilla conzilla = kit.getConzilla();
	
	//FIXME More options.
	ToolsMenu zoom = new ToolsMenu("ZOOM", DefaultMenuFactory.class.getName());
	
	
	zoom.addTool(new ZoomDefaultTool(controller), 100);
	zoom.addTool(new ZoomTool(controller, 1.3), 200);
	zoom.addTool(new ZoomTool(controller, 1/1.3), 300);
	
	viewm.add(zoom);
	viewm.setPriority(zoom, 100);
	
	ExclusiveStateTool[] tools = viewToolSet.getTools();
	if(tools.length > 1)
	    {
		ToolsMenu vt = new ToolsMenu("VIEW_TYPES", DefaultMenuFactory.class.getName());
		for(int i = 0; i < tools.length; i++)
		    vt.addTool(tools[i], i*10);
		
		viewm.add(vt);
		viewm.setPriority(vt, 300);
	    }
	
	viewm.addTool(new Tool("PACK", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent e)
		{
		    conzilla.getViewManager().getView(controller).pack();
		}
	    }, 400);

	return viewm;
    }
    
    
    
    
    ToolsMenu createSettingsMenu(final MapController controller)
    {
	final LocaleManager locMan = LocaleManager.getLocaleManager();
	
	final ToolsMenu localeMenu = new ToolsMenu("LANGUAGE", DefaultMenuFactory.class.getName());
	
	final PropertyChangeListener list = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e)
		{
		    if(e.getPropertyName().equals(LocaleManager.LOCALES_PROPERTY))
			makeLocalesMenu(localeMenu);
		}
	    };
	locMan.addPropertyChangeListener(list);
	
	ToolsMenu settings = new ToolsMenu(SETTINGS_MENU, DefaultMenuFactory.class.getName())
	    {
		public void detach()
		{
		    super.detach();
		    locMan.removePropertyChangeListener(list);
		}		    
	    };
	
	makeLocalesMenu(localeMenu);	    
	
	final ConzillaKit kit = controller.getConzillaKit();
	final Conzilla conzilla = kit.getConzilla();
	
	
	//FIXME Move to Extra
	/*	settings.add(new Tool("RESOLVER_TABLES", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    kit.getResolverEditor().show();
		}
	    });
	*/
	
	settings.addTool(new Tool("SET_AS_START_MAP", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    Tracer.debug("Set this map as startmap");
		    String uri=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI();
		    GlobalConfig.getGlobalConfig().setProperty(ConzillaEnvironment.STARTMAP_PROP, uri);
		}
	    }, 100);
	
	
	ToolsMenu cm = makeColorMenu();
	settings.add(cm);
	settings.setPriority(cm, 200);
	
	//Locales!
	
	settings.add(localeMenu);
	settings.setPriority(localeMenu, 300);

	settings.addTool(alwaysPack, 500);

	settings.addTool(new Tool("SETZOOM", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    GlobalConfig.getGlobalConfig().setProperty(Conzilla.ZOOM_PROP, "" + controller.getMapScrollPane().getDisplayer().getScale()*100);
		}	
	    }, 600);
	
	return settings;
    }
    
    
    
    
    
    
    ToolsMenu createToolsMenu(final MapController controller)
    {
	ToolsMenu tools = new ToolsMenu(TOOLS_MENU, DefaultMenuFactory.class.getName());
	
	final ConzillaKit kit = controller.getConzillaKit();
	final Conzilla conzilla = kit.getConzilla();
	
	
	tools.addTool(new Tool("RELOAD_ALL", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    Tracer.debug("Reload");
		    conzilla.reload();
		}
	    }, 100);
	
	
	return tools;
    }
    
    
    
    
    
    
    
    ToolsMenu createHelpMenu(final MapController controller)
    {
	ToolsMenu help = new ToolsMenu(HELP_MENU, DefaultMenuFactory.class.getName());
	JMenuItem mi=help;
	
	final ConzillaKit kit = controller.getConzillaKit();
	final Conzilla conzilla = kit.getConzilla();
	
	openMap(conzilla, help, controller, 
		ABOUT_MAP, "ABOUT", 100);
	
	openMap(conzilla, help, controller, 
		LOCAL_HELP_MAP, "LOCAL_HELP", 200);
	
	openMap(conzilla, help, controller, 
		NET_HELP_MAP, "NET_HELP", 300);
	
	return help;
    }
    
    void openMap(final Conzilla conzilla, final ToolsMenu menu,
		 final MapController controller, final String suri, final String id, int prio)
    {
	final URI uri = URIClassifier.parseValidURI(suri);
	JMenuItem mi = menu.addTool(new Tool(id, DefaultMenuFactory.class.getName()) {
		public void actionPerformed(ActionEvent ae)
		{		    
		    try {
			conzilla.openMapInNewView(uri, null);
		    } catch(ControllerException e)
			{
			    ErrorMessage.showError("Cannot load map", "Cannot load map", e, menu);
			}
		}
	    }, prio);
    }
    
    void updateLanguageTools()
    {
	Locale[] locales = LocaleManager.getLocaleManager().getLocales();
	Locale ldefault = Locale.getDefault();
	languageToolSet.removeAllTools();
	for(int i = 0; i < locales.length; i++)
	    {
		final Locale l = locales[i];
		ExclusiveStateTool tool =
		    new ExclusiveStateTool(locales[i].getDisplayName(ConzillaResourceManager.getDefaultManager().getDefaultLocale()),
					   null, l.equals(ldefault))
		    {
			public void propertyChange(PropertyChangeEvent e)
			{
			    if(e.getPropertyName().equals(ACTIVATED) &&
			       ((Boolean) e.getNewValue()).booleanValue())
				LocaleManager.getLocaleManager().setDefaultLocale(l);
			}
		    };
		languageToolSet.addTool(tool);
	    }
    }
    
    void makeLocalesMenu(ToolsMenu localeMenu)
    {
	Tracer.debug("Changing localeMenu");
	localeMenu.removeAll();
	
	localeMenu.addTool(new Tool("MANAGE_LANGUAGES", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent e)
		{
		    LocaleEditor edit = new LocaleEditor(null);
		    edit.show();
		    edit.dispose();
		}
	    }, 100);
	
	localeMenu.addSeparator(200);
	
	ExclusiveStateTool[] tools = languageToolSet.getTools();
	
	for(int i = 0; i < tools.length; i++)
	    {
		localeMenu.addTool(tools[i], 200 + i*5);
	    }
    }
    
    
    ToolsMenu makeColorMenu()
    {
	final GlobalConfig.ColorSet[] colorSets = GlobalConfig.getGlobalConfig().getColorSets();
	
	final GlobalConfig config = GlobalConfig.getGlobalConfig();
	
	ToolsMenu menu = new ToolsMenu("COLOR_SETTINGS", DefaultMenuFactory.class.getName());
	
	for (int i = 0; i < colorSets.length; i++)
	    {
		final int menuindex = i;
		
		final String rb = colorSets[i].resourceBundle;
		
		ToolsMenu submenu = new ToolsMenu(colorSets[i].nameProperty, rb);
		
		final String[] colorProps = colorSets[i].colorProperties;
		
		for (int j = 0; j < colorProps.length; j++)
		    {
			final int nr = j;
			submenu.addTool(new Tool(colorProps[j], rb) {
				public void actionPerformed(ActionEvent e)
				{
				    Color c = JColorChooser.showDialog(null, manager.getString(rb, colorProps[nr]),
								       config.getColor(colorProps[nr]));
				    if (c != null)
					config.setColor(colorProps[nr], c);				    
				}
			    }, j*10);	
		    }
		menu.add(submenu);
		menu.setPriority(submenu, 100*i);
	    }
	menu.addSeparator(2000);
	menu.addTool(new Tool("RESET_TO_DEFAULT", DefaultMenuFactory.class.getName())
	    {
		public void actionPerformed(ActionEvent e)
		{
		    for(int i = 0; i < colorSets.length; i++)
			for(int j = 0; j < colorSets[i].colorProperties.length; j++)
			    {
				config.setColor(colorSets[i].colorProperties[j], null);
			    }
		}
	    }, 2100);
	return menu;
    }
    
    static class PopupMenuAdapter implements PopupMenuListener
    {
	public void popupMenuCanceled(PopupMenuEvent e) {}
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
    }

}

