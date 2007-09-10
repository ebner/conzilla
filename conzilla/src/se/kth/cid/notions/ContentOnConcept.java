/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.notions;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Container;
import se.kth.cid.concept.Concept;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class ContentOnConcept
    implements ContentInformation {
    
    AttributeEntry entry;
    Concept concept;
    
    public ContentOnConcept(Concept concept, AttributeEntry entry) {
        this.concept = concept;
        this.entry = entry;
    }

    /**
     * @see se.kth.cid.notions.ContentInformation#getContext()
     */
    public Context getContext() {
        return null;
    }

    /**
     * @see se.kth.cid.notions.ContentInformation#getConceptURI()
     */
    public String getConceptURI() {
        return concept.getURI();
    }

    /**
     * @see se.kth.cid.notions.ContentInformation#getConceptToContentRelation()
     */
    public String getConceptToContentRelation() {
        return entry.getAttribute();
    }

    /**
     * @see se.kth.cid.notions.ContentInformation#getContentURI()
     */
    public String getContentURI() {
        return entry.getValue();
    }

    /* (non-Javadoc)
     * @see se.kth.cid.notions.ContentInformation#getContainer()
     */
    public Container getContainer() {
        return entry.getContainer();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getContentURI();
    }

}
