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


public class LanguageBox extends JPanel
{
  JComboBox box;
  JTextField area;

  Locale displayedLocale;

  class LocaleRenderer extends JTextField implements ListCellRenderer
  {
    Font f;
    public LocaleRenderer()
      {
	this.setEditable(false);
	this.setBorder(null);
	f = this.getFont();
      }

    public void paintComponent(Graphics g)
      {
	this.setFont(f);
	super.paintComponent(g);
      }
    
    public Component getListCellRendererComponent(JList list,
						  Object value,
						  int index,
						  boolean isSelected,
						  boolean cellHasFocus)
      {
	setText(getString((Locale) value));

	if (isSelected) {
	  this.setBackground(list.getSelectionBackground());
	  this.setForeground(list.getSelectionForeground());
	} else {
	  this.setBackground(Color.white);
	  this.setForeground(list.getForeground());
	}

	return this;
      }
    
  }

  
  public LanguageBox(String language, boolean editable)
    {
      setLayout(new FillLayout());

      displayedLocale = MetaDataUtils.getLocale(language);
      if(editable)
	{
	  box = new JComboBox();
	  box.setRenderer(new LocaleRenderer());
	  box.setBackground(Color.white);
	  
	  Vector v = new Vector();
	  v.add(new Locale("", "", ""));
	  v.add(new Locale("de", "", ""));
	  v.add(new Locale("en", "", ""));
	  v.add(new Locale("fr", "", ""));
	  v.add(new Locale("sv", "", ""));	  

	  int index = v.indexOf(displayedLocale);
	  if(index == -1)
	    {
	      v.add(displayedLocale);
	      index = v.size() - 1;
	    }
	  
	  DefaultComboBoxModel model = new DefaultComboBoxModel(v);
	  box.setModel(model);
	  box.setSelectedIndex(index);
	  add(box);
	}
      else
	{
	  area = new JTextField(getString(displayedLocale));
	  area.setEditable(false);
	  area.setBackground(Color.white);
	  add(area);
	}
    }

  String getString(Locale l)
    {
      if(l.getLanguage().length() == 0)
	return "(none)";
      else
	return l.getDisplayName();
    }

  public String getLanguage()
    {
      if(box != null)
	displayedLocale = (Locale) box.getSelectedItem();
      
      return MetaDataUtils.getLanguageString(displayedLocale);
    }
}
