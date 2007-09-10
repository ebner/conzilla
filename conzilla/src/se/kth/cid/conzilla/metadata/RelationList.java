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


public class RelationList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector kinds;
  Vector descriptions;
  Vector locations;

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

  public RelationList(MetaData.Relation[] rList,
		      boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      kinds        = new Vector();
      descriptions = new Vector();
      locations    = new Vector();
      panels       = new Vector();

      if(rList != null)
	for(int i = 0; i < rList.length; i++)
	  addItem(rList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Relation[] getRelations(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(panels.size() == 0)
        return null;

      MetaData.Relation[] relations
        = new MetaData.Relation[panels.size()];
      
      for(int i = 0; i < relations.length; i++)
	{
	  relations[i] = new MetaData.Relation(((LangStringComponent) kinds.get(i)).getLangString(resetEdited),
					       ((LangStringList) descriptions.get(i)).getLangStringType(resetEdited),
					       ((StringPanel) locations.get(i)).getString(resetEdited, true));
	}
      
      return relations;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < panels.size(); i++)
	{
	  if(((LangStringComponent) kinds.get(i)).isEdited() ||
	     ((LangStringList) descriptions.get(i)).isEdited() ||
	     ((StringPanel) locations.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  protected void removeItemImpl(int index)
    {
      removeAndDetach(kinds, index);
      removeAndDetach(descriptions, index);
      removeAndDetach(locations, index);

      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Relation(null, null, null), index);
    }
  
  void addItem(MetaData.Relation rel, int index)
    {
      LangStringComponent kindComp = new LangStringComponent(rel.kind, false, editable, editListener, metaDataField);
      LangStringList      descComp = new LangStringList(rel.resource_description, true, editable, editListener, metaDataField);
      StringPanel         locComp  = new StringPanel(rel.resource_location, false, editable, editListener, metaDataField);
      
      kinds.add(index, kindComp);
      descriptions.add(index, descComp);
      locations.add(index, locComp);

      LabelFields panel = new LabelFields();

      panel.addLabelField("Kind", kindComp);
      panel.addLabelField("Resource Description", descComp);
      panel.addLabelField("Resource Location", locComp);
      
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


