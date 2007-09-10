/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package se.kth.cid.conzilla.edit.layers.handles;

import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;

import java.awt.*;
import java.util.*;


public abstract class SegmentHandle extends AbstractHandle
{
    public Handle first, second;
    int segment;
    MapObject mapObject;

    public static class NeuronLineSegmentHandle extends SegmentHandle
    {
	public NeuronLineSegmentHandle(Handle first, Handle second, int segment, MapObject mo)
	{super(first, second, segment, mo);}

	public boolean contains(MapEvent m)
	{
	    return m.hitType==MapEvent.HIT_BOXLINE && 
		m.mapObject==this.mapObject &&
		m.lineSegmentNumber==this.segment;
	}
    }	

    public static class AxonLineSegmentHandle extends SegmentHandle
    {
	public AxonLineSegmentHandle(Handle first, Handle second, int segment, MapObject mo)
	{super(first, second, segment, mo);}

	public boolean contains(MapEvent m)
	{
	    return m.hitType==MapEvent.HIT_AXONLINE && 
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
     *  @returns a collection of rectangles where repaint is needed.
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
    
    public ConceptMap.Position getPosition() {return null;}
    public ConceptMap.Position getOffset(MapEvent m) {return null;}
    
    public void setSelected(boolean selected)        
    {
	first.setSelected(selected);
	second.setSelected(selected);
    }
}
