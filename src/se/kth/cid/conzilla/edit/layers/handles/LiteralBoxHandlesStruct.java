/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.StatementLayout;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */

public class LiteralBoxHandlesStruct extends BoxHandlesStruct {
    public LiteralBoxHandlesStruct(StatementLayout ns, GridModel gridModel) {
        super(ns, gridModel, ns.getLiteralBoundingBox());
    }

    public void setImpl(ContextMap.BoundingBox bb) {
        ((StatementLayout) drawerLayout).setLiteralBoundingBox(bb);
    }
}
