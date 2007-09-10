/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManager;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.layer.LayersTool;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;

/** This class creates BroweMapManagers for a single MapController.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class BrowseMapManagerFactory implements MapManagerFactory {
	
    public static final String BROWSE_MENU = "BROWSEMENU";

    ConzillaKit kit;

    public BrowseMapManagerFactory() {
    }

    public String getName() {
        return "BrowseMapManagerFactory";
    }

    public boolean initExtra(ConzillaKit kit) {
        this.kit = kit;
        return true;
    }

    public boolean canManage(MapController mc, URI map) {
        return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController mc) {
        if (menu.getName().equals(DefaultMenuFactory.FILE_MENU)) {
            Tool t =
                new Tool("OPEN_MAP", BrowseMapManagerFactory.class.getName()) {
            	{setIcon(Images.getImageIcon(Images.ICON_FILE_OPEN));}
                public void actionPerformed(ActionEvent ae) {
                	String uriString = JOptionPane.showInputDialog("Give the URI of the map:");
                	if (uriString != null) {
                		try {
                			URI uri = new URI(uriString);
                			ContextMap oldMap = mc.getConceptMap();
                			mc.showMap(uri);
                			mc.getHistoryManager().fireOpenNewMapEvent(mc, oldMap, uri);
                		} catch (URISyntaxException e) {
                			ErrorMessage.showError("URI not valid", "You provided a non valid URI.", e, null);
                		} catch (ControllerException e) {
                			ErrorMessage.showError("URI not valid", "You provided a non valid URI.", e, null);
                		}
                	}
                }
            };
            t.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
            menu.addTool(t, 160);
        }
        if (menu.getName().equals(DefaultMenuFactory.TOOLS_MENU)) {
            menu.addTool((Tool) mc.get("ContributionsTool"), 100);
            menu.addTool((Tool) mc.get("LayerTool"), 200);
        }
    }

    public void addExtraFeatures(MapController c) {
        ContributionsTool contributions = new ContributionsTool(c);
    	c.put("ContributionsTool", contributions);
		ToolsBar bar = c.getView().getToolsBar();
		bar.addTool(contributions);
	   	LayersTool lt = new LayersTool(c);
    	c.put("LayerTool", lt);
		bar.addTool(lt);
 
    }

    public void refreshExtra() {
    }

    public boolean saveExtra() {
        return true;
    }

    public void exitExtra() {
    }

    public MapManager createManager(MapController controller) {
        return new BrowseMapManager(controller);
    }

	public boolean requiresSession() {
		return false;
	}

}
