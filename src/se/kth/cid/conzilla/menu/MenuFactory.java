/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.menu;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;


public interface MenuFactory
{        
    String FILE_MENU = "FILE";
    String VIEW_MENU = "VIEW";
    String SETTINGS_MENU = "SETTINGS";
    String TOOLS_MENU = "TOOLS";
    String HELP_MENU = "HELP";

    void initFactory(ConzillaKit kit);
    void addMenus(MapController c);
    void addExtraMenu(String name, String nameOfResourceBundle, int priority);
}
