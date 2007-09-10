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
public class MenuManager
{
    protected static ResourceBundle menuResources;
    protected Font mapMenuFont;

    public MenuManager(File propertiesPath) throws IOException
    {
	Tracer.debug("MenuManager");
	mapMenuFont=new Font("Lucida Sans", Font.BOLD, 10);
	Tracer.debug("Font is:"+mapMenuFont.toString());
    }

    public static void internationalize(String resource)
    {
	menuResources = ResourceBundle.getBundle(resource, Locale.getDefault());
    }

    public String getString(String prop)
    {
      try {
	  return menuResources.getString(prop);
      }catch (MissingResourceException e)
	  {
	      return null;
	  }
    }
    
    public void customizeButton(AbstractButton but, String prop)
    {
	try {
	    but.setText(menuResources.getString(prop));
	    but.setToolTipText(menuResources.getString(prop+"_TOOL_TIP"));
	    //	    but.setFont(mapMenuFont);
	}catch (MissingResourceException e)
	    {}
    }
    public void customizeButton(Component but)
    {
      //but.setFont(mapMenuFont);
    }

    public Font getMapMenuFont()
    {
	return mapMenuFont;
    }
}
