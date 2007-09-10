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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


/** This class creates BroweMapManagers for a single MapController.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class BrowseMapManagerFactory implements MapManagerFactory
{
    public static final String BROWSE_MENU  = "BROWSEMENU";
    
    public static final String COLOR_POPUP_BACKGROUND          = "conzilla.browse.popup.background.color";
    public static final String COLOR_POPUP_BACKGROUND_ACTIVE   = "conzilla.browse.popup.background.activecolor";

    public static final String COLOR_POPUP_TEXT                = "conzilla.browse.popup.text.color";
    public static final String COLOR_POPUP_TEXT_ACTIVE         = "conzilla.browse.popup.text.activecolor";

    public static final String COLOR_POPUP_BORDER              = "conzilla.browse.popup.border.color";
    public static final String COLOR_POPUP_BORDER_ACTIVE       = "conzilla.browse.popup.border.activecolor";

    public static final String COLOR_MOUSEOVER                 = "conzilla.browse.mouseover.color";
    public static final String COLOR_LASTNEURON                = "conzilla.browse.lastneuron.color";
    
    static final String[] COLOR_SET = {
	COLOR_POPUP_BACKGROUND,        
	COLOR_POPUP_BACKGROUND_ACTIVE,
	COLOR_POPUP_TEXT,
	COLOR_POPUP_TEXT_ACTIVE,
	COLOR_POPUP_BORDER,      
	COLOR_POPUP_BORDER_ACTIVE,
	COLOR_MOUSEOVER,
	COLOR_LASTNEURON
    };
    
    String lastMap;
    ConzillaKit kit;
    
    public BrowseMapManagerFactory()
    {
    }

    public String getName()
    {
	return "BrowseMapManagerFactory";
    }
    
    public boolean initExtra(ConzillaKit kit)
    {
	this.kit = kit;
	
	GlobalConfig.getGlobalConfig().addDefaults(BrowseMapManagerFactory.class);
	GlobalConfig.getGlobalConfig().registerColorSet("COLOR_MENU", COLOR_SET,
							BrowseMapManagerFactory.class.getName());
	return true;
    }

    public boolean canManage(MapController mc, URI map)
    {
	return true;
    }

    public void extendMenu(ToolsMenu menu, final MapController mc)
    {
	if(menu.getName().equals(DefaultMenuFactory.FILE_MENU))
	    {
		Tool t = new Tool("OPEN_MAP", BrowseMapManagerFactory.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    openMapInOldView(mc);
			}
		    };
		t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menu.addTool(t, 150);
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
    
    public void exitExtra()
    {}

    
    public MapManager createManager()
    {
	return new BrowseMapManager();
    }


    void findLastMap(MapController controller)
    {
	try {
	    lastMap = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI().toString();
	} catch (NullPointerException ne)
	    {} //ok, no map to find and hence no intelligent URI to start with.
    }
    
    public URI openMap(MapController controller) throws MalformedURIException
    {
	if(lastMap == null)
	    findLastMap(controller);
	
	String newMap = (String) JOptionPane.showInputDialog(null, "Open map", "Open map",
							     JOptionPane.QUESTION_MESSAGE,
							     null, null, lastMap);
	if (newMap != null)
	    return URIClassifier.parseURI(newMap.trim());
	
	return null;
    }      

    public void openMapInOldView(MapController mc)
    {
	try{
	    URI map = openMap(mc);
	    if(map == null)
		return;
	    kit.getConzilla().openMapInOldView(map, kit.getConzilla().getViewManager().getView(mc));
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
