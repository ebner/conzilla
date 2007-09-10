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

package se.kth.cid.conzilla.data;
import se.kth.cid.component.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.*;
import java.util.*;


public class DataDisplayerComponent extends JPanel implements DataDisplayer
{
  Neuron neuron;
  
  JPopupMenu addDel;
  JMenu tagAlt;
  
  int popupRow = -1;

  EditListener editListener;
  boolean ignoreEdits = false;

  boolean isEditable = false;  
  
  class DataComponentFactory implements GridTableComponentFactory
  {
    public DataComponentFactory() {}
    
    public JComponent getComponentFor(final int row, final int column, Object o)
      {
      String str = (String) o;

      final GridTableModel model = table.getModel();

      if(column == 0)
	{
	  final JTextArea area = new JTextArea(str);
	  area.setEditable(false);
	  area.setBorder(makeBorder());

	  area.addMouseListener(new MouseAdapter()
	    {
	      public void mousePressed(MouseEvent e)
	      {
		if(e.isPopupTrigger() && isEditable)
		  {
		    popupRow = row;
		    addDel.show(area, e.getX(), e.getY());
		    e.consume();
		  }
	      }
	    });
	  return area;
	}
      else
	{
	  final JTextArea area = new JTextArea(str);
	  
	  area.setBorder(makeBorder());
	  area.setEditable(isEditable);
	  area.addFocusListener(new FocusAdapter() {	  
	      public void focusLost(FocusEvent e)
	      {
		model.setValueAt(row, column, area.getText());
	      }
	    });
	  area.addMouseListener(new MouseAdapter()
	    {
	      public void mousePressed(MouseEvent e)
	      {
		if(!isEditable)
		  {
		    String str0=(String) DataDisplayerComponent.this.table.getModel().getValueAt(row,0);
		    if (str0.indexOf("URI")!=-1)
		      {
			Tracer.debug("an URI was found!!!!!");
		      }
		    else if (str0.indexOf("URL")!=-1)
		      {
			Tracer.debug("an URL was found!!!!!");
		      }
		    else
		      {
		      }
		    e.consume();
		  }
	      }
	      });

	  return area;
	}
    }
    
    Border makeBorder()
    {
      return BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
						BorderFactory.createEmptyBorder(2,2,2,2));
    }
  }
  
  class DataTableModel implements GridTableModel
  {    
    Vector tags;
    
    public DataTableModel()
      {
      tags=new Vector();
      String [] ttags = neuron.getDataTags();
      for (int i=0;i<ttags.length;i++)
	{
	  String [] values=neuron.getDataValues(ttags[i]);
	  for (int j=0; j<values.length;j++)
	    tags.addElement(ttags[i]);
	}
    }

    public int getColumnCount()
    {
      return 2;
    }

    public int getRowCount()
    {
      return tags.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex)
      {
      if(columnIndex == 0)
	return tags.elementAt(rowIndex);
      else
	{
	  String[] strs=neuron.getDataValues((String) tags.elementAt(rowIndex));
	  int tagnr=0;
	  if (strs!=new String[0])
	    {
	      while (rowIndex-tagnr>0 && ((String) tags.elementAt(rowIndex-tagnr-1)).equals(tags.elementAt(rowIndex)))
		tagnr++;
	      return strs[tagnr];
	    }
	  else
	    return null;
	}
    }
    
    public void setValueAt(int rowIndex, int columnIndex, Object o)
    {
      if(!isEditable)
	return;
      
      if(columnIndex == 0)
	return;
      else
	{
	  ignoreEdits = true;
	  neuron.removeDataValue((String) tags.elementAt(rowIndex), (String) getValueAt(rowIndex, 1));
	  neuron.addDataValue((String) tags.elementAt(rowIndex), (String) o);
	  ignoreEdits = false;
	}
    }
  }
  
  GridTable table;

  public DataDisplayerComponent()
  {
    super();
    neuron=null;
    setLayout(new BorderLayout());

    addDel = new JPopupMenu();
    tagAlt = new JMenu("New tag...");
    
    addDel.add(tagAlt);
    
    addDel.add(new AbstractAction("Remove this tag")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Remove");
	  if(popupRow != -1)
	    removeRow(popupRow);
	  popupRow = -1;
	}
      });

    table = new GridTable();
    table.setFactory(new DataComponentFactory());
    add(table, BorderLayout.CENTER);

    editListener = new EditListener()
      {
	public void componentEdited(EditEvent e)
	{
	  if(ignoreEdits)
	    return;
	  switch(e.getEditType())
	    {
	    case Component.EDITABLE_CHANGED:	      
	      updateEditable();
	      break;
	    case Neuron.DATAVALUE_ADDED:
	    case Neuron.DATAVALUE_REMOVED:
	      updatedData();
	      break;
	    default:
	    }
	}
      };
  }

  public void updateEditable()
  {
    if(neuron != null)
      {
	isEditable = neuron.isEditable();
    
	for(int i = 0; i < table.getModel().getRowCount(); i++)
	  {
	    JTextArea area = (JTextArea) table.getComponentAtCell(i, 1);
	    area.setEditable(isEditable);
	  }
      }
  }
  
  public void showData(Neuron neuron, NeuronType neuronType)
  {
    checkDataTags(neuronType);
    
    if(this.neuron != null)
      this.neuron.removeEditListener(editListener);
    
    this.neuron = neuron;
    
    if(neuron != null)
      {
	isEditable = neuron.isEditable();
	neuron.addEditListener(editListener);
	table.setModel(new DataTableModel());
      }
    else
      {
	table.setModel(null);
	isEditable = false;
      }

    table.revalidate();
    repaint();
  }
  
  void checkDataTags(NeuronType neuronType)
    {
      tagAlt.removeAll();
      if (neuronType==null)
	return;
      String [] strs=neuronType.getDataTags();
      
      for (int i=0; i<strs.length;i++)
	{
	  final String name=strs[i];
	  tagAlt.add(new AbstractAction(name)
		   {
		     public void actionPerformed(ActionEvent ae)
		       {
			 addTag(name);
			 popupRow = -1;
		       }
		   });
	}
    }

  void updatedData()
    {
      table.setModel(new DataTableModel());
      table.revalidate();
      repaint();
    }

  void addTag(String tag)
  {
    Tracer.debug("Insert tag "+ tag);
    neuron.addDataValue(tag, "");
  }

  void removeRow(int index)
  {
    //This is a workaround for the problem where the value-JTextArea
    //being removed has the keyboard focus.
    //Since it has the focus a reference is kept in the focusmanager,
    //hence it won't be removen by the garbage manager, hence
    //it will try to save itself (in the old wrong location) when
    // it loses focus. For example when the mouse exits the window.
    JTextArea area=(JTextArea) table.getComponentAtCell(index, 1);
    if (area.hasFocus())
	((JTextArea) table.getComponentAtCell(index, 0)).requestFocus();
    //End ugly workaround.
    
    neuron.removeDataValue((String) table.getModel().getValueAt(index, 0),
			   (String) table.getModel().getValueAt(index, 1));
  }
  
}
