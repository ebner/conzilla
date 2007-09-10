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
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HandledMark extends HandledObject
{
    HandleStore store;
    MapEvent lastClick;
    
  public HandledMark(MapEvent m, HandleStore store, TieTool tieTool)
  {
    super(m.mapObject, tieTool);
    this.store   = store;
  }  

  protected ConceptMap.Position startDragImpl(MapEvent m)
    {
	chooseHandle(m);
	if (currentHandle!=null)
	    {
		//		currentHandle.setSelected(true);
		return currentHandle.getOffset(m);
	    }
	return null;
    }

  protected Collection dragImpl(MapEvent m, int x, int y) 
    {
	Collection cols = new Vector();
	Collection col = null;

	Iterator it = handles.iterator();
	while (it.hasNext())
	    {
		Handle ha =(Handle) it.next();
		if (ha.isSelected())
		    col = ha.dragForced(x, y);
		if (col != null) 
		    cols.addAll(col);
	    }
	return cols;
    }

  protected void stopDragImpl(MapEvent m)
  {
  }

  public boolean update(EditEvent e) 
  {
      return false;
  }

    public void click(MapEvent m)
    {	    
	if (currentHandle != null && !(currentHandle instanceof BoxTotalHandle) )
	    {
		lastClick=null;
		if (currentHandle.getFollowers() != null)
		    {
			Iterator it = currentHandle.getFollowers().iterator();
			while (it.hasNext())
			    {
				Handle ha = (Handle) it.next();
				ha.setSelected(false);
				removeHandle(ha);
			    }
		    }
		else
		    {
			currentHandle.setSelected(false);
			removeHandle(currentHandle);
		    }
		return;
	    }

	Collection col=new HashSet();

	if (m.hitType==MapEvent.HIT_NONE)
	    return;
	    
	NeuronStyle ns=m.mapObject.getNeuronStyle();
	switch (m.hitType)
	    {
	    case MapEvent.HIT_BOXLINE:
		col.addAll(store.getNeuronLineHandles(ns).handles);
		if (!tieTool.isActivated())
		    break;
		col.addAll(store.getAndSetAxonCenterFollowers(ns));
	    case MapEvent.HIT_BOX:
	    case MapEvent.HIT_BOXTITLE:
	    case MapEvent.HIT_BOXDATA:
		col.add(store.getNeuronBoxHandles(ns).tot);
		if (!tieTool.isActivated())
		    break;
		col.addAll(store.getAndSetBoxFollowers(ns));
		break;
	    case MapEvent.HIT_AXONLINE:
		col.addAll(store.getAxonHandles(m.mapObject.getAxonStyle()).handles);
		if (!tieTool.isActivated())
		    break;
		col.addAll(store.getAndSetBoxFollowers(m.mapObject.getNeuronStyle()));
		col.add(store.getNeuronBoxHandles(m.mapObject.getNeuronStyle()).tot);
		NeuronStyle end = m.mapObject.getAxonStyle().getObject();
		col.addAll(store.getAndSetBoxFollowers(end));
		if (end.getBodyVisible())
		    col.add(store.getNeuronBoxHandles(end).tot);
		break;
	    }
	Iterator it = col.iterator();
	if (lastClick!=null && lastClick.mapObject==m.mapObject)
	    while(it.hasNext())
		{
		    lastClick=null;
		    Handle ha=(Handle) it.next();
		    ha.setSelected(false);
		    removeHandle(ha);
		}
	else
	    {
		lastClick=m;
		while(it.hasNext())
		    ((Handle) it.next()).setSelected(true);		
		addHandles(col);
	    }
    }

    public void setSelected(Collection col)
    {
	addHandles(col);
    }
    public void detach()
    {
    }
}
