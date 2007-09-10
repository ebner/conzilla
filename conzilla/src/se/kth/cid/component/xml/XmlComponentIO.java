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


package se.kth.cid.component.xml;
import se.kth.cid.component.*;
import se.kth.cid.component.xml.dtd.*;
import se.kth.cid.xml.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;

import java.util.*;
import java.io.*;
import java.net.*;


/** This class handles the loading and saving of components from/to XML,
 *  from/to an Input/OutputStream.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlComponentIO
{
  /** The handler for Neurons.
   */
  static XmlComponentHandler neuronHandler;

  /** The handler for NeuronTypes.
   */
  static XmlComponentHandler neuronTypeHandler;

  /** The handler for ConceptMaps.
   */
  static XmlComponentHandler conceptMapHandler;

  /** The DTD catalog.
   */
  static Catalog catalog;

  /** A table mapping entities to handlers.
   *  Maps ExternalEntity --> XmlComponentHandler
   */
  static Hashtable entityToHandler;

  /** The loader used to load XML documents.
   */
  XmlLoader loader;

  /** The loader used to print XML documents.
   */
  XmlPrinter printer;

  /** Constructs an XmlComponentIO
   */
  public XmlComponentIO()
  {
    loader = new XmlLoader(catalog);
    printer = new XmlPrinter();
  }

  /** Loads a component from the given stream.
   *
   *  The component type is inferred from the document type by using the external
   *  entities of the handlers. 
   *
   *  @param realURI the original URI of the component, which is set
   *                 in the component before it is returned.
   *  @param is the InputStream to read from.
   *  @param recursiveLoader the loader to use for loading needed components.
   *  @return the laoded component.
   *  @exception XmlComponentException if anything goes wrong.
   */
  public Component loadComponent(URI realURI, InputStream is,
				 ComponentLoader recursiveLoader)
    throws XmlComponentException
  {
    XmlDocument doc = null;
    ExternalEntity ent = null;

    try {
      doc = loader.parse(is);
    } catch(XmlLoaderException e)
      {
	throw new XmlComponentException("Parse Error:\n " + e.getMessage());
      }
    
    
    ent = catalog.resolveEntity(doc.getPublicDTDId(), doc.getSystemDTDId(), doc.getRoot().getName());
    
    if(ent == null)
      throw new XmlComponentException("Unknown component with docType: " + doc.getRoot().getName()
				      + " PUBLIC " + doc.getPublicDTDId()
				      + " SYSTEM " + doc.getSystemDTDId());
    
    XmlComponentHandler handler =
      (XmlComponentHandler) entityToHandler.get(ent);
    
    if(handler == null)
      throw new XmlComponentException("Unknown component with docType: " + doc.getRoot().getName()
				      + " PUBLIC " + doc.getPublicDTDId()
				      + " SYSTEM " + doc.getSystemDTDId());

    Component comp = handler.loadComponent(doc.getRoot(), recursiveLoader);

    // Setting the URI will work, as LocalComponents are default editable.
    try {
      comp.setURI(realURI.toString());
    } catch(MalformedURIException e)
      {
	Tracer.trace("Loader got illegal URI: " + realURI + "!", Tracer.ERROR);
	throw new XmlComponentException("Loader got illegal URI: " + realURI + "!");
      }
    // Now we want everyone to know that the component is in synch with the loaded state.
    comp.setEdited(false);
    // Finally, make un uneditable.
    comp.setEditable(false);
    return comp;
  }
  
  /** Prints a component to the given stream.
   *
   *  The component type is inferred by using instanceof.
   *
   *  @param comp the component to print.
   *  @param os the OutputStream to print to.
   *  @exception XmlComponentException if anything goes wrong.
   */
  public void printComponent(Component comp, OutputStream os)
    throws XmlComponentException
  {
    XmlComponentHandler handler;
    
    if(comp instanceof Neuron)
      handler = neuronHandler;
    else if(comp instanceof NeuronType)
      handler = neuronTypeHandler;
    else if(comp instanceof ConceptMap)
      handler = conceptMapHandler;
    else
      throw new XmlComponentException("Unknown component!");
    
    ExternalEntity ent = handler.getDTD();

    try {
      XmlDocument doc = new XmlDocument();
      doc.setSystemDTDId(ent.getSystemID());
      doc.setPublicDTDId(ent.getPublicID());

      doc.setRoot(handler.buildXmlTree(comp));
      
      printer.print(doc, os);
    }
    catch(XmlDocumentException e)
      {
	Tracer.trace("Invalid XML document:" + e.getMessage(), Tracer.ERROR);
	throw new XmlComponentException("Invalid XML document:\n "
					+ e.getMessage());
      }
  }

  /* Initialize the static tables.
   */
  static
  {
    catalog = new Catalog();
    entityToHandler = new Hashtable();

    XmlComponentHandler handler;

    handler = neuronHandler = new XmlNeuronHandler();
    catalog.addEntity(handler.getDTD());
    entityToHandler.put(handler.getDTD(), handler);

    handler = neuronTypeHandler = new XmlNeuronTypeHandler();
    catalog.addEntity(handler.getDTD());
    entityToHandler.put(handler.getDTD(), handler);

    handler = conceptMapHandler = new XmlConceptMapHandler();
    catalog.addEntity(handler.getDTD());
    entityToHandler.put(handler.getDTD(), handler);

    catalog.addEntity(new MetaDataDTD());
  }
}

