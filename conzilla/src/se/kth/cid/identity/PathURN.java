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

  /** Creates a "file:" URL from the given string.
   *
   *  @param nuri the string to parse.
   *  @exception MalformedURIException if the string did not parse.
   */
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

  public String makeRelative(URI other, boolean allowDotDot) throws MalformedURIException
    {
      if(!(other instanceof PathURN))
	return other.toString();
      return genericMakeRelative(other, ((PathURN) other).secondColonLocation + 1, secondColonLocation + 1,
				 allowDotDot);
    }

  protected URI parseRelativeURI(String relstr) throws MalformedURIException
    {
      return new PathURN(genericParseRelativeURI(relstr, secondColonLocation + 1));
    }  
}

  
