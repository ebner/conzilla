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


public class TaxonList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector ids;
  Vector entries;

  Vector panels;
  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return panels.size();
      }

    public int getColumnCount()
      {
	return 1;
      }

    public Object getValueAt(int row, int col)
      {
	return panels.get(row);
      }

    public String getTitle(int col)
      {
	return null;
      }
    
  }

  public TaxonList(MetaData.Taxon[] tList,
		   boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      ids     = new Vector();
      entries = new Vector();
      panels  = new Vector(); 
      
      if(tList != null)
	for(int i = 0; i < tList.length; i++)
	  addItem(tList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Taxon[] getTaxons(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(panels.size() == 0)
        return null;

      MetaData.Taxon[] taxs
        = new MetaData.Taxon[panels.size()];
      
      for(int i = 0; i < taxs.length; i++)
        {
	  taxs[i] = new MetaData.Taxon(((StringPanel) ids.get(i)).getString(resetEdited, true),
				       ((LangStringList) entries.get(i)).getLangStringType(resetEdited));
	}
      
      return taxs;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < panels.size(); i++)
	{
	  if(((StringPanel) ids.get(i)).isEdited() ||
	     ((LangStringList) entries.get(i)).isEdited())
	    return true;
	}
      return false;
    }


  protected void removeItemImpl(int index)
    {
      removeAndDetach(ids, index);
      removeAndDetach(entries, index);
      
      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Taxon(null, null), index);
    }
  
  void addItem(MetaData.Taxon t, int index)
    {
      StringPanel id = new StringPanel(t.id, false, editable, editListener, metaDataField);
      LangStringList entry = new LangStringList(t.entry, false, editable, editListener, metaDataField);

      ids.add(index, id);
      entries.add(index, entry);
      
      LabelFields panel = new LabelFields();

      panel.addLabelField("Id", id);
      panel.addLabelField("Entry", entry);
      
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


