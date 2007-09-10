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

import se.kth.cid.util.Tracer;

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

  /** The location of the hash '#'.
   */
  protected int fragmentLocation;


  public URI(String nuri) throws MalformedURIException
    {
      uri = nuri;
      colonLocation = nuri.indexOf(':');

      fragmentLocation = URIClassifier.getFragmentLocation(nuri);

      sanityCheck();
    }
  

  
  /** Constructs an URI from a String and the parsed location of the colon.
    * @param nuri the URI
    * @param coloc the location of the colon. Will not be checked.
    */
  protected URI(String nuri, int coloc, int fragloc)
    {
      uri = nuri;
      colonLocation = coloc;
      fragmentLocation = fragloc;
    }

  private void sanityCheck() throws MalformedURIException
    {
      if(colonLocation == -1 || colonLocation > fragmentLocation
	 || colonLocation == 0)
	throw new MalformedURIException("No scheme in URI \"" + uri +
					"\"", uri);
    }
  
  
  protected URI makeRelativeURI(String relstr) throws MalformedURIException
    {
      throw new MalformedURIException("Generic URIs do not support relative URIs", uri);
      
    }


  /** returns the java.net.URL representation.
   */
  public java.net.URL getJavaURL() throws java.net.MalformedURLException
    {
      return new java.net.URL(uri);
    }
  
  /** Returns the <code>scheme</code> part of the URI.
   */
  public String getScheme()
    {
      return uri.substring(0, colonLocation);
    }

  /** Returns the <code>scheme-specific</code> part of the URI.
    */
  public String getSchemeSpecific()
    {
      return uri.substring(colonLocation + 1, fragmentLocation);
    }

  /** Returns the <code>fragment</code> part of the URI.
    */
  public String getFragment()
    {
      if(fragmentLocation == uri.length())
	return "";
      
      return uri.substring(fragmentLocation + 1);
    }

  
  
  /** Returns the URI in original form, i.e.
    * <code>scheme:scheme-specific</code>.
    */
  public String toString()
    {
      return uri;
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

  
