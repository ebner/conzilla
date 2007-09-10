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

import java.net.*;
import java.util.*;


/** This interface is use to lookup component URIs.
 *  The idea is to give it an URI, and the result is a new URI that tells you how
 *  to actually get contact with the component (i.e. usually an URL), as well as a
 *  MIME-type that says what format the component is in.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface URILookup
{

  /** Looks up an URI.
   *
   * @param uri the URI of the component to look up.
   * @return the location of the component and the format it is in. Never null.
   * @exception LookupException if anything goes wrong in the lookup.
   */
  URILookupResult lookupURI(URI uri) throws LookupException;
  
}
