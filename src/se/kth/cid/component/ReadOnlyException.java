/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

/**
 * An exception thrown if someone tries to edit a component which is not
 * editable. It is a subclass of RuntimeException because any reasonable
 * application can avoid getting this thrown by checking isEditable. Thus, an
 * application will not need to catch-or-declare this exception. This exception
 * should be a CORBA exception.
 * 
 * @author Mikael Nilsson
 * @version $Revision$
 */
public class ReadOnlyException extends RuntimeException {

	/**
	 * Constructs a ReadOnlyException with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public ReadOnlyException(String message) {
		super(message);
	}
	
}