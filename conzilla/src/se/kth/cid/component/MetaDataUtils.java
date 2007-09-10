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


package se.kth.cid.component;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;

import java.util.*;

public class MetaDataUtils
{

  private MetaDataUtils()
    {
    }
  

  public static String getLanguageString(Locale l)
    {
      String lang = l.getLanguage();
      String country = l.getCountry();

      if(lang.length() == 0)
	return null;

      if(country.length() == 0)
	return lang;

      return lang + "-" + country;
    }
  

  public static Locale getLocale(String language)
    {
      if(language == null || language.length() == 0)
	return new Locale("", "", "");
      
      if(language.length() == 5 && language.charAt(2) == '-')
	return new Locale(language.substring(0, 2), language.substring(3, 5), "");
      if(language.length() == 2)
	return new Locale(language, "", "");

      Tracer.trace("Illegal language: " + language, Tracer.WARNING);
      return new Locale(language, "");
    }
  
  public static String getLocalizedString(MetaData metaData,
					  MetaData.LangStringType langstring)
    {
      String defaultLang = metaData.get_metametadata_language();

      if(defaultLang == null)
	defaultLang = "en";

      Tracer.debug("Metadata def lang = " + metaData.get_metametadata_language());
      
      Locale l = Locale.getDefault();

      Tracer.debug("Java def locale = " + l.toString());
      
      if(langstring != null)
	{
	  String langMatch = null;
	  
	  for(int i = langstring.langstring.length - 1; i >= 0; i--)
	    {
	      String lang = langstring.langstring[i].language;
	      if(lang == null)
		lang = defaultLang;

	      Locale stringlocale = getLocale(lang);

	      if(stringlocale.equals(l))
		return langstring.langstring[i].string;
	      
	      if(stringlocale.getLanguage().equals(l.getLanguage()))
		langMatch = langstring.langstring[i].string;
	    }
	  
	  if(langMatch != null)
	    return langMatch;

	  return langstring.langstring[0].string;
	}
      return "";
    }  

  public static URI[] getLocations(MetaData.Location[] loc)
    {
      if(loc != null)
	{
	  Vector uris = new Vector();

	  for(int i = 0; i < loc.length; i++)
	    if(loc[i].type != null && loc[i].type.equals("URI"))
	      try {
		uris.addElement(URIClassifier.parseURI(loc[i].string));
	      } catch (MalformedURIException e)
		{
		  Tracer.trace("Ignoring malformed URI in Metadata:\n "
			       + e.getMessage(), Tracer.WARNING);
		}
	  return (URI[]) uris.toArray(new URI[uris.size()]);
	}
      return new URI[0];
    }

  public static MIMEType[] getDigitalFormats(MetaData.LangStringType format)
    {
      if(format != null)
	{
	  Vector types = new Vector();

	  for(int i = 0; i < format.langstring.length; i++)
	    try {
	      types.addElement(new MIMEType(format.langstring[i].string));
	    } catch (MalformedMIMETypeException e)
	      {
		Tracer.trace("Ignoring non-MIMEType in Metadata:\n "
			     + e.getMessage(), Tracer.DETAIL);
	      }
	  return (MIMEType[]) types.toArray(new MIMEType[types.size()]);
	}
      return new MIMEType[0];
    }
  
}

