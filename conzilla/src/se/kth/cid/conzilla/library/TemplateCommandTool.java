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
public class TemplateCommandTool extends AbstractActionTool
                                 implements NeuronDraftListener
{

  ConzKit kit;
  JMenu menu;
  NeuronStyle template;
  TemplateLibrary tl;
  NeuronDraft neuronDraft=null;
  MapEvent makeNeuronMapEvent=null;
  
  public class NAA extends AbstractAction
  {
    NeuronStyle ns;
    public NAA(NeuronStyle nns)
      {
	super(nns.getTitle());
	ns=nns;
      }
    public void  actionPerformed(ActionEvent ae)
      {
	template(ns);
      }
  }
  
  protected static String TEMPLATE_NAME="create from template";

  /** Constructs a TemplateCommandTool.
   *
   *  @param man the MapManager the tool is attached to.
   *  @param cont the controller controlling the manager.
   */
  public TemplateCommandTool(MapManager man, ConzKit kit)
  {
    super(TEMPLATE_NAME, man, null);
    this.kit=kit;
    menu=new JMenu(TEMPLATE_NAME);    
  }
  
  public void putToolInMenu(JPopupMenu pmenu)
  {
    pmenu.add(menu);
  }

  
  /** The Template-command is always active.
   */
  protected boolean updateActionImpl(boolean bo)
    {
      if (overNeuron==null)
	{
	  update();
	  return true; 
	}
      menu.setEnabled(false);
      return false;
    }
  
  private void update()
    {
      menu.setEnabled(true);
      menu.removeAll();
      tl=(TemplateLibrary) kit.libraryDisplayer.getLibrary().getLibrary("templatelibrary");
      if (tl!=null)
	{
	  Tracer.debug("TemplateLibrary wasn't null");
	  java.util.Enumeration en=tl.getTemplates();
	  for (;en.hasMoreElements();)
	    {
	      menu.add(new NAA((NeuronStyle) en.nextElement()));
	      Tracer.debug("adding template");
	    }
	}
    }

  public void template(NeuronStyle ns)
    {
      if (neuronDraft!=null)
	{
	  neuronDraft.removeListener(this);
	  neuronDraft=null;
	}
      makeNeuronMapEvent=mapevent;
      template=ns;
      kit.neuronDisplayer.display();
      neuronDraft=kit.neuronDisplayer.addNewDraft();
      
      neuronDraft.hintsFromNeuron(ns.getNeuron());
      if (manager!=null)
	neuronDraft.hintBaseURI(manager.getDisplayer().getMap().getURI().toString(), true);
      else
	neuronDraft.hintBaseURI("cid:", false);
      neuronDraft.addListener(this);
    }
  
  public void madeNeuron(NeuronDraft nd)
    {
      if (manager!=null)
	try{
	  ConceptMap conceptmap=manager.getDisplayer().getMap();
	  NeuronStyle ns=conceptmap.addNeuronStyle(nd.getNeuronURI());
	  MouseEvent me=makeNeuronMapEvent.mouseevent;   //shouldn't be null if we've come this far.
	  ns.setBoundingBox(new Rectangle(me.getX(),me.getY(), 70, 30));
	  ns.setTitle(ns.getNeuron().getMetaData().getValue("Title"));
	} catch (ReadOnlyException roe) {
	} catch (NeuronStyleException nse) {
	} catch (ComponentException ce) {}
    }
  public void neuronCanceled(NeuronDraft nd)
    {
      nd.removeListener(this);
    }

  public void action()
    {
      update();
    }
}
