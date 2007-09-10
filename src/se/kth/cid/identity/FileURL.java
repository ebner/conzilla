/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;

import java.io.File;

/**
 * This class represents "file:" URLs.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 * @deprecated
 */
public class FileURL extends URI {
	/**
	 * Creates a "file:" URL from the given string.
	 * 
	 * @param nuri
	 *            the string to parse.
	 * @exception MalformedURIException
	 *                if the string did not parse.
	 */
	public FileURL(String nuri) throws MalformedURIException {
		super(nuri);

		if (uri.length() == colonLocation + 1)
			throw new MalformedURIException("Empty path", null);

		if (uri.charAt(colonLocation + 1) != '/')
			throw new MalformedURIException("No leading '/' in \"" + uri + "\"", uri);
		if (!uri.regionMatches(0, "file", 0, colonLocation))
			throw new MalformedURIException("The identifier was no File URI \"" + uri + "\".", uri);
	}

	public String getPath() {
		return super.getSchemeSpecific();
	}

	/**
	 * Returns a FileURL pointing to the given system-dependent path.
	 * 
	 * @return a FileURL pointing to the file.
	 * @exception MalformedURIException
	 *                if the URI became unvalid.
	 */
	public static FileURL getFileURL(String path) throws MalformedURIException {
		File pathFile = new File(path);

		java.net.URL url = null;

		try {
			url = pathFile.toURL();
		} catch (java.net.MalformedURLException e) {
			throw new MalformedURIException("Invalid path: " + path + ":\n " + e.getMessage(), path);
		}

		return new FileURL(url.toString());
	}

	/**
	 * Returns a Java File object pointing to the file represented by this
	 * FileURL.
	 * 
	 * @return a Java File object.
	 */
	public File getJavaFile() {
		return new File(getPath());
	}

	public String makeRelative(URI other, boolean allowDotDot) throws MalformedURIException {
		if (!(other instanceof FileURL))
			return other.toString();
		return genericMakeRelative(other, 5, 5, allowDotDot);
	}

	protected URI parseRelativeURI(String relstr) throws MalformedURIException {
		return new FileURL(genericParseRelativeURI(relstr, 5));
	}

}
