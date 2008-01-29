/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JRadioButtonMenuItem;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.tool.MapToolsMenu;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.tree.TreeTagNode;
import se.kth.cid.util.AttributeEntryUtil;

public class MoveToLayerMapTool extends MapToolsMenu {
	DrawerLayout drawerLayout;

	class LayerMenuTool extends Tool {
		public LayerMenuTool(String name, final LayerLayout ls,
				final MapController cont, final LayerManager lMan) {
			super(name, null, cont);

			final JRadioButtonMenuItem mi = new JRadioButtonMenuItem();
			setJMenuItem(mi);
			mi.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED
							&& drawerLayout != null) {
						TreeTagNode ttn = (TreeTagNode) drawerLayout
								.getParent();
						ttn.remove(drawerLayout);
						ls.add(drawerLayout);
						cont.getView().getMapScrollPane().getDisplayer()
								.repaint();
					}
				}
			});
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	/**
	 * Constructs an DataVisibilityMapTool.
	 */
	public MoveToLayerMapTool(MapController cont) {
		super("MOVE_TO_LAYER", EditMapManagerFactory.class.getName(), cont);
	}

	public void update(MapEvent mapEvent) {
		if (!getPopupMenu().isVisible() && mapEvent.mapObject != null
				&& mapEvent.mapObject.getDrawerLayout() != null) {
			String containerURI = mapEvent.mapObject.getDrawerLayout()
					.getLoadContainer();
			if (!mapEvent.mapObject.getDrawerLayout().getConceptMap()
					.getComponentManager().getEditingSesssion()
					.getContainerURIForLayouts().equals(containerURI)) {
				setEnabled(false);
			} else {

				removeAll();
				LayerManager lMan = controller.getConceptMap()
						.getLayerManager();

				drawerLayout = null;
				DrawerLayout ns = mapEvent.mapObject.getDrawerLayout();
				TreeTagNode parent = (TreeTagNode) ns.getParent();
				Vector layers = lMan.getLayers();
				for (int counter = layers.size() - 1; counter >= 0; counter--) {
					LayerLayout ls = (LayerLayout) layers.elementAt(counter);
					String title = AttributeEntryUtil.getTitleAsString(ls);
					if (title == null) {
						title = ls.getURI().substring(
								ls.getURI().lastIndexOf('/') + 1);
					}

					LayerMenuTool lmt = new LayerMenuTool(title, ls,
							controller, lMan);
					if (ls == parent)
						((AbstractButton) lmt.getJMenuItem()).setSelected(true);
					addTool(lmt, 10 * (layers.size() - 1 - counter));
				}

				setEnabled(true);
				drawerLayout = ns;
			}
		} else {
			setEnabled(false);
		}

		super.update(mapEvent);
	}
}
