/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;

/** This exception is thrown when something goes wrong when trying to
 *  filter content.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public class FilterException extends Exception
{

  /** Constructs a FilterException with the specified detail message.
   *
   * @param message the detail message.
   */
  public FilterException(String message)
  {
      super(message);
  }
}

