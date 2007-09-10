/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.identity;

/** Thrown if a MIME type was not possible to parse.
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class MalformedMIMETypeException extends Exception
{
  /** The MIME type that could not be parsed.
   */
  String type;
  
  /** Constructs an exception with the given detail message.
   *
   * @param nreason the detail message.
   * @param type the malformed MIME type.
   */
  MalformedMIMETypeException(String nreason, String type)
    {
      super(nreason);
      this.type = type;
    }

  /** Returns the MIME type that could not be parsed.
   *
   * @return the MIME type that could not be parsed.
   */
  public String getType()
  {
    return type;
  }
}
