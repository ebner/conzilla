/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.graphics.LineDraw;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.style.LineStyle;
import se.kth.cid.style.StyleManager;

public class LineDrawer extends MapDrawer {

    protected int pathType = 0;
    LineStyle style;

    Vector path;
    GeneralPath polygon;
    Point[] line;

    DrawerMapObject drawerMapObject;
    boolean boxline = false;
    //  ConceptMapObject conceptMapObject;

    public LineDrawer(DrawerMapObject drawMapObject) {
        super(drawMapObject.getDisplayer());
        this.drawerMapObject = drawMapObject;
        boxline = true;
    }

    public LineDrawer(TripleMapObject tripleMapObject, boolean tripleline) {
        super(tripleMapObject.getDisplayer());
        this.drawerMapObject = tripleMapObject;
    }

    public boolean getErrorState() {
        return drawerMapObject.getErrorState();
    }

    public Collection getBoundingboxes() {
        Vector vect = new Vector();
        if (path != null) {
            Iterator segments = path.iterator();
            while (segments.hasNext()) {
                Shape seg = (Shape) segments.next();
                vect.addElement(seg.getBounds());
            }
        }
        return vect;
    }

    protected void doPaint(Graphics2D g, Mark mark) {
        if (path != null) {
            LineDraw.paintLine(g, path, style, mark);
        }
    }

    protected void update(Point[] line) {
        this.line = line;
        updateStyle();

        path = LineDraw.constructPath(line, pathType);
    }

    private void updateStyle() {

        style = new LineStyle();

        if (!boxline) {
            //Works since I know It's a TripleMapObject....
            pathType =
                ((StatementLayout) drawerMapObject.getDrawerLayout())
                    .getPathType();
            fixStyle();

        } else {

            //FIXME: boxline support as style...
            pathType = LineStyle.PATH_TYPE_STRAIGHT;
            style.setTypeStr("continuous");
            style.setThickness(2);
            /*	    pathType = drawerMapObject.getDrawerLayout().getBoxLinePathType();
            LineStyle ls = drawerMapObject.getBoxLineStyle();
            
            if(ls != null)
            {
                type      = ls.getLineStyle();
                thickness = ls.getLineThickness();
                }*/
            style.setStroke(LineDraw.makeStroke(style, null));
        }
    }

    private void fixStyle() {
        StyleManager sm = drawerMapObject.getStyleManager();
        java.util.List stack = drawerMapObject.getStyleStack();

        style.fetchStyle(sm, stack);
    }

    protected boolean checkAndFillHit(MapEvent m) {
        if (path == null)
            return false;
        int segmentnr = LineDraw.findIntersectionOnPath(m.mapX,m.mapY,path);
        if (segmentnr != -1) {
            m.lineSegmentNumber = segmentnr;
            return true;
        } else {
            return false;
        }
    }
}
