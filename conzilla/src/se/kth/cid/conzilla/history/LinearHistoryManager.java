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

package se.kth.cid.conzilla.history;

import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class LinearHistoryManager
{
  LinearHistory history;

  Tool backTool;
  Tool forwardTool;

  MapController controller;
  
  MouseInputAdapter backListener;
  MouseInputAdapter forwardListener;

  JPopupMenu backMenu;
  JPopupMenu forwardMenu;
  
  
  public LinearHistoryManager(MapController controller)
  {
    this.controller = controller;
    history = new LinearHistory();

    backTool = new AbstractIconTool("Back", "properties/toolbarButtonGraphics/navigation/Back16.gif", Tool.ACTION)
      {
	protected void activateImpl()
	  {
	    back();
	  }
	protected void deactivateImpl()
	  {}
	protected void detachImpl()
	  {}
      };

    backTool.disable();
    forwardTool = new AbstractIconTool("Forward","properties/toolbarButtonGraphics/navigation/Forward16.gif", Tool.ACTION)
      {
	protected void activateImpl()
	  {
	    forward();
	  }
	protected void deactivateImpl()
	  {}
	protected void detachImpl()
	  {}
      };
    forwardTool.disable();
    
    controller.getHistoryManager().addHistoryListener(new HistoryListener() {
	public void historyEvent(HistoryEvent e)
	  {
	    switch(e.getType())
	      {
	      case HistoryEvent.MAP:
		history.historyEvent(e);
		updateTools();
	      }
	  }
      });
    
    backListener = new MouseInputAdapter() {
	public void mousePressed(MouseEvent e)
	  {
	      //FIXME isPopupTrigger doesn't work on Windows (2000).	      
	    if(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e))
	      {
		popupBackMenu((Component) e.getSource());
		e.consume();
	      }
	  }
      };
    forwardListener = new MouseInputAdapter() {
	public void mousePressed(MouseEvent e)
	  {
	      //FIXME isPopupTrigger doesn't work on Windows (2000).
	    if(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e))
	      {
		popupForwardMenu((Component) e.getSource());
		e.consume();
	      }
	  }
      };
    
    backMenu = new JPopupMenu();
    forwardMenu = new JPopupMenu();
  }
  
  public LinearHistory getHistory()
    {
      return history;
    }
  
  public void createTools(ToolSetBar toolSetBar)
    {
      AbstractButton but = toolSetBar.addTool(backTool);
      but.addMouseListener(backListener);
      
      but = toolSetBar.addTool(forwardTool);
      but.addMouseListener(forwardListener);
    }
  
  public void detachTools(ToolSetBar toolSetBar)
    {
      AbstractButton back = toolSetBar.getToolButton(backTool);
      AbstractButton forw = toolSetBar.getToolButton(forwardTool);

      if(back != null)
	{
	  back.removeMouseListener(backListener);
	  Tracer.debug("Successfully removed mouseListener");
	}
      if(forw != null)
	forw.removeMouseListener(forwardListener);
      toolSetBar.removeTool(backTool);
      toolSetBar.removeTool(forwardTool);
      
    }

  void updateTools()
    {
      if(history.getIndex() > 0)
	backTool.enable();
      else
	backTool.disable();
      
      if(history.getIndex() + 1 < history.getSize())
	forwardTool.enable();
      else
	forwardTool.disable();
    }
  
  
  public void back()
    {
      controlledJump(history.getIndex() - 1);
    }
  
  public void forward()
    {
      controlledJump(history.getIndex() + 1);
    }
  
  void popupBackMenu(Component c)
    {
      if(backMenu.isVisible())
	backMenu.setVisible(false);
      else
	{
	  backMenu.removeAll();
	  
	  String[] titles = history.getBackwardMapTitles();
	  
	  for(int i = 0; i < titles.length; i++)
	    {
	      JMenuItem item = backMenu.add(titles[i]);
	      final int index = history.getIndex() - 1 - i;
	      item.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		    {
		      controlledJump(index);
		    }
		});
	    }
	  
	  if(titles.length > 0)
	    backMenu.show(c, 0, c.getBounds().height);
	}
    }
  
  void popupForwardMenu(Component c)
    {
      if(forwardMenu.isVisible())
	forwardMenu.setVisible(false);
      else
	{
	  forwardMenu.removeAll();
	  
	  String[] titles = history.getForwardMapTitles();
	  
	  for(int i = 0; i < titles.length; i++)
	    {
	      JMenuItem item = forwardMenu.add(titles[i]);
	      final int index = history.getIndex() + 1 + i;
	      item.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		    {
		      controlledJump(index);
		    }
		});
	    }
	  
	  if(titles.length > 0)
	    forwardMenu.show(c, 0, c.getBounds().height);
	}
    }
  
  void controlledJump(int index)
    {
      URI map = history.getMapURI(index);
      
      if(map != null)
	{
	  try {
	    controller.showMap(map);
	    history.setIndex(index);
	    updateTools();
	  } catch(ControllerException e)
	    {
	      ErrorMessage.showError("Load Error",
				     "Failed to load map\n\n" + map,
				     e, controller.getMapPanel());
	    }
	}
    }
}
