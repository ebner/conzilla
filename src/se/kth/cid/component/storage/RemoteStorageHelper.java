/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.storage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;

/**
 * Helper class for choosing a RemoteStorage implementation depending on the
 * protocol/scheme part of a URL or URI.
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see RemoteStorage
 * @see HTTPStorage
 * @see FTPStorage
 * @see WebDAVStorage
 */
public class RemoteStorageHelper {

	/**
	 * Contains a list of all supported protocols. Has to be lower-case.
	 */
	private static final String[] supportedProtocols = { "http", "dav", "ftp" };

	/**
	 * Contains a list of all supported protocols. Has to be lower-case.
	 */
	private static final String[] supportedWriteProtocols = { "dav", "ftp" };

	/**
	 * Creates and returns a RemoteStorage object. The decision about the
	 * implementation is built upon the protocol prefix. <br>
	 * 
	 * The comparison is based on startsWith() instead of equals(), so secure
	 * extensions like https and webdavs are covered as well.
	 * <p>
	 * Sample usage:<br>
	 * <code>
	 * RemoteStorage storage = null;
	 * try {
	 * 		storage = RemoteStorageHelper.getRemoteStorage(url);
	 * 		storage.connect();
	 * 		...
	 * } catch (RemoteStorageException rse) {
	 * 		...
	 * } finally {
	 * 		try {
	 * 			storage.disconnect();
	 * 		} catch (RemoteStorageException rse) {
	 * 		}
	 * }
	 * </code>
	 * 
	 * @param uri
	 *            URI of the to be loaded container.
	 * @return Remote storage object
	 * @throws RemoteStorageException
	 */
	public static RemoteStorage getRemoteStorage(URI uri) throws RemoteStorageException {
		uri = URI.create(uri.toASCIIString());
		String scheme = uri.getScheme();

		if (scheme != null) {
			if (scheme.equalsIgnoreCase("ftp")) {
				return new FTPStorage(uri.toString());
			} else if (scheme.toLowerCase().startsWith("http")) {
				return new HTTPStorage();
			} else if (scheme.toLowerCase().startsWith("dav")) {
				return new WebDAVStorage(uri.toString());
			}
		}

		throw new IllegalArgumentException("No valid protocol handler for \"" + scheme + "\" found.");
	}

	/**
	 * Checks the protocol prefix and tries to find out whether it is supported
	 * by a RemoteStorage implementation.
	 * 
	 * The comparison is based on startsWith() instead of equals(), so secure
	 * extensions like https and webdavs are covered as well.
	 * 
	 * @param uri
	 *            URI of a location.
	 * @return True if the URI describes a remote location with a supported
	 *         protocol.
	 */
	public static boolean isRemoteAndSupported(URI uri) {
		String scheme = uri.getScheme();
		if (scheme != null) {
			for (int i = 0; i < supportedProtocols.length; i++) {
				// We prefer startsWith() over equals() as it also covers the
				// secure extensions of the protocols (https, davs, ...)
				if (scheme.toLowerCase().startsWith(supportedProtocols[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks the protocol prefix and tries to find out whether the protocol
	 * supports modification.
	 * 
	 * The comparison is based on startsWith() instead of equals(), so secure
	 * extensions like webdavs are covered as well.
	 * 
	 * @param uri
	 *            URI of a location.
	 * @return True if the URI describes a remote location with a supported
	 *         protocol and if modification is supported.
	 */
	public static boolean isRemoteAndSupportsModification(URI uri) {
		String scheme = uri.getScheme();
		if (scheme != null) {
			for (int i = 0; i < supportedWriteProtocols.length; i++) {
				// We prefer startsWith() over equals() as it covers also the
				// secure extensions of the protocols (https, davs, ...)
				if (scheme.toLowerCase().startsWith(supportedWriteProtocols[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the username supplied within a URL.
	 * 
	 * @param url
	 *            URL.
	 * @return Username. Null if it does not contain one or if it cannot be
	 *         parsed.
	 */
	public static String getUserName(URL url) {
		if (url.getUserInfo() != null) {
			try {
				return url.getUserInfo().split(":")[0];
			} catch (IndexOutOfBoundsException ioobe) {
			} catch (NullPointerException npe) {
			}
		}

		return null;
	}

	/**
	 * Returns the password supplied within a URL.
	 * 
	 * @param url
	 *            URL.
	 * @return Password. Null if it does not contain one or if it cannot be
	 *         parsed.
	 */
	public static String getPassword(URL url) {
		if (url.getUserInfo() != null) {
			try {
				return url.getUserInfo().split(":")[1];
			} catch (IndexOutOfBoundsException ioobe) {
			} catch (NullPointerException npe) {
			}
		}

		return null;
	}

	/**
	 * Returns the file path part from a URL string.
	 * 
	 * @param url
	 *            URL String.
	 * @return File path as String.
	 */
	public static String getFilePath(String url) throws RemoteStorageException {
		return buildURL(url).getFile();
	}

	/**
	 * Constructs a URL object out of a String. Replaces an eventually existing
	 * "webdav" protocol prefix with "http".
	 * 
	 * @param url
	 *            URL as String.
	 * @return URL object.
	 */
	public static URL buildURL(String url) throws RemoteStorageException {
		url = replaceProtocol(url);
		URL newURL;

		try {
			newURL = new URL(url);
		} catch (MalformedURLException e) {
			throw new RemoteStorageException("Not a valid URL.");
		}

		return newURL;
	}

	/**
	 * Constructs a HttpURL object out of a String. Replaces an eventually
	 * existing "webdav" protocol prefix with "http".
	 * 
	 * @param url
	 *            URL as String.
	 * @return URL as HttpURL object.
	 * @throws RemoteStorageException
	 */
	public static HttpURL buildHttpURL(String url) throws RemoteStorageException {
		try {
			url = replaceProtocol(url);
			return new HttpURL(url);
		} catch (URIException ue) {
			throw new RemoteStorageException(ue);
		}
	}

	/**
	 * Replaces the protocol prefix webdav/webdavs with http/https. This is
	 * necessary to be able to convert a String into a URL object.
	 * 
	 * @param url
	 *            URL as String.
	 * @return URL as String.
	 */
	private static String replaceProtocol(String url) {
		return url.replaceFirst("dav://", "http://").replaceFirst("davs://", "https://");
	}

}