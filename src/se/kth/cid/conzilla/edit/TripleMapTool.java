/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class TripleMapTool extends Tool
{

  static final String TRIPLE_VISIBLE = "HIDE_TRIPLE";
  static final String TRIPLE_INVISIBLE = "SHOW_TRIPLE";

  boolean tripleVisible = true;

  public TripleMapTool(MapController cont)
  {
    super("HIDE_TRIPLE", EditMapManagerFactory.class.getName(), cont);
  }
    
  protected boolean updateEnabled() {

    if (!mapEvent.mapObject.getDrawerLayout().isEditable()) {
        return false;
    }
    DrawerLayout dl = mapEvent.mapObject.getDrawerLayout();
    if (!(dl instanceof StatementLayout))
      return false;
    StatementLayout sl = (StatementLayout) dl;
    if (mapEvent == null || mapObject == null)
      return false;

    if (dl.getBodyVisible())
      if (sl.getLine().length > 0)
        tripleVisible = true;
      else
        tripleVisible = false;
    else
    	return false;

    ConzillaResourceManager.getDefaultManager().customizeButton(
      getJMenuItem(),
      EditMapManagerFactory.class.getName(),
      tripleVisible ? TRIPLE_VISIBLE : TRIPLE_INVISIBLE);

    return true;
  }

  public void actionPerformed(ActionEvent e)
    {
    	//No tests needed since updateEnabled have disabled the button if no
    	//action should be taken.

   		StatementLayout sl = (StatementLayout) mapObject.getDrawerLayout();

    	if (tripleVisible)
    		sl.setLine(new ContextMap.Position[0]);
		else
			sl.setLine(LayoutUtils.tripleLine(sl.getSubjectLayout(), sl.getObjectLayout(), 
				((EditMapManager) controller.getManager()).getGridModel()));
    }
}

