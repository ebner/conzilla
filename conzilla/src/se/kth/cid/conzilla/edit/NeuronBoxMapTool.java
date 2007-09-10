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
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import javax.swing.*;
import java.awt.event.*;

public class NeuronBoxMapTool extends MapTool implements AlterationTool
{

  String boxVisible;
  String boxInvisible;
  String currentName;

  public NeuronBoxMapTool(String boxVisible, String boxInvisible , MapController cont)
  {
    super(boxVisible,Tool.ACTION, cont);
    this.boxVisible=boxVisible;
    this.boxInvisible=boxInvisible;
    currentName = boxVisible;
  }
    
  public JMenuItem getMenuItem()
    {       
	if (mapEvent==null || mapObject==null || !mapEvent.mapObject.getNeuronStyle().getBodyVisible())
	    currentName = boxInvisible;
	else
	    currentName = boxVisible;
	JMenuItem mi=new JMenuItem(currentName);

	AbstractAction act=new AbstractAction() {
	  public void actionPerformed(ActionEvent ae)
	    {
	      NeuronBoxMapTool.this.activate();
	    }
	};
	
	mi.addActionListener(act);
	return mi;
    }

  public String getName()
    {
	return currentName;
    }
	
  protected boolean updateImpl()
    {
      if (mapEvent.hitType==MapEvent.HIT_NONE)
	return false;
      return true;
    }

  public void activateImpl()
    {
      if (mapObject.getNeuronStyle().getBodyVisible())
	  if (mapObject.getNeuronStyle().getAxonStyles().length == 0)
	      {
		  if (JOptionPane.showConfirmDialog(null, 
						    "This neuron will be removed since invisible neurons can't be handled.",
						    "Neuron removal", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
		      mapObject.getNeuronStyle().remove();
	      }
	  else
	      mapObject.getNeuronStyle().setBodyVisible(false);
      else
	{
	  mapObject.getNeuronStyle().setBodyVisible(true);
	  ConceptMap.Dimension ndim=mapObject.getNeuronStyle().getBoundingBox().dim;
	  if (ndim.width==0 || ndim.height==0)
	    {
	      ConceptMap.BoundingBox bb=
		  new ConceptMap.BoundingBox(mapEvent.mouseEvent.getX(),
					     mapEvent.mouseEvent.getY(),
					     50,20);
	      mapObject.getNeuronStyle().setBoundingBox(bb);
	    }
	}
    }
}

