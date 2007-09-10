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

/** Represents an URL.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class URL extends URI
{
  int port;
  int portColonLocation;
  int pathLocation;

  public URL(String nurl) throws MalformedURIException
    {
      super(nurl);

      if(uri.length() < colonLocation + 4)
	throw new MalformedURIException("URL is incomplete: \""+
					uri + "\"", uri);
      
      if((uri.charAt(colonLocation + 1) != '/') ||
	 (uri.charAt(colonLocation + 2) != '/'))
	throw new MalformedURIException("URL does not begin with '://': \"" +
					uri + "\"", uri);
      portColonLocation = uri.indexOf(':', colonLocation + 3);
      pathLocation      = uri.indexOf('/', colonLocation + 3);

      if(pathLocation == colonLocation + 3)
	throw new MalformedURIException("URL has no host: \""+
					uri + "\"", uri);
      if(pathLocation == -1)
	throw new MalformedURIException("URL has no '/': \""+
					uri + "\"", uri);

      if(pathLocation > fragmentLocation)
	throw new MalformedURIException("URL has no '/' before fragment: \""+
					uri + "\"", uri);
	
      port = -1;
      if(portColonLocation >= 0 && portColonLocation < pathLocation)
	{
	  try {
	    port = Integer.parseInt(uri.substring(portColonLocation + 1,
						  pathLocation), 10);
	  }
	  catch(NumberFormatException e)
	    {
	      throw new MalformedURIException("Invalid port number \"" +
					      uri.substring(portColonLocation + 1,
							     pathLocation)
					      + "\" in \"" + uri + "\"",
					      uri);
	    }
	  if(port <= 0 || port > 65535)
	    throw new MalformedURIException("Invalid port number \"" + port +
					    "\" in \"" + uri + "\"",
					    uri);
	}
      else
	portColonLocation = pathLocation;
    }
  
  
  /** Returns the <code>host</code> part of the URL.
   */
  public String getHost()
    {
      return uri.substring(colonLocation + 3, portColonLocation);
    }
  
   /** Returns the <code>port</code> part of the URL.
   */
  public int getPort()
    {
      return port;
    }
  
  /** Returns the <code>path</code> part of the URL.
   */
  public String getPath()
    {
      return uri.substring(pathLocation, fragmentLocation);
    }

  protected URI makeRelativeURI(String relstr) throws MalformedURIException
    {
      String res;
      
      if(relstr.charAt(0) == '/')
	res = uri.substring(0, pathLocation) + relstr;
      else
	{
	  int sl = uri.lastIndexOf('/', fragmentLocation);
	  res = uri.substring(0, sl + 1) + relstr;
	}
      
      return new URL(res);
    }
  
}

  
