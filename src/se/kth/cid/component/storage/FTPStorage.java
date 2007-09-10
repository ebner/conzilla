/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP implementation of the RemoteStorage interface.
 * 
 * Even though a whole URL is expected as parameter to the methods which
 * implement the interface, they only take the path-part of the URL into
 * consideration. The port number to connect to cannot be set right now.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see RemoteStorage
 */
public class FTPStorage implements RemoteStorage {

	/**
	 * Main resource, used to communicate with the server.
	 */
	private FTPClient ftpResource;

	private String hostName;

	private String authUserName;

	private String authPassword;

	private static final String ANONYMOUS_USER = "anonymous";

	private static final String ANONYMOUS_PASS = "FTPStorage@conzilla.org";

	/* Constructors */

	/**
	 * Initializes the object with all necessary values.
	 * 
	 * @param url
	 *            URL as URL object.
	 */
	public FTPStorage(URL url) {
		this.ftpResource = new FTPClient();
		this.hostName = url.getHost();
		this.authUserName = RemoteStorageHelper.getUserName(url);
		this.authPassword = RemoteStorageHelper.getPassword(url);
	}

	/**
	 * Initializes the object.
	 * 
	 * @param url
	 *            URL as String.
	 * @throws RemoteStorageException
	 */
	public FTPStorage(String url) throws RemoteStorageException {
		this(RemoteStorageHelper.buildURL(url));
	}

	/* Helper methods */
	
	private void checkConnection() throws RemoteStorageException {
		if (!this.isConnected()) {
			throw new RemoteStorageException("Connection not established.");
		}
	}

