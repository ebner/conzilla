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


package se.kth.cid.conceptmap.local;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import java.util.*;

/** 
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class LocalLayerManager implements LayerManager
{
    MapGroupStyle layers;
    MapGroupStyle current;
    Vector listeners;

    public LocalLayerManager()
    {
	layers = new LocalMapGroupStyle(null, null);
	listeners = new Vector();
    }

    public LocalLayerManager(MapGroupStyle layers)
    {
	this.layers = layers; 
	listeners = new Vector();
    }

    public void addLayerListener(LayerListener list)
    {
	listeners.add(list);
    }
    
    public void removeLayerListener(LayerListener list)
    {
	listeners.remove(list);
    }

    public void fireLayerChange(LayerEvent event)
    {
	Enumeration en = listeners.elements();
	while (en.hasMoreElements())
	    ((LayerListener) en.nextElement()).layerChange(event);
    }

    public LayerStyle createLayer(String id, Object tag, ConceptMap cMap)
    {
	LayerStyle ls = new LocalMapGroupStyle(id, cMap);
	addLayer(ls, tag);
	return ls;
    }

    public void addLayer(LayerStyle layer, Object tag)
    {
	layers.addObjectStyle(layer, tag);
	current = layer;
	fireLayerChange(new LocalLayerEvent(LayerEvent.OBJECTSTYLE_ADDED));
    }
       
    public void removeLayer(String name)
    {
	ObjectStyle layer = layers.getObjectStyle(name);
	if (layer != null)
	    layers.removeObjectStyle(layer);
	if (current == layer)
	    current = null;
	fireLayerChange(new LocalLayerEvent(LayerEvent.OBJECTSTYLE_REMOVED));
    }

    public void removeLayer(LayerStyle layer)
    {
	layers.removeObjectStyle(layer);
	if (current == layer)
	    current = null;
	fireLayerChange(new LocalLayerEvent(LayerEvent.OBJECTSTYLE_REMOVED));
    }

    public void setEditMapGroupStyle(String name)
    {
	if (name == null)
	    current = null;
	else
	    current = (MapGroupStyle) layers.getObjectStyle(name);
    }

    public MapGroupStyle getEditMapGroupStyle()
    {
	return current;
    }

    public void setLayerVisible(String name, boolean visible)
    {
	if (layers.getObjectStyleHidden(name) != !visible)
	    {
		layers.setObjectStyleHidden(name, !visible);
		fireLayerChange(new LocalLayerEvent(LayerEvent.VISIBILITY_CHANGED));
	    }
    }

    public boolean getLayerVisible(String name)
    {
	return layers.getObjectStyleHidden(name);
    }

    public Vector getLayers()
    {
	return layers.getObjectStyles();
    }

    public LayerStyle getLayer(String name)
    {
	ObjectStyle os = layers.getObjectStyle(name);
	if (os instanceof LayerStyle)
	    return (LayerStyle) os;
	return null;
    }
    public MapGroupStyle getParent(ObjectStyle os)
    {
	return layers.getParent(os);
    }

    public void lowerLayer(LayerStyle layer)
    {
	layers.lowerObjectStyle(layer);
	fireLayerChange(new LocalLayerEvent(LayerEvent.ORDER_CHANGED));
    }

    public void raiseLayer(LayerStyle layer)
    {
	layers.raiseObjectStyle(layer);
	fireLayerChange(new LocalLayerEvent(LayerEvent.ORDER_CHANGED));
    }

    public int getOrderOfLayer(LayerStyle layer)
    {
	return layers.getOrderOfObjectStyle(layer);
    }

    public void setOrderOfLayer(LayerStyle layer, int position)
    {
	layers.setOrderOfObjectStyle(layer, position);
	fireLayerChange(new LocalLayerEvent(LayerEvent.ORDER_CHANGED));
    }    
    
    public Vector getNeuronStyles(int visibility)
    {
	Vector collect = new Vector();
	layers.getObjectStyles(collect, visibility, NeuronStyle.class);
	return collect;
    }
    
    public NeuronStyle getNeuronStyle(String id)
    {
	ObjectStyle os =layers.recursivelyGetObjectStyle(id);
	if (os instanceof NeuronStyle)
	    return (NeuronStyle) os;
	return null;
    }

    public Set IDSet()
    {
	return layers.IDSet();
    }

    /** 
     *
     * @param parent the parent where this neuronstyle should be added,
     *               if null the current editMapGroupStyl is used.
     */
    public void addNeuronStyle(NeuronStyle ns, Object tag, String parent)
    {
	ObjectStyle os = null;
	if (parent != null)
	    os = layers.recursivelyGetObjectStyle(parent);
	else if (current != null)
	    os = current;
	else
	    parent = "topLayer";

	if (os != null)
	    {
		if  (!(os instanceof MapGroupStyle))
		    Tracer.bug("Trying to add a NeuronStyle to parent that isn't a MapGroupStyle");
		((MapGroupStyle) os).addObjectStyle(ns, tag);
		fireLayerChange(new LocalLayerEvent(LayerEvent.OBJECTSTYLE_ADDED));
	    }
	else
	    {
		LayerStyle ls = createLayer(parent, parent, ns.getConceptMap());
		ls.addObjectStyle(ns, tag);
		fireLayerChange(new LocalLayerEvent(LayerEvent.OBJECTSTYLE_ADDED));
	    }
    }
    public boolean removeNeuronStyle(NeuronStyle ns)
    {
	//	if (current != null)
	boolean bo = layers.recursivelyRemoveObjectStyle(ns);
	if (bo)
	    fireLayerChange(new LocalLayerEvent(LayerEvent.OBJECTSTYLE_REMOVED));
	    
	return bo;
    }	
}



    /*    
    public int getNumberOfLayers()
    {
	return layers.getNumberOfObjectStyles();
    }

    public Hashtable getHashedNeuronStyles(int visibility)
    {
	Hashtable collect = new Hashtable();
	layers.getObjectStyles(collect, visibility, NeuronStyle.class);
	return collect;
    }

    */
