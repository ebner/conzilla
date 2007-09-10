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


package se.kth.cid.conzilla.center;

import se.kth.cid.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.component.lookup.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

class ConzFactory
{

  private ConzKit kit;
  Conzilla conzilla=null;

  public ConzFactory(ConzKit kit, Conzilla conzilla)
    {
      this.conzilla=conzilla;
      this.kit=kit;
    }  
  
  public ConzWindow newConzWindow(URI map) throws ControllerException
    {
      SimpleController controller=new SimpleController(kit);
      controller.setLayout(new BorderLayout());
      controller.addHistoryListener(new HistoryListener()
				    {
				public void historyEvent(HistoryEvent e)
				  {
				    Tracer.debug("History: type = " + e.getType() +
						 ", sourceMap = " + e.getSourceMapURI() +
						 ", sourceNeuron = " + e.getSourceNeuronURI() +
						 ", destination = " + e.getDestinationURI());
				  }
				  });  
      
      ConzWindow cw=new ConzWindow();
      cw.setController(controller);
      cw.newFactory();
      setMenuBar(cw);
      cw.fixLayout();
      cw.loadMap(map);
      return cw;
    }
  
  abstract class ConzAbstractAction extends AbstractAction
  {
    ConzWindow conzwindow;
    public ConzAbstractAction(ConzWindow conzwindow, String str)
      {
	super(str);
	this.conzwindow=conzwindow;
      }
  }
  
  private void setMenuBar(ConzWindow cw)
    {
      JMenu file = new JMenu("File");
      
      file.add(new ConzAbstractAction(cw, "Open new map")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Opens a new map in existing window.");
		       conzwindow.opentool.open();
		     }
		 });
	file.add(new ConzAbstractAction(cw, "New window")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Create a new window");
		       ConzFactory.this.conzilla.clone(conzwindow);
		     }
		 });
	file.add(new ConzAbstractAction(cw, "Close this window")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Close this window");
		       ConzFactory.this.conzilla.close(conzwindow);
		     }
		 });
	file.add(new ConzAbstractAction(cw, "Exit")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Exits the whole application.");
		       ConzFactory.this.conzilla.exit(0);
		     }
		 });
	JMenu extras = new JMenu("Extras");
	extras.add(new ConzAbstractAction(cw, "Close contentselection view")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Close contentselection view");
		       conzwindow.closeContentSelectionView();
		     }
		 });
	extras.add(new ConzAbstractAction(cw, "Show NeuronEditor")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Shows the NeuronEditor");
		       /*		       NeuronDialogAdapter nda=new NeuronDialogAdapter(kit);
		       Neuron ne=conzwindow.controller.getCurrentMapManager().getDisplayer().getContentDescription().getNeuron();
		       ne.setEditable(true);
		       nda.setNeuron(ne); */
		       NeuronDisplayer nd=ConzFactory.this.kit.neuronDisplayer;
		       // nd.addNeuronDialog(nda);
		       ((JFrame) nd).setVisible(true);
		     }
		 });
	extras.add(new ConzAbstractAction(cw, "Show library")
		 {
		   public void actionPerformed(ActionEvent ae)
		     {
		       Tracer.debug("Show library");
		       ConzFactory.this.kit.libraryDisplayer.showLibrary();
		     }
		 });
	JMenuBar mBar = new JMenuBar();
	mBar.add(file);
	mBar.add(extras);
	cw.setMenuBar(mBar);
    }
}
