/* $Id$:*/
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

package se.kth.cid.conzilla.edit;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.library.GenericLibraryMenuWrapper;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import se.kth.cid.library.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;

import javax.swing.*;
import java.awt.event.*;

class CreateNeuronMapTool extends ActionMapMenuTool 
{
  MapEvent draftInvokedMapEvent;

  GridModel gridModel;

  public CreateNeuronMapTool(MapController cont, GridModel model)
    {
      super("CREATE_NEURON", EditMapManagerFactory.class.getName(), cont);

      gridModel = model;
    }
    
  
  public boolean updateEnabled()
  {
      if (mapEvent.hitType==MapEvent.HIT_NONE)
	  return true;
      else
	  return false;
  }
    
  public void actionPerformed(ActionEvent e)
    {
      NeuronDraft componentDraft = new NeuronDraft(controller.getConzillaKit(), controller.getMapScrollPane());

      componentDraft.hintBaseURI(controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI(), true);

      componentDraft.show();

      Component comp = componentDraft.getComponent();

      if(comp == null)
	return;

      ConceptMap cmap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
      
      URI base = URIClassifier.parseValidURI(cmap.getURI());
      URI absoluteURI = URIClassifier.parseValidURI(comp.getURI());
      String relativeURI;
      try {
	relativeURI = base.makeRelative(absoluteURI, false);
      } catch (MalformedURIException me)
	{
	  relativeURI = comp.getURI();
	}

      NeuronStyle ns = null;
      try {
	ns = cmap.addNeuronStyle(relativeURI);	
      } catch(InvalidURIException ex)
	{
	  Tracer.bug("Invalid URI: " + ex.getMessage());
	}
      
      try {
	relativeURI = absoluteURI.makeRelative(base, false);
      } catch (MalformedURIException me)
	{
	  relativeURI = cmap.getURI();
	}
      
      MetaData md = comp.getMetaData();	


      //context relation
      MetaData.Relation relation = new MetaData.Relation(new MetaData.LangString(null, "context"),
							 null,
							 relativeURI);
      MetaDataUtils.addObject(md, "relation", relation);
      

      ConceptMap.BoundingBox box = new ConceptMap.BoundingBox(mapEvent.mapX,
							      mapEvent.mapY,
							      80,30);

      java.awt.Dimension dim=controller.getMapScrollPane().getDisplayer().getNeuronMapObject(ns.getURI()).getPreferredSize();
      ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, 
						       mapEvent.mapX,
						       mapEvent.mapY,
						       dim));
    }
  

}

