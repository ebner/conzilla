/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.applet.conzilla;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author enok
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Relation {
    String URI;

    String type;

    String headType;

    boolean forwardDirection;

    int subject;

    int object;

    String title;

    String desc;

    int[] path;

    int[] xLinePoints;

    int[] yLinePoints;

    String lineType;

    int lineDrawType;

    public Relation(
        String URI,
        String type,
        int subject,
        int object,
        String title,
        String desc) {
        this.URI = URI;
        this.type = type;
        this.subject = subject;
        this.object = object;
        this.title = title;
        this.desc = desc;
    }

    /**
     * @return Returns the direction.
     */
    public boolean isForwardDirection() {
        return forwardDirection;
    }

    /**
     * The direction to set.
     * @param forward
     */
    public void setForwardDirection(boolean forward) {
        this.forwardDirection = forward;
    }

    /**
     * @return Returns the headType.
     */
    public String getHeadType() {
        return headType;
    }

    /**
     * @param headType
     *           The headType to set.
     */
    public void setHeadType(String headType) {
        this.headType = headType;
    }

    public void setPath(int[] path) {
        this.path = path;
        xLinePoints = new int[path.length / 2];
        yLinePoints = new int[path.length / 2];
        for (int i = 0; i < path.length; i++) {
            if (i % 2 == 0)
                xLinePoints[i / 2] = path[i];
            else
                yLinePoints[i / 2] = path[i];
        }
    }

    public int[] getPath() {
        return path;
    }

    public Point[] getPathAsPoints() {
        Point[] points = new Point[xLinePoints.length];
        for (int i = 0; i < xLinePoints.length; i++) {
            points[i] = new Point(xLinePoints[i], yLinePoints[i]);
        }
        return points;
    }

    public int[] getXPointPath() {
        return xLinePoints;
    }

    public int[] getYPointPath() {
        return yLinePoints;
    }

    public String getSurfMap() {
        return null; //Fix me!
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getLineType() {
        return lineType;
    }

    public int getLineDrawType() {
        return lineDrawType;
    }

    public void setLineDrawType(int s) {
        lineDrawType = s;
    }
}