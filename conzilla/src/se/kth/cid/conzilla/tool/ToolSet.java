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
import javax.swing.event.*;
import java.util.*;


/** This class represents a set of EXCLUSIVE tools that are mutually exclusive.
 *  Other tools may be put here as well.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ToolSet implements ToolStateListener
{
  /** All the tools.
   */
  Vector tools;

  /** Constructs a ToolSet.
   */
  public ToolSet()
  {
    tools = new Vector();
  }

  /** Adds a tool to this tool set.
   *
   *  @param tool the tool to add.
   */
  public void addTool(final Tool tool)
  {
    if(tool.getToolType() == Tool.EXCLUSIVE)
      {
	tool.addToolStateListener(this);
      }
    
    tools.addElement(tool);
  }

  public void toolStateChanged(ToolStateEvent e)
  {
    Tool tool = e.getTool();

    if(e.getEvent() == ToolStateEvent.ACTIVATED)
      {
	for(int i = 0; i < tools.size(); i++)
	  {
	    Tool otherTool = (Tool) tools.elementAt(i);
	    if(otherTool != tool &&
	       otherTool.getToolType() == Tool.EXCLUSIVE &&
	       otherTool.isActivated())
	      {
		otherTool.deactivate();
	      }
	  }
      }
  }

  /** Removes a tool from this tool set.
   *
   *  @param tool the tool to remove.
   */
  public void removeTool(Tool tool)
  {
    tool.removeToolStateListener(this);

    tools.removeElement(tool);
  }

  /** Removes all tools from this tool set.
   */
  public void removeAllTools()
  {
    for(int i = 0; i < tools.size(); i++)
      {
	Tool tool = (Tool) tools.elementAt(i);
	tool.removeToolStateListener(this);
      }
    tools.removeAllElements();
  }

  /** Returns the currently active EXCLUSIVE tool.
   *
   *  @return the currently active EXCLUSIVE tool.
   */
  public Tool getActiveExclusiveTool()
  {
    for(int i = 0; i < tools.size(); i++)
      {
	Tool tool = (Tool) tools.elementAt(i);
	if(tool.getToolType() == Tool.EXCLUSIVE &&
	   tool.isActivated())
	  {
	    return tool;
	  }
      }
    return null;
  }


  /** Returns the tools.
   *
   * @return the tools.
   */
  public Tool[] getTools()
  {
    Tool[] toolsArr = new Tool[tools.size()];

    tools.copyInto(toolsArr);

    return toolsArr;
  }

  /** Returns the tool with the given name.
   *
   *  @return the tool with the given name.
   */
  public Tool getTool(String name)
  {
    for(int i = 0; i < tools.size(); i++)
      {
	Tool tool = (Tool) tools.elementAt(i);
	if(tool.getName().equals(name))
	  return tool;
      }
    return null;
  }
  
}
