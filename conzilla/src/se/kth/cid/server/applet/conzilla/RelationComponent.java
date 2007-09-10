/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.util.Vector;

import se.kth.cid.graphics.HeadDraw;
import se.kth.cid.graphics.LineDraw;
import se.kth.cid.style.HeadStyle;
import se.kth.cid.style.LineStyle;
import se.kth.cid.style.OverlayStyle;
/**
 * @author enok
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationComponent {
    Relation relation;
    Vector path;
    LineStyle style;
    HeadStyle hstyle;
    OverlayStyle ostyle;
    Shape head;
    boolean highlight;

    /**
     * 
     */
    public RelationComponent(Relation relation) {
        this.relation = relation;

        int ldt = LineStyle.PATH_TYPE_STRAIGHT;
        highlight = false;
        style = new LineStyle(3.f, this.relation.getLineType());
        ostyle = new OverlayStyle();
        hstyle = new HeadStyle();
        LineStyle hlineStyle=  new LineStyle(3.f, "Continous");
        hstyle.setHeadLineStyle(hlineStyle);

        Point[] points = relation.getPathAsPoints();
        //if (relation.getLineDrawType().equals("LinePathType_Curve")){
        //  ldt = LineStyle.PATH_TYPE_CURVE;
        //}
        ldt = relation.getLineDrawType();
        path = LineDraw.constructPath(points, ldt);
        //LineStyle.PATH_TYPE_STRAIGHT);

        hstyle.setHeadForward(relation.isForwardDirection());
        hstyle.setWidth(4);
        hstyle.setLength(4);
        hstyle.setFilled(false);
        hstyle.setTypeStr(relation.getHeadType());
        head = HeadDraw.constructHead(hstyle);
        head = HeadDraw.rotateHead(head, points, hstyle);
    }

    public void paintComponent(Graphics g) {
        if (g instanceof Graphics2D) {
            if (highlight) {
                ostyle.setLineWidth(5.f);
                ostyle.setForegroundColor(ConceptComponent.HIGHLIGHT);
            } else {
                ostyle.setLineWidth(1f);
                ostyle.setForegroundColor(Color.BLACK);
            }
            LineDraw.paintLine((Graphics2D) g, path, style, ostyle);
            HeadDraw.paintHead(
                (Graphics2D) g,
                head,
                hstyle,
                ostyle,
                MapApplet.BACKGROUND);
        }
    }

    public void highlight(boolean b) {
        highlight = b;
    }

    public Relation getRelation() {
        return relation;
    }
}
