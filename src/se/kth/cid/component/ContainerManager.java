/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.net.URI;
import java.util.List;

import se.kth.cid.component.cache.ComponentCache;

/**
 * @author Matthias
 */
public interface ContainerManager {
    int REFERRENCED_MULTIPLE_TIMES = -1;
    int UNKOWN_REFERRENCED = -2;

	
	String CONTAINER_ADDED = "container_added";

	String CONTAINER_REMOVED = "container_removed";

	String CONTAINER_ORDER_CHANGED = "container_order_changed";

	String CURRENT_CONCEPT_CONTAINER_CHANGED = "current_concept_container_changed";

	String CURRENT_LAYOUT_CONTAINER_CHANGED = "current_layout_container_changed";

	void refreshCacheOfContainers(ComponentCache cache);

	/**
	 * Checks whether a component loaded from the given URI would be savable.
	 * 
	 * @param uri
	 *            the URI of the component to check.
	 * @return true if a component loaded from the given URI would be savable.
	 */
	// ->CompMan boolean isSavable(URI uri);
	/**
	 * Checks whether a component with this URI could be created.
	 * 
	 * @param uri
	 *            the URI of the component to check.
	 * @exception ComponentException
	 *                if a component loaded from the given URI would not be
	 *                savable. Otherwise, returns normally.
	 */
	void checkCreateContainer(URI uri) throws ComponentException;

	void checkLoadContainer(URI uri) throws ComponentException;

	Container createContainer(URI uri, URI origuri, ComponentCache cache) throws ComponentException;

	/**
	 * Loads a Container from a given URI.
	 * 
	 * @param uri
	 *            URI of the component to load.
	 * @return the loaded component. Never null.
	 * @exception ComponentException
	 *                if anything goes wrong when loading the component.
	 */
	Container loadContainer(URI uri, URI origURI, boolean createIfMissing, ComponentCache cache)
			throws ComponentException;

	List getContainers();

	List getContainers(String purpose);

	Container getContainer(String uri);

	// -> For edit perspective

	Container getCurrentConceptContainer();

	void setCurrentConceptContainer(Container m);

	Container getCurrentLayoutContainer();

	void setCurrentLayoutContainer(Container m);

	String getBaseURIForConcepts();

	void setBaseURIForConcepts(String base);

	String getBaseURIForLayout();

	void setBaseURIForLayout(String base);

	// <- edit perspective

	String createUniqueURI(String baseURI);

	boolean isURIUsed(String uri);

	void setIndexOfContainer(Container container, int index);

	Container loadPublishedContainer(URI uri, ComponentCache cache) throws ComponentException;

	// deprecated: String getRelationType();

	// deprecated: void setRelationType(String rt);
	
	Container findLoadContainerForResource(Resource resource);
	    
    /**
     * Returns a number indicating the number of times a Component is referrenced to or
     * a negative value according:
     * <ul><li>{@link #REFERRENCED_MULTIPLE_TIMES} - when it is referenced 
     * more than one time but unknown exactly how many times.</li>
     * <li>{@link #UNKOWN_REFERRENCED} when it is unknown how many times it
     * is referrenced if at all.</li></ul>
     * 
     * @return a number indicating the number of times it is referenced. 
     */
	int isComponentReferredTo(Component comp);
}
