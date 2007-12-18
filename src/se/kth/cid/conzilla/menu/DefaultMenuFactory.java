/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.menu;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import se.kth.cid.collaboration.CollaborillaConfiguration;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.Conzilla;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.NewWindowTool;
import se.kth.cid.conzilla.browse.ZoomDefaultTool;
import se.kth.cid.conzilla.browse.ZoomTool;
import se.kth.cid.conzilla.config.CollaborationSettingsDialog;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.content.SourceViewer;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.ExclusiveStateTool;
import se.kth.cid.conzilla.tool.StateTool;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolSet;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.ViewManager;
import se.kth.cid.util.LocaleEditor;
import se.kth.cid.util.LocaleManager;
import se.kth.cid.util.Tracer;

public class DefaultMenuFactory implements MenuFactory {

	public static final String ABOUT_MAP = "urn:path:/org/conzilla/builtin/maps/about/About";

	public static final String LOCAL_HELP_MAP = "urn:path:/org/conzilla/builtin/help/overview_map";

	public static final String NET_HELP_MAP = "urn:path:/org/conzilla/help/startmap_help";

	ConzillaKit kit;

	ConzillaResourceManager manager;

	ToolSet viewToolSet;

	ToolSet languageToolSet;

	StateTool alwaysPack;

	HashSet extraMenuNames = new HashSet();

	HashMap extraMenu2Bundle = new HashMap();

	HashMap extraMenu2priority = new HashMap();

	public DefaultMenuFactory() {
	}

