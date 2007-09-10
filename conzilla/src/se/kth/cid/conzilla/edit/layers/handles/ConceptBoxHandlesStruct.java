/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */

public class ConceptBoxHandlesStruct extends BoxHandlesStruct {

    public ConceptBoxHandlesStruct(DrawerLayout ns, GridModel gridModel) {
        super(ns, gridModel, ns.getBoundingBox());
    }

    protected void setImpl(ContextMap.BoundingBox bb) {
        drawerLayout.setBoundingBox(bb);
    }
}
