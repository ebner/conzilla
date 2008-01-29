/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;




/**
 * There are two reasons for this class:
 * <nl><li>{@link MapMenuItem}s are supposed to be inside menues poped over a map, and hence 
 * change it behaviour depending which concept/concept-relation/map it is over. 
 * This is controlled via the {@link #update(MapEvent)} method getting called just before being shown.</li>
 * <li>In some situations the {@link MapMenuItem} is a single choice, sometimes it is a submenu, 
 * this flexibility requires the menu to be constructed just in time, just before it is popped. 
 * The method {@link #getJMenuItem()} is called to retrieve the menu item just before popup of the
 * containing menu.</li>
 * </nl>
 */

/**
 * The main reason for introducing Tools are that the same tool may be added
 * to several menues ({@link ToolsMenu}) and ToolBars {@link ToolsBar} without 
 * swing complaining.
 * 
 * Tools are objects that are responsible for reacting to the user's gestures.
 * They are usually placed in menus and in toolbars.
 * 
 * Tools can be in two states: activated or deactivated. Independently of this,
 * they can be enabled and disabled.
 * 
 * There are three types of tools. The ACTION tools does something immediately
 * in response to being activated, and thus has no concept of deactivation.
 * 
 * The EXCLUSIVE tools can be activated only one at a time and usually responds
 * to user actions continuously while activated.
 * 
 * The STATE tools may be activated and deactivated independently of other
 * tools.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public abstract class Tool extends AbstractAction {

	public static final String ENABLED = "enabled";

	String name;

	private Icon selectedIcon;

	protected MapEvent mapEvent;

	protected MapObject mapObject;

	protected MapController controller;

	private JMenuItem menuItem = null;

	private boolean nextPopupIsOverMap;

	public Tool(String name) {
		setTitleAndTooltip(name, getClass().getName());
	}

	public Tool(String name, String resbundle) {
		setTitleAndTooltip(name, resbundle);
	}

	public Tool(String name, String resbundle, MapController controller) {
		setTitleAndTooltip(name, resbundle);
		this.controller = controller;
	}

	protected void setTitleAndTooltip(String name, String resbundle) {
		this.name = name;
		if (resbundle != null) {
			String n = ConzillaResourceManager.getDefaultManager().getString(resbundle, name);
			if (n == null) {
				n = name;
			}
			putValue(NAME, n);
			putValue(SHORT_DESCRIPTION, ConzillaResourceManager.getDefaultManager().getString(resbundle,
					name + "_TOOL_TIP"));
		} else
			putValue(NAME, name);
	}

	/**
	 * Returns the name of the tool.
	 * 
	 * @return the name of the tool.
	 */
	public String getText() {
		return (String) getValue(NAME);
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the tool tip of the tool.
	 * 
	 * @return the tool tip of the tool.
	 */
	public String getToolTip() {
		return (String) getValue(SHORT_DESCRIPTION);
	}

	/**
	 * Returns the icon of the tool, if any.
	 * 
	 * @return the icon of the tool.
	 */
	public Icon getIcon() {
		return (Icon) getValue(SMALL_ICON);
	}

	public void setIcon(Icon i) {
		putValue(SMALL_ICON, i);
	}

	public void setSelectedIcon(Icon i) {
		selectedIcon = i;
	}

	public Icon getSelectedIcon() {
		return selectedIcon;
	}
	
	public void setToolTip(String toolTip) {
		putValue(SHORT_DESCRIPTION, toolTip);
	}

	public void setAccelerator(KeyStroke k) {
		putValue(ACCELERATOR_KEY, k);
	}

	public KeyStroke getAccelerator() {
		return (KeyStroke) getValue(ACCELERATOR_KEY);
	}
	
	/**
	 * Should be called when this tool is no longer being used.
	 * 
	 * Makes sure this tool detaches all listeners etc. so that it can be gc'ed.
	 * Calling any method in the tool after this will cause errors.
	 * 
	 * A tool will be deactivated and have its listeners removed.
	 */
	public void detach() {
	}
		
	public void setJMenuItem(JMenuItem mi) {
		JMenuItem oldmi = getJMenuItem();
		if (oldmi != null)
			oldmi.removeActionListener(this);		
		menuItem  = mi;
		
		ConzillaResourceManager.getDefaultManager().plainCustomizeButton(mi, (String) getValue(NAME), (String) getValue(SHORT_DESCRIPTION));
		mi.addActionListener(this);
	}

	public JMenuItem getJMenuItem() {
		return menuItem;
	}
	
	public void updateBeforePopup() {
		if (nextPopupIsOverMap) {
			nextPopupIsOverMap = false;
		} else {
			mapEvent = null;
			mapObject = null;
		}
		boolean enabled = updateEnabled();
		setEnabled(enabled);
		JMenuItem mi = getJMenuItem();
		if (mi != null) {
			mi.setEnabled(enabled);
		}
	}
		
	public void update(MapEvent e) {
		nextPopupIsOverMap = true;
		mapEvent = e;
		mapObject = mapEvent.mapObject;
	}
	
	protected boolean updateEnabled() {
		return true;
	}
}