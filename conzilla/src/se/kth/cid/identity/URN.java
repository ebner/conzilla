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

/** Represents an URN.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class URN extends URI
{

  /** The location of the second colon.
   */
  protected int secondColonLocation;


  public URN(String nurn) throws MalformedURIException
    {
      super(nurn);
      secondColonLocation = nurn.indexOf(':', colonLocation + 1);

      if(secondColonLocation == -1
	 || secondColonLocation > fragmentLocation
	 || secondColonLocation == colonLocation + 1)
	throw new MalformedURIException("No protocol part in URN \""
					+ nurn + "\".", nurn);
      if(!nurn.regionMatches(0, "urn", 0, colonLocation))
	throw new MalformedURIException("The identifier was no URN \""
					+ nurn + "\".", nurn);
    }
  

  /** Returns the <code>URN protocol</code> part of the URN.
    */
  public String getProtocol()
    {
      return uri.substring(colonLocation + 1, secondColonLocation);
    }

  
  /** Returns the <code>protocol specific</code> part of the URN.
    */
  public String getProtocolSpecific()
    {
      return uri.substring(secondColonLocation + 1, fragmentLocation);
    }

  protected URI makeRelativeURI(String relstr) throws MalformedURIException
    {
      throw new MalformedURIException("Generic URNs do not support relative URIs", uri);
      
    }
    
}

  
