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
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import se.kth.cid.conzilla.util.PriorityMenu;

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
  
  /** Adds a ToolsMenu to the menu.
    *
    *  @param menu the ToolsMenu to add.
    *  @return the menu item that represents the menu.
    */
   public JMenuItem addToolsMenu(final ToolsMenu menu, int prio)
   {
     JMenuItem menuItem = null;

     menuItem = super.add(menu);
        
     setPriority(menuItem, prio);

    // toolItems.put(menu, menuItem);

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
