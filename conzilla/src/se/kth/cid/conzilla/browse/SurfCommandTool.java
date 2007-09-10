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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a surf-command-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron to act on.
 *  Typically this is done by calling updateState with a mapEvent as input.
 *
 *  @author Matthias Palm�r
 *  @version $Revision$
 */
public class SurfCommandTool extends AbstractActionTool
{
  protected static String SURF_NAME="Surf";

  /** Constructs an SurfCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public SurfCommandTool(MapManager man, MapController cont)
  {
    super(SURF_NAME, man, cont);
    action=new AbstractAction(SURF_NAME)
      {
        public void actionPerformed(ActionEvent ae)
	  {
	    Tracer.debug("Surf in to a (hopefully) more detailed map.");
	    action();
	  }
      };
  }

  /** Enables or disenabled the surf command in the menu accordingly to if
   *  there is a detailedmap to surf to.
   */
  protected boolean updateActionImpl(boolean bo)
    {
      if (bo && overNeuron.getDetailedMap() != null)
	{
	  action.setEnabled(true);
	  return true;
	}
      action.setEnabled(false);
      return false;
    }

  /** This is a surf-command that results in a zoomIn via the controller.
   *  Observe that updateState has to have been succesfully last time called.
   *  Otherwise the surf-action isn't activated and this function isn't called.
   *
   *  @see Controller.zoomIn()
   */
  public void action()
    {
      if (overNeuron==null)
	return;
      try {
	controller.zoomIn(new URI(overNeuron.getNeuron().getURI()));
      }
      catch(ControllerException e)
	{
	  TextOptionPane.showError(manager, "Failed to zoom in:\n "
				   + e.getMessage());
	}
      catch(MalformedURIException e)
	{
	  Tracer.trace("Component had illegal URI: " +
		       overNeuron.getNeuron().getURI()
		       + ": " + e.getMessage()
		       + "!", Tracer.ERROR);
	}
    }
}
