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
import java.util.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class DataDrawer extends GridTable
{
  DataModel data;
  NeuronMapObject neuronMapObject;

  Color color;
  double scale;
  
  public DataDrawer(NeuronMapObject neuronMapObject)
    {
      this.neuronMapObject = neuronMapObject;

      scale = neuronMapObject.getDisplayer().getScale();

      color = Color.black;
      
      data      = new DataModel();

      data.update();
      
      setOpaque(false);
      setFactory(data);
      setModel(data);

      setColor(neuronMapObject.getMark());
      setScale(scale);
    }

  void setColor(Mark mark)
    {
	Color c = mark.foregroundColor;
      for(int i = 0; i < data.getRowCount(); i++)
	((MapTextArea) getComponentAtCell(i, 0)).setColor(c);
      /*      for(int i = 0; i < data.getRowCount(); i++)
	((MapTextArea) getComponentAtCell(i, 1)).setColor(c);
      */
      this.color = c;

      updateBorder();
      
      repaint();
    }

  void updateBorder()
    {
      setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder((int)Math.round(2*scale), 0, 0, 0),
      						   BorderFactory.createMatteBorder((int)Math.round(scale), -1, -1, -1, color)));
    }
  
      
      
  public void setScale(double scale)
    {
      for(int i = 0; i < data.getRowCount(); i++)
	((MapTextArea) getComponentAtCell(i, 0)).setScale(scale);
      /*      for(int i = 0; i < data.getRowCount(); i++)
	      ((MapTextArea) getComponentAtCell(i, 1)).setScale(scale);*/
      this.scale = scale;
      updateBorder();
      revalidate();
      repaint();
    }

  public void updateData()
    {
      data.update();
      update();

      setColor(neuronMapObject.getMark());
      setScale(scale);
      
      revalidate();
      repaint();
    }
  
  public boolean didHit(MapEvent m)
    {
	Point p=SwingUtilities.convertPoint((java.awt.Component) m.mouseEvent.getSource(),m.mouseEvent.getX(), m.mouseEvent.getY(),this); 
	return contains(p.x, p.y);
    }

  class DataModel implements GridTableModel, GridTableComponentFactory
  {
    Vector tagnames;
    Vector tagvalues;

    GridTableComponentFactory.Constraints defaultConstraints;

    
    public DataModel()
      {
	tagnames             = new Vector();
	tagvalues            = new Vector();
	defaultConstraints = new GridTableComponentFactory.Constraints();
	defaultConstraints.fill   = GridBagConstraints.NONE;
	defaultConstraints.anchor = GridBagConstraints.WEST;
	update();
      }
    
    /** Fixes the tags and values from source.
     */
    public void update()
      {
	tagnames  = new Vector();
	tagvalues = new Vector();
	
	String[] tags = neuronMapObject.getNeuronStyle().getDataTags();

	if(neuronMapObject.getNeuron() == null)
	  return;
	
	Neuron.DataValue values[] = neuronMapObject.getNeuron().getDataValues();
	for (int i = 0; i < tags.length; i++)
	  {
	    boolean gotOne = false;
	    for (int j = 0; j < values.length; j++)
	      {
		if(values[j].tag.equals(tags[i]))
		  {
		    if(!gotOne)
		      {
			tagnames.add(tags[i] + ": ");
			gotOne = true;
		      }
		    else
		      tagnames.add("");
		    tagvalues.add(values[j].value);
		  }
	      }
	  }

      }
    
    public int getRowCount()
      {
	return tagvalues.size();
      }

    public int getColumnCount()
      {
	  //	return 2;
	  return 1;
      }

    public Object getValueAt(int rowIndex, int columnIndex)
      {
	  /*	if (columnIndex == 0)
	  return (String) tagnames.get(rowIndex);
	  else */
	  return (String) tagvalues.get(rowIndex);
      }

    public JComponent getComponentFor(final int rowIndex, final int columnIndex, Object o)
      {
	String str = (String) o;
	final MapTextArea area = new MapTextArea(0.7);
	area.setText(str);
	area.setEditable(false);
	return area;
      }
    
    public GridTableComponentFactory.Constraints getConstraintsFor(int rowIndex, int columnIndex)
      {
	  /*	if(columnIndex == 0)
	  defaultConstraints.weightx = 0.0;
	  else*/
	  defaultConstraints.weightx = 1.0;
	return defaultConstraints;
      }
    
  }
}

