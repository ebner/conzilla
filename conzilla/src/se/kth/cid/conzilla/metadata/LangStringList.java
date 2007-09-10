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


public class LangStringList extends AbstractListDisplayer
{
  Vector languageBoxes;
  Vector textAreas;
  
  class GridModel implements ListGridTableModel
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

    public void setValueAt(int row, int col, Object v)
      {
	if(col == 0)
	  languageBoxes.set(row, v);

	textAreas.set(row, v);
      }

    public String getTitle(int col)
      {
	if(col == 0)
	  return "Language";

	return "String";
      }
    
  }

  public LangStringList(MetaData.LangStringType langstringtype,
			boolean editable)
    {
      this((langstringtype == null) ? null : langstringtype.langstring, editable);
    }
  
  public LangStringList(MetaData.LangString[] strings,
			boolean editable)
    {
      super(editable);

      languageBoxes = new Vector();
      textAreas     = new Vector();
      
      if(strings == null)
	strings = new MetaData.LangString[0];
      
      for(int i = 0; i < strings.length; i++)
	{
	  addItem(strings[i], i);
	}
      
      setModel(new GridModel());      
    }

  public MetaData.LangStringType getLangStringType()
    {
      MetaData.LangString[] ls = getLangStrings();

      if(ls == null)
	return null;

      return new MetaData.LangStringType(ls);
    }
  
    
  public MetaData.LangString[] getLangStrings()
    {
      if(languageBoxes.size() == 0)
        return null;

      MetaData.LangString[] strings
      = new MetaData.LangString[languageBoxes.size()];
      
      for(int i = 0; i < strings.length; i++)
	{
	  strings[i] = new MetaData.LangString(((LanguageBox) languageBoxes.get(i)).getLanguage(), ((JTextArea) textAreas.get(i)).getText());
	}
      
      return strings;
    }

  protected void removeItemImpl(int index)
    {
      languageBoxes.remove(index);
      textAreas.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem(new MetaData.LangString(null, ""), index);
    }
  
  void addItem(MetaData.LangString string, int index)
    {
      LanguageBox box = new LanguageBox(string.language, editable);

      JTextArea area = new JTextArea(0, 30);
      area.setText(string.string);
      area.setBorder(BorderFactory.createLoweredBevelBorder());
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
      area.setEditable(editable);
      
      languageBoxes.add(index, box);
      textAreas.add(index, area);
    }
  
}


