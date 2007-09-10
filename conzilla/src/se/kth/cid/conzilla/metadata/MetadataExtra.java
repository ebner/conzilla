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


package se.kth.cid.conzilla.metadata;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.component.*;

import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 *  @author Matthias Palmer.
 */
public class MetadataExtra implements Extra
{
    FrameMetaDataDisplayer metadataDisplayer;

    public MetadataExtra()
    {
    }

    public boolean initExtra(ConzillaKit kit) 
    {
	metadataDisplayer = new FrameMetaDataDisplayer();
	return true;
    }

    public String getName()
    {
	return "MetadataExtra";
    }

    public void refreshExtra() 
    {
    }
    
    public boolean saveExtra()
    {
	return true;
    }

    public void exitExtra() {}

    public void extendMenu(ToolsMenu menu, final MapController c)
    {
	if(menu.getName().equals(BrowseMapManagerFactory.BROWSE_MENU))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new InfoMapTool(c, metadataDisplayer), 300);
	    }
	else if(menu.getName().equals(ContentMenu.CONTENT_MENU))
	    {
		final ContentMenu cm = (ContentMenu) menu;
		final MapController mc = c;

		cm.addTool(new ContentTool("INFO", MetadataExtra.class.getName()) {
			public void actionPerformed(ActionEvent e) {
			    Component comp = mc.getContentSelector().getContent(contentIndex);
			    metadataDisplayer.showMetaData(comp);	
			}}, 200);
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_NEURON))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new InfoMapTool(c, metadataDisplayer), 1100);
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_AXON))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new InfoMapTool(c, metadataDisplayer), 1400);
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_MAP))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new InfoMapTool(c, metadataDisplayer), 300);
	    }
	
    }


    public void addExtraFeatures(final MapController c, final Object o, 
				 String location, String hint)
    {}
}
