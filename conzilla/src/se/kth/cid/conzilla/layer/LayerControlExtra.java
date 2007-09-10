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


package se.kth.cid.conzilla.layer;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.menu.*;

import java.awt.event.*;

/** 
 *  @author Matthias Palmer.
 */
public class LayerControlExtra implements Extra
{
    public LayerControlExtra()
    {}
    
    public boolean initExtra(ConzillaKit kit)
    {
	return true;
    }

    public String getName()
    {
	return "LayerControl";
    }

    public void extendMenu(ToolsMenu menu, MapController c)
    {
	if(menu.getName().equals(DefaultMenuFactory.TOOLS_MENU))
	    {
		final LayerControl lc = new LayerControl(c);
		Tool t = new Tool("LAYER", LayerControl.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    lc.show(true);
			}
		    };

		//		t.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
		menu.addTool(t, 350);
		//		menu.addSeparator(351);
	    }
    }
    
    public void addExtraFeatures(final MapController c, final Object o, String location, String hint) {}
    
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
