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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.component.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.identity.*;

import java.io.*;

public abstract class ConzillaAppEnv extends ConzillaEnvironment
{
  ConzillaConfig config;
  ConzillaLocaleManager localeManager;
    //  PropertiesManager properties;
  
  public ConzillaAppEnv()
    {
      try {
	tryInit();
      } catch(IOException e)
	{
	  needInstall("Conzilla does not seem to be installed\n" +
		      "on your account.\n");
	}
      catch(MalformedURIException e)
	{
	  initError("Invalid URI\n\n" + e.getURI() +
		    "\n\nin config file.", e);
	}
      catch(ConfigException e)
	{
	  initError("Invalid config file.", e);
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
    
  void tryInit() throws MalformedURIException, IOException, LibraryException, ResolveException, ConfigException
    {
      config = new ConzillaConfig(true);
      config.load();

      localeManager = new ConzillaLocaleManager(config);
      
      PropertiesManager.setDefaultPropertiesManager(new PropertiesManager(config.getPropertiesDir()));

      resolverManager = new ResolverManager();      
      
      resolverManager.addTable(new ResolverTable(config.getLocalResolver()));
      try {
	resolverManager.addTable(new ResolverTable(config.getGlobalResolver()));
      } catch (ResolveException e)
	{
	  Tracer.trace("Ignoring resolver file: " + config.getGlobalResolver()
		       + ", as:\n " + e.getMessage(),
		       Tracer.WARNING);
	}

      DefaultComponentHandler handler = new DefaultComponentHandler(resolverManager.getResolver());
      
      // Add other formats here.
      handler.addFormatHandler(MIMEType.XML,
			       new XmlFormatHandler());
      
      store = new ComponentStore(handler, new SoftCache());
      
      rootLibrary = new RootLibrary(store, config.getRootLibrary());
      
      initDefaultContentDisplayer(resolverManager.getResolver());
    }

  protected abstract void initDefaultContentDisplayer(PathURNResolver resolver);
  
  public void exit(int result)
    {
      System.exit(result);
    }

  public ConzillaConfig getConfig()
    {
      return config;
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
	tryInit();
      } catch(IOException e)
	{
	  error =
	    "Conzilla does still not seem to be installed\n" +
	    "on your account";
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
      catch(ConfigException e)
	{
	  error = "Invalid Config file.";
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
