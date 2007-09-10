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
import java.lang.reflect.*;

/** This class contains utility functions for use with MetaData.
 */
public class MetaDataUtils
{

  public static final Locale EMPTY_LOCALE = new Locale("", "", "");
  
  
  private MetaDataUtils()
    {
    }
  

  /** Returns an IMS meta-data conforming string representing
   *  the given language.
   *
   *  @param l the Locale to convert.
   *  @return a IMS meta-data conforming string representing the given locale.
   */
  public static String getLanguageString(Locale l)
    {
      if(l == null)
	return null;
      
      String lang = l.getLanguage();
      String country = l.getCountry();

      if(lang.length() == 0)
	return null;

      if(country.length() == 0)
	return lang;

      return lang + "-" + country.toUpperCase();
    }
  
  /** Converts an IMS meta-data language specification into a Java Locale.
   *
   *  Please note that getLanguageString(getLocale(str))
   *  is not always the identity mapping, when the string
   *  given does not conform to the specification.
   *
   *  Valid example:   en-US -> en-US,
   *  Invalid example: en_US -> en_us.
   *
   *  @param language the string to convert.
   *  @return a Locale object representing the language.
   */
  public static Locale getLocale(String language)
    {
      if(language == null || language.length() == 0)
	return EMPTY_LOCALE;
      
      if(language.length() == 5 && language.charAt(2) == '-')
	return new Locale(language.substring(0, 2), language.substring(3, 5), "");
      if(language.length() == 2)
	return new Locale(language, "", "");

      Tracer.trace("Illegal language: " + language, Tracer.WARNING);
      return new Locale(language, "");
    }

  /** Returns the string in the LangStringType most matching the
   *  current default Locale.
   *
   *  This takes into account both country codes, if applicable,
   *  and the default metadata language (which is "en" if not given).
   *
   *  If no good match is found, the first string is returned.
   *
   *  @param defaultLang the default language, as given by
   *                     MetaData.get_metametadata_language().
   *  @param langstring the LangStringType to search. If null, the empty
   *                    string is returned.
   *  @return a string in the most matching language, or "".
   */
  public static MetaData.LangString getLocalizedString(String defaultLang,
						       MetaData.LangStringType langstring)
    {
      if(defaultLang == null)
	defaultLang = "en";

      if(langstring == null)
	return new MetaData.LangString(defaultLang, "");
      

      Locale l = Locale.getDefault();

      MetaData.LangString langMatch = null;
      
      for(int i = langstring.langstring.length - 1; i >= 0; i--)
	{
	  String lang = langstring.langstring[i].language;
	  if(lang == null)
	    lang = defaultLang;
	  
	  Locale stringlocale = getLocale(lang);
	  
	  if(stringlocale.equals(l))
	    return new MetaData.LangString(lang, langstring.langstring[i].string);
	  
	  if(stringlocale.getLanguage().equals(l.getLanguage()))
	    langMatch =  new MetaData.LangString(lang, langstring.langstring[i].string);
	}
      
      if(langMatch != null)
	return langMatch;

      String lang = langstring.langstring[0].language;
      
      if(lang == null)
	lang = defaultLang;
      
      return  new MetaData.LangString(lang, langstring.langstring[0].string);
    }

  /** Returns a list of URIs contained in a Location array.
   *
   *  Ignores non-parsable URIs.
   *
   *  @param loc the location element to search.
   *  @return the parsed URIs. Never null, but possibly empty.
   */
  public static URI[] getLocations(MetaData.Location[] loc, URI baseuri)
    {
      if(loc != null)
	{
	  Vector uris = new Vector();

	  for(int i = 0; i < loc.length; i++)
	    if(loc[i].type != null && loc[i].type.equals("URI"))
	      try {
		uris.addElement(URIClassifier.parseURI(loc[i].string, baseuri));
	      } catch (MalformedURIException e)
		{
		  Tracer.trace("Ignoring malformed URI in Metadata:\n "
			       + e.getMessage(), Tracer.WARNING);
		}
	  return (URI[]) uris.toArray(new URI[uris.size()]);
	}
      return new URI[0];
    }

  /** Returns a list of MIMETypes contained in a LangStringType,
   *  such as the one in technical.format.
   *
   *  Ignores non-parsable MIME types.
   *
   *  @param format the element to search.
   *  @return the parsed MIMETypes. Never null, but possibly empty.
   */
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

  
  public static void addObject(MetaData md, String tag, Object obj)
    {
      if(obj == null)
	return;

      Class mdClass = md.getClass();
      Class componentType = obj.getClass();
      try{
	Method getMethod = mdClass.getMethod("get_" + tag, null);
	
	Object[] oldArr = (Object[])getMethod.invoke(md, null);
	
	int length = 1;
	if (oldArr != null)
	  length = oldArr.length + 1;
	
	Object[] newArr = (Object[]) Array.newInstance(componentType, length);
	
	for (int i = 0; i < length - 1; i++)
	  newArr[i] = oldArr[i];
	
	newArr[length - 1] = obj;
	
	Method setMethod = mdClass.getMethod("set_" + tag, new Class[]{newArr.getClass()});
	
	setMethod.invoke(md, new Object[]{newArr});
      } catch(NoSuchMethodException e)
	{
	  Tracer.bug("No such MetaData method: " + e.getMessage());
	}
      catch(IllegalAccessException e)
	{
	  Tracer.bug("Illegal access to MetaData method: " + e.getMessage());
	}
      catch(InvocationTargetException e)
	{
	  Tracer.bug("Illegal invocation target for MetaData method: " + e.getMessage());
	}
    }

