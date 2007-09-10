/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.style.LineStyle;
import se.kth.cid.style.OverlayStyle;

/**
 * Help functionality for drawing lines in Swing.
 * 
 * @author matthias
 */
public class LineDraw {

    public static Stroke makeStroke(LineStyle style, OverlayStyle os) {
        float dist;
        float width;
        float lineWidth = os != null ? os.getLineWidth() : 1f;
        if (style != null) {
        	int finely = style.getType() == LineStyle.FINELYDOTTED ? 2 : 5;
            dist= (float) (finely * Math.sqrt(style.getThickness() * lineWidth));
            width = style.getThickness() * lineWidth / 2.5f;
        } else {
            dist= 0;
            width = 1;            
        }
        
        
        switch (style != null ? style.getType() : -1) {
            case LineStyle.DOTTED:
            case LineStyle.FINELYDOTTED:
            float[] arr = { 0, dist };
            return new BasicStroke(
                width,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                0f,
                arr,
                0f);
            case LineStyle.DASHED:
            float[] arr0 = { dist };
            return new BasicStroke(
                width,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                0,
                arr0,
                0);
                case LineStyle.DASHDOT:
            float[] arr1 = { dist, dist, 0, dist };
            return new BasicStroke(
                width,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                0,
                arr1,
                0);
            case LineStyle.DASHDOTDOT:                
            float[] arr2 = { dist, dist, 0, dist, 0, dist };
            return new BasicStroke(
                width,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                0,
                arr2,
                0);
            case LineStyle.DASHDOTDOTDOT:
            float[] arr3 = { dist, dist, 0, dist, 0, dist, 0, dist };
            return new BasicStroke(
                width,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                0,
                arr3,
                0);
            case LineStyle.CONTINUOUS:
            default:
            return new BasicStroke(
                width,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
        }
    }

    /**
     * Generates a path of shapes following the array of points given.
     * The shapes will be choosen according to the specified path type.
     * 
     * @param line array of {@link Point}s.
     * @return a vector of {@link Shape}s.
     */
    public static Vector constructPath(Point[] line, int pathType) {
        if (line != null && line.length >= 2) {
            Vector path = new Vector();
            switch (pathType) {
                case LineStyle.PATH_TYPE_STRAIGHT :
                    for (int i = 0; i < line.length - 1; i++)
                        path.add(
                            new Line2D.Float(
                                line[i].x,
                                line[i].y,
                                line[i + 1].x,
                                line[i + 1].y));
                    return path;
                case LineStyle.PATH_TYPE_QUAD :
                    for (int i = 0; i < line.length - 1; i += 2)
                        path.add(
                            new QuadCurve2D.Float(
                                line[i].x,
                                line[i].y,
                                line[i + 1].x,
                                line[i + 1].y,
                                line[i + 2].x,
                                line[i + 2].y));
                    return path;
                case LineStyle.PATH_TYPE_CURVE :
                    for (int i = 0; i < line.length - 1; i += 3)
                        path.add(
                            new CubicCurve2D.Float(
                                line[i].x,
                                line[i].y,
                                line[i + 1].x,
                                line[i + 1].y,
                                line[i + 2].x,
                                line[i + 2].y,
                                line[i + 3].x,
                                line[i + 3].y));
                    return path;
            }
        }
        return null;
    }

    /**
     * Checks wether a point is on a path.
     * 
     * @param x 
     * @param y
     * @param path a vector of Shapes to check intersection against.
     * @return an integer telling which segment of the path that was intersected,
     * -1 is returned if no intersection occured.
     */
    public static int findIntersectionOnPath(int x, int y, Vector path) {
        for (int i = 0; i < path.size(); i++)
            if (((Shape) path.elementAt(i))
                .intersects(x - 3, y - 3, 6.1, 6.1)) {
                return i;
            }
        return -1;
    }

    /**
     * Paints a path on the graphics object using the LineStyle and OverlayStyle
     * for determining colors, linewidth and the like. 
     * 
     * @param g
     * @param path
     * @param style
     * @param os specifies color and linewidth to be used, may be null.
     */
    public static void paintLine(Graphics2D g, Vector path, LineStyle style, OverlayStyle os) {
        Stroke s = g.getStroke();

        if (os != null) {
            g.setColor(os.getForegroundColor());
        } else {
            g.setColor(Color.black);
        }
    
        if (os != null && os.isLineWidthModified()) {
            g.setStroke(
                makeStroke(style,os));
        } else {
            g.setStroke(style.getStroke());
        }
    
        Iterator segments = path.iterator();
        while (segments.hasNext())
            g.draw(((Shape) segments.next()));
        
        if (os.isMarked()) {
            if (path.size() % 2 == 1) {
                Shape line = (Shape) path.elementAt(path.size()/2);
                if (line instanceof Line2D) {
                    Point2D point = ((Line2D) line).getP1();
                    Point2D point2 = ((Line2D) line).getP2();
                    int x = (int) (point.getX() + ((point2.getX()-point.getX())/2));
                    int y = (int) (point.getY() + ((point2.getY()-point.getY())/2));
                    g.fill(new Ellipse2D.Double(x-3,y-3,6,6));
                } else if (line instanceof CubicCurve2D) {
                    CubicCurve2D curveL = new CubicCurve2D.Double();
                    ((CubicCurve2D) line).subdivide(curveL, new CubicCurve2D.Double());
                    g.fill(new Ellipse2D.Double(curveL.getX2()-3,curveL.getY2()-3,6,6));                    
                } else {
                    throw new RuntimeException("Unsupported line type");
                }
            } else {
                Shape line = (Shape) path.elementAt(path.size()/2-1);
                Point2D point = null;
                if (line instanceof Line2D) {
                     point = ((Line2D) line).getP2();
                } else if (line instanceof CubicCurve2D) {
                    point = ((CubicCurve2D) line).getP2();
                } else {
                    throw new RuntimeException("Unsupported line type");
                }
                g.fill(new Ellipse2D.Double(point.getX()-3,point.getY()-3,6,6));
            }
        }
        
        g.setStroke(s);
    }
}
