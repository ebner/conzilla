/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;
import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.metadata.InfoPanel;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.conzilla.tool.MapToolsMenu;

public class BrowseMenu extends MapToolsMenu
{
  public BrowseMenu(MapController controller, Browse browse)
  {
    super(BrowseMapManagerFactory.BROWSE_MENU, BrowseMapManagerFactory.class.getName(), controller);

    addMapMenuItem(new SurfAlterationTool(controller, browse), 100);
    //addMapMenuItem(new ViewAlterationTool("VIEW", BrowseMapManagerFactory.class.getName(), cont), 200);
    addMapMenuItem(new ViewTool(controller), 200);
    addMapMenuItem(new ActionMapMenuTool("INFO", BrowseMapManagerFactory.class.getName(), controller) {
        protected boolean updateEnabled() {
            return true;
        }

        public void actionPerformed(ActionEvent arg0) {
            if (mapEvent.hitType == MapEvent.HIT_NONE)  {
                InfoPanel.launchInfoPanelInFrame(BrowseMenu.this.controller.getConceptMap(), EditPanel.context_form);
            } else {
                InfoPanel.launchInfoPanelInFrame(mapObject.getConcept(), EditPanel.concept_form);   
            }
        }
    },300);

    ConzillaKit.getDefaultKit().extendMenu(this, controller);
  }
}
