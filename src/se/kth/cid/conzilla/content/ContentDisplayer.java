/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;
import java.beans.PropertyChangeListener;

import se.kth.cid.component.Resource;

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
  void setContent(Resource c) throws ContentException;

  /** Gets the currently displaying content.
   *
   *  Returns the content description that was used in the last successful
   *  call to setContent.
   *
   * @return the currently displaying content.
   */
  Resource getContent();

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
