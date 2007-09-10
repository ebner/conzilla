/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.properties.ConzillaResourceManager;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;
import se.kth.cid.conzilla.tool.ToolsBar;


public class GridTool extends StateTool
{
  
    JPopupMenu gridMenu;
    MouseInputAdapter gridMenuListener;
    GridModel gridModel;
    JMenu gridLayoutMenu;
    JMenu gridSizeMenu;

 public GridTool(GridModel gridModel)
  {
      super("GRID", EditMapManagerFactory.class.getName(), true);
      setIcon(Images.getImageIcon(Images.ICON_GRID));

    this.gridModel=gridModel;

    ConzillaResourceManager menuManager = ConzillaResourceManager.getDefaultManager();

    gridMenu = new JPopupMenu();
    
    //Layouts of grid
    gridLayoutMenu=new JMenu();
    
    menuManager.customizeButton(gridLayoutMenu, EditMapManagerFactory.class.getName(), "STYLE");

    ButtonGroup layoutGroup=new ButtonGroup();
    JRadioButtonMenuItem layout;
    for (int i=0;i<gridModel.gridLayouts.length;i++)
	{
	    layout = new JRadioButtonMenuItem();

	    menuManager.customizeButton(layout, EditMapManagerFactory.class.getName(), gridModel.gridLayoutNames[i]);
	    if (gridModel.gridLayouts[i]==gridModel.getGridLayout())
		layout.setSelected(true);
	    final int place=i;
	    layout.addItemListener(new ItemListener()
		{
		    public void itemStateChanged(ItemEvent e)
		    {
			if(e.getStateChange() == ItemEvent.SELECTED)
			    GridTool.this.gridModel.setGridLayout(GridTool.this.gridModel.gridLayouts[place]);
		    }
		});
	    layoutGroup.add(layout);
	    gridLayoutMenu.add(layout);
	}

    gridMenu.add(gridLayoutMenu);
    
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
	
	gridLayoutMenu.setEnabled(b);
	gridSizeMenu.setEnabled(b);
    }

}
