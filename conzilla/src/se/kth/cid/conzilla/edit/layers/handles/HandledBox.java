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
import java.beans.*;

public class HandledBox extends HandledObject implements PropertyChangeListener
{

    protected MapEvent mapevent;
    protected Vector followHandles;
    protected HandleStore store;
  
    public HandledBox(MapEvent m, TieTool tieTool, HandleStore store)
    {
	super(m.mapObject,tieTool);
	this.store=store;
	mapevent=m;
	tieTool.addPropertyChangeListener(this);
	loadModel();
    }

    protected void reloadModel()
    {
	removeAllHandles();
	loadModel();
    }
	
    protected void loadModel()
    {
	followHandles = new Vector();

	NeuronBoxHandlesStruct nbhs = store.getNeuronBoxHandles(mapObject.getNeuronStyle());
	
	
	Collection followHandles = new Vector();
	if (tieTool.isActivated())
	    followHandles = store.getAndSetBoxFollowers(mapObject.getNeuronStyle());

	addHandle(nbhs.lr);
	nbhs.lr.setFollowers(followHandles);

	addHandle(nbhs.ll);
	nbhs.ll.setFollowers(followHandles);

	addHandle(nbhs.ur);
	nbhs.ur.setFollowers(followHandles);

	addHandle(nbhs.ul);
	nbhs.ul.setFollowers(followHandles);

	addHandles(followHandles);

        addHandle(nbhs.tot);

    }

    public boolean update(EditEvent e) 
    {
	NeuronStyle ns=mapObject.getNeuronStyle();
	switch (e.getEditType())
	    {
	    case ConceptMap.NEURONSTYLE_REMOVED:
		String target=(String) e.getTarget();
		if (target.equals(ns.getURI()))
		    return false;
		else
		    reloadModel();
		break;			    
	    case NeuronStyle.BODYVISIBLE_EDITED:
		if (((NeuronStyle) e.getEditedObject())==ns)
			return false;
		else
		    reloadModel(); //Probably not neccessary!!!
		break;
	    case NeuronStyle.AXONSTYLE_REMOVED:
	    case NeuronStyle.AXONSTYLE_ADDED: 
	    case NeuronStyle.LINE_EDITED:
	    case NeuronStyle.BOUNDINGBOX_EDITED:
		reloadModel();
	    }
	return true;
    }
    
    public void propertyChange(PropertyChangeEvent e)
    {
	reloadModel();
    }
    public void detach()
    {
	tieTool.removePropertyChangeListener(this);
    }
}
