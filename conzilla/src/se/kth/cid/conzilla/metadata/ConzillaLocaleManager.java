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

import se.kth.cid.util.*;
import se.kth.cid.conzilla.install.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.app.*;

import java.util.*;
import java.io.*;

public class ConzillaLocaleManager
{
  ConzillaConfig config;
  LocaleManager manager;

  public ConzillaLocaleManager(ConzillaConfig config) throws ConfigException
    {
      this.config = config;
      manager = LocaleManager.getLocaleManager();
      initLocales();
    }

  void cleanLocales()
    {
      String count = config.getProperty("locale.count", "0");
      int n = 0;
      try {
	n = Integer.parseInt(count);
      } catch (NumberFormatException e)
	{
	  Tracer.bug("Invalid locale.count: " + count);
	}

      if(n < 0)
	Tracer.bug("Invalid locale.count: " + count);

      for(int i = 0; i < n; i++)
	config.remove("locale." + i);
    }
      
      
  void initLocales() throws ConfigException
    {
      LocaleManager locMan = LocaleManager.getLocaleManager();

      String count = config.getProperty("locale.count", "0");
      int n = 0;
      try {
	n = Integer.parseInt(count);
      } catch (NumberFormatException e)
	{
	  throw new ConfigException("Invalid locale.count: " + count);
	}

      if(n < 0)
	throw new ConfigException("Invalid locale.count: " + count);

      for(int i = 0; i < n; i++)
	{
	  String locale = config.getProperty("locale." + i);
	  if(locale == null)
	    throw new ConfigException("Invalid locale." + i);

	  locMan.addLocale(parseLocale(locale));
	}

      locMan.addLocaleListener(new LocaleListener()
	{
	  public void localeAdded(LocaleEvent e)
	    {
	      saveLocale();
	    }
	  public void localeRemoved(LocaleEvent e)
	    {
	      saveLocale();
	    }
	  public void setDefaultLocale(LocaleEvent e)
	    {
	      saveLocale();
	    }
	});
      String defStr = config.getProperty("locale.default");
      if(defStr != null)
	manager.setDefaultLocale(parseLocale(defStr));
    }


  public static Locale parseLocale(String locale) throws ConfigException
    {
      String lang = "";
      String country = "";
      String variant = "";
      
      StringTokenizer st = new StringTokenizer(locale, "_");
      if(!st.hasMoreTokens())
	throw new ConfigException("Invalid locale: " + locale);
      
      lang = st.nextToken();
      if(st.hasMoreTokens())
	country = st.nextToken();
      if(st.hasMoreTokens())
	variant = st.nextToken();

      return new Locale(lang, country, variant);
    }
  
  public void saveLocale()
    {
      cleanLocales();

      Locale[] locales = manager.getLocales();
      
      config.setProperty("locale.count", "" + locales.length);

      for(int i = 0; i < locales.length; i++)
	{
	  config.setProperty("locale." + i, locales[i].toString());
	}
      
      config.setProperty("locale.default", "" + Locale.getDefault().toString());
      try {
	config.store();
      } catch(IOException e)
	{
	  ErrorMessage.showError("Save Error", "Cannot save config file.", e, null);
	}
      
    }
}


