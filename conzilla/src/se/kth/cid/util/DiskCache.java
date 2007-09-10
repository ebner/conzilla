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
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
	
	private long flushingInterval = 10000; // 10 seconds
	
	private int bufferSize = 8192; // 8kB
	
	private boolean modified;
	
	private String cacheName;
	
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
	
	public DiskCache(String cacheName, File indexFile) {
		this.indexFile = indexFile;
		this.cacheName = cacheName;
		
		if (indexFile.exists()) {
			loadIndex(indexFile);
		} else {
			createIndex();
		}
		
		initIndexFlushers();
		debug("INIT: Started");
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
			debug("INIT: Loading Index from " + indexFile);
			bis = new BufferedInputStream(new FileInputStream(indexFile), bufferSize);
			input = new XMLDecoder(bis);
			cacheMap = (HashMap) input.readObject();
		} catch (IOException ioe) {
			// instead of throwing an exception:
			// this.createIndex();
			// this.clear();
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
			debug("INIT: Cache directory does not exist, creating");
			if (!cacheDirFile.mkdirs()) {
				debug("INIT: Unable to create cache directory");
				throw new InfoMessageException("Unable to create cache directory.");
			}
		}
		cacheMap = new HashMap();
	}
	
	/**
	 * Prints a message through the Tracer class.
	 * 
	 * @param message
	 *            Debug message.
	 */
	protected void debug(String message) {
		Tracer.debug(cacheName + ": " + message);
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
	 * Deletes all files in a given directory, but does not remove the directory
	 * itself.
	 * 
	 * @param dir
	 *            Directory to be cleaned.
	 * @return True if successful for all files.
	 */
	protected static boolean deleteAllFilesInDir(File dir) {
		File file;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				file = new File(dir, children[i]);
				if (file.isFile()) {
					if (!file.delete()) {
						return false;
					}
				}
			}
		}
		return true;
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
		debug("INIT: Setting up index flushing timer and shutdown hook");
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
					output.flush();
					output.close();
				}
			}
			moveFile(tmpDataFile, indexFile);
			setModified(false);
			debug("FLUSH: Wrote cache index to disk");
		}
	}
	
	private void moveFile(File tempFile, File destFile) {
		if (tempFile.equals(destFile)) {
			return;
		}
		if ((tempFile != null) && (destFile != null)) {
			Tracer.debug("Moving temporary file " + tempFile + " to " + destFile);
			if (!tempFile.renameTo(destFile)) {
				try {
					copyFile(tempFile, destFile);
					if (!tempFile.delete()) {
						Tracer.debug("Unable to delete temporary file");
					}
				} catch (IOException e) {
					Tracer.debug(e.getMessage());
				}
			}
		}
	}
	
    void copyFile(File src, File dst) throws IOException {
    	FileChannel sourceChannel = new FileInputStream(src).getChannel();
    	FileChannel destinationChannel = new FileOutputStream(dst).getChannel();
    	sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
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