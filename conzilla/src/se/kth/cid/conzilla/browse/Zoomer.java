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


public class Zoomer
{
  public final static String ZOOM_PROPERTY = "zoom";

  public static Zoomer zoomDefault = new Zoomer();
  
  Vector zoomListeners;
  double scale=1.0;

  public Zoomer()
    {
	zoomListeners = new Vector();
    }
    
  public void addZoomListener(PropertyChangeListener pcl)
    {
	zoomListeners.addElement(pcl);
    }
  public void removeZoomListener(PropertyChangeListener pcl)
    {
	zoomListeners.remove(pcl);
    }
	
  public void fireZoomChange(PropertyChangeEvent pe)
    {
	Enumeration en=zoomListeners.elements();
	for (;en.hasMoreElements();)
	    ((PropertyChangeListener) en.nextElement()).propertyChange(pe);
    }

  public void zoomMap(double factor)
    {
      double newscale = scale*factor;
      fireZoomChange(new PropertyChangeEvent((Object) Zoomer.class, "zoom", new Double(scale), new Double(newscale)));
      scale=newscale;
    }

  public void setScale(double newscale)
    {
      fireZoomChange(new PropertyChangeEvent((Object) Zoomer.class, "zoom", new Double(scale), new Double(newscale)));
      scale=newscale;
    }    
}
