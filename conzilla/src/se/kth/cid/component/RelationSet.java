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
  /** The component containing the relation set.
   */
  Component component;

  /** The Components contained as relations in the Component.
   */
  Component[] relations;

  /** The loader user to load the components.
   */
  ComponentStore store;

  /**As the next constructor but without the component loaded.
   * 
   * @exception ComponentException if something goes wrong.
   */
  public RelationSet(URI uri, String kind, ComponentStore store)
    throws ComponentException
  {
    this(store.getAndReferenceComponent(uri), kind, store);
  }



  /** Constructs a RelationSet from a given Component URI.
   *
   * @param uri the URI of the component containing the set.
   * @param kind the value of the kind elemtent for the wanted relations.
   * @param store the ComponentStore used to retrieve the components.
   */
  public RelationSet(Component comp,String kind, ComponentStore store)
  {
    Vector vrels = new Vector();
    component=comp;
    
    MetaData.Relation[] rels = component.getMetaData().get_relation();
    if(rels != null)
      {
	for(int i = 0; i < rels.length; i++)
	  {
	    if(rels[i].kind != null && rels[i].kind.string.equals(kind)
	       && rels[i].resource_location != null)
	      {
		try{
		  URI reluri = URIClassifier.parseURI(rels[i].resource_location,
						      URIClassifier.parseValidURI(comp.getURI()));
		  vrels.addElement(store.getAndReferenceComponent(reluri));
		} catch (MalformedURIException e)
		  {
		    Tracer.trace("Ignoring illegal relation location: "
				 + e.getURI() + ":\n " + e.getMessage(), Tracer.DETAIL);
		  }
		catch (ComponentException e)
		  {
		    Tracer.trace("Ignoring illegal component: \n "
				 + e.getMessage(), Tracer.DETAIL);
		  }
	      }
	  }
      }
    relations = (Component[]) vrels.toArray(new Component[vrels.size()]);
  }

  /** Returns the Components in this RelationSet.
   *
   * @return the Components in this RelationSet.
   */
  public Component[] getRelations()
  {
    return relations;
  }

  /** Returns the Component containing this ContentSet.
   *
   * @return the Component containing this ContentSet.
   */
  public Component getComponent()
  {
    return component;
  }

}
