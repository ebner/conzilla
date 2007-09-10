/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.util.Date;

import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;

/**
 * Interface for the metadata cache. Used for caching data from the Collaborilla
 * service.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public interface MetaDataCache {

	/**
	 * Tries to fetch a dataset out of the cache. Returns only data if it is
	 * up-to-date (determined by the Date parameter).
	 * 
	 * @param uri
	 *            URI of the container or contextmap where the metadata belongs
	 *            to.
	 * @param maxAge
	 *            Returns the dataset only if not older than the given date. If
	 *            this parameter is null, no date comparison is done and any
	 *            cached container is returned.
	 * @return Returns null if the dataset is not cached, otherwise it will
	 *         return an object with metadata.
	 */
	CollaborillaDataSet getDataSet(String uri, Date maxAge);

	/**
	 * Stores a dataset in the cache, overwriting an eventually existing older
	 * version.
	 * 
	 * @param uri
	 *            URI of the container or contextmap where the metadata belongs
	 *            to.
	 * @param dataSet
	 *            Dataset to be cached.
	 * @return True if the dataset has been successfully cashed. False is
	 *         returned e.g. if the to-be-cashed dataset is older than the
	 *         already cashed one. (in this case it will not be cashed)
	 */
	boolean putDataSet(String uri, CollaborillaDataSet dataSet);

	/**
	 * Removes a specific dataset from the cache.
	 * 
	 * @param uri
	 *            URI of the dataset to be removed.
	 */
	void removeDataSet(String uri);

	/**
	 * Tidy up and remove all cached datasets.
	 */
	void clear();

	/**
	 * Checks whether the dataset is cached.
	 * 
	 * @param uri
	 *            URI of the dataset.
	 * @return True if the dataset is cached.
	 */
	boolean isCached(String uri);

	/**
	 * Tells when the dataset has been cached.
	 * 
	 * @param uri
	 *            URI of the dataset.
	 * @return Time when the dataset has been cached.
	 */
	Date getCachedAge(String uri);

}