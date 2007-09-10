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


package se.kth.cid.conzilla.browse;
import se.kth.cid.util.*;
import se.kth.cid.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;



public class ContentTool extends AbstractTool
{
  MapManager manager;
  MapController controller;
  /** Listens for content selections.
   */
  SelectionListener selectionListener;

  /** Listens for content-selctor change events.
   */
  PropertyChangeListener propertyListener;

  JPopupMenu choice;
  AbstractAction content;
  InfoCommandTool info;
  StoreCommandTool store;
  ContentEvent ce;
  
  public ContentTool(MapManager manager, MapController controller)
  {
    super("Content",Tool.EXCLUSIVE);
    this.manager=manager;
    this.controller=controller;
    selectionListener = new SelectionListener() {
      public void contentSelected(ContentEvent c)
	{
	  if(ContentTool.this.controller.getCurrentMapManager() ==
	     ContentTool.this.manager)
	    {
	      ce=c;
	      info.registerContentDescription(c.contentdescription);
	      store.registerContentDescription(c.contentdescription);
	      choice.show((java.awt.Component) c.getSource(),c.getX(),
			  c.getY());
	    }
	}
    };
    propertyListener = 
      new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e)
	{
	  Tracer.debug("Propchange: " + e.getPropertyName());
	  if(e.getPropertyName().equals("contentSelector"))
	    {
		ContentSelector oldSel = (ContentSelector) e.getOldValue();
		ContentSelector newSel = (ContentSelector) e.getNewValue();
		if(oldSel != null)
		  oldSel.removeSelectionListener(selectionListener);
		if(newSel != null)
		  newSel.addSelectionListener(selectionListener);
	    }
	}
    };
    content=new AbstractAction("content")
      {
        public void actionPerformed(ActionEvent ae)
	  {
	    displayContent();
	  }
      };
    info=new InfoCommandTool(manager,controller);
    store=new StoreCommandTool(manager,controller);
    choice=new JPopupMenu();
    choice.add(content);
    info.putToolInMenu(choice);
    store.putToolInMenu(choice);
    
  }  
  
  protected void activateImpl()
  {
    controller.addPropertyChangeListener(propertyListener);
    ContentSelector sel = controller.getContentSelector();
    if(sel != null)
      sel.addSelectionListener(selectionListener);
  }

  protected void deactivateImpl()
  {
    controller.removePropertyChangeListener(propertyListener);
    ContentSelector sel = controller.getContentSelector();
    if(sel != null)
      sel.removeSelectionListener(selectionListener);
  }

  protected void detachImpl()
  {
    selectionListener = null;
    propertyListener = null;
    info=null;
    store=null;
    
    controller = null;
    manager = null;
  }

  /** Called when a content has been selected.
   *  And displays it during firing a historyevent.
   *
   */
  protected void displayContent()
  {    
    ContentDisplayer displayer = controller.getContentDisplayer();
    if(displayer != null)
      {
	try {
	  displayer.setContent(ce.contentdescription);
	  String neuronTitle = controller.getCurrentMapManager().
	    getDisplayer().getMap().
	    getNeuronStyle(controller.getSelectContent()).getTitle();
	  controller.
	    fireHistoryEvent(new HistoryEvent(HistoryEvent.CONTENT,
					      controller,
					      controller.getCurrentMapURI(),
					      controller.getCurrentMapTitle(),
					      controller.getSelectContent(),
					      neuronTitle,
					      ce.contentdescription.getURI(),
					      ce.contentdescription.getNeuron().getMetaData().getValue("Title")));
	} catch(ContentException e)
	  {
	    TextOptionPane.showError(manager,
				     "Could not show content:\n " +
				     e.getMessage());
	  }
      }
  }

}
