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


package se.kth.cid.conzilla.app;


import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.graphics.Mark;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class Conzilla
{ 
  ConzillaFactory conzfactory;
  ConzillaKit kit;
  Vector cowins;
  
  public Conzilla(ConzillaKit kit)
    {
      this.kit = kit;
      cowins = new Vector();

      this.conzfactory = new ConzillaFactory(kit);      
      
    }

  public boolean openMap(URI map)
    {
      Tracer.debug("Opening " + map);
      try
	{
	  ConzillaWindow cw = conzfactory.createBrowseWindow(map);
	  cowins.addElement(cw);
	} catch(ControllerException e)
	  {
	    ErrorMessage.showError("Load Error",
				   "Unable to load map\n\n" + map,
				   e, null);
	    return false;
	  }
      return true;
    }

  public boolean editMap(URI map)
    {
      Tracer.debug("Opening " + map);
      try
	{
	  ConzillaWindow cw = conzfactory.createEditWindow(map);
	  cowins.addElement(cw);
	} catch(ControllerException e)
	  {
	    ErrorMessage.showError("Load Error",
				   "Unable to load map\n\n" + map, e, null);
	    return false;
	  }
      return true;
    }

  
  public void clone(ConzillaWindow cw)
    {
      openMap(URIClassifier.parseValidURI(cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI()));
    }
  
  public void close(ConzillaWindow cw)
    {
      if (cowins.size() == 1)
	exit(0);
      else
	{
	  cw.getController().getMapScrollPane().getDisplayer().reset();
	  cw.close();
	  cowins.removeElement(cw);
	}
    }

  void resetAll()
    {
      Enumeration en = cowins.elements();
      while(en.hasMoreElements())
	((ConzillaWindow) en.nextElement()).getController().getMapScrollPane().getDisplayer().reset();
    }
  
  public void reload()
    {
      resetAll();
      if(!kit.getComponentEdit().askSaveAll())
	return;

      kit.getComponentStore().getCache().clear();

      kit.getComponentEdit().refresh();
      
      kit.getFilterFactory().refresh();

      Enumeration en = cowins.elements();
      try {
	while(en.hasMoreElements())
	    {
		ConzillaWindow cw=(ConzillaWindow) en.nextElement();
		cw.getController().reload();
		if (!cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().isEditable())
		    if (conzfactory.isConzillawindowInEditMode(cw))
			conzfactory.changeToBrowseWindow(cw);
	    }
      } catch(ControllerException e)
	{
	  ErrorMessage.showError("Reload error", "Cannot reload all maps.",
				 e, null);
	}
      
    }

    //FIXME:  HACK!!! 
  public void pushMark(Set set, Mark mark, Object o)
    {
	Enumeration en = cowins.elements();
	for (;en.hasMoreElements();)
	    ((ConzillaWindow) en.nextElement()).getController().getMapScrollPane().getDisplayer().pushMark(set, mark, o);
    }
  public void popMark(Set set, Object o)
    {
	Enumeration en = cowins.elements();
	for (;en.hasMoreElements();)
	    ((ConzillaWindow) en.nextElement()).getController().getMapScrollPane().getDisplayer().popMark(set, o);

    }

  public void exit(int result)
    {
      resetAll();
      if(!kit.getComponentEdit().askSaveAll())
	return;
      

      conzfactory = null;

      Enumeration en = cowins.elements();
      /*
      for (;en.hasMoreElements();)
	  if (!((ConzillaWindow) en.nextElement()).tryCloseMap())
	      return;
      */
      en = cowins.elements();
      for (;en.hasMoreElements();)
	  ((ConzillaWindow) en.nextElement()).close();
	      
      cowins = null;

      kit.getConzillaEnvironment().exit(result);
    }
}
