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

import se.kth.cid.component.cache.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;

/** ComponentStore is the central place for component handling and caching.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ComponentStore
{
  /** The handler to use.
   */
  ComponentHandler handler;

  /** The cache to use.
   */
  ComponentCache   cache;

  /** Creates a ComponentStore with the given handler and cache.
   *
   *  @param handler the handler to use.
   *  @param cache the cache to use..
   */
  public ComponentStore(ComponentHandler handler, ComponentCache cache)
    {
      this.cache = cache;
      this.handler = handler;
    }

  /** Returns the cache of this ComponentStore.
   *
   *  @return the cache of this ComponentStore.
   */
  public ComponentCache getCache()
    {
      return cache;
    }

  /** Returns the handler of this ComponentStore.
   *
   *  @return the handler of this ComponentStore.
   */
  public ComponentHandler getHandler()
    {
      return handler;
    }
  
  
  /** Loads a Component, referencing it in the cache.
   *  If the component already is in the cache, that component is returned.
   *
   *  @param uri the URI of the component to load.
   *  @param c the class the component must be of.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if the component
   *             could not be loaded, or was not of type c.
   */
  Component getAndReferenceComponentImpl(URI uri, Class c) throws ComponentException
    {

      Component comp;
      comp = cache.getComponent(uri.toString());
      
      if(comp == null)
	{
	  Tracer.trace("Cache miss for " + uri, Tracer.MAJOR_INT_EVENT);
	  if (Container.class.isAssignableFrom(c))
	      comp = handler.loadContainer(uri);
	  else if (!uri.getFragment().equals("")) //this case should be removed when RDF conceptmaps works.
	      {
		  URI base = URIClassifier.parseValidURI(uri.getBase());
		  Container container = (Container) cache.getComponent(base.toString());
		  if (container == null)
		      {
			  container = handler.loadContainer(base);
			  cache.referenceComponent(container);
		      }
      
		  comp = handler.loadComponent(uri, container);
	      }
	  else
	      comp = handler.loadComponent(uri, null);
	}
      
      if(! (c.isInstance(comp)))
	{
	  int lastIndex = c.getName().lastIndexOf('.');
	  if(lastIndex == -1)
	    lastIndex = 0;
	  throw new ComponentException("Component was no " + c.getName().substring(lastIndex) + ": '"+ uri + "'.");
	}

      cache.referenceComponent(comp);

      return comp;
    }
  

  /** Loads a Neuron, referencing it in the cache.
   *  If the component already is in the cache, that component is returned.
   *
   *  @param uri the URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if the component
   *             could not be loaded, or was no neuron.
   */
  public Neuron getAndReferenceNeuron(URI uri)
    throws ComponentException
    {
      return (Neuron) getAndReferenceComponentImpl(uri, Neuron.class);
    }

  /** Loads a NeuronType, referencing it in the cache.
   *  If the NeuronType already is in the cache, that NeuronType is returned.
   *
   *  @param uri the URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if the component
   *             could not be loaded, or was no NeuronType.
   */
  public NeuronType getAndReferenceNeuronType(URI uri)
    throws ComponentException
    {
      return (NeuronType) getAndReferenceComponentImpl(uri, NeuronType.class);
    }
  
  /** Loads a ConceptMap, referencing it in the cache.
   *  If the ConceptMap already is in the cache, that ConceptMap is returned.
   *
   *  @param uri the URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if the component
   *             could not be loaded, or was no ConceptMap.
   */
  public ConceptMap getAndReferenceConceptMap(URI uri)
    throws ComponentException
    {
      return (ConceptMap) getAndReferenceComponentImpl(uri, ConceptMap.class);
    }

  /** Loads any Component, referencing it in the cache.
   *  If the Component already is in the cache, that Component is returned.
   *
   *  @param uri the URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if the component
   *             could not be loaded.
   */
  public Component getAndReferenceComponent(URI uri)
    throws ComponentException
    {
      return getAndReferenceComponentImpl(uri, Component.class);
    }

  /** Loads any ContainerComponent, referencing it in the cache.
   *  If the container already is in cache, that container is returned.
   *
   *  @param uri the URI of the component to load.
   *  @return the loaded component. Never null.
   *  @exception ComponentException if the component
   *             could not be loaded.
   */
  public Container getAndReferenceContainer(URI uri)
    throws ComponentException
    {
      return (Container) getAndReferenceComponentImpl(uri, Container.class);
    }
}

