/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.io.File;

import se.kth.cid.util.DiskCache;

/**
 * Implementes the {@link ContributionInformationStore} interface. Implemented
 * as a Singleton.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ContributionInformationDiskStore extends DiskCache implements ContributionInformationStore {

	private final static String indexFileName = "contributions.xml";
	
	private static ContributionInformationStore instance;

	private ContributionInformationDiskStore() {
		super(new File(getIndexFilePath(indexFileName)));
		setFlushingInterval(30000); // 30 seconds
	}
	
	public static ContributionInformationStore getContributionInformationStore() {
		if (instance == null) {
			instance = new ContributionInformationDiskStore();
		}
		return instance;
	}

	/* Interface implementation */

	/**
	 * @see se.kth.cid.collaboration.ContributionInformationStore#getMetaData(java.lang.String)
	 */
	public synchronized String getMetaData(String uri) {
		if (cacheMap.containsKey(uri)) {
			return (String) cacheMap.get(uri);
		} else {
			return null;
		}
	}

	/**
	 * @see se.kth.cid.collaboration.ContributionInformationStore#hasMetaData(java.lang.String)
	 */
	public synchronized boolean hasMetaData(String uri) {
		return cacheMap.containsKey(uri);
	}

	/**
	 * @see se.kth.cid.collaboration.ContributionInformationStore#removeMetaData(java.lang.String)
	 */
	public synchronized void removeMetaData(String uri) {
		if (cacheMap.containsKey(uri)) {
			cacheMap.remove(uri);
			setModified(true);
		}
	}

	/**
	 * @see se.kth.cid.collaboration.ContributionInformationStore#storeMetaData(java.lang.String,
	 *      java.lang.String)
	 */
	public synchronized void storeMetaData(String uri, String metaData) {
		if ((uri != null) && (metaData != null)) {
			cacheMap.put(uri, metaData);
			setModified(true);
		}
	}

}
