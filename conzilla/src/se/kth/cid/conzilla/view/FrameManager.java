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


package se.kth.cid.conzilla.view;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;

import java.beans.*;

public class FrameManager extends AbstractViewManager implements PropertyChangeListener
{ 
  public FrameManager()
    {}

    public String getID()
    {
	return "FRAME_VIEW";
    }

  public View newView(MapController controller)
    {
	FrameView fv = new FrameView(this, controller);

	addView(fv);

	controller.addPropertyChangeListener(this);
	fv.setJMenuBar(makeMenuBar(controller));

	//FIXME: ad hoc, right place to do it?
	//	fv.setLocation(100, 100);
	//	fv.setSize(100, 100);
	return fv;
    }

    protected void closeView(View v, boolean closeController)
    {
	v.getController().removePropertyChangeListener(this);
	((FrameView) v).close(closeController);
    }
    
    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(MapController.MENUS_PROPERTY))
	    {
		MapController mc = (MapController) e.getSource();
		//Pane workaround for mac, mac-menus is moved up and can't contain buttons.
		//Complementary code in FrameView, intermediate rootpane added.

		
		((FrameView) getView(mc)).setJMenuBar(makeMenuBar(mc));
	    }
    }
}
