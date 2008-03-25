/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.ResponseEntity;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.DepthSupport;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;

/**
 * WebDAV implementation of the RemoteVersionedStorage interface.
 * 
 * Even though a whole URL is expected as parameter to the methods which
 * implement the interface, they only take the path-part of the URL into
 * consideration. (The Apache WebDAV-implementation expects absolute paths.)
 * 
 * To create a new connection to a different server with different credentials
 * and/or path, the constructor has to be called again.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class WebDAVStorage implements RemoteStorage {

	/**
	 * The main resource.
	 */
	private WebdavResource webdavResource;

	/* Constructors */

	/**
	 * Initializes the object. The given URL has to exist, otherwise the client
	 * cannot connect to the WebDAV resource.
	 * 
	 * The protocol prefix "webdav" is explicitly allowed, it gets replaced
	 * internally with "http".
	 * 
	 * @param rootURL
	 * @throws RemoteStorageException
	 */
	public WebDAVStorage(HttpURL rootURL) throws RemoteStorageException {
		boolean followRedirects = true;

		try {
			webdavResource = new WebdavResource(rootURL, followRedirects);
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
		
		Config config = ConfigurationManager.getConfiguration();
		String proxyServer = config.getString(Settings.CONZILLA_COLLAB_PROXY_SERVER);
		int proxyPort = -1;
		try {
			proxyPort = config.getInt(Settings.CONZILLA_COLLAB_PROXY_PORT, -1);
		} catch (NumberFormatException nfe) {}
		if ((proxyServer != null) && (proxyServer.trim().length() > 0) && (proxyPort > -1)) {
			webdavResource.setProxy(proxyServer, proxyPort);
			// TODO webdavResource.setProxyCredentials();
		}
	}
	
	/**
	 * Initializes the object. The given URL (path) has to exist, otherwise the client
	 * cannot connect to the WebDAV resource.
	 * 
	 * The protocol prefix "webdav" is explicitly allowed, it gets replaced
	 * internally with "http".
	 * 
	 * @param rootURL
	 * @throws RemoteStorageException
	 */
	public WebDAVStorage(String rootURL) throws RemoteStorageException {
		this(RemoteStorageHelper.buildHttpURL(rootURL));
	}
	
	/**
	 * Initializes the object. The given URL (path) has to exist, otherwise the client
	 * cannot connect to the WebDAV resource.
	 * 
	 * @param rootURL
	 * @throws RemoteStorageException
	 */
	public WebDAVStorage(URL rootURL) throws RemoteStorageException {
		this(rootURL.toString());
	}

	/* Helper methods */

	private InputStream getWithPath(String path) throws RemoteStorageException {
		try {
			return webdavResource.getMethodData(path);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * Creates a full directory-path if it does not exist.
	 * 
	 * @param url
	 *            Path to a directory.
	 * @throws IOException
	 */
	private void createParentDirectories(String url) throws IOException, RemoteStorageException {
		String path = RemoteStorageHelper.getFilePath(url);
		if ((path != null) && (path.length() > 1)) {
			path.replace('\\', '/');
			String sep = "/";
			String collection = "";
			StringTokenizer t = new StringTokenizer(path, sep);
			int count = t.countTokens();

			// exclude the last token (filename)
			while ((t.hasMoreTokens()) && (count > 1)) {
				collection += sep + t.nextToken();
				webdavResource.mkcolMethod(collection);
				count--;
			}
		} else {
			throw new IllegalArgumentException("Parameter must be a valid path.");
		}
	}

	/**
	 * Requests the value of a given property of a specific path.
	 * 
	 * @param url
	 *            Path to a file or folder.
	 * @param property
	 *            Property to be retrieved.
	 * @return The value of the requested property.
	 */
	private String getProperty(String url, String property) throws RemoteStorageException {
		try {
			Vector<PropertyName> prop = new Vector<PropertyName>();
			prop.add(new PropertyName("DAV:", property));
			Enumeration enu = webdavResource.propfindMethod(RemoteStorageHelper.getFilePath(url), DepthSupport.DEPTH_1, prop);

			if (enu.hasMoreElements()) {
				ResponseEntity re = (ResponseEntity) enu.nextElement();
				Enumeration names = re.getProperties();
				while (names.hasMoreElements()) {
					Property a = (Property) names.nextElement();
					if (a.getLocalName().equals(property)) {
						return a.getPropertyAsString();
					}
				}
			}
		} catch (IOException ioe) {
			throw new RemoteStorageException("Unable to get property. Make sure the path exists.", ioe);
		}

		return null;
	}
	
	/* Interface implementation */

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#connect()
	 */
	public void connect() throws RemoteStorageException {
		// Not necessary
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#disconnect()
	 */
	public void disconnect() throws RemoteStorageException {
		// Not necessary
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#get(java.lang.String)
	 */
	public InputStream get(String url) throws RemoteStorageException {
		return getWithPath(RemoteStorageHelper.getFilePath(url));
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#get(java.lang.String,
	 *      int)
	 */
	public InputStream get(String url, int revision) throws RemoteStorageException {
		String revPath = HTTPStorage.pathToRevision(RemoteStorageHelper.getFilePath(url), revision);
		return getWithPath(revPath);
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#put(java.lang.String,
	 *      java.io.InputStream)
	 */
	public void put(String url, InputStream stream) throws RemoteStorageException {
		try {
			createParentDirectories(url);
			webdavResource.putMethod(RemoteStorageHelper.getFilePath(url), stream);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#delete(java.lang.String)
	 */
	public boolean delete(String url) throws RemoteStorageException {
		try {
			return webdavResource.deleteMethod(RemoteStorageHelper.getFilePath(url));
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getVersionName(java.lang.String)
	 */
	public String getVersionName(String url) throws RemoteStorageException {
		return getProperty(url, "version-name");
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#exists(java.lang.String)
	 */
	public boolean exists(String url) throws RemoteStorageException {
		try {
			if (webdavResource.headMethod(RemoteStorageHelper.getFilePath(url)) && (webdavResource.getStatusCode() != 404)) {
				return true;
			}
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}

		return false;
	}

	/**
	 * Tells whether a location is a directory.
	 * 
	 * @param url URL of a file or directory.
	 * @return True if the URL points to a directory.
	 * @throws RemoteStorageException
	 */
	public boolean isDirectory(String url) throws RemoteStorageException {
		boolean result = false;

		try {
			webdavResource.setPath(RemoteStorageHelper.getFilePath(url));
			result = webdavResource.isCollection();
		} catch (IOException ioe) {
			throw new RemoteStorageException("Make sure path exists.", ioe);
		}

		return result;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#isVersioned(java.lang.String)
	 */
	public boolean isVersioned(String url) throws RemoteStorageException {
		String ver = getProperty(url, "checked-in");

		if ((ver != null) && (ver.length() > 0)) {
			return true;
		}

		return false;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#isWritable(java.lang.String)
	 */
	public boolean isWritable(String url) throws RemoteStorageException {
		try {
			return webdavResource.optionsMethod(RemoteStorageHelper.getFilePath(url), "PUT");
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * Checks for the date of creation of a file.
	 * 
	 * @param url URL of a file.
	 * @return Date of creation.
	 * @throws RemoteStorageException
	 */
	public Date getCreationDate(String url) throws RemoteStorageException {
		String strDate = getProperty(url, "creationdate");

		if (strDate == null) {
			return null;
		}

		Date date = null;
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		try {
			date = formatter.parse(strDate);
		} catch (ParseException pe) {
			throw new RemoteStorageException(pe);
		}

		return date;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getLastModificationDate(java.lang.String)
	 */
	public Date getLastModificationDate(String url) throws RemoteStorageException {
		try {
			webdavResource.setPath(url);
			return new Date(webdavResource.getGetLastModified());
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getSize(java.lang.String)
	 */
	public long getSize(String url) throws RemoteStorageException {
		try {
			webdavResource.setPath(url);
			return webdavResource.getGetContentLength();
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getProtocolName()
	 */
	public String getProtocolName() {
		return new String("WebDAV");
	}

}