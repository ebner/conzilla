/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

/**
 * The Resource interface describes the common behavior of all objects loaded
 * into the system, such as Concepts, ConceptTypes and ConceptMaps. This API is
 * intended to be exportable over CORBA.
 * 
 * There are also components that are of this type only. These only contain
 * meta-data.
 * 
 * @author Mikael Nilsson
 * @version $Revision: 1.21 $
 */
public interface Resource {

	/**
	 * Indicates that this component has been edited. This is fired when
	 * setEdited(true) is called.
	 */
	int EDITED = 0;

	/**
	 * Indicates that the edited state of this component has been set to false.
	 */
	int SAVED = 1;

	/**
	 * Indicates that the meta-data of this component has been edited.
	 */
	int METADATA_EDITED = 2;

	/**
	 * The last EditEvent type number that is used in this interface..
	 */
	int LAST_RESOURCE_EDIT_CONSTANT = METADATA_EDITED;

	/**
	 * Returns the URI of this component.
	 * 
	 * It is returned as a String because the URI class is not intended to be
	 * exported over CORBA. However, _you_may_assume_ that this string is a
	 * valid URI.
	 * 
	 * TODO change return type to URI
	 * 
	 * @return the URI of this component. Never null.
	 */
	String getURI();

	/**
	 * Checks whether this component has been edited.
	 * 
	 * This flag is intended to indicate whether this component is in synch with
	 * its permanent storage or needs a save. Thus, CORBA-components may not set
	 * this flag at all.
	 * 
	 * All methods that change attributes of this component relative to its
	 * permanent storage will automatically set this flag.
	 * 
	 * @return true if this component has been edited, false otherwise.
	 */
	boolean isEdited();

	/**
	 * Sets the edited state of this component.
	 * 
	 * This is useful for example when a component has been saved, and thus is
	 * in synch with its permanent storage, or you for some reason want to mark
	 * it as needing a save.
	 * 
	 * This functionality need some thought for CORBA-components.
	 * 
	 * @param b
	 *            the new edited state of this component.
	 * @exception ReadOnlyException
	 *                if this component is not editable.
	 */
	void setEdited(boolean b) throws ReadOnlyException;

	/**
	 * Adds an edit listener to this component.
	 * 
	 * The listener will receive notification of all changes to this component.
	 * See the individual functions for details.
	 * 
	 * @param l
	 *            the new EditListener.
	 */
	void addEditListener(EditListener l);

	/**
	 * Removes an edit listener from this component.
	 * 
	 * @param l
	 *            the EditListener to remove.
	 */
	void removeEditListener(EditListener l);

}