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
public class ToolsBar extends JToolBar implements PropertyChangeListener
{
  /** Maps tool --> AbstractButton
   */
  Hashtable toolItems;

  /** Constructs a ToolsBar with the given name.
   *
   *  @param title thetitle of this menu.
   */
  public ToolsBar(String title, String resbundle)
  {
    super(ConzillaResourceManager.getDefaultManager().getString(resbundle, title));

    toolItems = new Hashtable();
  }

  /** Adds a tool to the menu.
   *
   *  @param tool the tool to add.
   *  @return the menu item that represents the tool.
   */
  public AbstractButton addTool(final Tool tool)
  {
    AbstractButton button = null;

    if(tool instanceof ExclusiveStateTool)
	{
	    final ExclusiveStateTool stateTool = (ExclusiveStateTool) tool;
	    final JRadioButton stateButton = new JRadioButton(tool);
	    stateButton.setSelected(stateTool.isActivated());

	    button = stateButton;

	    button.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    stateTool.setActivated(true);
			else if(e.getStateChange() == ItemEvent.DESELECTED && stateTool.isActivated())
			    stateButton.setSelected(true);
		    }
		});
	    add(button);
	}
    else if(tool instanceof StateTool)
	{
	    final StateTool stateTool = (StateTool) tool;
	    final JToggleButton stateButton = new JToggleButton(tool);
	    stateButton.setSelected(stateTool.isActivated());

	    button = stateButton;

	    button.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    stateTool.setActivated(true);
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			    stateTool.setActivated(false);
		    }
		});
	    add(button);
	}
    else
	{
	    button = add(tool);
	}
    
    button.setEnabled(tool.isEnabled()); 
    

    customizeButton(button, tool);

    tool.addPropertyChangeListener(this);
    toolItems.put(tool, button);

    return button;
  }

  /** Returns the menu item for a given tool.
   *
   *  @param tool the tool to search for.
   *  @return the menu item for the given tool.
   */
    public AbstractButton getToolButton(Tool tool)
    {
      return (AbstractButton) toolItems.get(tool);
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
      AbstractButton item = getToolButton(t);
      
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
    AbstractButton button = getToolButton((Tool)e.getSource());

    if(button == null)
      return;
    
    if(e.getPropertyName().equals(StateTool.ACTIVATED))
	button.setSelected(((Boolean) e.getNewValue()).booleanValue());
    else if(e.getPropertyName().equals(Tool.ENABLED))
	button.setEnabled(((Boolean) e.getNewValue()).booleanValue());
  }

    
    protected void customizeButton(AbstractButton button, Tool tool)
    {
	if(tool.getIcon() != null)
	    button.setText(null);
	
	button.setMargin(new Insets(0, 0, 0, 0));
	Dimension pref = button.getPreferredSize();
	pref.height = 22;
	button.setMaximumSize(pref);
	button.setPreferredSize(pref);
	button.setMinimumSize(pref);
    }
}
