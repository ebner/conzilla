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


import se.kth.cid.util.*;

import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.menu.*;

import java.util.*;
import java.beans.*;
import javax.swing.*;
import java.awt.*;

public abstract class AbstractViewManager implements ViewManager
{ 
    private int count = 0;
    ArrayList views;

    PropertyChangeSupport pcs;

    public AbstractViewManager()
    {
    }
    
    public void initManager()
    {
	pcs = new PropertyChangeSupport(this);
	views = new ArrayList();      
    }

    public void detachManager()
    {
	closeViews();
	pcs = null;
	views = null;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
	pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
	pcs.removePropertyChangeListener(l);
    }

    public void fireViewsChanged()
    {
	pcs.firePropertyChange(VIEWS_PROPERTY, null, null);
    }
    
    protected void addView(View view)
    {
	views.add(view);
	fireViewsChanged();
    }

    public View getView(MapController c)
    {
	Iterator e = getViews();
	while(e.hasNext())
	    {
		View v = (View) e.next();
		if(v.getController() == c)
		    return v;
	    }	
	return null;
    }

    public Iterator getViews()
    {
	return views.iterator();
    }

    public void close(View view, boolean closeController)
    {
	view.getController().getMapScrollPane().getDisplayer().reset();
	closeView(view, closeController);
	views.remove(view);
	fireViewsChanged();
    }

    public void closeViews()
    {
	Iterator e = getViews();
	while(e.hasNext())
	    {
		close((View) e.next(), true);
		e = getViews();
	    }
    }

    protected abstract void closeView(View v, boolean closeController);

    protected JMenuBar makeMenuBar(MapController mc)
    {
	JMenuBar mb = new JMenuBar();
	
	ToolsMenu[] menus = mc.getMenus();
	ToolsMenu help = null;

	for(int i = 0; i < menus.length; i++)
	    {
		if(menus[i].getName().equals(MenuFactory.HELP_MENU))
		    help = menus[i];
		else
		    mb.add(menus[i]);
	    }
	
	mb.add(Box.createRigidArea(new Dimension(20, 10)));

	mc.getToolsBar().setFloatable(false);
	mb.add(mc.getToolsBar());

	mb.add(Box.createHorizontalGlue());
	// mb.setHelpMenu(help);   Yields an exception, not yet implemented.
	if(help != null)
	    mb.add(help);

	return mb;
    }
}
