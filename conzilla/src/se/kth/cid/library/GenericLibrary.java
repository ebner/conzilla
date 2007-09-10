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


package se.kth.cid.library;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;

import java.util.*;


public class GenericLibrary
{
  static final String ITEM_AXON_TYPE = "item";
  static final String SUBLIB_AXON_TYPE = "sublibrary";
  
  protected Vector neurons;
  protected Vector subLibNeurons;

  Neuron libNeuron;
  NeuronType libNeuronType;

  ComponentStore store;
  
  public GenericLibrary(ComponentStore store, URI libNeuronURI)
    throws LibraryException
    {
      this.store = store;
      neurons = new Vector();
      subLibNeurons = new Vector();
      
      try {
	libNeuron = store.getAndReferenceNeuron(libNeuronURI);
	libNeuronType = store.getAndReferenceNeuronType(URIClassifier.parseValidURI(libNeuron.getType(), libNeuron.getURI()));

	checkLibrary();
	
	Axon[] axons = libNeuron.getAxons();
	for(int i = 0; i < axons.length; i++)
	  {
	    if(axons[i].predicateURI().equals(ITEM_AXON_TYPE))
	      neurons.add(store.getAndReferenceNeuron(URIClassifier.parseValidURI(axons[i].objectURI(), libNeuron.getURI())));
	    if(axons[i].predicateURI().equals(SUBLIB_AXON_TYPE))
	      subLibNeurons.add(store.getAndReferenceNeuron(URIClassifier.parseValidURI(axons[i].objectURI(), libNeuron.getURI())));
	  }
	
      } catch(ComponentException e)
	{
	    e.printStackTrace();
	  throw new LibraryException("Could not load library: " + "\n  "
				     + e.getMessage());
	}
    }

  protected void checkLibrary() throws LibraryException
    {
      if(!isGenericLibrary(libNeuron, libNeuronType))
	throw new LibraryException("Neuron was no generic library: '" +
				   libNeuron.getURI() + "'.");
    }
  
  
  public void addNeuron(Neuron n) throws LibraryException
    {
      try {
	NeuronType neuronType = store.getAndReferenceNeuronType(URIClassifier.parseValidURI(n.getType(), n.getURI()));

	checkNeuron(n, neuronType);
	
      } catch(ComponentException e)
	{
	  throw new LibraryException("Could not load neuron type:\n  "
				     + e.getMessage());
	}      
      addNeuron(n, ITEM_AXON_TYPE);
    }
  
  void addNeuron(Neuron n, String type) throws LibraryException
    {       
	try{
	    libNeuron.addAxon(type, n.getURI());
      } catch(NeuronException e)
	{
	  throw new LibraryException("Could not add Neuron to Library:\n "
				     + e.getMessage());
	}
      catch(InvalidURIException e)
	{
	  Tracer.error("Invalid URI in neuron; \n" + e.getMessage());
	}
    }

  public void removeNeuron(Neuron n)
    {
      String uri = n.getURI();
      Axon[] axons = libNeuron.getAxons();

      for(int i = 0; i < axons.length; i++)
	{
	  if(axons[i].predicateURI().equals(ITEM_AXON_TYPE)
	     && URIClassifier.parseValidURI(axons[i].objectURI(), libNeuron.getURI()).equals(uri))
	    {
	      libNeuron.removeAxon(axons[i].getURI());
	      neurons.remove(n);
	      return;
	    }
	}
    }
  

  public void addSubLibrary(Neuron n) throws LibraryException
    {
      try {
	NeuronType neuronType = store.getAndReferenceNeuronType(URIClassifier.parseValidURI(n.getType(), n.getURI()));

	checkSubLibNeuron(n, neuronType);
	
      } catch(ComponentException e)
	{
	  throw new LibraryException("Could not load neuron type:\n  "
				     + e.getMessage());
	}      
      addNeuron(n, SUBLIB_AXON_TYPE);
    }

  public void removeSubLibrary(Neuron n)
    {
      String uri = n.getURI();
      Axon[] axons = libNeuron.getAxons();

      for(int i = 0; i < axons.length; i++)
	{
	  if(axons[i].predicateURI().equals(SUBLIB_AXON_TYPE)
	     && URIClassifier.parseValidURI(axons[i].objectURI(), libNeuron.getURI()).equals(uri))
	    {
	      libNeuron.removeAxon(axons[i].getURI());
	      subLibNeurons.remove(n);
	      return;
	    }
	}
    }

  protected void checkSubLibNeuron(Neuron n, NeuronType nt)
    throws LibraryException
    {
      if(!isGenericLibrary(n, nt))
	throw new LibraryException("Neuron was no generic library: '" +
				   libNeuron.getURI() + "'.");
    }

  protected void checkNeuron(Neuron n, NeuronType nt)
    throws LibraryException
    {
    }
  
  public Neuron getLibraryNeuron()
    {
      return libNeuron;
    }
  
  public Neuron[] getNeurons()
    {
      return (Neuron[]) neurons.toArray(new Neuron[neurons.size()]);
    }
  
  public Neuron[] getSubLibraries()
    {
      return (Neuron[]) subLibNeurons.toArray(new Neuron[subLibNeurons.size()]);
    }
  
  public ConceptMap getConceptMap()
    {
      return null;
    }
  
  public static boolean isGenericLibrary(Neuron n, NeuronType type)
    {
      String[] taxon = {"Library", "Generic"};
      
      return MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
					  "NeuronType", "Conzilla", taxon);
    }
}

