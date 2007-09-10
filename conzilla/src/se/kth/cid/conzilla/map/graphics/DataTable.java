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
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.*;

public class DataTable extends JTable
{
  public class MyCellRenderer implements TableCellRenderer
  {
    public Color textcolor;
    
    public MyCellRenderer()
    {
      textcolor=Color.black;
    }
    public java.awt.Component getTableCellRendererComponent(JTable table,Object value,
							    boolean isSelected,
							    boolean hasFocus,
							    int row, int column)
    {
      JTextField jtf=new JTextField((String) value);
      jtf.setEditable(false);
      jtf.setEnabled(false);
      jtf.setBorder(new EmptyBorder(1,1,1,1)); // top, left, bottom, right insets.
      jtf.setEnabled(false);
      jtf.setOpaque(false);
      jtf.setDisabledTextColor(textcolor);
      return jtf;
    }
  }

  //----------------CellEditor------------------------------------
  public class MyCellEditor extends DefaultCellEditor implements ActionListener
  {
    public int rowIndex;
    public int columnIndex;
    private BasicTextFieldUI ui;
    private MouseEvent me;
    private JTable table;
    
    public MyCellEditor(JTable table)
    {
      super(new JTextField());
      this.table=table;
      setClickCountToStart(1);
      ui = new BasicTextFieldUI();
      ui.installUI((JTextField) getComponent());
      ((JTextField) getComponent()).setCaretPosition(0);
      ((JTextField) getComponent()).setEnabled(true);
      KeyStroke kup=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP,0);
      KeyStroke kdown=KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN,0);
      
      ((JTextField) getComponent()).registerKeyboardAction(this,"VK_UP",kup,
						 JComponent.WHEN_FOCUSED );
      
      ((JTextField) getComponent()).registerKeyboardAction(this,"VK_DOWN",kdown,
						 JComponent.WHEN_FOCUSED );
      me=null;
    }
    
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row,int column)
    {

      int pos=((JTextField) getComponent()).getCaretPosition();
      JTextField comp = (JTextField) super.getTableCellEditorComponent(table,value,isSelected,row,column);
      if (me != null)
	{
	  ((DefaultCaret) comp.getCaret()).mousePressed(me);
	  ((DefaultCaret) comp.getCaret()).mouseClicked(me);
	}
      else
	if (comp.getText().length() >= pos)
	  comp.setCaretPosition(pos);
	else
	  comp.setCaretPosition(comp.getText().length());
      this.rowIndex=row;
      this.columnIndex=column;
      comp.setOpaque(false);
      return comp;
    }

    public boolean isCellEditable(EventObject anEvent)
    {
      if (anEvent instanceof MouseEvent)
	me=(MouseEvent) anEvent;
      else
	me=null;
      return super.isCellEditable(anEvent);
    }
    //--------------cell editor listens for actions from its JTextField-----------
    public void actionPerformed(ActionEvent e)
    {
      if (e.getActionCommand().equals("VK_UP"))
	if (table.isCellEditable(rowIndex-1,columnIndex))
	  table.editCellAt(rowIndex-1,columnIndex);
      if (e.getActionCommand().equals("VK_DOWN"))
	  if(table.isCellEditable(rowIndex+1,columnIndex))
	    table.editCellAt(rowIndex+1,columnIndex); 
    }
  }

  
  public DataTable(TableModel dm)
    {
      super(dm);
      Class cl=String.class;
      
      setDefaultRenderer(cl,new MyCellRenderer());
      setDefaultEditor(cl,new MyCellEditor(this));

      unregisterKeyboardAction(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT,0));
      unregisterKeyboardAction(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT,0));
      unregisterKeyboardAction(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP,0));
      unregisterKeyboardAction(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN,0));
      setOpaque(false);
      setTableHeader(null);
      setShowHorizontalLines(false);
    }
  //-------Function overriden from JTable (copied and edited) because of bugs.--------
  public boolean editCellAt(int row, int column, EventObject e){
    if (isEditing()) {
      // Try to stop the current editor
      if (cellEditor != null) {
	boolean stopped = cellEditor.stopCellEditing();
	if (!stopped)
	  return false;       // The current editor not resigning
      }
    }
    
    if (!isCellEditable(row, column))
      return false;
    
    TableCellEditor editor = getCellEditor(row, column);
    if (editor != null) {
      if (editor.shouldSelectCell(e)) {
	// prepare editor - size it then added it to the table
	editorComp = prepareEditor(editor, row, column);
	
	editorComp.setBounds(getCellRect(row, column, false));
	this.add(editorComp);
	editorComp.validate();
	    
	setCellEditor(editor);
	setEditingRow(row);
	setEditingColumn(column);
	editor.addCellEditorListener(this);
	editorComp.requestFocus();
	return true;
      }
    }
    return false;
  }
  
}
