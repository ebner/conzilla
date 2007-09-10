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


/** ComponentLoader are used to locate and connect with/retrieve
 *  different types of components.
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
   *  This functionality could be removed from here if someone took
   *  responsibility
   *  for loading the neurons in a conceptmap. Could be the right thing to do.
   *  
   *  @param uri URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if anything goes wrong when loading the component.
   */
  Component loadComponent(URI uri) throws ComponentException;
  

  
  //  boolean isSavable(URI uri) throws ComponentException;
    
  //  boolean existsComponent(URI uri) throws ComponentException;

  /** Tests if a component with this URI can be created.
   *  This is a possibly heavy-weight operation.
   *
   *  @return false if there already was a component with this URI.
   *          true if everything should work. Otherwise exception
   */
  boolean    canCreateComponent(URI uri) throws ComponentException;

  Component  createComponent(URI uri) throws ComponentException; 
  Neuron     createNeuron(URI uri, URI type) throws ComponentException;
  NeuronType createNeuronType(URI uri) throws ComponentException;
  ConceptMap createConceptMap(URI uri) throws ComponentException; 
  
  void       saveComponent(Component comp) throws ComponentException;
}

