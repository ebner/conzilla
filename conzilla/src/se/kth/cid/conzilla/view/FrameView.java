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


package se.kth.cid.conzilla.view;

import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.view.*;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.identity.*;


import java.util.*;
import java.net.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.awt.event.*;

public class FrameView extends JFrame implements PropertyChangeListener, View
{
  FrameManager frameManager;
  MapController controller;
  ConzillaSplitPane conzillaSplitPane;


  public FrameView(FrameManager frameManager, MapController controller)
  {
    this.frameManager = frameManager;
    this.controller=controller;

    final FrameManager fm = frameManager;
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		fm.close(FrameView.this, true);
	    }});
    
    
    LocaleManager.getLocaleManager().addPropertyChangeListener(this);

    conzillaSplitPane = new ConzillaSplitPane();
    //Pane workaround for mac, mac-menus is moved up and can't contain buttons.
    //Complementary code in FrameManager in function propertychange.
    JRootPane pane = new JRootPane();
    pane.setContentPane(conzillaSplitPane);
    setContentPane(pane);

    controller.addPropertyChangeListener(this);

    conzillaSplitPane.setPanes(controller.getMapPanel(), controller.getContentSelector());
    controller.getMapPanel().setVisible(true); 
    updateTitle();

  }

    public void updateFonts()
    {
	SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(this));
    }

    public void draw()
    {
	show();
	pack();
    }

  public void propertyChange(PropertyChangeEvent e)
  {
    if (MapController.MAP_PROPERTY.equals(e.getPropertyName()) || LocaleManager.DEFAULT_LOCALE_PROPERTY.equals(e.getPropertyName()))
      {
	updateTitle();
      }
  }
  
  void updateTitle()
    {
      String title = "(none)";
      
      if(controller.getMapScrollPane() != null)
	{
	  ConceptMap map = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	  
	  se.kth.cid.component.MetaData md = map.getMetaData();
	  title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	}
      setTitle("Conzilla - " + title);
    }
  
  protected void close(boolean closeController)
    {
	controller.removePropertyChangeListener(this);
	
	if(closeController)
	    controller.detach();

	controller = null;
	
	conzillaSplitPane.detach();
	LocaleManager.getLocaleManager().removePropertyChangeListener(this);
	dispose();
    }

  
  public MapController getController()
    {
      return controller;
    }
}
