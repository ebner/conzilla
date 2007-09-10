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
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class MyTableModel extends AbstractTableModel
{
  NeuronStyle neuronstyle;
  Hashtable nr2name;
  Hashtable nr2value;
  boolean editable;
  
  public MyTableModel(NeuronStyle neuronstyle)
  {
    this.neuronstyle=neuronstyle;
    editable=false;
    fix();
  }

  public void detach()
  {
    neuronstyle=null;
    nr2value=null;
    nr2value=null;
  }
  
  public void setEditable(boolean editable)
  {
    this.editable=editable;
  }
  
  public void fix()
  {
    nr2name=new Hashtable();
    nr2value=new Hashtable();
    Enumeration en=neuronstyle.getDataTags().elements();
    for (int i=0;en.hasMoreElements();i++)
      {
	String name=(String) en.nextElement();
	String values[]=neuronstyle.getNeuron().getDataValues(name);
	for (int j=0;j<values.length;j++)
	  {
	    nr2name.put(new Integer(i),name);
	    nr2value.put(new Integer(i),values[j]);
	    name="";
	    i++;
	  }
      }
  }
  public int getRowCount()
  {
    return nr2name.size();
  }
  public int getColumnCount()
  {
    return 2;
  }
  public String getColumnName(int columnIndex)
  {
    return "";
  }
  public Class getColumnClass(int columnIndex)
  {
    try {
      return Class.forName("java.lang.String");      
    } catch (ClassNotFoundException e) {
      return null;
    }  
  }
  
  public boolean isCellEditable(int rowIndex,
				int columnIndex)
  {
    if (columnIndex==0)
      return false;
    else
      {
	if (rowIndex>=0 && rowIndex<getRowCount())
	  return editable;
	else
	  return false;
      }
  }
  public Object getValueAt(int rowIndex,
			   int columnIndex)
  {
    String name=(String) nr2name.get(new Integer(rowIndex));
    if (columnIndex==0)
      return name;
    else
      return (String) nr2value.get(new Integer(rowIndex));
  }
    
  public void setValueAt(Object aValue,
			 int rowIndex,
			 int columnIndex)
  {
    if (editable)
      {
	// a search is necessary while all but the first occurence of the tag (name)
	// is noted as a string of lenght 0 in the hashtable.
	for (int i=rowIndex; i>=0 ; i--)
	  if (!((String) nr2name.get(new Integer(i))).equals(""))
	    {
	      neuronstyle.getNeuron().removeDataValue(((String) nr2name.get(new Integer(i))),
						      (String) nr2value.get(new Integer(rowIndex)));
	      neuronstyle.getNeuron().addDataValue(((String) nr2name.get(new Integer(i))),
						   (String) aValue);
	      //Could do fix() instead of theese two, but then the order could be different.
	      //Other field using the data from the neuron can be updated different though.
	      nr2value.remove(new Integer(rowIndex));
	      nr2value.put(new Integer(rowIndex),aValue);
	    }
      }
  }
  
  public void addTableModelListener(TableModelListener l) {}
  public void removeTableModelListener(TableModelListener l) {}
  /*  public void editingCanceled(ChangeEvent e) {}
  public void editingStopped(ChangeEvent e)
  {
    DataTable.MyCellEditor mce=(DataTable.MyCellEditor) e.getSource();
    setValueAt(mce.getCellEditorValue(),mce.rowIndex,mce.columnIndex);
    } */
}
