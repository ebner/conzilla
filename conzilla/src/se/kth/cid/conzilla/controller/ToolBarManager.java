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


package se.kth.cid.conzilla.controller;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.map.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import java.beans.*;

/** This class maintains the toolbar of a SimpleController.
 *
 *  It contains the toolbars of the current MapManager,
 *  as well as tools belonging to the controller.
 *
 *  @author Mikael Nilsson
 *  @version $Revision: 1.1
 */
public class ToolBarManager extends ToolSetBar
{
  /** The combe box used to switch between the toolbars.
   */
  JComboBox toolBarBox;

  /** The current manager.
   */
  MapManager manager;

  /** The current toolbar.
   */
  LazyToolBar currentBar;

  class ToolBarBoxModel extends AbstractListModel implements ComboBoxModel
  {
    Object selected = null;
    boolean delaySelect = true;
    
    public ToolBarBoxModel()
    {
    }
    
    public Object getElementAt(int index)
    {
      return manager.getToolBars().elementAt(index);
    }

    public int getSize()
    {
      return manager.getToolBars().size();
    }

    public Object getSelectedItem()
    {
      return selected;
    }

    public void setSelectedItem(Object item)
    {
      Object oldItem = selected;
      selected = item;
      
      if(delaySelect)
	delaySelect = false;
      
      else if(item != currentBar)
	{
	  if(currentBar != null)
	    deselect(currentBar);
	  if(item != null)
	    select((LazyToolBar) item);
	}
    }
  }
  
  void select(LazyToolBar toolBar)
  {
    Tracer.debug("Selected: " + toolBar);

    currentBar = toolBar;
    toolBar.makeToolBar();
    
    ToolSetBar newToolBar = toolBar.getToolBar();

    manager.setActiveToolBar(toolBar.getName());
    
    newToolBar.setFloatable(false);
    newToolBar.setBorder(new EmptyBorder(0, 0, 0, 0));

    newToolBar.setOrientation(getOrientation());
    
    add(newToolBar);
    
    if(newToolBar.getDefaultTool() != null)
      newToolBar.getDefaultTool().activate();
    
    newToolBar.revalidate();
    repaint();
    pack();
  }

  void deselect(LazyToolBar toolBar)
  {
    Tracer.debug("Deselected: " + toolBar);
    if(toolBar.getToolBar() != null)
      remove(toolBar.getToolBar());
  }

  public ToolBarManager(String title)
  {
    super(title);
    toolBarBox = new JComboBox();
    Font f = toolBarBox.getFont();
    Tracer.debug("Font: " + f);
    toolBarBox.setFont(new Font(f.getName(), Font.PLAIN, 10));
    
    add(toolBarBox);

    addPropertyChangeListener(new PropertyChangeListener()
      {
	public void propertyChange(PropertyChangeEvent e)
	{
	  if(e.getPropertyName().equals("orientation"))
	    {
	      int neworient = ((Integer) e.getNewValue()).intValue();
	      if(currentBar != null)
		currentBar.getToolBar().setOrientation(neworient);
	    }
	}
      });
  }
  
  public void setManager(MapManager man)
  {
    manager = man;
    
    toolBarBox.setModel(new ToolBarBoxModel());

    toolBarBox.setMaximumSize(toolBarBox.getMinimumSize());
    
    int index = getToolBar(manager.getActiveToolBar());
    
    if(index == -1)
      toolBarBox.setSelectedIndex(0);
    else
      toolBarBox.setSelectedIndex(index);
  }
  
  int getToolBar(String toolBarName)
  {
    Vector toolBars = manager.getToolBars();
    for(int i = 0; i < toolBars.size(); i++)
      {
	LazyToolBar toolBar = (LazyToolBar) toolBars.elementAt(i);
	if(toolBar.getName().equals(toolBarName))
	  return i;
      }
    return -1;
  }
}
