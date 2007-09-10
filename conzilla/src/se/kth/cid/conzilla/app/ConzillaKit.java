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
import se.kth.cid.component.xml.*;
import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.util.*;

import java.util.*;
import java.awt.*;
import java.net.*;

/** A kit of resources for the conzilla environment.*/
public class ConzillaKit
{
  Conzilla conzilla;
  ComponentStore store;
  ContentDisplayer contentDisplayer;
  //  MetaDataDisplayer metaData;
  //  public LibraryDisplayer libraryDisplayer;
  //  public NeuronDisplayer neuronDisplayer;
  
  public ConzillaKit(URI resolverTable, Conzilla conzilla)
    {
      this.conzilla = conzilla;
      try {
	// Add other resolvers here.
	TableResolverXmlLoader load = new TableResolverXmlLoader();
	TableResolver resolver      = load.loadResolver(resolverTable);
	
	DefaultComponentHandler handler =
	  new DefaultComponentHandler(resolver);

	// Add other formats here.
	handler.addFormatHandler(new MIMEType("text/xml"),
				 new XmlFormatHandler());
	
	store = new ComponentStore(handler, new EasyCCache());


	// Add other content displayers here.
	MultiContentDisplayer md = new MultiContentDisplayer(store);
	contentDisplayer = md;
	
	if(conzilla.isApplet())
	  md.addContentDisplayer(null, new  BrowserContentDisplayer(conzilla.getAppletContext(),
								    conzilla.getParameter("TARGETWINDOW"), store));
	else
	  md.addContentDisplayer(null, new NetscapeContentDisplayer(store));
	
	md.addContentDisplayer(new MIMEType(ConceptMap.MIME_TYPE),
			       new FrameMapContentDisplayer(store));

	
	//	metaData =	new FrameMetaDataDisplayer(saver);
	//	((FrameMetaDataDisplayer) metaData).setLocation(300, 300);
	
	//      neuronDisplayer = new FrameNeuronDisplayer(this);
	
	//	fixLibrary();
	
      } catch(MalformedMIMETypeException e)
	{
	  TextOptionPane.showError(null, "Malformed MIME Type:\n " +
				   e.getMessage());
	  conzilla.exit(-1);
	}
      catch(ResolveException e)
	{
	  TextOptionPane.showError(null, "Error loading resolver table:\n "
				   + e.getMessage());
	  conzilla.exit(-1);
	}
    }
//  private void fixLibrary()
//    {
//	ResourceController cont=new ResourceController(this);
//	cont.setLayout(new BorderLayout());
//	cont.setContentSelector(new ListContentSelector());
//	cont.setToolFactory(new BasicToolFactory(cont, cont));
//	
//	cont.setContentDisplayer(contentDisplayer);
//	cont.setMetaDataDisplayer(metaData);
//	
//	IndexLibrary library = ResourceLibrary.getDefault(cont);
//	cont.resourceLibrary(library);
//	
//	libraryDisplayer = new FrameLibraryDisplayer(library, cont);
  //    }

  public Conzilla getConzilla()
    {
      return conzilla;
    }

  public ComponentStore getComponentStore()
    {
      return store;
    }

  public ContentDisplayer getContentDisplayer()
    {
      return contentDisplayer;
    }
}
