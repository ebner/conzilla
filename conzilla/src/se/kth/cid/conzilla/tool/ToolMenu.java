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
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class ToolMenu extends AbstractTool implements AlterationTool
{
  protected MapController controller;
  protected ToolSetMenu choice;
  protected MapEvent mapEvent=null;
   
  Vector tools;

  public ToolMenu(String name, MapController controller)
  {
    super(name, Tool.ACTION);
    this.controller=controller;
    choice=new ToolSetMenu(name);
    tools=new Vector();
  }
  public void addTool(Tool tool)
  {
      tools.addElement(tool);
      choice.addTool(tool);
  }

  public JMenuItem getMenuItem()
  {
    return choice;
  }

  public void update(Object o)
  {
    if ( o != null && o instanceof MapEvent)
	{
	  mapEvent=(MapEvent) o;
	  if (!choice.getPopupMenu().isVisible())
	    choice.update(mapEvent);
	}
  }

  /** Observe that a ToolMenu shouldn't be activated if it is used as
   *  a submenu. If it is alone responstible for invoking itself it should be activated.
   *  Otherwise its parent menu will invoke it when needed instead of having a mouselistener
   *  waiting to find a suitable trigger.
   */
  protected void activateImpl()
  {	
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension choiceSize = choice.getPopupMenu().getSize();

      if (choiceSize.width == 0)
	  choiceSize = choice.getPopupMenu().getPreferredSize();

      Point p = new Point(mapEvent.mouseEvent.getX(), mapEvent.mouseEvent.getY());
      SwingUtilities.convertPointToScreen(p, controller.getMapScrollPane().getDisplayer());
	
      if (p.x + choiceSize.width >= screenSize.width)
	  p.x -= choiceSize.width;

      if (p.y + choiceSize.height >= screenSize.height)
	  p.y -= choiceSize.height;
      
      SwingUtilities.convertPointFromScreen(p, controller.getMapScrollPane().getDisplayer());
      
      choice.getPopupMenu().show(controller.getMapScrollPane().getDisplayer(),
				 p.x,
				 p.y);      
  }
  
  protected void deactivateImpl()
  {}

  protected void detachImpl()
  {
      controller=null;
      choice=null;
      mapEvent=null;
      Enumeration en=tools.elements();
      for (;en.hasMoreElements();)
	  ((Tool) en.nextElement()).detach();
      tools=null;
  }
}
