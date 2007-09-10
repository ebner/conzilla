/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.notions;

import java.util.Set;

import se.kth.cid.component.Component;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;

/**
 * TODO: Description
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public interface Context extends Component{
    ContentInformation addContentInContext(String conceptURI, String contentAttribute, String contentURI) 
            throws ReadOnlyException, InvalidURIException;
    void removeContentInContext(ContentInformation cc)
            throws ReadOnlyException, InvalidURIException;
            
    /**
     * 
     * @param conceptURI
     * @return a set of ContentInformations, never null.
     */
    Set getContentInContextForConcept(String conceptURI);
}
