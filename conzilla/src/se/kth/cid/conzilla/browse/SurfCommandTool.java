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
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
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
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class SurfCommandTool extends CommandTool
{
  protected static String SURF_NAME="Surf";  
  
  /** Constructs an SurfCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public SurfCommandTool(MapController cont)
  {
    super(SURF_NAME, cont);
//    action=new AbstractAction(SURF_NAME)
//	{
//	  public void actionPerformed(ActionEvent ae)
//	    {
//	      Tracer.debug("Surf in to a (hopefully) more detailed map.");
//	      surf();
//	    }
//	};
  }
  
  /** Enables or disenabled the surf command in the menu accordingly to if
   *  there is a detailedmap to surf to.
   */
  protected void updateStateImpl()
    {
      if (overNeuron != null && overNeuron.getDetailedMap() != null)
	enable();
      else      
	disable();
    }
  
  /** This is a surf-command that results in a zoomIn via the controller.
   *  Observe that updateState has to have been succesfully last time called.
   *  Otherwise the surf-action isn't activated and this function isn't called.
   *
   *  @see Controller.zoomIn()
   */
  protected void activateImpl()
    {
      try {
	controller.showMap(URIClassifier.parseURI(overNeuron.getDetailedMap()));
	Tracer.debug(getName() + "-command active!");
      }
      catch(ControllerException e)
	{
	  TextOptionPane.showError(controller.getMapScrollPane(),
				   "Failed to zoom in:\n "
				   + e.getMessage());
	}
      catch(MalformedURIException e)
	{
	  Tracer.trace("Component had illegal URI: " +
		       overNeuron.getDetailedMap()
		       + ": " + e.getMessage()
		       + "!", Tracer.ERROR);
	}
    }
}
