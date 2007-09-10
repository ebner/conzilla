/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;

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
      SwingUtilities.convertPointToScreen(p, controller.getView().getMapScrollPane().getDisplayer());
	
      if (p.x + choiceSize.width >= screenSize.width)
	  p.x -= choiceSize.width;

      if (p.y + choiceSize.height >= screenSize.height)
	  p.y -= choiceSize.height;
      
      SwingUtilities.convertPointFromScreen(p, controller.getView().getMapScrollPane().getDisplayer());
      
      getPopupMenu().show(controller.getView().getMapScrollPane().getDisplayer(), p.x, p.y);      
    }
    
}
