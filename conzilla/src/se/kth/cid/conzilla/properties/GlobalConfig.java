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
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.util.*;
import java.io.*;
import java.net.MalformedURLException;
import java.awt.*;
import java.util.*;
import java.beans.*;

public class GlobalConfig extends Properties
{
    
    PropertyChangeSupport pcs;

    HashMap colorCache;

    Vector colorSets;

    static GlobalConfig globalConfig;    

    Properties defaults;
    
    GlobalConfig(Properties defaults)
    {
	super(defaults);
	this.defaults = defaults;
	pcs = new PropertyChangeSupport(this);
	colorCache = new HashMap();

	colorSets = new Vector();
    }

    public static GlobalConfig getGlobalConfig()
    {
	if(globalConfig == null)
	    globalConfig = new GlobalConfig(new Properties());
	
	return globalConfig;
    }
    
    public Properties getDefaults()
    {
	return defaults;
    }
    
    public void addDefaults(Properties map)
    {
	defaults.putAll(map);
    }

    public void addDefaults(Class propClass)
    {
	String classname = propClass.getName();

	int dotindex = classname.lastIndexOf('.');
	if(dotindex > 0)
	    classname = classname.substring(dotindex + 1);

	Properties props = new Properties();
	try{
	    InputStream is = propClass.getResourceAsStream(classname + ".defaults");
	    props.load(is);
	    addDefaults(props);
	    is.close();
	} catch(IOException e)
	    {
		Tracer.trace("Could not load defaults for " + propClass.getName() + "\n " + e.getMessage(), 
			     Tracer.WARNING);
	    }
    }
    
  public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
	pcs.addPropertyChangeListener(prop, pcl);
    }
    
  public void removePropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
	pcs.removePropertyChangeListener(prop, pcl);
    }

  public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
	pcs.addPropertyChangeListener(pcl);
    }
    
  public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
	pcs.removePropertyChangeListener(pcl);
    }

  public Object setProperty(String key, String newValue)
    {
	String oldValue = getProperty(key);
	super.setProperty(key, newValue);
	if(!newValue.equals(oldValue))
	    pcs.firePropertyChange(key, oldValue, newValue);
	return newValue;
    }

  public void loadConfig(InputStream s) throws IOException
    {
	clear();
	
	load(s);
    }
    
    public void store(OutputStream s) throws IOException
    {
	super.store(s, " Conzilla Config file");
    }

    public Object remove(Object key)
    {
	Object oldp = super.remove(key);
	if(oldp != null)
	    pcs.firePropertyChange((String) key, (String) oldp, getProperty((String)key));
	return oldp;
    }

  public java.net.URL getURL(String key) throws MalformedURLException
    {
	String p = getProperty(key);
	if(p == null)
	    return null;
	
	return new java.net.URL(p);
    }
  public URI getURI(String key) throws MalformedURIException
    {
	String p = getProperty(key);
	if(p == null)
	    return null;
	
	return URIClassifier.parseURI(p);
    }
    
    public Color getColor(String key)
    {
	Color col = (Color) colorCache.get(key);
	if(col != null)
	    return col;

	if(colorCache.containsKey(key))
	    return null;

	String value = getProperty(key);

	if(value != null)
	    {
		try {
		    if (!value.startsWith("0x"))
			col = Color.decode(value);
		    else
			{
			    int rgb = Long.decode(value).intValue();
			    col = new Color(rgb);
			}
		} catch (NumberFormatException nfe)
		    {
		    }
	    }

	colorCache.put(key, col);

	return col;
    }
    
    public void setColor(String key, Color col)
    {
	if(col != null)
	    {
		colorCache.put(key, col);
		setProperty(key, "0x" + Integer.toHexString(col.getRGB()));
	    }
	else
	    {
		colorCache.remove(key);
		remove(key);
	    }
    }


    public static class ColorSet
    {
	public String nameProperty;
	public String[] colorProperties;
	public String resourceBundle;
	
	public ColorSet(String nameprop, String[] colorProps, String resourceBundle)
	{
	    this.nameProperty = nameprop;
	    this.colorProperties = colorProps;
	    this.resourceBundle = resourceBundle;
	}
    }
    
    public void registerColorSet(String nameprop, String[] colorprops, String resourceBundle)
    {
	colorSets.add(new ColorSet(nameprop, colorprops, resourceBundle));
    }

    public ColorSet[] getColorSets()
    {
	return (ColorSet[]) colorSets.toArray(new ColorSet[colorSets.size()]);
    }

    public Enumeration keys()
    {
	String [] keys = (String[]) keySet().toArray(new String[size()]);
	Arrays.sort(keys);
	final String[] nkeys = keys;
	return new Enumeration()
	    {
		int index = 0;
		public boolean hasMoreElements()
		{
		    return index < nkeys.length;
		}
		public Object nextElement()
		{
		    return nkeys[index++];
		}
	    };
    }
}
