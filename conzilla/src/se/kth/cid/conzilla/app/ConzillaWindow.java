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
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.metadata.*;
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

public class ConzillaWindow extends JFrame implements PropertyChangeListener, LocaleListener
{
  JPanel browsePanel;
  
  MapController controller;
  
  public OpenMapHandler openMapHandler;
  
  public ConzillaWindow(MapController cont)
  {
    this.controller = cont;
    //      opentool = new OpenTool(controller, this);
      
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	controller.getConzillaKit().getConzilla().close(ConzillaWindow.this);
      }
    });

    LocaleManager.getLocaleManager().addLocaleListener(this);

    setJMenuBar(new javax.swing.JMenuBar());

    layoutInternals();
      
    openMapHandler=new OpenMapHandler(controller);
    cont.addPropertyChangeListener(this);
  }

  /** This funtion sets a new controller for this Window.
   *  Observe that the old MapController is't detached....
   *  This has to be done explicitly somewhere else.
   *
   *  @see getController()
   */
  public void setController(MapController controller)
  {
    if (controller!=null)
      controller.removePropertyChangeListener(this);
    this.controller=controller;
    openMapHandler=new OpenMapHandler(controller);
    controller.addPropertyChangeListener(this);
    layoutInternals();
  }
    
  void layoutInternals()
  {
    if (browsePanel!=null)
	  getContentPane().removeAll();
    browsePanel = new JPanel();
    browsePanel.setLayout(new BorderLayout());
    final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				      false,
				      controller.getMapPanel(),
				      controller.getContentSelector().getComponent());	      

    split.setOneTouchExpandable(true);
    browsePanel.add(split, BorderLayout.CENTER);

    java.awt.Component component=split;
    while (component.getParent()!=null)
	component=component.getParent();
    
    SplitPaneLayoutFixer adapt=new SplitPaneLayoutFixer(component, controller, split);

    component.addComponentListener(adapt);
    getContentPane().add(browsePanel, BorderLayout.CENTER);
    //getContentPane().add(controller.getToolBar(), BorderLayout.NORTH);
  }


  public void propertyChange(PropertyChangeEvent e)
  {
    if ("map".equals(e.getPropertyName()))
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
	  
	  MetaData md = map.getMetaData();
	  title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	}
      setTitle("Conzilla - " + title);
    }
  
  public void close()
    {
      controller.detach();
      controller = null;
      browsePanel = null;

      LocaleManager.getLocaleManager().removeLocaleListener(this);
      dispose();
    }

  public void localeAdded(LocaleEvent e)
    {}
  public void localeRemoved(LocaleEvent e)
    {}
  public void setDefaultLocale(LocaleEvent e)
    {
      updateTitle();
    }

  
  public MapController getController()
    {
      return controller;
    }

  class SplitPaneLayoutFixer extends ComponentAdapter
  {
      Dimension dim;
      Point location;
      java.awt.Component component;
      MapController controller;
      boolean selectorActive=false;
      final JSplitPane split;
      
      public SplitPaneLayoutFixer(java.awt.Component component, MapController controller, final JSplitPane split)
      {
	  this.controller=controller;
	  this.component=component;
	  this.split=split;
	  
	  location=component.getLocation();
	  dim=component.getSize();
	  
	  controller.getContentSelector().addSelectionListener(ContentSelector.SELECTOR, 
							       new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
			    //Sets the mode for the resize behaviour.
			    selectorActive= e.getNewValue()!=null;
			    
			    //Calculates and sets the new dividerLocation.
			    int dividerLocation;
			    if (split.getRightComponent().getPreferredSize().width != 0)
				dividerLocation = split.getWidth()-
				    split.getRightComponent().getPreferredSize().width-
				    split.getDividerSize() -5;
			    else
				dividerLocation = split.getWidth();
			    split.setDividerLocation(dividerLocation);
			    split.revalidate();
			}
		    });

	    }

      public void componentResized(ComponentEvent m)
      {
	  Dimension newdim=component.getSize();
	  Point newlocation=component.getLocation();
	  int left=newlocation.x-location.x;
	  int right=left + newdim.width - dim.width;
	  
	  //FIXME:  This is a very buggy solution, the UI (layoutmanager inside
	  //the UI) calculates the new loction separately from this code.
	  //Hence the location asked for here can already have been modified.
	  int divloc = split.getDividerLocation(); 

	  if (selectorActive)
	      {
		  if (right != 0)
		      divloc+=right;
		  if (divloc> split.getWidth())
		      divloc=split.getWidth();
	      }
	  else
	      divloc=split.getWidth();
	  split.setDividerLocation(divloc);
	  
	  dim=newdim;
	  location=newlocation;
      }
  }
}
