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
import java.beans.*;

public class HandledLine extends HandledObject implements PropertyChangeListener
{
    protected MapEvent mapevent; 
    protected LineTool linetool;
    protected HandleStore store;

    protected Handle controller1, controller2;

  public HandledLine(MapEvent m, LineTool linetool, TieTool tietool, HandleStore store)
  {
    super(m.mapObject, tietool);
    this.store=store;
    mapevent=m;
    this.linetool=linetool;
    tietool.addPropertyChangeListener(this);
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
	Collection col = store.getAxonHandles(mapObject.getAxonStyle()).getDraggers(true);
	addHandles(col);


	//Fix the segments...
	Iterator it=col.iterator();
	Handle firstHandle = (Handle) it.next();
	Handle secondHandle;
	int segment = 0;
	do {
	    secondHandle = (Handle) it.next();
	    addHandle(new SegmentHandle.AxonLineSegmentHandle(firstHandle, secondHandle, segment, mapObject));
	    firstHandle = secondHandle;
	    segment++;
	} while (it.hasNext());
		
	NeuronStyle owner=mapObject.getAxonStyle().getOwner();
	NeuronStyle end=mapObject.getAxonStyle().getObject();
	addHandles(store.getAndSetAxonCenterFollowers(mapObject.getNeuronStyle()));
	addHandles(store.getAndSetBoxFollowers(owner));
	addHandles(store.getAndSetBoxFollowers(end));
	if (owner.getBodyVisible())
	    addHandle(store.getNeuronBoxHandles(mapObject.getAxonStyle().getOwner()).tot);
	if (end != null && end.getBodyVisible())
	    addHandle(store.getNeuronBoxHandles(mapObject.getAxonStyle().getObject()).tot);
    }
        
  public void click(MapEvent m)
  {
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
		      switch(mapObject.getAxonStyle().getPathType()) {
		      case AxonStyle.PATH_TYPE_CURVE:
			  double [] src = new double[8];
			  double [] left = new double[8];
			  double [] right = new double[8];
			  CornerHandle chleft = (CornerHandle) alhs.getHandle(m.lineSegmentNumber*3);
			  CornerHandle chright = (CornerHandle) alhs.getHandle((m.lineSegmentNumber+1)*3);
			  src[0] = chleft.getPosition().x; src[1] = chleft.getPosition().y;
			  src[2] = chleft.control2.getPosition().x; src[3] = chleft.control2.getPosition().y;
			  src[4] = chright.control1.getPosition().x; src[5] = chright.control1.getPosition().y;
			  src[6] = chright.getPosition().x; src[7] = chright.getPosition().y;
			  java.awt.geom.CubicCurve2D.subdivide(src,0, left, 0, right, 0);
			  chleft.control2.setPosition((int) left[2],(int) left[3]);
			  chright.control1.setPosition((int) right[4], (int) right[5]);
			  CornerHandle middle = new CornerHandle( new ConceptMap.Position((int) left[6], (int) left[7]),
								  new ConceptMap.Position((int) left[4], (int) left[5]),
								  new ConceptMap.Position((int) right[2], (int) right[3]));
			  alhs.handles.insertElementAt(middle.control2,m.lineSegmentNumber*3+2);
			  alhs.handles.insertElementAt(middle,m.lineSegmentNumber*3+2);
			  alhs.handles.insertElementAt(middle.control1,m.lineSegmentNumber*3+2);
			  reloadModel();			  
			  break;
		      default:
			  DefaultHandle h = new DefaultHandle(new ConceptMap.Position(m.mapX, m.mapY));
			  alhs.handles.insertElementAt(h, m.lineSegmentNumber+1);
			  reloadModel();
		      }
		  }
	      else
		  {
		      AxonHandlesStruct alhs = store.getAxonHandles(mapObject.getAxonStyle());		      
		      switch(mapObject.getAxonStyle().getPathType()) {
		      case AxonStyle.PATH_TYPE_CURVE:
			  if (currentHandle instanceof CornerHandle && alhs.getFirstHandle()!=currentHandle && alhs.getLastHandle()!=currentHandle)
			  {
			      alhs.handles.remove(currentHandle);
			      alhs.handles.remove(((CornerHandle) currentHandle).control1);
			      alhs.handles.remove(((CornerHandle) currentHandle).control2);
			      reloadModel();
			  }
			  break;
		      default:
			  if (alhs.getFirstHandle()!=currentHandle && alhs.getLastHandle()!=currentHandle)
			      {
				  alhs.handles.remove(currentHandle);
				  reloadModel();
			      }
		      }
		      
		  }
	  }
  }
   
    protected ConceptMap.Position startDragImpl(MapEvent m)
    {
	ConceptMap.Position pos = super.startDragImpl(m);
	
	updateControlPointsImpl();
	return pos;
    }
 
    protected void updateControlPointsImpl()
    {
	updateControlPoints(mapObject.getAxonStyle().getPathType());
    }

    protected final void updateControlPoints(int type)
    {
	switch(type) {
	case AxonStyle.PATH_TYPE_CURVE:
	    if (currentHandle instanceof SegmentHandle)
		{
		    if (controller1 != null)
			removeHandle(controller1);
		    if (controller2 != null)
			removeHandle(controller2);
	    
		    controller1 = ((CornerHandle) ((SegmentHandle) currentHandle).second).control1;
		    controller2 = ((CornerHandle) ((SegmentHandle) currentHandle).first).control2;

		    addHandleFirst(controller1);			
		    addHandleFirst(controller2);
		}
	    else if (currentHandle instanceof CornerHandle)
		{
		    if (controller1 != null)
			removeHandle(controller1);
		    if (controller2 != null)
			removeHandle(controller2);

		    controller1 =  ((CornerHandle) currentHandle).control1;
		    controller2 =  ((CornerHandle) currentHandle).control2;

		    //The check is needed for cornerhandles since it can be an endpoint, 
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

    public boolean update(EditEvent e)
    {
	switch (e.getEditType())
	    {
	    case NeuronStyle.AXONSTYLE_REMOVED:
		if (((String) e.getTarget()).equals(mapObject.getAxonStyle().getURI()))
		    return false;
		else if (tieTool.isActivated())
		    reloadModel();
		return true;
	    case ConceptMap.NEURONSTYLE_REMOVED:
		String target=(String) e.getTarget();
		if (target.equals(mapObject.getNeuronStyle().getURI()))
		    return false;
		else
		    reloadModel();
		return true;
	    case NeuronStyle.AXONSTYLE_ADDED:
	    case NeuronStyle.BOUNDINGBOX_EDITED:
	    case NeuronStyle.BODYVISIBLE_EDITED:
		if (tieTool.isActivated())
		    reloadModel();
		return true;
	    case NeuronStyle.LINE_EDITED:
	    case AxonStyle.LINE_EDITED:
		return false;

	    }
	return true;
    }
    public void propertyChange(PropertyChangeEvent e)
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
    tieTool.removePropertyChangeListener(this);
  }
}
