/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.concept;

import java.util.Set;

import se.kth.cid.component.ContainerManager;

/** This is the interface representing Concepts.
 *
 *  @author Mikael Nilsson
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface Concept extends Statement {
    int FIRST_CONCEPT_EDIT_CONSTANT = LAST_COMPONENT_EDIT_CONSTANT + 1;
    int TRIPLE_ADDED = FIRST_CONCEPT_EDIT_CONSTANT + 1;
    int TRIPLE_REMOVED = FIRST_CONCEPT_EDIT_CONSTANT + 2;

    int LAST_CONCEPT_ONLY_EDIT_CONSTANT = TRIPLE_REMOVED;
    int LAST_CONCEPT_EDIT_CONSTANT = Triple.LAST_TRIPLE_EDIT_CONSTANT;

    int REFERRENCED_MULTIPLE_TIMES = -1;
    int UNKOWN_REFERRENCED = -2;
    
    /**
     * Returns a number indicating the number of times it is referrenced or
     * a negative value according:
     * <ul><li>{@link #REFERRENCED_MULTIPLE_TIMES} - when it is referenced 
     * more than one time but unknown exactly how many times.</li>
     * <li>{@link #UNKOWN_REFERRENCED} when it is unknown how many times it
     * is referrenced if at all.</li></ul>
     * @deprecated use {@link ContainerManager#isComponentReferredTo(se.kth.cid.component.Component)} instead.
     * 
     * @return a number indicating the number of times it is referenced. 
     */
    int isReferredTo();

    /**
     * 
     * 
     * @return a set of ContentInformations, never null.
     */
    Set getContentInformation();
}
