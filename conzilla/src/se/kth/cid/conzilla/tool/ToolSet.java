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
import java.beans.*;


/** This class represents a set of EXCLUSIVE tools that are mutually exclusive.
 *  Other tools may be put here as well.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ToolSet implements PropertyChangeListener
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
    public void addTool(final ExclusiveStateTool tool)
    {
	if(getActiveTool() != null)
	    tool.setActivated(false);

	tool.addPropertyChangeListener(this);
	
	tools.addElement(tool);

    }
    
  public void propertyChange(PropertyChangeEvent e)
  {
      ExclusiveStateTool tool = (ExclusiveStateTool) e.getSource();

    if(e.getPropertyName().equals(StateTool.ACTIVATED))
	{
	    if(!((Boolean) e.getNewValue()).booleanValue())
		return;
	    
	    for(int i = 0; i < tools.size(); i++)
		{
		    ExclusiveStateTool otherTool = (ExclusiveStateTool) tools.elementAt(i);
		    if(otherTool != tool &&
		       otherTool.isActivated())
			{
			    otherTool.setActivated(false);
			}
		}
	}
  }
    
    /** Removes a tool from this tool set.
     *
     *  @param tool the tool to remove.
     */
    public void removeTool(ExclusiveStateTool tool)
    {
	tool.removePropertyChangeListener(this);
	
	tools.removeElement(tool);
    }
    
    /** Removes all tools from this tool set.
     */
    public void removeAllTools()
    {
	for(int i = 0; i < tools.size(); i++)
	    {
		ExclusiveStateTool tool = (ExclusiveStateTool) tools.elementAt(i);
		tool.removePropertyChangeListener(this);
	    }
	tools.removeAllElements();
    }
    
    /** Returns the currently active EXCLUSIVE tool.
     *
     *  @return the currently active EXCLUSIVE tool.
     */
    public ExclusiveStateTool getActiveTool()
    {
	for(int i = 0; i < tools.size(); i++)
	    {
		ExclusiveStateTool tool = (ExclusiveStateTool) tools.elementAt(i);
		if(tool.isActivated())
		    return tool;
	    }
	return null;
    }
    
    
    /** Returns the tools.
     *
     * @return the tools.
     */
    public ExclusiveStateTool[] getTools()
    {
	ExclusiveStateTool[] toolsArr = new ExclusiveStateTool[tools.size()];
	
	tools.copyInto(toolsArr);
	
	return toolsArr;
    }
    
}
