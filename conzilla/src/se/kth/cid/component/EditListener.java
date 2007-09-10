/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;


/** An EditListener listens for changes in a component.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface EditListener
{

  /** This method is called when the component has been edited.
   *
   * @param e the EditEvent representing the edit.
   */
  void componentEdited(EditEvent e);
}

