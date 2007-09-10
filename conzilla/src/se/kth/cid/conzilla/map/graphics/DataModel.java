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

public class DataModel implements GridTableModel, EditListener
{
  NeuronStyle neuronstyle;
  Hashtable nr2name;
  Hashtable nr2value;
  boolean editable;
  DataDrawer parent;
  ComponentSaver csaver;
  int size;
  
  public DataModel(NeuronStyle neuronstyle, DataDrawer parent)
    {
      this.neuronstyle=neuronstyle;
      //      fix();
      nr2name=new Hashtable();
      nr2value=new Hashtable();
      this.parent=parent;
      csaver=null;
      editable=false;
      size=0;
    }
  public void detach()
    {
      if (neuronstyle!=null)
	neuronstyle.getNeuron().removeEditListener(this);
      neuronstyle=null;
      nr2value=null;
      nr2name=null;
      parent=null;
      csaver=null;
    }
  /** Fixes the tags and values from source.
   */
  public void fix()
    {
      nr2name=new Hashtable();
      nr2value=new Hashtable();
      Enumeration en=neuronstyle.getDataTags().elements();
      size=0;
      for (int i=0;en.hasMoreElements();i++)
	{
	  String name=(String) en.nextElement();
	  String values[]=neuronstyle.getNeuron().getDataValues(name);
	  for (int j=0;j<values.length;j++)
	    {
	      nr2name.put(new Integer(size),name);
	      nr2value.put(new Integer(size),values[j]);
	      size++;
	    }
	}
    }
  public int getRowCount()
    {
      return size;
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
      String name=(String) nr2name.get(new Integer(rowIndex));
      if (columnIndex==0)
	return name;
      else
	return (String) nr2value.get(new Integer(rowIndex));
    }

  public void setValueAt(int rowIndex, int columnIndex, Object o)
    {
      if (columnIndex==0)
	return;
      
      //Because of callbacks we can't do a remove and add in peace,
      //(the hachtables nr2name and nr2value will be changed and rowIndex will be wrong).
      //The solution is to pick out the two strings from the tables in advance.
      String name=(String) nr2name.get(new Integer(rowIndex));
      String value=(String) nr2value.get(new Integer(rowIndex));  
      if(value.equals(o))
	return;
      neuronstyle.getNeuron().removeDataValue(name,value);
      neuronstyle.getNeuron().addDataValue(name,(String) o);
      
      //Could do fix() instead of theese two, but then the order could be different.
      //Other field using the data from the neuron can be updated different though.
      /*      nr2value.remove(new Integer(rowIndex));
	      nr2value.put(new Integer(rowIndex),(String) o);*/
    }

  public void setEditable(boolean editable, ComponentSaver csaver)
    {
      if (this.editable!=editable)
	{
	  if (editable==true){
	    this.csaver=csaver;
	    neuronstyle.getNeuron().addEditListener(this);
	  }
	  else {
	    neuronstyle.getNeuron().removeEditListener(this);
	    this.csaver=null;
	  }
	  this.editable=editable;
	}
    }
  
  /* Function demanded by EditListener.
   */
  public void componentEdited(EditEvent e)
    {
      if (e.getEditType()==se.kth.cid.component.Component.EDITABLE_CHANGED)
	check();
    }

  public boolean check()
    {
      //      System.out.println("DATAMODEL, check !! ");
      Neuron n=neuronstyle.getNeuron();
      if (editable && n.isEditingPossible() &&
	  csaver!=null && csaver.isComponentSavable(n))
	{
	  neuronstyle.getNeuron().removeEditListener(this);
	  parent.enable(n, true);
	  neuronstyle.getNeuron().addEditListener(this);
	  return true;
	}
      neuronstyle.getNeuron().removeEditListener(this);      
      parent.enable(n, false);
      neuronstyle.getNeuron().addEditListener(this);      
      return false;
    }
}
