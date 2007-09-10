/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import se.kth.cid.component.Resource;

/** This class extends AbstracAction to fit the functionality of
 *  the filter.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public abstract class FilterAction extends AbstractAction {

  protected FilterNode node;
  protected Resource component;


 /** Constructs a FilterAction.
  *
  *  @param node the FilterNode this action is attached to.
  *  @param title the name of this action.
  */
  public FilterAction(FilterNode node, String title)
  {
    super();
    this.node = node;
    
    putValue(Action.NAME, title);
  }

  public FilterAction(FilterNode node)
  {
//    this(node, AttributeEntryUtil.getTitleAsString(node));
  }


  public boolean isEnabled()
  {
      List list = node.getContent(component);
      return list.size() > 0;
  }
	
 /** Sets the content for this action.
  *
  *  @param comp the contents for this action.
  */
  public void setComponent(Resource comp)
  {
    this.component = comp;
  }
}
