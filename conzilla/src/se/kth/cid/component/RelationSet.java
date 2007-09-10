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

import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;

import java.util.*;

/** This class wraps around a Neuron containing a number of relations
 *  pointing to components. It is in essence,
 *  a set of components, usually in turn pointing to external resources.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class RelationSet
{
  /** The neuron containing the content set.
   */
  Neuron neuron;

  /** The Components contained as relations in the Neuron.
   */
  Component[] relations;

  /** The loader user to load the neurons.
   */
  ComponentStore store;


  /** Constructs a RelationSet from a given Neuron URI.
   *
   * @param uri the URI of the neuron containing the set.
   * @param loader the ComponentLoader used to retrieve the neurons.
   * @exception ComponentException if something goes wrong.
   */
  public RelationSet(URI uri, String kind, ComponentStore store)
    throws ComponentException
  {
    this.store = store;

    neuron = store.getAndReferenceNeuron(uri);
    
    Vector vrels = new Vector();

    MetaData.Relation[] rels = neuron.getMetaData().get_relation();
    if(rels != null)
      {
	for(int i = 0; i < rels.length; i++)
	  {
	    if(rels[i].kind != null && rels[i].kind.string.equals(kind)
	       && rels[i].resource_location != null)
	      {
		try{
		  URI reluri = URIClassifier.parseURI(rels[i].resource_location);
		  vrels.addElement(store.getAndReferenceComponent(reluri));
		} catch (MalformedURIException e)
		  {
		    Tracer.debug("Ignoring illegal relation location: "
				 + e.getURI() + ":\n " + e.getMessage());
		  }
		catch (ComponentException e)
		  {
		    Tracer.debug("Ignoring illegal component: \n "
				 + e.getMessage());
		  }
	      }
	  }
      }
    relations = (Component[]) vrels.toArray(new Component[vrels.size()]);
  }

  /** Returns the Components in this ContentSet.
   *
   * @return the Components in this ContentSet.
   */
  public Component[] getRelations()
  {
    return relations;
  }

  /** Returns the Neuron containing this ContentSet.
   *
   * @return the Neuron containing this ContentSet.
   */
  public Neuron getNeuron()
  {
    return neuron;
  }

  public void dereference() throws ComponentException
    {
      if(neuron == null)
	{
	  Tracer.trace("ContentSet was already dereferenced.",
		       Tracer.ERROR);
	  throw new IllegalStateException("ContentSet was already dereferenced.");
	}

    store.getCache().dereferenceComponent(neuron.getURI());
    for(int i = 0; i < relations.length; i++)
      store.getCache().dereferenceComponent(relations[i].getURI());
    
    neuron = null;
    relations = null;
  }
}
