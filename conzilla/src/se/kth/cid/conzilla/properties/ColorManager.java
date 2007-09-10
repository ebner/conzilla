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
public class ColorManager extends Properties
{
    public final static String MAP_BACKGROUND    = "MAP_BACKGROUND_COLOR";
    public static Color MAP_BACKGROUND_DEFAULT   = new Color(230, 255, 230);

    public final static String MAP_FOREGROUND    = "MAP_FOREGROUND_COLOR";
    public static Color MAP_FOREGROUND_DEFAULT   = Color.black;

    public final static String MAP_NEURON_BACKGROUND    = "MAP_NEURON_BACKGROUND_COLOR";
    public static Color MAP_NEURON_BACKGROUND_DEFAULT   = new Color(255, 255, 255);

    public final static String MAP_TEXT          = "MAP_TEXT_COLOR";
    public static Color MAP_TEXT_DEFAULT         = Color.black;

    public final static String MAP_POPUP_BACKGROUND    = "MAP_POPUP_BACKGROUND_COLOR";
    public static Color MAP_POPUP_BACKGROUND_DEFAULT   = new Color(242,239,242);

    public final static String MAP_POPUP_TEXT    = "MAP_POPUP_TEXT_COLOR";
    public static Color MAP_POPUP_TEXT_DEFAULT   = Color.black;    
    
    public final static String MAP_POPUP_BORDER    = "MAP_POPUP_BORDER_COLOR";
    public static Color MAP_POPUP_BORDER_DEFAULT   = Color.black;    

    public final static String MAP_POPUP_BACKGROUND_ACTIVE    = "MAP_POPUP_BACKGROUND_ACTIVE_COLOR";
    public static Color MAP_POPUP_BACKGROUND_ACTIVE_DEFAULT   = new Color(228, 228, 255);

    public final static String MAP_POPUP_TEXT_ACTIVE    = "MAP_POPUP_TEXT_ACTIVE_COLOR";
    public static Color MAP_POPUP_TEXT_ACTIVE_DEFAULT   = Color.black;    
    
    public final static String MAP_POPUP_BORDER_ACTIVE    = "MAP_POPUP_BORDER_ACTIVE_COLOR";
    public static Color MAP_POPUP_BORDER_ACTIVE_DEFAULT   = Color.black;    

    public final static String MAP_NEURON_ERROR    = "MAP_NEURON_ERROR_COLOR";
    public static Color MAP_NEURON_ERROR_DEFAULT         = Color.red;

    public final static String MAP_MOUSE_OVER_BOX    = "MAP_MOUSE_OVER_BOX_COLOR"; 
    public static Color MAP_MOUSE_OVER_BOX_DEFAULT   = new Color(153, 153, 255);

    public final static String MAP_CONTENT_FROM_BOX  = "MAP_CONTENT_FROM_BOX_COLOR";
    public static Color MAP_CONTENT_FROM_BOX_DEFAULT = new Color(255, 255, 111);

    public final static String EDIT_CHOICES      = "EDIT_CHOICES_COLOR";
    public static Color EDIT_CHOICES_DEFAULT     = new Color(0, 0, 152);

    public final static String EDIT_GRID         = "EDIT_GRID_COLOR";
    public static Color EDIT_GRID_DEFAULT        = Color.lightGray;

    public final static String SELECTOR_BACKGROUND         = "SELECTOR_BACKGROUND_COLOR";
    public static Color SELECTOR_BACKGROUND_DEFAULT        = new Color(255, 255, 210);

    public final static String SELECTOR_TEXT         = "SELECTOR_TEXT_COLOR";
    public static Color SELECTOR_TEXT_DEFAULT        = Color.black;

    public final static String SELECTOR_SELECTION_BACKGROUND         = "SELECTOR_SELECTION_BACKGROUND_COLOR";
    public static Color SELECTOR_SELECTION_BACKGROUND_DEFAULT        = new Color(204, 204, 255);

    public final static String SELECTOR_SELECTION_TEXT         = "SELECTOR_SELECTION_TEXT_COLOR";
    public static Color SELECTOR_SELECTION_TEXT_DEFAULT        = Color.black;

