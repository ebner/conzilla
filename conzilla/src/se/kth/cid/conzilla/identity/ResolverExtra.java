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


package se.kth.cid.conzilla.identity;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;

import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 *  @author Matthias Palmer.
 */
public class ResolverExtra implements Extra
{
    ResolverEdit editor;
    ConzillaKit kit;

    public ResolverExtra()
    {
    }

    public boolean initExtra(ConzillaKit kit) 
    {
	this.kit = kit;
	return true;
    }

    public String getName()
    {
	return "ResolverExtra";
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
	if(menu.getName().equals(MenuFactory.SETTINGS_MENU))
	    {
		menu.addTool(new Tool("RESOLVER_EDITOR", ResolverExtra.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    showEditor();
			}
		    }, 400);
	    }
    }
    
    
    void showEditor()
    {
	if(editor == null)
	    editor = new ResolverEdit(kit.getConzillaEnvironment().getResolverManager());
	editor.show();
    }

    public void addExtraFeatures(final MapController c, final Object o, 
				 String location, String hint)
    {}
}
