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


package se.kth.cid.conzilla.util;

import se.kth.cid.util.Tracer;
import se.kth.cid.conzilla.properties.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/** 
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class PriorityMenu extends JMenu
{ 
    boolean needSort = false;

    static final String PRIORITY_PROP = "priority";

    static class PrioComparator implements Comparator
    {
	public int compare(Object a, Object b)
	{
	    JComponent ac = (JComponent) a; 
	    JComponent bc = (JComponent) b; 
	    
	    Integer aprio = (Integer) ac.getClientProperty(PRIORITY_PROP);
	    Integer bprio = (Integer) bc.getClientProperty(PRIORITY_PROP);

	    int ap = Integer.MAX_VALUE;
	    int bp = Integer.MAX_VALUE;
	    if(aprio != null)
		ap = aprio.intValue();
	    if(bprio != null)
		bp = bprio.intValue();

	    return ap - bp;
	}
    }

    static PrioComparator comparator = new PrioComparator();

    public PriorityMenu(String formalname, String resbundle)
    {
	setName(formalname);
	if(resbundle == null)
	    resbundle = getClass().getName();
	ConzillaResourceManager.getDefaultManager().customizeButton(this, resbundle, getName());
	getPopupMenu().addPopupMenuListener(new PopupMenuListener()
	    {
		public void popupMenuCanceled(PopupMenuEvent e){}
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e){}
		public void popupMenuWillBecomeVisible(PopupMenuEvent e)
		{
		    if(needSort)
			sortMenu();
		    needSort = false;
		}
	    });
    }
   

    public PriorityMenu(String formalname)
    {
	this(formalname, null);
    }

    public int getPriority(JComponent c)
    {
	Integer prio = (Integer) c.getClientProperty(PRIORITY_PROP);
	return prio.intValue();
    }

    public void setPriority(JComponent c, int prio)
    {
	c.putClientProperty(PRIORITY_PROP, new Integer(prio));
	needSort = true;
    }
    
    void sortMenu()
    {
	Component[] comps = getPopupMenu().getComponents();

	removeAll();
	Arrays.sort(comps, comparator);
	for(int i = 0; i < comps.length; i++)
	    {
		add(comps[i]);
	    }

    }

    public void addSeparator(int prio)
    {
	JSeparator js = new JSeparator();
	add(js);
	setPriority(js, prio);
    }
}
