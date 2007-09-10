/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.storage;

import java.io.InputStream;
import java.util.Date;

/**
 * Interface for accessing files remotely.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public interface RemoteStorage {

	/**
	 * Connects to the server. (Not all protocols require a properly set up
	 * connection though.)
	 * 
	 * @throws RemoteStorageException
	 */
	public void connect() throws RemoteStorageException;

	/**
	 * Disconnects from the server. (Not all protocols require a properly set up
	 * connection though.)
	 * 
	 * @throws RemoteStorageException
	 */
	public void disconnect() throws RemoteStorageException;

	/**
	 * Retrieves a file. If the file is checked-in the most recent revision is
	 * retrieved.
	 * 
	 * @param url
	 *            URL of a file.
	 * @return InputStream with the file data.
	 */
	public InputStream get(String url) throws RemoteStorageException;

	/**
	 * Determines whether the given URL exists.
	 * 
	 * @param url
	 *            URL of a file or folder.
	 * @return True if the given path exists.
	 */
	public boolean exists(String url) throws RemoteStorageException;

	/**
	 * Requests the date of last modification of a URL.
	 * 
	 * @param url
	 *            URL of a file or folder.
	 * @return Date of last modification.
	 */
	public Date getLastModificationDate(String url) throws RemoteStorageException;

	/**
	 * Stores an InputStream to the location given by the URL.
	 * 
	 * @param url
	 *            URL of a file. An eventually existing file will be
	 *            overwritten.
	 * @param stream
	 *            InputStream with the data to be written.
	 */
	public void put(String url, InputStream stream) throws RemoteStorageException;

	/**
	 * Deletes a file which is located by a URL.
	 * 
	 * @param url
	 *            URL of a file or folder.
	 * @return True if the path could be deleted.
	 */
	public boolean delete(String url) throws RemoteStorageException;

	/**
	 * Determines whether the given URL is writable.
	 * 
	 * @param url
	 *            URL of a file or folder.
	 * @return True if the given URL is writable.
	 */
	public boolean isWritable(String url) throws RemoteStorageException;
	
	/**
	 * Returns the file size of the specified URL in bytes.
	 * 
	 * @param url URL of a file.
	 * @return Size of the file in bytes, or -1 if the size could not be determined.
	 * @throws RemoteStorageException
	 */
	public long getSize(String url) throws RemoteStorageException;

	/**
	 * Returns the name of the currently used protocol. Useful if the
	 * implementations are adressed via RemoteStorage (e.g. the helper class
	 * RemoteStorageHelper returns such an object).
	 * 
	 * @return Name of the currently used protocol.
	 */
	public String getProtocolName();
	
	/* Versioned methods */
	
	/**
	 * Retrieves a specific revision of a file (if it is checked into a
	 * repository).
	 * 
	 * @param url
	 *            URL of the file.
	 * @param revision
	 *            Revision number.
	 * @return InputStream with the file data.
	 */
	public InputStream get(String url, int revision) throws RemoteStorageException;

	/**
	 * Determines whether the given path is versioned (which means that it is
	 * checked into a version control system). Should be checked before
	 * get(String, int) or getVersionName(String) is called.
	 * 
	 * @param url
	 *            URL of a file or folder.
	 * @return True if the given path is versioned.
	 */
	public boolean isVersioned(String url) throws RemoteStorageException;

	/**
	 * Requests the name or number (depending on the version control system) of
	 * a revision of a path.
	 * 
	 * @param url
	 *            URL of a file or folder.
	 * @return Current revision.
	 */
	public String getVersionName(String url) throws RemoteStorageException;

}