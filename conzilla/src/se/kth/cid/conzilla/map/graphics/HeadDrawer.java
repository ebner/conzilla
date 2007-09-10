/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.graphics.HeadDraw;
import se.kth.cid.style.HeadStyle;
import se.kth.cid.style.StyleManager;

public class HeadDrawer extends MapDrawer {

    DrawerMapObject drawerMapObject;
    Point[] line;
    HeadStyle style;
    Shape lineHead;
    
    boolean boxline = false;

    public HeadDrawer(DrawerMapObject drawerMapObject) {
        super(drawerMapObject.getDisplayer());
        this.drawerMapObject = drawerMapObject;
        boxline = true;
    }

    public HeadDrawer(TripleMapObject tripleMapObject, boolean tripleline) {
        super(tripleMapObject.getDisplayer());
        this.drawerMapObject = tripleMapObject;
    }

    public boolean getErrorState() {
        return drawerMapObject.getErrorState();
    }

    public Rectangle getBoundingBox() {
        return lineHead != null ? lineHead.getBounds() : null;
    }

    protected void doPaint(Graphics2D g, Mark mark) {
        if (lineHead == null) {
            return;
        }
        
        //FIXME: The case when the linehead should be hollow.
        //In that case we paint over the interior of the head since we
        //do always draw the line all the way to the tip of the head.
        Color mapColor = ColorTheme.getColor(ColorTheme.Colors.MAP_BACKGROUND);
        HeadDraw.paintHead(g, lineHead, style, mark, mapColor);
    }

    protected void update(Point[] line) {
        this.line = line;
        fixStyle();
        Shape rawShape = HeadDraw.constructHead(style);
        lineHead = HeadDraw.rotateHead(rawShape, line, style);
    }

    private void fixStyle() {
        if (!boxline) {
            StyleManager sm = drawerMapObject.getStyleManager();
            java.util.List stack = drawerMapObject.getStyleStack();
            style = new HeadStyle();
            style.fetchStyle(sm, stack);
        }
    }

}
