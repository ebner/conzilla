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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.*;
import java.awt.event.*;

/** 
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class RemoveNeuronMapTool extends ActionMapMenuTool
{

  public RemoveNeuronMapTool(MapController cont)
  {
      super("REMOVE_NEURON", EditMapManagerFactory.class.getName(), cont);
  }
    
  protected boolean updateEnabled()
    {
      if (mapEvent.hitType != MapEvent.HIT_NONE)
	  return true;
      return false;
    }

  public void actionPerformed(ActionEvent e)
    {
	int result=0;
	if (mapObject.getNeuron()!=null)
	    {
		MetaData md=mapObject.getNeuron().getMetaData();
		MetaData.LangString lstr = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title());
		String str = lstr.string;
		
		result=JOptionPane.showConfirmDialog(controller.getMapScrollPane().getDisplayer(),
				    "Warning! \nYou are about to remove a represenation of neuron, titled\n'"+
						     str+"'.\n"+
				    "The representation will only be removed from this map. \n"+
				    "You can still reach the neuron by using it's URI:\n\n"+
				    mapObject.getNeuron().getURI()+"\n\n"+
				    "Continue?", "Remove Neuron", 
				    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	    }
	else
	    {
		result=JOptionPane.showConfirmDialog(controller.getMapScrollPane().getDisplayer(),
				    "Warning you are about to remove a graphical representation \n"+
						     "of a neuron that seems to be missing.\n"+
						     "This could be a temporary fault, like a\n"+
						     "network or server problem. \n"+
				    "Continue?", "Remove representation", 
				    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	    }
	if (result==JOptionPane.NO_OPTION)
	    return;

      mapObject.getNeuronStyle().remove();
      if (mapObject.getNeuron()!=null && mapObject.getNeuron().isEditable())
	  {
	      ConceptMap cMap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	      NeuronStyle [] nss=cMap.getNeuronStyles();
	      boolean stillContained=false;
	      URI base=URIClassifier.parseValidURI(cMap.getURI());
	      String neuronURI=mapObject.getNeuron().getURI();
	      for (int i=0; i<nss.length;i++)
		  try {
		      if (URIClassifier.parseURI(nss[i].getNeuronURI(), base).toString().equals(neuronURI))
			  stillContained=true;
		  } catch (MalformedURIException m)
		      {}
	      if (!stillContained)
		  {
		      Neuron neuron=mapObject.getNeuron();
		      URI mapURI = URIClassifier.parseValidURI(controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI());
		      MetaData.Relation rel=MetaDataUtils.getRelationTo(neuron.getMetaData(), "context", mapURI);
			  if (rel!=null)
			      MetaDataUtils.removeObject(neuron.getMetaData(), "relation", rel);
		  }
	  }
    }
}

