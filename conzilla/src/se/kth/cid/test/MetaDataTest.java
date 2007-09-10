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
import se.kth.cid.neuron.*;
import se.kth.cid.xml.*;


import java.util.*;

public class MetaDataTest
{
  public static void main(String[] argv)
    throws Exception
    {
      if(argv.length < 2)
	{
	  Tracer.trace("Usage: MetaDataTest  URLLocator-origin Component [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.ALL);
      
      
      URI table = URIClassifier.parseURI(argv[0]);
      
      ResolverTable rtable = new ResolverTable(table);
      TableResolver resolver = new TableResolver();
      rtable.fillResolver(resolver);

      DefaultComponentHandler handler = new DefaultComponentHandler(resolver);
      handler.addFormatHandler(MIMEType.XML, new XmlFormatHandler());
      
      URI compuri = URIClassifier.parseURI(argv[1]);
      
      Component comp = handler.loadComponent(compuri);

      MetaData md = comp.getMetaData();

      Locale.setDefault(new Locale("fr", "", ""));
      
      Tracer.debug("Title: " + MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()));

      URI baseuri=URIClassifier.parseValidURI(comp.getURI());
      Tracer.debug("URIs: " + Arrays.asList(MetaDataUtils.getLocations(md.get_technical_location(), baseuri)));
      Tracer.debug("MIME types: " + Arrays.asList(MetaDataUtils.getDigitalFormats(md.get_technical_format())));


      String[] entries = {"Social Sciences", "Political Science"};

      Tracer.debug("Match?: " + MetaDataUtils.isClassifiedAs(md.get_classification(), null, null, entries));
      
      MetaData.Location l = new MetaData.Location("URI", "http://hej/");
      MetaDataUtils.addObject(md, "technical_location", l);
      MetaDataUtils.addObject(md, "technical_location", l);

      Tracer.debug("URIs: " + Arrays.asList(MetaDataUtils.getLocations(md.get_technical_location(), baseuri)));

      MetaDataUtils.removeObject(md, "technical_location", l);
      Tracer.debug("URIs: " + Arrays.asList(MetaDataUtils.getLocations(md.get_technical_location(), baseuri)));

      Tracer.debug("Rels: " + Arrays.asList(md.get_relation()));
      MetaDataUtils.removeObject(md, "relation", MetaDataUtils.getRelationTo(md, "context", URIClassifier.parseValidURI("urn:path:/org/w3/rdf/RDF_Property_Map")));
      Tracer.debug("Rels: " + Arrays.asList(md.get_relation()));
      //      XmlComponentIO componentIO = new XmlComponentIO();
      
      //componentIO.printComponent(comp, System.out);
    }
}





