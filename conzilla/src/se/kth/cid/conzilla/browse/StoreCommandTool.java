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
import se.kth.cid.conzilla.library.*;
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
public class StoreCommandTool extends CommandTool
{

  protected static String STORE_NAME="Store";

  ContentDescription cdesc;
  
  /** Constructs an StoreCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public StoreCommandTool(MapManager man, MapController cont)
  {
    super(STORE_NAME, man, cont);
    action=new AbstractAction(STORE_NAME)
      {
        public void actionPerformed(ActionEvent ae)
        {
          Tracer.debug("Store this neuron in library.");
	  store();
        }
      };
    cdesc=null;
  }

  public void registerContentDescription(ContentDescription cd)
  {
    cdesc=cd;
  }
  
  /** The Store-command is always active since everything is some sort
   *  of neuron. A conceptmap, is stored as it's
   *  ContentDescription which also is a neuron.
   */
  protected boolean updateStateImpl(boolean bo)
    {
      return true;
    }

  protected void store()
    {
      if (cdesc!=null)
	{
	  ClipboardLibrary cl=(ClipboardLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
	  if (cl!=null)
	    cl.addNeuron(cdesc);
	  cdesc=null;
	}
      else 
	{
	  ClipboardLibrary cl=(ClipboardLibrary) controller.getLibraryDisplayer().getLibrary().getLibrary("clipboardlibrary");
	  if (cl!=null)
	    cl.addNeuron(mapevent);
	}
    }
}

