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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.map.*;
import java.awt.*;
import javax.swing.*;

/** A Action Tool acts on something, often it does so from a menu
 *  invoked over a certain neuron or as a layer sensitive for
 *  mouseklicks.
 *
 *  This interface gives you primitives to easily add the Action
 *  to a menu (JPopupMenu) and also to update it before it is
 *  invoked. (Typically this is done over a map.)
 *
 *  Implement/override updateActionImpl to decide when an
 *  action should occur.
 *
 *  Use the activate/deactivate functions to add/remove a
 *  layer on top of the map. This layer will autmatically
 *  listen for mouseclicks and invoke the action if
 *  the updateActionImpl functions says ok.
 *
 *  WARNING: Do not use both functionalitys at once.
 *           Choose one of the following strategies:
 *          1) Use putToolInMenu, and call updateAction
 *             just before the menu is invoked.
 *             Do not use activate/deactivate.
 *          2) Go in and out of layer mode with
 *             activate/deactivate functions inherited
 *             from Tool.
 *             Do not use putToolInMenu or call updateAction
 *             by hand.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface ActionTool extends Tool
{
  
  /** Adds the Tool as JMenuItem to a given menu.
   *
   * @param menu a JpopupMenu to add an action to.
   */
  void putToolInMenu(JPopupMenu menu);
  
  /** The Tool can be given a chance to update it's state
   *  from an MapEvent.
   *
   *  Typically a menu is invoked over a Neuron in a map.
   *  Then before anything else happens all subsequent
   *  Tools in the menu can be given a chance to update themselfs.
   *
   *  @param e the event from the map.
   */
  boolean updateAction(MapEvent e);

  /** Essentially, this is what the ActionTool should do
   *  when invoked.
   *  Implement it according to your own action
   *  intended behaviour. The UpdateAction function should
   *  normally have been called before.
   *
   *  If the ActionTool is an menu the action will
   *  never be called explicitly, instead everything happends in
   *  updatestate.
   */
  void action();
}
