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


public class TextAnchorMapTool extends MapToolsMenu
{  


  class AnchorMenuTool extends MapMenuTool
  {
      public AnchorMenuTool(final String name, final int value, MapController cont, final boolean vertical)
      {
	  super(name, EditMapManagerFactory.class.getName(), cont);

	  final JRadioButtonMenuItem mi = new JRadioButtonMenuItem();
	  setJMenuItem(mi);
	  mi.addItemListener(new ItemListener() {
		  public void itemStateChanged(ItemEvent e)
		  {
		      if(e.getStateChange() == ItemEvent.SELECTED && neuronStyle !=null)
			  if (vertical)
			      neuronStyle.setVerticalTextAnchor(value);
			  else
			      neuronStyle.setHorisontalTextAnchor(value);
		  }});
      }
  }

    AnchorMenuTool north;
    AnchorMenuTool south;
    AnchorMenuTool hcenter;
    AnchorMenuTool vcenter;
    AnchorMenuTool west;
    AnchorMenuTool east;
    NeuronStyle neuronStyle;

  /** Constructs an DataVisibilityMapTool.
   */
  public TextAnchorMapTool(MapController cont)
  {
    super("TEXT_ANCHOR", EditMapManagerFactory.class.getName(), cont);


    ButtonGroup vgroup = new ButtonGroup();
    north = new AnchorMenuTool("NORTH", NeuronStyle.NORTH, cont, true);
    vgroup.add((AbstractButton) north.getJMenuItem());
    addMapMenuItem(north, 100);

    vcenter = new AnchorMenuTool("CENTER", NeuronStyle.CENTER, cont, true);
    vgroup.add((AbstractButton) vcenter.getJMenuItem());
    addMapMenuItem(vcenter, 200);

    south = new AnchorMenuTool("SOUTH", NeuronStyle.SOUTH, cont, true);
    vgroup.add((AbstractButton) south.getJMenuItem());
    addMapMenuItem(south, 300);

    addSeparator(399);
    
    ButtonGroup hgroup = new ButtonGroup();
    west = new AnchorMenuTool("WEST", NeuronStyle.WEST, cont, false);
    hgroup.add((AbstractButton) west.getJMenuItem());
    addMapMenuItem(west, 400);

    hcenter = new AnchorMenuTool("CENTER", NeuronStyle.CENTER, cont, false);
    hgroup.add((AbstractButton) hcenter.getJMenuItem());
    addMapMenuItem(hcenter, 500);

    east = new AnchorMenuTool("EAST", NeuronStyle.EAST, cont, false);
    hgroup.add((AbstractButton) east.getJMenuItem());
    addMapMenuItem(east, 600);
  }

  public void update(MapEvent mapEvent)
    {
	if (!getPopupMenu().isVisible() && 
	    mapEvent.mapObject != null &&
	    mapEvent.mapObject.getNeuronStyle() != null)
	    {
		neuronStyle = null;
		NeuronStyle ns = mapEvent.mapObject.getNeuronStyle();
		switch (ns.getHorisontalTextAnchor())
		    {
		    case NeuronStyle.WEST:
			west.getJMenuItem().setSelected(true);
			break;
		    case NeuronStyle.CENTER:
			hcenter.getJMenuItem().setSelected(true);
			break;
		    case NeuronStyle.EAST:
			east.getJMenuItem().setSelected(true);
			break;
		    }
		switch (ns.getVerticalTextAnchor())
		    {
		    case NeuronStyle.NORTH:
			north.getJMenuItem().setSelected(true);
			break;
		    case NeuronStyle.CENTER:
			vcenter.getJMenuItem().setSelected(true);
			break;
		    case NeuronStyle.SOUTH:
			south.getJMenuItem().setSelected(true);
			break;
		    }
		setEnabled(true);
		neuronStyle = ns;
	    }
      else
	  setEnabled(false);
      
      super.update(mapEvent);
  }
}


