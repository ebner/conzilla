/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.conzilla.InfoMessageException;
import se.kth.cid.conzilla.install.Installer;

/**
 * Abstract class to implement the basic functionality of a cache based on a
 * map. The cache respectively its index is stored to disk by serializing it.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public abstract class DiskCache {
	
	Log log = LogFactory.getLog(DiskCache.class);
	
	private long flushingInterval = 10000; // 10 seconds
	
	private int bufferSize = 8192; // 8kB
	
	private boolean modified;
	
	private File indexFile;
	
	protected Map cacheMap;
	
	/**
	 * Calls the flush() method of the given DiskContainerCache instance.
	 * Supposed to be used in connection with TaskTimer and/or ShutdownHook.
	 * 
	 * @author Hannes Ebner
	 */
	private class IndexFlusher extends TimerTask implements Runnable {

		private DiskCache cache;

		IndexFlusher(DiskCache cache) {
			this.cache = cache;
		}

		public void run() {
			cache.flushIndex();
		}
	}
	
	public DiskCache(File indexFile) {
		this.indexFile = indexFile;
		
		if (indexFile.exists()) {
			loadIndex(indexFile);
		} else {
			createIndex();
		}
		
		initIndexFlushers();
		log.info("Started");
	}

	/**
	 * Loads (deserializes) an existing index from disk into memory.
	 * 
	 * @param indexFile
	 *            File object for the index file.
	 */
	private void loadIndex(File indexFile) {
		BufferedInputStream bis = null;
		XMLDecoder input = null;
		try {
			log.debug("Loading Index from " + indexFile);
			bis = new BufferedInputStream(new FileInputStream(indexFile), bufferSize);
			input = new XMLDecoder(bis);
			cacheMap = (HashMap) input.readObject();
		} catch (IOException ioe) {
			// instead of throwing an exception:
			// this.createIndex();
			// this.clear();
			log.error("Unable to load cache index", ioe);
			throw new InfoMessageException("Unable to load cache index", ioe);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	/**
	 * Creates a new index, and eventually non-existing cache directories.
	 */
	private void createIndex() {
		File cacheDirFile = new File(getCacheDirectory());
		if (!(cacheDirFile.exists() && cacheDirFile.isDirectory())) {
			log.info("Cache directory does not exist, creating");
			if (!cacheDirFile.mkdirs()) {
				log.warn("Unable to create cache directory");
				throw new InfoMessageException("Unable to create cache directory.");
			}
		}
		cacheMap = new HashMap();
	}
	
	/**
	 * Constructs and returns the path to the file containing the cache index.
	 * 
	 * @param file
	 *            Filename
	 * @return Full path
	 */
	protected static String getIndexFilePath(String file) {
		return getCacheDirectory().concat(file);
	}
	
	/**
	 * Returns the currently used directory for the cache with a trailing
	 * separator (e.g. slash).
	 * 
	 * @return Cache directory.
	 */
	protected static String getCacheDirectory() {
		String dirHelper = Installer.getConzillaDir().getAbsolutePath();
		if (!dirHelper.endsWith(File.separator)) {
			dirHelper = dirHelper.concat(File.separator);
		}
		return dirHelper.concat("cache").concat(File.separator);
	}
	
	/**
	 * Activates a TaskTimer and a ShutdownHook to flush the index to disk.
	 */
	private void initIndexFlushers() {
		log.debug("Setting up index flushing timer and shutdown hook");
		new Timer().schedule(new IndexFlusher(this), flushingInterval, flushingInterval);
		Runtime.getRuntime().addShutdownHook(new Thread(new IndexFlusher(this)));
	}
	
	/**
	 * Flushes the cache index file to disk. It checks first if something has
	 * changed.
	 */
	private synchronized void flushIndex() {
		if (isModified()) {
			File tmpDataFile = new File(indexFile.toString().concat("~"));
			BufferedOutputStream bos = null;
			XMLEncoder output = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(tmpDataFile), bufferSize);
				output = new XMLEncoder(bos);
				output.writeObject(this.cacheMap);
			} catch (FileNotFoundException fnfe) {
				throw new InfoMessageException(fnfe);
			} finally {
				if (output != null) {
					output.close();
				}
			}
			FileOperations.moveFile(tmpDataFile.toURI(), indexFile.toURI());
			setModified(false);
			log.debug("Wrote cache index to disk");
		}
	}
	
	/**
	 * A marker for the method flush() that something has changed.
	 */
	protected void setModified(boolean value) {
		this.modified = value;
	}

	/**
	 * Tells whether the cache index has been modified and therefore needs to be
	 * flushed.
	 * 
	 * @return Changed cache index since last flush.
	 */
	protected boolean isModified() {
		return this.modified;
	}
	
	public long getFlushingInterval() {
		return flushingInterval;
	}
	
	public void setFlushingInterval(long interval) {
		this.flushingInterval = interval;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
}