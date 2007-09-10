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
import se.kth.cid.conzilla.library.GenericLibraryMenuWrapper;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.edit.layers.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.MenuLibraryListener;
import se.kth.cid.neuron.Neuron;
import se.kth.cid.component.MetaDataUtils;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;


/** Controlls the createlayer and which type that should be created next.
 *
 *  @author Matthias Palmer.
 */
public class CreateTool extends StateTool implements MenuLibraryListener
{
  
    JPopupMenu gridMenu;
    MouseInputAdapter createMenuListener;
    GridModel gridModel;
    JMenu typeMenu;
    EditMapManager edit;
    protected CreateLayer createLayer;
    boolean layerpushed=false;
    ConzillaKit kit;
    AbstractButton button;

 public CreateTool(MapController controller, EditMapManager edit, GridModel gridModel)
  {
      super("CREATE", EditMapManagerFactory.class.getName(), false);
      this.edit = edit;
      this.kit = controller.getConzillaKit();

      createLayer = new CreateLayer(controller, gridModel);

      //      setIcon(new ImageIcon(GridTool.class.getResource("/graphics/edit/Grid16.gif")));

      this.gridModel=gridModel;



    ConzillaResourceManager menuManager = ConzillaResourceManager.getDefaultManager();
    
    se.kth.cid.library.TemplateLibrary template = kit.getRootLibrary().getTemplateLibrary();
    GenericLibraryMenuWrapper glmw = new GenericLibraryMenuWrapper(kit.getComponentStore(), "No type selected", template, this);
    typeMenu = glmw.getMenu();

    menuManager.customizeButton(typeMenu, EditMapManagerFactory.class.getName(), "CREATE");


    createMenuListener = new MouseInputAdapter() {
	public void mousePressed(MouseEvent e)
	  {
	      //FIXME isPopupTrigger doesn't work on Windows (2000).
	      if(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) 
	      {
		  typeMenu.setSelected(true);
		  //		  typeMenu.setMenuLocation(e.getX(), e.getY());
		  //		  typeMenu.setPopupMenuVisible(true);
		  Tracer.debug("number of menuitems in type menu is: "+typeMenu.getItemCount());		  
		  typeMenu.getPopupMenu().show((Component) e.getSource(), e.getX(), e.getY());
		  e.consume();
	      }
	  }
      };
  }   

    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(StateTool.ACTIVATED))
	    activated(((Boolean) e.getNewValue()).booleanValue());
    }
    
  public void installYourself(ToolsBar toolSetBar)
    {
	Tracer.debug("installYourself."); 
      button = toolSetBar.addTool(this);
      button.addMouseListener(createMenuListener);      
    }
  
  public void removeYourself(ToolsBar toolSetBar)
    {
      AbstractButton but = toolSetBar.getToolButton(this);

      if(but != null)
	  but.removeMouseListener(createMenuListener);
      toolSetBar.removeTool(this);
    }

    
    void activated(boolean b)
    {
	//	gridModel.setGrid(b);
	typeMenu.setEnabled(b);

	if(b!=layerpushed)
	    {
		if (b)
		    edit.push(createLayer);
		else
		    edit.pop(createLayer);
		layerpushed = b;
	    }
    }
    public void selected(Neuron neuron)
	{
	    createLayer.selected(neuron);
	    String typeName = MetaDataUtils.getLocalizedString(neuron.getMetaData().get_metametadata_language(), 
							   neuron.getMetaData().get_general_title()).string;
	    if (typeName.equals(""))
		typeName = "[Unknown type name]";
	    
	    //	    button.setText(typeName);
	    putValue(NAME, typeName);
		
	    
	}
}
