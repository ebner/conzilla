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

package se.kth.cid.conzilla.controller;

import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;

/** This interface is used to create the tools and toolbars for a new
 *  MapManager.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ToolFactory
{
  /** Called when a new MapManager has been created.
   *
   *  This method is supposed to fill in the MapManager's tool set, tool bars,
   *  and set the active toolbar.
   * 
   *  @param manager the new manager.
   */
  void makeTools(MapManager manager);

  /** Called whenever a new toolbar is needed.
   *
   *  This method is called by a ToolBarFactory automatically when a
   *  toolBar is needed. I.e. When a LazyToolBar can't be lazy anymore.
   *  (ToolBarFactory extends LazyToolBar)
   *
   *  @param manager  a MapManager responsible for the belonging ToolSet among other stuff.
   *  @param name     a string describing which of the ToolSets should be showed.
   *  @param tsb      a ToolSetBar to add new tools to.
   */
  ToolSetBar newToolBar(MapManager manager, String name, ToolSetBar tsb);

  /** Called whenever a ToolSetBar has served it's duty.
   *  @param toolSetBar  the ToolSetBar that should be detached.
   */
  void detachToolBar(ToolSetBar toolSetBar);
}
