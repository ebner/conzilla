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

public class HandledLine extends HandledObject implements ToolStateListener
{
    protected MapEvent mapevent; 
    protected LineTool linetool;
    protected TieTool tietool;
    protected HandleStore store;
    
  public HandledLine(MapEvent m, LineTool linetool, TieTool tietool, HandleStore store)
  {
    super(m.mapObject);
    this.store=store;
    mapevent=m;
    this.linetool=linetool;
    this.tietool=tietool;
    tietool.addToolStateListener(this);
    //    setSingleSelection(true);
    loadModel();
  }

  protected void reloadModel()
    {
	removeAllHandles();
	loadModel();
    }
	    
  
    protected void loadModel()
    {   
	Collection col=getAxonHandles(mapObject.getAxonStyle(), store);
	addHandles(col);
	
	//Fix the segments...
	Iterator it=((Collection) col).iterator();
	Handle firstHandle = (Handle) it.next();
	Handle secondHandle;
	int segment = 0;
	do {
	    secondHandle = (Handle) it.next();
	    addHandle(new SegmentHandle.AxonLineSegmentHandle(firstHandle, secondHandle, segment, mapObject));
	    firstHandle = secondHandle;
	    segment++;
	} while (it.hasNext());
	

	if (!tietool.isActivated())
	    return;
	
	NeuronStyle owner=mapObject.getAxonStyle().getOwner();
	NeuronStyle end=mapObject.getAxonStyle().getEnd();
	addHandles(getAxonCenterFollowers(mapObject.getNeuronStyle(), store));
	addHandles(getBoxFollowers(owner, store));
	addHandles(getBoxFollowers(end, store));
	if (owner.getBodyVisible())
	    addHandle(getBoxTotalHandle(mapObject.getAxonStyle().getOwner(), store));
	if (end.getBodyVisible())
	    addHandle(getBoxTotalHandle(mapObject.getAxonStyle().getEnd(), store));
    }
        
  public void click(MapEvent m)
  {
      Tracer.debug("yes a click!");
      //      chooseHandle(m);
      if (linetool.isActivated())
	  {
	      if (currentHandle instanceof SegmentHandle)
		  {
		      currentHandle.setSelected(false);
		      currentHandle=null;
		  }
	      if (currentHandle==null )
		  {
		      
		      AxonHandlesStruct alhs = store.getAxonHandles(mapObject.getAxonStyle());
		      DefaultHandle h = new DefaultHandle(new ConceptMap.Position(m.mapX, m.mapY));
		      alhs.handles.insertElementAt(h, m.lineSegmentNumber+1);
		      reloadModel();
		  }
	      else
		  {
		      AxonHandlesStruct alhs = store.getAxonHandles(mapObject.getAxonStyle());
		      if (alhs.getFirstHandle()!=currentHandle && alhs.getLastHandle()!=currentHandle)
			  {
			      alhs.handles.remove(currentHandle);
			      reloadModel();
			  }
		  }
	  }
  }
    public boolean update(EditEvent e)
    {
	switch (e.getEditType())
	    {
	    case NeuronStyle.AXONSTYLE_REMOVED:
		if (((String) e.getTarget()).equals(mapObject.getAxonStyle().getAxonID()))
		    return false;
		else if (tietool.isActivated())
		    reloadModel();
		return true;
	    case ConceptMap.NEURONSTYLE_REMOVED:
		String target=(String) e.getTarget();
		if (target.equals(mapObject.getNeuronStyle().getID()))
		    return false;
		else
		    reloadModel();
		return true;
	    case NeuronStyle.AXONSTYLE_ADDED:
	    case NeuronStyle.LINE_EDITED:
	    case AxonStyle.LINE_EDITED:
	    case NeuronStyle.BOUNDINGBOX_EDITED:
	    case NeuronStyle.BODYVISIBLE_EDITED:
		if (tietool.isActivated())
		    reloadModel();
	    }
	return true;
    }
    public void toolStateChanged(ToolStateEvent e)
  {
      reloadModel();
      /*
      if (e.getEvent() == ToolStateEvent.ACTIVATED)
	  getFollowHandles(true);
      if (e.getEvent() == ToolStateEvent.DEACTIVATED)
      getFollowHandles(false);*/
  }
  public void detach()
  {
    tietool.removeToolStateListener(this);
  }
}
