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

package se.kth.cid.conzilla.content;
import se.kth.cid.content.*;

/** This interface describes the functionality of an object that
 *  is able to select amongst content.
 *  In contrast to ContentFilter, this interface is intended to
 *  be asynchronous, i.e., allows user interaction.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ContentSelector
{

  /** Sets the content to select amongst.
   *
   *  If set to null, the selecting of content is disabled.
   *
   *  @param content the content to select amongst.
   */
  void selectContent(ContentDescription[] content);

  /** Adds a selection listener to this object.
   *
   *  The selection listener will receive notification when
   *  a content has been selected.
   *
   *  @param l the listener to add.
   */
  void addSelectionListener(SelectionListener l);


  /** Removes a selection listener from this object.
   *
   *  @param l the listener to remove.
   */
  void removeSelectionListener(SelectionListener l);
}
