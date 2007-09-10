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


package se.kth.cid.conzilla.install;


import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.util.*;
import java.io.*;
import java.util.*;
import java.beans.*;

public class ConzillaConfig extends Properties
{
  public static final String CONFIG_FILE      = "conzilla.cnf";
  public static final String CONZILLA_VERSION = "1.0";
  
  public final static String PROPERTY_STARTMAP="property.startmap";

  boolean editable=false;
  PropertyChangeSupport pcs;

  static File                configFilePath;

  static URI                 configFileURI;  

  URI globalResolver;
  URI localResolver;
  URI rootLibrary;

  String version;
    
  static String userDir = System.getProperty("user.home");
  static String conzillaDir;

  static
    {
      
      if(File.separatorChar == '/') // UNIX etc.
	conzillaDir = ".conzilla";
      else                          // Others
	conzillaDir = "Conzilla";
      
      configFilePath = new File(userDir + File.separatorChar +
				conzillaDir + File.separatorChar +
				CONFIG_FILE);
      try {
	configFileURI = FileURL.getFileURL(configFilePath.toString());
      } catch(MalformedURIException e)
	{
	  Tracer.error("Could not make URI " + e.getURI());
	}
      Tracer.debug(configFilePath + ", " + configFileURI);
    }
  
  public ConzillaConfig(boolean editable)
    {
	this.editable=editable;
	pcs=new PropertyChangeSupport(this);
    }
 
  public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
	pcs.addPropertyChangeListener(prop, pcl);
    }
    
  public void removePropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
	pcs.removePropertyChangeListener(prop, pcl);
    }

  
  public boolean isPropertiesEditable()
    {
	return editable;
    }
    
  public Object setProperty(String key, String newValue)
    {
	if (!editable)
	    return null;

	String oldValue=getProperty(key);
	Object o=super.setProperty(key, newValue);
	pcs.firePropertyChange(key, oldValue, newValue);
	return o;
    }

  public String getProperty(String key, String value)
    {
	//Reason?????
	//	if (!editable)
	//	    return null;
	return super.getProperty(key, value);
    }

  public static URI getConfigURI()
    {
      return configFileURI;
    }

  public static File getConzillaDir()
    {
      return getConfigFile().getParentFile();
    }
  
  public static File getConfigFile()
    {
      return configFilePath;
    }

    public static File getPropertiesDir()
    {
	return new File(getConzillaDir(), Installer.PROPERTIES_DIR);
    }

  public void load() throws IOException, ConfigException, MalformedURIException
    {
      FileInputStream cfg = new FileInputStream(configFilePath);
      load(cfg);
      cfg.close();

      String prop = getProperty("resolver.global");
      if(prop != null)
	globalResolver = URIClassifier.parseURI(prop);
      else
	throw new ConfigException("Property not found: resolver.global");

      prop = getProperty("resolver.local");
      if(prop != null)
	localResolver = URIClassifier.parseURI(prop);
      else
	throw new ConfigException("Property not found: resolver.local");

      prop = getProperty("library.root");
      if(prop != null)
	rootLibrary = URIClassifier.parseURI(prop);
      else
	throw new ConfigException("Property not found: library.root");

      prop = getProperty("conzilla.version");
      if(prop != null)
	{
	  version = prop;
	  if(!version.equals(CONZILLA_VERSION))
	    throw new ConfigException("Unsupported version: " + version);  
	}
      else
	throw new ConfigException("Property not found: conzilla.version");

    }

  public void store() throws IOException
    {
      FileOutputStream cfg = new FileOutputStream(configFilePath);
      store(cfg, " Conzilla Config file");
      cfg.close();
    }

  public URI getRootLibrary()
    {
      return rootLibrary;
    }

  public URI getGlobalResolver()
    {
      return globalResolver;
    }

  public URI getLocalResolver()
    {
      return localResolver;
    }

  public String getVersion()
    {
      return version;
    }

  public void setVersion(String version)
    {
      this.version = version;
      setProperty("conzilla.version", version);
    }

  public void setGlobalResolver(URI uri)
    {
      globalResolver = uri;
      setProperty("resolver.global", uri.toString());
    }

  public void setLocalResolver(URI uri)
    {
      localResolver = uri;
      setProperty("resolver.local", uri.toString());
    }

  public void setRootLibrary(URI uri)
    {
      rootLibrary = uri;
      setProperty("library.root", uri.toString());
    }
  
}
