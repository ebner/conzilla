/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;

import se.kth.cid.component.Container;
import se.kth.cid.notions.ContentInformation;
import se.kth.cid.notions.Context;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFComponent;
import se.kth.cid.rdf.RDFModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This class represents the case when we have a content on a concept in a context.
 * This is represented via a reification in the layout-container and pointed to from the contextmap.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class ContentInContext extends RDFComponent implements ContentInformation{

    RDFConceptMap cMap;
    String conceptURI;
    String contentURI;
    String conceptToContentRelation;

    /**
     * 
     * @param uri is the 
     * @param context the contextMap where the relation is valid.
     * @param conceptURI the uri of the concept the content is added on.
     * @param ccRelation the uri of the property used as relation between the concept and the content.
     * @param contentURI
     */
    protected ContentInContext(
    		URI uri,
    		RDFConceptMap contextMap,
    		String conceptURI,
    		String ccRelation,
    		String contentURI) {
        super(uri);
        cMap = contextMap;
        this.conceptURI = conceptURI;
        this.conceptToContentRelation = ccRelation;
        this.contentURI = contentURI;
        
        if (conceptToContentRelation == null) {
            this.conceptToContentRelation = CV.contains.toString();
        }
    }
    
    
    /**
     * @see ContentInformation#getContext()
     */
    public Context getContext() {
        return cMap;
    }

    /**
     * @see ContentInformation#getConceptToContentRelation()
     */
    public String getConceptToContentRelation() {
        return conceptToContentRelation;
    }

    /**
     * @see ContentInformation#getContentURI()
     */
    public String getContentURI() {
        return contentURI;
    }

    /**
     * @see ContentInformation#getConceptURI()
     */
    public String getConceptURI() {
        return conceptURI;
    }
    
    
    
    protected void initializeInModel(RDFModel model) {
		super.initializeInModel(model);
		
        //Lets get jena-resources of everything we need.
        Resource contentReification = model.createResource(getURI());        
        Resource conceptResource = model.getResource(conceptURI);
        Resource contentResource = model.getResource(contentURI);
        Resource contextMapResource = cMap.getResource();
        Property relation = model.getProperty(conceptToContentRelation);

        //The includes relation from the contextmap to the reification
        model.add(contextMapResource, CV.includes, contentReification);

        //The reification of the relation between the concept and the content.
        contentReification.addProperty(RDF.type, CV.ContentInContext);
        model.add(contentReification, RDF.subject, conceptResource);
        model.add(contentReification, RDF.predicate, relation);
        model.add(contentReification, RDF.object, contentResource);
        model.setEdited(true);
        //We have now edited this content on concept in context reification
        setEdited(true);
        //And also edited the contextmap since we have added an includes relation.
        cMap.setEdited(true);           
    }
    
    /**
     * @see RDFResource#initUpdate()
     */
    protected void initUpdate() {
        super.initUpdate();
        
        Model model = getResource().getModel();
        
        Resource contentReification = model.getResource(getURI());
        conceptToContentRelation = contentReification.getProperty(RDF.predicate).getObject().toString();
        conceptURI = contentReification.getProperty(RDF.subject).getObject().toString();
        contentURI = contentReification.getProperty(RDF.object).getObject().toString();
    }
    
    
    /**
     * @see RDFResource#removeFromModel(se.kth.cid.rdf.RDFModel)
     */
    protected void removeFromModel(RDFModel model) {
        //Lets get jena-resources of everything we need.
        Resource contentReification = model.createResource(getURI());        
        Resource contextMapResource = cMap.getResource();
        
        //Remove the reification and its inclusion.
        contentReification.removeProperties();
        model.remove(model.createStatement(contextMapResource, CV.includes, contentReification));
        
        //The contextMap is now edited.
        cMap.setEdited(true);
        
        super.removeFromModel(model);
    }

    public Container getContainer() {
        return getLoadModel();
    }

    public String toString() {
        return getContentURI();
    }
}