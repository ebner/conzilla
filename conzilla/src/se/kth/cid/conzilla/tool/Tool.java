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

/** Tools are objects that are responsible for reacting to the user's
 *  gestures. They are usually placed in menus and in toolbars.
 *
 *  Tools can be in two states: activated or deactivated.
 *  Independently of this, they can be enabled and disabled.
 *
 *  There are three types of tools. The ACTION tools does something
 *  immediately in response to being activated, and thus has no concept
 *  of deactivation.
 *
 *  The EXCLUSIVE tools can be activated only one at a time and usually
 *  responds to user actions continuously while activated.
 *
 *  The STATE tools may be activated and deactivated independently of other
 *  tools.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public abstract class Tool
{
  /** The EXCLUSIVE tool type.
   */
  public final static int EXCLUSIVE = 0;

  /** The STATE tool type.
   */
  public final static int STATE = 1;

  /** The ACTION tool type.
   */
  public final static int ACTION = 2;


  /** The name of this tool.
   */
  String name;

  /** The type of this tool.
   */
  int type;

  /** Whether this tool is activated.
   */
  boolean activated = false;

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
  public Tool(String name, int type)
  {
    this.name = name;
    this.type = type;
    toolStateListeners = new Vector();
    
  }

  /** Returns the name of the tool.
   *
   *  @return the name of the tool.
   */
  public String getName()
  {
    return name;
  }


  /** Returns the type of the tool.
   *
   *  @return the type of the tool.
   */
  public int getToolType()
  {
    return type;
  }


  /** Activates this tool.
   *
   *  Will not activate if the tool is already activated.
   *  For types other than ACTION, fires a ToolStateEvent.
   *  Will not change state for tools of type ACTION.
   *
   *  Upon activation, activateImpl is called.
   */
  public final void activate()
  {
    if(!activated)
      {
	if(type != ACTION)
	  {
	    activated = true;
	    fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.ACTIVATED));
	  }
	activateImpl();
      }
  }

  /** Deactivates this tool.
   *
   *  Will not deactivate if the tool is already deactivated.
   *  Fires a ToolStateEvent if the state changes.
   *
   *  Upon deactivation, deactivateImpl is called.
   */
  public final void deactivate()
  {
    if(activated)
      {
	activated = false;

	fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.DEACTIVATED));
	deactivateImpl();
      }
  }

  /** Enables this tool.
   *
   *  Fires a ToolStateEvent.
   */
  public final void enable()
  {
    if(!enabled)
      {
	enabled = true;
	fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.ENABLED));
      }
  }

  /** Checks whether this tool is enabled.
   *
   *  @return whether this tool is enabled.
   */
  public final boolean isEnabled()
  {
    return enabled;
  }
  
  /** Disables this tool.
   *
   *  A disabled tool will still be usable programmatically, but should not be
   *  usable to the user.
   *
   *  Fires a ToolStateEvent.
   */
  public final void disable()
  {
    if(enabled)
      {
	enabled = false;
	fireToolStateEvent(new ToolStateEvent(this, ToolStateEvent.DISABLED));
      }
  }
  
  /** Checks whether this tool is activated.
   *
   *  @return whether this tool is activated.
   */
  public final boolean isActivated()
  {
    return activated;
  }

  /** Called when the tool is activated.
   *
   */
  protected abstract void activateImpl();
  
  /** Called when the tool is deactivated.
   *
   *  Never called for ACTION tools.
   */
  protected abstract void deactivateImpl();


  /** Should be called when this tool is no longer being used.
   *
   *  Makes sure this tool detaches all listeners etc.
   *  so that it can be gc'ed. Calling any method in the tool after this will
   *  cause errors.
   *
   *  A tool will be deactivated and have its listeners removed.
   */
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
  protected abstract void detachImpl();


  /** Adds a tool state listener to this tool.
   *
   *  @param l the listener to add.
   */
  public void addToolStateListener(ToolStateListener l)
  {
    toolStateListeners.addElement(l);
  }

  /** Removes a tool state listener from this tool.
   *
   *  @param l the listener to remove.
   */
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
