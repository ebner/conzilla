/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.history;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.tool.ToolsBar;
import se.kth.cid.conzilla.util.ErrorMessage;

public class LinearHistoryManager
{
  LinearHistory history;
    HistoryListener listener;

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
    history = controller.getLinearHistory();

    backTool = new Tool("BACK", LinearHistoryManager.class.getName())
	{
	    public void actionPerformed(ActionEvent e)
	    {
		back();
	    }
	};
    backTool.setIcon(Images.getImageIcon(Images.ICON_NAVIGATION_BACK));
    backTool.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.ALT_MASK));

    forwardTool = new Tool("FORWARD", LinearHistoryManager.class.getName())
	{
	    public void actionPerformed(ActionEvent e)
	    {
		forward();
	    }
	};
    forwardTool.setIcon(Images.getImageIcon(Images.ICON_NAVIGATION_FORWARD));
    forwardTool.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.ALT_MASK));

    listener = new HistoryListener() {
	    public void historyEvent(HistoryEvent e)
	    {
		switch(e.getType())
		    {
		    case HistoryEvent.MAP:
			updateTools();
		    }
	    }
	};
    controller.getHistoryManager().addHistoryListener(listener);
    
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
    updateTools();

  }
  
  public void createTools(ToolsBar toolsBar)
    {
      AbstractButton but = toolsBar.addTool(backTool);
      but.addMouseListener(backListener);
      
      but = toolsBar.addTool(forwardTool);
      but.addMouseListener(forwardListener);
    }
  
  public void detachTools(ToolsBar toolsBar)
    {
      AbstractButton back = toolsBar.getToolButton(backTool);
      AbstractButton forw = toolsBar.getToolButton(forwardTool);

      if(back != null)
	  back.removeMouseListener(backListener);
      if(forw != null)
	  forw.removeMouseListener(forwardListener);

      toolsBar.removeTool(backTool);
      toolsBar.removeTool(forwardTool);
      
    }

  void updateTools()
    {
	backTool.setEnabled(history.getIndex() > 0);
      
	forwardTool.setEnabled(history.getIndex() + 1 < history.getSize());
    }
  
    public Tool getBackTool()
    {
	return backTool;
    }
  
    public Tool getForwardTool()
    {
	return forwardTool;
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
  
    public void controlledJump(int index)
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
				     e, controller.getView().getMapPanel());
	    }
	}
    }

    public void detach()
    {
	controller.getHistoryManager().removeHistoryListener(listener);
	backTool.detach();
	forwardTool.detach();
    }
}
