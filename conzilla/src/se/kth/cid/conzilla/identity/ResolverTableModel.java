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
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.properties.*;

import java.util.*;
import javax.swing.table.*;

public class ResolverTableModel extends AbstractTableModel
{
  static final String columnNames[] = {"PATH",
				 "BASE_URI",
				 "TYPE"};

  public static final String MIMETypes[] = {"text/xml"};
  
  
  Vector rows;

  ResolverTable table;

  boolean isEdited;
  
  public ResolverTableModel(ResolverTable table)
    {
      this.table = table;

      reset();
    }

  public void reset()
    {
      isEdited = false;
      
      ResolverTable.ResolverEntry[] entries = table.getEntries();
      
      rows = new Vector(entries.length);
      for(int i = 0; i < entries.length; i++)
	{
	  String[] entry = {entries[i].path,
			    entries[i].baseURI,
			    entries[i].type.toString()};
	  rows.add(entry);
	}
      fireTableDataChanged();
    }

  public boolean isEdited()
    {
      return isEdited;
    }

  public void setEdited(boolean b)
    {
      isEdited = b;
    }
  
  public int getRowCount()
    {
      return rows.size();
    }

  public int getColumnCount()
    {
      return 3;
    }

  public Object getValueAt(int row, int col)
    {
      return ((String[])rows.get(row))[col];
    }

  public boolean isCellEditable(int rowIndex, int columnIndex)
    {
      return table.isSavable();
    }

  public void setValueAt(Object aValue, int row, int col)
    {
      if(((String[])rows.get(row))[col].equals(aValue))
	return;

      ((String[])rows.get(row))[col] = (String)aValue;

      fireTableCellUpdated(row, col);
      isEdited = true;
    }

  
  public void remove(int rowno)
    {
      rows.removeElementAt(rowno);
      fireTableRowsDeleted(rowno, rowno);
      isEdited = true;
    }

  
  public void insert(int rowno)
    {
      String[] row = new String[3];
      row[0] = "/";
      row[1] = "file:/";
      row[2] = "text/xml";
	
      rows.insertElementAt(row, rowno);
      fireTableRowsInserted(rowno, rowno);
      isEdited = true;
    }
	   
  public String getColumnName(int col)
    { 
      return ConzillaResourceManager.getDefaultManager().getString(ResolverExtra.class.getName(), columnNames[col]);
    }

  public void drag(int start, int stop)
    {
      Object startRow = rows.get(start);
      Object stopRow = rows.get(stop);

      rows.removeElementAt(start);
      fireTableRowsDeleted(start, start);

      rows.insertElementAt(startRow, stop);
      fireTableRowsInserted(stop, stop);
      isEdited = true;
    }    

  public ResolverTable.ResolverEntry[] getEntries() throws ResolveException, MalformedURIException, MalformedMIMETypeException
    {
      ResolverTable.ResolverEntry[] entries = new ResolverTable.ResolverEntry[rows.size()];
      for(int i = 0; i < rows.size(); i++)
	{
	  String[] arr = (String[]) rows.get(i);
	  entries[i] = table.newResolverEntry(arr[0], arr[1],
					      new MIMEType(arr[2]));
	}
      return entries;
    }
}

