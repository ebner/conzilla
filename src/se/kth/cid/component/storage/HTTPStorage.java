/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.config.Settings;

/**
 * HTTP implementation of the RemoteStorage interface. Supports PUT and DELETE
 * commands.
 * 
 * TODO: Support for self-signed certificates is missing right now. Right now
 * only "official" and valid certificates are supported. This requires a custom
 * implementation of the SecureProtocolSocketFactory interface. Same applies to
 * WebDAVStorage.
 * Example:
 * http://svn.apache.org/viewvc/jakarta/commons/proper/httpclient/trunk/src/contrib/org/apache/commons/httpclient/contrib/ssl/
 * 
 * @author Hannes Ebner
 * @version $Id$
 * @see RemoteStorage
 */
public class HTTPStorage implements RemoteStorage {
	
	private static int TIMEOUT = 10000; // ms 

	private HttpClient httpClient;

	/* Constructors */

	/**
	 * Initializes the object with a new HttpClient object.
	 */
	public HTTPStorage() {
		httpClient = new HttpClient();
		httpClient.getParams().setConnectionManagerTimeout(TIMEOUT);
		httpClient.getParams().setSoTimeout(TIMEOUT);
		httpClient.getParams().setParameter("http.connection.timeout", TIMEOUT); // there is no convenience method for this
		
		Config config = ConfigurationManager.getConfiguration();
		String proxyServer = config.getString(Settings.CONZILLA_COLLAB_PROXY_SERVER);
		int proxyPort = -1;
		try {
			proxyPort = config.getInt(Settings.CONZILLA_COLLAB_PROXY_PORT, -1);
		} catch (NumberFormatException nfe) {}
		if (proxyServer != null && proxyServer.trim().length() > 0 && proxyPort > -1) {
			httpClient.getHostConfiguration().setProxy(proxyServer, proxyPort);
			// TODO httpClient.getState().setProxyCredentials(); // this requires method.setDoAuthentication(true);
		}
	}

	/* Helper methods */

	private void setupMethod(String url, HttpMethod httpObject) throws RemoteStorageException {
		setRetryHandler(httpObject);
		boolean urlHasCredentials = prepareAuthentication(url);
		httpObject.setDoAuthentication(urlHasCredentials);
	}

	private boolean prepareAuthentication(String strURL) throws RemoteStorageException {
		boolean result = false;

		URL url = RemoteStorageHelper.buildURL(strURL);
		String host = url.getHost();
		String username = RemoteStorageHelper.getUserName(url);
		String password = RemoteStorageHelper.getPassword(url);

		if ((username != null) && (password != null) && (username.length() > 0)) {
			AuthScope scope = new AuthScope(host, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password); 
			httpClient.getState().setCredentials(scope, credentials);
			result = true;
		}

		return result;
	}

	private void setRetryHandler(HttpMethod httpObject) {
		DefaultHttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler(2, false);
		httpObject.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
	}

	private boolean supportsMethod(String url, String method) throws RemoteStorageException {
		OptionsMethod options = new OptionsMethod(url);
		setupMethod(url, options);
		boolean result = false;

		try {
			httpClient.executeMethod(options);
			result = options.isAllowed(method);
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			options.releaseConnection();
		}

		return result;
	}

	/**
	 * Constructs the path to a specific revision. This method is Subversion
	 * specific.
	 * 
	 * @param path
	 *            Path to a resource.
	 * @param revision
	 *            Revision to be accessed.
	 * @return Full path to a specific revision.
	 */
	protected static String pathToRevision(String path, int revision) {
		String svnPrefix = "!svn/ver/";
		String parentPath = null;
		File fullPath = new File(path);

		if ((parentPath = fullPath.getParent()) == null) {
			parentPath = "/";
		} else if (!parentPath.endsWith("/")) {
			parentPath = parentPath.concat("/");
		}

		return parentPath.concat(svnPrefix).concat(Integer.toString(revision)).concat("/").concat(fullPath.getName());
	}

