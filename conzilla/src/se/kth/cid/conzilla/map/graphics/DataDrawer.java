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
package se.kth.cid.conzilla.map.graphics;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class DataDrawer //implements java.awt.event.FocusListener
{
  DataModel data;
  GridTable gridtable;
  JScrollPane jsp;
  Rectangle bb;
  NeuronMapObject neuronMapObject;
  
  public DataDrawer(NeuronMapObject neuronMapObject,
		    MapDisplayer.MapComponentDrawer parent)
    {
      this.neuronMapObject = neuronMapObject;
      data      = new DataModel(neuronMapObject, this);
      gridtable = new GridTable();
      gridtable.setVisible(true);
      gridtable.setFactory(new DataComponentFactory(gridtable));
      jsp = new JScrollPane(gridtable, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      parent.add(jsp);
      jsp.getHorizontalScrollBar().setUnitIncrement(10);
      
      gridtable.setModel(data);
      bb = null;
    }

  public void detach()
  {
    data.detach();
    data = null;
    gridtable = null;
  }
  
  public void update(Rectangle re)
    {
      if(re != null)
	{
	  bb = re;
	  ((DataComponentFactory) gridtable.getFactory()).setTextColor(Color.black);
	  reshape();
	  jsp.setVisible(true);
	}
      else
	{
	  jsp.setVisible(false);
	  bb = null;
	}
      jsp.getParent().validate();
      jsp.repaint();
      gridtable.repaint();
    }
  


  private void reshape()
    {
      data.update();
      gridtable.setModel(data);
      jsp.setSize(bb.width,gridtable.getPreferredSize().height);
      jsp.setLocation(bb.x, bb.y);
    }
  
  public boolean checkHit(MapEvent m)
    {
      return jsp.contains(m.mouseEvent.getX(), m.mouseEvent.getY());
    }  
}
