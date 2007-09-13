/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.content.ContentSelector;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ConzillaTabbedPane;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.LocaleManager;

public class DefaultView implements View, PropertyChangeListener {
    
	private final class Repainter implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if (DefaultView.this.mapScrollPane != null) {
				DefaultView.this.mapScrollPane.repaint();
			}
			ContentSelector selector = DefaultView.this.getController().getContentSelector();
			if (selector != null) {
				selector.getComponent().repaint();
			}
		}
	}

    protected MapController controller;
	MapScrollPane mapScrollPane;
	JPanel mapPanel;
	ConzillaTabbedPane rightPanel = new ConzillaTabbedPane(JTabbedPane.TOP);
	ConzillaTabbedPane leftPanel = new ConzillaTabbedPane(JTabbedPane.TOP);
	ToolsBar toolBar;
	Vector menus;
	Repainter repainter;
	JPanel locationField;
	JTextField location;
	StatusBar statusBar;
	
	public DefaultView(MapController controller) {
		this.controller = controller;
		controller.setView(this);
		menus = new Vector();
		toolBar = new ToolsBar("MAP_TOOLS", MapController.class.getName());
		toolBar.setFloatable(true);
		statusBar = new StatusBar(controller);
		mapPanel = new JPanel();
		mapPanel.setLayout(new BorderLayout());
		repainter = new Repainter();
		locationField = new JPanel();
		locationField.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
		locationField.setLayout(new BoxLayout(locationField, BoxLayout.X_AXIS));
		locationField.add(new JLabel("Context-Map URI:"));
		locationField.add(Box.createHorizontalStrut(5));
		location = new JTextField();
		location.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					URI uri = null;
					try {
						uri = new URI(location.getText().trim());
					} catch (URISyntaxException e1) {
						JOptionPane.showMessageDialog(null, "URI is not valid.", "Could not load context-map", JOptionPane.ERROR_MESSAGE);
					}
					final URI mapURI = uri;
					Thread thread = new Thread(new Runnable() {
						public void run() {
							try {
								if (mapURI.isAbsolute()) {
									MapController controller = DefaultView.this.controller;
									ContextMap oldMap = controller.getConceptMap();
									DefaultView.this.controller.showMap(mapURI);
									controller.getHistoryManager().fireOpenNewMapEvent(controller, oldMap, mapURI);
								} else {
									JOptionPane.showMessageDialog(null, "URI is not absolute, you need to specify a scheme e.g. \"http://\" or \"urn:path://\".", "Could not load context-map", JOptionPane.ERROR_MESSAGE);							
								}
							} catch (ControllerException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Could not load context-map", JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					thread.start();
				}
			}
		});
		location.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		locationField.add(location);
        controller.addPropertyChangeListener(this);
        controller.addPropertyChangeListener(statusBar);
		ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, repainter);
        LocaleManager.getLocaleManager().addPropertyChangeListener(this);
	}

	public MapController getController() {
		return controller;
	}

	public MapScrollPane getMapScrollPane() {
		return mapScrollPane;
	}
	
	public void setMap(MapStoreManager storeManager) {

		MapScrollPane oldPane = mapScrollPane;
		mapScrollPane = new MapScrollPane(new MapDisplayer(storeManager));

		embeddMapPanel();
		location.setText(DefaultView.this.controller.getConceptMap().getURI());

		if (oldPane != null) {
			mapScrollPane.getDisplayer().setScale(oldPane.getDisplayer().getScale());
			oldPane.getDisplayer().getStoreManager().detach();
			oldPane.getDisplayer().detach();
			oldPane.detach();
		}

		mapPanel.revalidate();
		mapPanel.repaint();
		toolBar.revalidate();
	}
	
	public void embeddMapPanel() {
		mapPanel.removeAll();
		if (controller.getManager() != null) {
			mapPanel.add(controller.getManager().embeddMap(mapScrollPane), BorderLayout.CENTER);
		} else {
			mapPanel.add(mapScrollPane, BorderLayout.CENTER);
		}

		mapPanel.revalidate();
		mapPanel.repaint();
	}

	/**
	 * Adds a component in a tab to the right panel.
	 * 
	 * @param comp the component to add.
	 * @param title the label displayed in the tab.
	 * @param al an action listener that will be called when the tab is closed, 
	 * if null there will be no close button.
	 */
	public void addToRight(final Component comp, String title,
			final ActionListener al) {
		AbstractAction aa = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				controller.firePropertyChange(RIGHT_PANE_PROPERTY, null, comp);
				if (al != null) {
					al.actionPerformed(e);
				}
			}
		};
		rightPanel.addTab(comp, title, aa, al != null);
		controller.firePropertyChange(RIGHT_PANE_PROPERTY, null, comp);
	}

	/**
	 * Removes the given component from the right panel.
	 * 
	 * @param comp the component to remove.
	 */
	public void removeFromRight(Component comp) {
		rightPanel.removeTab(comp);
	}

	public ConzillaTabbedPane getRightPanel() {
		return rightPanel;
	}

	/**
	 * Adds a component in a tab to the left panel.
	 * 
	 * @param comp the component to add.
	 * @param title the label displayed in the tab.
	 * @param al an action listener that will be called when the tab is closed, 
	 * if null there will be no close button.
	 */
	public void addToLeft(final Component comp, String title,
			final ActionListener al) {
		AbstractAction aa = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				controller.firePropertyChange(LEFT_PANE_PROPERTY, null, comp);
				if (al != null) {
					al.actionPerformed(e);
				}
			}
		};
		leftPanel.addTab(comp, title, aa, al != null);
		controller.firePropertyChange(LEFT_PANE_PROPERTY, null, comp);
	}

	/**
	 * Removes the given component from the left panel.
	 * 
	 * @param comp the component to remove.
	 */
	public void removeFromLeft(Component comp) {
		leftPanel.removeTab(comp);
	}

	public ConzillaTabbedPane getLeftPanel() {
		return leftPanel;
	}
	
	public JPanel getMapPanel() {
		return mapPanel;
	}

	public ToolsBar getToolsBar() {
		return toolBar;
	}

	public ToolsMenu[] getMenus() {
		return (ToolsMenu[]) menus.toArray(new ToolsMenu[menus.size()]);
	}

	public ToolsMenu getMenu(String name) {
		for (int i = 0; i < menus.size(); i++) {
			ToolsMenu m = (ToolsMenu) menus.get(i);
			if (m.getName().equals(name))
				return m;
		}
		return null;
	}

	public void addMenu(ToolsMenu menu, int prio) {
		menu.putClientProperty("menus_prio", new Integer(prio));
		menus.add(menu);
		sortMenus();
		controller.firePropertyChange(MENUS_PROPERTY, null, menu);
	}

	public void removeMenu(ToolsMenu menu) {
		if (menus.remove(menu))
			controller.firePropertyChange(MENUS_PROPERTY, null, menu);
	}

	void sortMenus() {
		Collections.sort(menus, new Comparator() {
			public int compare(Object o1, Object o2) {
				int p1 = ((Integer) ((ToolsMenu) o1)
						.getClientProperty("menus_prio")).intValue();
				int p2 = ((Integer) ((ToolsMenu) o2)
						.getClientProperty("menus_prio")).intValue();
				return p1 - p2;
			}
		});
	}
	
	public void setScale(double newscale) {
		double oldscale = mapScrollPane.getDisplayer().getScale();
		mapScrollPane.setScale(newscale);
		controller.firePropertyChange(ZOOM_PROPERTY, new Double(oldscale),
				new Double(newscale));
	}

	public void zoomMap(double factor) {
		setScale(mapScrollPane.getDisplayer().getScale() * factor);
	}

	public void detach() {
		if (mapScrollPane != null) {
			mapScrollPane.getDisplayer().detach();
			mapScrollPane.detach();
			mapPanel.remove(mapScrollPane);
		}
		if (menus != null) {
			for (int i = 0; i < menus.size(); i++) {
				((ToolsMenu) menus.get(i)).detach();
			}
		}

		menus = null;
        controller.removePropertyChangeListener(this);
        controller.removePropertyChangeListener(statusBar);
        LocaleManager.getLocaleManager().removePropertyChangeListener(this);
		ConfigurationManager.getConfiguration().removePropertyChangeListener(repainter);
	}
	
	public void draw() {
	}

	public void pack() {
	}

	public void updateFonts() {		
	}

	public JPanel getLocationField() {
		return locationField;
	}
	
	public StatusBar getStatusBar() {
		return statusBar;
	}

    public void propertyChange(PropertyChangeEvent e) {
        if (MapController.MAP_PROPERTY.equals(e.getPropertyName())) {
			location.setText(DefaultView.this.controller.getConceptMap().getURI());
            updateTitle();
        }
        if (LocaleManager.DEFAULT_LOCALE_PROPERTY.equals(e.getPropertyName())) {
            updateTitle();
        }
    }

	protected void updateTitle() {
	}
}