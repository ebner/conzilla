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


package se.kth.cid.conzilla.map;
import se.kth.cid.conzilla.install.Defaults;
import se.kth.cid.conzilla.browse.Zoomer;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;


public class MapScrollPane extends JScrollPane implements PropertyChangeListener
{
  public static final Integer BACKGROUND_LAYER = new Integer(0);
  public static final Integer MAP_LAYER        = new Integer(20);
  public static final Integer EDIT_LAYER       = new Integer(40);
  
  MapDisplayer displayer;

  JPanel        background;
  
  JLayeredPane       layerPane;

  Zoomer zoomManager;
  
  public MapScrollPane(MapDisplayer displayer, Zoomer zoomManager)
    {
      this.displayer = displayer;
      this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      
      this.getHorizontalScrollBar().setUnitIncrement(15);
      this.getVerticalScrollBar().setUnitIncrement(15);
      this.zoomManager = zoomManager;

      background = new JPanel();
      ColorManager cm = PropertiesManager.getDefaultPropertiesManager().getColorManager();
      background.setBackground(cm.getColor(ColorManager.MAP_BACKGROUND));
      cm.addPropertyChangeListener(ColorManager.MAP_BACKGROUND, this);
      
      zoomManager.addZoomListener(this);

      layerPane = new JLayeredPane();
      layerPane.setLayout(new FillLayout());
      
      layerPane.add(background, BACKGROUND_LAYER);
      layerPane.add(displayer, MAP_LAYER);

      this.setViewportView(layerPane);
            
      revalidate();
    }


  public MapDisplayer getDisplayer()
    {
      return displayer;
    }

  public JLayeredPane getLayeredPane()
    {
      return layerPane;
    }
  
  public void propertyChange(PropertyChangeEvent evt)
    {
	if (evt.getPropertyName().equals(Zoomer.ZOOM_PROPERTY))
	    {
		double factor = ((Double) evt.getNewValue()).doubleValue()/((Double) evt.getOldValue()).doubleValue();
		JViewport port = getViewport();

		Dimension extent = port.getExtentSize();
		Point     pos    = port.getViewPosition();
		
		pos.x =
		    Math.max((int) ((pos.x + extent.width/2)*factor - extent.width/2), 0);
		pos.y =
		    Math.max((int) ((pos.y + extent.height/2)*factor - extent.height/2), 0);
		port.setViewPosition(pos);

		//MapDisplayer is resized here.
		displayer.setScale(((Double) evt.getNewValue()).doubleValue());
	    }
	else
	    background.setBackground((Color) evt.getNewValue());
    }

  public void detach()
    {
	ColorManager cm = PropertiesManager.getDefaultPropertiesManager().getColorManager();
	cm.removePropertyChangeListener(ColorManager.MAP_BACKGROUND, this);
	zoomManager.removeZoomListener(this);
    }
}
