/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.menu;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.EditMapManagerFactory;
import se.kth.cid.conzilla.edit.InsertConceptMapTool;
import se.kth.cid.conzilla.edit.RemoveContextMapMapTool;
import se.kth.cid.conzilla.edit.ResourceInsertionMenu;
import se.kth.cid.conzilla.metadata.EditPanel;
import se.kth.cid.conzilla.tool.MapToolsMenu;

/**
 * Menu that is shown when right-clicking on the ContextMap, i.e. the
 * background.
 * 
 * @version $Revision$, $Date$
 * @author matthias
 * @see se.kth.cid.conzilla.edit.menu.OverConceptMenu for recomended priorities
 *      on tools.
 */
public class OverBackgroundMenu extends MapToolsMenu {
	
	private static final long serialVersionUID = 1L;

	public OverBackgroundMenu(MapController controller, EditMapManager mm) {
		super(EditMapManagerFactory.EDIT_MAP_MENU, EditMapManagerFactory.class.getName(), controller);

		addTool(new EditPanel.EditMetadataTool(controller), 100);
		addTool(new RemoveContextMapMapTool(controller), 110);
		addTool(new InsertConceptMapTool(controller, mm.getGridModel()), 120);
		addToolsMenu(new ResourceInsertionMenu(controller, mm.getGridModel()), 130);
		ConzillaKit.getDefaultKit().extendMenu(this, controller);

		// Not yet mature...
		//addMapMenuItem(new MapTreeDisplayer.MapTreeDisplayTool(controller), 50);
	}
}