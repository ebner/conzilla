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


package se.kth.cid.util;

import java.net.*;

/** Represents an URI.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class URI 
{

  /** The URI in full.
   */
  protected String uri;

  /** The location of the colon.
   */
  protected int colonLocation;

  
  /** Parses an URI from a String.
    * @param nuri the string to parse
    * @exception MalformedURIException if there is no ':' in nuri
    */
  public URI(String nuri) throws MalformedURIException
    {
      if(nuri == null)
	throw new MalformedURIException("Null URI!", null);
      uri = nuri;
      colonLocation = uri.indexOf(':');
      if(colonLocation == -1)
	throw new MalformedURIException("No ':' in " + uri, uri);
    }

  /** The copy constructor.
    * @param nuri the URI to parse
    */
  public URI(URI nuri)
    {
      if(nuri == null)
	throw new NullPointerException("URI copy constructor got null pointer"
				       +"instead of URI!");
      uri = nuri.uri;
      colonLocation = nuri.colonLocation;
    }

  /** Returns the <code>scheme</code> part of the URI.
    */
  public String getScheme()
    {
      return uri.substring(0, colonLocation);
    }

  /** Returns the <code>scheme-specific</code> part of the URI.
    */
  public String getSchemeSpecifics()
    {
      return uri.substring(colonLocation + 1);
    }

  /** Returns the URI in original form, i.e.
    * <code>scheme:scheme-specific</code>.
    */
  public String toString()
    {
      return uri;
    }
  
  /** Returns the URI as an URL.
   *  @exception MalformedURLException if it is no URL.
   */
  public URL toURL() throws MalformedURLException
    {
      return new URL(uri);
    }

  public boolean equals(Object compURI)
    {
      if(compURI == null)
	return false;
      if(compURI instanceof URI)
	{
	  URI nuri = (URI) compURI;
	  return nuri.uri.equals(uri);
	}
      return false;
    }
  
  
  public int hashCode()
    {
      return uri.hashCode();
    }
}

  
