/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class BoxMapTool extends Tool {

	static final String BOX_VISIBLE = "HIDE_BOX";

	static final String BOX_INVISIBLE = "SHOW_BOX";

	String currentName;

	public BoxMapTool(MapController cont) {
		super(BOX_VISIBLE, EditMapManagerFactory.class.getName(), cont);
		currentName = BOX_VISIBLE;
	}

	protected boolean updateEnabled() {

		DrawerLayout dl = mapEvent.mapObject.getDrawerLayout();
		if (mapEvent == null || mapObject == null || !dl.getBodyVisible())
			currentName = BOX_INVISIBLE;
		else
			currentName = BOX_VISIBLE;

/*		ConzillaResourceManager.getDefaultManager().customizeButton(
				getJMenuItem(), EditMapManagerFactory.class.getName(),
				currentName);*/
		setTitleAndTooltip(currentName, EditMapManagerFactory.class.getName());

		if (!(dl instanceof StatementLayout))
			return false;
		else if (((StatementLayout) dl).getLine().length == 0)
			return false;

		if (mapEvent.hitType == MapEvent.HIT_NONE)
			return false;
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (mapObject.getDrawerLayout().getBodyVisible())
			if (!(mapObject.getDrawerLayout() instanceof StatementLayout)
					|| ((StatementLayout) mapObject.getDrawerLayout())
							.getLine() == null) {
				if (JOptionPane
						.showConfirmDialog(
								null,
								"This concept will be removed since invisible concepts can't be handled.",
								"Concept removal", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
					mapObject.getDrawerLayout().remove();
			} else {
				mapObject.getDrawerLayout().setBodyVisible(false);
				mcontroller.getView().getMapScrollPane().getDisplayer()
						.repaint();
			}
		else {
			mapObject.getDrawerLayout().setBodyVisible(true);
			ContextMap.BoundingBox oldbb = mapObject.getDrawerLayout()
					.getBoundingBox();
			if (oldbb == null || oldbb.dim.width == 0 || oldbb.dim.height == 0) {
				ContextMap.BoundingBox bb = new ContextMap.BoundingBox(
						mapEvent.mapX, mapEvent.mapY, 50, 20);
				mapObject.getDrawerLayout().setBoundingBox(bb);
				mcontroller.getView().getMapScrollPane().getDisplayer()
						.repaint();
			}
		}
	}
}