    public final static String SELECTOR_LEAF_ASPECT            = "SELECTOR_LEAF_ASPECT_COLOR";
    public static Color SELECTOR_LEAF_ASPECT_DEFAULT        = Color.black;

    public final static String SELECTOR_POPUP_BACKGROUND         = "SELECTOR_POPUP_BACKGROUND_COLOR";
    public static Color SELECTOR_POPUP_BACKGROUND_DEFAULT        = new Color(255, 255, 210);

    public final static String SELECTOR_POPUP_BACKGROUND_ACTIVE  = "SELECTOR_POPUP_BACKGROUND_ACTIVE_COLOR";
    public static Color SELECTOR_POPUP_BACKGROUND_ACTIVE_DEFAULT = new Color(205, 205, 255);
    
    public final static String SELECTOR_POPUP_TEXT         = "SELECTOR_POPUP_TEXT_COLOR";
    public static Color SELECTOR_POPUP_TEXT_DEFAULT        = Color.black;

    public final static String SELECTOR_POPUP_TEXT_ACTIVE  = "SELECTOR_POPUP_TEXT_ACTIVE_COLOR";
    public static Color SELECTOR_POPUP_TEXT_ACTIVE_DEFAULT = Color.black;
    
    public final static String SELECTOR_POPUP_BORDER              = "SELECTOR_POPUP_BORDER_COLOR";
    public static Color SELECTOR_POPUP_BORDER_DEFAULT             = Color.black;

    public final static String SELECTOR_POPUP_BORDER_ACTIVE       = "SELECTOR_POPUP_BORDER_ACTIVE_COLOR";
    public static Color SELECTOR_POPUP_BORDER_ACTIVE_DEFAULT      = Color.black;
    



    public final static String [] colorNameArrMap = {MAP_BACKGROUND,
						     MAP_FOREGROUND,
						     MAP_NEURON_BACKGROUND,
						     MAP_TEXT,
						     MAP_POPUP_BACKGROUND,
						     MAP_POPUP_TEXT,
						     MAP_POPUP_BORDER,
						     MAP_POPUP_BACKGROUND_ACTIVE,
						     MAP_POPUP_TEXT_ACTIVE,
						     MAP_POPUP_BORDER_ACTIVE,
						     MAP_NEURON_ERROR,
						     MAP_MOUSE_OVER_BOX,
						     MAP_CONTENT_FROM_BOX};

    public final static String [] colorNameArrEdit ={EDIT_CHOICES,
						     EDIT_GRID};

    public final static String [] colorNameArrSelector = {SELECTOR_BACKGROUND, 
							  SELECTOR_TEXT, 
							  SELECTOR_SELECTION_BACKGROUND, 
							  SELECTOR_SELECTION_TEXT, 
							  SELECTOR_POPUP_BACKGROUND,
							  SELECTOR_POPUP_BACKGROUND_ACTIVE,
							  SELECTOR_POPUP_TEXT,
							  SELECTOR_POPUP_TEXT_ACTIVE,
							  SELECTOR_POPUP_BORDER,
							  SELECTOR_POPUP_BORDER_ACTIVE,
							  SELECTOR_LEAF_ASPECT};

    public final static String [] [] colorMenu = {colorNameArrMap,
							 colorNameArrEdit,
							 colorNameArrSelector};

    public final static String [] colorMenuNames = {"MAP_COLORS_BROWSE_MODE",
						    "MAP_COLORS_EDIT_MODE",
						    "SELECTOR_COLORS"};
    
    static ColorManager defaultManager;
    /*    static {
	defaultManager=new ColorManager();
	}*/
	
    Hashtable colors;
    PropertyChangeSupport pcs;

    static ResourceBundle colorTitles;    
    File colorFilePath;
    final static String COLOR_CONFIG_FILE="colorproperties"; 

  public static boolean allowTranslucency = true;
    
    public ColorManager(File propertiesPath) throws IOException
    {
	Tracer.debug("initialising the colorManager");
	colors = new Hashtable();
	pcs = new PropertyChangeSupport(this);
	resetColors();
	
	colorFilePath = new File(propertiesPath, COLOR_CONFIG_FILE);
		    
	loadColors();
    }

