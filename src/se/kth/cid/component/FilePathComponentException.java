/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import se.kth.cid.util.Tracer;

/**
 * If a component can't be created due to missing directorys, this is the
 * exception created. Observe that the path don't have to be the only problem.
 * 
 * @author Matthias Palmer
 * @version $Revision$
 */
public class FilePathComponentException extends PathComponentException {
	File file;

	/**
	 * 
	 * @param message
	 *            the detail message.
	 */
	public FilePathComponentException(String message, File file) {
		super(message);
		this.file = file;
	}

	public boolean makePath() {
		return (new File(file.getParent())).mkdirs();
	}

	public URI getPath() {
		try {
			return new URI(file.toURL().toString());
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		} catch (MalformedURLException e) {
			Tracer.error("Malformed File:\n " + e.getMessage());
		}
		return null;
	}

}
