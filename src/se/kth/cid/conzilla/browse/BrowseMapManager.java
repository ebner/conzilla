/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JToolBar.Separator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaEnvironment;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.FullScreenTool;
import se.kth.cid.conzilla.app.OnlineStateTool;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManager;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.history.LinearHistory;
import se.kth.cid.conzilla.history.LinearHistoryManager;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.util.LocaleChooser;

/**
 * This class creates BroweMapManagers for a single MapController.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class BrowseMapManager implements MapManager, PropertyChangeListener {

    Tool zoomIn;
    Tool zoomOut;
    Tool home;
    Tool defaultHome;
    Tool onlineState;
    Tool fullScreen;
    Tool newWindow;
    Tool editMode;
    Tool reload;
//    Tool contributions;
//    Tool sessions;
    
    Highlighter highlighter;
    PopupLayer popup;
    PopupControlTool popupControl;
    Browse browse;
    LinearHistoryManager linearHistoryManager;
    ToolsMenu goMenu;
    MapController controller;
    LocaleChooser localeChooser;
	private Separator separator1;
	private Separator separator2;
	private Separator separator3;
	private Separator separator4;
	private Separator separator5;
	private Separator separator6;

    public BrowseMapManager(final MapController controller) {
    	this.controller = controller;
    	View view = controller.getView();
        final ConzillaKit kit = ConzillaKit.getDefaultKit();
        newWindow = new NewWindowTool(controller);
        popup = new PopupLayer(controller);
        browse = new Browse(controller, popup);
        zoomIn = new ZoomTool(controller, 1.3);
        zoomOut = new ZoomTool(controller, 1 / 1.3);
        highlighter = new Highlighter(controller);
        popupControl = new PopupControlTool(controller, popup);
        localeChooser = new LocaleChooser();
        fullScreen = new FullScreenTool(view.getToolsBar());
        reload = new ReloadTool(controller);
        // We don't need this tool if we don't have disk access (e.g. as applet),
        // as it is directly connected to the DiskContainerCache.
        if (ConzillaKit.getDefaultKit().getConzillaEnvironment().hasLocalDiskAccess()) {
        	onlineState = new OnlineStateTool();
        }
        
        home = new Tool("HOME", BrowseMapManagerFactory.class.getName()) {
            private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
                try {
                    kit.getConzilla().openMapInOldView(
                            ConfigurationManager.getConfiguration().getURI(Settings.CONZILLA_STARTMAP, new URI(ConzillaEnvironment.DEFAULT_STARTMAP)),
                            kit.getConzilla()
                                    .getViewManager().getView(controller));
                } catch (ControllerException e) {
                    ErrorMessage.showError("Cannot load map",
                            "Cannot load map", e, controller.getView().getToolsBar());
                } catch (URISyntaxException e) {
                    ErrorMessage.showError("Cannot load map",
                            "Cannot load map", e, controller.getView().getToolsBar());
                }
            }
        };
        home.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HOME,
                Event.ALT_MASK));
        home.setIcon(Images.getImageIcon(Images.ICON_HOME));
        
        editMode = new Tool("EDIT_MODE", BrowseMapManagerFactory.class.getName()) {
			public void actionPerformed(ActionEvent ev) {
				Enumeration extras = ConzillaKit.getDefaultKit().getExtras();
				while (extras.hasMoreElements()) {
					Object nextExtra = extras.nextElement();
					if (nextExtra instanceof MapManagerFactory) {
						MapManagerFactory mmf = (MapManagerFactory) nextExtra;
						if (mmf.getName().equals("EditMapManagerFactory")) {
							ConzillaKit.getDefaultKit().getConzilla().changeMapManagerFactory(controller, mmf);
						}
					}
				}
            }
        };
        editMode.setIcon(Images.getImageIcon(Images.ICON_FILE_EDIT));

        defaultHome = new Tool("DEFAULT_HOME", BrowseMapManagerFactory.class.getName()) {
            private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
                try {
                    kit.getConzilla()
                            .openMapInOldView(new URI(ConzillaEnvironment.DEFAULT_STARTMAP),
                                    kit.getConzilla()
                                            .getViewManager().getView(
                                                    controller));
                } catch (URISyntaxException urise) {
                	urise.printStackTrace();
                } catch (ControllerException e) {
                    ErrorMessage.showError("Cannot load map",
                            "Cannot load map", e, controller.getView().getToolsBar());
                }
            }
        };
        linearHistoryManager = new LinearHistoryManager(controller);
        goMenu = new ToolsMenu("GO", BrowseMapManagerFactory.class.getName());
        goMenu.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                updateGoMenu();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
        });
    }

    public void install() {
    	View view = controller.getView();
        view.getController().addPropertyChangeListener(this);
        view.getMapScrollPane().setPanningState(true);
        highlighter.install();
        
        ToolsBar bar = view.getToolsBar();
        
        bar.addTool(editMode);
        
        separator1 = new JToolBar.Separator(null);
        bar.add(separator1);

        bar.addTool(newWindow);
        
        separator2 = new JToolBar.Separator(null);
        bar.add(separator2);
        
        linearHistoryManager.createTools(bar);
        bar.addTool(reload);
        bar.addTool(home);
        
        separator3 = new JToolBar.Separator(null);
        bar.add(separator3);
        
        bar.addTool(zoomIn);
        bar.addTool(zoomOut);
        
        separator4 = new JToolBar.Separator(null);
        bar.add(separator4);
        
        bar.addTool(highlighter.sHigh);
        bar.addTool(highlighter.vHigh);
        bar.addTool(highlighter.iHigh);
        bar.addTool(popupControl);
        
        separator5 = new JToolBar.Separator(null);
        bar.add(separator5);
        
        if (onlineState != null) {
        	bar.addTool(onlineState);
        }
        bar.addTool(fullScreen);
        
        separator6 = new JToolBar.Separator(null);
        bar.add(separator6);
        
        bar.add(localeChooser);

        browse.install(view.getMapScrollPane());
        if (popupControl.isActivated()) {
    		popup.activate(view.getMapScrollPane());
        }

        updateGoMenu();

        view.addMenu(goMenu, 35);
        //view.addToLeft(view.getController().getContainerEntries(), "Contributions", null);
    }

    public void deInstall() {
    	View view = controller.getView();
    	view.removeFromLeft(view.getController().getContainerEntries());
        view.getMapScrollPane().setPanningState(false);
        view.removeMenu(goMenu);
        goMenu.removeTool(linearHistoryManager.getBackTool());
        goMenu.removeTool(linearHistoryManager.getForwardTool());
        goMenu.removeTool(home);
       // goMenu.removeTool(defaultHome);

        goMenu.detach();

        ToolsBar bar = view.getToolsBar();

        linearHistoryManager.detachTools(bar);
        linearHistoryManager.detach();
        bar.removeTool(newWindow);
        bar.removeTool(editMode);
        bar.removeTool(home);
        bar.removeTool(reload);
        bar.removeTool(zoomIn);
        bar.removeTool(zoomOut);
        bar.removeTool(highlighter.sHigh);
        bar.removeTool(highlighter.vHigh);
        bar.removeTool(highlighter.iHigh);
        bar.removeTool(popupControl);
        if (onlineState != null) {
        	bar.removeTool(onlineState);
        }
        bar.removeTool(fullScreen);
        bar.remove(localeChooser);
        browse.uninstall(view.getMapScrollPane());
		popup.deactivate(view.getMapScrollPane());

//        browse.detach();

        view.getController().removePropertyChangeListener(this);
        highlighter.deInstall();
        
        bar.remove(separator1);
        bar.remove(separator2);
        bar.remove(separator3);
        bar.remove(separator4);
        bar.remove(separator5);
        bar.remove(separator6);
        
     //   bar.removeAll(); // to remove separators as well
    }

    void updateGoMenu() {
        goMenu.removeAllTools();
        goMenu.removeAll();

        goMenu.addTool(linearHistoryManager.getBackTool(), 100);
        goMenu.addTool(linearHistoryManager.getForwardTool(), 200);
        goMenu.addTool(home, 300);
      //  goMenu.addTool(defaultHome, 400);
        goMenu.addSeparator(500);

        LinearHistory history = controller.getLinearHistory();

        String[] forwardTitles = controller.getLinearHistory()
                .getForwardMapTitles();

        int i;
        for (i = 0; i < forwardTitles.length; i++) {
            final int index = history.getIndex() + 1 + i;
            Tool item = new Tool(forwardTitles[i], null) {
                private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
                    linearHistoryManager.controlledJump(index);
                }
            };
            goMenu.addTool(item, 700 - i);
        }
        JMenuItem mi = new JMenuItem(history.getMapTitle(history.getIndex())
                + "    <-- current");
        mi.setEnabled(false);
        goMenu.add(mi);
        goMenu.setPriority(mi, 701);

        String[] backTitles = controller.getLinearHistory()
                .getBackwardMapTitles();

        for (i = 0; i < backTitles.length; i++) {
            final int index = history.getIndex() - 1 - i;
            Tool item = new Tool(backTitles[i], null) {
                private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
                    linearHistoryManager.controlledJump(index);
                }
            };
            goMenu.addTool(item, 900 + i);
        }
    }
    
    public void gotFocus() {
        //Do nothing... for now.
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(MapController.MAP_PROPERTY)) {
            controller.getView().getMapScrollPane().setPanningState(true);
    		popup.deactivate((MapScrollPane) e.getOldValue());
            browse.uninstall((MapScrollPane) e.getOldValue());
            browse.install((MapScrollPane) e.getNewValue());
            editMode.setEnabled(!(((MapScrollPane) e.getNewValue()).getDisplayer()
            		.getStoreManager().getConceptMap().getURI()
            		.equals(ConzillaEnvironment.DEFAULT_BLANKMAP)));
            if (popupControl.isActivated()) {
        		popup.activate((MapScrollPane) e.getNewValue());            	
            }
            highlighter.sHigh.propertyChange(e);
            highlighter.vHigh.propertyChange(e);
        }
    }

	public JComponent embeddMap(MapScrollPane map) {
		return map;
	}
}