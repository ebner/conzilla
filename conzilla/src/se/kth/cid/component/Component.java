/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;
import java.util.List;

/** Components extends the resources by being contained in one or 
 * several {@link se.kth.cid.component.Container}s.
 * A component should be considered as a set of metadata centered around a URI.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface Component extends Resource {
    int FIRST_COMPONENT_EDIT_CONSTANT = LAST_RESOURCE_EDIT_CONSTANT + 1;
    int ATTRIBUTES_EDITED = FIRST_COMPONENT_EDIT_CONSTANT;
    int LAST_COMPONENT_EDIT_CONSTANT = ATTRIBUTES_EDITED;

    /** When a Component is loaded there might be a Container containing
     * information crucial to it's existence.
     * 
     * @return String a URI for the load-Container.
     */
    String getLoadContainer();

    ComponentManager getComponentManager();
    
    /** Returns the URI of the type of this resource.
     *  This String may be assumed to be a valid URI.
     *
     *  @return the URI of the type of this Concept.
     */
    String getType();

   /**
    * Retrieves all AttributeValues for the specified attribute in all relevant containers.
    * A typicall situation is when you ask for the Dublin Core title you use the dc:title property.
    * 
    * @param attribute reccomended is to always use a valid URI 
    * (otherwise it won't work in RDF), if null all AttributeEntrys on this Component is returned.
    * @return a {@link List} of {@link AttributeEntry}, never null.
    * @see ComponentManager#getLoadedRelevantContainers()
    */
    List getAttributeEntry(String attribute);
    
    /**
     * Retrieves a specific AttributeValue in a specific Container.
     * 
     * @param attribute is the relation type, in RDF it is the predicate. Null is not allowed.
     * @param value is a URI or just a plain string value, in RDF this is the object which might 
     * be a Resource or a Literal. Null is not allowed.
     * @param isValueAURI a Boolean that tells if the value is a URI or a plain String, if null either kind may be returned.
     * @param containerURI the contaner to search in, if null all containers are searched 
     * and the first match is returned.
     * @return a matching AttributeEntry or null if none where found.
     */
    AttributeEntry getAttributeEntry(String attribute, String value, Boolean isValueAURI, String containerURI);

    /**
    * The attribute-value pair will be added on this component in the 
    * {@link Container} specified by the current 
    * {@link se.kth.cid.conzilla.session.Session} which you get from the
    * {@link se.kth.cid.conzilla.session.SessionManager}.
    * 
    * @param attribute
    * @param value
    */
    AttributeEntry addAttributeEntry(String attribute, Object value);

    /**
    * The attribute-value pair will be removed in the 
    * {@link Container} specified by the current 
    * {@link se.kth.cid.conzilla.session.Session} which you get from the
    * {@link se.kth.cid.conzilla.session.SessionManager}.
     * 
     * @param ae
     */
    void removeAttributeEntry(AttributeEntry ae);

    /** 
     * Removes metadata, type information etc....
     */
    void remove();

}
