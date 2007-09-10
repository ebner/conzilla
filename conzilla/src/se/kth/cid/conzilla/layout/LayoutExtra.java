/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.layout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.LayoutUtils;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsMenu;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.util.Tracer;

/** Proof of concept layout support, star-shaped box layout
 *  with order depending on number of relations attached.
 *
 *  @author Matthias Palmer.
 */
public class LayoutExtra implements Layout, Extra{
    GridModel gridModel;

    public LayoutExtra() {
    }

    public boolean initExtra(ConzillaKit kit) {
        gridModel = new GridModel(6);
        return true;
    }

    public String getName() {
        return "layout";
    }
    public void refreshExtra() {
    }
    public void showExtra() {
    }
    public void closeExtra() {
    }
    public boolean saveExtra() {
        return true;
    }
    public void exitExtra() {
    }

    public void addExtraFeatures(MapController c) {
    }

    public void extendMenu(ToolsMenu menu, final MapController mc) {
        if (menu.getName().equals(DefaultMenuFactory.TOOLS_MENU))
            menu
                .addTool(new Tool(
                    "LAYOUT_TRIVIAL",
                    LayoutExtra.class.getName()) {
            public void actionPerformed(ActionEvent ae) {
                Tracer.debug("Layouts the current map.");
                layout(mc);
            }
        }, 200);
    }

    public void layout(MapController controller) {
        layout(
            controller.getConceptMap());
    }

    public void layout(ContextMap cMap) {
        DrawerLayout[] nss = cMap.getDrawerLayouts();
        Collection vnss1 = new Vector();
        Collection vnss2 = new Vector();
        separate(nss, vnss1, vnss2);
        sortBoxes(vnss1, vnss2);

        ContextMap.Dimension size = cMap.getDimension();
        int radius = size.width > size.height ? size.height : size.width;
        radius = (int) (radius * 0.5 - 50);

        int nr = vnss1.size();
        int i = 0;
        for (Iterator iter = vnss1.iterator(); iter.hasNext();) {
            ConceptLayout conceptLayout = (ConceptLayout) iter.next();            
            double v = 2 * Math.PI / nr * i;
            int xpos = (int) (size.width / 2 + radius * Math.cos(v));
            int ypos = (int) (size.height / 2 + radius * Math.sin(v));
            ContextMap.Position p = new ContextMap.Position(xpos, ypos);
            ContextMap.BoundingBox bb = conceptLayout.getBoundingBox();
            ContextMap.BoundingBox newbb =
                new ContextMap.BoundingBox(bb.dim, p);
            conceptLayout.setBoundingBox(newbb);
            i++;            
        }

        for (Iterator statements = vnss2.iterator(); statements.hasNext();) {
            StatementLayout statement = (StatementLayout) statements.next();
            DrawerLayout sl = statement.getSubjectLayout();
            DrawerLayout ol = statement.getObjectLayout();
            ContextMap.Position [] line = LayoutUtils.tripleLine(sl, ol, null);
            statement.setLine(line);
            if (statement.getBodyVisible()) {
                ContextMap.BoundingBox bb = statement.getBoundingBox();
                int xpos = (int) ((line[0].x + line[1].x)/2);
                int ypos = (int) ((line[0].y + line[1].y)/2);
                ContextMap.Position newp = new ContextMap.Position(xpos, ypos);
                ContextMap.BoundingBox newbb = new ContextMap.BoundingBox(bb.dim, newp);
                statement.setBoundingBox(newbb);
            }             
        }
    }
    public void resizeBoxes(ContextMap cMap, MapController controller) {
        DrawerLayout[] nss = cMap.getDrawerLayouts();
        Collection vnss1 = new Vector();
        Collection vnss2 = new Vector();
        separate(nss, vnss1, vnss2);

        Iterator it = vnss1.iterator();
        while (it.hasNext()) {
            ConceptLayout ns = (ConceptLayout) it.next();
            java.awt.Dimension dim =
                controller.getView()
                    .getMapScrollPane()
                    .getDisplayer()
                    .getMapObject(ns.getURI())
                    .getPreferredSize();
            ContextMap.BoundingBox bb = ns.getBoundingBox();
            ns.setBoundingBox(
                LayoutUtils.preferredBoxOnGrid(
                    gridModel,
                    bb.pos.x,
                    bb.pos.y,
                    dim));

        }
    }

    private void separate(
        DrawerLayout[] nss,
        Collection vnss1,
        Collection vnss2) {
        for (int i = 0; i < nss.length; i++) {
            DrawerLayout ns = nss[i];
            if (ns instanceof StatementLayout)
                vnss2.add(ns);
            else
                vnss1.add(ns);
        }
    }
    private Collection sortBoxes(Collection concepts, Collection statements) {
        Iterator it = statements.iterator();
        while (it.hasNext()) {
            StatementLayout sl = (StatementLayout) it.next();
            concepts.remove(sl.getSubjectLayout());
            concepts.remove(sl.getObjectLayout());
            concepts.add(sl.getSubjectLayout());
            concepts.add(sl.getObjectLayout());
        }
        return concepts;
    }
}
