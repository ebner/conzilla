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


package se.kth.cid.xml;

import java.awt.*;
import java.util.*;


/** This class contains utility functions for loading and saving
 *  XML components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlLoaderHelper
{
  /** Constructing an XmlLoaderHelper is not allowed.
   */
  protected XmlLoaderHelper()
    {}

  public static String loadAttribute(XmlElement el, String attname, String def)
    throws XmlStructureException
    {
      return loadAttribute(el, attname, def, false);
    }

  public static String loadAttribute(XmlElement el, String attname, String def,
				     boolean emptyAllowed)
    throws XmlStructureException
    {
      String att = el.getAttribute(attname);
      if(att == null || (!emptyAllowed && att.length() == 0))
	{
	  if(def != null)
	    return def;
	  else
	    throw new XmlStructureException("Missing " + attname + " in " + el.getName());	    
	}
      return att;
    }

  public static XmlElement getSubElement(XmlElement el, String elname)
    throws XmlStructureException
    {
      XmlElement[] els = el.getSubElements(elname);
      if(els.length == 0)
	throw new XmlStructureException("Missing element '" + elname + "'");

      if(els.length > 1)
	throw new XmlStructureException("Too many '" + elname + "'s");

      return els[0];
    }

  public static XmlElement maybeGetSubElement(XmlElement el, String elname)
    throws XmlStructureException
    {
      XmlElement[] els = el.getSubElements(elname);
      if(els.length == 0)
	return null;

      if(els.length > 1)
	throw new XmlStructureException("Too many '" + elname + "'s");
      
      return els[0];
    }
    
  /** Loads a boolean attribute.
   *
   *  Allowed values are "true" and "false".
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a boolean.
   *  @return the loaded boolean.
   *  @exception XmlStructureException if the attribute was invalid.
   */  
  public static boolean loadBoolean(XmlElement el, String attr, String def)
    throws XmlStructureException
    {
      boolean bool;
      
      String boolStr = loadAttribute(el, attr, def);

      if(boolStr.equals("true"))
	bool = true;
      else if(boolStr.equals("false"))
	bool = false;
      else
	throw new XmlStructureException(attr + "=\"" + boolStr +
				     "\", must be 'true' or 'false'");
      return bool;
    }
  
  /** Loads a positive integer attribute.
   *
   *  If allowInf is true, the string "infinity"
   *  is interpreted as Integer.MAX_VALUE.
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a positive integer.
   *  @return the loaded integer.
   *  @exception XmlStructureException if the attribute was invalid.
   */  
  public static int loadPositiveInteger(XmlElement el, String attr, String def,
					boolean allowInf)
    throws XmlStructureException
    {
      int value;

      String intStr = loadAttribute(el, attr, def);
      if(intStr.equals("infinity"))
	{
	  if(!allowInf)
	    throw new XmlStructureException(attr + " may not be infinity!");
	  value = Integer.MAX_VALUE;
	}
      else
	value = Integer.parseInt(intStr);
      
      //FIXME Why can't positions be negative???
      //      if(value < 0)
      //	throw new XmlStructureException(attr + "=\"" + intStr +
      //				     "\" is not a valid positive integer!");
      return value;
    }
}


