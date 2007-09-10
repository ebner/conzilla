/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

/**
 * This class represents a set of EXCLUSIVE tools that are mutually exclusive.
 * Other tools may be put here as well.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ToolSet implements PropertyChangeListener {
	
	/**
	 * All the tools.
	 */
	Vector tools;

	/**
	 * Constructs a ToolSet.
	 */
	public ToolSet() {
		tools = new Vector();
	}

	/**
	 * Adds a tool to this tool set.
	 * 
	 * @param tool
	 *            the tool to add.
	 */
	public void addTool(final ExclusiveStateTool tool) {
		if (getActiveTool() != null) {
			tool.setActivated(false);
		}

		tool.addPropertyChangeListener(this);
		tools.addElement(tool);
	}

	public void propertyChange(PropertyChangeEvent e) {
		ExclusiveStateTool tool = (ExclusiveStateTool) e.getSource();

		if (e.getPropertyName().equals(StateTool.ACTIVATED)) {
			if (!((Boolean) e.getNewValue()).booleanValue()) {
				return;
			}
			for (int i = 0; i < tools.size(); i++) {
				ExclusiveStateTool otherTool = (ExclusiveStateTool) tools.elementAt(i);
				if (otherTool != tool && otherTool.isActivated()) {
					otherTool.setActivated(false);
				}
			}
		}
	}

	/**
	 * Removes a tool from this tool set.
	 * 
	 * @param tool
	 *            the tool to remove.
	 */
	public void removeTool(ExclusiveStateTool tool) {
		tool.removePropertyChangeListener(this);
		tools.removeElement(tool);
	}

	/**
	 * Removes all tools from this tool set.
	 */
	public void removeAllTools() {
		for (int i = 0; i < tools.size(); i++) {
			ExclusiveStateTool tool = (ExclusiveStateTool) tools.elementAt(i);
			tool.removePropertyChangeListener(this);
		}
		tools.removeAllElements();
	}

	/**
	 * Returns the currently active EXCLUSIVE tool.
	 * 
	 * @return the currently active EXCLUSIVE tool.
	 */
	public ExclusiveStateTool getActiveTool() {
		for (int i = 0; i < tools.size(); i++) {
			ExclusiveStateTool tool = (ExclusiveStateTool) tools.elementAt(i);
			if (tool.isActivated()) {
				return tool;
			}
		}
		return null;
	}

	/**
	 * Returns the tools.
	 * 
	 * @return the tools.
	 */
	public ExclusiveStateTool[] getTools() {
		ExclusiveStateTool[] toolsArr = new ExclusiveStateTool[tools.size()];
		tools.copyInto(toolsArr);
		return toolsArr;
	}

}
