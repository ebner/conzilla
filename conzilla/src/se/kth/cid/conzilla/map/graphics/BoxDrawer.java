/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.graphics.BoxDraw;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.style.BoxStyle;
import se.kth.cid.style.StyleManager;


public class BoxDrawer extends MapDrawer {
    
    Rectangle outerBox;
    Rectangle outerBorderBox;
    Rectangle innerBorderBox;
    Rectangle hitBox;
    Rectangle innerBox;

    Shape box;
    Shape boxWithIcon;

    BoxStyle style;
    DrawerMapObject drawerMapObject;

    public BoxDrawer(DrawerMapObject drawerMapObject) {
        super(drawerMapObject.getDisplayer());
        this.drawerMapObject = drawerMapObject;
    }

    public boolean getErrorState() {
        return drawerMapObject.getErrorState();
    }

    public void doPaint(Graphics2D g, Mark mark) {
        BoxDraw.doPaint(g, box, innerBox, outerBox, style, mark);
    }

    public Rectangle2D getInnerBoundingBox() {
        return innerBox;
    }

    public Rectangle getBoundingBox() {
        return outerBox;
    }

    public Rectangle getOuterBorderedBoundingBox() {
        if (outerBorderBox == null)
            outerBorderBox =
                BoxDraw.calculateBorderBox(outerBox, style, null).getBounds();
        return outerBorderBox;
    }

    public void update(DrawerMapObject drawerMapObject) {
        //Fix a new style
        style = new BoxStyle(); //Contains sensible defaults.
        if (drawerMapObject instanceof TripleMapObject)
            style.setTypeStr("invisible");
        fixStyle();  //The stylemanager retrieves sensible values.

        
        //Fetch box and calculate inner, outer and hit boxes.
        ContextMap.BoundingBox bbox =
            drawerMapObject.getDrawerLayout().getBoundingBox();
        if (bbox == null)
            return;
        
        //If allowed zero the intersection check in didHit function
        //will always fail. Hence the object will never be selected.
        if (bbox.dim.width == 0) {
            bbox.dim.width = 1;
        }
        if (bbox.dim.height == 0) {
            bbox.dim.height = 1;
        }
        outerBox = convertBoundingBoxToRectangle(bbox);
        hitBox = BoxDraw.discoverHitBox(style, outerBox);
        innerBox = BoxDraw.calculateInnerFromOuterBox(style, outerBox);
        
        //Update the box's  shape.
        box = BoxDraw.constructBox(style, outerBox);
        boxWithIcon = BoxDraw.appendIcon(style, box, outerBox);
    }
    
    protected void fixStyle() {
        //      BoxStyle bs = drawerMapObject.getBoxStyle();
        StyleManager sm = drawerMapObject.getStyleManager();
        java.util.List stack = drawerMapObject.getStyleStack();

        style.fetchStyle(sm, stack, drawerMapObject instanceof TripleMapObject);
    }

    public boolean didHit(MapEvent m) {
        if (hitBox == null)
            return false;
        
        if (hitBox.contains(m.mapX, m.mapY)) {           
            if (box.contains(m.mapX, m.mapY)) {
                return true;
            } else {
                Rectangle rect = new Rectangle(m.mapX-4, m.mapY-4, 8,8);
                return boxWithIcon.intersects(rect);
            }
        }
        return false;
    }

    protected Rectangle convertBoundingBoxToRectangle(ContextMap.BoundingBox bbox) {
        return new Rectangle(
                bbox.pos.x,
                bbox.pos.y,
                bbox.dim.width,
                bbox.dim.height);
    }
}
