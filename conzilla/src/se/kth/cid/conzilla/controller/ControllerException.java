/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.controller;

/** This exception is thrown when something goes wrong with a MapController.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ControllerException extends Exception
{
  /** Constructs a ControllerException with the specified detail message.
   *
   * @param message the detail message.
   */
  public ControllerException(String message)
    {
      super(message);
    }
}

