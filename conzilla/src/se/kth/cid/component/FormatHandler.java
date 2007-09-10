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

import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;

/** FormatHandlers load and save components in a given MIME type.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface FormatHandler
{
  /** Loads the specified component.
   *
   * @param realURI the URI of the component.
   * @param fetchURI the URI used to fetch the component (found via an URILookup).
   * @return the loaded component. Never null.
   * @exception FormatException if anything went wrong while loading the component.
   */
  Component  loadComponent(URI uri, URI origuri, boolean isSavable)
    throws ComponentException;

  boolean    isSavable(URI uri);
  
  boolean    canCreateComponent(URI uri) throws ComponentException;
  
  Component  createComponent(URI uri, URI origuri)
    throws ComponentException;
  Neuron     createNeuron(URI uri, URI origuri, URI type)
    throws ComponentException;
  NeuronType createNeuronType(URI uri, URI origuri)
    throws ComponentException;
  ConceptMap createConceptMap(URI uri, URI origuri)
    throws ComponentException; 
  
  /** Tries to save the component to the given URI.
   *
   * @param uri the URI to save the component to. Usually found via a URILookup.
   * @param comp the component that is to be saved.
   * @exception FormatException if anything went wrong when saving the component.
   */
  void saveComponent(URI uri, Component comp) throws ComponentException;
}

