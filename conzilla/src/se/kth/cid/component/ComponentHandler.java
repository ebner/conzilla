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
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.util.*;
import java.util.*;


/** ComponentHandlers are used to locate and create/retrieve/save
 *  different types of components.
 *
 *  This interface should contain remove and path listing functionality, at least.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ComponentHandler
{
  /** Loads a Component from a given URI.
   *
   *  The type of component returned can be identified by using instanceof.
   *  Internally, the type of component is identified in some unspecified way,
   *  possibly different for different protocols.
   *
   *  @param uri URI of the component to load.
   *  @param container the container where the component should be loaded from, may be null.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if anything goes wrong when loading the component.
   */
  Component loadComponent(URI uri, Container container) throws ComponentException;

  /** Loads a Container from a given URI.
   *
   *  @param uri URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if anything goes wrong when loading the component.
   */
  Container loadContainer(URI uri) throws ComponentException;
  

  /** Tests if a component with this URI can be created.
   *  This is a possibly heavy-weight operation, involving not only a URI resolve,
   *  but also interaction with the server.
   *
   *  @return An array containing the URI that should be used in a createComponent call,
   *          and the MIME-type.
   *  @param uri URI of the new component to check.
   *  @exception ComponentException if anything stops us from creating the component.
   */
  Object[] checkCreateComponent(URI uri) throws ComponentException;

  
  /** Creates a Component with this URI.
   *  This is a possibly heavy-weight operation, involving not only a URI resolve,
   *  but also interaction with the server.
   *
   *  @param uri URI of the component to create.
   *  @param createURI Actual URI of the component to create, as returned from checkCreateComponent.
   *  @param type MIME type of the component to create, as returned from checkCreateComponent.
   *  @return the created component.
   *  @exception ComponentException if anything stops us from creating the component.
   */
  Component  createComponent(URI uri, URI creatURI, MIMEType type) throws ComponentException; 

  /** Creates a Neuron with this URI.
   *  This is a possibly heavy-weight operation, involving not only a URI resolve,
   *  but also interaction with the server.
   *
   *  @param uri URI of the neuron to create.
   *  @param createURI Actual URI of the neuron to create, as returned from checkCreateComponent.
   *  @param mimetype MIME type of the neuron to create, as returned from checkCreateComponent.
   *  @return the created component.
   *  @exception ComponentException if anything stops us from creating the component.
   */
  Neuron     createNeuron(URI uri, URI createURI, MIMEType mimetype, URI type) throws ComponentException;

  /** Creates a NeuronType with this URI.
   *  This is a possibly heavy-weight operation, involving not only a URI resolve,
   *  but also interaction with the server.
   *
   *  @param uri URI of the component to create.
   *  @param createURI Actual URI of the neuron type to create, as returned from checkCreateComponent.
   *  @param type MIME type of the neuron type to create, as returned from checkCreateComponent.
   *  @return the created component.
   *  @exception ComponentException if anything stops us from creating the component.
   */
  NeuronType createNeuronType(URI uri, URI createURI, MIMEType type) throws ComponentException;

  /** Creates a ConceptMap with this URI.
   *  This is a possibly heavy-weight operation, involving not only a URI resolve
   *  but also interaction with the server.
   *
   *  @param uri URI of the component to create.
   *  @param createURI Actual URI of the conceptmap to create, as returned from checkCreateComponent.
   *  @param type MIME type of the conceptmap to create, as returned from checkCreateComponent.
   *  @return the created component.
   *  @exception ComponentException if anything stops us from creating the component.
   */
  ConceptMap createConceptMap(URI uri, URI createURI, MIMEType type) throws ComponentException; 

  /** Saves a component.
   *  When save suceeded edited is set to false.
   *  Note that CORBA-components would probably not support save.
   *  
   *  @param comp the component to save.
   *  @exception ComponentException if anything stops us from saving the component.
   */
  void       saveComponent(Component comp) throws ComponentException;

  /** Adds a FormatHandler as a way to access components in specific formats.
   *  This function might be removed in future if other componentHandlers are added
   *  that work in other ways.
   *
   * @param fh the FormatHandler to add.
   */
    void addFormatHandler(FormatHandler f);

}

