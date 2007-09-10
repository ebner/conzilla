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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import se.kth.cid.content.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class SuckInTool extends AbstractTool
{
  MapController controller;
  PropertyChangeListener controllerListener;
  PropertyChangeListener displayerListener;

  ContentDescription contentDescription;

  Component dialogParent;
  
  public SuckInTool(MapController cont, Component dialogParent)
  {
    super("Contextualize", Tool.ACTION);
    controller = cont;
    this.dialogParent = dialogParent;
    
    controllerListener = 
      new PropertyChangeListener() {
	  public void propertyChange(PropertyChangeEvent e)
	  {
	    Tracer.debug("Propchange: " + e.getPropertyName());
	    if(e.getPropertyName().equals("contentDisplayer"))
	      {
		ContentDisplayer oldDisp = (ContentDisplayer) e.getOldValue();
		ContentDisplayer newDisp = (ContentDisplayer) e.getNewValue();
		if(oldDisp != null)
		  oldDisp.removePropertyChangeListener(displayerListener);
		if(newDisp != null)
		  {
		    newDisp.addPropertyChangeListener(displayerListener);
		    setContent(newDisp);
		  }
	      }
	  }
	};

    displayerListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent e)
	{
	  Tracer.debug("Propchange: " + e.getPropertyName());
	  if(e.getPropertyName().equals("content"))
	    setContent((ContentDisplayer) e.getSource());
	}
      };

    cont.addPropertyChangeListener(controllerListener);
    ContentDisplayer disp = cont.getContentDisplayer();
    if(disp != null)
      {
	disp.addPropertyChangeListener(displayerListener);
      }
    setContent(disp);
  }

  void setContent(ContentDisplayer disp)
  {
    ContentDescription cont = null;
    if(disp != null)
      cont = disp.getContent();
    
    if(cont != null &&
       cont.getContentType().toString().equals(ConceptMap.MIME_TYPE))
      {
	contentDescription = cont;
	enable();
      }
    else
      {
	contentDescription = null;
	disable();
      }
  }
  
  protected void activateImpl()
  {
    Tracer.debug("SuckIn!");
    try {
      if(contentDescription != null)
	controller.jump(contentDescription.getURI());
    } catch(ControllerException e)
      {
	TextOptionPane.showError(dialogParent, "Failed to suck in:\n "
				 + e.getMessage());
      }
  }

  protected void deactivateImpl()
  {}
  
  protected void detachImpl()
  {
    controller.removePropertyChangeListener(controllerListener);

    ContentDisplayer disp = controller.getContentDisplayer();

    if(disp != null)
      disp.removePropertyChangeListener(displayerListener);

    controller = null;
    
    contentDescription = null;

    dialogParent = null;
  }
}
