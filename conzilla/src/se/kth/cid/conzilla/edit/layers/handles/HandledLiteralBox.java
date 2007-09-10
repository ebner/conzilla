/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.edit.TieTool;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class HandledLiteralBox
    extends HandledObject
    implements PropertyChangeListener {

    protected MapEvent mapevent;
    protected Vector followHandles;
    protected HandleStore store;

    public HandledLiteralBox(MapEvent m, TieTool tieTool, HandleStore store) {
        super(m.mapObject, tieTool);
        this.store = store;
        mapevent = m;
        tieTool.addPropertyChangeListener(this);
        loadModel();
    }
    
    public boolean isWithinLiteralHandle(MapEvent m) {
        BoxHandlesStruct nbhs =
            store.getLiteralBoxHandlesStruct((StatementLayout) mapObject.getDrawerLayout());
        return nbhs.tot.contains(m);
    }
    
    protected void reloadModel() {
        removeAllHandles();
        loadModel();
    }

    protected void loadModel() {
        followHandles = new Vector();

        BoxHandlesStruct nbhs =
            store.getLiteralBoxHandlesStruct((StatementLayout) mapObject.getDrawerLayout());

        Collection followHandles = new Vector();
        if (tieTool.isActivated())
            followHandles.add(
                store.getAndSetLiteralBoxFollower(
                    (StatementLayout) mapObject.getDrawerLayout()));

        addHandle(nbhs.lr);
        nbhs.lr.setFollowers(followHandles);

        addHandle(nbhs.ll);
        nbhs.ll.setFollowers(followHandles);

        addHandle(nbhs.ur);
        nbhs.ur.setFollowers(followHandles);

        addHandle(nbhs.ul);
        nbhs.ul.setFollowers(followHandles);

        addHandles(followHandles);

        addHandle(nbhs.tot);

    }

    public boolean update(EditEvent e) {
        DrawerLayout ns = mapObject.getDrawerLayout();
        switch (e.getEditType()) {
            case ContextMap.RESOURCELAYOUT_REMOVED :
                String target = (String) e.getTarget();
                if (target.equals(ns.getURI()))
                    return false;
                break;
            case DrawerLayout.LITERAL_BOUNDINGBOX_EDITED :
                reloadModel();
        }
        return true;
    }

    public void propertyChange(PropertyChangeEvent e) {
        reloadModel();
    }
    public void detach() {
        tieTool.removePropertyChangeListener(this);
    }
}
