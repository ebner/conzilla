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


package se.kth.cid.conzilla.library;

import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.component.ComponentException;
import se.kth.cid.content.*;
import se.kth.cid.util.*;
import java.awt.*;
import javax.swing.*;



public abstract class LibraryAdapter implements Library
{
  URI libraryneuron;
  URI librarycontentdescription;
  int ns_width=30, ns_height=30;
  ConceptMap conceptmap;
  MapController controller;
  boolean loaded=false;
  
  public LibraryAdapter(MapController controller,
			URI libraryneuron, URI librarycontentdescription)
    {
      this.controller=controller;
      this.libraryneuron=libraryneuron;
      this.librarycontentdescription=librarycontentdescription;
      loadMap();
    }
  
  public LibraryAdapter(NeuronStyle ns)
    {
      libraryneuron=ns.getURI();
      librarycontentdescription=ns.getDetailedMap();
    }

  public void loadMap()
    {
      if (loaded)
	return;
      ContentDescription thiscDesc;
      try {
	thiscDesc = new ContentDescription(librarycontentdescription, controller.getComponentLoader());
      } catch (ComponentException e)
	{
	  Tracer.trace("Couldn't load library-map's contentdescription." +
		       e.getMessage(), Tracer.MINOR_EXT_EVENT);
	  return;
	}
      se.kth.cid.component.Component comp;
      try {
	comp = controller.getComponentLoader().loadComponent(thiscDesc.getContentURI(), controller.getComponentLoader());
      }
      catch(ComponentException e)
	{
	  Tracer.trace("Library-map "+thiscDesc.getContentURI()+
		       " could not be loaded:\n "
		       + e.getMessage(),Tracer.MINOR_EXT_EVENT);
	  return;
	}
      
      if(! (comp instanceof ConceptMap))
	{
	  controller.getComponentLoader().releaseComponent(comp);
	  Tracer.trace("Concept-map "+ thiscDesc.getContentURI() +
		       " could not be loaded:\n "
		       + "Component was no concept-map", Tracer.MINOR_EXT_EVENT);
	  return;
	}
      conceptmap = (ConceptMap) comp;
      loaded=true;
    }
  
  public URI getLibraryNeuron()
    {
      return libraryneuron;
    }
  
  public URI getLibraryContentDescription()
    {
      return librarycontentdescription;
    }

  public ConceptMap getConceptMap()
    {
      return conceptmap;
    }
  
  public NeuronStyle placeYourselfInMap(ConceptMap cm)
    {
      try {   //Just for formalism, a non connected neuron is always editable.
	NeuronStyle ns=new NeuronStyle(controller.getComponentLoader(),
				       getLibraryNeuron(), cm);
	ns.setBoundingBox(new Rectangle(10,10,ns_width,ns_height));
	ns.setDetailedMap(getLibraryContentDescription());
	if (ns.connect(cm))
	  return ns;
      } catch (Exception e)
	{
	  Tracer.trace(e.getMessage(),Tracer.BUG);
	}
      return null;
    }  
}

