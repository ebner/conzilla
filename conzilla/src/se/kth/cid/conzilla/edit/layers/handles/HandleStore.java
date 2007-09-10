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
}

