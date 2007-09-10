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
import se.kth.cid.component.*;
import se.kth.cid.library.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.filter.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.conzilla.view.*;
import se.kth.cid.util.*;

import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

/** A kit of resources for the conzilla environment.*/
public class ConzillaKit
{

    public static final String ROOT_LIBRARY_PROP = "conzilla.library.root";
    public static final String EXTRAS_PROP = "conzilla.extra.list";
    public static final String FORMATS_PROP = "conzilla.format.list";

    Conzilla conzilla;
    ConzillaEnvironment environment;
    ComponentStore store;
    ContentDisplayer contentDisplayer;
    //  ComponentEdit componentEdit;
    //FrameMetaDataDisplayer metaDataDisplayer;
    //    FilterFactory filterFactory;
    //    ResolverEdit  resolverEdit;
    
    ConzillaLocaleManager localeManager;

    RootLibrary rootLibrary;

    Hashtable extras;
  
    public ConzillaKit(ConzillaEnvironment env) throws IOException, LibraryException, MalformedURIException
    {
	this.environment = env;

	extras = new Hashtable();
	
	localeManager = new ConzillaLocaleManager();
		
	DefaultComponentHandler handler = new DefaultComponentHandler(env.getResolverManager().getResolver());

	loadFormats(handler);
	
	store = new ComponentStore(handler, new SoftCache());

	handler.setComponentStore(store);

	rootLibrary = new RootLibrary(store, GlobalConfig.getGlobalConfig().getURI(ROOT_LIBRARY_PROP));
	

	//      componentEdit = new ComponentEdit(this);

	//	filterFactory = new SimpleFilterFactory();
	
	// Add other content displayers here.
	MultiContentDisplayer md = new MultiContentDisplayer();
	contentDisplayer = md;
	
	md.addContentDisplayer(null, env.getDefaultContentDisplayer());
	
	md.addContentDisplayer(MIMEType.CONCEPTMAP,
			       new FrameMapContentDisplayer(this, store));
	
	conzilla = new Conzilla();
	conzilla.initConzilla(this);
	//	metaDataDisplayer = new FrameMetaDataDisplayer();
	//	((FrameMetaDataDisplayer) metaData).setLocation(300, 300);
	
	//      neuronDisplayer = new FrameNeuronDisplayer(this);
	
	//	fixLibrary();
	loadExtras();	
    }

    void loadFormats(DefaultComponentHandler handler)
    {
	String formats = GlobalConfig.getGlobalConfig().getProperty(FORMATS_PROP, "");

	StringTokenizer st = new StringTokenizer(formats, ",");
	
	while(st.hasMoreTokens())
	    {
		String format = st.nextToken();

		FormatHandler fh;
		try {
		    fh = (FormatHandler) Class.forName(format).newInstance();
		    
		    handler.addFormatHandler(fh);
		} catch(ClassCastException e)
		    {
			Tracer.trace("This is not a FormatHandler: " + format, Tracer.WARNING);
		    }
		catch(ClassNotFoundException e)
		    {
			Tracer.trace("Could not find FormatHandler: " + format, Tracer.WARNING);
		    }
		catch(InstantiationException e)
		    {
			Tracer.trace("Could not instantiate FormatHandler: " + format + "\n " + e.getMessage(), Tracer.WARNING);
		    }
		catch(IllegalAccessException e)
		    {
			Tracer.trace("Could not instantiate FormatHandler (illegal access): " + format + "\n " + e.getMessage(),
				     Tracer.WARNING);
		    }
	    }
    }
    
   void loadExtras()
    {
	String extras = GlobalConfig.getGlobalConfig().getProperty(EXTRAS_PROP, "");
	StringTokenizer st = new StringTokenizer(extras, ",");
	
	while(st.hasMoreTokens())
	    {
		String extra = st.nextToken();

		if(extra == null)
		    {
			Tracer.trace("Extra invalid: " + extra, Tracer.WARNING);
			continue;
		    }
		Extra nextra;
		try {
		    nextra = (Extra) Class.forName(extra).newInstance();
		    registerExtra(nextra);
		} catch(ClassCastException e)
		    {
			Tracer.trace("This is not an Extra: " + extra, Tracer.WARNING);
		    }
		catch(ClassNotFoundException e)
		    {
			Tracer.trace("Could not find Extra: " + extra, Tracer.WARNING);
		    }
		catch(InstantiationException e)
		    {
			Tracer.trace("Could not instantiate Extra: " + extra + "\n " + e.getMessage(), Tracer.WARNING);
		    }
		catch(IllegalAccessException e)
		    {
			Tracer.trace("Could not instantiate Extra (illegal access): " + extra + "\n " + e.getMessage(),
				     Tracer.WARNING);
		    }
	    }
    }

    public void registerExtra(Extra extra)
    {
	if(extra.initExtra(this))
	    extras.put(extra.getName(), extra);
	else
	    Tracer.trace("Extra was not initialized: " + extra.getClass().getName(), Tracer.WARNING);
    }
  
    public Enumeration getExtras()
    {
	return extras.elements();
    }

    public void extendMenu(ToolsMenu menu, MapController mc)
    {
	Enumeration en = getExtras();
	while (en.hasMoreElements())
	    ((Extra) en.nextElement()).extendMenu(menu, mc);
    }

    public Conzilla getConzilla()
    {
      return conzilla;
    }
    
    public ConzillaEnvironment getConzillaEnvironment()
    {
      return environment;
    }

    public ComponentStore getComponentStore()
    {
      return store;
    }

    public ContentDisplayer getContentDisplayer()
    {
      return contentDisplayer;
    }

    /*  public ComponentEdit getComponentEdit()
    {
      return componentEdit;
      }*/
    /*    public FrameMetaDataDisplayer getMetaDataDisplayer()
    {
	return metaDataDisplayer;
    }
    */
    /*    public FilterFactory getFilterFactory()
    {
	return filterFactory;
	}*/

    /*    public ResolverEdit getResolverEditor()
    {
	if(resolverEdit == null)
	    resolverEdit = new ResolverEdit(environment.getResolverManager());
	
	return resolverEdit;
    }   
    */
    public RootLibrary getRootLibrary()
    {
	return rootLibrary;
    }
}
