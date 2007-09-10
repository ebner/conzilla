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

/** ComponentLoaders are used to locate and connect with/retrieve
 *  different types of components.
 *
 *  It could be implemented in a number of ways, probably
 *  involving some form of URI lookup.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ComponentLoader
{
  /** Loads a Component from a given URI.
   *
   *  The type of component returned can be identified by using instanceof.
   *  Internally, the type of component is identified in some unspecified way,
   *  possibly different for different protocols.
   *
   *  Components such as ConceptMaps will need to load more components automatically.
   *  They will use the recursiveLoader for this. This makes it possible to
   *  nest loaders, e.g. overloading a loader with a cache that wants to be involved
   *  even in such automatic loads. The top-level caller will
   *  usually simply do loader.loadComponent(uri, loader).
   *
   *  This functionality could be removed from here if someone took responsibility
   *  for loading the neurons in a conceptmap. Could be the right thing to do.
   *  
   *  @param uri URI of the component to load.
   *  @param recursiveLoader the loader to use for loading more components.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if anything goes wrong when loading the component.
   */
  Component loadComponent(URI uri, ComponentLoader recursiveLoader)
    throws ComponentException;

  /** This method must be called on components that will no longer be used.
   *
   *  This makes it possible to implement caches transparently.
   *
   *  @param comp the component not longer needed.
   */
  void      releaseComponent(Component comp);

  /** If a component is saved locally or just given another URI, this
   *  function should be called.
   *
   *  Without renaming the component in the loader a new instance of the object will
   *  be fetched (if possible) while the old one is still there but with the wrong URI.
   *  It doesn't matter if the component itself is renamed or not for the function to work.
   *
   * @param olduri the old URI for a component
   * @param newuri the new URI for the component.
   */
  void      renameComponent(URI olduri, URI newuri);

  /** Provides a Component to be internally reachable.
   *
   *  Notice that no check if the Component is savable is done.
   *  @param comp is a Component of any kind.
   *  @returns false if the Component doesn't contain a acceptable URI. 
   *
   boolean   addComponent(Component comp);
  */
}

