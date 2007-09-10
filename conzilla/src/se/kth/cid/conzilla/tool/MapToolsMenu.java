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


package se.kth.cid.conzilla.tool;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/** This class is a menu that supports adding tools as buttons.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class MapToolsMenu extends ToolsMenu implements MapMenuItem
{
    protected MapController controller;
    
    Hashtable mapMenuTools;

    public MapToolsMenu(String title, String resbundle, MapController cont)
    {
	super(title, resbundle);
	
	controller = cont;
	
	mapMenuTools = new Hashtable();
    }

    public JMenuItem getJMenuItem()
    {
	return this;
    }
    
    
    public void addMapMenuItem(MapMenuItem t, int prio)
    {
	JMenuItem mi = t.getJMenuItem();
	
	mapMenuTools.put(t, mi);
	add(mi);
	setPriority(mi, prio);
    }

    public void removeMapMenuItem(MapMenuItem t)
    {
	JMenuItem mi = (JMenuItem) mapMenuTools.get(t);

	if(mi == null)
	    return;
	
	mapMenuTools.remove(t);
	remove(mi);
    }

    public void update(MapEvent mapEvent)
    {
	
	Enumeration en = mapMenuTools.keys();
	while (en.hasMoreElements())
	    {
		MapMenuItem tool = (MapMenuItem) en.nextElement();
		tool.update(mapEvent);
		
		JMenuItem newME = tool.getJMenuItem();
		JMenuItem oldME = (JMenuItem) mapMenuTools.get(tool);

		if (newME != oldME)
		    {
			int prio = getPriority(oldME);
			mapMenuTools.put(tool, newME);
			int pos = getPopupMenu().getComponentIndex(oldME);
			getPopupMenu().remove(oldME);
			getPopupMenu().insert(newME, pos);
			setPriority(newME, prio);
		    }
	    }
    }

    public void popup(MapEvent mapEvent)
    {
	update(mapEvent);
	
	SwingUtilities.updateComponentTreeUI(this);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension choiceSize = getPopupMenu().getSize();

      if (choiceSize.width == 0)
	  choiceSize = getPopupMenu().getPreferredSize();

      Point p = new Point(mapEvent.mouseEvent.getX(), mapEvent.mouseEvent.getY());
      SwingUtilities.convertPointToScreen(p, controller.getMapScrollPane().getDisplayer());
	
      if (p.x + choiceSize.width >= screenSize.width)
	  p.x -= choiceSize.width;

      if (p.y + choiceSize.height >= screenSize.height)
	  p.y -= choiceSize.height;
      
      SwingUtilities.convertPointFromScreen(p, controller.getMapScrollPane().getDisplayer());
      
      getPopupMenu().show(controller.getMapScrollPane().getDisplayer(), p.x, p.y);      
    }
    
}
