/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;

/**
 * This class is a menu that supports adding tools as buttons.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class MapToolsMenu extends ToolsMenu {
	protected MapController controller;

	public MapToolsMenu(String title, String resbundle, MapController cont) {
		super(title, resbundle);
		controller = cont;
	}
	
	public void updateBeforePopup() {
		addJITTools();
		sortMenu();
	}
	
	public void updateAfterPopup() {		
		removeJITTools();
	}
	
	public void update(MapEvent mapEvent) {

		Iterator tools= getTools().iterator();
		while (tools.hasNext()) {
			Object entry = tools.next();
			if (entry instanceof Tool) {
				((Tool) entry).update(mapEvent);
			} else if(entry instanceof MapToolsMenu) {
				((MapToolsMenu) entry).update(mapEvent);
			}
		}
	}

	public void popup(MapEvent mapEvent) {
		update(mapEvent);

		SwingUtilities.updateComponentTreeUI(this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension choiceSize = getPopupMenu().getSize();

		if (choiceSize.width == 0)
			choiceSize = getPopupMenu().getPreferredSize();

		Point p = new Point(mapEvent.mouseEvent.getX(), mapEvent.mouseEvent
				.getY());
		SwingUtilities.convertPointToScreen(p, controller.getView()
				.getMapScrollPane().getDisplayer());

		if (p.x + choiceSize.width >= screenSize.width)
			p.x -= choiceSize.width;

		if (p.y + choiceSize.height >= screenSize.height)
			p.y -= choiceSize.height;

		SwingUtilities.convertPointFromScreen(p, controller.getView()
				.getMapScrollPane().getDisplayer());

		getPopupMenu().show(
				controller.getView().getMapScrollPane().getDisplayer(), p.x,
				p.y);
	}
}
