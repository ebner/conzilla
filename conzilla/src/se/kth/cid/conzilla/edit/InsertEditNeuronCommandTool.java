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
import se.kth.cid.content.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.center.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** This is a template-command-tool that have to be embedded into a menu.
 *  It launches a dialog where information about the neuron have to
 *  be filled in.
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class InsertEditNeuronCommandTool extends AbstractActionTool
{

  ConzKit kit;
  
  protected static String IEN_NAME="from neuronEdit";

  /** Constructs a TemplateCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public InsertEditNeuronCommandTool(MapManager man, ConzKit kit)
  {
    super(IEN_NAME, man, null);
    this.kit=kit;
    action=new AbstractAction(IEN_NAME)
      {
	public void actionPerformed(ActionEvent e)
	  {
	    action();
	  }
      };
  }  
  
  protected boolean updateActionImpl(boolean bo)
    {
      if ( !bo && kit.neuronDisplayer.getSelected()!=null &&
	   mapevent.editabilityChecked && mapevent.mapEditable)
	{
	  action.setEnabled(true);
	  return true;
	}
      action.setEnabled(false);
      return false;
    }
  
  protected void insertEditNeuron()
    {
      Neuron ne=kit.neuronDisplayer.getSelected();
      if (manager!=null && ne!=null)
	try{
	  ConceptMap conceptmap=manager.getDisplayer().getMap();
	  NeuronStyle ns=conceptmap.addNeuronStyle(new URI(ne.getURI()));
	  MouseEvent me=mapevent.mouseevent;   //shouldn't be null if we've come this far.
	  ns.setBoundingBox(new Rectangle(me.getX(),me.getY(), 70, 30));
	  ns.setTitle(ns.getNeuron().getMetaData().getValue("Title"));
	} catch (ReadOnlyException roe) {
	} catch (NeuronStyleException nse) {
	} catch (ComponentException ce) {
	} catch (MalformedURIException me) {}
    }
}
  
