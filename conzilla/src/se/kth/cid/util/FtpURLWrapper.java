/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class FtpURLWrapper {
	static HashMap host2passwd = new HashMap();

	String host;

	String user;

	String passwd;

	String home;

	/**
	 * The wrapper only accepts a wellformed url and information enough to
	 * perform an authorization.
	 */
	public FtpURLWrapper(URL url) throws IOException {
		dissolveURL(url);
	}

	public OutputStream getOutputStream() throws IOException {
		try {
			URL url = getURL();
			URLConnection urlc = url.openConnection();
			// InputStream is = urlc.getInputStream(); // To download
			return urlc.getOutputStream(); // To upload
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return
	 */
	private URL getURL() {
		try {
			return new URL("ftp://" + user + ":" + passwd + "@" + host + home);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getFtpInputStream() throws IOException {
		try {
			URL url = getURL();
			URLConnection urlc = url.openConnection();
			return urlc.getInputStream(); // To download
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void dissolveURL(URL url) {
//		String[] parts = new String[4];
		int slash = url.toString().indexOf('/', 7);
		String before = url.toString().substring(6, slash);
		home = url.toString().substring(slash);
		int p = before.indexOf('@');
		if (p != -1) {
			user = before.substring(0, p);
			host = before.substring(p + 1);
			p = user.indexOf(':');
			if (p != -1) {
				passwd = user.substring(p + 1);
				user = user.substring(0, p);
			} else
				// FIXME: This is degenerate... should ask user.
				askForPasswd();
		} else {
			user = "anonymous";
			passwd = "humty@dumty";
		}
	}

	/**
	 * @return
	 */
	private void askForPasswd() {
		if (host2passwd.containsKey(host)) {
			passwd = (String) host2passwd.get(host);
		}
		passwd = JOptionPane.showInputDialog("Give the password for the user " + user + " at host " + host);
		host2passwd.put(host, passwd);
	}
}