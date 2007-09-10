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
import se.kth.cid.component.cache.*;
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
    private ComponentCache cache;
    private Vector listeners;
    
    public TotalModel(NodeFactory nodeFactory, ComponentCache cache)
    {
	this.cache = cache;
	this.nodeFactory = nodeFactory;
	models = new Vector();
	listeners = new Vector();
    }

    public void addModel(ConzillaRDFModel  m)
    {
	union = null;
	models.add(m);
	
	try {
	    Iterator it = RDFUtil.getResources(m).values().iterator();
	    while (it.hasNext())
		{
		    Resource res = (Resource) it.next();
		    ResourceComponent rc = findComponent(res.getURI());
		    if (rc != null)
			rc.addModel(m);
		}
	} catch (ModelException me)
	    {}

	firePropertyChangeEvent(new PropertyChangeEvent(this, "add", null, null));
    }

    public void removeModel(ConzillaRDFModel  m)
    {
	union = null;
	models.remove(m);

	try {
	    Iterator it = RDFUtil.getResources(m).values().iterator();
	    while (it.hasNext())
		{
		    Resource res = (Resource) it.next();
		    ResourceComponent rc = findComponent(res.getURI());
		    if (rc != null)
			rc.removeModel(m);
		}
	} catch (ModelException me)
	    {}

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
	    {
		PropertyChangeListener phl = (PropertyChangeListener) it.next();
		Tracer.debug("fireing to "+phl.toString());
		phl.propertyChange(e);
	    }
	Tracer.debug("done firing");
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

    public Model find1Model(Resource subject, Resource predicate, RDFNode object)
    {
	Iterator it = models.iterator();
	while (it.hasNext())
	    {
		try {
		    Model m=((Model) it.next()).find(subject, predicate, object);
		    if (!m.isEmpty())
			return m;
		} catch (ModelException me) {}
	    }
	return null;
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

    public ResourceComponent findComponent(String suri)
    {
	try {
	    URI uri = URIClassifier.parseURI(suri);
	    return findComponent(uri);
	} catch (MalformedURIException mue)
	    {
		return null;
	    }
    }

    public ResourceComponent findComponent(URI uri)
    {
      Component comp;
      comp = cache.getComponent(uri.toString());

      if (!(comp instanceof ResourceComponent))
	  return null;
      return (ResourceComponent) comp;
    }

    //----------------Find reified ResourceComponents given a uri.-------
    /** @see #getReifiedResourceComponent(Resource)
     */
    public ReifiedResourceComponent getReifiedResourceComponent(String uri)
    {
	ReifiedResourceComponent ref =null;
	
	Iterator it = models.iterator();
	while (it.hasNext() && ref==null)
	    {
		ConzillaRDFModel m = (ConzillaRDFModel) it.next();
		ref = m.getReifiedResourceComponent(uri);
	    }
	return ref;
    }

    /** Delegates possible wrapping of reifications to individual models, i.e.
     *	a model that contains the four triples type, subject, predicate and object.
     * 
     *  @param statement a resource that hopefully is described as a statement in some model.
     *  @return a ReifiedResourceComponent of the resource.
     */
    public ReifiedResourceComponent getReifiedResourceComponent(Resource statement)
    {
	ReifiedResourceComponent ref =null;
	
	Iterator it = models.iterator();
	while (it.hasNext() && ref==null)
	    {
		ConzillaRDFModel m = (ConzillaRDFModel) it.next();
		ref = m.getReifiedResourceComponent(statement);
	    }
	return ref;
    }
    
    /** Finds the triple (instance of the class  {@link Statement Statement}, which is a bad name
     *  for that class, it should be called Triple instead) represented by the given resource.
     *  This works only if the resource is a reification, i.e. in some model there is four triples, 
     *  the first says that the given resource is of type Statement and the remaining three points 
     *  to the subject, predicate and object resources of the wanted triple.
     *
     *  Obs! A reification might talk about a triple that doesn't exist in any model.
     *  Hence a negative response doesn't imply that the resource isn't a valid Statement.
     *  
     *  @param r the resource that should be a reification (i.e. of the type Statement etc.)
     *  @return a triple, i.e. an instance of the class Statement, null may be returned if
     *          the resource doesn't represent a valid reification or if no triple specified 
     *          by the reification is expressed in any of the models.
     */
    public Statement getTripleFromReification(Resource r)
    {
	try {
	    Model m = find1Model(r, RDF.type, RDF.Statement);
	    if ( m == null)
		return null;
	    RDFNode subject   = RDFUtil.getObjectResource(m, r, RDF.subject);
	    RDFNode predicate = RDFUtil.getObjectResource(m, r, RDF.predicate);
	    RDFNode object    = RDFUtil.getObject(m, r, RDF.object);
	    
	    if (subject !=null && subject instanceof Resource && predicate != null  
		&& predicate instanceof Resource && object != null)
		return this.find1((Resource) subject, (Resource) predicate, object);
	} catch (ModelException me) {
	    Tracer.debug("ModelException!!!");
	}
	return null;
    }
}
