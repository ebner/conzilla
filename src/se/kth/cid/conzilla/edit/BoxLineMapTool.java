/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class BoxLineMapTool extends Tool {

    static final String LINE_VISIBLE = "HIDE_CONCEPTLINE";
    static final String LINE_INVISIBLE = "SHOW_CONCEPTLINE";

    boolean boxLineVisible = false;

    public BoxLineMapTool(MapController cont) {
        super(LINE_VISIBLE, EditMapManagerFactory.class.getName(), cont);
    }

    protected boolean updateEnabled() {

        if (!mapEvent.mapObject.getDrawerLayout().isEditable()) {
            return false;
        }
        if (mapEvent == null || mapObject == null)
            return false;
        DrawerLayout dl = mapObject.getDrawerLayout();
        if (!(dl instanceof StatementLayout))
            return false;
        StatementLayout sl = (StatementLayout) dl;

        if (sl.getBoxLine().length == 0)
            if (sl.getLine().length != 0 && sl.getBodyVisible())
                boxLineVisible = false;
            else
                return false;
        else
            boxLineVisible = true;

        ConzillaResourceManager.getDefaultManager().customizeButton(
            getJMenuItem(),
            EditMapManagerFactory.class.getName(),
            boxLineVisible ? LINE_VISIBLE : LINE_INVISIBLE);

        if (mapEvent.hitType == MapEvent.HIT_NONE)
            return false;
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        StatementLayout sl = (StatementLayout) mapObject.getDrawerLayout();
        if (boxLineVisible)
            sl.setBoxLine(new ContextMap.Position[0]);
        else {
            ContextMap.Position[] pos =
                LayoutUtils.boxLine(
                    sl,
                    LayoutUtils.getPosition(mapEvent),
                    ((EditMapManager) controller.getManager()).getGridModel());
            sl.setBoxLine(pos);
        }
    }
}
