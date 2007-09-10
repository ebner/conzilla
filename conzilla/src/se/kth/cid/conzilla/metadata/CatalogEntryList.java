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


public class CatalogEntryList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector entryAreas;
  Vector langStringLists;
  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return entryAreas.size();
      }

    public int getColumnCount()
      {
	return 2;
      }

    public Object getValueAt(int row, int col)
      {
	if(col == 0)
	  return entryAreas.get(row);

	return langStringLists.get(row);
      }

    public String getTitle(int col)
      {
	if(col == 0)
	  return "Catalogue";

	return "Entry";
      }
    
  }

  public CatalogEntryList(MetaData.CatalogEntry[] catEntries,
			  boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      entryAreas      = new Vector();
      langStringLists = new Vector();
      
      if(catEntries == null)
	catEntries = new MetaData.CatalogEntry[0];
      
      for(int i = 0; i < catEntries.length; i++)
	addItem(catEntries[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.CatalogEntry[] getCatalogEntries(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(entryAreas.size() == 0)
        return null;

      MetaData.CatalogEntry[] entries
        = new MetaData.CatalogEntry[entryAreas.size()];
      
      for(int i = 0; i < entries.length; i++)
	{
	  entries[i] = new MetaData.CatalogEntry(((StringPanel) entryAreas.get(i)).getString(resetEdited, true),
						 ((LangStringList) langStringLists.get(i)).getLangStringType(resetEdited));
	}
      
      return entries;
    }


  protected boolean isItemEdited()
    {
      for(int i = 0; i < entryAreas.size(); i++)
	{
	  if(((StringPanel) entryAreas.get(i)).isEdited() ||
	     ((LangStringList) langStringLists.get(i)).isEdited())
	    return true;
	}
      return false;
    }
  
  protected void removeItemImpl(int index)
    {
      removeAndDetach(entryAreas, index);
      removeAndDetach(langStringLists, index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.CatalogEntry("", null), index);
    }
  
  void addItem(MetaData.CatalogEntry cEntry, int index)
    {
      StringPanel area = new StringPanel(cEntry.catalogue, false, editable, editListener, metaDataField);
      LangStringList lsComp = new LangStringList(cEntry.entry, false, editable, editListener, metaDataField);
      
      entryAreas.add(index, area);
      langStringLists.add(index, lsComp);
    }
  
}


