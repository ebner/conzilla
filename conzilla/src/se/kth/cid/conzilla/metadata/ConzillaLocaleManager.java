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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.app.*;

import java.util.*;
import java.io.*;
import java.beans.*;

public class ConzillaLocaleManager
{
    public static final String LOCALES_PROP = "conzilla.locale.list";
    public static final String DEFAULT_LOCALE_PROP = "conzilla.locale.default";

  LocaleManager manager;

    GlobalConfig config;

  public ConzillaLocaleManager() throws IOException
    {
      config = GlobalConfig.getGlobalConfig();
      manager = LocaleManager.getLocaleManager();
      initLocales();
    }

      
  void initLocales() throws IOException
    {
      LocaleManager locMan = LocaleManager.getLocaleManager();

      String locales = config.getProperty(LOCALES_PROP, "en");
      
      StringTokenizer st = new StringTokenizer(locales, ",");

      while(st.hasMoreTokens())
	  {
	      String locale = st.nextToken();
	      locMan.addLocale(parseLocale(locale));
	  }

      locMan.addPropertyChangeListener(new PropertyChangeListener()
	{
	  public void propertyChange(PropertyChangeEvent e)
	    {
	      saveLocale();
	    }
	});
      String defStr = config.getProperty(DEFAULT_LOCALE_PROP);
      if(defStr != null)
	manager.setDefaultLocale(parseLocale(defStr));
    }


  public static Locale parseLocale(String locale) throws IOException 
    {
      String lang = "";
      String country = "";
      String variant = "";
      
      StringTokenizer st = new StringTokenizer(locale, "_");
      if(!st.hasMoreTokens())
	  throw new IOException("Invalid locale: " + locale);
      
      lang = st.nextToken();
      if(st.hasMoreTokens())
	country = st.nextToken();
      if(st.hasMoreTokens())
	variant = st.nextToken();

      return new Locale(lang, country, variant);
    }
  
  public void saveLocale()
    {
      Locale[] locales = manager.getLocales();

      StringBuffer sb = new StringBuffer();
      
      if(locales.length > 0)
	  sb.append(locales[0].toString());
      
      for(int i = 1; i < locales.length; i++)
	  {
	      sb.append(",");
	      sb.append(locales[i].toString());
	  }
      
      config.setProperty(LOCALES_PROP, sb.toString());

      
      config.setProperty(DEFAULT_LOCALE_PROP, Locale.getDefault().toString());
    }

}


