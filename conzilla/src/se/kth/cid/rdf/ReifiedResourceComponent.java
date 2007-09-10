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


package se.kth.cid.rdf;

import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import org.w3c.rdf.util.*;
import org.w3c.rdf.model.*;
import org.w3c.rdf.implementation.model.StatementImpl;
import se.kth.cid.neuron.Axon;

/** A class for reified statements combined with an implementation of
 *  the Axon interface.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ReifiedResourceComponent extends ResourceComponent implements Axon, Statement
{
    Statement statement;

    public ReifiedResourceComponent(TotalModel totalModel, ConzillaRDFModel model, Resource resource, Statement statement, URI uri)
    {
	super(totalModel, model, resource, uri);
	this.statement = statement;
    }

    /** Returns the ID of this Statement. I.e. the same as localname????
     *
     *  @return the fragment.
     */
    public String getID()
    {
	try {
	    return resource.getLocalName();
	} catch (ModelException me) {
	    Tracer.bug("Couldn't get the localname from a resource.");
	    return "";
	}
    }
    
    /** Returns the type of this statement. 
     *
     *  @return a URI to a resource with type information tied to it.
     */
    public String predicateURI()
    {
	try {
	    return statement.predicate().getURI();
	} catch (ModelException me)
	    {}
	return ConzillaRDFModel.S_RDF+"Property";
    }

    public String getType()
    {
	return predicateURI();
    }

    public Resource predicate()
    {
	try {
	    return statement.predicate();
	} catch (ModelException me) {
	    Tracer.bug("Couldn't get the predicate from the statement within a verifyed reified resource.");
	    return null;
	}
    }

  /** If the object of the statement is a RDF-Resource the URI 
   *  for it is returned, otherwise null is returned.
   *
   *  @return the URI of the pointed-to RDF-resource.
   */
  public String objectURI()
    {
	try {
	    if (statement.object() instanceof Resource)
		return ((Resource) statement.object()).getURI();
	} catch (ModelException me) {}
	return null;
    }
    
  public RDFNode object()
    {
	try {
	return statement.object();
	} catch (ModelException me) {
	    Tracer.bug("Couldn't get the object from the statement within a verifyed reified resource.");
	    return null;
	}
    }

  /** Returns the subjects URI.
   *
   *  @return an URI.
   */
  public String subjectURI()
    {
	try {
	    return statement.subject().getURI();
	} catch (ModelException me) {}
	return "";
    }
    
  public Resource subject()
    {
	try {
	return statement.subject();
	} catch (ModelException me) {
	    Tracer.bug("Couldn't get the subject from the statement within a verifyed reified resource.");
	    return null;
	}
    }
	
    //Functions to fulfill the statement interface.
    //Maybe it's not neccessary to wrap the statement functions... (it's nice though.)

    public java.lang.String getLocalName()
	throws ModelException
    {
	return statement.getLocalName();
    }

    public java.lang.String getNamespace()
	throws ModelException
    {
	return statement.getNamespace();
    }

    public java.lang.String getLabel()
	throws ModelException
    {
	return statement.getLabel();
    }
}
