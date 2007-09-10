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
import se.kth.cid.component.*;

/** FormatLoaders load components in a given MIME type.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface FormatLoader
{
  /** Loads the specified component.
   *
   * @param realURI the URI of the component.
   * @param fetchURI the URI used to fetch the component (found via an URILookup).
   * @param recursiveLoader the Loader to use to load components recursively.
   * @return the loaded component. Never null.
   * @exception FormatException if anything went wrong while loading the component.
   */
  Component loadComponent(URI realURI, URI fetchuri, ComponentLoader recursiveLoader)
    throws FormatException;
}
