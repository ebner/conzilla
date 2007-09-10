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
import se.kth.cid.conzilla.component.ComponentEdit;
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
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.util.*;

import java.util.*;
import java.awt.*;
import java.net.*;

/** A kit of resources for the conzilla environment.*/
public class ConzillaKit
{
  Conzilla conzilla;
  ConzillaEnvironment environment;
  ComponentStore store;
  ContentDisplayer contentDisplayer;
  ComponentEdit componentEdit;
  FrameMetaDataDisplayer metaDataDisplayer;
  FilterFactory filterFactory;
  ResolverEdit  resolverEdit;
  
  //  public LibraryDisplayer libraryDisplayer;
  
  public ConzillaKit(ConzillaEnvironment env)
    {
      this.environment = env;
      store = env.getComponentStore();

      componentEdit = new ComponentEdit(this);
      
      filterFactory = new SimpleFilterFactory();

      // Add other content displayers here.
      MultiContentDisplayer md = new MultiContentDisplayer();
      contentDisplayer = md;
      
      md.addContentDisplayer(null, env.getDefaultContentDisplayer());
      
      md.addContentDisplayer(MIMEType.CONCEPTMAP,
			     new FrameMapContentDisplayer(store));

      conzilla = new Conzilla(this);
      
      metaDataDisplayer = new FrameMetaDataDisplayer();
      //	((FrameMetaDataDisplayer) metaData).setLocation(300, 300);
      
      //      neuronDisplayer = new FrameNeuronDisplayer(this);
      
      //	fixLibrary();
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

  public ComponentEdit getComponentEdit()
    {
      return componentEdit;
    }
  public FrameMetaDataDisplayer getMetaDataDisplayer()
    {
      return metaDataDisplayer;
    }
  
  public FilterFactory getFilterFactory()
    {
      return filterFactory;
    }

  public ResolverEdit getResolverEditor()
    {
      if(resolverEdit == null)
	resolverEdit = new ResolverEdit(environment.getResolverManager());

      return resolverEdit;
    }

}
