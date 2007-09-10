/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.layout.ContextMap;
import se.kth.cid.style.LineStyle;

/**
 * @author Matthias Palmer
 * @version $version: $
 */
public abstract class LineHandlesStruct {
	public Vector handles;

	public LineHandlesStruct() {
		handles = new Vector();
	}

	protected void loadHandles(ContextMap.Position[] points, int type) {
		switch (type) {
		case LineStyle.PATH_TYPE_STRAIGHT:
			for (int i = 0; i < points.length; i++)
				handles.addElement(new DefaultHandle(points[i]));
			break;
		case LineStyle.PATH_TYPE_QUAD:
			for (int i = 0; i < points.length; i++)
				handles.addElement(new DefaultHandle(points[i]));
			break;
		case LineStyle.PATH_TYPE_CURVE:
			CornerHandle corner = new CornerHandle(points[0], null, points[1]);
			handles.addElement(corner);
			handles.addElement(corner.control2);

			for (int i = 4; i < points.length; i += 3) {
				corner = new CornerHandle(points[i - 1], points[i - 2], points[i]);
				handles.addElement(corner.control1);
				handles.addElement(corner);
				handles.addElement(corner.control2);
			}
			corner = new CornerHandle(points[points.length - 1], points[points.length - 2], null);
			handles.addElement(corner.control1);
			handles.addElement(corner);
			break;
		}
	}

	/*
	 * public void insertHandleAt(Handle handle, int pos) {
	 * handles.insertElementAt(handle, pos); }
	 * 
	 * public void removeHandle(Handle handle) { handles.remove(handle); }
	 */

	public DefaultHandle getHandle(int nr) {
		return (nr >= 0 && nr <= handles.size()) ? (DefaultHandle) handles.elementAt(nr) : null;
	}

	public abstract Collection getDraggers(boolean withEnds);

	protected Collection getDraggers(int type, boolean withEnds) {
		switch (type) {
		case LineStyle.PATH_TYPE_STRAIGHT:
//		case LineStyle.PATH_TYPE_QUAD:
			if (withEnds || (handles.size() < 2))
				return handles;
			else
				return new ArrayList(handles.subList(1, handles.size() - 1));
		case LineStyle.PATH_TYPE_CURVE:
			Vector draggers = new Vector();
			Iterator it = handles.iterator();
			for (int i = 0; it.hasNext(); i++) {
				if (i % 3 == 0)
					draggers.add(it.next());
				else
					it.next();
			}
			if (withEnds) {
				return draggers;
			}
			return draggers.subList(1, draggers.size() - 1);
		}
		// should never be reached.
		return null;
	}

	public DefaultHandle getFirstHandle() {
		return handles.isEmpty() ? null : (DefaultHandle) handles.firstElement();
	}

	public DefaultHandle getSecondHandle() {
		return handles.size() <= 1 ? null : (DefaultHandle) handles.elementAt(1);
	}

	public DefaultHandle getLastHandle() {
		return handles.isEmpty() ? null : (DefaultHandle) handles.lastElement();
	}

	public DefaultHandle getSecondLastHandle() {
		return handles.size() <= 1 ? null : (DefaultHandle) handles.elementAt(handles.size() - 2);
	}

	public void paint(Graphics2D g) {
		Iterator it = handles.iterator();
		while (it.hasNext()) {
			Handle handle = (Handle) it.next();
			handle.paint(g);
		}
	}
}
