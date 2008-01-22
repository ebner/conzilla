/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.awt.Font;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.content.ListContentSelector;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.conzilla.view.ViewManager;
import se.kth.cid.layout.ContextMap;

public class Conzilla implements PropertyChangeListener {
	
	public static final String CURRENT_VERSION = "2.2.0";

	Hashtable viewManagers;

	ViewManager viewManager;

	ConzillaKit kit;

	MapManagerFactory defaultMapManagerFactory;
	
	Window window;
	
	Log log = LogFactory.getLog(Conzilla.class);

	public Conzilla() {
	}

	public void initConzilla(ConzillaKit kit) throws IOException {
		this.kit = kit;

		String fontSize = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_FONT_SIZE, "10");

		int fs = Integer.parseInt(fontSize);

		setGlobalFontSize(fs);

		loadMapManager();
		loadViewManagers();
		// loadMenuFactory(); moved to ConzillaKit

	}

	void loadMapManager() throws IOException {
		String mapmanager = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_MAPMANAGERFACTORY, "");

		if (mapmanager == null) {
			throw new IOException("MapManager invalid: " + mapmanager);
		}
		try {
			MapManagerFactory mf = (MapManagerFactory) Class.forName(mapmanager).newInstance();
			kit.registerExtra(mf);
			setDefaultMapManagerFactory(mf);
		} catch (ClassNotFoundException e) {
			throw new IOException("Could not find MapManager: " + mapmanager);
		} catch (InstantiationException e) {
			throw new IOException("Could not make MapManagerFactory: " + mapmanager + "\n " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException("Could not make MapManagerFactory: " + mapmanager + "\n " + e.getMessage());
		} catch (ClassCastException e) {
			throw new IOException("Could not make MapManagerFactory: " + mapmanager + "\n " + e.getMessage());
		}
	}

	void loadViewManagers() {
		viewManagers = new Hashtable();

		List viewmanagers = ConfigurationManager.getConfiguration().getStringList(Settings.CONZILLA_VIEWMANAGERS, new ArrayList());

		Iterator viewIt = viewmanagers.iterator();

		while (viewIt.hasNext()) {
			String vm = (String)viewIt.next();

			if (vm == null) {
				log.warn("Invalid ViewManager: " + vm);
				continue;
			}
			try {
				registerViewManager((ViewManager) Class.forName(vm).newInstance());
			} catch (ClassNotFoundException e) {
				log.warn("Could not find ViewManager: " + vm);
			} catch (InstantiationException e) {
				log.warn("Could not create ViewManager: " + vm, e);
			} catch (IllegalAccessException e) {
				log.warn("Could not create ViewManager: " + vm, e);
			} catch (ClassCastException e) {
				log.warn("Could not create ViewManager: " + vm, e);
			}
		}
		String viewm = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_VIEWMANAGER_DEFAULT, "");

		ViewManager v = (ViewManager) viewManagers.get(viewm);

		if (v == null) {
			ErrorMessage.showError("Could not create ViewManager", "The ViewManager " + viewm + " does not exist.\n",
					null, null);
			kit.getConzillaEnvironment().exit(1);
		}
		setViewManager(v);
	}

	public void registerViewManager(ViewManager s) {
		viewManagers.put(s.getClass().getName(), s);
	}

	public Enumeration getViewManagers() {
		return viewManagers.elements();
	}

	public ViewManager getViewManager() {
		return viewManager;
	}

	public void setViewManager(ViewManager manager) {
		if (!viewManagers.containsValue(manager)) {
			throw new IllegalArgumentException("Invalid ViewManager: " + manager.getClass().getName());
		}
		if (manager == viewManager) {
			return;
		}

		manager.initManager();

		if (viewManager != null) {
			viewManager.removePropertyChangeListener(this);
			Iterator e = viewManager.getViews();
			while (e.hasNext()) {
				View v = (View) e.next();
				MapController c = v.getController();
				c.getManager().deInstall();
				viewManager.close(v, false);
				View nv = manager.newView(c);
				nv.setMap(c.getMapStoreManager());
				nv.getController().firePropertyChange(MapController.MAP_PROPERTY, null, nv.getMapScrollPane());
				kit.extend(c);
				if (kit.getMenuFactory() != null) {
					kit.getMenuFactory().addMenus(c);
				}
				nv.getMapScrollPane().setScale(nv.getMapScrollPane().getDisplayer().getScale());
				c.getManager().install();
				nv.draw();
				e = viewManager.getViews();
			}
			viewManager.detachManager();
		}
		viewManager = manager;
		viewManager.addPropertyChangeListener(this);
		ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_VIEWMANAGER_DEFAULT, manager.getClass().getName());
	}

	public MapManagerFactory getDefaultMapManagerFactory() {
		return defaultMapManagerFactory;
	}

	public void setDefaultMapManagerFactory(MapManagerFactory mf) {
		defaultMapManagerFactory = mf;
	}

	public View openMapInNewView(URI map, MapController oldcont) throws ControllerException {
		return openMapInNewView(map, defaultMapManagerFactory, oldcont);
	}

	public boolean canChangeMapManager(MapController mc, MapManagerFactory mmf) {
		URI map = URI.create(mc.getConceptMap().getURI());
		return mmf.canManage(mc, map);
	}

	public boolean changeMapManagerFactory(MapController mc, MapManagerFactory mmf) {
		URI map = URI.create(mc.getConceptMap().getURI());
		if (!mmf.canManage(mc, map)) {
			return false;
		}
		mc.changeMapManager(mmf);
		return true;
	}

	public View cloneView(View v) throws ControllerException {
		MapController controller = new MapController(new ListContentSelector());
		URI map = null;
		try {
			map = new URI(v.getController().getConceptMap().getURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		if (!defaultMapManagerFactory.canManage(controller, map)) {
			controller.detach();
			throw new ControllerException("Cannot manage map!");
		}
		View view = viewManager.newView(controller);
		
		try {
			openMap(controller, map, null);
		} catch (ControllerException e) {
			controller.detach();
			throw (ControllerException) e.fillInStackTrace();
		}

		controller.getLinearHistory().copyHistory(v.getController().getLinearHistory());

		kit.extend(controller);
		if (kit.getMenuFactory() != null) {
			kit.getMenuFactory().addMenus(controller);
		}

		controller.changeMapManager(defaultMapManagerFactory);
		controller.addPropertyChangeListener(this);
		view.setScale(Double.parseDouble(ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_ZOOM, "100")) / 100);

		view.draw();
		if (ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_PACK)) {
			view.pack();
		}

		return view;
	}

	public View openMapInNewView(URI map, MapManagerFactory mmf, MapController oldcont) throws ControllerException {
		MapController controller = new MapController(new ListContentSelector());
		if (!mmf.canManage(controller, map)) {
			controller.detach();
			throw new ControllerException("Cannot manage map!");
		}

		View v = viewManager.newView(controller);
		if (oldcont != null) {
			controller.getLinearHistory().copyHistory(oldcont.getLinearHistory());
		}

		try {
			openMap(controller, map, null);
		} catch (ControllerException e) {
			viewManager.close(v, true);
			if (controller != null) {
				controller.detach();
			}
			if (v != null) {
				v.detach();
			}
			throw (ControllerException) e.fillInStackTrace();
		}

		kit.extend(controller);
		if (kit.getMenuFactory() != null) {
			kit.getMenuFactory().addMenus(controller);
		}
		controller.changeMapManager(mmf);
		controller.addPropertyChangeListener(this);
		v.setScale(Double.parseDouble(ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_ZOOM, "100")) / 100);

		v.draw();
		if (ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_PACK)) {
			v.pack();
		}
		return v;
	}

	public void openMapInOldView(URI map, View view) throws ControllerException {
		MapController controller = view.getController();
		ContextMap oldMap = controller.getConceptMap();
		openMap(controller, map, oldMap);
	}

	void openMap(MapController controller, URI map, ContextMap oldMap) throws ControllerException {
		controller.showMap(map);
		controller.getHistoryManager().fireOpenNewMapEvent(controller, oldMap, map);
	}

	public void close(View view) {
		viewManager.close(view, true);
	}

	void resetAll() {
		Iterator en = viewManager.getViews();
		while (en.hasNext())
			((View) en.next()).getMapScrollPane().getDisplayer().reset();
	}

	public void reload() {
		Enumeration en = kit.getExtras();
		while (en.hasMoreElements())
			if (!((Extra) en.nextElement()).saveExtra())
				return;

		resetAll();

		kit.getResourceStore().refresh();

		en = kit.getExtras();
		while (en.hasMoreElements())
			((Extra) en.nextElement()).refreshExtra();

		// kit.getFilterFactory().refresh();

		Iterator it = viewManager.getViews();
		try {
			while (it.hasNext()) {
				View view = (View) it.next();
				view.getController().reload();
				changeMapManagerFactory(view.getController(), defaultMapManagerFactory);
			}
		} catch (ControllerException e) {
			ErrorMessage.showError("Reload error", "Cannot reload all maps.", e, null);
		}
	}

	// FIXME: HACK!!!
	public void pushMark(Set set, Mark mark, Object o) {
		Iterator en = viewManager.getViews();
		while (en.hasNext()) {
			View nextView = (View) en.next();
			if (nextView.getMapScrollPane() != null && nextView.getMapScrollPane().getDisplayer() != null) {
				nextView.getMapScrollPane().getDisplayer().pushMark(set, mark, o);
			}
		}
	}

	public void popMark(Set set, Object o) {
		if (set == null || o == null) {
			return;
		}
		
		Iterator en = viewManager.getViews();
		while (en.hasNext()) {
			View nextView = (View) en.next();
			if (nextView.getMapScrollPane() != null && nextView.getMapScrollPane().getDisplayer() != null) {
				nextView.getMapScrollPane().getDisplayer().popMark(set, o);
			}
		}
	}

	public void exit(int result) {
		resetAll();

    	if (!viewManager.closeable()) {
    		return;
    	}
		viewManager.saveProperties();
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

	public void propertyChange(PropertyChangeEvent e) {
		if (ViewManager.VIEWS_PROPERTY.equals(e.getPropertyName())) {
			if (!viewManager.getViews().hasNext()) {
				try {
					openMapInNewView(URI.create(ConzillaEnvironment.DEFAULT_BLANKMAP), null);
				} catch (ControllerException e1) {
					e1.printStackTrace();
				}
//				exit(0);
			}
		} else if (MapController.MAP_PROPERTY.equals(e.getPropertyName())
				|| View.ZOOM_PROPERTY.equals(e.getPropertyName())) {
			if (ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_PACK)) {
				viewManager.getView((MapController) e.getSource()).pack();
			}
		}
	}

	public void setGlobalFontSize(int size) {
		FontUIResource menuFont = new FontUIResource("Lucida Sans", Font.PLAIN, size);
		FontUIResource textFont = new FontUIResource("Lucida Sans", Font.PLAIN, (int) (size * 1.2));
		setGlobalFont(menuFont, textFont);
		
		String os = (String) System.getProperties().get("os.name");
		if ((os != null) && (os.toLowerCase().matches(".*windows.*"))) {
			// We need this, otherwise the meta-data popups look crap on Windows
			UIManager.put("TextArea.font", new FontUIResource("Lucida Sans", Font.PLAIN, (int) (size * 1.1)));
		}
		
		ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_FONT_SIZE, Integer.toString(size));
	}

	void setGlobalFont(FontUIResource menuFont, FontUIResource textFont) {
		/*
		 * UIManager.put("Button.font", menuFont);
		 * UIManager.put("ToggleButton.font", menuFont);
		 * UIManager.put("RadioButton.font", menuFont); //
		 * UIManager.put("CheckBox.font", font); //
		 * UIManager.put("ColorChooser.font", font);
		 * UIManager.put("ComboBox.font", menuFont); UIManager.put("Label.font",
		 * menuFont); // UIManager.put("List.font", font);
		 * UIManager.put("MenuBar.font", menuFont);
		 * UIManager.put("MenuItem.font", menuFont);
		 * UIManager.put("RadioButtonMenuItem.font", menuFont);
		 * UIManager.put("CheckBoxMenuItem.font", menuFont);
		 * UIManager.put("Menu.font", menuFont); //
		 * UIManager.put("PopupMenu.font", font); //
		 * UIManager.put("OptionPane.font", font); //
		 * UIManager.put("Panel.font", font); //
		 * UIManager.put("ProgressBar.font", font); //
		 * UIManager.put("ScrollPane.font", font); //
		 * UIManager.put("Viewport.font", font);
		 * UIManager.put("TabbedPane.font", menuFont); //
		 * UIManager.put("Table.font", font); //
		 * UIManager.put("TableHeader.font", font);
		 * UIManager.put("TitledBorder.font", menuFont); //
		 * UIManager.put("ToolBar.font", font); // UIManager.put("ToolTip.font",
		 * font); // UIManager.put("Tree.font", font);
		 * 
		 * UIManager.put("TextField.font", textFont); //
		 * UIManager.put("PasswordField.font", font);
		 * UIManager.put("TextArea.font", textFont); //
		 * UIManager.put("TextPane.font", font); //
		 * UIManager.put("EditorPane.font", font);
		 * 
		 * if (viewManager == null) return;
		 * 
		 * Iterator i = viewManager.getViews(); while (i.hasNext()) { View v =
		 * (View) i.next(); v.updateFonts(); if ("true"
		 * .equals(GlobalConfig.getGlobalConfig().getProperty(PACK_PROP)))
		 * v.pack(); }
		 */
	}
	
}