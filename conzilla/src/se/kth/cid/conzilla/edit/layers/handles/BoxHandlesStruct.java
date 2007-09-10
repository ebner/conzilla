/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.awt.Graphics2D;

import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */

public abstract class BoxHandlesStruct {
    public DrawerLayout drawerLayout;

    public BoxHandle ul;
    public BoxHandle ur;
    public BoxHandle lr;
    public BoxHandle ll;
    public BoxTotalHandle tot;

    public BoxHandlesStruct(DrawerLayout ns, GridModel gridModel, ContextMap.BoundingBox bb) {
        this.drawerLayout = ns;
				initStruct(bb, gridModel);
    }
    
    protected void initStruct(ContextMap.BoundingBox bb, GridModel gridModel) {
        ul = new BoxHandle(bb.pos, gridModel); //upper left
        ur =
            new BoxHandle(
                new ContextMap.Position(bb.pos.x + bb.dim.width, bb.pos.y),
                gridModel);
        //upper right
        lr =
            new BoxHandle(
                new ContextMap.Position(
                    bb.pos.x + bb.dim.width,
                    bb.pos.y + bb.dim.height),
                gridModel);
        //lower right
        ll =
            new BoxHandle(
                new ContextMap.Position(bb.pos.x, bb.pos.y + bb.dim.height),
                gridModel);
        //lower left
        tot = new BoxTotalHandle(ul, ur, lr, ll);
        ul.addNeighbours(ll, ur, lr, tot, true, true);
        ur.addNeighbours(lr, ul, ll, tot, true, false);
        ll.addNeighbours(ul, lr, ur, tot, false, true);
        lr.addNeighbours(ur, ll, ul, tot, false, false);
    }

    public void set() {
        ContextMap.Position pos = ul.getPosition();
        ContextMap.BoundingBox bb =
            new ContextMap.BoundingBox(
                pos.x,
                pos.y,
                ur.rectangle.x - ul.rectangle.x,
                ll.rectangle.y - ul.rectangle.y);
        
        setImpl(bb);
    }
		
		protected abstract void setImpl(ContextMap.BoundingBox bb);
		
    public void paint(Graphics2D g) {
        if (ul.getParent() != null) {
            ul.paint(g);
            ur.paint(g);
            lr.paint(g);
            ll.paint(g);
        }
        if (tot.getParent() != null)
            tot.paint(g);
    }
}
