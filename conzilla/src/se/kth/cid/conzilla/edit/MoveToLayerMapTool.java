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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class MoveToLayerMapTool extends MapToolsMenu
{  
  NeuronStyle neuronStyle;

  class LayerMenuTool extends MapMenuTool
  {
      public LayerMenuTool(final LayerStyle ls, final MapController cont, final LayerManager lMan)
      {
	  super(ls.getURI(), null, cont);

	  final JRadioButtonMenuItem mi = new JRadioButtonMenuItem();
	  setJMenuItem(mi);
	  mi.addItemListener(new ItemListener() {
		  public void itemStateChanged(ItemEvent e)
		  {
		      if(e.getStateChange() == ItemEvent.SELECTED && neuronStyle !=null)
			  {
			      MapGroupStyle mgs = lMan.getParent(neuronStyle);
			      //FIXME: tag stuff is dubious...
			      Object tag = mgs.getObjectStyleTag(neuronStyle);
			      mgs.removeObjectStyle(neuronStyle);
			      ls.addObjectStyle(neuronStyle, tag);
			      cont.getMapScrollPane().getDisplayer().repaint();
			  }
		  }});
      }
  }

  /** Constructs an DataVisibilityMapTool.
   */
  public MoveToLayerMapTool(MapController cont)
  {
    super("MOVE_TO_LAYER", EditMapManagerFactory.class.getName(), cont);
  }

  public void update(MapEvent mapEvent)
    {
	if (!getPopupMenu().isVisible() && 
	    mapEvent.mapObject != null &&
	    mapEvent.mapObject.getNeuronStyle() != null)
	    {
		removeAll();
		LayerManager lMan = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getLayerManager();
		
		neuronStyle = null;
		NeuronStyle ns = mapEvent.mapObject.getNeuronStyle();
		MapGroupStyle parent = lMan.getParent(ns);
		Vector layers = lMan.getLayers();
		for (int counter = layers.size()-1; counter >= 0 ; counter--)
		    {
			LayerStyle ls = (LayerStyle) layers.elementAt(counter);
			LayerMenuTool lmt = new LayerMenuTool(ls, controller, lMan);
			if (ls == parent)
			    ((AbstractButton) lmt.getJMenuItem()).setSelected(true);
			addMapMenuItem(lmt, 10*(layers.size()-1-counter));
		    }
		
		setEnabled(true);
		neuronStyle = ns;
	    }
      else
	  setEnabled(false);
      
      super.update(mapEvent);
  }
}


