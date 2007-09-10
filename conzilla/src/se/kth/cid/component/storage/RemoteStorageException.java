/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.storage;

/**
 * Custom exception class for use with RemoteStorage.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class RemoteStorageException extends Exception {

	private static final long serialVersionUID = 1L;

	public RemoteStorageException(String message) {
		super(message);
	}
	
	public RemoteStorageException(Throwable cause) {
		super(cause);
	}
	
	public RemoteStorageException(String message, Throwable cause) {
		super(message, cause);
	}

}