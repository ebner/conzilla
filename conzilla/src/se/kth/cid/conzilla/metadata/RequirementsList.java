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


public class RequirementsList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector types;
  Vector names;
  Vector minvers;
  Vector maxvers;

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

  public RequirementsList(MetaData.Requirements[] rList,
			  boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      types    = new Vector();
      names    = new Vector();
      minvers  = new Vector();
      maxvers  = new Vector();
      panels   = new Vector();

      if(rList != null)
	for(int i = 0; i < rList.length; i++)
	  addItem(rList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Requirements[] getRequirements(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(panels.size() == 0)
        return null;

      MetaData.Requirements[] reqs
        = new MetaData.Requirements[panels.size()];
      
      for(int i = 0; i < reqs.length; i++)
	{
	  reqs[i] = new MetaData.Requirements(((LangStringComponent) types.get(i)).getLangString(resetEdited),
					      ((LangStringComponent) names.get(i)).getLangString(resetEdited),
					      ((StringPanel) minvers.get(i)).getString(resetEdited, true),
					      ((StringPanel) maxvers.get(i)).getString(resetEdited, true));
	}
      
      return reqs;
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < panels.size(); i++)
	{
	  if(((LangStringComponent) types.get(i)).isEdited() ||
	     ((LangStringComponent) names.get(i)).isEdited() ||
	     ((StringPanel) minvers.get(i)).isEdited() ||
	     ((StringPanel) maxvers.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  protected void removeItemImpl(int index)
    {
      removeAndDetach(names, index);
      removeAndDetach(types, index);
      removeAndDetach(minvers, index);
      removeAndDetach(maxvers, index);

      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Requirements(null, null, null, null), index);
    }
  
  void addItem(MetaData.Requirements req, int index)
    {
      LangStringComponent typeComp = new LangStringComponent(req.type, false, editable, editListener, metaDataField);
      LangStringComponent nameComp = new LangStringComponent(req.name, false, editable, editListener, metaDataField);
      StringPanel minVers = new StringPanel(req.minimumversion, false, editable, editListener, metaDataField);
      StringPanel maxVers = new StringPanel(req.maximumversion, false, editable, editListener, metaDataField);
      
      types.add(index, typeComp);
      names.add(index, nameComp);
      minvers.add(index, minVers);
      maxvers.add(index, maxVers);

      LabelFields panel = new LabelFields();

      panel.addLabelField("Type", typeComp);
      panel.addLabelField("Name", nameComp);
      panel.addLabelField("Minimum Version", minVers);
      panel.addLabelField("Maximum Version", maxVers);
      
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


