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

public class ComponentStore
{

  ComponentHandler handler;
  ComponentCache   cache;
  
  public ComponentStore(ComponentHandler handler, ComponentCache cache)
    {
      this.cache = cache;
      this.handler = handler;
    }

  public ComponentCache getCache()
    {
      return cache;
    }

  public ComponentHandler getHandler()
    {
      return handler;
    }

  public Neuron getAndReferenceNeuron(URI neuron)
    throws ComponentException
    {
      Tracer.debug("Trying to ref " + neuron);
      Component comp;
      comp = cache.getComponent(neuron.toString());

      if(comp == null)
	comp = handler.loadComponent(neuron);

      if(! (comp instanceof Neuron))
	throw new ComponentException("Component was no Neuron: '" + neuron + "'.");

      cache.referenceComponent(comp);

      return (Neuron) comp;
    }

  public NeuronType getAndReferenceNeuronType(URI neuronType)
    throws ComponentException
    {
      Component comp;
      comp = cache.getComponent(neuronType.toString());

      if(comp == null)
	comp = handler.loadComponent(neuronType);

      if(! (comp instanceof NeuronType))
	throw new ComponentException("Component was no NeuronType: '"
				 + neuronType + "'.");

      cache.referenceComponent(comp);
      
      return (NeuronType) comp;
    }
  
  public ConceptMap getAndReferenceConceptMap(URI mapURI)
    throws ComponentException
    {
      Component comp;
      comp = cache.getComponent(mapURI.toString());
      
      if(comp == null)
	comp = handler.loadComponent(mapURI);
      
      if(! (comp instanceof ConceptMap))
	throw new ComponentException("Component was no ConceptMap: '"
				       + mapURI + "'.");
      
      cache.referenceComponent(comp);
      return (ConceptMap) comp;
    }

  public Component getAndReferenceComponent(URI compURI)
    throws ComponentException
    {
      Component comp;
      comp = cache.getComponent(compURI.toString());
      
      if(comp == null)
	comp = handler.loadComponent(compURI);
      
      cache.referenceComponent(comp);
      return comp;
    }
}

