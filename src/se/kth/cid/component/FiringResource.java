/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

/** This interface adds two different updating functionalities for an 
 *  implementation of a component.
 *
 *  Either the component is updated from storage, hence it isn't edited 
 *  and shouldn't be marked as such (avoiding it to be saved).
 *  Or it is changed by the application and the changes should be saved
 *  in the end of the session (except if it is a CORBA component of course).
 *
 *  @author Matthias Palmer
 */
public interface FiringResource extends Resource
{   
  /** Fires an EditEvent to all listeners and marks the
   *  component as being edited.
   *
   *  @param e the event to fire.
   */
  void fireEditEvent(EditEvent e);

  /** Fires an EditEvent to all listeners without marking the component as being edited.
   *
   *  @param e the event to fire.
   */
  void fireEditEventNoEdit(EditEvent e);
}
