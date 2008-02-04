/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.conzilla.edit.TieTool;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.layout.ContextMap;

public abstract class HandledObject {
	protected Vector handles;

	private HashSet handlesCheck;

	protected MapEvent mapeventold;

	protected boolean dragdirty;

	protected MapObject mapObject;

	protected boolean lock;

	protected Handle currentHandle;

	protected TieTool tieTool;

	public HandledObject(MapObject mapObject, TieTool tieTool) {
		this.mapObject = mapObject;
		this.tieTool = tieTool;

		handles = new Vector();
		handlesCheck = new HashSet();
		dragdirty = false;
	}

	public MapObject getMapObject() {
		return mapObject;
	}

	/**
	 * @return a boolean telling wheter this handle could be taken care of, i.e.
	 *         if it didn't have a parent.
	 */
	public void addHandle(Handle handle) {
		if (handlesCheck.add(handle))
			handles.add(handle);
	}

	/**
	 * @return a boolean telling wheter this handle could be taken care of, i.e.
	 *         if it didn't have a parent.
	 */
	public void addHandleFirst(Handle handle) {
		if (handlesCheck.add(handle))
			handles.insertElementAt(handle, 0);
	}

	/**
	 * Takes care of handles without parents.
	 */
	public void addHandles(Collection handles) {
		Iterator it = handles.iterator();
		while (it.hasNext()) {
			Handle handle = (Handle) it.next();
			if (handlesCheck.add(handle))
				this.handles.add(handle);
		}
	}

	public void removeHandle(Handle handle) {
		if (handlesCheck.remove(handle))
			this.handles.remove(handle);
	}

	public void removeAllHandles() {
		handles = new Vector();
		handlesCheck = new HashSet();
	}

	public void chooseHandle(MapEvent m) {
		Handle ha;
		Iterator it = handles.iterator();
		while (it.hasNext()) {
			ha = (Handle) it.next();
			if (ha.contains(m)) {
				// Tracer.debug("Found handle.");
				m.consume();
				currentHandle = ha;
				return;
			}
		}
		currentHandle = null;
	}

	public final Collection drag(MapEvent m) {
		dragdirty = true;
		int x = m.mapX - mapeventold.mapX;
		int y = m.mapY - mapeventold.mapY;
		if (x == 0 && y == 0)
			return null;

		mapeventold = m;
		return move(x, y);
	}

	public Collection move(int x, int y) {
		if (currentHandle != null) {
			if (tieTool.isActivated())
				return currentHandle.drag(x, y);
			else
				return currentHandle.dragUnTied(x, y);
		}
		return null;
	}

	public final ContextMap.Position startDrag(MapEvent m) {
		dragdirty = false;
		Iterator it = handles.iterator();
		while (it.hasNext())
			((Handle) it.next()).clearEdited();

		mapeventold = m;
		ContextMap.Position pos = startDragImpl(m);
		return pos;
	}

	protected ContextMap.Position startDragImpl(MapEvent m) {
		chooseHandle(m);
		if (currentHandle != null) {
			currentHandle.setSelected(true);
			return currentHandle.getOffset(m);
		}
		return null;
	}

	public final void stopDrag(MapEvent m) {
		mapeventold = m;
		stopDragImpl(m);
		if (dragdirty)
			endDrag(m);
	}

	protected void stopDragImpl(MapEvent m) {
		if (currentHandle != null)
			currentHandle.setSelected(false);
	}

	protected void endDrag(MapEvent m) {
	}

	public void click(MapEvent m) {
	}

	/**
	 * This function is called to give the HandledObject the opportunity to
	 * adapt to a changed ConceptMap. It is called from the Layer-class which
	 * itselvs servs as an EditListener.
	 * 
	 * @param e an EditEvent received from the ConceptMap via an editListener.
	 */
	public boolean update(EditEvent e) {
		return true;
	}

	/**
	 * Always call detach on an HandledObject before you throw it away.
	 */
	public void detach() {
		handles = null;
		handlesCheck = null;
	}

	public void paint(Graphics2D g, Graphics2D original) {
		if (handles != null) {
			Iterator it = handles.iterator();
			while (it.hasNext())
				((Handle) it.next()).paint(g);
		}
	}

	protected void setFollowers(Collection controllhandles, Collection followers) {
		Iterator it = controllhandles.iterator();
		while (it.hasNext())
			((Handle) it.next()).setFollowers(followers);
	}

	// help function.
	public static Rectangle positiveRectangle(Rectangle re) {
		if (re.width < 0)
			if (re.height < 0)
				return new Rectangle(re.x + re.width, re.y + re.height,
						-re.width, -re.height);
			else
				return new Rectangle(re.x + re.width, re.y, -re.width,
						re.height);
		else if (re.height < 0)
			return new Rectangle(re.x, re.y + re.height, re.width, -re.height);
		else
			return new Rectangle(re.x, re.y, re.width, re.height);
	}
}