	/* Interface implementation */

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#connect()
	 */
	public void connect() throws RemoteStorageException {
		// not necessary
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#disconnect()
	 */
	public void disconnect() throws RemoteStorageException {
		// not necessary
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#get(java.lang.String)
	 */
	public InputStream get(String url) throws RemoteStorageException {
		GetMethod get = new GetMethod(url);
		setupMethod(url, get);
		get.setRequestHeader(new Header("Accept-Encoding", "gzip"));
		
		InputStream stream = null;
		try {
			int statusCode = httpClient.executeMethod(get);
			if (statusCode != HttpStatus.SC_OK) {
				throw new RemoteStorageException(get.getStatusText());
			}
			
	        boolean gzip = false;
			Header encHeader = get.getResponseHeader("Content-Encoding");
	        if (encHeader != null) {
	            String encValue = encHeader.getValue();
	            if (encValue != null) {
	        		gzip = (encValue.indexOf("gzip") >= 0);
	            }
	        }
	        
	        if (gzip) {
	        	stream = new GZIPInputStream(get.getResponseBodyAsStream());
	        } else {
	        	stream = get.getResponseBodyAsStream();
	        }
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			// No release, we need the connection to retrieve the stream data
			// get.releaseConnection();
		}

		return stream;
	}

	/**
	 * This method is experimental and works only if it is supported by the
	 * server.
	 * 
	 * @see se.kth.cid.component.storage.RemoteStorage#put(java.lang.String,
	 *      java.io.InputStream)
	 */
	public void put(String url, InputStream stream) throws RemoteStorageException {
		if (!supportsMethod(url, "PUT")) {
			throw new RemoteStorageException("Method (PUT) not supported.");
		}

		PutMethod put = new PutMethod(url);
		setupMethod(url, put);
		put.setRequestEntity(new InputStreamRequestEntity(stream, InputStreamRequestEntity.CONTENT_LENGTH_AUTO));

		try {
			int statusCode = httpClient.executeMethod(put);

			// A successful response may not just be 200, but may also be
			// 201 Created, 204 No Content or any of the other 2xx range
			// responses.
			if ((statusCode < 200) || (299 < statusCode)) {
				throw new RemoteStorageException(put.getStatusText());
			}
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			put.releaseConnection();
		}
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#delete(java.lang.String)
	 */
	public boolean delete(String url) throws RemoteStorageException {
		if (!supportsMethod(url, "DELETE")) {
			throw new RemoteStorageException("Method (DELETE) not supported.");
		}

		DeleteMethod delete = new DeleteMethod(url);
		setupMethod(url, delete);

		try {
			int statusCode = httpClient.executeMethod(delete);

			if (statusCode != HttpStatus.SC_OK) {
				throw new RemoteStorageException(delete.getStatusText());
			}
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			delete.releaseConnection();
		}

		return true;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#exists(java.lang.String)
	 */
	public boolean exists(String url) throws RemoteStorageException {
		HeadMethod head = new HeadMethod(url);
		setupMethod(url, head);
		boolean result = true;

		try {
			int statusCode = httpClient.executeMethod(head);

			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				result = false;
			}
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			head.releaseConnection();
		}

		return result;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#isWritable(java.lang.String)
	 */
	public boolean isWritable(String url) throws RemoteStorageException {
		if (supportsMethod(url, "PUT")) {
			return true;
		}

		return false;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getLastModificationDate(java.lang.String)
	 */
	public Date getLastModificationDate(String url) throws RemoteStorageException {
		HeadMethod head = new HeadMethod(url);
		setupMethod(url, head);
		Date date = null;

		try {
			int statusCode = httpClient.executeMethod(head);

			if (statusCode != HttpStatus.SC_OK) {
				throw new RemoteStorageException(head.getStatusText());
			}

			Header response = head.getResponseHeader("Last-Modified");
			head.releaseConnection();

			if (response == null) {
				// Workaround for WebDAV/Apache:
				// If requested via HTTP is does not send "last-modified"
				// in the header, so we try again with WebDAV (because this does
				// not request the date via HEAD, it uses PROPFIND)
				WebDAVStorage dav = new WebDAVStorage(url);
				return dav.getLastModificationDate(url);
			}

			String lastModified = response.getValue();
			DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");

			try {
				date = formatter.parse(lastModified);
			} catch (ParseException pe) {
			}
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		}

		return date;
	}
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getSize(java.lang.String)
	 */
	public long getSize(String url) throws RemoteStorageException {
		HeadMethod head = new HeadMethod(url);
		setupMethod(url, head);
		long fileSize;
		
		try {
			int statusCode = httpClient.executeMethod(head);

			if (statusCode != HttpStatus.SC_OK) {
				throw new RemoteStorageException(head.getStatusText());
			}

			Header response = head.getResponseHeader("Content-Length");

			if (response == null) {
				throw new RemoteStorageException("Could not get requested information.");
			}

			String contentLength = response.getValue();

			try {
				fileSize = Long.parseLong(contentLength);
			} catch (NumberFormatException nfe) {
				fileSize = -1;
			}
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			head.releaseConnection();
		}

		return fileSize;
	}

	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#getProtocolName()
	 */
	public String getProtocolName() {
		return new String("HTTP");
	}
	
	/* Versioned methods */
	
	/**
	 * @see se.kth.cid.component.storage.RemoteStorage#get(java.lang.String,
	 *      int)
	 */
	public InputStream get(String url, int revision) throws RemoteStorageException {
		String revPath = pathToRevision(url, revision);
		return get(revPath);
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
	public boolean isVersioned(String url) throws RemoteStorageException {
		boolean result = false;
		HeadMethod head = new HeadMethod(url);
		
		try {
			int statusCode = httpClient.executeMethod(head);
			if (statusCode != HttpStatus.SC_OK) {
				throw new RemoteStorageException(head.getStatusText());
			}
			Header response = head.getResponseHeader("ETag");
			if ((response != null) && (response.getValue().length() > 0)) {
				result = true;
			}
			
			// Alternatively we could use WebDAV:
			// WebDAVStorage dav = new WebDAVStorage(url);
			// result = dav.isVersioned(url);
		} catch (HttpException he) {
			throw new RemoteStorageException(he);
		} catch (IOException ioe) {
			throw new RemoteStorageException(ioe);
		} finally {
			head.releaseConnection();
		}

		return result;
	}

}