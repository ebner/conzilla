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
import java.util.*;

/** This is a URILookup that works according to the same principle as
 *  BaseURLLookup, but contains a table mapping patches to URLs instead of
 *  mapping all patches to the same URL.
 *
 *  The path is constructed as: <br>
 *  <code>'base path for patch'/'component'.xml</code>, and the MIME type will always
 *  be "text/xml".
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class TableURLLookup implements URILookup
{
  /** Table containing the base paths to the components.
   *  Maps patch (String) --> URL.
   */
  Hashtable basePaths;

  /** Constructs an empty TableURLLookup
   */
  public TableURLLookup()
  {
    basePaths = new Hashtable();
  }

  /** Adds/removes a patch from the table.
   *  If basePath is non-null, it is added as the base path to the patch.
   *  Otherwise, the patch is removed from the table.
   *
   *  @param patch the patch to modify.
   *  @param basePath the base path of the patch, or null to remove the patch.
   */
  public void putBasePath(String patch, URL basePath)
  {
    if(basePath == null)
      basePaths.remove(patch);
    else
      basePaths.put(patch, basePath);
  }

  /** Returns the base path of the patch.
   *
   * @param patch the patch to look up.
   * @return the base path of the given patch, or null if unknown.
   */
  public URL getBasePath(String patch)
  {
    return (URL) basePaths.get(patch);
  }

  public URILookupResult lookupURI(URI uri)
    throws LookupException
  {
    try {
      if(uri.getScheme().equals("cid"))
	{
	  ComponentURI curi = new ComponentURI(uri.toString());
	  
	  URL basePath=null;
	  String part=curi.getPatch()+"/"+curi.getComponent();
	  String back="";
	  basePath = getBasePath(part);
	  int slashlocation;
	  while ((slashlocation = part.lastIndexOf('/')) != -1 &&
		 basePath==null)
	    {
	      back=part.substring(slashlocation)+back;
	      part=part.substring(0,slashlocation);
	      basePath = getBasePath(part);
	    }
	  if(basePath == null)
	    throw new LookupException("Could not find component " +
				      uri, uri);
	  URI newURI =
	      new URI(basePath + back + ".xml");
	  //	    new URI(basePath + "/" + back + ".xml");
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
