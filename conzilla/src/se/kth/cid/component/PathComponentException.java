/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;
import java.net.URI;

/** If a component can't be created due to missing directorys, 
 *  this is the exception created. 
 *  Observe that the path don't have to be the only problem.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public abstract class PathComponentException extends ComponentException
{
  /** 
   *
   * @param message the detail message.
   */
  public PathComponentException(String message)
    {
	super(message);
    }

  public abstract boolean makePath();

  public abstract URI getPath();
}

