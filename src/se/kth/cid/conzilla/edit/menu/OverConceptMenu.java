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
import se.kth.cid.conzilla.edit.RemoveDrawerMapTool;
import se.kth.cid.conzilla.edit.TextAnchorMapTool;
import se.kth.cid.conzilla.edit.TripleEdit;
import se.kth.cid.conzilla.edit.TripleMapTool;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.tool.MapToolsMenu;


/**
 * Menu that is shown when right-clicking on a Concept.
 * The priorities are roughly organized as:
 * <ul><li>Range 100-199 contains tools that work with information.</li>
 * <li>Range 200-799 contains tools that work with presentational issues.</li>
 * <li>Range 700-899 is reserved for the clipboard if there is one.</li>
 * <li>Range 900-999 is reserved for tools that work with navigational aspects.</li>
 * <li>Ranges 0-99, 1000- is reserved for tools originating from unknown extras.<li>
 * </ul>
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class OverConceptMenu extends MapToolsMenu
{
    public OverConceptMenu(MapController controller, TripleEdit tripleEdit, EditMapManager mm)
  {
    super(EditMapManagerFactory.EDIT_CONCEPT_MENU, EditMapManagerFactory.class.getName(), controller);

    addTool(new EditPanel.EditMetadataTool(controller), 100);
    addTool(new RemoveDrawerMapTool(controller, mm), 110);
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
    addTool(new BoxMapTool(controller), 230);
    addTool(new TripleMapTool(controller), 240);
    addTool(new BoxLineMapTool(controller), 250);
    addToolsMenu(new TextAnchorMapTool(controller), 260);
    
    addSeparator(900);
    addTool(new ViewTool(controller), 910);

    ConzillaKit.getDefaultKit().extendMenu(this, controller);

    //    addMapMenuItem(new TripleShowMapTool(controller, tripleEdit), 300);
    //    addMapMenuItem(new TripleCreateMapTool(controller, tripleEdit), 400);

  }    
}
