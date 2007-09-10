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


package se.kth.cid.conzilla.clipboard;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.content.ContentMenu;
import se.kth.cid.conzilla.content.ContentTool;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.library.ClipboardLibrary;
import se.kth.cid.component.Component;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.JMenu;
import java.awt.event.*;


/** 
 *  @author Matthias Palmer.
 */
public class Clipboard implements Extra
{
    public Clipboard()
    {}
    
    public boolean initExtra(ConzillaKit kit)
    {
	return true;
    }

    public String getName()
    {
	return "ClipBoard";
    }

    public void extendMenu(ToolsMenu menu, MapController c)
    {
	if(menu.getName().equals(BrowseMapManagerFactory.BROWSE_MENU))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new StoreMapTool(c), 500);
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_CONTENT))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new InsertClipboardContentMapTool(c), 200);
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_NEURON)
		|| menu.getName().equals(EditMapManagerFactory.EDIT_MENU_AXON))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new StoreMapTool(c), 2000);			
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_MAP))
	    {
		((MapToolsMenu) menu).addMapMenuItem(new InsertClipboardNeuronMapTool(c, (EditMapManager) c.getManager()),
						     200);
		((MapToolsMenu) menu).addMapMenuItem(new StoreMapTool(c), 2000);			
	    }
	else if(menu.getName().equals(ContentMenu.CONTENT_MENU))
	    {
		final ContentMenu cm = (ContentMenu) menu;
		final MapController mc = c;

		cm.addTool(new ContentTool("COPY", Clipboard.class.getName()) {
			public void actionPerformed(ActionEvent e)
			{
			    ClipboardLibrary cl = mc.getConzillaKit().getRootLibrary().getClipboardLibrary();
			    Component comp = mc.getContentSelector().getContent(contentIndex);
			    cl.setComponent(comp);
			}}, 400); 
	    }
	
    }
    
    public void addExtraFeatures(final MapController c, final Object o, String location, String hint)
    {
    }
    
    public void refreshExtra()
    {
    }
    public boolean saveExtra()
    {
	return true;
    }

    public void exitExtra()
    {
    }
}
