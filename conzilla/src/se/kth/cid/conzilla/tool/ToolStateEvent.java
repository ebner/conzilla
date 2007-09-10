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

import java.util.*;

/** This class represents a change in a tool's state.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ToolStateEvent extends EventObject
{
  /** The tool has been activated.
   */
  public final static int ACTIVATED = 0;

  /** The tool has been deactivated.
   */
  public final static int DEACTIVATED = 1;

  /** The tool has been enabled.
   */
  public final static int ENABLED = 2;

  /** The tool has been disabled.
   */
  public final static int DISABLED = 3;

  /** The type of event.
   */
  int event;

  /** Constructs a ToolStateEvent.
   *
   *  @param source the source of this event.
   *  @param event the type of event.
   */
  public ToolStateEvent(Tool source, int event)
  {
    super(source);
    this.event = event;
  }

  /** Returns the tool that caused this event.
   *
   *  @return the tool that caused this event.
   */
  public Tool getTool()
  {
    return (Tool) source;
  }

  /** Returns the type of event.
   *
   *  @return the type of event.
   */
  public int getEvent()
  {
    return event;
  }
}
