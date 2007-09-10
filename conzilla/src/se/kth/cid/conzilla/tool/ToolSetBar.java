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

/** This class is a tool bar that supports adding tools as buttons.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ToolSetBar extends CToolBar implements ToolStateListener
{
  /** Maps tool --> button.
   */
  Hashtable toolButtons;

  /** Constructs a ToolSetBar with the given title
   *
   *  @param title the title of the tool bar.
   */
  public ToolSetBar(String title)
    {
      super(title);
      toolButtons = new Hashtable();
    }
  
  /** Adds a tool to the tool bar.
   *
   *  @param tool the tool to add.
   *  @return the button that represents the tool.
   */
  public AbstractButton addTool(final Tool tool)
  {
    AbstractButton button = null;

    switch(tool.getToolType())
      {
      case Tool.EXCLUSIVE:
	final JToggleButton toggleButton = new JToggleButton(tool.getName());
	button = toggleButton;
	button.addItemListener(new ItemListener()
	  {
	    public void itemStateChanged(ItemEvent e)
	    {
	      if(e.getStateChange() == ItemEvent.SELECTED)
		{
		  if(!tool.isActivated())
		    tool.activate();
		}
	      else
		if(tool.isActivated())
		  toggleButton.setSelected(true);
	    }
	  });
	break;
      case Tool.STATE:
	final JToggleButton stateButton = new JToggleButton(tool.getName());
	button = stateButton;
	button.addItemListener(new ItemListener()
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
	button = new JButton(tool.getName());
	button.addActionListener(new ActionListener()
	  {
	    public void actionPerformed(ActionEvent e)
	    {
	      tool.activate();
	    }
	  });
	break;
      }
    button.setEnabled(tool.isEnabled()); 
    
    button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
							BorderFactory.createEmptyBorder(2,2,2,2)));
    
    add(button);
    
    tool.addToolStateListener(this);

    toolButtons.put(tool, button);
    
    return button;
  }

  /** Returns the button for a given tool.
   *
   *  @param tool the tool to search for.
   *  @return the button for the given tool.
   */
  public AbstractButton getToolButton(Tool tool)
  {
    return (AbstractButton) toolButtons.get(tool);
  }

  /** Removes a tool from this tool bar.
   *
   *  @param t the tool to remove.
   */
  public void removeTool(Tool t)
  {
    AbstractButton button = getToolButton(t);

    if(button == null)
      return;
    
    remove(button);
    t.removeToolStateListener(this);
    toolButtons.remove(t);
  }


  /** Removes all tools from this tool bar.
   */
  public void removeAllTools()
  {
    Enumeration tools = toolButtons.keys();
    
    for(; tools.hasMoreElements();)
      {
	Tool tool = (Tool) tools.nextElement();
	removeTool(tool);
      }
  }

  public void toolStateChanged(ToolStateEvent e)
  {
    AbstractButton button = getToolButton(e.getTool());

    if(button == null)
      return;
    
    switch(e.getEvent())
      {
      case ToolStateEvent.ACTIVATED:
	button.setSelected(true);
	break;
      case ToolStateEvent.DEACTIVATED:
	button.setSelected(false);
	break;	
      case ToolStateEvent.ENABLED:
	button.setEnabled(true);
	break;	
      case ToolStateEvent.DISABLED:
	button.setEnabled(false);
	break;	
      }
  }
}
