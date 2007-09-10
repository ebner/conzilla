/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.util.Collection;
import java.util.Iterator;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.edit.LineTool;
import se.kth.cid.conzilla.edit.TieTool;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.util.Tracer;

public class HandledBoxLine extends HandledLine {
    public HandledBoxLine(MapEvent m, LineTool linetool, TieTool tietool,
            HandleStore store) {
        super(m, linetool, tietool, store);
    }

    protected void loadModel() {
        BoxLineHandlesStruct nlhs = store.getBoxLineHandles((StatementLayout) mapObject
                .getDrawerLayout());
        Collection col = nlhs.getDraggers(true);
        addHandles(col);

        //Fix the segments...
        Iterator it = col.iterator();
        Handle firstHandle = (Handle) it.next();
        Handle secondHandle;
        int segment = 0;
        do {
            Tracer.debug("segment " + segment);
            secondHandle = (Handle) it.next();
            addHandle(new SegmentHandle.ConceptLineSegmentHandle(firstHandle,
                    secondHandle, segment, mapObject));
            firstHandle = secondHandle;
            segment++;
        } while (it.hasNext());

        DrawerLayout owner = mapObject.getDrawerLayout();
        //	addHandles(store.getAndSetTripleCenterFollowers(mapObject.getConceptLayout()));
        addHandles(store.getAndSetBoxFollowers(owner));
        if (owner.getBodyVisible())
            addHandle(store.getBoxHandlesStruct(mapObject.getDrawerLayout()).tot);
    }

    public void click(MapEvent m) {
        chooseHandle(m);
        if (linetool.isActivated()) {
            if (currentHandle instanceof SegmentHandle) {
                currentHandle.setSelected(false);
                currentHandle = null;
            }

            if (currentHandle == null) {
                BoxLineHandlesStruct nlhs = store.getBoxLineHandles((StatementLayout) mapObject
                        .getDrawerLayout());
                DefaultHandle h = new DefaultHandle(new ContextMap.Position(
                        m.mapX, m.mapY));
                nlhs.handles.insertElementAt(h, m.lineSegmentNumber + 1);
                reloadModel();
            } else {
                BoxLineHandlesStruct nlhs = store.getBoxLineHandles((StatementLayout) mapObject
                        .getDrawerLayout());
                if (nlhs.getFirstHandle() != currentHandle
                        && nlhs.getLastHandle() != currentHandle) {
                    nlhs.handles.remove(currentHandle);
                    reloadModel();
                }
            }
        }
    }

    protected void updateControlPointsImpl() {
        updateControlPoints(((StatementLayout) mapObject.getDrawerLayout()).getBoxLinePathType());
    }

    public boolean update(EditEvent e) {
        switch (e.getEditType()) {
        /*
         * case ConceptLayout.TRIPLESTYLE_REMOVED: reloadModel(); return true;
         */
        case ContextMap.RESOURCELAYOUT_REMOVED:
            String target = (String) e.getTarget();
            if (target.equals(mapObject.getDrawerLayout().getURI()))
                return false;
            else
                reloadModel();
            return true;
        case DrawerLayout.BOXLINE_EDITED:
            if (e.getEditedObject() == mapObject.getDrawerLayout())
                if (e.getTarget() == null)
                    return false;
                else
                    reloadModel();
            return true;
        //	    case ConceptLayout.TRIPLESTYLE_ADDED:
        case StatementLayout.LINE_EDITED:
        case ConceptLayout.BOUNDINGBOX_EDITED:
            if (tieTool.isActivated())
                reloadModel();
            return true;
        case ConceptLayout.BODYVISIBLE_EDITED:
            if (e.getEditedObject() == mapObject.getDrawerLayout())
                if (!((Boolean) e.getTarget()).booleanValue())
                    return false;
        }
        return true;
    }

}