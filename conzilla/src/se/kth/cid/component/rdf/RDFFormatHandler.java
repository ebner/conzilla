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


package se.kth.cid.component.rdf;

import se.kth.cid.rdf.*;
import org.w3c.rdf.util.*;
import org.w3c.rdf.model.*;
import org.w3c.rdf.syntax.*;
import org.xml.sax.InputSource;
import org.w3c.rdf.implementation.syntax.sirpac.SiRPAC;

import se.kth.cid.component.*;
import se.kth.cid.component.local.*;

import netscape.security.PrivilegeManager;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conceptmap.local.*;

import java.io.*;
import java.util.*;

/** This is the FormatHandler that are used with RDF models (text/rdf).
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFFormatHandler implements FormatHandler
{
  /** Whether we should bother using the Netscape privilege manager.
   */
  public static boolean usePrivMan = false;

  /** Factory for parsers, models, nodes and serializers.
   */
    ConzillaRDFFactoryImpl factory;

  /** Constructs an XmlFormatLoader.
   */
  public RDFFormatHandler()
  {
      factory = new ConzillaRDFFactoryImpl();
  }
  
  
  public Component loadComponent(URI uri, URI origuri)
    throws ComponentException
    {
      if(usePrivMan)
	PrivilegeManager.enablePrivilege("UniversalConnect");
      
      ConzillaRDFModel      model = factory.createModel(origuri, uri);
      RDFParser parser = new com.hp.hpl.jena.rdf.arp.StanfordImpl();//f.createParser();

      //RDFParser parser = factory.createParser();
      
      java.net.URL url = null;
      try {
	  url = uri.getJavaURL();
      }	catch (java.net.MalformedURLException e)
	  { throw new ComponentException("Invalid URL " + uri +
					 " for component " + origuri + ":\n "
					 + e.getMessage());}
      
      try {

	  //Skipping the normalizing part, hope it is not neccessary.

	  //	  URL url2 = new URL (RDFUtil.normalizeURI(url.toString()));

	  InputSource source = new InputSource(url.openStream());
	  source.setSystemId( origuri.toString() );
	  parser.parse( source, new ModelConsumer(model) );

	  factory.getTotalModel().addModel(model);
	  //	  model.setSourceURI(origuri.toString());
	  //	  source.setPublicId( origuri.toString() );

	  //Loads the metadata for container model, from the container itself (metametadata) 
	  //It sends in it's own empty metadatacomponent for update.
	  //model.getMetaData(model, model.getMetaData());

      } catch (org.xml.sax.SAXException se) {
	  se.getException().printStackTrace();
	  throw new ComponentException("Format error loading URL " + url +
				       " for component " + origuri + ":\n "
				       + se.getMessage());	 
      } catch (java.io.IOException se) {
	  throw new ComponentException("IO error loading URL " + url +
				       " for component " + origuri + ":\n "
				       + se.getMessage());
      } catch (org.w3c.rdf.model.ModelException se) {
	  throw new ComponentException("Model error loading URL " + url +
				       " for component " + origuri + ":\n "
				       + se.getMessage());
      }

      
      return model;
    }
  
  public boolean isSavable(URI uri)
    {
	return false;
    }
  
  public void checkCreateComponent(URI uri) throws ComponentException
    {
	throw new ComponentException("Not implemented on RDF models yet");
    }
    
  public Component createComponent(URI uri, URI realURI, String type, Object extras)
    throws ComponentException
    {
	throw new ComponentException("Creation of components in RDF models not implemented yet.");      
    }

  public void saveComponent(URI uri, Component comp)
    throws ComponentException
    {
	
	throw new ComponentException("Saving of RDF models not implemented yet.");
	//      if(usePrivMan)
	//	  PrivilegeManager.enablePrivilege("UniversalConnect");
    }
}
