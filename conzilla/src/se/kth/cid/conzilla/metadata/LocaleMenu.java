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

import se.kth.cid.conzilla.properties.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LocaleMenu extends JMenu implements LocaleListener
{
  JMenuItem manageItem;

  LocaleManager locMan;

  public LocaleMenu(final Component over)
    {
      super("Language");
      locMan = LocaleManager.getLocaleManager();
      
      locMan.addLocaleListener(this);
      manageItem = new JMenuItem("Manage languages...");
      MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();
      menuManager.customizeButton(manageItem, "MANAGE_LANGUAGES");
      manageItem.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      locMan.displayEditor(over);
	    }
	});

      makeMenu();
    }

  void makeMenu()
    {
      MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();

      removeAll();

      add(manageItem);

      addSeparator();

      Locale[] locales = locMan.getLocales();

      for(int i = 0; i < locales.length; i++)
	{
	  final Locale l = locales[i];
	  JMenuItem menuItem = new JMenuItem(locales[i].getDisplayName());
	  menuItem.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
		{
		  locMan.setDefaultLocale(l);
		}
	    });
	  menuManager.customizeButton(add(menuItem));
	}
    }

  public void localeAdded(LocaleEvent e)
    {
      makeMenu();
    }

  public void localeRemoved(LocaleEvent e)
    {
      makeMenu();
    }

  public void setDefaultLocale(LocaleEvent e)
    {
      makeMenu();
    }
  
}

