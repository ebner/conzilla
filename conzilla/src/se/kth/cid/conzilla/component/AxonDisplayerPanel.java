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

package se.kth.cid.conzilla.component;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class AxonDisplayerPanel extends AbstractListDisplayer
{
  Vector axons;

  ItemListener listener;

  Neuron neuron;
  NeuronType type;
  
  class AxonEditor 
  {
      private Axon axon;
      private AxonType axonType;
      private String uri;
      JComboBox tag;
      
      public AxonEditor(Axon axon, AxonType axonType)
      {
	  this.axon=axon;
	  this.axonType=axonType;
	  
	  tag = new JComboBox();
	  
	  if (axon != null)
	      {		  
		  uri=axon.objectURI();
		  int found = -1;
		  AxonType [] allowedTypes = type.getAxonTypes();
		  if(allowedTypes != null)
		      {
			  String type = axonType.getType();
			  if( type == null)
			      found = 0;
			  
			  for(int i = 0; i < allowedTypes.length; i++)
			      {
				  if(allowedTypes[i].getType().equals(type)) 
				      found = i;
				  tag.addItem(allowedTypes[i].getType());
			      }
		      }
		  
		  if(found == -1)
		      {
			  IllegalString str = new IllegalString(axonType.getType());
			  tag.addItem(str);
			  tag.setSelectedItem(str);
		      }
		  else
		      tag.setSelectedIndex(found);		  
	      }
	  else if (type!=null)
	      {
		  uri="";
		  AxonType [] allowedTypes = type.getAxonTypes();
		  if(allowedTypes != null)
		      for(int i = 0; i < allowedTypes.length; i++)
			  tag.addItem(allowedTypes[i].getType());
	      }

	  tag.setRenderer(new IllegalStringRenderer());
	  
	  tag.setBackground(Color.white);
	  
	  if(tag.getSelectedItem() instanceof IllegalString)
	      tag.setForeground(Color.red);
	  
	  tag.addItemListener(listener);
      }
      
      public String getAxonURI()
      {
	  return uri;
      }
      
      public Object getType()
      {
	  return tag;
      }
  }

  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return axons.size();
      }

    public int getColumnCount()
      {
	return 2;
      }

    public Object getValueAt(int row, int col)
      {
	if(col == 0)
	  return ((AxonEditor) axons.get(row)).getType();

	return new JTextField(((AxonEditor) axons.get(row)).getAxonURI());
      }

    public String getTitle(int col)
      {
	if(col == 0)
	  return "Axontype";

	return "EndURI";
      }  
  }
  class IllegalString
  {
    String s;
    public IllegalString(String s)
      {
	this.s = s;
      }
    public String getString()
      {
	return s;
      }

    public boolean equals(Object o)
      {
	if(o instanceof String)
	  return s.equals(o);
	else return super.equals(o);
      }
  }
  class IllegalStringRenderer extends StringRenderer
  {
    public IllegalStringRenderer()
      {
      }
  
    public Component getListCellRendererComponent(JList list,
						  Object value,
						  int index,
						  boolean isSelected,
						  boolean cellHasFocus)
      {
	if(value instanceof String)
	  return super.getListCellRendererComponent(list, value, index,
						    isSelected, cellHasFocus);
	
	setText(((IllegalString) value).getString());
	
	if (isSelected) {
	  this.setBackground(list.getSelectionBackground());
	  this.setForeground(Color.red);
	} else {
	  this.setBackground(Color.white);
	  this.setForeground(Color.red);
	}
	
	return this;
      }
  }


  
  public AxonDisplayerPanel(Neuron neuron, NeuronType type, boolean editable,
			    MetaDataEditListener editListener,
			    String metaDataField)
    {
      super(editable, editListener, metaDataField);

      this.neuron = neuron;
      this.type = type;
      
      listener = new ItemListener() {
	      public void itemStateChanged(ItemEvent e)
	      {
		  if(e.getStateChange() == ItemEvent.SELECTED)
		      fireEdited();
		  JComboBox src = (JComboBox) e.getSource();
		  if(src.getSelectedItem() instanceof IllegalString)
		      src.setForeground(Color.red);
		  else
		      src.setForeground(Color.black);
	      }
	  };
  
      axons   = new Vector();

      Axon [] ax = neuron.getAxons();
      
      for(int i = 0; i < ax.length; i++)
	addItem(ax[i], i);
      
      setModel(new GridModel());      
    }
  
    /*  public Neuron.DataValue[] getDataValues(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      Neuron.DataValue[] vals
        = new Neuron.DataValue[tags.size()];
      
      for(int i = 0; i < vals.length; i++)
	{
	  String tag;
	  if(editable)
	    tag = (String) ((JComboBox) tags.get(i)).getSelectedItem();
	  else
	    tag = ((StringPanel) tags.get(i)).getString(resetEdited, false);
	  
	  vals[i] = new Neuron.DataValue(tag, ((StringPanel) values.get(i)).getString(resetEdited, false));
	}
      
      return vals;
      }*/

  protected boolean isItemEdited()
    {
	/*      for(int i = 0; i < .size(); i++)
	{
	  if(((StringPanel) values.get(i)).isEdited())
	    return true;
	    }*/
      return false;
    }

  protected void removeItemImpl(int index)
    {
	if(editable)
	    axons.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
	addItem(null, index);
    }
  
  void addItem(Axon axon, int index)
    {
	if (editable && axon !=null)
	    {
		AxonType at=type.getAxonType(axon.predicateURI());
		if (at!=null)
		    axons.add(index, new AxonEditor(axon, at));		
	    }
	else
	    axons.add(index, new AxonEditor(null, null));
    }   
}