	public void initFactory(ConzillaKit kit) {
		this.kit = kit;
		manager = ConzillaResourceManager.getDefaultManager();

		viewToolSet = new ToolSet();
		final Conzilla conzilla = kit.getConzilla();
		Enumeration views = conzilla.getViewManagers();
		ViewManager nowWm = conzilla.getViewManager();

		while (views.hasMoreElements()) {
			final ViewManager vm = (ViewManager) views.nextElement();
			final String viewname = vm.getID();
			ExclusiveStateTool tool = new ExclusiveStateTool(viewname, vm.getClass().getName(), vm == nowWm) {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals(StateTool.ACTIVATED) && ((Boolean) e.getNewValue()).booleanValue()) {
						conzilla.setViewManager(vm);
						if (vm.getWindow() != null) {
							vm.getWindow().setVisible(true);
						}
						vm.revalidate();
					}
				}
			};
			viewToolSet.addTool(tool);
		}

		languageToolSet = new ToolSet();
		updateLanguageTools();
		LocaleManager.getLocaleManager().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(LocaleManager.LOCALES_PROPERTY))
					updateLanguageTools();
			}
		});

		alwaysPack = new StateTool("ALWAYS_PACK", DefaultMenuFactory.class.getName(), ConfigurationManager
				.getConfiguration().getBoolean(Settings.CONZILLA_PACK, false)) {
			private static final long serialVersionUID = 1L;
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(Settings.CONZILLA_PACK)) {
					setActivated(ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_PACK, false));
				} else if (e.getPropertyName().equals(ACTIVATED)) {
					ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_PACK, new Boolean(isActivated()));
				}
			}
		};
		ConfigurationManager.getConfiguration().addPropertyChangeListener(Settings.CONZILLA_PACK, alwaysPack);
	}

	public void addExtraMenu(String name, String nameOfResourceBundle, int priority) {
		extraMenuNames.add(name);
		extraMenu2Bundle.put(name, nameOfResourceBundle);
		extraMenu2priority.put(name, new Integer(priority));
	}

	public void addMenus(MapController mc) {
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

		Iterator extraMenues = extraMenuNames.iterator();
		while (extraMenues.hasNext()) {
			String name = (String) extraMenues.next();
			String bundle = (String) extraMenu2Bundle.get(name);
			int priority = ((Integer) extraMenu2priority.get(name)).intValue();
			ToolsMenu extraMenu = new ToolsMenu(name, bundle);
			extend(extraMenu, mc, priority);
		}
	}

	void extend(ToolsMenu m, MapController mc, int prio) {
		kit.extendMenu(m, mc);
		mc.getView().addMenu(m, prio);
	}

	/*
	 * dvoid detach() {
	 * LocaleManager.getLocaleManager().removePropertyChangeListener(this); }
	 */

	ToolsMenu createFileMenu(final MapController controller) {
		final ToolsMenu file = new ToolsMenu(FILE_MENU, DefaultMenuFactory.class.getName());

		final Conzilla conzilla = kit.getConzilla();

		Tool newWindow = new NewWindowTool(controller);
		newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		file.addTool(newWindow, 100);

		Vector v = new Vector();
		Enumeration e = kit.getExtras();
		while (e.hasMoreElements()) {
			Extra ex = (Extra) e.nextElement();
			if (ex instanceof MapManagerFactory) {
				v.add(ex);
			}
		}
		if (v.size() > 1) {
			file.addSeparator(200);

			for (int i = 0; i < v.size(); i++) {
				final MapManagerFactory mmf = (MapManagerFactory) v.get(i);
				final Tool tool = new Tool(mmf.getName(), mmf.getClass().getName()) {
					public void actionPerformed(ActionEvent ae) {
						conzilla.changeMapManagerFactory(controller, mmf);
					}
				};
				
				if (tool.getName().equals("EditMapManagerFactory")) {
					tool.setIcon(Images.getImageIcon(Images.ICON_FILE_EDIT));
				} else if (tool.getName().equals("BrowseMapManagerFactory")) {
					tool.setIcon(Images.getImageIcon(Images.ICON_FILE_BROWSE));
				}

				file.getPopupMenu().addPopupMenuListener(new PopupMenuAdapter() {
					public void popupMenuWillBecomeVisible(PopupMenuEvent me) {
						tool.setEnabled(conzilla.canChangeMapManager(controller, mmf));
					}
				});
				file.addTool(tool, 200 + i * 5);
			}
		}

		file.addSeparator(300);

		newWindow = new Tool("CLOSE", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent ae) {
				// FIXME
				if (/* view.tryCloseMap() */true) {
					kit.getConzilla().close(conzilla.getViewManager().getView(controller));
				}
			}
		};
		newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
		newWindow.setIcon(Images.getImageIcon(Images.ICON_FILE_CLOSE));
		file.addTool(newWindow, 400);

		newWindow = new Tool("EXIT", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent ae) {
				//kit.getConzilla().exit(0);
				ViewManager manager = conzilla.getViewManager();
				if (manager.closeable()) {
					manager.saveProperties();
					manager.closeViews();
				}
			}
		};
		newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		newWindow.setIcon(Images.getImageIcon(Images.ICON_EXIT));
		file.addTool(newWindow, 1000);

		// TODO: Make the RDFModelLoaderMenu an Extra instead of hardcoding it
		// here.
		// file.add(new se.kth.cid.conzilla.menu.RDFModelLoaderMenu(kit));
		return file;
	}

	ToolsMenu createViewMenu(final MapController controller) {
		ToolsMenu viewm = new ToolsMenu(VIEW_MENU, DefaultMenuFactory.class.getName());

		final ConzillaKit kit = ConzillaKit.getDefaultKit();
		final Conzilla conzilla = kit.getConzilla();

		// FIXME More options.
		ToolsMenu zoom = new ToolsMenu("ZOOM", DefaultMenuFactory.class.getName());
		zoom.setIcon(Images.getImageIcon(Images.ICON_ZOOM));

		zoom.addTool(new ZoomDefaultTool(controller), 100);
		zoom.addTool(new ZoomTool(controller, 1.3), 200);
		zoom.addTool(new ZoomTool(controller, 1 / 1.3), 300);

		viewm.add(zoom);
		viewm.setPriority(zoom, 100);

		ExclusiveStateTool[] tools = viewToolSet.getTools();
		if (tools.length > 1) {
			ToolsMenu vt = new ToolsMenu("VIEW_TYPES", DefaultMenuFactory.class.getName());
			for (int i = 0; i < tools.length; i++)
				vt.addTool(tools[i], i * 10);

			viewm.add(vt);
			viewm.setPriority(vt, 300);
		}

		viewm.addTool(new Tool("PACK", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent e) {
				conzilla.getViewManager().getView(controller).pack();
			}
		}, 400);
		
		ToolsMenu sourceViews = new ToolsMenu("SOURCE", DefaultMenuFactory.class.getName());
		
		sourceViews.addTool(new Tool("SOURCE_N3", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent e) {
				showSourceInWindow(controller, "N3-PP");
			}
		}, 10);
		
		sourceViews.addTool(new Tool("SOURCE_NTRIPLE", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent e) {
				showSourceInWindow(controller, "N-TRIPLE");
			}
		}, 20);
		
		sourceViews.addTool(new Tool("SOURCE_RDFXML", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent e) {
				showSourceInWindow(controller, "RDF/XML-ABBREV");
			}
		}, 30);
		
		sourceViews.addTool(new Tool("SOURCE_TURTLE", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent e) {
				showSourceInWindow(controller, "TURTLE");
			}
		}, 40);
		
		viewm.add(sourceViews);
		viewm.setPriority(sourceViews, 500);

		return viewm;
	}
	
	private void showSourceInWindow(final MapController controller, final String format) {
		Thread sourceViewThread = new Thread(new Runnable() {
			public void run() {
				SourceViewer source = new SourceViewer(controller, format);
				source.setVisible(true);
			}
		});
		sourceViewThread.start();
	}

	ToolsMenu createSettingsMenu(final MapController controller) {
		final LocaleManager locMan = LocaleManager.getLocaleManager();

		final ToolsMenu localeMenu = new ToolsMenu("LANGUAGE", DefaultMenuFactory.class.getName());

		final PropertyChangeListener list = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(LocaleManager.LOCALES_PROPERTY))
					makeLocalesMenu(localeMenu);
			}
		};
		locMan.addPropertyChangeListener(list);

		ToolsMenu settings = new ToolsMenu(SETTINGS_MENU, DefaultMenuFactory.class.getName()) {
			public void detach() {
				super.detach();
				locMan.removePropertyChangeListener(list);
			}
		};

		makeLocalesMenu(localeMenu);

