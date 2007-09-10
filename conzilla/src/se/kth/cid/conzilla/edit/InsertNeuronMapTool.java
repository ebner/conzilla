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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import javax.swing.*;
import java.awt.event.*;

class InsertNeuronMapTool extends InsertMapTool
{
  /** The last uri typed.*/ 
  String lastval;
  GridModel gridModel;

  public InsertNeuronMapTool(MapController cont, GridModel gm)
  {
    super("INSERT_NEURON_WITH_URI", EditMapManagerFactory.class.getName(), cont);

    gridModel = gm;
  }
    
  protected boolean updateEnabled()
  {
    return (mapEvent.hitType==MapEvent.HIT_NONE);
  }

  public void actionPerformed(ActionEvent e)
  {
    String newval = (String) JOptionPane.showInputDialog((java.awt.Component) mapEvent.mouseEvent.getSource(), "Enter URI for neuron",
							 "New Neuron",
							 JOptionPane.QUESTION_MESSAGE,
							 null, null, lastval);
    if(newval != null)
      {
	try {
	    lastval = newval;

	    ConceptMap cmap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	    URI base = URIClassifier.parseValidURI(cmap.getURI());

	    URI nuri=URIClassifier.parseURI(newval, base);
	    
	    Neuron neuron=controller.getConzillaKit().getComponentStore().getAndReferenceNeuron(nuri);
	    NeuronStyle ns=makeNeuronStyle(neuron);

	    java.awt.Dimension dim=controller.getMapScrollPane().getDisplayer().getNeuronMapObject(ns.getURI()).getPreferredSize();
	    	    
	    ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel, 
							     mapEvent.mapX,
							     mapEvent.mapY,
							     dim));
	    
	    showAxons(ns, neuron);
	} catch (ComponentException ce) {
	    ErrorMessage.showError("Not found.", "Couldn't find neuron.", ce,controller.getMapScrollPane().getDisplayer()); 
	} catch (MalformedURIException me) {
	    ErrorMessage.showError("Not an URI.", "The identifier doesn't conform to the URI standard.", 
				   me,controller.getMapScrollPane().getDisplayer()); 
	} catch (ReadOnlyException re) {
	    Tracer.bug("You shouldn't be able to choose 'insert neuron' from menu when map isn't editable.");
	} catch (InvalidURIException iue) {
	    ErrorMessage.showError("Not found.", "Couldn't find neuron.", iue,controller.getMapScrollPane().getDisplayer()); 
	}
      }

    }
}

