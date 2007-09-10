/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import se.kth.cid.conzilla.InfoMessageException;
import se.kth.cid.util.DiskCache;
import se.kth.cid.util.Hashing;

/**
 * Implementation of the ContainerCache interface, stores remote container files
 * into a local disk cache and provides access methods and some logic to
 * determine whether a container should be loaded from the cache or not. The
 * cache index is stored periodically or upon shutdown, and only if it has
 * changed.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class DiskContainerCache extends DiskCache implements ContainerCache {

	private final static String indexFileName = "containers.xml";

	private final static String simpleClassName = DiskContainerCache.class.getName().substring(
			DiskContainerCache.class.getName().lastIndexOf('.') + 1);

	/*
	 * Constructors
	 */

	/**
	 * Creates an instance, loads an already existing cache index or creates a
	 * new one. Enables regular flushing of the cache index and sets up a
	 * shutdown hook to make sure the cache index is up to date.
	 */
	public DiskContainerCache() {
		super(simpleClassName, new File(getIndexFilePath(indexFileName)));
		createContainerCacheDir();
	}

	/*
	 * Helpers
	 */
	
	private void createContainerCacheDir() {
		File cacheDirFile = new File(getContainerCacheDirectory());
		if (!(cacheDirFile.exists() && cacheDirFile.isDirectory())) {
			debug("INIT: Cache directory does not exist, creating");
			if (!cacheDirFile.mkdirs()) {
				debug("INIT: Unable to create cache directory");
				throw new InfoMessageException("Unable to create cache directory.");
			}
		}
	}

	private static String getContainerCacheDirectory() {
		return getCacheDirectory().concat("containers").concat(File.separator);
	}

	/**
	 * Constructs and returns the path to a cached file.
	 * 
	 * @param file
	 *            Filename
	 * @return Full Path
	 */
	private static String getCachedFilePath(String file) {
		return getContainerCacheDirectory().concat(file).concat(".rdf.gz");
	}

	/**
	 * Writes a string to a file.
	 * 
	 * @param data
	 *            Data to be written.
	 * @param container
	 *            File to be written.
	 */
	private void writeStreamToFile(InputStream data, File container) {
		BufferedOutputStream bos = null;
		GZIPOutputStream output = null;
		int count;
		byte[] buf = new byte[getBufferSize()];

		try {
			bos = new BufferedOutputStream(new FileOutputStream(container), getBufferSize());
			output = new GZIPOutputStream(bos, getBufferSize());
			while ((count = data.read(buf, 0, getBufferSize())) != -1) {
				output.write(buf, 0, count);
			}
		} catch (IOException ioe) {
			throw new InfoMessageException(ioe);
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException ioe) {
			}
		}
	}

	public synchronized long getCachedContainersSize() {
		long result = 0;
		String key;
		CachedContainerInformation containerInfo;
		Iterator cacheEntries = cacheMap.keySet().iterator();
		while (cacheEntries.hasNext()) {
			key = (String) cacheEntries.next();
			containerInfo = (CachedContainerInformation) cacheMap.get(key);
			result += containerInfo.getLocalFileSize();
		}
		return result;
	}

	/*
	 * Interface implementation
	 */

	/**
	 * @see se.kth.cid.component.cache.ContainerCache#clear()
	 */
	public synchronized void clear() {
		deleteAllFilesInDir(new File(getContainerCacheDirectory()));
		cacheMap.clear();
		setModified(true);
		debug("CLEAR: Cache cleared");
	}

	/**
	 * @see se.kth.cid.component.cache.ContainerCache#getContainer(java.lang.String,
	 *      java.util.Date)
	 */
	public InputStream getContainer(String uri, Date maxAge) {
		synchronized (cacheMap) {
			if (!cacheMap.containsKey(uri)) {
				debug("GET: Container not in cache: " + uri);
				return null;
			}
		}

		CachedContainerInformation cci;
		synchronized (cacheMap) {
			cci = (CachedContainerInformation) cacheMap.get(uri);
		}
		
		
		if ((maxAge != null) && (cci.getLastModificationDate() != null) && (cci.getLastModificationDate().before(maxAge))) {
			return null;
		}

		FileInputStream fis;
		BufferedInputStream bis;
		GZIPInputStream input;
		try {
			debug("GET: Loading cached container: " + uri);
			fis = new FileInputStream(getCachedFilePath(cci.getFileName()));
			bis = new BufferedInputStream(fis, getBufferSize());
			try {
				input = new GZIPInputStream(bis, getBufferSize());
			} catch (IOException e) {
				debug("GET: ERROR: " + e.getMessage());
				return null;
			}
		} catch (FileNotFoundException fnfe) {
			// should not happen
			debug("GET: ERROR: Container is indexed but file was not found");
			this.removeContainer(uri);
			return null;
		}

		return input;
	}

	/**
	 * @see se.kth.cid.component.cache.ContainerCache#putContainer(java.lang.String,
	 *      java.util.Date, java.io.InputStream)
	 */
	public synchronized boolean putContainer(String uri, Date lastModificationDate, InputStream container) {
		String filePath;
		String hashedFileName;

		if (cacheMap.containsKey(uri)) {
			CachedContainerInformation cachedInfo;
			synchronized (cacheMap) {
				cachedInfo = (CachedContainerInformation) cacheMap.get(uri);
			}
			
			if ((cachedInfo.getLastModificationDate() != null) &&
					(cachedInfo.getLastModificationDate().compareTo(lastModificationDate) >= 0)) {
				debug("PUT: Container is already cached and up-to-date: " + uri);
				return false;
			}

			debug("PUT: Updated container available: " + uri);

			hashedFileName = cachedInfo.getFileName();
			filePath = getCachedFilePath(hashedFileName);
		} else {
			String hashModifier = new String();

			while (true) {
				hashedFileName = Hashing.md5(uri + hashModifier);
				filePath = getCachedFilePath(hashedFileName);
				File testFile = new File(filePath);

				if (!testFile.exists()) {
					break;
				} else {
					debug("PUT: Hash collision, calculating new hash: " + uri);
					hashModifier = hashModifier.concat("#");
				}
			}
		}

		File cachedFile = new File(filePath);
		writeStreamToFile(container, cachedFile);
		long cachedFileSize = cachedFile.length();

		CachedContainerInformation cci = new CachedContainerInformation();
		cci.setFileName(hashedFileName);
		cci.setLocalFileSize(cachedFileSize);
		cci.setLastModificationDate(lastModificationDate);
		cci.setCachedDate(new Date());
		
		synchronized (cacheMap) {
			cacheMap.put(uri, cci);
		}
		this.setModified(true);

		debug("PUT: Container cached: " + uri);

		return true;
	}

	/**
	 * @see se.kth.cid.component.cache.ContainerCache#removeContainer(java.lang.String)
	 */
	public synchronized void removeContainer(String uri) {
		if (cacheMap.containsKey(uri)) {
			CachedContainerInformation cci;
			synchronized (cacheMap) {
				cci = (CachedContainerInformation) cacheMap.get(uri);
			}

			File containerFile = new File(getCachedFilePath(cci.getFileName()));
			if (containerFile.exists()) {
				containerFile.delete();
			}

			synchronized (cacheMap) {
				cacheMap.remove(uri);
			}

			debug("REMOVE: Container removed from cache: " + uri);
		}
	}
	
	/**
	 * @see se.kth.cid.component.cache.ContainerCache#isCached(java.lang.String)
	 */
	public boolean isCached(String uri) {
		return cacheMap.containsKey(uri);
	}
	
	/*
	 * This method is supposed to decrease the cache size.
	 * 
	 * An algorithm could look like this: We take 10% of the max cache size as
	 * buffer. This means if we reach 100%, we start removing containers until
	 * we are below 90% again. Oldest first, sorted after the cachedDate, not
	 * the lastModDate. Perhaps a 80:20 ratio makes more sense.
	 * 
	 * This method should be executed as a thread.
	 * 
	 * TODO update javadoc of the method in the interface
	 */
	/**
	 * @see se.kth.cid.component.cache.ContainerCache#purgeCache(java.util.Date, long)
	 */
	public synchronized void purgeCache(Date maxAge, long size) {
		if ((maxAge == null) && (size == -1)) {
			this.clear();
		}
		// TODO
	}
	
	/**
	 * @see se.kth.cid.component.cache.ContainerCache#getFillPercentage()
	 */
	public int getFillPercentage() {
		// TODO
		return 0;
	}

}