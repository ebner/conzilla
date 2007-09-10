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

  ContentDisplayer disp;

  MapController controller;

  Component content;

  java.awt.Component dialogParent;
  
  public SuckInTool(MapController cont, java.awt.Component dialogParent)
  {
    super("Contextualize", Tool.ACTION);
    this.controller = cont;
    this.disp = controller.getConzillaKit().getContentDisplayer();
    
    this.dialogParent = dialogParent;
    
    displayerListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent e)
	{
	  Tracer.debug("Propchange: " + e.getPropertyName());
	  if(e.getPropertyName().equals("content"))
	    setContent();
	}
      };
    
    disp.addPropertyChangeListener(displayerListener);

    setContent();
  }
  
  void setContent()
    {
      Component cont = disp.getContent();
      
      if(cont != null &&
	 cont instanceof ConceptMap)
	{
	  content = cont;
	  enable();
	}
      else
	{
	  content = null;
	  disable();
	}
    }
  
  protected void activateImpl()
    {
      Tracer.debug("SuckIn!");
      try {
	if(content != null)
	  controller.showMap(URIClassifier.parseURI(content.getURI()));
      } catch(ControllerException e)
	{
	  TextOptionPane.showError(dialogParent, "Failed to suck in:\n "
				   + e.getMessage());
	}
      catch(MalformedURIException e)
	{
	  Tracer.trace("Malformed URI: " + e.getMessage(), Tracer.ERROR);
	}
    }
  
  protected void deactivateImpl()
    {}
  
  protected void detachImpl()
    {
      disp.removePropertyChangeListener(displayerListener);

      disp = null;
      
      controller = null;
      
      content = null;
      
      dialogParent = null;
    }
}
