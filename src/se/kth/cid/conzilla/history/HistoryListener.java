/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.history;

/** This interface listens to history events.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface HistoryListener
{
  /** This method is called when a history event occurs.
   *
   * @param e the history event.
   */
  void historyEvent(HistoryEvent e);
}
