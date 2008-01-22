/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.util.DiskCache;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;

/**
 * Implements the {@link MetaDataCache} interface.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class MetaDataDiskCache extends DiskCache implements MetaDataCache {
	
	Log log = LogFactory.getLog(MetaDataDiskCache.class);
	
	private final static String indexFileName = "metadata.xml";

	private HashMap metaDataAge;

	public MetaDataDiskCache() {
		super(new File(getIndexFilePath(indexFileName)));
		metaDataAge = new HashMap();
	}
	
	/*
	 * Interface implementation
	 */

	/**
	 * @see se.kth.cid.collaboration.MetaDataCache#clear()
	 */
	public synchronized void clear() {
		cacheMap.clear();
		setModified(true);
		log.info("Cache cleared");
	}

	/**
	 * @see se.kth.cid.collaboration.MetaDataCache#getDataSet(java.lang.String, java.util.Date)
	 */
	public synchronized CollaborillaDataSet getDataSet(String uri, Date maxAge) {
		if (!cacheMap.containsKey(uri)) {
			log.debug("GET: Dataset not in cache: " + uri);
			return null;
		}
		
		CollaborillaDataSet dataSet = (CollaborillaDataSet) cacheMap.get(uri);
		if ((maxAge != null) && (dataSet.getTimestampModified().before(maxAge))) {
			return null;
		} else {
			return dataSet;
		}
	}

	/**
	 * @see se.kth.cid.collaboration.MetaDataCache#putDataSet(java.lang.String, se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet)
	 */
	public synchronized boolean putDataSet(String uri, CollaborillaDataSet dataSet) {
		if (dataSet == null) {
			return false;
		}
		
		if (cacheMap.containsKey(uri)) {
			CollaborillaDataSet cachedDataSet = (CollaborillaDataSet) cacheMap.get(uri);
			Date dataTime = dataSet.getTimestampModified();
			if (dataTime == null) {
				log.warn("PUT: Timestamp (modified) should not be null: not caching dataset");
				return false;
			}
			if (cachedDataSet != null && cachedDataSet.getTimestampModified() != null) {
				if (!dataSet.getTimestampModified().after(cachedDataSet.getTimestampModified())) {
					log.debug("PUT: Dataset is already cached and up-to-date: " + uri);
					metaDataAge.put(uri, new Date());
					return false;
				}
			}
			log.debug("PUT: Updated dataset available: " + uri);
		}
		
		cacheMap.put(uri, dataSet);
		metaDataAge.put(uri, new Date());
		setModified(true);
		log.info("PUT: Dataset cached: " + uri);

		return true;
	}

	/**
	 * @see se.kth.cid.collaboration.MetaDataCache#removeDataSet(java.lang.String)
	 */
	public synchronized void removeDataSet(String uri) {
		if (cacheMap.containsKey(uri)) {
			cacheMap.remove(uri);
			log.info("REMOVE: Dataset removed from cache: " + uri);
		}
	}
	
	/**
	 * @see se.kth.cid.collaboration.MetaDataCache#isCached(java.lang.String)
	 */
	public synchronized boolean isCached(String uri) {
		return cacheMap.containsKey(uri);
	}
	
	/**
	 * @see se.kth.cid.collaboration.MetaDataCache#getCachedAge(java.lang.String)
	 */
	public synchronized Date getCachedAge(String uri) {
		Date age = null;
		if (metaDataAge.containsKey(uri)) {
			age = (Date) metaDataAge.get(uri);
		}
		return age;
	}

}