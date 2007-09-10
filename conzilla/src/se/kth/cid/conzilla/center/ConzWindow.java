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


package se.kth.cid.conzilla.center;

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

class ConzWindow extends JFrame
{
  ListContentSelector selector;
  JPanel center;
  
  public MapController controller;
  BasicToolFactory toolfactory;
  JMenuBar menubar;

  public OpenTool opentool;
  
  public ConzWindow()
    {
      super();
      selector = new ListContentSelector();
      addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  ConzWindow.this.controller.getConzKit().conzilla.close(ConzWindow.this);
	}
      });
      setSize(420, 420);
      setLocation(100, 100);
      show();
    }
  public void closeContentSelectionView()
    {
      selector.selectContent(null);
    }
  public void setMenuBar(JMenuBar menubar)
    {
      this.menubar=menubar;
    }  
  public void setController(MapController controller)
    {
      this.controller = controller;
      opentool=new OpenTool(controller, (Component) controller);
      controller.setContentSelector(selector);
    }

  public void loadMap(URI map) throws ControllerException
    {
      controller.jump(map);
    }
  
  public void newFactory()
    {
      toolfactory = new BasicToolFactory(controller, (Component) controller);
      controller.setToolFactory(toolfactory);
    }
  public void fixLayout()
    {
      center = new JPanel();
      center.setLayout(new BorderLayout());
      center.add(((SimpleController) controller).getTitleBox(), BorderLayout.NORTH);
      center.add((SimpleController) controller, BorderLayout.CENTER);
      center.add(selector, BorderLayout.EAST);
	
      getContentPane().add(center, BorderLayout.CENTER);
      getContentPane().add(((SimpleController) controller).getToolBar(), BorderLayout.SOUTH);
      setJMenuBar(menubar);
    }
  public void close()
    {
      controller=null;
      selector=null;
      toolfactory=null;
      center=null;
      menubar=null;
      dispose();
    }
}
