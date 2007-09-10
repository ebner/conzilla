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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/** This class is for convenience when implementing ActionTools.
 *
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class AbstractActionTool extends AbstractTool implements ActionTool, MapEventListener
{
  /** The action to be placed in a JPopupMenu for triggering the command.
   */
  protected AbstractAction action;
  
  /** The controller to use.
   */
  protected MapController controller;

  /** The manager this tool is attached to.
   */
  protected MapManager manager;

  /** The neuron we are currently over.
   */
  protected NeuronStyle overNeuron = null;

  /** UpdateState saves the MapEvent for use when
   *  the tool is used.*/
  protected MapEvent mapevent;
  
  /** Constructs an AbstractActionTool.
   *
   *  @param name the name of the tool.
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public AbstractActionTool(String name, MapManager man, MapController cont)
    {
      super(name, Tool.ACTION);
      controller = cont;
      manager = man;
    }
  
  /** Adds the tool-command as an action to a given menu.
   *
   * @param menu a JpopupMenu to add an action to.
   */
  public void putToolInMenu(JPopupMenu menu)
  {
    menu.add(action);
  }
  
  /** Call this before the function action is called,
   *  it will update the ActionTool depending on what
   *  to act on.
   *
   *  @param e the event that triggered the update.
   */
  public boolean updateAction(MapEvent e)
  {
    mapevent=e;
    if(e.neuronstyle != null)
      overNeuron = e.neuronstyle;
    else if(e.rolestyle != null)
      overNeuron = e.rolestyle.getRoleOwner();
    else
      {
	overNeuron = null;
	return 	updateActionImpl(false);
      }
    return updateActionImpl(true);
  }

  /** You must implement this function,
   *  it is intended to add update behaviour.
   */
  protected abstract boolean updateActionImpl(boolean bo);

  /** This add the layer functionality of the ActionTool.
   */
  protected void activateImpl()
  {
    manager.getDisplayer().addMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    Tracer.debug(getName() + "-command active!");
  }

  /** This removes the layer functionality of the ActionTool.
   */
  protected void deactivateImpl()
  {
    manager.getDisplayer().removeMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    Tracer.debug(getName() + "-command inactive!");
    overNeuron = null;
  }

  /** This function is called when the action tool is activated,
   *  i.e. it works as a mode in a map.
   *
   *  @see MapEventListener
   */
  public void eventTriggered(MapEvent m)
  {
    if (m.mouseevent.getID()==MouseEvent.MOUSE_PRESSED)
      {
	m.checkEditability(controller.getConzKit());
	if (updateAction(m))
	  action();
      }
  }

  /** @see Tool.detachImpl()
   */
  protected void detachImpl()
  {
    controller = null;
    manager = null;
    
    overNeuron = null;    
  }

  /** @see ActionTool.action() */
  public void action() {}
}
