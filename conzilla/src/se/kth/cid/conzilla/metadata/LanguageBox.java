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


public class LanguageBox extends JPanel implements MetaDataFieldEditor, LocaleListener
{
  JComboBox box;
  StringPanel area;

  boolean edited = false;

  Locale defaultLocale;
  
  String metaDataField;
  MetaDataEditListener editListener;

  LocaleManager manager;
  
  public LanguageBox(String language, boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      this.editListener = editListener;
      this.metaDataField = metaDataField;
      this.manager = manager;
      
      setLayout(new FillLayout());

      defaultLocale = MetaDataUtils.getLocale(language);

      
      if(editable)
	{
	  box = new JComboBox();
	  box.setRenderer(new LocaleRenderer());
	  box.setBackground(Color.white);

	  manager = LocaleManager.getLocaleManager();
	  //	  manager.addLocale(defaultLocale);
	  manager.addLocaleListener(this);

	  setModel(defaultLocale);

	  box.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e)
		{
		  if(e.getStateChange() == ItemEvent.SELECTED)
		    fireEdited();
		}
	    });
	  
	  add(box);
	}
      else
	{
	  String lang = "";
	  if(defaultLocale.getLanguage().length() > 0)
	    lang = defaultLocale.getDisplayName();
	  area = new StringPanel(lang, false, false, editListener, metaDataField);
	  add(area);
	}
    }


  public boolean isEdited()
    {
      return edited;
    }

  public void detach()
    {
      if(box != null)
	manager.removeLocaleListener(this);
    }
  
  
  public String getLanguage(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      Locale locale = defaultLocale;
      
      if(box != null)
	locale = (Locale) box.getSelectedItem();
      
      return MetaDataUtils.getLanguageString(locale);
    }

  void fireEdited()
    {
      edited = true;
      if(editListener != null)
	editListener.fieldEdited(new MetaDataEditEvent(null, metaDataField));
    }

    //FIXME This should be speeded up a bit (reuse in some way). 
  void setModel(Locale selected)
    {
      DefaultComboBoxModel model = new DefaultComboBoxModel();
      Locale[] locales = manager.getLocales();

      if(!Arrays.asList(locales).contains(selected))
	model.addElement(selected);

      if(!selected.equals(MetaDataUtils.EMPTY_LOCALE))
	model.addElement(MetaDataUtils.EMPTY_LOCALE);
      
      for(int i = 0; i < locales.length; i++)
	model.addElement(locales[i]);

      box.setModel(model);
      model.setSelectedItem(selected);
    }
  
  

  public void localeAdded(LocaleEvent e)
    {
      setModel((Locale) box.getSelectedItem());
    }

  public void localeRemoved(LocaleEvent e)
    {
      setModel((Locale) box.getSelectedItem());
    }

  public void setDefaultLocale(LocaleEvent e)
    {
      setModel((Locale) box.getSelectedItem());
    }
}
