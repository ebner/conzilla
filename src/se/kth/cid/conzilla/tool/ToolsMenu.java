/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import se.kth.cid.conzilla.util.PriorityMenu;

/**
 * This class is a menu that supports adding tools as buttons.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ToolsMenu extends PriorityMenu implements PropertyChangeListener{
	/**
	 * Maps tool --> JMenuItem
	 */
	HashMap tools2Prio = new HashMap();
	
	/** 
	 * Maps tool --> JMenuItem
	 */
	Hashtable toolItems = new Hashtable();
	
	HashSet jITTools = new HashSet();

	/**
	 * Constructs a ToolSetMenu with the given name.
	 * 
	 * @param title thetitle of this menu.
	 */
	public ToolsMenu(String title, String resbundle) {
		super(title, resbundle);
	}

	public ToolsMenu(String title) {
		super(title);
	}

	public JMenuItem add(Tool t) {
		return addTool(t, Integer.MAX_VALUE);
	}

	/**
	 * Adds a tool to the menu.
	 * 
	 * @param tool the tool to add.
	 * @return the menu item that represents the tool.
	 */
	public JMenuItem addTool(final Tool tool, int prio) {
		tools2Prio.put(tool, new Integer(prio));
		addToolImpl(tool, prio);
		return null;
	}

	/**
	 * Removes a tool from this menu.
	 * 
	 * @param t the tool to remove.
	 */
	public void removeTool(Tool t) {
		tools2Prio.remove(t);
		jITTools.remove(t);
		toolItems.remove(t);
	}

	/**
	 * Adds a ToolsMenu to the menu.
	 * 
	 * @param menu the ToolsMenu to add.
	 * @return the menu item that represents the menu.
	 */
	public JMenuItem addToolsMenu(final ToolsMenu menu, int prio) {
		tools2Prio.put(menu, new Integer(prio));
		super.add(menu);
		setPriority(menu, prio);
		return null;
	}
	
	public void addToolImpl(final Tool tool, int prio) {
		if (tool.getJMenuItem() == null) {
			JMenuItem menuItem = null;
			if (tool instanceof ExclusiveStateTool) {
				final ExclusiveStateTool stateTool = (ExclusiveStateTool) tool;
				final JRadioButtonMenuItem stateItem = new JRadioButtonMenuItem(tool);
				menuItem = stateItem;
				menuItem.setSelected(stateTool.isActivated());
				menuItem.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED)
							stateTool.setActivated(true);
						else if (e.getStateChange() == ItemEvent.DESELECTED
								&& stateTool.isActivated())
							stateItem.setSelected(true);
					}
				});
				add(menuItem);
			} else if (tool instanceof StateTool) {
				final StateTool stateTool = (StateTool) tool;
				final JCheckBoxMenuItem stateItem = new JCheckBoxMenuItem(tool);
				menuItem = stateItem;
				menuItem.setSelected(stateTool.isActivated());

				menuItem.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED)
							stateTool.setActivated(true);
						else if (e.getStateChange() == ItemEvent.DESELECTED)
							stateTool.setActivated(false);
					}
				});
				add(menuItem);
			} else {
				menuItem = super.add(tool);
			}

			menuItem.setEnabled(tool.isEnabled());
			KeyStroke k = tool.getAccelerator();
			if (k != null) {
				menuItem.setAccelerator(k);
			}
			setPriority(menuItem, prio);
			tool.addPropertyChangeListener(this);
			toolItems.put(tool, menuItem);
		} else {
			jITTools.add(tool);
		}
	}
		
	public void updateBeforePopup() {
		//Update all Tools so that they are up to date.
		for (Iterator iter = tools2Prio.keySet().iterator(); iter.hasNext();) {
			Object entry = iter.next();
			if (entry instanceof Tool) {
				((Tool) entry).updateBeforePopup();
			}
		}
		
		//Add all Just In Time tools.
		for (Iterator iter = jITTools.iterator(); iter.hasNext();) {
			Tool tool = (Tool) iter.next();
			JMenuItem menuItem = tool.getJMenuItem();
			super.add(menuItem);
			setPriority(menuItem, ((Integer) tools2Prio.get(tool)).intValue()); 
			toolItems.put(tool, menuItem);
		}
		
		super.updateBeforePopup();
	}

	public void updateAfterPopup() {
		super.updateAfterPopup();
		
		//Set all tools enabled so that potential key-shortcuts works.
		for (Iterator iter = tools2Prio.keySet().iterator(); iter.hasNext();) {
			Object entry = iter.next();
			if (entry instanceof Tool) {
				((Tool) entry).setEnabled(true);
			}
		}
		
		//Remove all Just In Time Tools from the menu so that they can 
		//exist in other menues if needed.
		for (Iterator iter = jITTools.iterator(); iter.hasNext();) {
			Tool tool = (Tool) iter.next();
			super.remove(getToolItem(tool));
			toolItems.remove(tool);
		}
	}

	/**
	 * Fetch all tools in this menu.
	 */
	public Set getTools() {
		return tools2Prio.keySet();
	}

	/**
	 * Removes all tools from this menu.
	 */
	public void removeAllTools() {
		tools2Prio = new HashMap();

		Component[] comps = getPopupMenu().getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JSeparator) {
				continue;
			}
			remove(comps[i]);
		}
	}
	
	public void propertyChange(PropertyChangeEvent e) {
	    JMenuItem menuItem = getToolItem((Tool)e.getSource());

	    if(menuItem == null)
	      return;
	    
	    if(e.getPropertyName().equals(StateTool.ACTIVATED)) {
	    	menuItem.setSelected(((Boolean) e.getNewValue()).booleanValue());
	    } else if(e.getPropertyName().equals(Tool.ENABLED)) {
	    	menuItem.setEnabled(((Boolean) e.getNewValue()).booleanValue());
	    }
	}
	  
	/** Returns the menu item for a given tool.
	 *
	 *  @param tool the tool to search for.
	 *  @return the menu item for the given tool.
	 */
	JMenuItem getToolItem(Tool tool) {
		return (JMenuItem) toolItems.get(tool);
	}

	public void detach() {
		for (Iterator iter = tools2Prio.keySet().iterator(); iter.hasNext();) {
			Object entry = iter.next();
			if (entry instanceof Tool) {
				((Tool) entry).detach();
			} else {
				((ToolsMenu) entry).detach();
			}
		}
		removeAllTools();
	}
}