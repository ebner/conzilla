/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.session;

import java.util.Collection;

/** Storage central for Session related information, can be ignored when browsing.
 * Each editing session should take place within a Session.
 * A specific ConceptMap can be managed wihtin different Sessions.
 * A Session should provide one way to edit stuff, if there is alternative approches, 
 * such as "sometimes I would like to save stuff in that model instead" separate
 * Sessions should be used.
 * 
 * This interface is agnostic to how Sessions interrelate and how they are saved and loaded.
 * 
 * @author matthias
 */
public interface Session {

    String getTitle();
    void setTitle(String title);

   /** The identifier for the Session is an URI.
    * 
    * @return a string represenation of the URI. 
    */
    String getURI();

    /** The base of the URI to use when new concepts are created.
     * 
     * @return the base-URI as a String, if null the caller 
     * should use the current ConceptMap URI-base instead. 
     */
    String getBaseURIForConcepts();
    
    /** The base of the URI to use when new layouts are created.
     * 
     * @return the base-URI as a String, if null the caller 
     * should use the current ConceptMap URI-base instead. 
     */
    String getBaseURIForLayouts();
    
    /** 
     * @see #getBaseURIForConcepts()
     * @param base the base-URI as a String.
     */
    void setBaseURIForConcepts(String base);
    
    /**
     * @see #getBaseURIForLayouts()
     * @param base
     */
    void setBaseURIForLayouts(String base);

    /** Tells which container to use for storing new concepts, when a concept is created 
     * the function {@link se.kth.cid.component.Component#getLoadContainer()} is used to 
     * access that container.
     * 
     * @return a URI as a String, never null.
     */
    String getContainerURIForConcepts();
    
    /** Tells which container to use for storing new layouts, when a layout is created
     * the function {@link se.kth.cid.component.Component#getLoadContainer()} is used to
     * access that container.
     * 
     * @return a URI as a String, never null.
     */
    String getContainerURIForLayouts();
    
    /**
     * @param container a URI as a String, null or invalid URIs is not allowed.
     * @see #getContainerURIForConcepts()
     */
    void setContainerURIForConcepts(String container);
    
    /**
     * @param container a URI as a String, null or invalid URIs is not allowed.
     * @see #getContainerURIForLayouts()
     */
    void setContainerURIForLayouts(String container);
    
    void addManaged(String uri);
    
    boolean removeManaged(String uri);
    
    boolean isManaged(String uri);
    
    Collection getManaged();
    
    void setModified(boolean modified);
    
    boolean isModified(); 
    
}