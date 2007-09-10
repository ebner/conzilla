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

/** Basically a renaming of the functions in a LocalMapGroupStyle to better
 *  fit the layer concept.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class LayerManager
{
    LocalMapGroupStyle layers;

    public LayerManager()
    {
	layers = new LocalMapGroupStyle(null, null);
    }


    public void addLayer(LayerStyle layer, String name)
    {
	layers.addObjectStyle(layer, name);
    }
       
    public void removeLayer(String name)
    {
	ObjectStyle layer = layers.getObjectStyle(name);
	if (os != null)
	    layers.removeObjectStyle(layer);
    }

    public void removeLayer(LayerStyle layer)
    {
	layers.removeObjectStyle(layer);
    }
    
    public void setLayerVisible(String name, boolean visible)
    {
	layers.setTagVisible(name, visible);
    }

    public boolean getLayerVisible(String name)
    {
	return layers.getTagVisible(name);
    }
    
    public Enumeration getLayerNames()
    {
	layers.getTags();
    }

    public void lowerLayer(LayerStyle layer)
    {
	layers.lowerObjectStyle(layer);
    }

    public void raiseLayer(LayerStyle layer)
    {
	layers.raiseObjectStyle(layer);
    }

    public int getOrderOfLayer(LayerStyle layer)
    {
	return layers.getOrderOfObjectStyle(layer);
    }
    
    public Vector getVisibleNeuronStyles()
    {
	return layers.getVisibleNeuronStyles();
    }
}
