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
  
    /*  public static String findShortestRelativeURI(String makeSmaller, String fromBase)
  {
    boolean foundSmaller=false;
    int spos=makeSmaller.indexOf('/');
    int bpos=fromBase.indexOf('/');    
    String sstr=makeSmaller;
    String bstr=fromBase;
    
    while ( spos==bpos && spos!=-1 && 
	    sstr.substring(0,spos).equals(bstr.substring(0,bpos)) )
      {
	foundSmaller=true;
	sstr=sstr.substring(spos+1);
	bstr=bstr.substring(bpos+1);
	spos=sstr.indexOf('/');
	bpos=bstr.indexOf('/');
      }
    if (foundSmaller)
      {
	if ((bpos=bstr.indexOf('/'))==-1)
	  return sstr;
	else {
	  bstr=bstr.substring(bpos+1);
	  if ((bpos=bstr.indexOf('/'))==-1)
	    return "../"+sstr;
	  else {
	    bstr=bstr.substring(bpos+1);
	    if ((bpos=bstr.indexOf('/'))==-1)
	      return "../../"+sstr;
	  }
	}
      }
    return makeSmaller;
    } */


  public static URI parseValidURI(String nuri)
    {
      return parseValidURI(nuri, null);
    }
  
  public static URI parseValidURI(String nuri, String baseuri)
    {
      try {
	if(baseuri != null)
	  return URIClassifier.parseURI(nuri, URIClassifier.parseURI(baseuri, null));
	else
	  return URIClassifier.parseURI(nuri, null);
      } catch (MalformedURIException e)
	{
	  Tracer.bug("Malformed URI '" + e.getURI() +"': \n"
		     + e.getMessage());
	  return null; // Never reached
	}
    }

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
	    return baseuri.parseRelativeURI(nuri);
	}

      String protocol = nuri.substring(0, colonLocation);

      if (protocol.equals("urn"))
	return parseURN(nuri, colonLocation, fragmentLocation);
      else if (protocol.equals("http"))
	return new URL(nuri);
      else if (protocol.equals("file"))
	return new FileURL(nuri);
      else if (protocol.equals("res"))
	return new ResourceURL(nuri);
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

  
