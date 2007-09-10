/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla;

/**
 * Custom exception class for messages to be presented to the user.
 * 
 * @author Hannes Ebner
 */
public class InfoMessageException extends RuntimeException {

	public InfoMessageException(String message) {
		super(message);
	}

	public InfoMessageException(Throwable cause) {
		super(cause);
	}

	public InfoMessageException(String message, Throwable cause) {
		super(message, cause);
	}
	
}