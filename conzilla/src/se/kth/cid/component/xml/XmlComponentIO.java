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
import se.kth.cid.component.local.*;
import se.kth.cid.identity.*;
import se.kth.cid.xml.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;

import java.util.*;
import java.io.*;


/** This class handles the loading and saving of components from/to XML,
 *  from/to an Input/OutputStream.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlComponentIO
{
  
  /** The loader used to load XML documents.
   */
  XmlLoader loader;

  /** The printer used to print XML documents.
   */
  XmlPrinter printer;

  /** The handler for Neurons
   */
  XmlNeuronHandler neuronHandler;

  /** The handler for NeuronTypes
   */
  XmlNeuronTypeHandler neuronTypeHandler;

  /** The handler for ConceptMaps
   */
  XmlConceptMapHandler conceptMapHandler;
  
  
  /** Constructs an XmlComponentIO
   */
  public XmlComponentIO()
  {
    loader = new XmlLoader(null);
    printer = new XmlPrinter();
    printer.setStandalone(true);
    neuronHandler = new XmlNeuronHandler();
    neuronTypeHandler = new XmlNeuronTypeHandler();
    conceptMapHandler = new XmlConceptMapHandler();
  }

  /** Loads a component from the given stream.
   *
   *  The component type is inferred from the document.
   *
   *  @param origuri the original URI of the component
   *  @param is the InputStream to read from.
   *  @param isSavable whether the component is savable. Will become the editablility
   *         of the component
   *  @return the loaded component.
   *  @exception ComponentException if anything goes wrong.
   */
  public Component loadComponent(URI origuri, URI loadURI, InputStream is,
				 boolean isSavable)
    throws ComponentException
  {
    XmlDocument doc = null;
    GenericComponent comp;
  
    try {
	try {
	    doc = loader.parse(is);
	} catch(XmlLoaderException e)
	    {
		throw new ComponentException("Parse Error:\n " + e.getMessage());
	    }
	
	if(!(doc.getRoot().getName().equals("Component")))
	    throw new ComponentException("Document was no Component. DOCTYPE: " +
					 doc.getRoot().getName());
	
	String version = XmlLoaderHelper.loadAttribute(doc.getRoot(), "VERSION",
						       XmlFormatHandler.COMPONENT_DTD_VERSION);
	if(!XmlFormatHandler.COMPONENT_DTD_VERSION.equals(version))
	    {
		throw new ComponentException("Cannot handle version " + version +
					     " components");
	    }
	
	XmlElement compEl = XmlLoaderHelper.maybeGetSubElement(doc.getRoot(), "ConceptMap");
	if(compEl != null)
	    comp = conceptMapHandler.loadComponent(compEl, origuri, loadURI);
	
	else if((compEl = XmlLoaderHelper.maybeGetSubElement(doc.getRoot(), "Neuron")) != null)
	    comp = neuronHandler.loadComponent(compEl, origuri, loadURI);
	
	else if((compEl = XmlLoaderHelper.maybeGetSubElement(doc.getRoot(), "NeuronType")) != null)
	    comp = neuronTypeHandler.loadComponent(compEl, origuri, loadURI);
	else
	    comp = new LocalComponent(origuri, loadURI, MIMEType.XML);

	XmlMetaDataHandler.load(comp.getMetaData(),
				XmlLoaderHelper.getSubElement(doc.getRoot(),
							      "MetaData"));
	
	// Now we want everyone to know that the component is in synch with the loaded state.
	comp.setEdited(false);
	comp.setEditable(isSavable);
	//    Tracer.debug("setEdited false!!!!!!!!!!!!!!!!!! "+comp.getURI());
    } catch (XmlStructureException e)
	{
	    throw new ComponentException(e.getMessage());
	}
    return comp;
  }
  
  /** Prints a component to the given stream.
   *
   *  The component type is inferred by using instanceof.
   *
   *  @param comp the component to print.
   *  @param os the OutputStream to print to.
   *  @exception ComponentException if anything goes wrong.
   */
  public void printComponent(Component comp, OutputStream os)
    throws ComponentException
    {
      XmlComponentHandler handler;      
      
      XmlDocument doc = new XmlDocument();
      
      XmlElement root = new XmlElement("Component");

      root.setAttribute("VERSION", XmlFormatHandler.COMPONENT_DTD_VERSION);
      
      XmlElement metaDataEl = new XmlElement("MetaData");
      try {
	metaDataEl.addSubElement(XmlMetaDataHandler.buildXmlTree(comp.getMetaData()));
	
	XmlElement el = null;
	
	if(comp instanceof Neuron)
	  el = neuronHandler.buildXmlTree(comp);
	else if(comp instanceof NeuronType)
	  el = neuronTypeHandler.buildXmlTree(comp);
	else if(comp instanceof ConceptMap)
	  el = conceptMapHandler.buildXmlTree(comp);
	
	root.addSubElement(metaDataEl);
	
	if(el != null)
	  root.addSubElement(el);
      } catch (XmlElementException e)
	{
	  Tracer.error("XML exception." + e.getMessage());
	}
      
      doc.setRoot(root);
      
      printer.print(doc, os);
    }
  
}

