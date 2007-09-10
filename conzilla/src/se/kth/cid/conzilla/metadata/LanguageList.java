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


public class LanguageList extends AbstractListDisplayer
{
  Vector languageBoxes;
  
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
	return 1;
      }

    public Object getValueAt(int row, int col)
      {
	return languageBoxes.get(row);
      }

    public void setValueAt(int row, int col, Object v)
      {
	languageBoxes.set(row, v);
      }

    public String getTitle(int col)
      {
	return "Language";
      }
    
  }

  public LanguageList(String[] langs, boolean editable)
    {
      super(editable);

      languageBoxes = new Vector();
      
      if(langs == null)
	langs = new String[0];
      
      for(int i = 0; i < langs.length; i++)
	addItem(langs[i], i);
      
      setModel(new GridModel());      
    }

  public String[] getLanguages()
    {
      if(languageBoxes.size() == 0)
        return null;

      String[] strings = new String[languageBoxes.size()];
      
      for(int i = 0; i < strings.length; i++)
	{
	  strings[i] = ((LanguageBox) languageBoxes.get(i)).getLanguage();
	}
      
      return strings;
    }

  protected void removeItemImpl(int index)
    {
      languageBoxes.remove(index);
    }
  
  protected void createItemImpl(int index)
    {
      addItem("", index);
    }
  
  void addItem(String string, int index)
    {
      LanguageBox box = new LanguageBox(string, editable);

      languageBoxes.add(index, box);
    }
  
}


