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
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.awt.event.*;

public class ConzillaSplitPane extends JSplitPane
{
  MapController controller;
  java.awt.Component component;
  SplitPaneLayoutFixer adapt;
    
  public ConzillaSplitPane()
  {
      super(JSplitPane.HORIZONTAL_SPLIT, false);
      //    setDividerSize(5);
      setOneTouchExpandable(true);
      controller = null;
  }
    
    /** This funtion sets the two panes of the splitpane.
     */
    public void setPanes(JComponent left, ContentSelector selector)
    {
	if (component != null)
	    component.removeComponentListener(adapt);

	if(adapt != null)
	    adapt.detach();

	setLeftComponent(left);
	setRightComponent(selector.getComponent());	      
	
	component = this;
	while (component.getParent() != null)
	    component = component.getParent();
    
	adapt = new SplitPaneLayoutFixer(component, selector, this);

	component.addComponentListener(adapt);
	//Calculates and sets the new dividerLocation.
	int dividerLocation;

	if (getRightComponent().getPreferredSize().width != 0)
	    dividerLocation = getWidth()-
		getRightComponent().getPreferredSize().width-
		getDividerSize() -5;
	else
	    dividerLocation = getWidth();
	setDividerLocation(dividerLocation);
	//    revalidate();
	
    }

    public void detach()
    {
	component.removeComponentListener(adapt);
	adapt.detach();
	adapt = null;
	component = null;
	controller = null;
    }
 
  class SplitPaneLayoutFixer extends ComponentAdapter
  {
      Dimension dim;
      Point location;
      java.awt.Component component;
      boolean selectorActive=false;
      final JSplitPane split;
      
      PropertyChangeListener selectionl; 
      ContentSelector selector;

      public SplitPaneLayoutFixer(java.awt.Component component, ContentSelector selector, final JSplitPane split)
      {
	  this.component=component;
	  this.split=split;
	  this.selector = selector;
	  
	  location=component.getLocation();
	  dim=component.getSize();
	  
	  selectionl = new PropertyChangeListener() {
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
	      };

	  selector.addSelectionListener(ContentSelector.SELECTOR, selectionl);
      }
      
      void detach()
      {
	  selector.removeSelectionListener(ContentSelector.SELECTOR, selectionl);
	  selector = null;
	  component = null;
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
