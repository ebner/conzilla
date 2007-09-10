/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.cache;

import java.io.InputStream;
import java.util.Date;

/**
 * Caches container files. Useful for browsing in connection with collaboration,
 * depending on the implementation it will speed up online as well as offline
 * access of containers.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public interface ContainerCache {

	/**
	 * Tries to retrieve the path to a container out of the cache.
	 * 
	 * @param uri
	 *            URI of the container.
	 * @param maxAge
	 *            Returns the container only if not older than the given date. If
	 *            this parameter is null, no date comparison is done and any
	 *            cached container is returned.
	 * @return Returns null if the container is not cached, otherwise it will
	 *         return an InputStream with container data.
	 */
	InputStream getContainer(String uri, Date maxAge);

	/**
	 * Stores a container in the cache, overwriting an eventually existing older
	 * version.
	 * 
	 * @param uri
	 *            URI of the container.
	 * @param container
	 *            Container as InputStream to be cached.
	 * @param creationDate
	 *            Date of creation of the container file.
	 * @return True if the container has been successfully cashed. False is
	 *         returned e.g. if the to-be-cashed container is older than the
	 *         already cashed one. (in this case it will not be cashed)
	 */
	boolean putContainer(String uri, Date creationDate, InputStream container);

	/**
	 * Removes a specific container from the cache.
	 * 
	 * @param uri
	 *            URI of the container to be removed.
	 */
	void removeContainer(String uri);
	
	/**
	 * Checks whether a container is cached or not.
	 * 
	 * @param uri
	 *            URI of the container.
	 * @return True if the container is in the cache and therefore can be used
	 *         in offline mode.
	 */
	boolean isCached(String uri);

	/**
	 * Tidy up and remove all cached containers.
	 */
	void clear();
	
	/**
	 * TODO
	 * 
	 * @param maxAge
	 * @param size
	 */
	void purgeCache(Date maxAge, long size);
	
	/**
	 * Returns the percentage of how much the cache is filled.
	 * 
	 * @return Fill percentage of the cache.
	 */
	int getFillPercentage();

}