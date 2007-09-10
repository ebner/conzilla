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


package se.kth.cid.test;

import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.neuron.*;
import se.kth.cid.xml.*;


import java.util.*;

public class Content
{
  public static void main(String[] argv)
    throws Exception
    {
      if(argv.length < 2)
	{
	  Tracer.trace("Usage: Loader  URLLocator-origin Component [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 3)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[3]));
      else
	Tracer.setLogLevel(Tracer.NONE);
      
      
      URI table = URIClassifier.parseURI(argv[0]);
      
      ResolverTable rtable = new ResolverTable(table);
      TableResolver resolver = new TableResolver();
      rtable.fillResolver(resolver);

      DefaultComponentHandler handler = new DefaultComponentHandler(resolver);
      handler.addFormatHandler(MIMEType.XML, new XmlFormatHandler());

      ComponentStore store = new ComponentStore(handler, new SoftCache());
      
      URI compuri = URIClassifier.parseURI(argv[1]);
      
      RelationSet relset = new RelationSet(compuri, "content", store);

      Component[] co = relset.getRelations();

      for(int i = 0; i < co.length; i++)
	{
	  Tracer.debug("Content: " + co[i].getURI());
	}
      
      relset = new RelationSet(compuri, "context", store);

      co = relset.getRelations();
      
      for(int i = 0; i < co.length; i++)
	{
	  Tracer.debug("Context: " + co[i].getURI());
	}

      
    }
  
}





