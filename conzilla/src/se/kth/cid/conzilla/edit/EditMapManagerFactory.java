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


package se.kth.cid.conzilla.edit;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.component.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.component.*;

import java.awt.event.*;


/** This class creates EditMapManagers for a single MapController.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class EditMapManagerFactory implements MapManagerFactory
{
    public static final String COLOR_CHOICES      = "conzilla.edit.choice.color";
    public static final String COLOR_GRID         = "conzilla.edit.grid.color";

    public static final String EDIT_MENU_NEURON   = "EDIT_MENU_1";
    public static final String EDIT_MENU_AXON     = "EDIT_MENU_2";
    public static final String EDIT_MENU_MAP      = "EDIT_MENU_3";
    public static final String EDIT_MENU_CONTENT  = "EDIT_MENU_CONTENT";

  public EditMapManagerFactory()
    {
    }

    public String getName()
    {
	return "EditMapManagerFactory";
    }

    public boolean initExtra(ConzillaKit kit)
    {
	GlobalConfig.getGlobalConfig().addDefaults(EditMapManagerFactory.class);
	GlobalConfig.getGlobalConfig().registerColorSet("COLOR_MENU", 
							new String[]{EditMapManagerFactory.COLOR_CHOICES,
									 EditMapManagerFactory.COLOR_GRID},
							EditMapManagerFactory.class.getName());
	return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController mc)
    {
	if(menu.getName().equals(MenuFactory.FILE_MENU))
	    {
		menu.addTool(new Tool("NEW_MAP", EditMapManagerFactory.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    Tracer.debug("Create a new map in new window");
			    openNewMapInNewView(mc);
			}
		    }, 160);
	    }
    }

    public void addExtraFeatures(MapController c, Object o, String loc, String hint)
    {}

    public void refreshExtra()
    {}

    public boolean saveExtra()
    {
	return true;
    }
    
    public boolean canManage(MapController controller, URI uri)
    {
	try {
	    ComponentStore store = controller.getConzillaKit().getComponentStore();
	    Component comp = store.getAndReferenceComponent(uri);
	    return comp instanceof ConceptMap && comp.isEditable();
	} catch (ComponentException ce)
	    {}
	return false;
    }
  
    public void exitExtra()
    {}

    public MapManager createManager()
    {
	return new EditMapManager();
    }
    
    public URI openNewMap(MapController controller) throws MalformedURIException
    {
	ComponentDraft componentDraft = new ConceptMapDraft(controller.getConzillaKit(), controller.getMapScrollPane());
	
	componentDraft.hintBaseURI(controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI().toString(), true);
	componentDraft.show();
	
	Component c = componentDraft.getComponent();
	if(c == null)
	    return null;
	
	
	return URIClassifier.parseValidURI(c.getURI());
    }      
    
    public void openNewMapInNewView(MapController mc)
    {
	try{
	    URI map = openNewMap(mc);
	    if(map == null)
		return;
	    mc.getConzillaKit().getConzilla().openMapInNewView(map, this, mc);
	} catch (MalformedURIException me) {
	    ErrorMessage.showError("Parse Error",
				   "Invalid URI\n\n",
				   me, null);
	} catch (ControllerException ce) {
	    ErrorMessage.showError("Load Error",
				   "Failed to open map\n\n",
				   ce, null);
	}
    }

}
