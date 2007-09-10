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


/** FormatSavers save components in a given MIME type.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface FormatSaver
{
  /** Checks whether the component is savable to the given URI.
   *
   * @param uri the URI to save the component to. Usually found via a URILookup.
   * @param comp the component that is to be saved, usage? Can be null.
   * @return true if it is reasonable to try to save the component to the given URI.
   */
  boolean isComponentSavable(URI uri, Component comp);

  /** Checks wheter the component already exists.
   * @param uri the URI to check for existens.
   * @return true if a component exists.
   */
  boolean doComponentExist(URI uri);
  
  /** Tries to save the component to the given URI.
   *
   * @param uri the URI to save the component to. Usually found via a URILookup.
   * @param comp the component that is to be saved.
   * @exception FormatException if anything went wrong when saving the component.
   */
  void saveComponent(URI uri, Component comp)
    throws FormatException;
}
