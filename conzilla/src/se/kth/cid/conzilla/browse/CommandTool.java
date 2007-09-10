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


package se.kth.cid.conzilla.browse;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import javax.swing.*;

/** This class is the base class for browse-like tools.
 *
 *  It is used to click on interesting neurons and to select content.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class CommandTool extends Tool
{
  /** The action to be placed in a JPopupMenu for triggering the command.
   */
  //  protected AbstractAction action; // Should suffice with ToolSetMenu...
  
  /** The controller to use.
   */
  protected MapController controller;
  
  /** The manager this tool is attached to.
   */
  //  protected MapManager manager;
  
  /** The neuron we are currently over.
   */
  protected NeuronStyle overNeuron = null;

  //  protected MapEvent mapevent;
  
  /** Constructs a CommandTool.
   *
   *  @param name the name of the tool.
   *  @param cont the controller controlling the map.
   */
  public CommandTool(String name, MapController cont)
    {
      super(name, Tool.ACTION);
      controller = cont;
    }
  
  /** Adds the action-command as an action to a given menu.
   *
   * @param menu a JpopupMenu to add an action to.
   */
  //  public void putActionInMenu(JPopupMenu menu)
  //  {
  //    menu.add(action);
  //  }
  
  /** Updates the state in response to a cursor move.
   *
   *  @param e the event that triggered the update.
   */
  public void updateState(MapEvent e)
  {
    if(e.mapObject != null)
      overNeuron = e.mapObject.getNeuronStyle();
    else
      overNeuron = null;

    updateStateImpl();
  }

  protected abstract void updateStateImpl();
  
  protected void deactivateImpl()
  {
    Tracer.debug(getName() + "-command inactive!");
    overNeuron = null;
  }

  protected void detachImpl()
  {
    controller = null;

    overNeuron = null;    
  }
}
