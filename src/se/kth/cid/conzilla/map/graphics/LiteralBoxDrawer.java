/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map.graphics;

import se.kth.cid.graphics.BoxDraw;
import se.kth.cid.graphics.LineDraw;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.style.BoxStyle;
import se.kth.cid.style.LineStyle;

/**
 * @author matthias
 */
public class LiteralBoxDrawer extends BoxDrawer {

    /**
     * Constructor for LiteralBoxDrawer.
     */
    public LiteralBoxDrawer(TripleMapObject tripleMapObject) {
        super(tripleMapObject);
    }    
    
    public void update(TripleMapObject tripleMapObject) {
        //Fix a new Style, set defaults and then let the stylemanager set
        //reasonable values where it can.
        style = new BoxStyle();
        style.setTypeStr("rectangle");
        style.setFilled(false);
        style.setThickness(1);
        style.setBorderType("continuous");
        fixStyle();
        
        //Fetch box and calculate inner, outer and hit boxes.
        ContextMap.BoundingBox bbox = ((StatementLayout) tripleMapObject
                .getDrawerLayout()).getLiteralBoundingBox();
        if (bbox == null)
            return;
        outerBox = convertBoundingBoxToRectangle(bbox);
        hitBox = BoxDraw.discoverHitBox(style, outerBox);
        innerBox = BoxDraw.calculateInnerFromOuterBox(style, outerBox);

        //Update the box's  shape.
        box = BoxDraw.constructBox(style, outerBox);
        boxWithIcon = BoxDraw.appendIcon(style, box, outerBox);
    }

    protected void fixStyle() {
        //super.fixStyle();
        style.setStroke(LineDraw.makeStroke(new LineStyle(3f, "continuous"),null));
        style.setType(BoxStyle.RECTANGLE);
    }
}