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


package se.kth.cid.protege;


import se.kth.cid.conzilla.app.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.library.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;

import java.io.*;
import java.util.*;

/** ConzillaEnvironment for protege tabwidget-plugin.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ConzillaProtege implements ConzillaEnvironment
{
    public static final String GLOBAL_RESOLVER_PROP = "conzilla.resolver.global";
    public static final String CONZILLA_CONFIG_VERSION = "1.1";
    public static final String CONZILLA_VERSION_PROP = "conzilla.version";

    ContentDisplayer defaultContentDisplayer;
    ResolverManager resolverManager;
    ConzillaKit kit;
    String parammap;

  public ConzillaProtege()
    {
    }

    public ConzillaKit getConzillaKit()
    {
	if (kit == null)
	    start(null);
	return kit;
    }

  protected void initDefaultContentDisplayer(PathURNResolver resolver)
    {
      defaultContentDisplayer = new ApplicationContentDisplayer(resolver);
    }

   public ContentDisplayer getDefaultContentDisplayer()
    {
	return defaultContentDisplayer;
    }
  public ResolverManager getResolverManager()
    {
	return resolverManager;
    }

  protected void start(String parammap)
    {
	this.parammap = parammap;
	try {
	    tryInit();
	} catch(IOException e)
	    {
		initError("Some files are missing.\n", e);
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
    
    void tryInit() throws MalformedURIException, IOException, LibraryException, ResolveException
    {
	java.net.URL configURL = getClass().getResource("conzilla.properties");

	//	URI configURI = Installer.getConfigURI();
	//	InputStream is = configURI.getJavaURL().openConnection().getInputStream();

	InputStream is = configURL.openConnection().getInputStream();
	GlobalConfig config = GlobalConfig.getGlobalConfig();
	config.loadConfig(is);
	is.close();

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
	    resolverManager.addTable(new ResolverTable(config.getURI(GLOBAL_RESOLVER_PROP)));
	} catch (ResolveException e)
	    {
		Tracer.trace("Ignoring resolver file: " + config.getURI(GLOBAL_RESOLVER_PROP)
			     + ", as:\n " + e.getMessage(),
			     Tracer.WARNING);
	    }
	
	//FIXME: PathURNResolver should have a addPath method???
	TableResolver tr = (TableResolver) ((RecursiveResolver) resolverManager.getResolver()).getResolver();
	
	URI componentURI = URIClassifier.parseValidURI("res:/components/");
	URI installURI = URIClassifier.parseValidURI("res:/install/");
	tr.addPath("/org/conzilla/builtin", componentURI, MIMEType.XML);
	tr.addPath("/org/conzilla/local", installURI, MIMEType.XML);
	initDefaultContentDisplayer(tr);


	kit = new ConzillaKit(this);

	/*	Vector startMaps = new Vector();
	
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
		if(kit.getConzilla().openMapInNewView((URI) startMaps.get(i), "browse"))
		    return;
	    }
	if(i == startMaps.size())
	    {
		ErrorMessage.showError("No valid maps",
				       "No valid maps could be found.\n\nGiving up.", null, null); 
		exit(1);	
	    }*/
    }
    
    
    public void exit(int result)
    {
    }
    
    
    void initError(String error, Exception e)
    {
	ErrorMessage.showError("Fatal initialization error",
			       "Cannot start Conzilla:\n\n" +
			       error +
			       "\n\nYou will be given the option\n" +
			       "to reinstall.", e, null);
    }
}
