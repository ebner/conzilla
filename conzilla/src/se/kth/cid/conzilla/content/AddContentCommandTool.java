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


package se.kth.cid.conzilla.content;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a Add Content-command-tool that have to be embedded into a menu.
 *  It launches a dialog where information about the neuron have to
 *  be filled in.
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class AddContentCommandTool extends AbstractActionTool
                                 implements NeuronDraftListener
{

  NeuronDraft neuronDraft=null;
  
  
  protected static String ADD_CONTENT_NAME="add content here";

  /** Constructs a TemplateCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public AddContentCommandTool(MapManager man, MapController cont)
  {
    super(ADD_CONTENT_NAME, man, cont);
    
    ConceptMap conceptmap=manager.getDisplayer().getMap();
    //A not very nice try to find the base-uri for the neurons in the conceptmap.
    //Needs some forma of standardization of directory structure....
    //Should maybee be separated into a separate configurable class.

    action=new AbstractAction(ADD_CONTENT_NAME)
      {
        public void actionPerformed(ActionEvent ae)
	  {
	    action();
	  }
      };

  }
  
  /** The Template-command is always active.
   */
  protected boolean updateActionImpl(boolean bo)
    {
      if (overNeuron!=null)
	{
	  action.setEnabled(mapevent.neuronEditable);
	  if (neuronDraft!=null)
	    {
	      neuronDraft.removeListener(this);
	      neuronDraft=null;
	    }	  
	  return true; 
	}
      return false;
    }

  public void action()
    {
      controller.getConzKit().neuronDisplayer.display();
      neuronDraft=controller.getConzKit().neuronDisplayer.addNewDraft();
      
      neuronDraft.hintNeuronType("cid:local/nt/contentdescription");
      neuronDraft.hintBaseURI(manager.getDisplayer().getMap().getURI().toString(), true);
      neuronDraft.hintData("URI", "http://");
      neuronDraft.hintData("MIMEType", "text/html");
      neuronDraft.addListener(this);
    }
  
  public void madeNeuron(NeuronDraft nd)
    {
      try{
	Neuron ne=overNeuron.getNeuron();
	if (controller.getConzKit().saver.isComponentSavable(ne) && ne.isEditingPossible())
	  if (!ne.isEditable())
	    ne.setEditable(true);
	overNeuron.getNeuron().addRole(new Role("content", nd.getNeuronURI().toString(), 1,1));
      } catch (NeuronException ne) {
      } catch (ReadOnlyException re) {}
    }
  public void neuronCanceled(NeuronDraft nd)
    {
      nd.removeListener(this);
    }
}

