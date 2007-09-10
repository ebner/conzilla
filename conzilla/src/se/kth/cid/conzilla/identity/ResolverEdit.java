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

package se.kth.cid.conzilla.identity;


import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.identity.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import javax.swing.border.*;
import java.awt.event.*;


public class ResolverEdit extends JFrame
{
  JTabbedPane pane;

  JTable[] tables;

  int dragStartRow = -1;

  ResolverManager manager;

  ResolverTable[] resolverTables;
  
  public ResolverEdit(ResolverManager manager)
    {
      super("Resolver Edit");
      this.manager = manager;

      pane = new JTabbedPane();

      resolverTables = manager.getResolverTables();
      
      tables = new JTable[resolverTables.length];
      for(int i = 0; i < resolverTables.length; i++)
	{
	  addTable(i);
	}
      
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(pane, BorderLayout.CENTER);

      JToolBar toolBar = new JToolBar();
      toolBar.setFloatable(false);
      
      JButton close = new JButton("Close");
      close.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e)
	    {
	      tryClose();
	    }
	});
      toolBar.add(Box.createHorizontalGlue());
      toolBar.add(close);
      getContentPane().add(toolBar, BorderLayout.SOUTH);

      setSize(400, 300);
      setLocation(100, 100);

      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter()
	{
	  public void windowClosing(WindowEvent e)
	    {
	      tryClose();
	    }
	});
      
    }
  

  void addTable(final int i)
    {

      // Make table
      
      tables[i] = new JTable(new ResolverTableModel(resolverTables[i]));

      tables[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tables[i].getTableHeader().setReorderingAllowed(false);

      TableColumn typeColumn = tables[i].getColumnModel().getColumn(2);
      JComboBox comboBox = new JComboBox();
      for(int j = 0; j < ResolverTableModel.MIMETypes.length; j++)
	comboBox.addItem(ResolverTableModel.MIMETypes[j]);
      typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
      
      for (int k = 0; k < 3; k++)
	{
	  TableColumn column = tables[i].getColumnModel().getColumn(k);
	  if (k == 1)
	    column.setPreferredWidth(100);
	  else
	    column.setPreferredWidth(50);
	}

      if(resolverTables[i].isSavable())
	{
	  MouseInputAdapter mouseListener = new MouseInputAdapter()
	    {
	      public void mouseDragged(MouseEvent e)
		{
		  drag(e);
		}
	      
	      public void mousePressed(MouseEvent e)
		{
		  initDrag(e);
		}
	      public void mouseReleased(MouseEvent e)
		{
		  stopDrag(e);
		}
	    };
	  tables[i].addMouseMotionListener(mouseListener);
	  tables[i].addMouseListener(mouseListener);
	}
      // Make panel 

      JScrollPane sPane = new JScrollPane(tables[i]);
  
      JPanel panel = new JPanel();
      
      pane.addTab(resolverTables[i].getName(), panel);

      panel.setLayout(new BorderLayout());

      panel.add(sPane, BorderLayout.CENTER);
      

      // Add toolbar
      if(resolverTables[i].isSavable())
	{
	  JPanel toolBar = new JPanel();
	  toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
	  
	  JButton add = new JButton("Add");
	  add.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e)
		{
		  int row = tables[i].getSelectedRow();
		  if(row == -1)
		    row = tables[i].getRowCount();
		  else
		    row ++;
		  
		  ((ResolverTableModel)tables[i].getModel()).insert(row);
		}
	    });
	  final JButton remove = new JButton("Remove");
	  remove.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e)
		{
		  int row = tables[i].getSelectedRow();
		  if(row == -1)
		    return;
		  
		  if(tables[i].isEditing())
		    tables[i].removeEditor();
		  ((ResolverTableModel)tables[i].getModel()).remove(row);
		  if(row != tables[i].getRowCount())
		    tables[i].getSelectionModel().setSelectionInterval(row, row);
		  else
		    remove.setEnabled(false);
		}
	    });
	  remove.setEnabled(false);
	  tables[i].getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	      public void valueChanged(ListSelectionEvent e)
		{
		  if (e.getValueIsAdjusting())
		    return;
		  
		  ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		  
		  if (lsm.isSelectionEmpty())
		    {
		      remove.setEnabled(false);
		    }
		  else
		    {
		      int selectedRow = lsm.getMinSelectionIndex();
		      remove.setEnabled(true);
		    }
		}
	    });

	  	  
	  final JButton save = new JButton("Save");
	  save.setEnabled(false);
	  tables[i].putClientProperty("SaveButton", save);
	  save.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e)
		{
		  saveTable(i);
		}
	    });

	  final JButton restore = new JButton("Restore");
	  restore.setEnabled(false);
	  tables[i].putClientProperty("RestoreButton", restore);
	  restore.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e)
		{
		  restoreTable(i);
		}
	    });
	  tables[i].getModel().addTableModelListener(new TableModelListener()
	    {
	      public void tableChanged(TableModelEvent e)
		{
		  save.setEnabled(true);
		  restore.setEnabled(true);
		}
	    });
	  toolBar.add(add);
	  toolBar.add(remove);
	  toolBar.add(Box.createHorizontalGlue());
	  toolBar.add(restore);
	  toolBar.add(save);
	  panel.add(toolBar, BorderLayout.SOUTH);
	}
      else
	{
	  panel.add(new JLabel("Table not editable."), BorderLayout.SOUTH);
	}
      
      // Add location

      JPanel locPanel = new JPanel();
      locPanel.setLayout(new BorderLayout());
      locPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
      locPanel.add(new JLabel("Location:  "), BorderLayout.WEST);
      JTextField location = new JTextField(resolverTables[i].getLocation().toString());
      location.setEditable(false);
      location.setBorder(null);
      locPanel.add(location, BorderLayout.CENTER);

      panel.add(locPanel, BorderLayout.NORTH);
      
    }


  void tryClose()
    {
      for(int i = 0; i < tables.length; i++)
	{
	  ResolverTableModel model = (ResolverTableModel) tables[i].getModel();
	  if(model.isEdited())
	    {
	      Object[] options = {"Save now", "Don't save", "Cancel"};
	      int ret =
		JOptionPane.showOptionDialog(tables[i],
					     "The resolver table\n" +
					     "'" + resolverTables[i].getName() + "'\n" +
					     "has unsaved changes.", "Warning", 
					     JOptionPane.DEFAULT_OPTION,
					     JOptionPane.WARNING_MESSAGE,
					     null, options, options[0]);
	      switch(ret)
		{
		case 0:
		  if(!saveTable(i))
		    return;
		  break;
		case 1:
		  restoreTable(i);
		  break;
		default:
		  return;
		}
	    }
	}  
      setVisible(false);
    }

  boolean saveTable(int i)
    {
      try {
	CellEditor editor = tables[i].getCellEditor();
	if(editor != null)
	  editor.stopCellEditing();
	tables[i].removeEditor();
	resolverTables[i].setEntries(((ResolverTableModel) tables[i].getModel()).getEntries());
	resolverTables[i].saveTable();
	manager.updateResolver();
	((ResolverTableModel)tables[i].getModel()).setEdited(false);
	disableSaveButtons(i);
	return true;
      } catch(IOException e)
	{
	  ErrorMessage.showError("Save Error",
				 "Could not save table\n\n" + resolverTables[i].getLocation(),
				 e, tables[i]);
	}
      catch(ResolveException e)
	{
	  ErrorMessage.showError("Save Error",
				 "Could not save table\n\n" + resolverTables[i].getLocation(),
				 e, tables[i]);
	}
      catch(MalformedURIException e)
	{
	  ErrorMessage.showError("Save Error",
				 "Could not save table\n\n" + resolverTables[i].getLocation(),
				 e, tables[i]);
	}
      catch(MalformedMIMETypeException e)
	{
	  ErrorMessage.showError("Save Error",
				 "Could not save table\n\n" + resolverTables[i].getLocation(),
				 e, tables[i]);
	}
      return false;
    }


  void restoreTable(int i)
    {
      ((ResolverTableModel) tables[i].getModel()).reset();
      disableSaveButtons(i);
    }

  void disableSaveButtons(int i)
    {      	
      ((JButton)tables[i].getClientProperty("SaveButton")).setEnabled(false);
      ((JButton)tables[i].getClientProperty("RestoreButton")).setEnabled(false);
    }
  
  
  void initDrag(MouseEvent e)
    {
      dragStartRow =
	((JTable)e.getSource()).rowAtPoint(new Point(e.getX(), e.getY()));
    }

  void drag(MouseEvent e)
    {
      JTable table = ((JTable)e.getSource());

      int dragStopRow =
	table.rowAtPoint(new Point(e.getX(), e.getY()));
      
      if(dragStopRow != -1
	 && dragStartRow != -1
	 && dragStartRow != dragStopRow)
	{
	  ((ResolverTableModel) table.getModel()).drag(dragStartRow, dragStopRow);
	  dragStartRow = dragStopRow;
	  table.setRowSelectionInterval(dragStopRow, dragStopRow);
	}
    }
  
	
  void stopDrag(MouseEvent e)
    {
      dragStartRow = -1;
    }
}

