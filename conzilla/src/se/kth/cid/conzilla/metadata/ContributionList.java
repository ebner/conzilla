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


public class ContributionList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector roles;
  Vector entities;
  Vector dates;

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

  public ContributionList(MetaData.Contribute[] cList,
			  boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      roles    = new Vector();
      entities = new Vector();
      dates    = new Vector();
      panels   = new Vector();

      if(cList != null)
	for(int i = 0; i < cList.length; i++)
	  addItem(cList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Contribute[] getContributes(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;
      
      if(panels.size() == 0)
        return null;

      MetaData.Contribute[] contributes
        = new MetaData.Contribute[panels.size()];
      
      for(int i = 0; i < contributes.length; i++)
	{
	  contributes[i] = new MetaData.Contribute(((LangStringComponent) roles.get(i)).getLangString(resetEdited),
						   ((StringList) entities.get(i)).getStrings(resetEdited),
						   ((DateEdit) dates.get(i)).getDateType(resetEdited));
	}
      
      return contributes;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < panels.size(); i++)
	{
	  if(((LangStringComponent) roles.get(i)).isEdited() ||
	     ((StringList) entities.get(i)).isEdited() ||
	     ((DateEdit) dates.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  
  protected void removeItemImpl(int index)
    {
      removeAndDetach(roles, index);
      removeAndDetach(entities, index);
      removeAndDetach(dates, index);

      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Contribute(null, null, null), index);
    }
  
  void addItem(MetaData.Contribute cont, int index)
    {
      LangStringComponent lscomp = new LangStringComponent(cont.role, false, editable, editListener, metaDataField);
      StringList entity = new StringList("VCard", cont.entity, false, editable, editListener, metaDataField);
      DateEdit date = new DateEdit(cont.date, editable, editListener, metaDataField);
      
      roles.add(index, lscomp);
      entities.add(index, entity);
      dates.add(index, date);
      
      LabelFields panel = new LabelFields();

      panel.addLabelField("Role", lscomp);
      panel.addLabelField("Entity", entity);
      panel.addLabelField("date", date);
      
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


