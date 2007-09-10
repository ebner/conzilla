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

/** Represents a collection of Tag = Value pairs in a component.
 *  Each tag has only one value.
 *
 *  This class is a candidate for IMS standardization.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface MetaData
{

  /** Returns the value of a given tag.
   *
   *  @param tag the tag to search for
   *  @return the value of the tag, or null if no such tag exists
   */
  String getValue(String tag);

  /** Sets the value of a given tag or removes it.
   *
   *  If value is non-null, sets the value of the tag to the specified value,
   *  overriding any old value, creating a new tag if necessary. If value is null,
   *  removes the tag from this metadata collection.
   *
   *  This method will fire an EditEvent with type MATADATATAG_EDITED/ADDED/REMOVED
   *  containing the modified tag.
   *
   * @param tag the tag to modify.
   * @param value the new value of the tag.
   * @exception ReadOnlyException if the component is not editable
   */
  void setValue(String tag, String value) throws ReadOnlyException;

  /** Returns all tags in this meta-data.
   *
   * @return an array containing strings. Never null, but may be empty.
   */
  String[] getTags();
}
