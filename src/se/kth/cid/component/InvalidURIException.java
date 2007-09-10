/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

/** Thrown if a URI was not possible to parse. This should be a
 *  CORBA exception.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class InvalidURIException extends Exception
{
  /** The URI that could not be parsed.
   */
  String uri;

  /** Constructs an exception with the given detail message.
   *
   * @param message the detail message.
   * @param nuri the malformed URI.
   */
  public InvalidURIException(String message, String nuri)
    {
      super(message);
      uri = nuri;
    }

  /** Returns the URI that could not be parsed.
   *
   * @return the URI that could not be parsed.
   */
  public String getURI()
  {
    return uri;
  }
}

