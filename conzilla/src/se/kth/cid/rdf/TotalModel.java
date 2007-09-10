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
import se.kth.cid.component.*;
import se.kth.cid.component.rdf.*;
import se.kth.cid.component.local.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import java.util.*;
import java.beans.*;
import org.w3c.rdf.implementation.model.*;
import org.w3c.rdf.model.*;
import org.w3c.rdf.util.*;
import org.w3c.rdf.vocabulary.rdf_syntax_19990222.RDF;
import org.w3c.rdf.vocabulary.rdf_schema_200001.RDFS;


public class TotalModel {

  /** A mapping between the statement and the belonging reified resource.
   *  (I.e. if the reified statement exists.)

  Hashtable  reifiedStatements;
   */

    private Vector models;
    private Model union=null;
    
    private NodeFactory nodeFactory;
    private Vector listeners;
    
    public TotalModel(NodeFactory nodeFactory)
    {
	this.nodeFactory = nodeFactory;
	models = new Vector();
	listeners = new Vector();
    }

    public void addModel(ConzillaRDFModel  m)
    {
	union = null;
	models.add(m);
	firePropertyChangeEvent(new PropertyChangeEvent(this, "add", null, null));
    }

    public void removeModel(ConzillaRDFModel  m)
    {
	union = null;
	models.remove(m);
	firePropertyChangeEvent(new PropertyChangeEvent(this, "remove", null, null));
    }

    //FIXME: Use instead PropertyChangeSupport, neccessary with property listener?

    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
	listeners.add(pcl);
    }
    public boolean removePropertyChangeListener(PropertyChangeListener pcl)
    {
	return listeners.remove(pcl);
    }

    protected void firePropertyChangeEvent(PropertyChangeEvent e)
    {
	Iterator it = listeners.iterator();
	while (it.hasNext())
	    ((PropertyChangeListener) it.next()).propertyChange(e);
    }

    public Model getTotalRDFModel()
    {
	if (union != null)
	    return union;
	
	union = new ModelImpl();
	Iterator it = models.iterator();
	while (it.hasNext())
	    {
		Enumeration en = ((ConzillaRDFModel) it.next()).elements();
		while (en.hasMoreElements())
		    try { union.add((Statement) en.nextElement());} catch (ModelException me) {}
	    }
	/*	try {
	    System.out.println("Here comes everything");
	    RDFUtil.printStatements(union, System.out);
	    } catch (ModelException me) {}*/
	return union;
    }			       

    public ReifiedResourceComponent[] getReifiedResourceComponents(Resource subject)
    {
	Vector rrc = new Vector();
	Enumeration en = null;
	HashSet tot = new HashSet();

	Iterator it = models.iterator();
	while (it.hasNext())
	    tot.addAll(((ConzillaRDFModel) it.next()).
		       getReifiedResourceComponentCollection(subject));

	ReifiedResourceComponent [] rrcv = new ReifiedResourceComponent[tot.size()];
	tot.toArray(rrcv);
	return rrcv;
    }

    public Statement find1(Resource subject, Resource predicate, RDFNode object)
    {
	Iterator it = models.iterator();
	while (it.hasNext())
	    {
		try {
		    Model m=((Model) it.next()).find(subject, predicate, object);
		    if (!m.isEmpty())
			return RDFUtil.get1(m);
		} catch (ModelException me) {}
	    }
	
	return null;
    }


    //----------------Find reified ResourceComponents given a uri.-------
    public ReifiedResourceComponent getReifiedResourceComponent(String uri)
    {
	try {
	    ReifiedResourceComponent ref = null;
	    Iterator it = models.iterator();
	    while (it.hasNext())
		if ((ref=((ConzillaRDFModel) it.next()).getReifiedResourceComponent(uri)) != null)
		    return ref;
	    
	    Resource re = nodeFactory.createResource(uri);
	    Statement st = getReifiedStatement(re);
	    
	    it = models.iterator();
	    while (it.hasNext() && ref==null)
		{
		    ConzillaRDFModel m = (ConzillaRDFModel) it.next();
		    ref = m.createReifiedResourceComponent(re, st);
		}
	    return ref;
	} catch (ModelException me)
	    {}
	return null;
    }
    

    public Statement getReifiedStatement(Resource r)
    {
	try {
	    if (find1(r, RDF.type, RDF.Statement) == null)
		return null;
	    RDFNode subject   = find1(r, RDF.subject, null).object();
	    RDFNode predicate = find1(r, RDF.predicate, null);
	    RDFNode object    = find1(r, RDF.object, null);
	    
	    if (subject !=null && subject instanceof Resource && predicate != null  
		&& predicate instanceof Resource && object != null)
		return this.find1((Resource) subject, (Resource) predicate, object);
	} catch (ModelException me) {
	    Tracer.debug("ModelException!!!");
	}
	return null;
    }
}


    /*
    public Resource getReification(Statement statement)
    {
	if (reifiedStatements==null)
	    hashReifications();

	return (Resource) reifiedStatements.get(statement);
    }


    public void hashReifications()
    {
	reifiedStatements = new Hashtable();
	Enumeration en=null;
	try {
	    en=RDFUtil.getResources(find(null, RDF.type, RDF.Statement)).elements(); 
	} catch (ModelException me)
	    {
		return;
	    }
	while (en.hasMoreElements())
	    {
		Resource re = (Resource) en.nextElement();
		Statement st = getReifiedStatement(re);
		if (st != null)
		    reifiedStatements.put(st, re);
	    }
    }

    public Model find(Resource subject, Resource predicate, RDFNode object)
    {
	Model tot = new ModelImpl();
	Iterator it = models.iterator();
	while (it.hasNext())
	    tot = SetOperations.union(tot, ((Model) it.next()).find(subject, predicat, object));
	return tot;
    }


    */
