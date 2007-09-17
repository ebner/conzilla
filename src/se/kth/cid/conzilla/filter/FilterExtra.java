/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.ViewTool;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.tool.ToolsMenu;

/**
 * @author Matthias Palmer.
 */
public class FilterExtra implements Extra {
	FilterFactory filterFactory;

	public FilterExtra() {
		filterFactory = new SimpleFilterFactory();
	}

	public boolean initExtra(ConzillaKit kit) {
		return true;
	}

	public String getName() {
		return "filter";
	}

	public void refreshExtra() {
		filterFactory.refresh();
	}

	public boolean saveExtra() {
		return true;
	}

	public void exitExtra() {
	}

	public void extendMenu(ToolsMenu menu, MapController c) {
	}

	public void addExtraFeatures(MapController c) {
		/*
		 * if (location.equals("viewalterationtool")) { Object[] arr =
		 * (Object[]) o; ViewTool va = (ViewTool) arr[0]; JMenu menu = (JMenu)
		 * arr[1]; Concept concept = (Concept) arr[2]; ContextMap cMap =
		 * (ContextMap) arr[3];
		 * 
		 * Filter filter=filterFactory.createFilter(c, concept, cMap); if
		 * (filter == null) return;
		 * filter.getFilterNode().filterThrough(concept); FilterNode node =
		 * filter.getFilterNode(); recursivelyBuildMenu(va, menu, node, concept,
		 * c); }
		 */
	}

	protected void recursivelyBuildMenu(final ViewTool va, JMenu menu, FilterNode node, Concept concept,
			MapController controller) {
		List contents = node.getContent(concept);
		if (contents.isEmpty())
			return;

		FilterNode subnode;

		for (int i = 0; i < node.numOfRefines(); i++) {
			subnode = node.getRefine(i);

			if (subnode.numOfRefines() == 0) {
				FilterAction filteraction = new FilterAction(subnode) {
					public void actionPerformed(ActionEvent ae) {
						// va.show(this.node.getContent(component),null
						// ,this.node);
					}
				};
				filteraction.setComponent(concept);
				JMenuItem menuItem = menu.add(filteraction);
				String toolTipText = subnode.getToolTipText();
				if (toolTipText != null)
					menuItem.setToolTipText(toolTipText);
			} else {
				JMenu submenu = new JMenu(subnode.getFilterTag());
				recursivelyBuildMenu(va, submenu, subnode, concept, controller);
				JMenuItem menuItem = menu.add(submenu);
				String toolTipText = subnode.getToolTipText();
				if (toolTipText != null)
					menuItem.setToolTipText(toolTipText);
			}
		}

		FilterAction menuAny = new FilterAction(node, "Any") {
			public boolean isEnabled() {
				Set set = this.node.getContentPassedRefines(component);
				return set.size() > 0;
			}

			public void actionPerformed(ActionEvent ae) {
				// va.show(this.node.getContentPassedRefines(component),
				// "Any", this.node);
			}
		};
		menuAny.setComponent(concept);

		FilterAction menuOther = new FilterAction(node, "Other") {
			public boolean isEnabled() {
				Set set = this.node.getContentPassedRefines(component);
				List list = this.node.getContent(component);
				List other = new Vector(list);
				other.removeAll(set);

				return other.size() > 0;
			}

			public void actionPerformed(ActionEvent ae) {
				// Tracer.debug("contentpassedRefines
				// ="+this.node.getContentPassedRefines(component).size());
				Set set = this.node.getContentPassedRefines(component);
				List list = this.node.getContent(component);
				List other = new Vector(list);
				other.removeAll(set);

				// va.show(other,"Other", this.node);
			}
		};
		menuOther.setComponent(concept);

		menu.addSeparator();
		ConzillaResourceManager.getDefaultManager().customizeButton(menu.add(menuAny), FilterExtra.class.getName(),
				"VIEW_ANY");
		ConzillaResourceManager.getDefaultManager().customizeButton(menu.add(menuOther), FilterExtra.class.getName(),
				"VIEW_OTHER");
		menu.addSeparator();

		// ViewAlterationTool vat=new ViewAlterationTool("VIEW_FILTER",
		// FilterExtra.class.getName(), controller);
		// FIXME just commented out.
		/*
		 * JMenuItem jmi=vat.getMenuItemForConcept(node.getFilterNode(), null);
		 * if (jmi!=null)
		 * ConzillaResourceManager.getDefaultManager().customizeButton(menu.add(jmi),
		 * FilterExtra.class.getName(), "VIEW_VIEW_FILTER");
		 */
	}
}
