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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.center.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class NeuronDisplayerCommandTool extends AbstractActionTool
{ 
  protected static String NE_NAME="Edit Neuron";
  
  /** Constructs an NeuronDisplayerCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public NeuronDisplayerCommandTool(MapManager man, MapController cont)
  {
    super(NE_NAME, man, cont);
    action=new AbstractAction(NE_NAME)
      {
        public void actionPerformed(ActionEvent ae)
        {
          Tracer.debug("Open NeuronDisplayer and show this neuron.");
	  action();
        }
      };
  }

  /** The NeuronDisplayer-command is always active since everything has some
   *  some sort of neuron in it.
   */
  protected boolean updateActionImpl(boolean bo)
    {
      return true;
    }

  public void action()
    {
      NeuronDisplayer displayer = controller.getConzKit().neuronDisplayer;
      if(displayer != null)  //Can it ever be null?
	{
	  if (overNeuron!=null)
	    displayer.editNeuron(overNeuron.getNeuron());
	  else
	    displayer.editNeuron(mapevent.mapdisplayer.getContentDescription().getNeuron());
	}
    } 
}
