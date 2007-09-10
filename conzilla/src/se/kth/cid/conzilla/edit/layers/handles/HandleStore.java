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
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.util.*;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class HandleStore
{
    Hashtable neuronBoxStructs;
    Hashtable neuronLineStructs;
    Hashtable axonStructs;

    GridModel gridModel;
    boolean lock = false;

    public HandleStore(GridModel gridModel)
    {
	this.gridModel = gridModel;
	clear();
    }
    
    public AxonHandlesStruct getAxonHandles(AxonStyle as)
    {
	AxonHandlesStruct ahs = (AxonHandlesStruct) axonStructs.get(as);
	if (ahs == null)
	    {
		ahs = new AxonHandlesStruct(as);
		axonStructs.put(as, ahs);
	    }
	return ahs;
    }
    public NeuronLineHandlesStruct getNeuronLineHandles(NeuronStyle ns)
    {
	NeuronLineHandlesStruct nlhs = (NeuronLineHandlesStruct) neuronLineStructs.get(ns);
	if (nlhs == null)
	    {
		nlhs = new NeuronLineHandlesStruct(ns);
		neuronLineStructs.put(ns, nlhs);
	    }
 	return nlhs;
    }

    public NeuronBoxHandlesStruct getNeuronBoxHandles(NeuronStyle ns)
    {
	NeuronBoxHandlesStruct nbhs = (NeuronBoxHandlesStruct) neuronBoxStructs.get(ns);
	if (nbhs == null)
	    {
		nbhs = new NeuronBoxHandlesStruct(ns, gridModel);
		neuronBoxStructs.put(ns, nbhs);
	    }
	return nbhs;
    }
 
    public void set()
    {
	if (lock)
	    return;
	lock=true;
	Enumeration en = neuronBoxStructs.elements();
	while(en.hasMoreElements())
	    ((NeuronBoxHandlesStruct) en.nextElement()).set();
	
	en = neuronLineStructs.elements();
	while(en.hasMoreElements())
	    ((NeuronLineHandlesStruct) en.nextElement()).set();
	
	en = axonStructs.elements();
	while(en.hasMoreElements())
	    ((AxonHandlesStruct) en.nextElement()).set();
	lock=false;
    }
    
    public void clear(Object o)
    {
	if (o instanceof NeuronStyle)
	    {
		neuronBoxStructs.remove(o);
		neuronLineStructs.remove(o);
	    }
	else if (o instanceof AxonStyle)
	    axonStructs.remove(o);
    }

    public void clear()
    {
	if (lock) 
	    return;
	neuronBoxStructs  = new Hashtable();
	neuronLineStructs = new Hashtable();
	axonStructs       = new Hashtable();
    }
    public void paint(Graphics2D g)
    {
	Enumeration  en = neuronBoxStructs.elements();
	while(en.hasMoreElements())
	    ((NeuronBoxHandlesStruct) en.nextElement()).paint(g);
	
	en = neuronLineStructs.elements();
	while(en.hasMoreElements())
	    ((NeuronLineHandlesStruct) en.nextElement()).paint(g);
	
	en = axonStructs.elements();
	while(en.hasMoreElements())
	    ((AxonHandlesStruct) en.nextElement()).paint(g);
    }


    //Helping functions

    //If box is visible set followers
    public Collection getAndSetBoxFollowers(NeuronStyle ns)
    {
	//FIXME:  solve this...
	if (ns == null)
	    return new Vector();
	if (ns.getBodyVisible())
	    {
		NeuronBoxHandlesStruct nlhs = getNeuronBoxHandles(ns);
		Collection col = getBoxFollowers(ns);
		nlhs.tot.setFollowers(col);
		return col;
	    }
	return getBoxFollowers(ns);
    }

    
    //Calculates the boxfollowers.
    public Collection getBoxFollowers(NeuronStyle ns)
    {	
	Collection fh=new Vector();
	

	//If visible neuronline should follow otherwise axoncenter.
	Handle lastNeuronLineHandle = getNeuronLineHandles(ns).getLastHandle();
	if (lastNeuronLineHandle != null)
	    fh.add(lastNeuronLineHandle);
	else
	    fh.addAll(getAndSetAxonCenterFollowers(ns));

	//Roles ending at this box       
	AxonStyle[] ass=ns.getObjectOfAxonStyles();
	Handle lastAxonHandle;
	Handle lastControlAxonHandle;
	for (int i=0;i<ass.length;i++)
	    {
		AxonStyle as = ass[i];
		switch (as.getPathType()) {
		case AxonStyle.PATH_TYPE_STRAIGHT:
		case AxonStyle.PATH_TYPE_QUAD:
		    lastAxonHandle = getAxonHandles(as).getLastHandle();
		    if (lastAxonHandle != null)
			fh.add(lastAxonHandle);
		    break;
		case AxonStyle.PATH_TYPE_CURVE:
		    lastAxonHandle = getAxonHandles(as).getLastHandle();
		    //		    lastControlAxonHandle = getAxonHandles(as).getSecondLastHandle();
		    if (lastAxonHandle != null) //&& lastControlAxonHandle != null)
			{
			    fh.add(lastAxonHandle);
			    //			    fh.add(lastControlAxonHandle);
			}
		}
	    }
		
	return fh;
    }

    public Collection getAndSetAxonCenterFollowers(NeuronStyle owner)
    {
	Vector fh=new Vector();
      
	//Neuronline should follow.
	Handle firstNeuronLineHandle = getNeuronLineHandles(owner).getFirstHandle();
	if (firstNeuronLineHandle!= null)  //If neuronline is visible, move it along
	    {
		fh.add(firstNeuronLineHandle);
		firstNeuronLineHandle.setFollowers(fh);
	    }

	//Roles belonging to this box       
	Handle firstAxonHandle;
	Handle secondAxonHandle;
	AxonStyle[] ass=owner.getAxonStyles();
	for (int i=0;i<ass.length;i++)
	    {
		AxonStyle as = ass[i];		 
		switch (as.getPathType()) {
		case AxonStyle.PATH_TYPE_STRAIGHT:
		case AxonStyle.PATH_TYPE_QUAD:
		    firstAxonHandle = getAxonHandles(as).getFirstHandle();
		    if (firstAxonHandle != null)
		    {
			fh.addElement(firstAxonHandle);
			firstAxonHandle.setFollowers(fh);
		    }
		    break;
		case AxonStyle.PATH_TYPE_CURVE:
		    firstAxonHandle = getAxonHandles(as).getFirstHandle();
		    //		    secondAxonHandle = getAxonHandles(as).getSecondHandle();
		    if (firstAxonHandle != null)// && secondAxonHandle != null)
		    {
			fh.addElement(firstAxonHandle);
			firstAxonHandle.setFollowers(fh);
			//			fh.addElement(secondAxonHandle);
		    }
		}
	    }
	return fh;
    }

    Collection getBoxHandles(NeuronStyle ns)
    {
	NeuronBoxHandlesStruct nbhs = getNeuronBoxHandles(ns);
	Vector vec = new Vector();
	vec.add(nbhs.ul);
	vec.add(nbhs.ur);
	vec.add(nbhs.lr);
	vec.add(nbhs.ll);
	vec.add(nbhs.tot);
	return vec;
    }
}

