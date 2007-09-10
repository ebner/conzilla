/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.layout.ContextMap;

/** This is a interface for creating a new Filter
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public interface FilterFactory
{
  /** Creates a Filter.
   */
   Filter createFilter(MapController cont, Concept concept, ContextMap conceptMap);
   
  /** Refresh all cached filter.
   */
  void refresh();
}
