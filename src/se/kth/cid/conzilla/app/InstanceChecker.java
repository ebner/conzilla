/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * Provides functionality to check for an already existing instance of an
 * application. The to be used lock-file is passed to the constructor.
 * Installs a shutdown-hook to ensure the lock is removed.
 * 
 * @author Hannes Ebner
 */
public class InstanceChecker {

	private File file;

	private FileChannel channel;

	private FileLock lock;

	/**
	 * @param lockFile File to install the lock on. It is created if it does not exist.
	 */
	InstanceChecker(File lockFile) {
		file = lockFile;
	}

	/**
	 * Tries to acquire a lock on the file to check for a running application.
	 * 
	 * @return Returns true if the application is running/the file is locked.
	 */
	public boolean isApplicationActive() {
		try {
			channel = new RandomAccessFile(file, "rw").getChannel();

			try {
				lock = channel.tryLock();
			} catch (OverlappingFileLockException e) {
				removeFileLock();
				return true;
			}

			if (lock == null) {
				removeFileLock();
				return true;
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					release();
				}
			});

			return false;
		} catch (Exception e) {
			removeFileLock();
			return true;
		}
	}

	/**
	 * Removes the file lock.
	 */
	private void removeFileLock() {
		if (lock != null) {
			try {
				lock.release();
			} catch (IOException e1) {
			}
		}
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Deletes the lock file.
	 */
	private void deleteFile() {
		if ((file != null) && file.exists() && file.canWrite()) {
			file.delete();
		}
	}
	
	public void release() {
		removeFileLock();
		deleteFile();
	}

}