    static public void internationalize(String resource)
    {
	colorTitles = ResourceBundle.getBundle(resource, Locale.getDefault());
    }

    public void resetColors()
    {
	setColor(MAP_FOREGROUND, MAP_FOREGROUND_DEFAULT);
	setColor(MAP_BACKGROUND, MAP_BACKGROUND_DEFAULT);
	setColor(MAP_NEURON_BACKGROUND, MAP_NEURON_BACKGROUND_DEFAULT);
	setColor(MAP_TEXT, MAP_TEXT_DEFAULT);
	setColor(MAP_POPUP_BACKGROUND, MAP_POPUP_BACKGROUND_DEFAULT);
	setColor(MAP_POPUP_BORDER, MAP_POPUP_BORDER_DEFAULT);
	setColor(MAP_POPUP_BACKGROUND_ACTIVE, MAP_POPUP_BACKGROUND_ACTIVE_DEFAULT);
	setColor(MAP_POPUP_TEXT, MAP_POPUP_TEXT_DEFAULT);
	setColor(MAP_POPUP_TEXT_ACTIVE, MAP_POPUP_TEXT_ACTIVE_DEFAULT);
	setColor(MAP_POPUP_BORDER_ACTIVE, MAP_POPUP_BORDER_ACTIVE_DEFAULT);
	setColor(MAP_NEURON_ERROR, MAP_NEURON_ERROR_DEFAULT);

	setColor(MAP_MOUSE_OVER_BOX, MAP_MOUSE_OVER_BOX_DEFAULT);
	setColor(MAP_CONTENT_FROM_BOX, MAP_CONTENT_FROM_BOX_DEFAULT);
	setColor(EDIT_CHOICES, EDIT_CHOICES_DEFAULT);
	setColor(EDIT_GRID, EDIT_GRID_DEFAULT);

	setColor(SELECTOR_BACKGROUND, SELECTOR_BACKGROUND_DEFAULT);
	setColor(SELECTOR_TEXT, SELECTOR_TEXT_DEFAULT);
	setColor(SELECTOR_SELECTION_BACKGROUND, SELECTOR_SELECTION_BACKGROUND_DEFAULT);
	setColor(SELECTOR_SELECTION_TEXT, SELECTOR_SELECTION_TEXT_DEFAULT);
	setColor(SELECTOR_POPUP_BACKGROUND, SELECTOR_POPUP_BACKGROUND_DEFAULT);
	setColor(SELECTOR_POPUP_BACKGROUND_ACTIVE, SELECTOR_POPUP_BACKGROUND_ACTIVE_DEFAULT);
	setColor(SELECTOR_POPUP_TEXT, SELECTOR_POPUP_TEXT_DEFAULT);
	setColor(SELECTOR_POPUP_TEXT_ACTIVE, SELECTOR_POPUP_TEXT_ACTIVE_DEFAULT);
	setColor(SELECTOR_POPUP_BORDER, SELECTOR_POPUP_BORDER_DEFAULT);
	setColor(SELECTOR_POPUP_BORDER_ACTIVE, SELECTOR_POPUP_BORDER_ACTIVE_DEFAULT);
	setColor(SELECTOR_LEAF_ASPECT, SELECTOR_LEAF_ASPECT_DEFAULT);
    }

    public boolean loadColors() throws IOException
    {
	FileInputStream cfp = null;
	try {
	     cfp = new FileInputStream(colorFilePath);
	} catch (FileNotFoundException fnfe)
	    {
		Tracer.debug("ColorManager: file not found. "+colorFilePath);
		return false;
	    }
	load(cfp);
	cfp.close();
	
	updateMirror();
	return true;
    }