//		final ConzillaKit kit = controller.getConzillaKit();
//		final Conzilla conzilla = kit.getConzilla();

		// FIXME Move to Extra
		/*
		 * settings.add(new Tool("RESOLVER_TABLES",
		 * DefaultMenuFactory.class.getName()) { public void
		 * actionPerformed(ActionEvent ae) { kit.getResolverEditor().show(); }
		 * });
		 */

		settings.addTool(new Tool("SET_AS_START_MAP", DefaultMenuFactory.class.getName()) {
			{setIcon(Images.getImageIcon(Images.ICON_HOME));}
			public void actionPerformed(ActionEvent ae) {
				Tracer.debug("Set this map as startmap");
				String uri = controller.getConceptMap().getURI();
				ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_STARTMAP, uri);
			}
		}, 100);

//		settings.addTool(new Tool("EDIT_PROFILE", DefaultMenuFactory.class.getName()) {
//			{setIcon(Images.getImageIcon(Images.ICON_SETTINGS_PERSONAL_INFO));}
//			public void actionPerformed(ActionEvent ae) {
//				Tracer.debug("Edit Personal information");
//				kit.getAgentManager().editAgentInformation();
//			}
//		}, 150);

		ToolsMenu cm = makeColorMenu();
		settings.add(cm);
		settings.setPriority(cm, 200);

		// Locales!

		settings.add(localeMenu);
		settings.setPriority(localeMenu, 300);

		settings.addTool(alwaysPack, 500);

		settings.addTool(new Tool("SETZOOM", DefaultMenuFactory.class.getName()) {
			public void actionPerformed(ActionEvent ae) {
				ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_ZOOM,
						"" + controller.getView().getMapScrollPane().getDisplayer().getScale() * 100);
			}
		}, 600);

		settings.addTool(new Tool("COLLABORATION_SETTINGS", DefaultMenuFactory.class.getName()) {
			{setIcon(Images.getImageIcon(Images.ICON_SETTINGS_COLLABORATION));}
			public void actionPerformed(ActionEvent ae) {
				new CollaborationSettingsDialog().setVisible(true);
			}
		}, 700);
		
		settings.addTool(new Tool("IMPORT_COLLABORATION_SETTINGS", DefaultMenuFactory.class.getName()) {
			//{setIcon(Images.getImageIcon(Images.ICON_SETTINGS_COLLABORATION_IMPORT));}
			public void actionPerformed(ActionEvent ae) {
				new CollaborillaConfiguration(ConfigurationManager.getConfiguration()).askForConfigurationFile();
			}
		}, 800);

		return settings;
	}

	ToolsMenu createToolsMenu(final MapController controller) {
		ToolsMenu tools = new ToolsMenu(TOOLS_MENU, DefaultMenuFactory.class.getName());

		final ConzillaKit kit = ConzillaKit.getDefaultKit();
		final Conzilla conzilla = kit.getConzilla();

		tools.addTool(new Tool("RELOAD_ALL", DefaultMenuFactory.class.getName()) {
			{setIcon(Images.getImageIcon(Images.ICON_REFRESH));}
			public void actionPerformed(ActionEvent ae) {
				Tracer.debug("Reload");
				conzilla.reload();
			}
		}, 100);

		return tools;
	}

	ToolsMenu createHelpMenu(final MapController controller) {
		ToolsMenu help = new ToolsMenu(HELP_MENU, DefaultMenuFactory.class.getName());
//		JMenuItem mi = help;

//		final ConzillaKit kit = controller.getConzillaKit();
//		final Conzilla conzilla = kit.getConzilla();

		help.addTool(new Tool("ABOUT", DefaultMenuFactory.class.getName()) {
			{setIcon(Images.getImageIcon(Images.ICON_INFORMATION));}
			public void actionPerformed(ActionEvent ae) {
				new AboutMessage();
			}
		}, 600);

		// openMap(conzilla, help, controller, ABOUT_MAP, "ABOUT", 100);

		// openMap(conzilla, help, controller, LOCAL_HELP_MAP, "LOCAL_HELP",
		// 200);

		// openMap(conzilla, help, controller, NET_HELP_MAP, "NET_HELP", 300);

		return help;
	}

	void openMap(final Conzilla conzilla, final ToolsMenu menu, final MapController controller, final String suri,
			final String id, int prio) {
		final URI uri = URI.create(suri);
		menu.addTool(new Tool(id, DefaultMenuFactory.class.getName()) {
			{setIcon(Images.getImageIcon(Images.ICON_FILE_OPEN));}
			public void actionPerformed(ActionEvent ae) {
				try {
					conzilla.openMapInNewView(uri, null);
				} catch (ControllerException e) {
					ErrorMessage.showError("Cannot load map", "Cannot load map", e, menu);
				}
			}
		}, prio);
	}

	void updateLanguageTools() {
		Locale[] locales = LocaleManager.getLocaleManager().getLocales();
		Locale ldefault = Locale.getDefault();
		languageToolSet.removeAllTools();
		for (int i = 0; i < locales.length; i++) {
			final Locale l = locales[i];
			ExclusiveStateTool tool = new ExclusiveStateTool(locales[i].getDisplayName(ConzillaResourceManager
					.getDefaultManager().getDefaultLocale()), null, l.equals(ldefault)) {
				private static final long serialVersionUID = 1L;

				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals(ACTIVATED) && ((Boolean) e.getNewValue()).booleanValue())
						LocaleManager.getLocaleManager().setDefaultLocale(l);
				}
			};
			languageToolSet.addTool(tool);
		}
	}

	void makeLocalesMenu(ToolsMenu localeMenu) {
		Tracer.debug("Changing localeMenu");
		localeMenu.removeAll();

		localeMenu.addTool(new Tool("MANAGE_LANGUAGES", DefaultMenuFactory.class.getName()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				LocaleEditor edit = new LocaleEditor(null);
				edit.setVisible(true);
				edit.dispose();
			}
		}, 100);

		localeMenu.addSeparator(200);

		ExclusiveStateTool[] tools = languageToolSet.getTools();

		for (int i = 0; i < tools.length; i++) {
			localeMenu.addTool(tools[i], 200 + i * 5);
		}
	}

	ToolsMenu makeColorMenu() {
		final List themeIDs = ColorTheme.getColorThemeIDs();
		final Iterator themeIt = themeIDs.iterator();
		String activatedTheme = ColorTheme.getCurrentColorThemeID();

		ToolsMenu menu = new ToolsMenu("COLOR_SETTINGS", DefaultMenuFactory.class.getName());
		menu.setIcon(Images.getImageIcon(Images.ICON_SETTINGS_COLOR_THEMES));
		ToolSet themeTools = new ToolSet();

		while (themeIt.hasNext()) {
			final String themeId = (String) themeIt.next();
			final String themeName = (String) ColorTheme.getColorThemeName(themeId);
			themeTools.addTool(new ExclusiveStateTool(themeName, themeId.equals(activatedTheme)) {
				public void propertyChange(PropertyChangeEvent e) {
					if (((Boolean) e.getNewValue()).booleanValue() && e.getPropertyName().equals(ACTIVATED)) {
						ColorTheme.setColorTheme(themeId);
						Tracer.debug("Color theme set to \"" + themeName + "\"");
					}
				}
			});
		}

		ExclusiveStateTool[] tools = themeTools.getTools();
		for (int i = 0; i < tools.length; i++) {
			menu.addTool(tools[i], 200 + i * 5);
		}

		return menu;
	}

	static class PopupMenuAdapter implements PopupMenuListener {
		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}
	}

}
