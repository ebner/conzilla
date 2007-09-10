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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class OpenTool extends Tool
{
  MapController controller;

  Component dialogParent;

  //  String lastmap;
  OpenMapDialog omd;
  
  public OpenTool(MapController cont, Component dialogParent)
  {
    super("Open", Tool.ACTION);
    controller = cont;
    omd = null;
    this.dialogParent = dialogParent;
  }

  protected void deactivateImpl()
    {
    }
  protected void detachImpl()
    {
    }
  
  public void open()
    {
      activateImpl();
    }
  
  protected void activateImpl()
    {
      if (omd == null)
	omd = new OpenMapDialog(JOptionPane.getFrameForComponent(dialogParent),
				controller.getMapScrollPane().getDisplayer().getManager().getConceptMap().getURI(),
				controller);
      else
	omd.setURIText(controller.getMapScrollPane().getDisplayer().getManager().getConceptMap().getURI());
      
      /*    String newval = (String) JOptionPane.showInputDialog(dialogParent,
	    "Open map",
	    "Open map",
	    JOptionPane.QUESTION_MESSAGE,
	    null, null, lastmap);
	    if(newval != null)
      {
	try {
	  lastmap = newval;
	  controller.jump(new URI(newval));
	} catch(ControllerException e)
	  {
	    TextOptionPane.showError(dialogParent, "Failed to open map:\n "
				     + e.getMessage());
	  }
	catch(MalformedURIException e)
	  {
	    TextOptionPane.showError(dialogParent, "Malformed URI:\n "
				     + e.getMessage());
	  }
	  }*/
      omd.show();
  }
}

