/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.Dimension;
import java.awt.geom.CubicCurve2D;
import java.util.Collection;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.style.LineStyle;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class LayoutUtils {
	
	static Log log = LogFactory.getLog(LayoutUtils.class);

    /** Calculates the best initial tripleline between the subject and object DrawerLayouts.
     *
     *  @param subjectEnd is the subject DrawerLayout of the triple.
     *  @param objectEnd is the object DrawerLayout of the triple.
     *  @param gm is the gridmodel used to help with positioning.
     *  @return a line in the form of an array of length two containing two ConceptMap.Position.
     */
    public static ContextMap.Position[] tripleLine(
        DrawerLayout subjectEnd,
        DrawerLayout objectEnd,
        GridModel gm) {
        ContextMap.Position[] pos = new ContextMap.Position[2];
        pos[1] = findPosition_FirstFromBody(subjectEnd, null, gm);
        if (pos[1] == null) {
            log.warn("Trying to add triple to non visible ConceptLayout, despite the fact it is choosen by a mapEvent - inconsistant!");
        }
        pos[0] = findPosition_FirstFromBody(objectEnd, pos[0], gm);

        return pos;
    }

    /** Calculates the best initial boxline for a ConceptLayout.
     *  The ConceptLayout has to be visible somehow.
     *
     *  @param owner is the ConceptLayout were the triple belongs.
     *  @param mapEvent is the event that sets the boxline visibel (has to be over the Owner.
     */
    public static ContextMap.Position[] boxLine(
        StatementLayout owner,
        MapEvent mapEvent,
        GridModel gm) {
        if (owner.getBoxLinePathType() == LineStyle.PATH_TYPE_STRAIGHT) {
            ContextMap.Position[] pos = new ContextMap.Position[2];
//            ContextMap.Position press = getPosition(mapEvent);
            pos[0] = findPosition_FromTriples(owner);
            if (pos[0] == null)
                pos[0] = getPosition(mapEvent);
            pos[1] = findPosition_FirstFromBody(owner, pos[0], gm);
            if (pos[0] == null)
                pos[0] = getPosition(mapEvent);
            return pos;
        } else {
            ContextMap.Position[] pos = new ContextMap.Position[4];
//            ContextMap.Position press = getPosition(mapEvent);
            pos[0] = findPosition_FromTriples(owner);
            if (pos[0] == null)
                pos[0] = getPosition(mapEvent);
            pos[3] = findPosition_FirstFromBody(owner, pos[0], gm);
            if (pos[0] == null)
                pos[0] = getPosition(mapEvent);

            pos[1] =
                new ContextMap.Position(
                    (int) (pos[0].x + ((pos[3].x - pos[0].x) * 0.3)),
                    (int) (pos[0].y + ((pos[3].y - pos[0].y) * 0.3)));
            pos[2] =
                new ContextMap.Position(
                    (int) (pos[0].x + ((pos[3].x - pos[0].x) * 0.7)),
                    (int) (pos[0].y + ((pos[3].y - pos[0].y) * 0.7)));
            return pos;
        }
    }

    /** Use this function when you want a box on the grid.
     *
     *  @param gridModel  the current gridmodel.
     *  @param prefX  the preferred x-position for the upper left corner of the box.
     *  @param prefY  the preferred y-position for the upper left corner of the box.
     *  @param prefDim the preferred dimension, the box will have at least this size.
     */
    public static ContextMap.BoundingBox preferredBoxOnGrid(
        GridModel gridModel,
        int prefX,
        int prefY,
        Dimension prefDim) {
        ContextMap.BoundingBox box =
            new ContextMap.BoundingBox(
                prefX,
                prefY,
                prefDim.width,
                prefDim.height);
        onGridMinimumSize(box, gridModel);
        return box;
    }

    public static ContextMap.Position onGrid(
        ContextMap.Position pos,
        GridModel gridmodel) {

        pos.x += (int) gridmodel.getGranularity() / 2.0;
        pos.y += (int) gridmodel.getGranularity() / 2.0;
        pos.x -= pos.x % gridmodel.getGranularity();
        pos.y -= pos.y % gridmodel.getGranularity();
        return pos;
    }
    public static ContextMap.Dimension onGrid(
        ContextMap.Dimension dim,
        GridModel gridmodel) {
        dim.width += (int) gridmodel.getGranularity() / 2.0;
        dim.height += (int) gridmodel.getGranularity() / 2.0;
        dim.width -= dim.width % gridmodel.getGranularity();
        dim.height -= dim.height % gridmodel.getGranularity();
        return dim;
    }
    public static ContextMap.BoundingBox onGrid(
        ContextMap.BoundingBox bb,
        GridModel gridmodel) {
        onGrid(bb.pos, gridmodel);
        onGrid(bb.dim, gridmodel);
        return bb;
    }
    public static ContextMap.BoundingBox onGridMinimumSize(
        ContextMap.BoundingBox bb,
        GridModel gridmodel) {
        onGrid(bb.pos, gridmodel);
        onGrid(bb.dim, gridmodel);
        return bb;
    }
    protected static ContextMap.Position getPosition(MapEvent mapEvent) {
        return new ContextMap.Position(mapEvent.mapX, mapEvent.mapY);
    }

    /** Calculates the middle of the triplesLayouts inner end.
     *
     *  @return a position, may be null if the ConceptLayout doesn't have any StatementLayouts. 
     */
    public static ContextMap.Position findPosition_FromTriples(
        ResourceLayout tripleOwner) {
        
        if (! (tripleOwner instanceof StatementLayout)) {
            return null;
        }
        StatementLayout sl = (StatementLayout) tripleOwner; 
        ContextMap.Position [] pos = sl.getLine();
        int pathType = sl.getPathType();
        int nrOfSegments = pathType == LineStyle.PATH_TYPE_STRAIGHT 
            ? pos.length -1 
                    : (pos.length -1) / 3;
        
        if (nrOfSegments % 2 == 1) {
            if (pathType == LineStyle.PATH_TYPE_STRAIGHT) {
                ContextMap.Position point = pos[pos.length/2-1];
                ContextMap.Position point2 = pos[pos.length/2];
                int x = (int) (point.x + ((point2.x-point.x)/2));
                int y = (int) (point.y + ((point2.y-point.y)/2));
                return new ContextMap.Position(x,y);
            } else {
                int start = pos.length / 2 -2;
                CubicCurve2D curveL = new CubicCurve2D.Double(
                        pos[start].x,pos[start].y,
                        pos[start+1].x,pos[start+1].y,
                        pos[start+2].x,pos[start+2].y,
                        pos[start+3].x,pos[start+3].y);
                CubicCurve2D halvCurve = new CubicCurve2D.Double();
                ((CubicCurve2D) curveL).subdivide(halvCurve, new CubicCurve2D.Double());
                return new ContextMap.Position((int) halvCurve.getX2(),
                        (int) halvCurve.getY2());
            }
        } else {
            return pos[(pos.length -1) / 2];
        }
    }

    /** Calcualtes the best line-end-point on the ConceptLayout, 
     *  i.e. chooses first from triple-middle then box and last it's boxline. 
     *
     * @see #findPosition_FromBody(DrawerLayout, ContextMap.Position, GridModel)
     * @see #findPosition_FromTriples(ResourceLayout)
     */
    public static ContextMap.Position findPosition_FirstFromTriples(
        ResourceLayout tripleOwner,
        ContextMap.Position press,
        GridModel gm) {
        ContextMap.Position pos;
        pos = findPosition_FromTriples(tripleOwner);
        if (pos != null && gm != null && gm.isGridOn()) {
            int gran = gm.getGranularity();
            pos.x = (pos.x + gran / 2) / gran * gran;
            pos.y = (pos.y + gran / 2) / gran * gran;
        }

        if (pos == null
            && tripleOwner instanceof StatementLayout
            && ((DrawerLayout) tripleOwner).getBodyVisible()) {
            StatementLayout sl = (StatementLayout) tripleOwner;
            ContextMap.Position[] line = sl.getBoxLine();
            pos = new ContextMap.Position(0, 0);
            if (line != null) {
                pos.x = line[0].x;
                pos.y = line[0].y;
            } else
                pos = findPosition_FromBody(sl, press, gm);
        }
        return pos;
    }

    /** Calculates the best line-end-point on the ConceptLayout, 
     *  i.e. chooses first from box, then boxline and last triples-middle.
     *
     * @see #findPosition_FromBody(DrawerLayout, ContextMap.Position, GridModel)
     * @see #findPosition_FromTriples(ResourceLayout)
     */
    public static ContextMap.Position findPosition_FirstFromBody(
        DrawerLayout bodyOwner,
        ContextMap.Position press,
        GridModel gm) {
        ContextMap.Position pos = findPosition_FromBody(bodyOwner, press, gm);
        if (pos == null)
            pos = findPosition_FromTriples(bodyOwner);
        return pos;
    }

	  public static ContextMap.Position findPosition_FromLiteral(StatementLayout boxOwner, ContextMap.Position press, GridModel gm) {
    	return findPosition_FromBoundingBox(boxOwner.getLiteralBoundingBox(), press, gm);
  	}


    /** Calculates the best point on the body given the other end of the line, 
     *  i.e first box and then boxline.
     * 
     *  @param boxOwner the box to find a closeby position from.
     *  @param press an initital coordinate telling the other end of the line, if null the upper left corner of the box is returned.
     *  @param gm used for better positioning.
     *  @return a position, null if the box isn't visible.
     */
    protected static ContextMap.Position findPosition_FromBody(
        DrawerLayout boxOwner,
        ContextMap.Position press,
        GridModel gm) {
        if (boxOwner.getBodyVisible())
            return findPosition_FromBoundingBox(
                boxOwner.getBoundingBox(),
                press,
                gm);
        else
            return null;
    }
    
    protected static ContextMap.Position findPosition_FromBoundingBox(
        ContextMap.BoundingBox bb,
        ContextMap.Position press,
        GridModel gm) {
        int gran = 1;
        int halfgran = 0;
        if (gm != null && gm.isGridOn()) {
            gran = gm.getGranularity();
            halfgran = gran / 2;
        }

        ContextMap.Position pos = new ContextMap.Position(0, 0);
        if (press == null) {
            pos.x = bb.pos.x;
            pos.y = bb.pos.y;
            return pos;
        }
        if (press.x < bb.pos.x || press.x > bb.pos.x + bb.dim.width) {
            pos.x = bb.pos.x + (press.x < bb.pos.x ? 0 : bb.dim.width);

            if (press.y < bb.pos.y)
                pos.y = bb.pos.y;
            else if (press.y > bb.pos.y + bb.dim.height)
                pos.y = bb.pos.y + bb.dim.height;
            else
                pos.y = (press.y + halfgran) / gran * gran;
        } else {
            if (press.y < bb.pos.y) {
                pos.x = (press.x + halfgran) / gran * gran;
                pos.y = bb.pos.y;
            } else if (press.y > bb.pos.y + bb.dim.height) {
                pos.x = (press.x + halfgran) / gran * gran;
                pos.y = bb.pos.y + bb.dim.height;
            } else {
                pos.x = bb.pos.x;
                pos.y = bb.pos.y;
            }
        }
        return pos;
    }

    public static ContextMap.Position findMiddleOfConceptLayouts_RegardingBody(
        ResourceLayout ns,
        Collection finished_locations,
        GridModel gm) {
        int x = 0, y = 0;
        int counter = 0;
        Enumeration en = ns.children();
        while (en.hasMoreElements()) {
            ResourceLayout re = (ResourceLayout) en.nextElement();
            if (!(re instanceof StatementLayout))
                continue;
            StatementLayout sl = (StatementLayout) re;
            if (!finished_locations.contains(sl.getObjectLayout()))
                continue;
            ContextMap.BoundingBox bb = sl.getObjectLayout().getBoundingBox();
            x += bb.pos.x + bb.dim.width / 2;
            y += bb.pos.y + bb.dim.height / 2;
            counter++;
        }
        ContextMap.Position point1 =
            new ContextMap.Position((int) (x / counter), (int) (y / counter));
        ContextMap.Position point2 =
            ns instanceof DrawerLayout
                ? findPosition_FromBody((DrawerLayout) ns, point1, gm)
                : null;
        return point2 == null ? point1 : point2;
    }

}