    public boolean saveColors()
    {
	try {
	    FileOutputStream cfs = new FileOutputStream(colorFilePath);
	    store(cfs, "Conzilla color properties");
	    cfs.close();
	    return true;
	} catch (IOException io)
	    {
		return false;
	    }
    }

    
    private void updateMirror()
    {
	Enumeration tags=keys();
	Enumeration values=elements();
	for (;tags.hasMoreElements();)
	    {
		String tag=(String) tags.nextElement();
		String value=(String) values.nextElement();
		Color col;
		try {
		    if (!value.startsWith("0x"))
			col = Color.decode(value);
		    else
			{
			    int rgb=Long.decode(value).intValue();
			    col = new Color(rgb);
			}
		} catch (NumberFormatException nfe)
		    {
			col = getColor(tag);
		    }
		if (!col.equals(getColor(tag)))
		    setColor(tag, col);
	    }
    }
	    

    public static ColorManager getDefaultColorManager()
    {
	return defaultManager;
    }

    public static void setDefaultColorManager(ColorManager cman)
    {
	defaultManager = cman;
    }

    public JMenu getColorMenu()
    {
	MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();

	JMenu menu=new JMenu("Color settings");

	for (int j=0;j<colorMenu.length; j++)
	    {
		final int menuindex = j;
		JMenu submenu = new JMenu(colorMenuNames[menuindex]);
		
		for (int i=0;i<colorMenu[menuindex].length;i++)
		    {
			final int nr = i;
			final String colorname = getInternationalizedColorName(colorMenu[menuindex][nr]);
			menuManager.customizeButton(submenu.add(new AbstractAction(colorname) {
				public void actionPerformed(ActionEvent e)
				{
				    Color c=JColorChooser.showDialog(null, colorname + " color", getColor(colorMenu[menuindex][nr]));
				    if (c!=null)
					setColor(colorMenu[menuindex][nr], c);				    
				}
			    }));			
		    }
		menuManager.customizeButton(menu.add(submenu), colorMenuNames[menuindex]);
	    }
	
	JMenuItem mi=menu.add(new AbstractAction("Reset to default")
	    {
		public void actionPerformed(ActionEvent e)
		{
		    ColorManager.this.resetColors();
		}
	    });
	menuManager.customizeButton(mi, "RESET_TO_DEFAULT");

	mi=menu.add(new AbstractAction("Reset to saved")
	    {
		public void actionPerformed(ActionEvent e)
		{
		    try {
			ColorManager.this.loadColors();
		    } catch (IOException io)
			{
			    Tracer.debug("Failed reloading saved color properties."+io.getMessage());
			}
		}
	    });
	menuManager.customizeButton(mi, "RESET_TO_SAVED");

	mi=menu.add(new AbstractAction("Save color settings")
	    {
		public void actionPerformed(ActionEvent e)
		{
		    ColorManager.this.saveColors();
		}
	    });
	menuManager.customizeButton(mi, "SAVE_COLOR_SETTINGS");

	return menu;
    }

    public static String getInternationalizedColorName(String property)
    {
	String name = property;
	try {
	    if (colorTitles!=null)
		{
		    name = colorTitles.getString(property);
		    return name;
		}
	} catch (MissingResourceException e)
	    {}
	return property;
    }

    public void setColor(String tag, Color newColor)
    {
	Color oldColor=(Color) colors.remove(tag);
	colors.put(tag, newColor);
	setProperty(tag, "0x"+Integer.toHexString(newColor.getRGB()));
	pcs.firePropertyChange(tag, oldColor, newColor);
    }
    
    public Color getColor(String tag)
    {
	return (Color) colors.get(tag);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
	if (prop==null)
	    pcs.addPropertyChangeListener(pcl);
	else
	    pcs.addPropertyChangeListener(prop, pcl);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
	if (prop==null)
	    pcs.removePropertyChangeListener(pcl);	
	else
	    pcs.removePropertyChangeListener(prop, pcl);
    }

    public static Color getLighterColor(Color col)
    {
	int red=col.getRed();
	red += (255 - red)/4 * 3;
	int green=col.getGreen();
	green += (255 - green) /4 * 3;
	int blue=col.getBlue();
	blue += (255 - blue) /4 * 3;
	return new Color(red, green, blue);
    }
    
    public static Color getTranslucentColor(Color col)
    {
	int red=col.getRed();
	int green=col.getGreen();
	int blue=col.getBlue();
	if(allowTranslucency)
	  return new Color(red, green, blue, 200);
	else
	  return new Color(red, green, blue);
    }
}
