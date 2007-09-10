/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

/**
 * Interface for the contribution information cache. Used for caching metadata
 * (comparable to commit messages) for contributions to components
 * (context-maps, containers, ...).
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public interface ContributionInformationStore {

	/**
	 * Retrieves metadata out of the store.
	 * 
	 * @param uri
	 *            URI of the component where the metadata belongs to.
	 * @return Returns null if there is no metadata for the component in the
	 *         store.
	 */
	String getMetaData(String uri);

	/**
	 * Stores metadata for a specific component, overwriting an eventually
	 * existing version. If either of the parameters is null, nothing is stored.
	 * 
	 * @param uri
	 *            URI of the component where the metadata belongs to.
	 * @param metaData
	 *            Metadata to be stored.
	 */
	void storeMetaData(String uri, String metaData);

	/**
	 * Removes metadata of a specific component.
	 * 
	 * @param uri
	 *            URI of the component.
	 */
	void removeMetaData(String uri);

	/**
	 * Checks whether metadata for a specific component is cached.
	 * 
	 * @param uri
	 *            URI of the component.
	 * @return True if there is metadata for the given URI is cached.
	 */
	boolean hasMetaData(String uri);

}