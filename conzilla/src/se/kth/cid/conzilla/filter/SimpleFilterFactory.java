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


package se.kth.cid.conzilla.filter;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.conceptmap.ConceptMap;

/** This is a class for creating a new Filter
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class SimpleFilterFactory implements FilterFactory
{
    Filter cachedFilter;
    URI cachedURI;
    public SimpleFilterFactory()
    {
	cachedFilter=null;
	cachedURI=null;
    }

    public void refresh()
    {
      cachedURI=null;
      cachedFilter=null;
    }

    public Filter createFilter(MapController cont, Neuron neuron, ConceptMap conceptMap)
    {
	if (neuron != null)
	    {
		Component [] filters=(new RelationSet(neuron, "filter", 
						      cont.getConzillaKit().getComponentStore())).getRelations();
		
		if (filters!=null && filters.length>0)
		    {
			try {
			    return createFilter(cont, URIClassifier.parseValidURI(filters[0].getURI()));
			} catch (FilterException fe)
			    {
				Tracer.debug("Malformed filter in neuron "+neuron.getURI().toString()+
					     "\n"+fe.getMessage());
			    }
		    }
	    }
     
	if (conceptMap != null)
	    {
		Component [] filters=(new RelationSet(conceptMap, "filter", 
					 cont.getConzillaKit().getComponentStore())).getRelations();
		
		if (filters!=null && filters.length>0)
		    {
			try {
			    return createFilter(cont, URIClassifier.parseValidURI(filters[0].getURI()));
			} catch (FilterException fe)
			    {
				Tracer.debug("Malformed filter in neuron "+neuron.getURI().toString()+
					     "\n"+fe.getMessage());
			    }
		    }
	    }
      return null;
    }
    protected Filter createFilter(MapController cont, URI uri) throws FilterException
    {
	if (cachedURI!=null && uri.equals(cachedURI))
	    return cachedFilter; 
	else
	    {
		cachedURI = uri;
		cachedFilter = new ConcreteFilter(cont, uri);
		return cachedFilter;
	    }
    }
}
