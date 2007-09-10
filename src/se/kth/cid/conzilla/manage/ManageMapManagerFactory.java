/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.manage;

import java.net.URI;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.controller.MapManager;
import se.kth.cid.conzilla.controller.MapManagerFactory;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.ToolsMenu;

/** This class creates BroweMapManagers for a single MapController.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ManageMapManagerFactory implements MapManagerFactory {
    ConzillaKit kit;
    SessionManager sessionManager;
    
    public ManageMapManagerFactory() {
    }

    public String getName() {
        return "ManageMapManagerFactory";
    }

    public boolean initExtra(ConzillaKit kit) {
        this.kit = kit;
        return true;
    }

    public boolean canManage(MapController mc, URI map) {
        return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController mc) {
    }

    public void addExtraFeatures(MapController c) {
    }

    public void refreshExtra() {
    }

    public boolean saveExtra() {
        return true;
    }

    public void exitExtra() {
    }

    public MapManager createManager(MapController controller) {
    	if(sessionManager == null) {
    		sessionManager = ConzillaKit.getDefaultKit().getSessionManager();
    	}
    	return new ManageMapManager(controller, sessionManager);
    }

	public boolean requiresSession() {
		return false;
	}

}
