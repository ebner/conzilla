/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.concept.generic;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.FiringResourceImpl;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.concept.Concept;
import se.kth.cid.concept.Triple;
import se.kth.cid.identity.MIMEType;

/** 
 * An implementation of Concept to be used for components downloaded
 * over the web.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class MemConcept extends FiringResourceImpl implements Concept {
    URI conceptType;

    Triple triple;
    Vector conceptvec;
    Vector conceptkeys;

    HashSet fragments;
    // Cache
    Concept[] concepts;

    public MemConcept(
        URI conceptURI,
        URI loadURI,
        MIMEType loadType,
        URI conceptType) {
        super(conceptURI, loadURI, loadType);

        this.conceptType = conceptType;

        //      hconcepts = new Hashtable();  
        conceptvec = new Vector();
        conceptkeys = new Vector();

        fragments = new HashSet();
    }

    public String getLoadContainer() {
        return getLoadURI();
    }

    public Collection getRelevantContainers() {
        Vector v = new Vector();
        v.add(getLoadContainer());
        return v;
    }

    public String getType() {
        return conceptType.toString();
    }

    public List getAttributeEntry(String attribute) {
        return null;
    }
    
    public AttributeEntry getAttributeEntry(String attribute, String value, Boolean isValueAURI, String containerURI) {
        return null;
    }

    public AttributeEntry addAttributeEntry(String attribute, Object value) {
        return null;
    }

    public void changeAttributeValue(
        String attribute,
        Object oldvalue,
        Object newvalue) {
    }

    public void removeAttributeEntry(AttributeEntry ae) {
    }

    /** 
     * Removes metadata, type information etc....
     */
    public void remove() {
    }

    public Triple getTriple() {
        return triple;
    }

    public void createTriple(
        String subjectURI,
        String predicateURI,
        String objectURI,
        boolean isLiteral)
        throws ReadOnlyException {
        this.triple =
            new MemTriple(subjectURI, predicateURI, objectURI, isLiteral);
    }

   
    protected void avoidFragmentFromURI(String uri) {
        try {
            String fragment = new URI(uri).getFragment();
            if (fragment != null && fragment.length() > 0)
                fragments.add(fragment);
        } catch (URISyntaxException mu) {
        }
    }

    public int isReferredTo() {
        //In the case when a concept and the container coincides the concept shall never be removed...
        //Instead the entire container should be removed.
        //This function is used to determine if the concept should be removed and
        //even if this is an independent memory implementation I've choosen the default
        //to be that concepts always are referred. If bette memorymanagment is needed
        //a MemContainerManager should solve this.
        return UNKOWN_REFERRENCED;
    }

    /**
     * @see se.kth.cid.concept.Statement#getTripleRepresentationType()
     */
    public String getTripleRepresentationType() {
        return null;
    }

    /**
     * @see se.kth.cid.concept.Concept#getContentInformation()
     */
    public Set getContentInformation() {
        return null;
    }

	public ComponentManager getComponentManager() {
		// TODO Auto-generated method stub
		return null;
	}
    
}