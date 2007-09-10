/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;
import java.util.Vector;

import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.controller.ControllerException;

/** The filter is a tool for structuring content in a concept map.
 *  It reads from a filtermap that comes with every concept map.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public interface Filter
{
  /** Returns the first FilterNode of this filter.
   *
   *  @return the first FilterNode of this filter.
   */
   FilterNode getFilterNode();

  /** Returns the URI string of this filter.
   *
   *  @return the URI string of this filter.
   */
   String getURI();

  /** Sets the content for current concept.
   *
   *  Throws a ControllerException.
   *
   *  @param component the current Resource to filter.
   */
   void setContent(Resource component) throws ControllerException;

  /** Displays the given content.
   *
   *  @param contents the contents to display.
   */
   void showContent(Vector contents);

  /** Filters the content of given concept with given node.
   *
   *  Only abstract implementation in AbstractFilter.
   *
   *  @param node the filternode to use.
   *  @param component the Resource whose content will be filtered.
   *  @return a vector with filtered content.
   Vector filterContent(FilterNode node, Resource component);
   */
   
   boolean componentPasses(Resource component, FilterNode node);
    
}

