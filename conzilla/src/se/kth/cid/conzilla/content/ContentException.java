/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import se.kth.cid.component.Resource;

/** This exception is thrown when something goes wrong when trying to display
 *  content.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class ContentException extends Exception
{
  /** The content description that had trouble.
   */
  Resource content;

  /** Constructs a ContentException with the specified detail message.
   *
   * @param message the detail message.
   * @param c the content resource that had trouble.
   */
  public ContentException(String message, Resource c)
    {
      super(message);
      this.content = c;
    }

  /** Returns the ContentDescription that had trouble.
   *
   *  @return the ContentDescription that had trouble.
   */
  public Resource getContent()
  {
    return content;
  }
}

