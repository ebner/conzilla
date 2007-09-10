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
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/** It's recommended to inherit from this class.
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class AbstractTool implements Tool
{
  /** The name of this tool.
   */
  String name;

  /** The type of this tool.
   */
  int type;

  /** Whether this tool is activated.
   *  It's protected because some tools initially wants to be 
   *  activated without having to call the activate function.
   */
  protected boolean activated = false;

  /** Whether this tool is enabled.
   */
  boolean enabled = true;

  /** The tool state listeners of this tool.
   */
  Vector toolStateListeners;

  /** Constructs a Tool with the given name and type.
   *
   *  @param name the name of the tool.
   *  @param type the type of tool.
   */
  public AbstractTool(String name, int type)
  {
    this.name = name;
    this.type = type;
    toolStateListeners = new Vector();
    
  }

  public String getName()
  {
    return name;
  }

  public Icon getIcon()
  {
      return null;
  }

  public int getToolType()
  {
    return type;
  }

  public final void activate()
  {
    if(!activated)
      {
	if(type != Tool.ACTION)
	  {
	    activated = true;
	    fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.ACTIVATED));
	  }
	activateImpl();
      }
  }

  public final void deactivate()
  {
    if(activated)
      {
	activated = false;

	fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.DEACTIVATED));
	deactivateImpl();
      }
  }

  public final void enable()
  {
    if(!enabled)
      {
	enabled = true;
	fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.ENABLED));
      }
  }

  public final boolean isEnabled()
  {
    return enabled;
  }  

  public final void disable()
  {
    if(enabled)
      {
	enabled = false;
	fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.DISABLED));
      }
  }

  public final boolean isActivated()
  {
    return activated;
  }

  /** Called when the tool is activated.
   *
   */
  protected void activateImpl() {}
  
  /** Called when the tool is deactivated.
   *
   *  Never called for ACTION tools.
   */
  protected void deactivateImpl() {}
  
  public void update(Object o)
    {}
  
  public void detach()
  {
    deactivate();
    toolStateListeners.removeAllElements();
    toolStateListeners = null;
    detachImpl();
  }

  /** Called at the latest stage of detaching to detach sub-class
   *  specific data.
   */
  protected void detachImpl() {}

  public void addToolStateListener(ToolStateListener l)
  {
    toolStateListeners.addElement(l);
  }

  public void removeToolStateListener(ToolStateListener l)
  {
    toolStateListeners.removeElement(l);
  }

  /** Fires a tool state event to all listeners.
   *
   *  @param e the event to fire.
   */
  void fireToolStateEvent(ToolStateEvent e)
  {
    for(int i = 0; i < toolStateListeners.size(); i++)
      {
	ToolStateListener l = (ToolStateListener) toolStateListeners.elementAt(i);
	l.toolStateChanged(e);
      }
  }
}
