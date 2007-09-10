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


package se.kth.cid.component.cache;
import  se.kth.cid.component.*;
import  se.kth.cid.identity.*;

/** Used to cache Components.
 *  It could be implemented in a number of ways.
 */
public interface ComponentCache 
{
  /** Finds a component within the cache and returns it.
   *
   * @param uri the URI of the component to find.
   * @returns a Component if it does exist, otherwise null.
   */
  Component getComponent(String uri);

  /** References a component in the cache.
   *  If the component is not already in the cache, it is added.
   *
   * @param comp the component to reference.
   */
  void referenceComponent(Component comp);

  /** Clears the cache, removing all components from it.
   *
   */
  void clear();
}

