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

import se.kth.cid.util.*;
import se.kth.cid.component.*;
import java.net.*;

/** A URI lookup class that uses a single base URL to contruct all URL, independent of
 *  the patch they are located in.
 *
 *  The Lookup will use
 *  the base path for all 'cid:' URIs.
 *  The path is constructed as: <br>
 *  <code>'base path'/'component'.xml</code>, and the MIME type will always
 *  be "text/xml".
 *
 *  All other schemas will be returned unmodified, but still with the same MIME type. 
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class BaseURLLookup implements URILookup
{
  /** The base path to the components.
   */
  URL basePath;

  
  /** Constructs a BaseURLLookup from a base path.
   * @param componentPath basic path to the components
   */
  public BaseURLLookup(URL componentPath)
    {
      basePath = componentPath;
    }

  public URILookupResult lookupURI(URI uri)
    throws LookupException
  {
    try {
      if(uri.getScheme().equals("cid"))
	{
	  ComponentURI curi = new ComponentURI(uri.toString());
	  
	  URI newURI =
	    new URI(basePath + "/" + curi.getComponent() + ".xml");
	  return new URILookupResult(newURI, new MIMEType("text/xml"));
	}
      return new URILookupResult(uri, new MIMEType("text/xml"));

    }
    catch(MalformedURIException e)
      {
	throw new LookupException("Invalid component ID: " + e.getURI()
				  + ":\n " + e.getMessage(), uri);
      }
    catch(MalformedMIMETypeException e)
      {
	Tracer.trace("Invalid MIMEType:" + e.getType(), Tracer.ERROR);
	throw new LookupException("Invalid MIMEType: " + e.getType()
				   + ":\n " + e.getMessage(), uri);
      }
  }
}
