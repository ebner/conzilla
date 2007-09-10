/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.edit.LineTool;
import se.kth.cid.conzilla.edit.TieTool;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.style.LineStyle;

public class HandledLine extends HandledObject implements
        PropertyChangeListener {
    protected MapEvent mapevent;

    protected LineTool linetool;

    protected HandleStore store;

    protected Handle controller1, controller2;

    public HandledLine(MapEvent m, LineTool linetool, TieTool tietool,
            HandleStore store) {
        super(m.mapObject, tietool);
        this.store = store;
        mapevent = m;
        this.linetool = linetool;
        tietool.addPropertyChangeListener(this);
        //    setSingleSelection(true);
        loadModel();
    }

    public boolean isWithinBoxHandle(MapEvent m) {
        if (!mapObject.getDrawerLayout().getBodyVisible()) {
            return false;
        }
        BoxHandlesStruct nbhs = store.getBoxHandlesStruct(mapObject
                .getDrawerLayout());
        return nbhs.tot.contains(m);
    }

    protected void reloadModel() {
        removeAllHandles();
        loadModel();
    }

    protected void loadModel() {
        StatementLayout sl = (StatementLayout) mapObject.getDrawerLayout();
        Collection col = store.getTripleHandles(sl).getDraggers(true);
        addHandles(col);

        //Fix the segments...
        Iterator it = col.iterator();
        Handle firstHandle = (Handle) it.next();
        Handle secondHandle;
        int segment = 0;
        do {
            secondHandle = (Handle) it.next();
            addHandle(new SegmentHandle.TripleLineSegmentHandle(firstHandle,
                    secondHandle, segment, mapObject));
            firstHandle = secondHandle;
            segment++;
        } while (it.hasNext());

        DrawerLayout subject = sl.getSubjectLayout();
        DrawerLayout object = sl.getObjectLayout();
        //	addHandles(store.getAndSetTripleCenterFollowers(mapObject.getDrawerLayout()));
        //The following two line, sensible??
        if (subject != null) {
            addHandles(store.getAndSetBoxFollowers(subject));
        }
        if (object != null) {
            addHandles(store.getAndSetBoxFollowers(object));
        }
        if (sl.getBodyVisible())
            addHandle(store.getBoxHandlesStruct(sl).tot);
        //	if (end != null && end.getBodyVisible())
        //	    addHandle(store.getBoxHandlesStruct(mapObject.getStatementLayout().getObject()).tot);
    }

    public void click(MapEvent m) {
        //      chooseHandle(m);
        if (linetool.isActivated()) {
            if (currentHandle instanceof SegmentHandle) {
                currentHandle.setSelected(false);
                currentHandle = null;
            }
            if (currentHandle == null) {
                TripleHandlesStruct alhs = store
                        .getTripleHandles((StatementLayout) mapObject
                                .getDrawerLayout());
                switch (((StatementLayout) mapObject.getDrawerLayout())
                        .getPathType()) {
                case LineStyle.PATH_TYPE_CURVE:
                    double[] src = new double[8];
                    double[] left = new double[8];
                    double[] right = new double[8];
                    CornerHandle chleft = (CornerHandle) alhs
                            .getHandle(m.lineSegmentNumber * 3);
                    CornerHandle chright = (CornerHandle) alhs
                            .getHandle((m.lineSegmentNumber + 1) * 3);
                    src[0] = chleft.getPosition().x;
                    src[1] = chleft.getPosition().y;
                    src[2] = chleft.control2.getPosition().x;
                    src[3] = chleft.control2.getPosition().y;
                    src[4] = chright.control1.getPosition().x;
                    src[5] = chright.control1.getPosition().y;
                    src[6] = chright.getPosition().x;
                    src[7] = chright.getPosition().y;
                    java.awt.geom.CubicCurve2D.subdivide(src, 0, left, 0,
                            right, 0);
                    chleft.control2.setPosition((int) left[2], (int) left[3]);
                    chright.control1
                            .setPosition((int) right[4], (int) right[5]);
                    CornerHandle middle = new CornerHandle(
                            new ContextMap.Position((int) left[6],
                                    (int) left[7]), new ContextMap.Position(
                                    (int) left[4], (int) left[5]),
                            new ContextMap.Position((int) right[2],
                                    (int) right[3]));
                    alhs.handles.insertElementAt(middle.control2,
                            m.lineSegmentNumber * 3 + 2);
                    alhs.handles.insertElementAt(middle,
                            m.lineSegmentNumber * 3 + 2);
                    alhs.handles.insertElementAt(middle.control1,
                            m.lineSegmentNumber * 3 + 2);
                    reloadModel();
                    break;
                default:
                    DefaultHandle h = new DefaultHandle(
                            new ContextMap.Position(m.mapX, m.mapY));
                    alhs.handles.insertElementAt(h, m.lineSegmentNumber + 1);
                    reloadModel();
                }
            } else {
                TripleHandlesStruct alhs = store
                        .getTripleHandles((StatementLayout) mapObject
                                .getDrawerLayout());
                switch (((StatementLayout) mapObject.getDrawerLayout())
                        .getPathType()) {
                case LineStyle.PATH_TYPE_CURVE:
                    if (currentHandle instanceof CornerHandle
                            && alhs.getFirstHandle() != currentHandle
                            && alhs.getLastHandle() != currentHandle) {
                        alhs.handles.remove(currentHandle);
                        alhs.handles
                                .remove(((CornerHandle) currentHandle).control1);
                        alhs.handles
                                .remove(((CornerHandle) currentHandle).control2);
                        reloadModel();
                    }
                    break;
                default:
                    if (alhs.getFirstHandle() != currentHandle
                            && alhs.getLastHandle() != currentHandle) {
                        alhs.handles.remove(currentHandle);
                        reloadModel();
                    }
                }

            }
        }
    }

    protected ContextMap.Position startDragImpl(MapEvent m) {
        ContextMap.Position pos = super.startDragImpl(m);

        updateControlPointsImpl();
        return pos;
    }

    protected void updateControlPointsImpl() {
        updateControlPoints(((StatementLayout) mapObject.getDrawerLayout())
                .getPathType());
    }

    protected final void updateControlPoints(int type) {
        switch (type) {
        case LineStyle.PATH_TYPE_CURVE:
            if (currentHandle instanceof SegmentHandle) {
                if (controller1 != null)
                    removeHandle(controller1);
                if (controller2 != null)
                    removeHandle(controller2);

                controller1 = ((CornerHandle) ((SegmentHandle) currentHandle).second).control1;
                controller2 = ((CornerHandle) ((SegmentHandle) currentHandle).first).control2;

                addHandleFirst(controller1);
                addHandleFirst(controller2);
            } else if (currentHandle instanceof CornerHandle) {
                if (controller1 != null)
                    removeHandle(controller1);
                if (controller2 != null)
                    removeHandle(controller2);

                controller1 = ((CornerHandle) currentHandle).control1;
                controller2 = ((CornerHandle) currentHandle).control2;

                //The check is needed for cornerhandles since it can be an
                // endpoint,
                //in that case one of the controllpoints is missing.
                if (controller1 != null)
                    addHandleFirst(controller1);
                if (controller2 != null)
                    addHandleFirst(controller2);
            }
            break;
        default:
        }
    }

    public boolean update(EditEvent e) {
        switch (e.getEditType()) {
        /*
         * case ConceptLayout.TRIPLESTYLE_REMOVED: if (((String)
         * e.getTarget()).equals(mapObject.getStatementLayout().getURI()))
         * return false; else if (tieTool.isActivated()) reloadModel(); return
         * true;
         */
        case ContextMap.RESOURCELAYOUT_REMOVED:
            String target = (String) e.getTarget();
            if (target.equals(mapObject.getDrawerLayout().getURI()))
                return false;
            else
                reloadModel();
            return true;
        //	    case ConceptLayout.TRIPLESTYLE_ADDED:
        case DrawerLayout.BOUNDINGBOX_EDITED:
        case DrawerLayout.BODYVISIBLE_EDITED:
            if (tieTool.isActivated())
                reloadModel();
            return true;
        case DrawerLayout.BOXLINE_EDITED:
        case DrawerLayout.BOXLINEPATHTYPE_EDITED:
        case StatementLayout.LINE_EDITED:
        case StatementLayout.LINEPATHTYPE_EDITED:
            return false;

        }
        return true;
    }

    public void propertyChange(PropertyChangeEvent e) {
        reloadModel();
        /*
         * if (e.getEvent() == ToolStateEvent.ACTIVATED) getFollowHandles(true);
         * if (e.getEvent() == ToolStateEvent.DEACTIVATED)
         * getFollowHandles(false);
         */
    }

    public void detach() {
        tieTool.removePropertyChangeListener(this);
    }
}