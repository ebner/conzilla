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
  boolean editable;
  JButton addBut;
  
  Vector menus;
  
  GridTable table;

  ListGridTableModel model;

  MouseListener listener;

  int colShift = 0;

  public interface ListGridTableModel extends GridTableModel
  {
    public String getTitle(int col);
  }
  
  class ComponentFactory implements GridTableComponentFactory
  {
    
    public ComponentFactory()
      {
      }

    public JComponent getComponentFor(int row, int col, Object o)
      {
	return (JComponent) o;
      }
  }
  
  class ListGridModel implements GridTableModel
  {
    public ListGridModel()
      {
      }
    
    public int getRowCount()
      {
	return model.getRowCount() + 1;
      }

    public int getColumnCount()
      {
	return model.getColumnCount() + colShift;
      }

    public Object getValueAt(int row, int col)
      {
	col = col - colShift;
	
	if(row == 0)
	  {
	    if(col == -1)
	      return new JPanel();
	    JTextField jf = new JTextField();
	    jf.setText(model.getTitle(col));
	    jf.setEditable(false);
	    jf.setForeground(Color.blue.darker());
	    return jf;
	  }
	
	    
	if(col == -1)
	  return menus.get(row - 1);

	return model.getValueAt(row - 1, col);
      }

    public void setValueAt(int row, int col, Object v)
      {
	col = col - colShift;
		
	if(row == 0)
	  return;
	
	if(col == -1)
	  menus.set(row - 1, v);

	model.setValueAt(row - 1, col, v);
      }
    
  }

  public AbstractListDisplayer(boolean editable)
    {
      this.editable = editable;
      
      if(editable)
	colShift = 1;

      setLayout(new BorderLayout());
      menus = new Vector();
      
      table = new GridTable();
      
      table.setFactory(new ComponentFactory());
      
      add(table, BorderLayout.CENTER);

      addBut = new JButton("Add item");
      addBut.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      createItem(0);
	    }
	});
    }

  protected void setModel(ListGridTableModel model)
    {
      if(this.model != null)
	throw new IllegalStateException("Model may be set only once");

      this.model = model;

      if(editable)
	for(int i = 0; i < model.getRowCount(); i++)
	  addMenu(i);

      table.setModel(new ListGridModel());

      updateModel();
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
      
    }

  protected abstract void removeItemImpl(int index);
  
  void removeItem(int index)
    {
      removeItemImpl(index);

      if(editable)
	menus.remove(index);

      updateModel();
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
      
      final JMenu menu = new JMenu("Edit");
      final JMenuBar mBar = new JMenuBar();
      mBar.add(menu);
      menu.setBorder(BorderFactory.createEtchedBorder());
      mBar.setBorder(null);
      
      menu.add(new AbstractAction("Insert item before") {
	  public void actionPerformed(ActionEvent e)
	    {
	      int index = menus.indexOf(mBar);
	      createItem(index);
	    }
	});

      menu.add(new AbstractAction("Insert item after") {
	  public void actionPerformed(ActionEvent e)
	    {
	      int index = menus.indexOf(mBar);
	      createItem(index + 1);
	    }
	});
      menu.add(new AbstractAction("Remove item") {
	  public void actionPerformed(ActionEvent e)
	    {
	      int index = menus.indexOf(mBar);
	      removeItem(index);
	    }
	});
      
      menus.add(ind, mBar);  
    }
}


