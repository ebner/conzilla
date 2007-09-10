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
import se.kth.cid.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a info-command-tool that have to be embedded into a menu.
 *  The reason is that it needs a neuron or a contentdescription to act on.
 *  Typically this is done by calling updateState with a mapEvent as input. 
 *
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class InfoCommandTool extends CommandTool
{

  protected static String INFO_NAME="Info";

  ContentDescription cdesc;
  
  /** Constructs an InfoCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public InfoCommandTool(MapManager man, MapController cont)
  {
    super(INFO_NAME, man, cont);
    action=new AbstractAction(INFO_NAME)
      {
        public void actionPerformed(ActionEvent ae)
        {
          Tracer.debug("See the metadata associated with this neuron.");
	  info();
        }
      };
    cdesc=null;
  }

  public void registerContentDescription(ContentDescription cd)
  {
    cdesc=cd;
  }
  
  /** The info-command is always active since everything contains
   *  some sort of metadata-information.
   *
   */
  protected boolean updateStateImpl(boolean bo)
    {
      return true;
    }

    protected void info()
    {
      MetaDataDisplayer displayer = controller.getMetaDataDisplayer();
      if(displayer != null)
	{
	  if (cdesc!=null)
	    {
	      displayer.showMetaData(cdesc.getNeuron());
	      cdesc=null;
	    }
	  else if (overNeuron != null)
	    displayer.showMetaData(overNeuron.getNeuron());
	  else
	    displayer.showMetaData(manager.getDisplayer().getContentDescription().getNeuron());
	}
    }
}
