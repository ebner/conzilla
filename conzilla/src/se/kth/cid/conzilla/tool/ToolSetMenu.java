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
	final JRadioButtonMenuItem stateItem =
	  new JRadioButtonMenuItem(tool.getName());
	menuItem = stateItem;
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
}
