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


package se.kth.cid.conzilla.app;

import se.kth.cid.library.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.install.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.component.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.identity.*;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

/** This class represents the information specific to Conzilla when run
 *  as an installed application. Thus, it will install Conzilla files
 *  locally, and use information stored there. It does _not_, however,
 *  assume that Conzilla is run as a stand-alone app. Conzilla can be
 *  run as an applet with local installation this way too.
 */

public abstract class ConzillaAppEnv implements ConzillaEnvironment
{
    public static final String LOCAL_RESOLVER_PROP = "conzilla.resolver.local";
    public static final String GLOBAL_RESOLVER_PROP = "conzilla.resolver.global";
    public static final String CONZILLA_CONFIG_VERSION = "1.1";
    public static final String CONZILLA_VERSION_PROP = "conzilla.version";

    
    ContentDisplayer defaultContentDisplayer;
    
    ResolverManager resolverManager;
      

    public ConzillaKit kit;

    String parammap;

    static class InstallException extends IOException
    {
    }
    

    public ConzillaAppEnv()
    {
    }
       
    public ContentDisplayer getDefaultContentDisplayer()
    {
	return defaultContentDisplayer;
    }
    
    public ResolverManager getResolverManager()
    {
	return resolverManager;
    }
      

    protected void start(String parammap, URI specifiedConfigURI)
    {
	this.parammap = parammap;
	try {
	    tryInit(specifiedConfigURI);
	} catch(InstallException e)
	    {
		needInstall("Conzilla does not seem to be installed\n" +
			    "on your account.\n");
	    }
	catch(IOException e)
	    {
		initError("Problem loading conzilla.\n", e);
	    }
	catch(MalformedURIException e)
	    {
		initError("Invalid URI\n\n" + e.getURI() +
			  "\n\nin config file.", e);
	    }
	catch(ResolveException e)
	    {
		initError("Invalid resolver file.", e);
	    }
	catch(LibraryException e)
	    {
		initError("Invalid root library.", e);
	    }      
    }
    
    void loadConfig() throws IOException
    {
    }
    

    void tryInit(URI specifiedConfigURI) throws MalformedURIException, IOException, LibraryException, ResolveException, InstallException
    {
	GlobalConfig config = GlobalConfig.getGlobalConfig();
	try {
	    URI configURI;
	    if (specifiedConfigURI != null)
		configURI = specifiedConfigURI;
	    else
		configURI = Installer.getConfigURI();
	    
	    InputStream is = configURI.getJavaURL().openConnection().getInputStream();
	    config.loadConfig(is);
	    is.close();
	} catch(IOException e)
	    {
		throw new InstallException();
	    }
	

	String prop = config.getProperty(CONZILLA_VERSION_PROP);
	if(prop != null)
	    {
		if(!prop.equals(CONZILLA_CONFIG_VERSION))
		    throw new IOException("Unsupported config file version: " + prop);  
	    }
	else
	    throw new IOException("Property not found: " + CONZILLA_VERSION_PROP);


	resolverManager = new ResolverManager();      
	
	try {
	    URI resolvURI = config.getURI(LOCAL_RESOLVER_PROP);
	    if (resolvURI!=null)
		resolverManager.addTable(new ResolverTable(resolvURI));
	    resolvURI = config.getURI(GLOBAL_RESOLVER_PROP);
	    if (resolvURI!=null)
		resolverManager.addTable(new ResolverTable(resolvURI));
	} catch (ResolveException e)
	    {
		Tracer.trace("Ignoring resolver file: " + config.getURI(GLOBAL_RESOLVER_PROP)
			     + ", as:\n " + e.getMessage(),
			     Tracer.WARNING);
	    }
	
	initDefaultContentDisplayer(resolverManager.getResolver());


	kit = new ConzillaKit(this);
	
	Vector startMaps = new Vector();
	
	URI startMap;
	
	if(parammap != null)
	    {
		try {
		    startMap = URIClassifier.parseURI(parammap);
		    startMaps.add(startMap);
		} catch(MalformedURIException e)
		    {
			ErrorMessage.showError("Invalid map URI",
					       "Invalid start map URI:\n"
					       + parammap + ":\n " +
					       e.getMessage(), e, null);
		    }
	    }

	String strStartMap = GlobalConfig.getGlobalConfig().getProperty(STARTMAP_PROP);
	if (strStartMap != null)
	    {
		try {
		    startMap = URIClassifier.parseURI(strStartMap);
		    startMaps.add(startMap);
		}
		catch (MalformedURIException me)
		    {
			ErrorMessage.showError("Invalid map URI",
					       "Invalid start map URI:\n"
					       + strStartMap + ":\n " +
					       me.getMessage(), me, null); 
		    }
	    }

	startMaps.add(URIClassifier.parseValidURI(DEFAULT_STARTMAP));
	int i;
	for(i = 0; i < startMaps.size(); i++)
	    {
		try {
		    kit.getConzilla().openMapInNewView((URI) startMaps.get(i), null);
		} catch(ControllerException e)
		    {
			continue;
		    }
		return;
	    }
	ErrorMessage.showError("No valid maps",
			       "No valid maps could be found.\n\nGiving up.", null, null); 
	exit(1);	
    }
    
    protected abstract void initDefaultContentDisplayer(PathURNResolver resolver);
    
    public void exit(int result)
    {
	try {
	    Installer.saveConfig();
	} catch(IOException e)
	    {
		Tracer.trace("IO Error saving config: " + e.getMessage(), Tracer.WARNING);
	    }
	System.exit(result);
    }
    
    
    void initError(String error, Exception e)
    {
	ErrorMessage.showError("Fatal initialization error",
			       "Cannot start Conzilla:\n\n" +
			       error +
			       "\n\nYou will be given the option\n" +
			       "to reinstall.", e, null);
	needInstall("There was an initialization error.\n\n" +
		    "You are now given the option\n"+
		    "to reinstall, but this may not be the\n" +
		    "right solution, as this will destroy\n" +
		    "any customizations you might have made.");
    }
    
    void needInstall(String s)
    {
	Installer.installOrExit(s);
	
	String error = null;
	Exception ex = null;
	try {
	    tryInit(null);
	} catch(IOException e)
	    {
		error =
		    "Conzilla does not seem to be correctly installed\n" +
		    "on your computer";
		ex = e;
	    }
	catch(MalformedURIException e)
	    {
		error =
		    "Invalid URI\n\n" + e.getURI() +
		    "\n\nin config file.";
		ex = e;
	    }
	catch(ResolveException e)
	    {
		error = "Invalid resolver file.";
		ex = e;
	    }
	catch(LibraryException e)
	    {
		error = "Invalid root library.";
		ex = e;
	    }
	if(error != null)
	    {
		ErrorMessage.showError("Fatal Error",
				       "Could not start Conzilla:\n\n" +
				       error +
				       "\n\nThis is probably an installer bug." +
				       "\n\nGiving up.\n\n", ex, null);	  
		exit(1);
	    }
    }    
}
