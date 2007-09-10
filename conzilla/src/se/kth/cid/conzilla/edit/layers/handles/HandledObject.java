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
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import java.awt.*;
import java.util.*;

public abstract class HandledObject 
{
  protected Vector handles;
  private HashSet handlesCheck;
  protected MapEvent mapeventold;
  protected boolean dragdirty;
  protected MapObject mapObject;
  protected boolean lock;
  protected Handle currentHandle;

  public HandledObject(MapObject mapObject)
  {
    this.mapObject=mapObject;
    handles = new Vector();
    handlesCheck = new HashSet();
    dragdirty=false;
  }
  public MapObject getMapObject()
  {
    return mapObject;
  }

    /** 
     *  @returns a boolean telling wheter this handle could be taken care of, 
     *    i.e. if it didn't have a parent.
     */
    public void addHandle(Handle handle)
    {
	if (handlesCheck.add(handle))	    
	    handles.add(handle);
    }

    /** Takes care of handles without parents.
     */
    public void addHandles(Collection handles)
    {
	Iterator it = handles.iterator();
	while (it.hasNext())
	    {
		Handle handle=(Handle) it.next();
		if (handlesCheck.add(handle)) 
		    this.handles.add(handle);
	    }
    }
    
    public void removeHandle(Handle handle)
    {
	if (handlesCheck.remove(handle))
	    this.handles.remove(handle);
    }
    public void removeAllHandles()
    {
	handles      = new Vector();
	handlesCheck = new HashSet();
    }

    public void chooseHandle(MapEvent m)
    {
	Handle ha;
	Iterator it=handles.iterator();
	while (it.hasNext())
	    {
		ha = (Handle) it.next();
		if (ha.contains(m))
		    {
			//	    Tracer.debug("Found handle.");
			m.consume();
			currentHandle=ha;
			return;
		    }
	    }
	currentHandle=null;
    }
  
    final public Collection drag(MapEvent m)
    {
	dragdirty=true;
	int x=m.mapX-mapeventold.mapX;
	int y=m.mapY-mapeventold.mapY;
	if (x==0 && y==0)
	    return null;
	
	mapeventold=m;
	return dragImpl(m, x, y);
    }
  protected Collection dragImpl(MapEvent m, int x, int y) 
    {
	if (currentHandle!=null)
	    return currentHandle.drag(x,y);
	return null;
    }
  
  final public ConceptMap.Position startDrag(MapEvent m)
  {
    dragdirty=false;
    Iterator it = handles.iterator();
    while (it.hasNext())
	((Handle) it.next()).clearEdited();

    mapeventold=m;
    ConceptMap.Position pos=startDragImpl(m);
    return pos;
  }
  
  protected ConceptMap.Position startDragImpl(MapEvent m)
    {
	chooseHandle(m);
	if (currentHandle!=null)
	    {
		currentHandle.setSelected(true);
		return currentHandle.getOffset(m);
	    }
	return null;
    }
  
  final public void stopDrag(MapEvent m)
  {
    mapeventold=m;
    stopDragImpl(m);
    if (dragdirty) 
      endDrag(m);
  }

  protected void stopDragImpl(MapEvent m)
  {
      if (currentHandle!=null)
	  currentHandle.setSelected(false);
  }

  protected void endDrag(MapEvent m) {}
  public void click(MapEvent m) {}

  /** This function is called to give the HandledObject the opportunity
   *  to adapt to a changed ConceptMap.
   *
   *  It is called from the Layer-class which itselvs servs
   *  as an EditListener.
   *
   *  @param e an EditEvent received from the ConceptMap via an editListener.
   */
  public boolean update(EditEvent e) 
  {
      return true;
  }
  
    /** Always call detach on an HandledObject before you throw it away.
     */ 
    public void detach() 
    {
	handles = null;
	handlesCheck = null;
    }

    public void paint(Graphics2D g, Graphics2D original)
    {
	if (handles!=null)
	    {
		Iterator it=handles.iterator();
		while (it.hasNext())
		    ((Handle) it.next()).paint(g);
	    }
    }

    protected void setFollowers(Collection controllhandles, Collection followers)
    {
	Iterator it = controllhandles.iterator();
	while (it.hasNext())
	    ((Handle) it.next()).setFollowers(followers);
    }

    public static Collection getBoxFollowers(NeuronStyle ns, HandleStore store)
    {
	Collection fh=new Vector();
	Vector axoncenterfollowers=null;

	//Neuronline should follow.
	Handle lastNeuronLineHandle = store.getNeuronLineHandles(ns).getLastHandle();
	if (lastNeuronLineHandle != null)
	    {
		fh.add(lastNeuronLineHandle);
		getBoxTotalHandle(ns, store).setFollowers(fh);
	    }
		
	else
	    //Roles belonging to this box.
	    {
		if (!ns.getBodyVisible())  //In case box is invisible, axoncenter should move other followers.
		                          //i.e. extend axoncentercollection followers....
		    fh=HandledLine.getAxonCenterFollowers(ns, store);
		else                      //Otherwise this new collection (fh) is created and axonfollowers is added to it.
		    {
			getBoxTotalHandle(ns, store).setFollowers(fh);
			fh.addAll(HandledLine.getAxonCenterFollowers(ns, store));
		    }
	    }
	
	//Roles ending at this box       
	AxonStyle[] as=ns.getEndOfAxonStyles();
	for (int i=0;i<as.length;i++)
	    {
		Handle lastAxonHandle = store.getAxonHandles(as[i]).getLastHandle();
		if (lastAxonHandle != null)
		    fh.add(lastAxonHandle);
	    }

	return fh;
    }

    public static Collection getAxonCenterFollowers(NeuronStyle owner, HandleStore store)
    {
	Vector fh=new Vector();
      
	//Neuronline should follow.
	Handle firstNeuronLineHandle = store.getNeuronLineHandles(owner).getFirstHandle();
	if (firstNeuronLineHandle!= null)  //If neuronline is visible, move it along
	    {
		fh.add(firstNeuronLineHandle);
		firstNeuronLineHandle.setFollowers(fh);
	    }

	//Roles belonging to this box       
	AxonStyle[] as=owner.getAxonStyles();
	for (int i=0;i<as.length;i++)
	    {
		Handle firstAxonHandle = store.getAxonHandles(as[i]).getFirstHandle();
		if (firstAxonHandle != null)
		    {
			fh.addElement(firstAxonHandle);
			firstAxonHandle.setFollowers(fh);
		    }
	    }
	return fh;
    }
    static public Collection getAxonHandles(AxonStyle as, HandleStore store)
    {
	return store.getAxonHandles(as).handles;
    }
    static public Collection getNeuronLineHandles(NeuronStyle ns, HandleStore store)
    {
	return store.getNeuronLineHandles(ns).handles;
    }    

    static public Handle getBoxTotalHandle(NeuronStyle ns, HandleStore store)
    {
	return store.getNeuronBoxHandles(ns).tot;
    }
    
    static public Collection getBoxHandles(NeuronStyle ns, HandleStore store)
    {
	NeuronBoxHandlesStruct nbhs = store.getNeuronBoxHandles(ns);
	Vector vec = new Vector();
	vec.add(nbhs.ul);
	vec.add(nbhs.ur);
	vec.add(nbhs.lr);
	vec.add(nbhs.ll);
	vec.add(nbhs.tot);
	return vec;
    }
	
}
