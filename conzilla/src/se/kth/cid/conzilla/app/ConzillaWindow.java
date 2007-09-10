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


package se.kth.cid.conzilla.app;

import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.browse.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

class ConzillaWindow extends JFrame
{
  JPanel browsePanel;
  
  MapController controller;
  
  //  OpenTool opentool;
  
  public ConzillaWindow(MapController cont)
    {
      this.controller = cont;
      //      opentool = new OpenTool(controller, this);
      
      addWindowListener(new WindowAdapter() {
	  public void windowClosing(WindowEvent e) {
	    controller.getConzillaKit().getConzilla().close(ConzillaWindow.this);
	  }
	});

      layoutInternals();
      
      setSize(420, 420);
      setLocation(100, 100);
    }
  
  void layoutInternals()
    {
      browsePanel = new JPanel();
      browsePanel.setLayout(new BorderLayout());
      browsePanel.add(controller.getMapPanel(), BorderLayout.CENTER);
      browsePanel.add(controller.getContentSelector().getComponent(),
		      BorderLayout.EAST);
      
      getContentPane().add(browsePanel, BorderLayout.CENTER);
      getContentPane().add(controller.getToolBar(), BorderLayout.SOUTH);
    }
  
  public void close()
    {
      controller = null;
      browsePanel = null;

      dispose();
    }

  public MapController getController()
    {
      return controller;
    }
}
