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


public class AnnotationList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector persons;
  Vector dates;
  Vector descriptions;

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

  public AnnotationList(MetaData.Annotation[] aList,
			boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      persons      = new Vector();
      dates        = new Vector();
      descriptions = new Vector();
      panels       = new Vector();

      if(aList != null)
	for(int i = 0; i < aList.length; i++)
	  addItem(aList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Annotation[] getAnnotations(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;
      
      if(panels.size() == 0)
        return null;

      MetaData.Annotation[] anns
        = new MetaData.Annotation[panels.size()];
      
      for(int i = 0; i < anns.length; i++)
	{
	  anns[i] = new MetaData.Annotation(((StringPanel) persons.get(i)).getString(resetEdited, true),
					    ((DateEdit) dates.get(i)).getDateType(resetEdited),
					    ((LangStringList) descriptions.get(i)).getLangStringType(resetEdited));
	}
      
      return anns;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < panels.size(); i++)
	{
	  if(((StringPanel) persons.get(i)).isEdited() ||
	     ((DateEdit) dates.get(i)).isEdited() ||
	     ((LangStringList) descriptions.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  protected void removeItemImpl(int index)
    {
      removeAndDetach(persons, index);
      removeAndDetach(dates, index);
      removeAndDetach(descriptions, index);
      
      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Annotation(null, null, null), index);
    }
  
  void addItem(MetaData.Annotation ann, int index)
    {
      StringPanel persComp = new StringPanel(ann.person, false, editable, editListener, metaDataField);
      DateEdit    dateComp = new DateEdit(ann.date, editable, editListener, metaDataField);
      LangStringList descComp = new LangStringList(ann.description, true, editable, editListener, metaDataField);
      
      persons.add(index, persComp);
      dates.add(index, dateComp);
      descriptions.add(index, descComp);

      LabelFields panel = new LabelFields();

      panel.addLabelField("Person (VCard)", persComp);
      panel.addLabelField("Date", dateComp);
      panel.addLabelField("Description", descComp);
      
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


