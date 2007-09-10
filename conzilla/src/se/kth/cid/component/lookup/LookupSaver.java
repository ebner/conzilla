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


package se.kth.cid.component.lookup;


import se.kth.cid.component.*;
import se.kth.cid.util.*;

import java.util.*;

/** This is a rather general implementation of a ComponentSaver.
 *
 *  It uses a URILookup to find out where to save the component,
 *  and then different savers for different
 *  MIME types. It currently only supports text/xml, but...
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LookupSaver implements ComponentSaver
{
  /** The URILookup to use.
   */
  URILookup lookup;

  /** Table containing the format savers.
   *  Maps MIME type -> FormatSaver.
   */
  Hashtable formatSavers;

  /** Constructs a LookupSaver that uses the given URILookup.
   *  There should probably be some way to add new format savers from the outside.
   *  This class could possibly be merged with LookupLoader.
   *
   * @param lookup the URILookup to use.
   * @exception ComponentException if constructing was impossible.
   */
  public LookupSaver(URILookup lookup)
    throws ComponentException
    {
      this.lookup = lookup;

      formatSavers = new Hashtable();
      try {
	formatSavers.put(new MIMEType("text/xml"), new XmlFormatSaver());
      } catch(MalformedMIMETypeException e)
	{
	  Tracer.trace("Invalid MIMEType:" + e.getType(), Tracer.ERROR);
	  throw new ComponentException("Invalid MIMEType: " + e.getType()
				       + ":\n " + e.getMessage());
	}
    }

  public boolean isURISavable(URI uri)
    {
      URILookupResult result  = null;  
      try {
	result = lookup.lookupURI(uri);
      }catch(LookupException e)
	{
	  // Ehhh, no really good way to return error messages.
	  Tracer.trace("Failed looking up save method for uri: " + uri.toString() + "!", Tracer.WARNING);
	  return false;
	}
      
      FormatSaver saver = (FormatSaver) formatSavers.get(result.format);
      if(saver == null)
	{
	  Tracer.trace("Failed finfind an apropriate FormatSaver for uri: " + uri.toString() + "!", Tracer.WARNING);
	  return false;
	}
      return saver.isComponentSavable(result.uri, null);
    }
    
  public boolean isComponentSavable(Component comp)
    {
      URILookupResult result  = null;
      URI uri=null;
      try {
	uri=new URI(comp.getURI());
	result = lookup.lookupURI(uri);
      } catch(MalformedURIException e)
	{
	  Tracer.trace("Component had illegal URI: " + comp.getURI() + "!", Tracer.ERROR);
	  return false;
	}
      catch(LookupException e)
	{
	  // Ehhh, no really good way to return error messages.
	  Tracer.trace("Failed looking up save method for uri: " + uri.toString() + "!", Tracer.WARNING);
	  return false;
	}
      
      FormatSaver saver = (FormatSaver) formatSavers.get(result.format);
      if(saver == null)
	{
	  Tracer.trace("Failed finfind an apropriate FormatSaver for uri: " + uri.toString() + "!", Tracer.WARNING);
	  return false;
	}
      return saver.isComponentSavable(result.uri, comp);
    }
  
  public boolean doComponentExist(URI uri)
    {
      URILookupResult result  = null;
      try {
	result = lookup.lookupURI(uri);
      }
      catch(LookupException e)
	{
	  // Ehhh, no really good way to return error messages.
	  Tracer.trace("Failed looking up save method for component: " + uri + "!", Tracer.WARNING);
	  return false;
	}
      FormatSaver saver = (FormatSaver) formatSavers.get(result.format);
      if(saver == null)
	{
	  Tracer.trace("Failed finfind an apropriate FormatSaver for component: " + uri + "!", Tracer.WARNING);
	  return false;
	}
      return saver.doComponentExist(result.uri);
    }


  
  public void saveComponent(Component comp)
    throws ComponentException
    {
    
      URILookupResult result  = null;

      try {
	result = lookup.lookupURI(new URI(comp.getURI()));
      } catch(MalformedURIException e)
	{
	  Tracer.trace("Component had illegal URI: " + comp.getURI() + "!", Tracer.ERROR);
	  throw new ComponentException("Component had illegal URI: " + comp.getURI() + "!");
	}

      FormatSaver saver = (FormatSaver) formatSavers.get(result.format);
      if(saver != null)
	saver.saveComponent(result.uri, comp);
      else
	throw new UnknownFormatException(result.format);
    }  
}
