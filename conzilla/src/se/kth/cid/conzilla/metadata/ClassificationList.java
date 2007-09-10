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


public class ClassificationList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector purposes;
  Vector taxonpaths;
  Vector descriptions;
  Vector keywords;

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

  public ClassificationList(MetaData.Classification[] cList,
			    boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);
      
      purposes      = new Vector();
      taxonpaths    = new Vector();
      descriptions  = new Vector();
      keywords      = new Vector();

      panels        = new Vector(); 
      
      if(cList != null)
	for(int i = 0; i < cList.length; i++)
	  addItem(cList[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.Classification[] getClassifications(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;
      
      if(panels.size() == 0)
        return null;

      MetaData.Classification[] cls
        = new MetaData.Classification[panels.size()];
      
      for(int i = 0; i < cls.length; i++)
	{
	  cls[i] = new MetaData.Classification(((LangStringComponent) purposes.get(i)).getLangString(resetEdited),
					       ((TaxonPathList) taxonpaths.get(i)).getTaxonPaths(resetEdited),
					       ((LangStringList) descriptions.get(i)).getLangStringType(resetEdited),
					       ((LangStringTypeList) keywords.get(i)).getLangStringTypes(resetEdited));
	}
      
      return cls;
    }

  protected void removeItemImpl(int index)
    {
      removeAndDetach(purposes, index);
      removeAndDetach(taxonpaths, index);
      removeAndDetach(descriptions, index);
      removeAndDetach(keywords, index);

      panels.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.Classification(null, null, null, null), index);
    }

  protected boolean isItemEdited()
    {
      for(int i = 0; i < panels.size(); i++)
	{
	  if(((LangStringComponent) purposes.get(i)).isEdited() ||
	     ((TaxonPathList) taxonpaths.get(i)).isEdited() ||
	     ((LangStringList) descriptions.get(i)).isEdited() ||
	     ((LangStringTypeList) keywords.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  
  void addItem(MetaData.Classification cl, int index)
    {
      LangStringComponent purpComp = new LangStringComponent(cl.purpose, false, editable, editListener, metaDataField);
      TaxonPathList       pathComp = new TaxonPathList(cl.taxonpath, editable, editListener, metaDataField);
      LangStringList      descComp = new LangStringList(cl.description, true, editable, editListener, metaDataField);
      LangStringTypeList  keyComp  = new LangStringTypeList(cl.keywords, false, editable, editListener, metaDataField);
      purposes.add(index, purpComp);
      taxonpaths.add(index, pathComp);
      descriptions.add(index, descComp);
      keywords.add(index, keyComp);
      
      LabelFields panel = new LabelFields();

      panel.addLabelField("Purpose", purpComp);
      panel.addLabelField("Taxon Paths", pathComp);
      panel.addLabelField("Description", descComp);
      panel.addLabelField("Keywords", keyComp);
      
      panel.setBorder(BorderFactory.createLineBorder(Color.gray));
      panels.add(index, panel);
    }
  
}


