/*  $Id: $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */
package se.kth.cid.conzilla.clipboard;

import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.ContextMap.BoundingBox;

public class ClipboardDrawerLayout {

	ContextMap.BoundingBox bb;
	boolean bodyVisible;
	int hTextAnchor;
	int vTextAnchor;
	String detailedMap;
	String conceptURI;
	String id;
	
	public ClipboardDrawerLayout(DrawerLayout dl) {
		bb = dl.getBoundingBox();
		bodyVisible = dl.getBodyVisible();
		hTextAnchor = dl.getHorisontalTextAnchor();
		vTextAnchor = dl.getVerticalTextAnchor();
		detailedMap = dl.getDetailedMap();
		conceptURI = dl.getConceptURI();
		id = dl.getURI();
	}
	
	public String getId() {
		return id;
	}
	
	public boolean getBodyVisible() {
		return bodyVisible;
	}

	public BoundingBox getBoundingBox() {
		return bb;
	}

	public String getConceptURI() {
		return conceptURI;
	}

	public String getDetailedMap() {
		return detailedMap;
	}

	public int getHorisontalTextAnchor() {
		return hTextAnchor;
	}

	public int getVerticalTextAnchor() {
		return vTextAnchor;
	}
}
