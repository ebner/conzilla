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

package se.kth.cid.conzilla.metadata;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.*;


public class MetaDataDisplayerComponent extends JPanel implements MetaDataDisplayer
{
  Component component;

  JPopupMenu addDel;

  int popupRow = -1;

  EditListener editListener;
  boolean ignoreEdits = false;

  boolean isEditable = false;
  
  class MetaDataComponentFactory implements GridTableComponentFactory
  {
    public MetaDataComponentFactory()
    {
    }
    
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
		    String str0=(String) MetaDataDisplayerComponent.this.table.getModel().getValueAt(row,0);
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
  
  class MetaDataTableModel implements GridTableModel
  {    
    String[] tags;
    
    public MetaDataTableModel()
    {
      tags = component.getMetaData().getTags();
    }

    public int getColumnCount()
    {
      return 2;
    }

    public int getRowCount()
    {
      return tags.length;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex)
    {
      if(columnIndex == 0)
	return tags[rowIndex];
      else
	return component.getMetaData().getValue(tags[rowIndex]);
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
	  component.getMetaData().setValue(tags[rowIndex], (String) o);
	  ignoreEdits = false;
	}
    }
  }
  
  GridTable table;

  public MetaDataDisplayerComponent()
  {
    super();
    setLayout(new BorderLayout());

    addDel = new JPopupMenu();
    
    addDel.add(new AbstractAction("New tag...")
      {
	public void actionPerformed(ActionEvent ae)
	{
	  Tracer.debug("Insert");
	  addTag();
	  popupRow = -1;
	}
      });
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
    table.setFactory(new MetaDataComponentFactory());
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
	    case Component.METADATATAG_EDITED:
	    case Component.METADATATAG_ADDED:
	    case Component.METADATATAG_REMOVED:
	      updatedMetaData();
	      break;
	    default:
	    }
	}
      };
  }

  public void updateEditable()
  {
    if(component != null)
      {
	isEditable = component.isEditable();
    
	for(int i = 0; i < table.getModel().getRowCount(); i++)
	  {
	    JTextArea area = (JTextArea) table.getComponentAtCell(i, 1);
	    area.setEditable(isEditable);
	  }
      }
  }
  
  public void showMetaData(Component comp)
  {
    if(component != null)
      component.removeEditListener(editListener);
    
    component = comp;

    if(component != null)
      {
	isEditable = component.isEditable();
	component.addEditListener(editListener);
	table.setModel(new MetaDataTableModel());
      }
    else
      {
	table.setModel(null);
	isEditable = false;
      }

    table.revalidate();
    repaint();
  }

  void updatedMetaData()
  {
    table.setModel(new MetaDataTableModel());
    table.revalidate();
    repaint();
  }

  void addTag()
  {
    String tag = JOptionPane.showInputDialog(this, "Enter new tag name: ",
					     "New tag",
					     JOptionPane.QUESTION_MESSAGE);
    if(tag != null)
      {
	if(component.getMetaData().getValue(tag) != null)
	  {
	    TextOptionPane.showError(this, "The tag \"" + tag + "\" already exists!");
	  }
	else
	  {
	    component.getMetaData().setValue(tag, "");
	    updatedMetaData();
	  }
      }
  }

  void removeRow(int index)
  {
    String[] tags = component.getMetaData().getTags();
    table.setModel(null);
    component.getMetaData().setValue(tags[index], null);
  }
  
}
