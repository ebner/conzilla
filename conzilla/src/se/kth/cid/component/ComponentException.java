/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

/**
 * The superclass of all exceptions that can be thrown by the classes and
 * interfaces in this package. Indicates that something has gone wrong with a
 * component.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ComponentException extends Exception {
	
	/**
	 * Constructs a ComponentException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public ComponentException(String message) {
		super(message);
	}
	
	public ComponentException(Throwable cause) {
		super(cause);
	}
	
	public ComponentException(String message, Throwable cause) {
		super(message, cause);
	}
}
