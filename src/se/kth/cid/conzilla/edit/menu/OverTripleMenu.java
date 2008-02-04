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
import se.kth.cid.conzilla.edit.RemoveApperanceTool;
import se.kth.cid.conzilla.edit.RemoveConditionalTool;
import se.kth.cid.conzilla.edit.RemoveNonConditionalTool;
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

    addTool(new EditPanel.EditMetadataTool(controller), 100);
    addTool(mm.remove, 110);
    addTool(mm.removeFromMap, 111);
    addTool(mm.removeFromSession, 112);
    addTool(new ChangeTypeMapTool(controller), 120);
    addTool(new EditDetailedMapMapTool(controller), 130);
    MapToolsMenu contentMenu = new MapToolsMenu(EditMapManagerFactory.EDIT_CONTENT_MENU,
            EditMapManagerFactory.class.getName(), controller);
    contentMenu.addTool(new LinkToContentMapTool(controller, LinkToContentMapTool.LINK_TO_CONTENT), 100);
    contentMenu.addTool(new LinkToContentMapTool(controller, LinkToContentMapTool.LINK_TO_CONTENT_IN_CONTEXT), 150);
    contentMenu.addTool(new ManageContentMapTool(controller, mm), 200);    
    ConzillaKit.getDefaultKit().extendMenu(contentMenu, controller);
    addToolsMenu(contentMenu, 140);

    addSeparator(200);
    addToolsMenu(new MoveToLayerMapTool(controller), 210);
    addTool(new PathTypeMapTool(controller), 220);    
    addTool(new BoxMapTool(controller), 230);
    addTool(new TripleMapTool(controller), 240);
    addTool(new BoxLineMapTool(controller), 250);
    
    addSeparator(280);
    addTool(mm.copy, 283);
    addTool(mm.cut, 285);

    addSeparator(900);
    addTool(new ViewTool(controller), 910);

    ConzillaKit.getDefaultKit().extendMenu(this, controller);

    /*    addMapMenuItem(new TripleShowMapTool(controller, tripleEdit), 300);
    addMapMenuItem(new TripleHideMapTool(controller), 400);
    addMapMenuItem(new TripleCreateMapTool(controller, tripleEdit), 500);
    addMapMenuItem(new TripleRemoveMapTool(controller), 600);
    */

  }    
}
