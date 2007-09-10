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

/** Classifies an URI
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class URIClassifier
{
  private URIClassifier()
    {}
  
  public static URI parseURI(String nuri) throws MalformedURIException
    {
      return parseURI(nuri, null);
    }
  
  /** Constructs an URI from a String
    * @param nuri the URI
    */
  public static URI parseURI(String nuri, URI baseuri) throws MalformedURIException
    {
      int colonLocation = nuri.indexOf(':');

      int fragmentLocation = getFragmentLocation(nuri);
      
      if(colonLocation == -1 || colonLocation > fragmentLocation)
	{
	  if(baseuri == null)
	    throw new MalformedURIException("No ':' in \"" + nuri +
					    "\" and no base URI given," +
					    " so no relative URIs allowed", nuri);
	  else
	    return baseuri.makeRelativeURI(nuri);
	}

      String protocol = nuri.substring(0, colonLocation);

      if (protocol.equals("urn"))
	return parseURN(nuri, colonLocation, fragmentLocation);
      else if (protocol.equals("http"))
	return new URL(nuri);
      else if (protocol.equals("file"))
	return new FileURL(nuri);
      else
	return parseGeneralURI(nuri, colonLocation, fragmentLocation);
    }

  protected static URI parseURN(String nuri, int colonLocation,
				int fragmentLocation) throws MalformedURIException
    {
      int secondColonLocation = nuri.indexOf(':', colonLocation + 1);

      if(secondColonLocation == -1
	 || secondColonLocation > fragmentLocation
	 || secondColonLocation == colonLocation + 1)
	throw new MalformedURIException("No protocol part in URN \""
					+ nuri + "\".", nuri);
    
      String urnprotocol = nuri.substring(colonLocation + 1,
					  secondColonLocation);

      if (urnprotocol.equals("path"))
	return new PathURN(nuri);
      else
	return parseGeneralURN(nuri, colonLocation, secondColonLocation, fragmentLocation);
      
    }

  protected static URI parseGeneralURN(String nuri, int colonLocation,
				       int secondColonLocation, int fragmentLocation)
    throws MalformedURIException
    {
      return new URN(nuri);
    }
  
  protected static URI parseGeneralURI(String nuri, int colonLocation,
				       int fragmentLocation)
    throws MalformedURIException
    {
      return new URI(nuri);
    }
  
  
  protected static int getFragmentLocation(String s)
    {
      int fragmentLocation = s.indexOf('#');
      if(fragmentLocation == -1)
	return s.length();

      return fragmentLocation;
    }
}

  
