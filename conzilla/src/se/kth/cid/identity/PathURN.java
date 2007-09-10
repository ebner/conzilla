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

/** Represents a Path URN.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class PathURN extends URN
{

  public PathURN(String nuri) throws MalformedURIException
    {
      super(nuri);

      if(uri.length() == secondColonLocation + 1)
	throw new MalformedURIException("Empty Path URN", uri);

      if(uri.charAt(secondColonLocation + 1) != '/')
	throw new MalformedURIException("Path URN has no '/': \""+
					uri + "\"", uri);

      if(!uri.regionMatches(colonLocation + 1, "path", 0, secondColonLocation - colonLocation - 1))
	throw new MalformedURIException("The identifier was no Path URN \""
					+ uri + "\".", uri);
    }

  
  /** Returns the <code>path</code> part of the URN.
   */
  public String getPath()
  {
    return super.getProtocolSpecific();
  }

  protected URI makeRelativeURI(String relstr) throws MalformedURIException
    {
      String res;
      if(relstr.charAt(0) == '/')
	res = uri.substring(0, secondColonLocation + 1) + relstr;
      else
	{
	  int sl = uri.lastIndexOf('/', fragmentLocation);
	  res = uri.substring(0, sl + 1) + relstr;
	}
      
      return new PathURN(res);
    }
      
}

  
