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
  boolean misfit;        //misfit is true if neuron or neurontype with data is missing.
  boolean editing;         //Are this instance editing?
  Boolean oldeditable;        //The edit-state of the neuron before
                               //this instance starts editing.
  Rectangle bb;
  
  public DataDrawer(final NeuronStyle neuronstyle, final MapDisplayer.MapComponentDrawer parent)
    {
      misfit= neuronstyle.getNeuronType()==null;
      data=new DataModel(neuronstyle, this);
      gridtable=new GridTable();
      gridtable.setVisible(true);
      gridtable.setFactory(new DataComponentFactory(gridtable));
      jsp=new JScrollPane(gridtable,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      if (!misfit)            //guarantees that nothing is drawn if misfit.
	parent.add(jsp);
      jsp.getHorizontalScrollBar().setUnitIncrement(10);
      
      gridtable.setModel(data);
      bb=null;
    }

  public void detach()
  {
    data.detach();
    data=null;
    gridtable=null;
  }
  
  public boolean setDataValuesEditable(boolean editable, ComponentSaver csaver)
    {
      if (!misfit)
	{
	  //	  System.out.println("DATADRAWER, not misfit setDataValuesEditable ");
	  data.setEditable(editable,csaver);
	  return data.check();
	}
      //      System.out.println("DATADRAWER, misfit setDataValuesEditable ");
      return false;
    }

  public void enable(Neuron neuron, boolean enable)
    {
      //      System.out.println("DATADRAWER, enable fkn. ");
      if (enable)
	{
	  //	  System.out.println("DATADRAWER, focus is on. ");
	  oldeditable=new Boolean(neuron.isEditable());
	  editing=true;
	  neuron.setEditable(true);
	  enableGridEdit(true);
	  jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
      else
	{
	  //	  System.out.println("DATADRAWER, disable fkn. ");
	  editing=false;
	  if (oldeditable!=null)
	    {
	      neuron.setEditable(oldeditable.booleanValue());
	      oldeditable=null;
	    }
	  enableGridEdit(false);
	  jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
      if (bb!=null)
	reshape();
      jsp.getParent().validate();
      jsp.repaint();
    }
  
  
  public void enableGridEdit(boolean edi)
    {
      //      System.out.println("DATADRAWER, enableGridEdit, "+edi);
      for(int i = 0; i < gridtable.getModel().getRowCount(); i++)
	{
	  JTextArea area = (JTextArea) gridtable.getComponentAtCell(i, 1);
	  area.setEditable(edi);
	}
    }
  
  public void paint(Graphics g) {}
  public void fixFromNeuronStyle(Rectangle re, NeuronStyle neuronstyle)
    {
      misfit=neuronstyle.getNeuronType()==null;
      if (!misfit && re!=null)
	  {
	    bb=re;
	    //	    Tracer.debug("fixfromneuronstyle======="+neuronstyle.getURI().toString());
	    ((DataComponentFactory) gridtable.getFactory()).setTextColor(java.awt.Color.black);
	    reshape();
	    jsp.setVisible(true);
	  }
      else
	{
	  jsp.setVisible(false);
	  bb=null;                 //indicates that nothing should be visible...
	                           //Neccesary to know when automatic updating according to ediable or not.
	}
      jsp.getParent().validate();
      jsp.repaint();
      gridtable.repaint();
    }
  


  private void reshape()
    {
      //      Rectangle contentrect=jsp.getViewport().getViewRect();
      //      System.out.println(contentrect);
      data.fix();
      gridtable.setModel(data);
      enableGridEdit(editing);
      
      if (editing){
	Dimension gts=gridtable.getPreferredSize();
	//	System.out.println("RESHAPE, editing");
	if (gts.width>=bb.width)
	  jsp.setSize(bb.width,gts.height+jsp.getHorizontalScrollBar().getMinimumSize().height);
	else if (gts.height>bb.height)
	  jsp.setSize(bb.width,gts.height);
	else
	  jsp.setSize(bb.width,bb.height);
      }
      else{
	//	System.out.println("RESHAPE, not editing");
	jsp.setSize(bb.width,gridtable.getPreferredSize().height);
      }
      jsp.setLocation(bb.x,bb.y);
      //      jsp.getViewport().scrollRectToVisible(contentrect);
    }
  
  public boolean didHit(MapEvent m)
    {
      if (!misfit)
	return jsp.contains(m.mouseevent.getX(),m.mouseevent.getY());
      return false;
    }  
}
