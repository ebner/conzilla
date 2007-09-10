/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package se.kth.cid.conzilla.filter;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.util.*;

/** This class extends AbstracAction to fit the functionality of
 *  the filter.
 *
 *  @author Daniel Pettersson
 *  @version $Revision$
 */
public abstract class FilterAction extends AbstractAction {

  protected FilterNode node;
  protected Component component;


 /** Constructs a FilterAction.
  *
  *  @param node the FilterNode this action is attached to.
  *  @param filtertag the name of this action.
  *  @param manager the manager attached this action.
  */
  public FilterAction(FilterNode node, String title)
  {
    super();
    this.node = node;
    
    putValue(Action.NAME, title);
  }

  public FilterAction(FilterNode node)
  {
    this(node, MetaDataUtils.getLocalizedString(node.getFilterNeuron().getMetaData().get_metametadata_language(), 
						 node.getFilterNeuron().getMetaData().get_general_title()).string);
  }


  public boolean isEnabled()
  {
      List list = node.getContent(component);
      return list.size() > 0;
  }
	
 /** Sets the content for this action.
  *
  *  @param contents the contents for this action.
  */
  public void setComponent(Component comp)
  {
    this.component = comp;
  }
}
