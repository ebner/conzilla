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
package se.kth.cid.conzilla.edit.layers;

import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conceptmap.ConceptMap;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/** Handles the rather complicated state controll for
 *  the createLayer.
 *
 *  @author Matthias Palmer
 */
public class CreateStateControl
{
    public static final int CREATE_BOX = 0;
    public static final int CREATE_FIRST_AXON = 1;
    public static final int CREATE_SECOND_AXON = 2;
    public static final int CREATE_N_AXON = 3;
    public static final int CREATE_WAITING = 4;
    public static final int CONTINUE_N_AXON = 5;

    Neuron neuron;
    Neuron templateTypeNeuron;
    int state;
    String drawType;
    ConzillaKit kit;
    MapController controller;
    AxonType [] axontypes;
    String axonType;
    int currentAxontype;
    NeuronType currentNeuronType;
    MapEvent mapEvent;
    
    public CreateStateControl(MapController controller)
    {
	this.controller = controller;
	this.kit = controller.getConzillaKit();
	state = CREATE_WAITING;
    }
    
    
    public int changeState(MapEvent m)
    {
	mapEvent = m;
	if (m.mouseEvent.getID() != MouseEvent.MOUSE_CLICKED)
	    return state;

	switch (m.hitType)
	    {
	    case MapEvent.HIT_NONE:
		if (state == CREATE_WAITING)
		    return CREATE_BOX;
		state = CREATE_WAITING;
		return state;
	    case MapEvent.HIT_BOX:
	    case MapEvent.HIT_BOXTITLE:
	    case MapEvent.HIT_BOXDATA:
	    case MapEvent.HIT_BOXLINE:
	    case MapEvent.HIT_AXONLINE:
		//If first press and the currenttype has axons.
		if (state ==CREATE_WAITING && stepAxonType())
		    {
			//If the same type where pressed, continue to create on that one.
			if (m.mapObject.getNeuronType()==getCurrentNeuronType())
			    {
				state = CONTINUE_N_AXON;
				return state;
			    }
			//Otherwise create new neuron.
			state = CREATE_FIRST_AXON;
			return state;
		    }
		else
		    stepAxonType(); //If not first press, step axontype.
		if (state == CREATE_SECOND_AXON)
		    state = CREATE_N_AXON;
		if (state == CREATE_FIRST_AXON)
		    state = CREATE_SECOND_AXON;
		if (state == CONTINUE_N_AXON)
		    state = CREATE_N_AXON;
		return state;
	    }
	return state;
    }

    public int getState()
    {
	return state;
    }

    public Neuron getNeuron()
    {
	return neuron;
    }
    
    public boolean isReady()
    {
	return templateTypeNeuron != null;
    }

    public void setTemplateType(Neuron neuron)
    {
	templateTypeNeuron = neuron;
	axontypes = null;
	currentAxontype = 0;
	drawType = null;
	currentNeuronType = null;
	initAxonTypes();
    }

    public URI getTypeURI()
    {
	return URIClassifier.parseValidURI(templateTypeNeuron.getType());
    }

    public String getCurrentAxonTypeBeforeClick()
    {
	return axonType;
    }

    private String getCurrentAxonType()
    {
	if (axontypes==null || axontypes.length == 0)
	    return "";
	
	return axontypes[currentAxontype].getType();
    }

    public boolean initAxonTypes()
    {
	try {
	    NeuronType nType = kit.getComponentStore().getAndReferenceNeuronType(getTypeURI());
	    axontypes = nType.getAxonTypes();
	    if (axontypes.length == 0)
		return false;

	    axonType = getCurrentAxonType();
	    currentAxontype = 0;	       
	} catch (ComponentException ce) {
	    return false;
	}
	return true;
    }

    public boolean stepAxonType()
    {
	if (axontypes==null || axontypes.length == 0)
	    return false;

	axonType = getCurrentAxonType();

	currentAxontype++;
	if (currentAxontype == axontypes.length)
	    currentAxontype = 0;
	return true;
    }

    public String getTypeName()
    {
	return MetaDataUtils.getLocalizedString(templateTypeNeuron.getMetaData().
						get_metametadata_language(), 
						templateTypeNeuron.getMetaData().get_general_title()).string;
    }

    public String getBase()
    {
      //TODO(MP):
      //A not very nice try to find the base-uri for the components in the conceptmap.
      //Needs some form of standardization of directory structure....
      //Should maybee be separated into a separate configurable class.

	ConceptMap cmap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	String baseMapURI = cmap.getURI();
	int slashpos=baseMapURI.lastIndexOf('/');
	return baseMapURI.substring(0,slashpos+1);
    }

    public boolean typeOK()
	{
	    return getCurrentNeuronType() !=null;
	}
	    
    public NeuronType getCurrentNeuronType()
    {
	if (currentNeuronType!= null)
	    return currentNeuronType;
	try {
	    currentNeuronType = kit.getComponentStore().getAndReferenceNeuronType(getTypeURI());
	    return currentNeuronType;
	} catch (ComponentException ce) {
	    return null;
	}
    }

    public String getDrawName()
    {	
	if (drawType == null)
	    {
		if (templateTypeNeuron != null)
		    drawType = getTypeName();
		else
		    drawType = "no type selected";
		if (drawType.equals(""))
		    drawType = "[Unknown type name]";
	    }
	

	if (mapEvent !=null && axontypes!=null && axontypes.length !=0)
	    switch (mapEvent.hitType)
		{
		case MapEvent.HIT_BOX:
		case MapEvent.HIT_BOXTITLE:
		case MapEvent.HIT_BOXDATA:
		case MapEvent.HIT_BOXLINE:
		case MapEvent.HIT_AXONLINE:
		    if (state ==CREATE_WAITING && mapEvent.mapObject.getNeuronType()==getCurrentNeuronType())
			return "resume create on this "+drawType;
		    else
			return getCurrentAxonType()+" role in "+drawType;
		default:
		}
	return drawType;
    }   
}
