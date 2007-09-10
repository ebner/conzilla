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
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public abstract class AbstractListDisplayer extends JPanel
{
  protected boolean editable;
  JButton addBut;

  protected boolean edited = false;
  
  Vector menus;
  
  GridTable table;

  ListGridTableModel model;

  MouseListener listener;

  int rowShift = 0;
  int colShift = 0;

  protected String metaDataField;
  protected MetaDataEditListener editListener;

  public abstract class ListGridTableModel implements GridTableModel
  {
    protected GridTableComponentFactory.Constraints defaultConstraints;

    public ListGridTableModel()
      {
	defaultConstraints = new GridTableComponentFactory.Constraints();
	defaultConstraints.fill = GridBagConstraints.NONE;
	defaultConstraints.anchor = GridBagConstraints.WEST;
      }
    
    public String getTitle(int col)
      {
	return null;
      }

    public GridTableComponentFactory.Constraints getConstraintsFor(int row, int col)
      {
	return defaultConstraints;
      }
  }
  
  final static GridTableComponentFactory.Constraints contraints
     = new GridTableComponentFactory.Constraints();

  class ComponentFactory implements GridTableComponentFactory
  {
    
    public ComponentFactory()
      {
      }

    public JComponent getComponentFor(int row, int col, Object o)
      {
	return (JComponent) o;
      }
    
    public GridTableComponentFactory.Constraints getConstraintsFor(int row, int col)
      {
	col = col - colShift;
	row = row - rowShift;

	if(row == -1 || col == -1)
	  return contraints;
	    
	return model.getConstraintsFor(row, col);
      }
  }
  
  class ListGridModel implements GridTableModel
  {
    public ListGridModel()
      {
      }
    
    public int getRowCount()
      {
	return model.getRowCount() + rowShift;
      }

    public int getColumnCount()
      {
	return model.getColumnCount() + colShift;
      }

    public Object getValueAt(int row, int col)
      {
	col = col - colShift;
	row = row - rowShift;
	
	if(row == -1)
	  {
	    if(col == -1)
	      return new JPanel();

	    return new LabelField(model.getTitle(col));
	  }
	
	    
	if(col == -1)
	  return menus.get(row);

	return model.getValueAt(row, col);
      }

  }

  public AbstractListDisplayer(boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      this.editable = editable;
      this.metaDataField = metaDataField;
      this.editListener = editListener;
      
      if(editable)
	colShift = 1;

      setLayout(new BorderLayout());
      menus = new Vector();
      
      table = new GridTable();
      
      table.setFactory(new ComponentFactory());
      
      addBut = new JButton("Add");
      addBut.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      createItem(0);
	    }
	});
      addBut.setMargin(new Insets(1, 5, 1, 5));
    }

  protected void setModel(ListGridTableModel model)
    {
      if(this.model != null)
	throw new IllegalStateException("Model may be set only once");

      this.model = model;

      if(model.getTitle(0) != null)
	rowShift = 1;
      
      if(editable)
	for(int i = 0; i < model.getRowCount(); i++)
	  addMenu(i);

      table.setModel(new ListGridModel());

      if(model.getRowCount() == 0)
	{
	  setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	  if(editable)
	    add(addBut);
	}
      else
	{
	  setLayout(new BorderLayout());
	  add(table, BorderLayout.CENTER);
	}

      revalidate();
      repaint();
    }

  void updateModel()
    {
      if(model.getRowCount() == 0)
	{
	  remove(table);
	  setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	  if(editable)
	    add(addBut);
	}
      else
	{
	  remove(addBut);
	  setLayout(new BorderLayout());
	  add(table, BorderLayout.CENTER);
	  table.update();
	}
      revalidate();
      repaint();

      fireEdited();
    }

  protected void fireEdited()
    {
      edited = true;
      if(editListener != null)
	editListener.fieldEdited(new MetaDataEditEvent(null, metaDataField));
    }

  protected abstract boolean isItemEdited();

  public boolean isEdited()
    {
      return edited || isItemEdited();
    }
  
  protected abstract void removeItemImpl(int index);
  
  void removeItem(int index)
    {
      removeItemImpl(index);

      if(editable)
	menus.remove(index);

      updateModel();
    }

  public void detach()
    {
      for(int i = model.getRowCount() - 1; i >= 0; i--)
	removeItemImpl(i);
    }
  
  protected void removeAndDetach(Vector v, int index)
    {
      ((MetaDataFieldEditor) v.get(index)).detach();
      v.remove(index);
    }
  
  
  protected abstract void createItemImpl(int index);
      
  void createItem(int index)
    {
      createItemImpl(index);
      addMenu(index);
      updateModel();
    }


  protected void addMenu(int ind)
    {
      if(!editable)
	return;
      
      final JMenu menu = new JMenu("\261"); // +/-
      final JMenuBar mBar = new JMenuBar();
      mBar.add(menu);
      menu.setBorder(BorderFactory.createEtchedBorder());
      mBar.setBorder(null);
      
      menu.add(new AbstractAction("Insert new item before") {
	  public void actionPerformed(ActionEvent e)
	    {
	      int index = menus.indexOf(mBar);
	      createItem(index);
	    }
	});

      menu.add(new AbstractAction("Insert new item after") {
	  public void actionPerformed(ActionEvent e)
	    {
	      int index = menus.indexOf(mBar);
	      createItem(index + 1);
	    }
	});
      menu.add(new AbstractAction("Remove this item") {
	  public void actionPerformed(ActionEvent e)
	    {
	      int index = menus.indexOf(mBar);
	      removeItem(index);
	    }
	});
      
      menus.add(ind, mBar);  
    }
}


