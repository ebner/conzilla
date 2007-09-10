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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.library.*;
import javax.swing.*;

abstract class InsertMapTool extends MapTool
{
  public InsertMapTool(String name, MapController cont)
  {
    super(name,Tool.ACTION, cont);
  }
    
  protected NeuronStyle makeNeuronStyle(Neuron neuron) throws InvalidURIException
    {
	ConceptMap cmap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	
	URI base = URIClassifier.parseValidURI(cmap.getURI());
	URI absoluteURI=URIClassifier.parseValidURI(neuron.getURI());
	String relativeURI;
	try {
	    relativeURI=base.makeRelative(absoluteURI, false);
	} catch (MalformedURIException me)
	    {
		relativeURI=absoluteURI.toString();
	    }
	
	NeuronStyle ns = cmap.addNeuronStyle(relativeURI);
	MetaData md=neuron.getMetaData();
	
	//context relation
	if (neuron.isEditable())
	    {
		try {
		    relativeURI=absoluteURI.makeRelative(base, false);
		} catch (MalformedURIException me)
		    {
			relativeURI=base.toString();
		    }
		
		//context relation		

		MetaData.Relation rel=MetaDataUtils.getRelationTo(neuron.getMetaData(), "context", base);
		if (rel==null)
		    {
			MetaData.Relation relation=new MetaData.Relation(new MetaData.LangString(null, "context"),
									 null,
									 relativeURI);
			MetaDataUtils.addObject(neuron.getMetaData(), "relation", relation);
		    }
	    }
	return ns;
    }
  
  protected void showAxons(NeuronStyle ns, Neuron neuron)
    {
	String baseuri1=neuron.getURI();
	NeuronStyle [] nss=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getNeuronStyles();
	Axon [] as=neuron.getAxons();
	for (int i=0; i<as.length;i++)
	  if (ns.getAxonStyle(as[i].getID()) == null) 
	    {
		URI uri1=URIClassifier.parseValidURI(as[i].objectURI(),baseuri1);
			
		for (int j=0;j<nss.length;j++)
		    {
			String baseuri2 =  nss[j].getConceptMap().getURI();
			URI uri2=URIClassifier.parseValidURI(nss[j].getNeuronURI(), baseuri2);
			if (uri2.equals(uri1))
			    {
				se.kth.cid.conzilla.map.graphics.NeuronMapObject nmo=controller.getMapScrollPane().getDisplayer().getNeuronMapObject(nss[j].getID());
				AxonEdit.addAxonStyle(as[i], ns, nmo.getNeuronStyle(),ns.getBoundingBox().pos, ((EditMapManager) controller.getManager()).getGridModel());
				
			    }
		    }
	    }
    }
}

