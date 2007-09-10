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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/** This class is a menu that supports adding tools as buttons.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ToolSetMenu extends JMenu implements ToolStateListener
{
  /** Maps tool --> JMenuItem
   */
  Hashtable toolItems;

  /** Constructs a ToolSetMenu with the given title.
   *
   *  @param title thetitle of this menu.
   */
  public ToolSetMenu(String title)
  {
    super(title);
    toolItems = new Hashtable();
  }

  /** Adds a tool to the menu.
   *
   *  @param tool the tool to add.
   *  @return the menu item that represents the tool.
   */
  public JMenuItem addTool(final Tool tool)
  {
    JMenuItem menuItem = null;

    /*    String name=PropertiesManager.getDefaultPropertiesManager().getMenuManager().getString(tool.getName());
    if (name==null)
	name=tool.getName();
    */
    if (tool instanceof AlterationTool)
      menuItem = ((AlterationTool) tool).getMenuItem();
    else
      switch(tool.getToolType())
      {
      case Tool.EXCLUSIVE:
	final JRadioButtonMenuItem radioItem =
	  new JRadioButtonMenuItem(tool.getName());
	menuItem = radioItem;
	menuItem.addItemListener(new ItemListener()
	  {
	    public void itemStateChanged(ItemEvent e)
	    {
	      if(e.getStateChange() == ItemEvent.SELECTED)
		{
		  if(!tool.isActivated())
		    tool.activate();
		}
	      else
		{
		  if(tool.isActivated())
		    radioItem.setSelected(true);
		}
	    }
	  });
	break;
      case Tool.STATE:
	final JCheckBoxMenuItem stateItem =
	  new JCheckBoxMenuItem(tool.getName());
	menuItem = stateItem;
	menuItem.setSelected(tool.isActivated());
	menuItem.addItemListener(new ItemListener()
	  {
	    public void itemStateChanged(ItemEvent e)
	    {
	      if(e.getStateChange() == ItemEvent.SELECTED)
		{
		  tool.activate();
		}
	      else
		{
		  tool.deactivate();
		}
	    }
	  });
	break;
      case Tool.ACTION:
	menuItem = new JMenuItem(tool.getName());
	menuItem.addActionListener(new ActionListener()
	  {
	    public void actionPerformed(ActionEvent e)
	    {
	      tool.activate();
	    }
	  });
	break;
      }

    menuItem.setEnabled(tool.isEnabled()); 
    customizeMenuItem(menuItem, tool);

    //    button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
    //							BorderFactory.createEmptyBorder(2,2,2,2)));
    
    add(menuItem);

    tool.addToolStateListener(this);
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
      t.removeToolStateListener(this);
      toolItems.remove(t);
    }

  /** Removes all tools from this menu.
   */
  public void removeAll()
  {
    Enumeration tools = toolItems.keys();
    for (;tools.hasMoreElements();)
	removeTool((Tool) tools.nextElement());
  }

  public void toolStateChanged(ToolStateEvent e)
  {
    JMenuItem menuItem = getToolItem(e.getTool());

    if(menuItem == null)
      return;
    
    switch(e.getEvent())
      {
      case ToolStateEvent.ACTIVATED:
	menuItem.setSelected(true);
	break;
      case ToolStateEvent.DEACTIVATED:
	menuItem.setSelected(false);
	break;	
      case ToolStateEvent.ENABLED:
	menuItem.setEnabled(true);
	break;	
      case ToolStateEvent.DISABLED:
	menuItem.setEnabled(false);
	break;	
      }
  }

    
    /** Updates all tools in this menu.
     *  The apperance of a tool that implements AlterationTool is refreshed.
     *
     *  @see AlterationTool#getMenuItem
     *  @see Tool#update
     */
  public void update(Object o)
  {
    Enumeration en=toolItems.keys();
    for (;en.hasMoreElements();)
	{
	    Tool tool=(Tool) en.nextElement();
	    tool.update(o);
	    if (tool instanceof AlterationTool)
		{
		    JMenuItem newME=((AlterationTool) tool).getMenuItem();
		    customizeMenuItem(newME, tool);

		    JMenuItem oldME=(JMenuItem) toolItems.get(tool);
		    toolItems.remove(tool);
		    toolItems.put(tool, newME);
		    if (newME!=oldME)
			{
			    int pos=getPopupMenu().getComponentIndex(oldME);
			    if (pos>=0)
				getPopupMenu().remove(oldME);
			    getPopupMenu().insert(newME,pos);
			}
		}
	}
  }
    protected void customizeMenuItem(JMenuItem mi, Tool tool)
    {
	MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();
	menuManager.customizeButton(mi, tool.getName());
	//	mi.setBorder(null);

    }
}
