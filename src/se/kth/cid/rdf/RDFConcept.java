/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.concept.Concept;
import se.kth.cid.concept.Triple;
import se.kth.cid.notions.ContentOnConcept;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

/** This class wraps RDF-resources into concepts.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFConcept extends RDFComponent implements Concept, Triple {
	
	Log log = LogFactory.getLog(RDFConcept.class);
	
    Statement statement;

    public RDFConcept(URI uri) {
    	super(uri);
    	statement = null;
    }
    
    public void remove() {
        super.remove();
        createTriple(null, null, null, false);
    }

    public void createTriple(
        String subjectURI,
        String predicateURI,
        String objectValue, 
        boolean isLiteral) {
        
    	//Check for existing statements/reifications??
    	//should not be neccessary when this function only is called once.
    	if (statement != null) {
    		RDFModel oldm = (RDFModel) statement.getModel();
    		if (oldm != null) {
    			Statement s =
    				oldm.createStatement(
    						getResource(),
    						RDF.subject,
    						statement.getSubject());
    			Statement p =
    				oldm.createStatement(
    						getResource(),
    						RDF.predicate,
    						statement.getPredicate());
    			Statement o =
    				oldm.createStatement(
    						getResource(),
    						RDF.object,
    						statement.getObject());
    			Statement t =
    				oldm.createStatement(
    						getResource(),
    						RDF.type,
    						oldm.createProperty(((RDFContainerManager) rcm.getContainerManager()).getRelationType()));
    			//RDF.Statement
    			if (oldm.contains(statement))
    				statement.remove();

    			s.remove();
    			p.remove();
    			o.remove();
    			t.remove();
    			oldm.setEdited(true);
    			setEdited(true);
    		}
    	}

    	if (subjectURI != null
    			&& predicateURI != null
    			&& objectValue != null) {
    		RDFModel m = (RDFModel) rcm.getCurrentConceptContainer();

    		Resource subject = m.getResource(subjectURI);
    		Property predicate = m.getProperty(predicateURI);
    		RDFNode object;
    		if (isLiteral)
    			object = m.createLiteral(objectValue);
    		else
    			object = m.getResource(objectValue);
    		//only resources....	   

    		//new statement in the current model.
    		statement = m.createStatement(subject, predicate, object);

    		if (((RDFContainerManager) rcm.getContainerManager()).reflectToTriple())
    			if (!m.contains(statement))
    				m.add(statement);

    		Statement s =
    			m.createStatement(
    					getResource(),
    					RDF.subject,
    					statement.getSubject());
    		if (!m.contains(s))
    			m.add(s);

    		Statement p =
    			m.createStatement(
    					getResource(),
    					RDF.predicate,
    					statement.getPredicate());
    		if (!m.contains(p))
    			m.add(p);

    		Statement o =
    			m.createStatement(
    					getResource(),
    					RDF.object,
    					statement.getObject());
    		if (!m.contains(o))
    			m.add(o);

    		Statement t =
    			m.createStatement(
    					getResource(),
    					RDF.type,
    					m.createProperty(((RDFContainerManager) rcm.getContainerManager()).getRelationType()));
    		//RDF.Statement
    		if (!m.contains(t))
    			m.add(t);
    		m.setEdited(true);
    		setEdited(true);
    	}
    }

    public Triple getTriple() {
        if (statement != null)
            return this;
        return null;
    }

    public String getTripleRepresentationType() {
        String rt = getType();
        if (rt.equals(RDF.Statement.toString()))
            return null;
        else
            return rt;
    }

    protected void initUpdate() {
    	super.initUpdate();
        RDFModel model = getLoadModel();
        String uri = getURI();
        if (model != null && model.isReification(uri))
            statement = model.getReification(uri);
    }

    //From Triple interface.....

    public String subjectURI() {
        if (statement != null)
            return statement.getSubject().getURI();
        return null;
    }

    public String predicateURI() {
        if (statement != null)
            return statement.getPredicate().getURI();
        return null;
    }
    
    public void setPredicateURI(String predicate) {
        createTriple(statement.getSubject().toString(),
                predicate,
                statement.getObject().toString(),
                isObjectLiteral());
    }

    public String objectValue() {
        if (statement != null)
            return statement.getObject().toString();
        return null;
    }

    public void setObjectValue(String value) {
        if (statement == null || value == null || value.length() == 0)
            return;

        RDFNode object = statement.getObject();
        try {
          	Model m = getLoadModel();
          	Resource re = getResource();
          	Statement reifObj = m.createStatement(re, RDF.object, object);
          	m.remove(reifObj);
            if (object instanceof Resource)
                object = statement.getModel().getResource(value);
            else
                object = statement.getModel().createLiteral(value);
            reifObj = m.createStatement(re, RDF.object, object);
            m.add(reifObj);
            statement = statement.changeObject(object);
        } catch (Exception re) {
           log.error("Failed to set object on statement", re);
        }
    }

    public boolean isObjectLiteral() {
        if (statement != null)
            return statement.getObject() instanceof Literal;
        return false;
    }

    public int isReferredTo() {
    	return rcm.getContainerManager().isComponentReferredTo(this);
    }

    /**
     * @see se.kth.cid.concept.Concept#getContentInformation()
     */
    public Set getContentInformation() {
        HashSet set = new HashSet();
        List attributes = getAttributeEntry(CV.contains.toString());                
        for (Iterator attributesIt = attributes.iterator(); attributesIt.hasNext();) {
            AttributeEntry attributeEntry = (AttributeEntry) attributesIt.next();
            set.add(new ContentOnConcept(this, attributeEntry));
        }
        return set;
    }
}
