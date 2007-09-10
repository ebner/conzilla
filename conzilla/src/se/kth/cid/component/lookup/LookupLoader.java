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

/** This is a rather general implementation of a ComponentLoader.
 *
 *  It uses a URILookup to locate the component, and then different loaders for different
 *  MIME types. It currently only supports text/xml, but...
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class LookupLoader implements ComponentLoader
{

  /** The lookup class to use.
   */
  URILookup lookup;

  /** Table containing the format loaders.
   *  Maps MIME type -> FormatLoader.
   */
  Hashtable formatLoaders;

  /** Constructs a LookupLoader that uses the given URILookup.
   *  There should probably be some way to add new format loaders from the outside.
   *
   * @param lookup the URILookup to use.
   * @exception ComponentException if constructing was impossible.
   */
  public LookupLoader(URILookup lookup)
    throws ComponentException
  {
    this.lookup = lookup;
    
    formatLoaders = new Hashtable();
    try {
      formatLoaders.put(new MIMEType("text/xml"), new XmlFormatLoader());
    } catch(MalformedMIMETypeException e)
      {
	Tracer.trace("Invalid MIMEType:" + e.getType(), Tracer.ERROR);
	throw new ComponentException("Invalid MIMEType: " + e.getType()
				     + ":\n " + e.getMessage());
      }
  }
  

  public Component loadComponent(URI uri, ComponentLoader recursiveLoader)
    throws ComponentException
  {
    
    URILookupResult result = lookup.lookupURI(uri);
      
    FormatLoader loader = (FormatLoader) formatLoaders.get(result.format);
    
    // Be careful to use the recursiveLoader when really loading the component.
    if(loader != null)
      return loader.loadComponent(uri, result.uri, recursiveLoader);
    
    throw new UnknownFormatException(result.format);
  }
  
  public void releaseComponent(Component comp)
    {
      // Nothing to do here. 
    }
  public void renameComponent(URI olduri, URI newuri)
    {
      // Nothing to do here. 
    }
  
}
