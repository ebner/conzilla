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


public class LangStringList extends AbstractListDisplayer implements MetaDataFieldEditor
{
  Vector languageBoxes;
  Vector textAreas;

  boolean textFlow = false;

  
  class GridModel extends ListGridTableModel
  {
    public GridModel()
      {
      }
    
    public int getRowCount()
      {
	return languageBoxes.size();
      }

    public int getColumnCount()
      {
	return 2;
      }

    public Object getValueAt(int row, int col)
      {
	if(col == 0)
	  return languageBoxes.get(row);

	return textAreas.get(row);
      }

    public GridTableComponentFactory.Constraints getConstraintsFor(int row, int col)
      {
	if(col == 0)
	  defaultConstraints.weightx = 0.0;
	else
	  defaultConstraints.weightx = 1.0;
	return defaultConstraints;
      }
  }

  public LangStringList(MetaData.LangStringType langstringtype, boolean textFlow,
			boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      this((langstringtype == null) ? null : langstringtype.langstring, textFlow, editable,
	   editListener, metaDataField);
    }
  
  public LangStringList(MetaData.LangString[] strings, boolean textFlow,
			boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      super(editable, editListener, metaDataField);

      languageBoxes = new Vector();
      textAreas     = new Vector();

      this.textFlow = textFlow;
      
      if(strings != null)
	for(int i = 0; i < strings.length; i++)
	  addItem(strings[i], i);
      
      setModel(new GridModel());      
    }

  public MetaData.LangStringType getLangStringType(boolean resetEdited)
    {
      MetaData.LangString[] ls = getLangStrings(resetEdited);

      if(ls == null)
	return null;

      return new MetaData.LangStringType(ls);
    }
  
    
  public MetaData.LangString[] getLangStrings(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(languageBoxes.size() == 0)
        return null;

      MetaData.LangString[] strings
      = new MetaData.LangString[languageBoxes.size()];
      
      for(int i = 0; i < strings.length; i++)
	{
	  strings[i] = new MetaData.LangString(((LanguageBox) languageBoxes.get(i)).getLanguage(resetEdited),
					       ((StringPanel) textAreas.get(i)).getString(resetEdited, false));
	}
      
      return strings;
    }
  
  protected boolean isItemEdited()
    {
      for(int i = 0; i < languageBoxes.size(); i++)
	{
	  if(((LanguageBox) languageBoxes.get(i)).isEdited() ||
	     ((StringPanel) textAreas.get(i)).isEdited())
	    return true;
	}
      return false;
    }

  protected void removeItemImpl(int index)
    {
      removeAndDetach(languageBoxes, index);
      removeAndDetach(textAreas, index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.LangString(null, ""), index);
    }
  
  void addItem(MetaData.LangString string, int index)
    {
      LanguageBox box = new LanguageBox(string.language, editable, editListener, metaDataField);
      
      StringPanel area = new StringPanel(string.string, textFlow, editable, editListener, metaDataField);
      
      languageBoxes.add(index, box);
      textAreas.add(index, area);
    }
  
}


