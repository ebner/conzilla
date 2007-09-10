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


package se.kth.cid.conzilla.map;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import java.util.*;


public class MapStoreManager implements EditListener
{
  ConceptMap conceptMap;

  Hashtable neurons;
  Hashtable neuronTypes;

  ComponentStore store;

  Vector editListeners;
  
  public MapStoreManager(URI mapURI, ComponentStore store)
    throws ComponentException
    {
      this.store = store;
      neurons = new Hashtable();
      neuronTypes = new Hashtable();

      editListeners = new Vector();
      
      conceptMap = store.getAndReferenceConceptMap(mapURI);

      referenceNeurons();
      
      conceptMap.addEditListener(this);
    }

  public void addEditListener(EditListener e)
    {
      editListeners.add(e);
    }

  public void removeEditListener(EditListener e)
    {
      editListeners.remove(e);
    }
  
  
  void referenceNeurons()
    {      
      NeuronStyle[] styles = conceptMap.getNeuronStyles();
      for(int i = 0; i < styles.length; i++) 
	{
	  referenceNeuron(styles[i]);
	}
    }


  void referenceNeuron(NeuronStyle style)
    {
      try {
	URI uri = URIClassifier.parseValidURI(style.getNeuronURI(), conceptMap.getURI());
	Neuron n = store.getAndReferenceNeuron(uri);
	neurons.put(style.getURI(), n);
	n.addEditListener(this);
	
	uri = URIClassifier.parseValidURI(n.getType(), n.getURI());
	NeuronType nt = store.getAndReferenceNeuronType(uri);
	neuronTypes.put(style.getURI(), nt);
	nt.addEditListener(this);
      } catch (ComponentException e)
	{
	  Tracer.debug("No Component found: \n" + e.getMessage());
	}
    }
  
  public Neuron getNeuron(String styleID)
    {
      return (Neuron) neurons.get(styleID);
    }

  public NeuronType getNeuronType(String styleID)
    {
      return (NeuronType) neuronTypes.get(styleID);
    }
  
  
  public void detach()
    {
      conceptMap.removeEditListener(this);

      Iterator it = neurons.values().iterator();
      while(it.hasNext())
	((Neuron) it.next()).removeEditListener(this);

      it = neuronTypes.values().iterator();
      while(it.hasNext())
	((NeuronType) it.next()).removeEditListener(this);

      neurons = null;
      neuronTypes = null;

    }
  
  public ComponentStore getStore()
    {
      return store;
    }
  
  
  public ConceptMap getConceptMap()
    {
      return conceptMap;
    }

  public void componentEdited(EditEvent e)
    {
      if(e.getEditType() == ConceptMap.NEURONSTYLE_ADDED)
	{
	  referenceNeuron(((ConceptMap) e.getEditedObject()).getNeuronStyle((String) e.getTarget()));
	}
      else if(e.getEditType() == ConceptMap.NEURONSTYLE_REMOVED)
	{
	  Neuron n      = (Neuron) neurons.get((String) e.getTarget());
	  NeuronType nt = (NeuronType) neuronTypes.get((String) e.getTarget());
	  
	  if(n != null)
	    {
	      neurons.remove((String) e.getTarget());
	      n.removeEditListener(this);
	    }
	  if(nt != null)
	    {
	      neuronTypes.remove((String) e.getTarget());
	      nt.removeEditListener(this);
	    }
	}
      for(int i = 0; i < editListeners.size(); i++)
	((EditListener) editListeners.get(i)).componentEdited(e);  
    }
}
