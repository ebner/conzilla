/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;

import java.awt.Graphics2D;
import java.util.Collection;

import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.layout.ContextMap;


public abstract class SegmentHandle extends AbstractHandle
{
    public Handle first, second;
    int segment;
    MapObject mapObject;

    public static class ConceptLineSegmentHandle extends SegmentHandle
    {
	public ConceptLineSegmentHandle(Handle first, Handle second, int segment, MapObject mo)
	{super(first, second, segment, mo);}

	public boolean contains(MapEvent m)
	{
	    return m.hitType==MapEvent.HIT_BOXLINE && 
		m.mapObject==this.mapObject &&
		m.lineSegmentNumber==this.segment;
	}
    }	

    public static class TripleLineSegmentHandle extends SegmentHandle
    {
	public TripleLineSegmentHandle(Handle first, Handle second, int segment, MapObject mo)
	{super(first, second, segment, mo);}

	public boolean contains(MapEvent m)
	{
	    return m.hitType==MapEvent.HIT_TRIPLELINE && 
		m.mapObject==this.mapObject &&
		m.lineSegmentNumber==this.segment;
	}
    }	
    
    //Konstructor
    public SegmentHandle(Handle first, Handle second, int segment, MapObject mo)
    {
	this.first=first;
	this.second=second;
	this.segment=segment;
	this.mapObject=mo;
	this.selected=false;
	clearEdited();
    }
    
    
    /** Drags the handle in the default way, i.e. via calling @link #dragForced
     *
     *  @return a collection of rectangles where repaint is needed.
     *  @see #dragForced
     */
    public Collection drag(int x, int y)
    {
	
	//Drag first
	Collection col=first.drag(x, y);
	//Drag second
	col.addAll(second.drag(x,y));
	
	return col;
    }
    
    public Collection dragForced(int x, int y)
    {
	return null;
    }

    public void paint(Graphics2D g) {}
    public void simplePaint(Graphics2D g) {}
    
    public ContextMap.Position getPosition() {return null;}
    public ContextMap.Position getOffset(MapEvent m) {return null;}
    
    public void setSelected(boolean selected)        
    {
	first.setSelected(selected);
	second.setSelected(selected);
    }
}
