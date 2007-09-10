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


public class LangStringTypeList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector langStringTypes;

  boolean textFlow = false;
  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return langStringTypes.size();
      }

    public int getColumnCount()
      {
	return 1;
      }

    public Object getValueAt(int row, int col)
      {
	return langStringTypes.get(row);
      }

  }

  public LangStringTypeList(MetaData.LangStringType[] strings, boolean textFlow,
			    boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      this.textFlow = textFlow;
      
      langStringTypes = new Vector();
      
      if(strings == null)
	strings = new MetaData.LangStringType[0];
      
      for(int i = 0; i < strings.length; i++)
	addItem(strings[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.LangStringType[] getLangStringTypes(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(langStringTypes.size() == 0)
        return null;

      MetaData.LangStringType[] strings = new MetaData.LangStringType[langStringTypes.size()];
      
      for(int i = 0; i < strings.length; i++)
	{
	  strings[i] = ((LangStringList) langStringTypes.get(i)).getLangStringType(resetEdited);
	}
      
      return strings;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < langStringTypes.size(); i++)
	{
	  if(((LangStringList) langStringTypes.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  
  protected void removeItemImpl(int index)
    {
      removeAndDetach(langStringTypes, index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.LangStringType(null), index);
    }
  
  void addItem(MetaData.LangStringType string, int index)
    {
      LangStringList list = new LangStringList(string, textFlow, editable, editListener, metaDataField);

      list.setBorder(BorderFactory.createLineBorder(Color.gray));
      langStringTypes.add(index, list);
    }
  
}


