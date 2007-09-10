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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

public class BrowseMapManager implements MapManager
{
  LinearHistoryManager historyManager;
  
  MapController controller;
  ConzillaWindow window;

  Tool suckIn;
  Tool zoomIn;
  Tool zoomOut;
  Tool browse;
  
  public BrowseMapManager(ConceptMap map, MapController controller, LinearHistoryManager histMan, Tool suckIn, Tool browse)
    {
      this.historyManager = histMan;
      this.controller = controller;
      
      this.suckIn = suckIn;
      this.browse = browse;

      zoomIn = new ZoomTool(controller, 1.3);
      zoomOut = new ZoomTool(controller, 1/1.3);
    }
  public void setConzillaWindow(ConzillaWindow window)
    {
      this.window=window;
    }

  public void initialize(ToolSetBar bar)
    {
      addTools(bar);
      //Add menus to Conzillawindow here.
      browse.activate();
    }

  public void deInitialize(ToolSetBar bar)
    {
	//Remove menus from Conzillawindow here.
	browse.deactivate();
	removeTools(bar);
    }

  // Library getLibrary();
  
  protected void addTools(ToolSetBar bar)
    {
	removeTools(bar);

	//      bar.addTool(browse);
	bar.addTool(zoomIn);
	bar.addTool(zoomOut);
	historyManager.createTools(bar);
	bar.addTool(suckIn);
    }
    
  protected void removeTools(ToolSetBar bar)
    {
      historyManager.detachTools(bar);
      
      //      bar.removeTool(browse);
      bar.removeTool(suckIn);
      bar.removeTool(zoomIn);
      bar.removeTool(zoomOut);
    }
}
