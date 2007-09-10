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


package se.kth.cid.identity;


import java.io.File;

/** This class represents "res:" URLs.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ResourceURL extends URI
{
  /** Creates a "res:" URL from the given string.
   *
   *  @param nuri the string to parse.
   *  @exception MalformedURIException if the string did not parse.
   */
  public ResourceURL(String nuri) throws MalformedURIException
    {
      super(nuri);

      if(uri.length() == colonLocation + 1)
	throw new MalformedURIException("Empty path", null);
      
      if(uri.charAt(colonLocation + 1) != '/')
	throw new MalformedURIException("No leading '/' in \""+ uri + "\"",
					uri);
      if(!uri.regionMatches(0, "res", 0, colonLocation))
	throw new MalformedURIException("The identifier was no Resource URI \""
					+ uri + "\".", uri);
    }

  
  public String getResourceName()
    {
      return super.getSchemeSpecific().substring(1);
    }
  
  
  /** Returns a Java URL object pointing to the file represented by this
   *  ResourceURL.
   *
   *  @returns a Java URL object.
   */
  public java.net.URL getJavaURL() throws java.net.MalformedURLException 
    {
      java.net.URL url = getClass().getClassLoader().getResource(getResourceName());
      if(url == null)
	throw new java.net.MalformedURLException("No such resource found: " + getResourceName());
      
      return url;
    }

  public String makeRelative(URI other, boolean allowDotDot) throws MalformedURIException
    {
      if(!(other instanceof ResourceURL))
	return other.toString();
      return genericMakeRelative(other, 4, 4, allowDotDot);
    }

  protected URI parseRelativeURI(String relstr) throws MalformedURIException
    {
      return new ResourceURL(genericParseRelativeURI(relstr, 4));
    }

}
