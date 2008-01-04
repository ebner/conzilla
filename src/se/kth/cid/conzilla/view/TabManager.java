/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;
import se.kth.cid.util.LocaleManager;

public class TabManager
    extends AbstractViewManager
    implements ChangeListener, PropertyChangeListener {
    RootPaneContainer rootPaneContainer;
    JTabbedPane tabbedPane;
//    Hashtable bars;
    ConzillaSplitPane conzillaSplitPane;
    Hashtable tab2View;
    boolean block;
    JFrame frame;

    public TabManager() {
    }

    public String getID() {
        return "TAB_VIEW";
    }

    public void initManager() {
        super.initManager();
        
        initTabs();
        initDefaultRootPaneContainer();
        
        LocaleManager.getLocaleManager().addPropertyChangeListener(this);
    }
    
    public void setRootPaneContainer(RootPaneContainer rpc) {
        rootPaneContainer = rpc;
        rootPaneContainer.setContentPane(conzillaSplitPane);
        reActivateTab();
        conzillaSplitPane.fixDividerLocation();
    }
    
    public int getViewCount() {
    	return views.size();
    }
    
    protected void initDefaultRootPaneContainer() {
        frame = new JFrame("Conzilla");
        //Pane workaround for mac, mac-menus is moved up and can't contain buttons.
        frame.setContentPane(conzillaSplitPane);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        restoreSizeAndLocation(frame);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	ConzillaKit.getDefaultKit().getConzilla().exit(0);
            }
        });
        
        rootPaneContainer = frame;
    }
    
    public void saveProperties() {
    	saveSizeAndLocation(frame);
    }
    
    protected void initTabs() {
        conzillaSplitPane = new ConzillaSplitPane();        
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) {
					java.awt.Component c = tabbedPane.getSelectedComponent();
					if (c != null) {
						TabView view = (TabView) tab2View.get(c);
						if (view != null) {
							ConzillaKit.getDefaultKit().getConzilla().close(view);
						}
					}
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
        });
        tab2View = new Hashtable();
    }

    public void detachManager() {
        super.detachManager();
        if (rootPaneContainer instanceof Window) {
            ((Window) rootPaneContainer).dispose();   
        }
        rootPaneContainer = null;
        tabbedPane.removeAll();
        tabbedPane = null;
        conzillaSplitPane.detach();
        conzillaSplitPane = null;

        tab2View = null;
        LocaleManager.getLocaleManager().removePropertyChangeListener(this);
    }

    public View newView(MapController controller) {
        block = true;
        if (rootPaneContainer instanceof Window) {
            if (!((Window) rootPaneContainer).isShowing()) {
            	// We don't show it here - We show it from ConzillaApp
            	// to avoid flickering when building the UI
            	//((Window) rootPaneContainer).setVisible(true);
            }
        }

        TabView tv = new TabView(this, controller);

        tabbedPane.add(getTitle(tv), tv.getMapPanel());
        tabbedPane.setSelectedComponent(tv.getMapPanel());
        addView(tv);

        controller.addPropertyChangeListener(this);
        tab2View.put(tv.getMapPanel(), tv);

        activateTab(tv);

        block = false;
        updateTitle(tv);
        return tv;
    }

    public void reActivateTab() {
        java.awt.Component c = tabbedPane.getSelectedComponent();
        if (c != null) {
            TabView view = (TabView) tab2View.get(c);
            activateTab(view);
            updateTitle(view);
        }
    }
    
    public void activateTab(TabView tv) {
        MapController controller = tv.getController();
        conzillaSplitPane.setPanes(
        		tv.getLeftPanel(),
        		tabbedPane,
        		tv.getRightPanel(),
        		tv);
        conzillaSplitPane.setToolBar(tv.getToolsBar());
        conzillaSplitPane.setLocationField(tv.getLocationField());
        conzillaSplitPane.setStatusBar(tv.getStatusBar());

        if (controller.getManager() != null) {
        	controller.getManager().gotFocus();
        }
        if (rootPaneContainer != null) {
            rootPaneContainer.getRootPane().setJMenuBar(makeMenuBar(tv, false));
        }
        tv.getMapPanel().setPreferredSize(null);

        if (ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_PACK, true)) {
            tv.pack();
        }

        if (rootPaneContainer != null) {
            rootPaneContainer.getRootPane().validate();
        }
        
        conzillaSplitPane.fixDividerLocation();
    }

    protected void updateFonts() {
        SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(rootPaneContainer.getRootPane()));
    }

    protected void pack() {
        JComponent active = (JComponent) tabbedPane.getSelectedComponent();
        Dimension pref = active.getPreferredSize();
        Enumeration en = tab2View.keys();
        while (en.hasMoreElements()) {
            JComponent c = (JComponent) en.nextElement();
            if (c != active)
                c.setPreferredSize(pref);
        }
        
        if (rootPaneContainer instanceof Window) {
            ((Window) rootPaneContainer).pack();   
        }
    }

    public void stateChanged(ChangeEvent ce) {
        if (block || (tabbedPane.getSelectedComponent() == null))
            return;
        TabView view =
            ((TabView) tab2View.get(tabbedPane.getSelectedComponent()));
        activateTab(view);
    }

    public void updateTitle(TabView view) {
        int index = tabbedPane.indexOfComponent(view.getMapPanel());
        tabbedPane.setTitleAt(index, getTitle(view));
    }

    private String getTitle(View view) {
        String title = "(none)";
        if (view.getMapScrollPane() != null) {
            ContextMap map = view.getController().getConceptMap();

            title = AttributeEntryUtil.getTitleAsString(map);
            if (title == null) {
                title = "(none)";
            }
        }
        return title;
    }

    public void propertyChange(PropertyChangeEvent e) {
    	if (e.getPropertyName().equals(LocaleManager.DEFAULT_LOCALE_PROPERTY)) {
    		Iterator en = getViews();
    		while (en.hasNext()) {
    			updateTitle((TabView) en.next());
    		}
    	} else if (e.getPropertyName().equals(View.MENUS_PROPERTY)) {
    		TabView actview = ((TabView) tab2View.get(tabbedPane.getSelectedComponent()));
    		activateTab(actview);
    	} else if (e.getPropertyName().equals(MapController.MAP_PROPERTY)) {
    		revalidate();
    	}
    }

    protected void closeView(View v, boolean closeController) {
        tabbedPane.remove(v.getMapPanel());

        v.getMapPanel().setPreferredSize(null);
        v.getController().removePropertyChangeListener(this);
        tab2View.remove(v.getMapPanel());

        java.awt.Component c = tabbedPane.getSelectedComponent();
        if (c != null) {
            TabView view = (TabView) tab2View.get(c);
            activateTab(view);
        }
        if (closeController) {
        	v.getController().detach();
        } else {
        	v.detach();
        }

        //((TabView) v).close(closeController);
    }
    
    public JComponent getSinglePane() {
        return conzillaSplitPane;
    }
    
    public void revalidate() {
    	super.revalidate();
    	reActivateTab();
    	conzillaSplitPane.fixDividerLocation();
    	rootPaneContainer.getRootPane().revalidate();
    }
    
    public Window getWindow() {
    	return frame;
    }
    
}