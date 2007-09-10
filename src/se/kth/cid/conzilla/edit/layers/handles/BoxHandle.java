/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.layout.ContextMap;

/** A Boxhandle is one corner of a box, if moved it moves the other corners acordingly. 
 *  If there are any follower handles they are moved as well (in a relative fashion).
 *
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class BoxHandle extends DefaultHandle {
    private BoxHandle xcomposant;
    private BoxHandle ycomposant;
    private BoxHandle oppositehandle;
    private BoxTotalHandle boxTotalHandle;
    private int swidth = 1, sheight = 1;
    private int xoffset = 0;
    private int yoffset = 0;
    private boolean xRel = true;
    private boolean yRel = true;
    private GridModel gridModel = null;

    public BoxHandle(ContextMap.Position p, GridModel gridModel) {
        super(p, true);
        this.gridModel = gridModel;
    }

    public void addNeighbours(
        BoxHandle xcomposant,
        BoxHandle ycomposant,
        BoxHandle oppositehandle,
        BoxTotalHandle boxTotalHandle,
        boolean upper,
        boolean left) {
        this.xcomposant = xcomposant;
        this.ycomposant = ycomposant;
        this.oppositehandle = oppositehandle;
        this.boxTotalHandle = boxTotalHandle;

        clearEdited();
        if (left)
            swidth = -1;
        if (upper)
            sheight = -1;
    }

    public void clearEdited() {
        super.clearEdited();
        xoffset = rectangle.x - oppositehandle.rectangle.x;
        yoffset = rectangle.y - oppositehandle.rectangle.y;
        if (xoffset == 0)
            xRel = false;
        if (yoffset == 0)
            yRel = false;
    }

    public Collection drag(int x, int y) {
        //Take care of case when some of the other corners are selected....

        xoffset += x;
        yoffset += y;

        if (xoffset * swidth < 0)
            x = oppositehandle.rectangle.x - rectangle.x;
        else
            x = xoffset + oppositehandle.rectangle.x - rectangle.x;
        if (yoffset * sheight < 0)
            y = oppositehandle.rectangle.y - rectangle.y;
        else
            y = yoffset + oppositehandle.rectangle.y - rectangle.y;

        //Drag all followers.
        Collection cols = dragfollowers(x, y);

        //Good to have if totalhandle is visible, se below.
        Rectangle oldTotal =
            boxTotalHandle.getParent() != null
                ? boxTotalHandle.getRectangle()
                : null;

        //Drag the corners...
        Collection col;
        col = dragForced(x, y);
        if (col != null)
            cols.add(col);
        else
            col = new Vector();
        col = xcomposant.dragForced(x, 0);
        if (col != null)
            cols.add(col);
        col = ycomposant.dragForced(0, y);
        if (col != null)
            cols.add(col);

        //Take the union of all collections.
        Vector vec = new Vector();

        //If totalhandle is visible...
        if (oldTotal != null) {
            vec.addElement(oldTotal);
            vec.addElement(boxTotalHandle.getRectangle());
        }

        Iterator it = cols.iterator();
        for (; it.hasNext();)
            vec.addAll((Collection) it.next());

        return vec;
    }

    public Collection dragUnTied(int x, int y) {
        Collection cols = new Vector();
        Collection col = super.dragForced(x, y);
        if (col != null)
            cols.addAll(col);
        col = xcomposant.dragForced(x, 0);
        if (col != null)
            cols.addAll(col);
        col = ycomposant.dragForced(0, y);
        if (col != null)
            cols.addAll(col);
        return cols;        
    }

    /** Drags the followers relative to how the box is resized.
     *  Overrides the @link HandleWithFollowers.dragFollowers function
     *  which only moves all followers an equal amount.
     */
    public Collection dragfollowers(int x, int y) {
        Collection cols = new Vector();
        if (followHandles == null)
            return cols;

        //Calculate relative change
        float xrelative =
            ((float) (rectangle.x + x - oppositehandle.rectangle.x))
                / ((float) (originalPosition.x
                    - oppositehandle.rectangle.x
                    - radius));
        float yrelative =
            ((float) (rectangle.y + y - oppositehandle.rectangle.y))
                / ((float) (originalPosition.y
                    - oppositehandle.rectangle.y
                    - radius));

        Collection col;
        Iterator it = followHandles.iterator();
        while (it.hasNext()) {
            DefaultHandle followhandle = (DefaultHandle) it.next();
            ContextMap.Position pos = followhandle.getOriginalPosition();
            int xpos =
                (int) ((pos.x - radius - oppositehandle.rectangle.x)
                    * xrelative)
                    + oppositehandle.rectangle.x;
            int ypos =
                (int) ((pos.y - radius - oppositehandle.rectangle.y)
                    * yrelative)
                    + oppositehandle.rectangle.y;
            if (gridModel != null && gridModel.isGridOn()) {
                int grad = gridModel.getGranularity();
                int hgrad = grad / 2;

                xpos = (xpos + radius + hgrad) / grad * grad - radius;
                ypos = (ypos + radius + hgrad) / grad * grad - radius;
            }

            if (xRel)
                if (yRel)
                    col =
                        followhandle.dragForced(
                            xpos - followhandle.rectangle.x,
                            ypos - followhandle.rectangle.y);
                else
                    col =
                        followhandle.dragForced(
                            xpos - followhandle.rectangle.x,
                            0);
            else if (yRel)
                col =
                    followhandle.dragForced(0, ypos - followhandle.rectangle.y);
            else
                col = null;

            if (col != null)
                cols.add(col);
        }
        return cols;
    }
}
