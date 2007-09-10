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


public class StringList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector stringAreas;

  String title;
  boolean textFlow = false;
  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return stringAreas.size();
      }

    public int getColumnCount()
      {
	return 1;
      }

    public Object getValueAt(int row, int col)
      {
	return stringAreas.get(row);
      }

    public String getTitle(int col)
      {
	return title;
      }
    
  }

  public StringList(String title, String[] strings, boolean textFlow,
		    boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);
      
      this.title = title;
      this.textFlow = textFlow;
      
      stringAreas     = new Vector();
      
      if(strings == null)
	strings = new String[0];
      
      for(int i = 0; i < strings.length; i++)
	addItem(strings[i], i);
      
      setModel(new GridModel());      
    }

  public String[] getStrings(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;
      
      if(stringAreas.size() == 0)
        return null;

      String[] strings = new String[stringAreas.size()];
      
      for(int i = 0; i < strings.length; i++)
	{
	  strings[i] = ((StringPanel) stringAreas.get(i)).getString(resetEdited, false);
	}
      
      return strings;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < stringAreas.size(); i++)
	{
	  if(((StringPanel) stringAreas.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  protected void removeItemImpl(int index)
    {
      removeAndDetach(stringAreas, index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem("", index);
    }
  
  void addItem(String string, int index)
    {
      StringPanel area = new StringPanel(string, textFlow, editable, editListener, metaDataField);
      
      stringAreas.add(index, area);
    }
  
}


