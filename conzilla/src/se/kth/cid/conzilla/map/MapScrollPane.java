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
import se.kth.cid.conceptmap.*;
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


public class MapScrollPane extends JScrollPane  implements EditListener
{
  public static final Integer BACKGROUND_LAYER = new Integer(0);
  public static final Integer MAP_LAYER        = new Integer(20);
  public static final Integer EDIT_LAYER       = new Integer(40);
  
  MapDisplayer displayer;

  JPanel        background;
  
  JLayeredPane       layerPane;
  
  public MapScrollPane(MapDisplayer displayer)
    {
      this.displayer = displayer;

      background = new JPanel();
      background.setBackground(Color.white);
      
      layerPane = new JLayeredPane();
      layerPane.setLayout(new FillLayout());
      
      layerPane.add(background, BACKGROUND_LAYER);
      layerPane.add(displayer, MAP_LAYER);
      
      resizeMap();      
      
      this.setViewportView(layerPane);
      
      revalidate();
      
      displayer.getStoreManager().getConceptMap().addEditListener(this);
    }


  public MapDisplayer getDisplayer()
    {
      return displayer;
    }

  public JLayeredPane getLayeredPane()
    {
      return layerPane;
    }

  public void componentEdited(EditEvent e)
    {
      if (e.getEditType() == ConceptMap.DIMENSION_EDITED)
	{
	  Tracer.debug("Dim edit");
	  resizeMap();
	  revalidate();
	}
    }

  void resizeMap()
    {
      ConceptMap.Dimension dim = displayer.getStoreManager().getConceptMap().getDimension();

      layerPane.setMinimumSize(new Dimension(dim.width, dim.height));
      layerPane.setPreferredSize(new Dimension(dim.width, dim.height));
    }

  public void detach()
    {
      displayer.getStoreManager().getConceptMap().removeEditListener(this);
    }
}