  public static void removeObject(MetaData md, String tag, Object obj)
    {
      if(obj == null)
	return;
      
      Class mdClass = md.getClass();
      Class componentType = obj.getClass();
      try{
	Method getMethod = mdClass.getMethod("get_" + tag, null);
	
	Object[] oldArr = (Object[]) getMethod.invoke(md, null);

	
	if(oldArr == null)
	  return;

	int index = -1;
	
	for(int i = 0; i < oldArr.length; i++)
	  {
	    if(oldArr[i] == obj)
	      {
		index = i;
		break;
	      }
	  }

	if(index == -1)
	  return;

	Method setMethod = mdClass.getMethod("set_" + tag, new Class[]{oldArr.getClass()});

	if(oldArr.length == 1)
	  {
	    setMethod.invoke(md, new Object[]{null});
	    return;
	  }

	Object[] newArr = (Object[]) Array.newInstance(componentType, oldArr.length - 1);

	for(int i = 0, j = 0; i < oldArr.length; i++, j++)
	  {
	    if(i == index)
	      {
		j--;
		continue;
	      }
	    newArr[j] = oldArr[i];
	  }
	
	setMethod.invoke(md, new Object[]{newArr});
      } catch(NoSuchMethodException e)
	{
	  Tracer.bug("No such MetaData method: " + e.getMessage());
	}
      catch(IllegalAccessException e)
	{
	  Tracer.bug("Illegal access to MetaData method: " + e.getMessage());
	}
      catch(InvocationTargetException e)
	{
	  Tracer.bug("Illegal invocation target for MetaData method: " + e.getMessage());
	}
    }
      

  public static MetaData.Relation getRelationTo(MetaData md, String kind, URI uri)
    {
      URI curi = URIClassifier.parseValidURI(md.getComponent().getURI());

      MetaData.Relation[] rel = md.get_relation();

      if(rel == null)
	return null;

      for(int i = 0; i < rel.length; i++)
	{
	  if(rel[i].kind != null && kind.equals(rel[i].kind.string))
	    {
	      try {
		URI relURI = URIClassifier.parseURI(rel[i].resource_location, curi);
		if(relURI.equals(uri))
		  return rel[i];
	      } catch(MalformedURIException e)
		{
		  Tracer.trace("Ignoring invalid URI: " + e.getMessage(),
			       Tracer.MINOR_INT_EVENT);
		}
	    }
	}
      return null;
    }
  
	      
      
      
  

  public static boolean containsString(MetaData.LangStringType lstr, String str)
    {
      if(lstr == null)
	return false;
      
      for(int i = 0; i < lstr.langstring.length; i++)
	{
	  if(lstr.langstring[i].string.equals(str))
	    return true;
	}

      return false;
    }
      
  /** This is a help function for investigating the classification entries in LOM metadata.
   *  See <href a="http://www.imsglobal.org/">The IMS homepage</a> for more information about
   *  the standard.
   *
   *  @param classific is the classification substructure of the MetaData structure.
   *  @param purpose is the purpose of a classification.
   *  @param source is by whom this classification is done.
   *  @param entries are an array containing the taxon path.
   */
  public static boolean isClassifiedAs(MetaData.Classification[] classific,
				       String purpose, String source,
				       String[] entries)
    {
      if(classific == null)
	return false;
      
      for(int i = 0; i < classific.length; i++)
	{
	  if(purpose != null &&
	     (classific[i].purpose == null || !(purpose.equals(classific[i].purpose.string))))
	    continue;
	  
	  if(classific[i].taxonpath == null)
	    continue;
	  
	  for(int j = 0; j < classific[i].taxonpath.length; j++)
	    {
	      MetaData.TaxonPath tp = classific[i].taxonpath[j];

	      if(source != null &&
		 (tp.source == null || !(source.equals(tp.source))))
		continue;
	      
	      if(tp.taxon == null || tp.taxon.length < entries.length)
		continue;

	      boolean match = true;
	      
	      for(int k = 0; k < entries.length; k++)
		{
		  MetaData.Taxon t = tp.taxon[k];

		  if(!containsString(t.entry, entries[k]))
		    {
		      match = false;
		      break;
		    }  
		}
	      if(match)
		return true;
	    }
	}
      return false;
    }
}

