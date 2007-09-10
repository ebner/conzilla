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


package se.kth.cid.component;

import se.kth.cid.util.*;


/**
 * Universal URI for locating external components, with protocol 'cid:'.
 * <p>
 * The URIs are meant to be globally unique, and are on the form
 * <code>cid:patch/component</code>,
 * where <code>patch</code> means where to get the component, and
 * <code>component</code> which component to get. The format of these are
 * in no way specified.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ComponentURI extends URI
{
  /** The location of the slash.
   */
  int slashLocation;

  /** Parses a ComponentURI from a String, including the 'cid:' part.
   *  @param ncuri the string to parse
   *  @exception MalformedURIException if this is an invalid URI, or
   *                                   if there is no '/' in ncuri
   */
  public ComponentURI(String ncuri) throws MalformedURIException
  {
    super(ncuri);

    slashLocation = uri.indexOf('/', colonLocation + 1);
    if(slashLocation == -1)
      throw new MalformedURIException("No '/' in cid URI \""
				      + ncuri + "\"", ncuri);
    }
  
  /** Returns the <code>patch</code> part of the ComponentURI.
   *
   * @return the <code>patch</code> part of the ComponentURI.
   */
  public String getPatch()
  {
    return uri.substring(colonLocation + 1, slashLocation);
  }

  /** Returns the <code>component</code> part of the ComponentURI.
   *
   * @return the <code>component</code> part of the ComponentURI.
   */
  public String getComponent()
  {
    return uri.substring(slashLocation + 1);
  }
}

  
