/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import se.kth.cid.style.HeadStyle;
import se.kth.cid.style.OverlayStyle;

/**
 * Help functionality for drawing the head of a line in Swing.
 * 
 * @author matthias
 */
public class HeadDraw {


    /**
     * Paints the head of a line on the graphics object using the HeadStyle and OverlayStyle
     * for determining colors, linewidth and the like. 
     * 
     * @param g
     * @param head
     * @param style
     * @param ostyle
     * @param mapColor needed in the case the head should be hollow 
     * (we need to paint over the to far drawn line below).
     */
    public static void paintHead(Graphics2D g, Shape head, HeadStyle style, OverlayStyle ostyle, Color mapColor) {
        Stroke s = g.getStroke();
        if (ostyle != null && ostyle.isLineWidthModified())
            g.setStroke(LineDraw.makeStroke(style.getHeadLineStyle(), ostyle));
        else
            g.setStroke(style.getStroke());
    
        if (style.isFilled() && style.isClearFill()) {
            g.setColor(ostyle.getForegroundColor());
            g.draw(head);
            g.fill(head);
        } else if (style.isClearFill()) {
            g.setColor(mapColor);
            g.fill(head);
            g.setColor(ostyle.getForegroundColor());
            g.draw(head);
        } else {
            g.setColor(ostyle.getForegroundColor());
            g.draw(head);
        }
        g.setStroke(s);
    }

    public static Shape constructHead(HeadStyle style) {
        GeneralPath path;
        switch (style.getType()) {
            case HeadStyle.NONE :
                return null;
            case HeadStyle.VARROW :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
                path.moveTo(0f, 0f);
                path.lineTo(1f, 0.5f);
                path.lineTo(0f, 1f);
                path.lineTo(1f, 0.5f);
                return path;
            case HeadStyle.ARROW :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
                path.moveTo(0f, 0f);
                path.lineTo(1f, 0.5f);
                path.lineTo(0f, 1f);
                path.closePath();
                return path;
            case HeadStyle.SHARPARROW :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(0, 0);
                path.lineTo(1f, 0.5f);
                path.lineTo(0f, 1f);
                path.lineTo(0.25f, 0.5f);
                path.closePath();
                return path;
            case HeadStyle.BLUNTARROW :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(0.25f, 0f);
                path.lineTo(1f, 0.5f);
                path.lineTo(0.25f, 1f);
                path.lineTo(0f, 0.5f);
                path.closePath();
                return path;
            case HeadStyle.DIAMOND :
                path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
                path.moveTo(0.5f, 0f);
                path.lineTo(1f, 0.5f);
                path.lineTo(0.5f, 1f);
                path.lineTo(0f, 0.5f);
                path.closePath();
                return path;
            case HeadStyle.ELLIPSE :
            default :
                return new Ellipse2D.Double(0, 0, 1, 1);
        }
    }

    /**
     * @param line
     * @param head
     */
    public static Shape rotateHead(Shape head, Point[] line, HeadStyle style) {
        if (line.length < 2) {
            GeneralPath shape = new GeneralPath();
            style.setClearFill(false);
            return shape;
        }
    
        Point nTip;
        Point tip;
        if (style.isHeadForward()) {
            nTip = line[1];
            tip = line[0];
        } else {
            tip = line[line.length - 1];
            nTip = line[line.length - 2];
        }
    
        int xLen = tip.x - nTip.x;
        int yLen = tip.y - nTip.y;
    
        Point2D arrowTip = new Point2D.Double(1, 0.5);
    
        if (head != null) {
            AffineTransform transform =
                AffineTransform.getTranslateInstance(tip.x, tip.y);
            transform.rotate(Math.atan2(yLen, xLen));
            transform.scale(style.getLength() * 3, style.getWidth() * 3);
            transform.translate(-arrowTip.getX(), -arrowTip.getY());
    
            return transform.createTransformedShape(head);
        } else {
            return null;
        }
    }

}