	/**
	 * Tries to return an FTPFile object. Used several times within this class.
	 * 
	 * @param path
	 *            Path to a file or folder.
	 * @return FTPFile object.
	 * @throws RemoteStorageException
	 */
	private FTPFile getFTPFile(String path) throws RemoteStorageException {
		try {
			FTPFile[] file = this.ftpResource.listFiles(path);
			if ((file != null) && (file.length > 0)) {
				return file[0];
			} else {
				throw new RemoteStorageException(
				"Failed to retrieve the requested information. Please make sure the path exists.");
			}
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * Creates a full directory-path if it does not exist.
	 * 
	 * @param path
	 *            Path to a directory.
	 * @throws IOException
	 */
	private void createParentDirectories(String path) throws IOException {
		if ((path != null) && (path.length() > 1)) {
			path.replace('\\', '/');
			String sep = "/";
			String collection = "";
			StringTokenizer t = new StringTokenizer(path, sep);
			int count = t.countTokens();

			// exclude the last token (filename)
			while ((t.hasMoreTokens()) && (count > 1)) {
				collection += sep + t.nextToken();
				this.ftpResource.makeDirectory(collection);
				count--;
			}
		} else {
			throw new IllegalArgumentException("Parameter must be a valid path.");
		}
	}

	/* Interface implementation */

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#connect()
	 */
	public void connect() throws RemoteStorageException {
		try {
			if (this.ftpResource.isConnected()) {
				this.disconnect();
			} else {
				this.ftpResource.connect(this.hostName);
				if (!FTPReply.isPositiveCompletion(this.ftpResource.getReplyCode())) {
					throw new RemoteStorageException(this.ftpResource.getReplyString());
				}
			}

			if ((this.authUserName == null) || (this.authPassword == null) || (this.authUserName.length() < 1)) {
				this.authUserName = ANONYMOUS_USER;
				this.authPassword = ANONYMOUS_PASS;
			}

			this.ftpResource.login(this.authUserName, this.authPassword);
			if (!FTPReply.isPositiveCompletion(this.ftpResource.getReplyCode())) {
				throw new RemoteStorageException(this.ftpResource.getReplyString());
			}

			// We send PASV, just in case we are behind a packet filter or sth.
			// Passive mode is more likely to work than active mode.
			this.ftpResource.enterLocalPassiveMode();

			// We transfer everything in binary mode.
			this.ftpResource.setFileType(FTP.BINARY_FILE_TYPE);

			// This is default, but we call it anyway
			this.ftpResource.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

			/*
			if ((this.pathName != null) && (this.pathName.length() > 0)) {
				this.ftpResource.changeWorkingDirectory(this.pathName);
				if (!FTPReply.isPositiveCompletion(this.ftpResource.getReplyCode())) {
					throw new RemoteStorageException(this.ftpResource.getReplyString());
				}
			}
			*/
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#disconnect()
	 */
	public void disconnect() throws RemoteStorageException {
		if ((this.ftpResource != null) && (this.ftpResource.isConnected())) {
			try {
				this.ftpResource.logout();
				this.ftpResource.disconnect();
			} catch (IOException e) {
				throw new RemoteStorageException(e);
			}
		}
	}
	
	public boolean isConnected() {
		return this.ftpResource.isConnected();
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#get(java.lang.String)
	 */
	public InputStream get(String url) throws RemoteStorageException {
		this.checkConnection();
		try {
			InputStream is = this.ftpResource.retrieveFileStream(RemoteStorageHelper.getFilePath(url));
			if (is != null) {
				return is;
			} else {
				throw new RemoteStorageException(
						"Did not get an input stream. Please make sure the remote file exists.");
			}
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#put(java.lang.String,
	 *      java.io.InputStream)
	 */
	public void put(String url, InputStream stream) throws RemoteStorageException {
		this.checkConnection();
		String path = RemoteStorageHelper.getFilePath(url);
		try {
			this.createParentDirectories(path);
			this.ftpResource.storeFile(path, stream);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#delete(java.lang.String)
	 */
	public boolean delete(String url) throws RemoteStorageException {
		this.checkConnection();
		try {
			return this.ftpResource.deleteFile(RemoteStorageHelper.getFilePath(url));
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#exists(java.lang.String)
	 */
	public boolean exists(String url) throws RemoteStorageException {
		this.checkConnection();
		try {
			FTPFile file = this.getFTPFile(RemoteStorageHelper.getFilePath(url));
			if (file.isFile() || file.isSymbolicLink() || file.isDirectory()) {
				return true;
			}
		} catch (RemoteStorageException ioe) {
		}

		return false;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getLastModificationDate(java.lang.String)
	 */
	public Date getLastModificationDate(String url) throws RemoteStorageException {
		this.checkConnection();
		return this.getFTPFile(RemoteStorageHelper.getFilePath(url)).getTimestamp().getTime();
	}

	/**
	 * Checks whether a URL is a directory.
	 * 
	 * @param url URL of a file or directory.
	 * @return True if the URL points to a directory.
	 * @throws RemoteStorageException
	 */
	public boolean isDirectory(String url) throws RemoteStorageException {
		this.checkConnection();
		return this.getFTPFile(RemoteStorageHelper.getFilePath(url)).isDirectory();
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#isWritable(java.lang.String)
	 */
	public boolean isWritable(String url) throws RemoteStorageException {
		this.checkConnection();
		return this.getFTPFile(RemoteStorageHelper.getFilePath(url)).hasPermission(FTPFile.USER_ACCESS,
				FTPFile.WRITE_PERMISSION);
	}
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getSize(java.lang.String)
	 */
	public long getSize(String url) throws RemoteStorageException {
		this.checkConnection();
		return this.getFTPFile(RemoteStorageHelper.getFilePath(url)).getSize();
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getProtocolName()
	 */
	public String getProtocolName() {
		return new String("FTP");
	}
	
	/* Versioned methods */
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#get(java.lang.String, int)
	 */
	public InputStream get(String url, int revision) throws RemoteStorageException {
		throw new IllegalStateException("Revisions not supported by this object.");
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getVersionName(java.lang.String)
	 */
	public String getVersionName(String url) {
		throw new IllegalStateException("Revisions not supported by this object.");
	}
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#isVersioned(java.lang.String)
	 */
	public boolean isVersioned(String url) {
		return false;
	}

}