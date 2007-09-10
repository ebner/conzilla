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


package se.kth.cid.conzilla.edit;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;


public class GridTool extends StateTool
{
  
    JPopupMenu gridMenu;
    MouseInputAdapter gridMenuListener;
    GridModel gridModel;
    JMenu gridStyleMenu;
    JMenu gridSizeMenu;

 public GridTool(GridModel gridModel)
  {
      super("GRID", EditMapManagerFactory.class.getName(), true);
      setIcon(new ImageIcon(GridTool.class.getResource("/graphics/edit/Grid16.gif")));

    this.gridModel=gridModel;

    ConzillaResourceManager menuManager = ConzillaResourceManager.getDefaultManager();

    gridMenu = new JPopupMenu();
    
    //Styles of grid
    gridStyleMenu=new JMenu();
    
    menuManager.customizeButton(gridStyleMenu, EditMapManagerFactory.class.getName(), "STYLE");

    ButtonGroup styleGroup=new ButtonGroup();
    JRadioButtonMenuItem style;
    for (int i=0;i<gridModel.gridStyles.length;i++)
	{
	    style = new JRadioButtonMenuItem();

	    menuManager.customizeButton(style, EditMapManagerFactory.class.getName(), gridModel.gridStyleNames[i]);
	    if (gridModel.gridStyles[i]==gridModel.getGridStyle())
		style.setSelected(true);
	    final int place=i;
	    style.addItemListener(new ItemListener()
		{
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    GridTool.this.gridModel.setGridStyle(GridTool.this.gridModel.gridStyles[place]);
		    }
		});
	    styleGroup.add(style);
	    gridStyleMenu.add(style);
	}

    gridMenu.add(gridStyleMenu);
    
    //Sizes of grid
    gridSizeMenu=new JMenu();

    menuManager.customizeButton(gridSizeMenu, EditMapManagerFactory.class.getName(), "SIZE");

    ButtonGroup buttonGroup=new ButtonGroup();
    JRadioButtonMenuItem gran;
    for (int i=0;i<gridModel.gridSizes.length;i++)
	{
	    gran = new JRadioButtonMenuItem("" + gridModel.gridSizes[i]);
	    if (gridModel.gridSizes[i]==gridModel.getGranularity())
		gran.setSelected(true);
	    final int place=i;
	    gran.addItemListener(new ItemListener()
		{
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    {
				GridTool.this.gridModel.setGranularity(GridTool.this.gridModel.gridSizes[place]);
			    }
		    }
		});
	    buttonGroup.add(gran);
	    gridSizeMenu.add(gran);
	}
    gridMenu.add(gridSizeMenu);

    gridMenuListener = new MouseInputAdapter() {
	public void mousePressed(MouseEvent e)
	  {
	      //FIXME isPopupTrigger doesn't work on Windows (2000).
	      if(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) 
	      {
		gridMenu.show((Component) e.getSource(), e.getX(), e.getY());
		e.consume();
	      }
	  }
      };

    setActivated(gridModel.isGridOn());
  }   

    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(StateTool.ACTIVATED))
	    activated(((Boolean) e.getNewValue()).booleanValue());
    }
    
  public void installYourself(ToolsBar toolSetBar)
    {
      AbstractButton but = toolSetBar.addTool(this);
      but.addMouseListener(gridMenuListener);      
    }
  
  public void removeYourself(ToolsBar toolSetBar)
    {
      AbstractButton but = toolSetBar.getToolButton(this);

      if(but != null)
	  but.removeMouseListener(gridMenuListener);
      toolSetBar.removeTool(this);
    }

    
    void activated(boolean b)
    {
	gridModel.setGrid(b);
	
	gridStyleMenu.setEnabled(b);
	gridSizeMenu.setEnabled(b);
    }

}
