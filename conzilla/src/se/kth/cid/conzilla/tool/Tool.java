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
public interface Tool
{
  /** The EXCLUSIVE tool type.
   */
  int EXCLUSIVE = 0;

  /** The STATE tool type.
   */
  int STATE = 1;

  /** The ACTION tool type.
   */
  int ACTION = 2;

  /** Returns the name of the tool.
   *
   *  @return the name of the tool.
   */
  String getName();

  /** Returns the icon of the tool, if any.
   *
   *  @return the icon of the tool.
   */
  Icon getIcon();

  /** Returns the type of the tool.
   *
   *  @return the type of the tool.
   */
  int getToolType();

  /** Activates this tool.
   *
   *  Will not activate if the tool is already activated.
   *  For types other than ACTION, fires a ToolStateEvent.
   *  Will not change state for tools of type ACTION.
   *
   *  Upon activation, activateImpl is called.
   */
  void activate();
  

  /** Deactivates this tool.
   *
   *  Will not deactivate if the tool is already deactivated.
   *  Fires a ToolStateEvent if the state changes.
   *
   *  Upon deactivation, deactivateImpl is called.
   */
  void deactivate();

  /** Enables this tool.
   *
   *  Fires a ToolStateEvent.
   */
  void enable();
  
  /** Checks whether this tool is enabled.
   *
   *  @return whether this tool is enabled.
   */
  boolean isEnabled();
  
  /** Disables this tool.
   *
   *  A disabled tool will still be usable programmatically, but should not be
   *  usable to the user.
   *
   *  Fires a ToolStateEvent.
   */
  void disable();
  
  /** Checks whether this tool is activated.
   *
   *  @return whether this tool is activated.
   */
  boolean isActivated();

  /** If used, the Tool is given a chance to adapt it's 
   *  behaviour to the situation.
   * (reasonably before activate is called).
   *
   *  For example the Tool is within a ToolSetMenu invoked 
   *  over a ConceptMap. Then before anything else happens all
   *  Tools in the menu that recognizes a MapEvent as parameter
   *  will be updated.
   *
   *  @param o an Object that contains information somehow, 
   *           typically a MapEvent.
   */
  void update(Object o);

  /** Should be called when this tool is no longer being used.
   *
   *  Makes sure this tool detaches all listeners etc.
   *  so that it can be gc'ed. Calling any method in the tool after this will
   *  cause errors.
   *
   *  A tool will be deactivated and have its listeners removed.
   */
  void detach();

  /** Adds a tool state listener to this tool.
   *
   *  @param l the listener to add.
   */
  void addToolStateListener(ToolStateListener l);

  /** Removes a tool state listener from this tool.
   *
   *  @param l the listener to remove.
   */
  void removeToolStateListener(ToolStateListener l);
}
