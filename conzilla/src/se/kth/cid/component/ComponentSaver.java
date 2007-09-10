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

/** ComponentSavers are used to save components.
 *
 *  It could be implemented in a number of ways, probably
 *  involving some form of URI lookup, and possibly synchronized with a ComponentLoader.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ComponentSaver
{
  /** Checks whether the given component is savable.
   *
   *  The check is not a guarantee; it just makes sure that there is a sensible protocol
   *  and if possible (e.g. for local files) checks for write permission.
   *
   *  This method is intended to be used before allowing the user to edit a component.
   *
   *  Probably there is use for some information regarding why the component
   *  is not savable.
   *
   * @param comp the component to check savability for.
   * @return true if there is a sensible way to save this component.
   */ 
  boolean isComponentSavable(Component comp);

  /** Checks whether the given URI is savable.
   *
   *  This method is intended to be used before the user creates a new component.
   *
   * @param uri the component to check savability for.
   * @return true if there is a sensible way to use this URI.
   */
  boolean isURISavable(URI uri);
  
  /** Similiar to function isComponentSavable but only checks for existence.
   * @see isComponentSavable
   * @param uri the URI to check for a component to exist.
   * @return true if component exists.
   */
  boolean doComponentExist(URI uri);
  
  /** Tries to save the component.
   *
   *  Uses the URI of the component to identify a location to save it.
   *
   * @param comp the component to save
   * @exception ComponentException if anything went wrong while trying
   *                               to save the component.
   */
  void saveComponent(Component comp) throws ComponentException;
}

