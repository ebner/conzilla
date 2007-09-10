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


package se.kth.cid.conzilla.controller;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conceptmap.ConceptMap;
import javax.swing.*;

public class OpenMapHandler
{
  MapController controller;

  String lastMap;
  URI uri;

  public OpenMapHandler(MapController cont)
    {
      controller = cont;
      lastMap = "urn:path:/";
    }  

  private void findLastMap()
    {
	try {
	    lastMap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI().toString();
	} catch (NullPointerException ne)
	    {} //ok, no map to find and hence no intelligent URI to start with.
    }

  public boolean openMap()
    {
      findLastMap();
      String newMap = (String) JOptionPane.showInputDialog(null, "Open map", "Open map",
							   JOptionPane.QUESTION_MESSAGE,
							   null, null, lastMap);
      return openMap(newMap);
    }      
  public boolean openMap(String newMap)
    {
	if (newMap != null)
	  {
	      //	      lastMap = newMap;
	      try {
		  uri = URIClassifier.parseURI(newMap);
		  controller.getConzillaKit().getComponentStore().getAndReferenceConceptMap(uri);
		  return true;
	      } catch (MalformedURIException me) {
		  ErrorMessage.showError("Parse Error",
					 "Invalid URI\n\n" + newMap,
					 me, null);
		  return false;
	      } catch (ComponentException ce) {
		  ErrorMessage.showError("Load Error",
					 "Failed to open map\n\n" + newMap,
					 ce, null);
		  return false;
	      }
	  }
      return false;
    }
    
    public void showOpenedMapInNewWindow()
    {
	showMap(true);
    }
    public void showOpenedMapInSameWindow()
    {
	showMap(false);
    }
    
    private void showMap(boolean nw)
    {
	if (uri==null)
	    Tracer.bug("No map is specified, can't show null  uri.\n"+
		       "Be sure that openMap is called suceffully before calling this function.");
	try {
	    ConceptMap oldMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	    if (!nw)
		{
		    controller.showMap(uri);
		    controller.getHistoryManager().fireOpenNewMapEvent(controller,
								       oldMap,
								       uri);
		    
		}
	    else
		controller.getConzillaKit().getConzilla().openMap(uri);
	} catch (ControllerException ce) {
	    ErrorMessage.showError("Load Error",
				   "Failed to open map\n\n" + uri,
				   ce, null);
	}
    }

  public void openNewMap()
    {
      ComponentEdit cEditor = controller.getConzillaKit().getComponentEdit();
      ComponentDraft componentDraft = new ConceptMapDraft(controller.getConzillaKit(), controller.getMapScrollPane());
      
      findLastMap();
      componentDraft.hintBaseURI(lastMap, true);
      componentDraft.show();

      Component c = componentDraft.getComponent();
      if(c == null)
	return;
      
      controller.getConzillaKit().getConzilla().editMap(URIClassifier.parseValidURI(c.getURI()));
    }
}

