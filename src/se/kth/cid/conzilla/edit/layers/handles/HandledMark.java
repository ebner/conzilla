/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.edit.TieTool;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class HandledMark extends HandledObject {
    HandleStore store;
    MapEvent lastClick;

    public HandledMark(MapEvent m, HandleStore store, TieTool tieTool) {
        super(m.mapObject, tieTool);
        this.store = store;
    }

    protected ContextMap.Position startDragImpl(MapEvent m) {
        chooseHandle(m);
        if (currentHandle != null) {
            //		currentHandle.setSelected(true);
            return currentHandle.getOffset(m);
        }
        return null;
    }

    public Collection move(int x, int y) {
        Collection cols = new Vector();
        Collection col = null;

        Iterator it = handles.iterator();
        while (it.hasNext()) {
            Handle ha = (Handle) it.next();
            if (ha.isSelected())
                col = ha.dragForced(x, y);
            if (col != null)
                cols.addAll(col);
        }
        return cols;
    }

    protected void stopDragImpl(MapEvent m) {}

    public boolean update(EditEvent e) {
        return false;
    }

    public void click(MapEvent m) {
        if (currentHandle != null
            && !(currentHandle instanceof BoxTotalHandle)) {
            lastClick = null;
            if (currentHandle.getFollowers() != null) {
                Iterator it = currentHandle.getFollowers().iterator();
                while (it.hasNext()) {
                    Handle ha = (Handle) it.next();
                    ha.setSelected(false);
                    removeHandle(ha);
                }
            } else {
                currentHandle.setSelected(false);
                removeHandle(currentHandle);
            }
            return;
        }

        Collection col = new HashSet();

        if (m.hitType == MapEvent.HIT_NONE)
            return;

        DrawerLayout ns = m.mapObject.getDrawerLayout();
        switch (m.hitType) {
            case MapEvent.HIT_BOXLINE :
                col.addAll(store.getBoxLineHandles((StatementLayout) ns).handles);
                if (!tieTool.isActivated())
                    break;
                //No triplecenter right now.
                //		col.addAll(store.getAndSetTripleCenterFollowers(ns));
            case MapEvent.HIT_BOX :
            case MapEvent.HIT_BOXTITLE :
            case MapEvent.HIT_BOXDATA :
                col.add(store.getBoxHandlesStruct(ns).tot);
                if (!tieTool.isActivated())
                    break;
                col.addAll(store.getAndSetBoxFollowers(ns));
                break;
            case MapEvent.HIT_TRIPLELINE :
            	StatementLayout sl = (StatementLayout) m.mapObject.getDrawerLayout();
                col.addAll(store.getTripleHandles(sl).handles);

                if (sl.getBodyVisible()) {
                	col.add(store.getBoxHandlesStruct(sl).tot); 
                }
                if (sl.getBoxLine() != null) {
                	col.addAll(store.getBoxLineHandles(sl).handles);
                }

                if (tieTool.isActivated()) {
                	if (sl.getBodyVisible()) {
                        col.addAll(store.getAndSetBoxFollowers(sl));
                	}
                	DrawerLayout start = sl.getSubjectLayout();
                	col.addAll(store.getAndSetBoxFollowers(start));
                    if (start.getBodyVisible())
                        col.add(store.getBoxHandlesStruct(start).tot);
                	if (!sl.isLiteralStatement()) {
                    	DrawerLayout end = sl.getObjectLayout();
                    	col.addAll(store.getAndSetBoxFollowers(end));
                        if (end.getBodyVisible())
                            col.add(store.getBoxHandlesStruct(end).tot);
                	}
                }
                break;
        }
        Iterator it = col.iterator();
        if (lastClick != null && lastClick.mapObject == m.mapObject)
            while (it.hasNext()) {
                lastClick = null;
                Handle ha = (Handle) it.next();
                ha.setSelected(false);
                removeHandle(ha);
            } else {
            lastClick = m;
            while (it.hasNext())
                 ((Handle) it.next()).setSelected(true);
            addHandles(col);
        }
    }

    public void setSelected(Collection col) {
        addHandles(col);
    }
    public void detach() {}
}
