/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import se.kth.cid.conzilla.properties.ConzillaResourceManager;

/**
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

	public Tool(String name) {
		init(name, getClass().getName());
	}

	public Tool(String name, String resbundle) {
		init(name, resbundle);
	}

	void init(String name, String resbundle) {
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
}