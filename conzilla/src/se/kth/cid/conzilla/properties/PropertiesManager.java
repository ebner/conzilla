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


package se.kth.cid.conzilla.properties;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.install.Defaults;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.io.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/** 
 *  @author Matthias Palmèr
 *  @version $Revision$
 */
public class PropertiesManager
{
    //    public final static String SELECTOR_POPUP_BORDER_ACTIVE       = "SELECTOR_POPUP_BORDER_ACTIVE_COLOR";
    
    ColorManager colorManager;
    MenuManager menuManager;
    ToolBarManager toolBarManager;

    public static PropertiesManager defaultManager;
    
    public PropertiesManager(File propertiesPath) throws IOException
    {
	Tracer.debug("PropertiesManager");

	//FIXME... should be moved into installation part.
	if (!propertiesPath.isFile() && !propertiesPath.isDirectory())
	    if (!propertiesPath.mkdir())
		throw new IOException("Cannot create properties directory "+propertiesPath+"!");
	
	colorManager = new ColorManager(propertiesPath);
	//FIXME: default in ColorManager isn't neccessary.
	ColorManager.setDefaultColorManager(colorManager);
	ColorManager.internationalize("properties.internationalization.ColorNames");

	menuManager = new MenuManager(propertiesPath);
	MenuManager.internationalize("properties.internationalization.MenuProperties");
	
	toolBarManager = new ToolBarManager(propertiesPath);
	toolBarManager.internationalize("properties.internationalization.ToolBarProperties");
    }

    public ColorManager getColorManager()
    {
	return colorManager;
    }
    
    public MenuManager getMenuManager()
    {
	return menuManager;
    }

    public ToolBarManager getToolBarManager()
    {
	return toolBarManager;
    }

    public static void setDefaultPropertiesManager(PropertiesManager manager)
    {
	defaultManager=manager;
    }
    public static PropertiesManager getDefaultPropertiesManager()
    {
	return defaultManager;
    }
}
