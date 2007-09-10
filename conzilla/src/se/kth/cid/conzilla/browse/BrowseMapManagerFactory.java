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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;


/** This class creates BroweMapManagers for a single MapController.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class BrowseMapManagerFactory implements MapManagerFactory
{

  LinearHistoryManager historyManager;

  MapController controller;
    
  Tool suckIn;
  Tool browse;
  
  ToolSet toolSet;
  
  public BrowseMapManagerFactory()
    {
    }

  public void setController(MapController controller)
    {
      this.controller = controller;
      historyManager = new LinearHistoryManager(controller);

      suckIn = new SuckInTool(controller, controller.getMapPanel());
      browse = new BrowseMenu(controller);
      
      toolSet = new ToolSet();
      toolSet.addTool(browse);
      toolSet.addTool(suckIn);
    }
  
  
  public MapManager createManager(ConceptMap map)
    {
      return new BrowseMapManager(map, controller, historyManager, suckIn, browse);
    }
}
