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
import se.kth.cid.component.*;
import java.beans.*;

/** This interface describes the functionality of an object that
 *  is able to display content.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface ContentDisplayer
{

  /** Sets the content to display.
   *
   *  If the new content is null, it shows no content at all.
   *  @param c the content to display.
   *  @exception ContentException if the content could not be displayed.
   */
  void setContent(Component c) throws ContentException;

  /** Gets the currently displaying content.
   *
   *  Returns the content description that was used in the last successful
   *  call to setContent.
   *
   * @return the currently displaying content.
   */
  Component getContent();

  /** Adds a property change listener to this object.
   * 
   *  The listener receives notification when the content changes,
   *  with property name "content".
   *
   *  @param l the listener to add.
   */
  void addPropertyChangeListener(PropertyChangeListener l);

  /** Removes a property change listener from this object.
   *
   *  @param l the lsitener to remove.
   */
  void removePropertyChangeListener(PropertyChangeListener l);
}
