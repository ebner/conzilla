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
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;

import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class ZoomTool extends Tool
{
  MapController controller;

  double scale;

  public ZoomTool(MapController cont, double scale)
  {
      super(scale > 1 ? "ZOOMIN" : "ZOOMOUT", MapController.class.getName());

      setIcon(new ImageIcon(getClass().getResource("/graphics/toolbarButtonGraphics/general/Zoom"
						   + (scale > 1 ? "In" : "Out") +"16.gif")));
      
      if(scale > 1)
	  setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK));
      else
	  setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK));
      this.scale = scale;
      this.controller = cont;
  }

    public void actionPerformed(ActionEvent e)
    {
	controller.zoomMap(scale);
    }

}
