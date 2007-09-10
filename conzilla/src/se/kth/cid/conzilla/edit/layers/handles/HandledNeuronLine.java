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
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HandledNeuronLine extends HandledLine
{
    public HandledNeuronLine(MapEvent m, LineTool linetool, TieTool tietool, HandleStore store)
    {
	super(m, linetool, tietool, store);
    }
    
    protected void loadModel()
    {   
	NeuronLineHandlesStruct nlhs = store.getNeuronLineHandles(mapObject.getNeuronStyle());      
	Collection col = nlhs.getDraggers(true);
	addHandles(col);
	
	//Fix the segments...
	Iterator it=col.iterator();
	Handle firstHandle = (Handle) it.next();
	Handle secondHandle;
	int segment = 0;
	do {
	    Tracer.debug("segment "+segment);
	    secondHandle = (Handle) it.next();
	    addHandle(new SegmentHandle.NeuronLineSegmentHandle(firstHandle, secondHandle, segment, mapObject));
	    firstHandle = secondHandle;
	    segment++;
	} while (it.hasNext());
	
	NeuronStyle owner=mapObject.getNeuronStyle();
	addHandles(store.getAndSetAxonCenterFollowers(mapObject.getNeuronStyle()));
	addHandles(store.getAndSetBoxFollowers(owner));
	if (owner.getBodyVisible())
	    addHandle(store.getNeuronBoxHandles(mapObject.getNeuronStyle()).tot);
    }
    
  public void click(MapEvent m)
  {
      chooseHandle(m);
      if (linetool.isActivated())
	  {
	      if (currentHandle instanceof SegmentHandle)
		  {
		      currentHandle.setSelected(false);
		      currentHandle=null;
		  }

	      if (currentHandle==null )
		  {
		      NeuronLineHandlesStruct nlhs = store.getNeuronLineHandles(mapObject.getNeuronStyle());      
		      DefaultHandle h = new DefaultHandle(new ConceptMap.Position(m.mapX, m.mapY));
		      nlhs.handles.insertElementAt(h, m.lineSegmentNumber+1);
		      reloadModel();
		  }
	      else
		  {
		      NeuronLineHandlesStruct nlhs = store.getNeuronLineHandles(mapObject.getNeuronStyle());      
		      if (nlhs.getFirstHandle()!=currentHandle && nlhs.getLastHandle()!=currentHandle)
			  {
			      nlhs.handles.remove(currentHandle);
			      reloadModel();
			  }
		  }
	  }
  }

    protected void updateControlPointsImpl()
    {
	updateControlPoints(mapObject.getNeuronStyle().getPathType());
    }

    public boolean update(EditEvent e)
    {
	switch (e.getEditType())
	    {
	    case NeuronStyle.AXONSTYLE_REMOVED:
		reloadModel();
		return true;
	    case ConceptMap.NEURONSTYLE_REMOVED:
		String target=(String) e.getTarget();
		if (target.equals(mapObject.getNeuronStyle().getURI()))
		    return false;
		else
		    reloadModel();
		return true;
	    case NeuronStyle.LINE_EDITED:
		if (e.getEditedObject()==mapObject.getNeuronStyle())
		    if (e.getTarget()==null)
			return false;
		    else
			reloadModel();
		return true;
	    case NeuronStyle.AXONSTYLE_ADDED:
	    case AxonStyle.LINE_EDITED:
	    case NeuronStyle.BOUNDINGBOX_EDITED:
		if (tieTool.isActivated())
		    reloadModel();
		return true;
	    case NeuronStyle.BODYVISIBLE_EDITED:
		if (e.getEditedObject()==mapObject.getNeuronStyle())
		    if (!((Boolean) e.getTarget()).booleanValue())
			return false;
	    }
	return true;
    }

}
