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
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class SuckInTool extends Tool
{
  PropertyChangeListener displayerListener;

  ContentSelector sel;

  MapController controller;

  Component content;

  java.awt.Component dialogParent;
  
  public SuckInTool(MapController cont, java.awt.Component dialogParent)
  {
      super("CONTEXTUALIZE", BrowseMapManagerFactory.class.getName()); 
      setIcon(new ImageIcon(SuckInTool.class.getResource("/graphics/toolbarButtonGraphics/general/Import16.gif")));
    this.controller = cont;
    this.sel = controller.getContentSelector();
    
    this.dialogParent = dialogParent;
    
    displayerListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent e)
	{
	    setContent();
	}
      };
    
    sel.addSelectionListener(ContentSelector.SELECTION, displayerListener);

    setContent();
    
  }

  
  void setContent()
    {
      Component cont = sel.getSelectedContent();
      if(cont != null &&
	 cont instanceof ConceptMap)
	{
	  content = cont;
	  setEnabled(true);
	}
      else
	{
	  content = null;
	  setEnabled(false);
	}
    }
  
  public void actionPerformed(ActionEvent e)
    {
      Tracer.debug("SuckIn!");
      try {
	if(content != null)
	  {
	    ConceptMap oldMap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	    
	    controller.showMap(URIClassifier.parseValidURI(content.getURI()));
	    controller.getHistoryManager().fireOpenNewMapEvent(controller,oldMap,
								URIClassifier.parseValidURI(content.getURI()));
	    try {
		controller.getConzillaKit().getContentDisplayer().setContent(null); //Closing ContentDisplayer.
	    } catch (ContentException ce)
		{
		    Tracer.bug("Couldn't close contentDisplayer");
		} 
	  }
      } catch(ControllerException ex)
	{
	  ErrorMessage.showError("Load Error",
				 "Failed to load map\n\n" + content.getURI(),
				 ex, dialogParent);
	}
    }
  
  public void detach()
    {
      sel.removeSelectionListener(ContentSelector.SELECTION, displayerListener);

      sel = null;
      
      controller = null;
      
      content = null;
      
      dialogParent = null;
    }
}
