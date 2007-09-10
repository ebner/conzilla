/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package se.kth.cid.component;

import se.kth.cid.util.*;


/** The Component interface describes the common behavior of all
 *  objects loaded into the system, such as Neurons, NeuronTypes and
 *  ConceptMaps. This API is intended to be exportable over CORBA.
 *
 *  There are also components that are of this type only.
 *  These only contain meta-data.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface Component
{

  /** Indicates that the meta-data of this component has been edited.
   */
  int METADATA_EDITED               = 0;

  /** Indicates that the edited state of this component has been set to false.
   */
  int SAVED                         = 1;

  /** The last EditEvent type number that is used in this interface..
   */
  int LAST_COMPONENT_EDIT_CONSTANT  = SAVED;


  //////////// Meta data /////////////////////

  /** Returns the meta-data of this component.
   *
   * @return the meta-data of this component. Never null.
   */
  MetaData getMetaData();
  

  /** Returns the URI of this component.
   *
   *  It is returned as a String
   *  because the URI class is not intended to be exported over CORBA.
   *  However, _you_may_assume_ that this string is a valid URI.
   *
   *  @return the URI of this component. Never null. 
   */
  String getURI();

  /** Returns the URI that was finally used to retrieve this component.
   *
   *  This URI will be used when saving the component. It may change
   *  between sessions.
   *
   *  It is returned as a String.
   *  because the URI class is not intended to be exported over CORBA.
   *  However, _you_may_assume_ that this string is a valid URI.
   *
   *  @return the URI of this component. Never null. 
   */
  String getLoadURI();

  /** Returns the MIME type that was finally used to retrieve this component.
   *
   *  This type will be used when saving the component. It may change
   *  between sessions.
   *
   *  It is returned as a String.
   *  because the MIMEType class is not intended to be exported over CORBA.
   *  However, _you_may_assume_ that this string is a valid MIMEType.
   *
   *  @return the MIME type of this component. Never null. 
   */
  String getLoadMIMEType();
  
  /** Checks whether this component is editable. This state can in general
   *  not be changed, as it depends in the savability of components. Also,
   *  this state is not expected to change during the usage of the component.
   *
   *  @return true if this component is editable, false otherwise.
   */
  boolean isEditable();

  /** Checks whether this component has been edited.
   *
   *  This flag is intended to indicate whether this component
   *  is in synch with its permanent storage or needs a save.
   *  Thus, CORBA-components may not set this flag at all.
   *
   *  All methods that change attributes of this component relative
   *  to its permanent storage will automatically set this flag.
   *
   *  @return true if this component has been edited, false otherwise.
   */
  boolean isEdited();

  /** Sets the edited state of this component.
   *
   *  This is useful for example when a component has been saved,
   *  and thus is in synch with its permanent storage,
   *  or you for some reason want to mark it as needing a save.
   *
   *  This functionality need some thought for CORBA-components.
   *
   *  @param b the new edited state of this component.
   *  @exception ReadOnlyException if this component is not editable.
   */
  void    setEdited(boolean b) throws ReadOnlyException;

  /** Adds an edit listener to this component.
   *
   *  The listener will receive notification of
   *  all changes to this component. See the individual functions for details.
   *
   *  @param l the new EditListener.
   */
  void    addEditListener(EditListener l);

  /** Removes an edit listener from this component.
   *
   *  @param l the EditListener to remove.
   */
  void    removeEditListener(EditListener l);
    
  /** Sends a editevent to all registered listeners.
   *
   * @param e the EditEvent to send
   */
  void fireEditEvent(EditEvent e);
}

