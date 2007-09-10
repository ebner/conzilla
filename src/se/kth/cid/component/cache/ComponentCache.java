/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component.cache;
import se.kth.cid.component.Component;
import se.kth.cid.component.EditListener;

/** Used to cache Components.
 *  It could be implemented in a number of ways.
 */
public interface ComponentCache 
{
  /** Finds a component within the cache and returns it.
   *
   * @param uri the URI of the component to find.
   * @return a Resource if it does exist, otherwise null.
   */
  Component getComponent(String uri);

  /** References a component in the cache.
   *  If the component is not already in the cache, it is added.
   *
   * @param comp the component to reference.
   */
  void referenceComponent(Component comp);

    void addGlobalEditListener(EditListener l);
    void removeGlobalEditListener(EditListener l);

  /** Clears the cache, removing all components from it.
   *
   */
  void clear();
}

