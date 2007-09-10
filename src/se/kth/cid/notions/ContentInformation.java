/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.notions;

import se.kth.cid.component.Container;


/**
 * Content are related to concepts either globally or within a specific context, 
 * i.e. within a specific ContextMap.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public interface ContentInformation {
    
    /**
     * @return a Context, null if the relation is global.
     */
    Context getContext();
    
    /**
     * @return uri of the concept the content is linked from.
     */
    String getConceptURI();
    
    /**
     * @return a String represenation of the kind of relation that
     * connects the content and concept (independent of if the relation 
     * it is expressed in a context or not).
     */
    String getConceptToContentRelation();
    
    /**
     * @return uri of the content linked to.
     */
    String getContentURI();
    
    /**
     * @return the Container where the relation is expressed.
     */
    Container getContainer();
}
