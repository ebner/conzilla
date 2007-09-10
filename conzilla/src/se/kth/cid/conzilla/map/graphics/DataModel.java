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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class DataModel implements GridTableModel
{
  NeuronMapObject neuronMapObject;
  Vector tagnames;
  Vector tagvalues;

  DataDrawer parent;

  
  public DataModel(NeuronMapObject neuronMapObject, DataDrawer parent)
    {
      this.neuronMapObject = neuronMapObject;
      tagnames             = new Vector();
      tagvalues            = new Vector();
      this.parent          = parent;
    }
  
  public void detach()
    {
      neuronMapObject = null;
      tagvalues       = null;
      tagnames        = null;
      parent          = null;
    }

  /** Fixes the tags and values from source.
   */
  public void update()
    {
      tagnames  = new Vector();
      tagvalues = new Vector();
      
      String[] tags = neuronMapObject.getNeuronStyle().getDataTags();

      for (int i = 0; i < tags.length; i++)
	{
	  String values[] = neuronMapObject.getNeuron().getDataValues(tags[i]);
	  
	  for (int j = 0; j < values.length; j++)
	    {
	      tagnames.add(tags[i]);
	      tagvalues.add(values[j]);
	    }
	}
    }
  public int getRowCount()
    {
      return tagvalues.size();
    }
  public int getColumnCount()
    {
      return 2;
    }

  /** Fetches the current value from the source.
   * @param rowIndex the row index starts from 0.
   * @param columnIndex the column index, starts from 0.
   * @return an object typically a string.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
    {
      String name = (String) tagnames.get(rowIndex);
      if (columnIndex==0)
	return name;
      else
	return (String) tagvalues.get(rowIndex);
    }

  public void setValueAt(int rowIndex, int columnIndex, Object o)
    {}

}
