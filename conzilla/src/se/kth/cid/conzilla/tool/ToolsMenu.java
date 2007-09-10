/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.conzilla.tool;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

/** This class is a menu that supports adding tools as buttons.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ToolsMenu extends PriorityMenu implements PropertyChangeListener
{
  /** Maps tool --> JMenuItem
   */
  Hashtable toolItems;

  /** Constructs a ToolSetMenu with the given name.
   *
   *  @param title thetitle of this menu.
   */
  public ToolsMenu(String title, String resbundle)
  {
      super(title, resbundle);
      toolItems = new Hashtable();
  }

  public ToolsMenu(String title)
  {
      super(title);
      toolItems = new Hashtable();
  }

    public JMenuItem add(Tool t)
    {
	return addTool(t, Integer.MAX_VALUE);
    }
    

  /** Adds a tool to the menu.
   *
   *  @param tool the tool to add.
   *  @return the menu item that represents the tool.
   */
  public JMenuItem addTool(final Tool tool, int prio)
  {
    JMenuItem menuItem = null;

    if(tool instanceof ExclusiveStateTool)
	{
	    final ExclusiveStateTool stateTool = (ExclusiveStateTool) tool;
	    final JRadioButtonMenuItem stateItem = new JRadioButtonMenuItem(tool);
	    menuItem = stateItem;
	    menuItem.setSelected(stateTool.isActivated());
	    menuItem.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    stateTool.setActivated(true);
			else if(e.getStateChange() == ItemEvent.DESELECTED && stateTool.isActivated())
			    stateItem.setSelected(true);
		    }
		});
	    add(menuItem);
	}
    else if(tool instanceof StateTool)
	{
	    final StateTool stateTool = (StateTool) tool;
	    final JCheckBoxMenuItem stateItem =
		new JCheckBoxMenuItem(tool);
	    menuItem = stateItem;
	    menuItem.setSelected(stateTool.isActivated());

	    menuItem.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    stateTool.setActivated(true);
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			    stateTool.setActivated(false);
		    }
		});
	    add(menuItem);
	}
    else
	{
	    menuItem = super.add(tool);
	}
    
    menuItem.setEnabled(tool.isEnabled()); 
    KeyStroke k = tool.getAccelerator();
    if(k != null)
	menuItem.setAccelerator(k);
    
    setPriority(menuItem, prio);

    tool.addPropertyChangeListener(this);
    toolItems.put(tool, menuItem);

    return menuItem;
  }

  /** Returns the menu item for a given tool.
   *
   *  @param tool the tool to search for.
   *  @return the menu item for the given tool.
   */
  JMenuItem getToolItem(Tool tool)
    {
      return (JMenuItem) toolItems.get(tool);
    }

  /** Fetch all tools in this menu.
   */
  public Enumeration getTools()
  {
    return toolItems.keys();
  }

  /** Removes a tool from this menu.
   *
   *  @param t the tool to remove.
   */
  public void removeTool(Tool t)
    {
      JMenuItem item = getToolItem(t);
      
      if(item == null)
	return;
      
      remove(item);
      t.removePropertyChangeListener(this);
      toolItems.remove(t);
    }

  /** Removes all tools from this menu.
   */
  public void removeAllTools()
  {
    Enumeration tools = toolItems.keys();
    for (;tools.hasMoreElements();)
	removeTool((Tool) tools.nextElement());
  }

  public void propertyChange(PropertyChangeEvent e)
  {
    JMenuItem menuItem = getToolItem((Tool)e.getSource());

    if(menuItem == null)
      return;
    
    if(e.getPropertyName().equals(StateTool.ACTIVATED))
	menuItem.setSelected(((Boolean) e.getNewValue()).booleanValue());
    else if(e.getPropertyName().equals(Tool.ENABLED))
	menuItem.setEnabled(((Boolean) e.getNewValue()).booleanValue());
  }

    public void detach()
    {
	removeAllTools();
	Component[] c = getComponents();
	for(int i = 0; i < c.length; i++)
	    {
		if(c[i] instanceof ToolsMenu)
		    ((ToolsMenu) c[i]).detach();
	    }
	removeAll();
    }
}
