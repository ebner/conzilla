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
  /** The String meaning Component
   */ 
  String COMPONENT  = "Component";
  /** The String meaning Neuron
   */ 
  String NEURON     = "Neuron";
  /** The String meaning NeuronType
   */ 
  String NEURONTYPE = "NeuronType";
  /** The String meaning ConceptMap
   */ 
  String CONCEPTMAP = "ConceptMap";
  

  /** Returns the MIME type served by this FormatHandler
   *
   * @return the MIME type served by this handler.
   */
  MIMEType getMIMEType();

  /** Returns whether this formathandler can deal with a specified URI.
   */
  boolean canHandleURI(URI uri);

  /** To be commented.
   */
  void setComponentStore(ComponentStore store);

  /** Loads the specified container.
   *
   * @param uri the URI from where the component should be loaded.
   * @param origuri the component's real URI.
   * @return the loaded component. Never null.
   * @exception ComponentException if anything went wrong while loading the component.
   */
  Container  loadContainer(URI uri, URI origuri)
    throws ComponentException;
  
  /** Loads the specified component.
   *
   * @param uri the URI from which to load the component.
   * @param origuri the component's real URI.
   * @return the loaded component. Never null.
   * @exception ComponentException if anything went wrong while loading the component.
   */
  Component  loadComponent(URI uri, URI origuri)
    throws ComponentException;

  /** Checks whether a component loaded from the given URI would be savable.
   *
   * @param uri the URI of the component to check.
   * @return true if a component loaded from the given URI would be savable.
   */
  boolean    isSavable(URI uri);
  
  /** Checks whether a component with this URI could be created.
   *
   * @param uri the URI of the component to check.
   * @exception ComponentException if a component loaded from the given URI would not be savable.
   *            Otherwise, returns normally.
   */
  void       checkCreateComponent(URI uri) throws ComponentException;

  /** Creates a component of the given type.
   *  Note that creation fails if the component already exists.
   *
   *  @param uri the URI where to create the component.
   *  @param origuri the component's real URI.
   *  @param type the type of the Component, as one of the Strings constants in the interface.
   *  @param extras extra arguments needed for this type of component. Typically NeuronType URI for
   *         Neurons.
   *  @return the created component. Never null.
   *  @exception ComponentException if anything went wrong while creating the component.
   */  
  Component  createComponent(URI uri, URI origuri, String type, Object extras)
    throws ComponentException;
  
  /** Tries to save the component to the given URI.
   *
   * @param uri the URI to save the component to.
   * @param comp the component that is to be saved.
   * @exception ComponentException if anything went wrong when saving the component.
   */
  void saveComponent(URI uri, Component comp) throws ComponentException;
}

