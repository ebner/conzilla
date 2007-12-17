/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The container interface describes the common behaviour of components that
 * really are sets of components, typicall containers respresents
 * zip/jar/tar-files or RDF-models.
 * 
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public interface Container extends Resource {
	String COMMON = "common";

	/**
	 * Sets the purpose of a Container.
	 * 
	 * @param purpose
	 *            if null is given it is replaced with {@link #COMMON}.
	 */
	void setPurpose(String purpose);

	/**
	 * The purpose of a Container may be several, e.g. for storing stylesheets,
	 * properties or the most {@link #COMMON}, i.e. storing metadata about
	 * ContextMaps and referenced resources.
	 * 
	 * @return the purpose of a Container, never null.
	 */
	String getPurpose();

	/**
	 * Finds all ContextMaps definitions in the container. A ContextMap may be
	 * spread out in several containers, but it is a recommendation that it is
	 * defined only in one Container. If a ContextMap is defined in several
	 * Containers an application should not crash but it is not required to
	 * perform well in an editing session.
	 * 
	 * @return a list of URIs as Strings, never null but may be empty.
	 */
	List<String> getDefinedContextMaps();

	/**
	 * Finds all ContextMaps in the container that references the given
	 * resource.
	 * 
	 * @param uri
	 *            URI as string for resource to check
	 * @return a list of URIs for maps as Strings, never null may be mepty.
	 */
	Set<String> getMapsReferencingResource(String uri);

	/**
	 * Adds an request for a container for individual component. Typically the
	 * container are then loaded when more information for the component is
	 * needed.
	 * 
	 * @param uri
	 *            the component (identified by a URI) who requests the continer.
	 * @param containeruri
	 *            the requested container.
	 * @see #removeRequestedContainerForURI(String, String)
	 */
	void addRequestedContainerForURI(String uri, String containeruri);

	/**
	 * Removes a request for a container for a specific component.
	 * 
	 * @param uri
	 *            the component to who the request for an addtional container
	 *            should be removed.
	 * @param containeruri
	 *            the container in the request.
	 * @return boolean tells wether the removal succeded.
	 * @see #addRequestedContainerForURI(String, String)
	 */
	boolean removeRequestedContainerForURI(String uri, String containeruri);

	/**
	 * All Containers requested in this Container for the given component is
	 * returned.
	 * 
	 * @param uri
	 *            the component identified by a URI.
	 * @return Collection of URIs (Strings) for the requested containers, never
	 *         null.
	 */
	Collection<String> getRequestedContainersForURI(String uri);

	/**
	 * All Components requesting some container are returned.
	 * 
	 * @return Collection components identified by their URIs (Strings), never
	 *         null.
	 */
	Collection<String> getURIsWithRequestedContainers();

	/**
	 * Returns the URI that was finally used to retrieve this component.
	 * 
	 * This URI will be used when saving the component. It may change between
	 * sessions.
	 * 
	 * It is returned as a String. because the URI class is not intended to be
	 * exported over CORBA. However, _you_may_assume_ that this string is a
	 * valid URI.
	 * 
	 * @return the URI of this component. Never null.
	 */
	URI getLoadURI();
	
	void setLoadURI(URI uri);

	String getPublishURL();

	/**
	 * Returns the MIME type that was finally used to retrieve this Container.
	 * 
	 * This type will be used when saving the container. It may change between
	 * sessions.
	 * 
	 * It is returned as a String. because the MIMEType class is not intended to
	 * be exported over CORBA. However, _you_may_assume_ that this string is a
	 * valid MIMEType.
	 * 
	 * @return the MIME type of this component. Never null.
	 */
	String getLoadMIMEType();

	/**
	 * Empties the container of all content.
	 */
	void clear();

	/**
	 * For every format there might be a containermanager that maintains a list
	 * of currently loaded containers.
	 */
	ContainerManager getContainerManager();

	/**
	 * Checks whether this component is editable. This state can in general not
	 * be changed, as it depends in the savability of components. Also, this
	 * state is not expected to change during the usage of the component.
	 * 
	 * @return true if this component is editable, false otherwise.
	 */
	boolean isEditable();

}