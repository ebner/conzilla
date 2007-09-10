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

  /** The loader used to print XML documents.
   */
  XmlPrinter printer;

  XmlNeuronHandler neuronHandler;
  XmlNeuronTypeHandler neuronTypeHandler;
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
   *  The component type is inferred from the document element.
   *
   *  @param origuri the original URI of the component, which is set
   *                 in the component before it is returned.
   *  @param is the InputStream to read from.
   *  @return the loaded component.
   *  @exception ComponentException if anything goes wrong.
   */
  public Component loadComponent(URI origuri, InputStream is,
				 boolean isSavable)
    throws ComponentException
  {
    XmlDocument doc = null;
    
    try {
      doc = loader.parse(is);
    } catch(XmlLoaderException e)
      {
	throw new ComponentException("Parse Error:\n " + e.getMessage());
      }
    
    if(!(doc.getRoot().getName().equals("Component")))
      throw new ComponentException("Document was no Component. DOCTYPE: " +
				   doc.getRoot().getName());

    LocalComponent comp;
    
    XmlElement compEl = XmlLoaderHelper.maybeGetSubElement(doc.getRoot(), "ConceptMap");
    if(compEl != null)
      comp = conceptMapHandler.loadComponent(compEl, origuri);      

    else if((compEl = XmlLoaderHelper.maybeGetSubElement(doc.getRoot(), "Neuron")) != null)
      comp = neuronHandler.loadComponent(compEl, origuri);

    else if((compEl = XmlLoaderHelper.maybeGetSubElement(doc.getRoot(), "NeuronType")) != null)
      comp = neuronTypeHandler.loadComponent(compEl, origuri);
    else
      comp = new LocalComponent(origuri);

    XmlMetaDataHandler.load(comp.getMetaData(),
			    XmlLoaderHelper.getSubElement(doc.getRoot(),
							  "MetaData"));
    
    // Now we want everyone to know that the component is in synch with the loaded state.
    comp.setEdited(false);
    comp.setEditable(isSavable);
    
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

      
      XmlElement metaDataEl = new XmlElement("MetaData");
      try {
	metaDataEl.addSubElement(XmlMetaDataHandler.buildXmlTree(comp.getMetaData()));
      } catch (XmlElementException e)
	{
	  Tracer.trace("XML exception." + e.getMessage(), Tracer.ERROR);
	  throw new IllegalStateException("XML exception." + e.getMessage());
	}
      
      XmlElement el = null;
      
      if(comp instanceof Neuron)
	el = neuronHandler.buildXmlTree(comp);
      else if(comp instanceof NeuronType)
	el = neuronTypeHandler.buildXmlTree(comp);
      else if(comp instanceof ConceptMap)
	el = conceptMapHandler.buildXmlTree(comp);

      try {
	root.addSubElement(metaDataEl);
	
	if(el != null)
	  root.addSubElement(el);
      } catch (UnknownElementNameException e)
	{
	  Tracer.trace("Unknown XML element." + e.getMessage(), Tracer.ERROR);
	  throw new IllegalStateException("Unknown XML element." + e.getMessage());
	}
      doc.setRoot(root);
      
      printer.print(doc, os);
    }
  
}

