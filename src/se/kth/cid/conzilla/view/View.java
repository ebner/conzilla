/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ConzillaTabbedPane;

/** 
 * This is the common interface for frames, single window view, 
 * splitpane view and internal frame view.
 *  
 * NOTE: When a view get focus, the MapManager must be informed via the function 
 * {@link se.kth.cid.conzilla.controller.MapManager#gotFocus()},
 * either the view itself or the {@link se.kth.cid.conzilla.view.ViewManager} 
 * should be responsible for this.
 * 
 * There is a strong connection to the {@link ViewManager} which controls
 * several views.
 *
 * @author Matthias Palmer.
 */
public interface View
{
	String MENUS_PROPERTY = "menus";
	String ZOOM_PROPERTY = "zoom";
	String RIGHT_PANE_PROPERTY = "right pane";
	String LEFT_PANE_PROPERTY = "left pane";

    MapController getController();
    MapScrollPane getMapScrollPane();
    void setMap(MapStoreManager mapManager);

	void updateFonts();
    void draw();
    void pack();
    
    ConzillaTabbedPane getRightPanel();
    void addToRight(Component tab, String label, ActionListener listener);
    void removeFromRight(Component tab);
    
    ConzillaTabbedPane getLeftPanel();
    void addToLeft(Component tab, String label, ActionListener listener);
    void removeFromLeft(Component tab);
    
    void embeddMapPanel();
    JPanel getMapPanel();
    ToolsBar getToolsBar();
    JPanel getLocationField();
    
    ToolsMenu[] getMenus();
    void addMenu(ToolsMenu menu, int priority);
    void removeMenu(ToolsMenu menu);
    
    void setScale(double newScale);
    void zoomMap(double factor);
        
    void detach();
}
