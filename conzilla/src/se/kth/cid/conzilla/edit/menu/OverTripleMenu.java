/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.menu;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.browse.ViewTool;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.BoxLineMapTool;
import se.kth.cid.conzilla.edit.BoxMapTool;
import se.kth.cid.conzilla.edit.ChangeTypeMapTool;
import se.kth.cid.conzilla.edit.EditDetailedMapMapTool;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.EditMapManagerFactory;
import se.kth.cid.conzilla.edit.LinkToContentMapTool;
import se.kth.cid.conzilla.edit.ManageContentMapTool;
import se.kth.cid.conzilla.edit.MoveToLayerMapTool;
import se.kth.cid.conzilla.edit.PathTypeMapTool;
import se.kth.cid.conzilla.edit.RemoveDrawerMapTool;
import se.kth.cid.conzilla.edit.TripleEdit;
import se.kth.cid.conzilla.edit.TripleMapTool;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.tool.MapToolsMenu;


/**
 * Menu that is launched when right-clicking on a triple.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 * @see OverTripleMenu for recomended priorities on tools.
 */
public class OverTripleMenu extends MapToolsMenu
{   
  public OverTripleMenu(MapController controller, TripleEdit tripleEdit, EditMapManager mm)
  {
    super(EditMapManagerFactory.EDIT_RELATION_MENU, EditMapManagerFactory.class.getName(), controller);

    addMapMenuItem(new EditPanel.EditMetadataTool(controller), 100);
    addMapMenuItem(new RemoveDrawerMapTool(controller, mm), 110);
    addMapMenuItem(new ChangeTypeMapTool(controller), 120);
    addMapMenuItem(new EditDetailedMapMapTool(controller), 130);
    MapToolsMenu contentMenu = new MapToolsMenu(EditMapManagerFactory.EDIT_CONTENT_MENU,
            EditMapManagerFactory.class.getName(), controller);
    contentMenu.addMapMenuItem(new LinkToContentMapTool(controller, LinkToContentMapTool.LINK_TO_CONTENT), 100);
    contentMenu.addMapMenuItem(new LinkToContentMapTool(controller, LinkToContentMapTool.LINK_TO_CONTENT_IN_CONTEXT), 150);
    contentMenu.addMapMenuItem(new ManageContentMapTool(controller, mm), 200);    
    ConzillaKit.getDefaultKit().extendMenu(contentMenu, controller);
    addMapMenuItem(contentMenu, 140);

    addSeparator(200);
    addMapMenuItem(new MoveToLayerMapTool(controller), 210);
    addMapMenuItem(new PathTypeMapTool(controller), 220);    
    addMapMenuItem(new BoxMapTool(controller), 230);
    addMapMenuItem(new TripleMapTool(controller), 240);
    addMapMenuItem(new BoxLineMapTool(controller), 250);
    
    addSeparator(900);
    addMapMenuItem(new ViewTool(controller), 910);

    ConzillaKit.getDefaultKit().extendMenu(this, controller);

    /*    addMapMenuItem(new TripleShowMapTool(controller, tripleEdit), 300);
    addMapMenuItem(new TripleHideMapTool(controller), 400);
    addMapMenuItem(new TripleCreateMapTool(controller, tripleEdit), 500);
    addMapMenuItem(new TripleRemoveMapTool(controller), 600);
    */

  }    
}
