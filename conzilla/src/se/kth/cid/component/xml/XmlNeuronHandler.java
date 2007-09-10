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
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.xml.*;

import java.util.*;
import java.io.*;


/** The XML handler for Neuron components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlNeuronHandler extends XmlLoaderHelper implements XmlComponentHandler 
{
  /** Constructs an XmlNeuronHandler.
   */
  public XmlNeuronHandler()
  {
  }

  public LocalComponent loadComponent(XmlElement root, URI origuri)
    throws ComponentException
  {
    LocalNeuron neuron;

    try {
      neuron = new LocalNeuron(origuri, URIClassifier.parseURI(root.getAttribute("TYPEURI"), origuri));
    } catch(MalformedURIException e)
      {
	throw new ComponentException("Invalid URI in TYPEURI for Neuron: "
				     + e.getURI() + ":\n "
				     + e.getMessage());
      }
      
    XmlElement data = maybeGetSubElement(root, "Data");

    if(data != null)
      {
	XmlElement[] dataEls = data.getSubElements("Tag");
	
	for(int i = 0; i < dataEls.length; i++)
	  {
	    String tag   = dataEls[i].getAttribute("NAME");
	    String value = dataEls[i].getCDATA();
	    neuron.addDataValue(tag, value);
	  }
      }
      
    XmlElement[] els = root.getSubElements("Axon");
      
    for(int i = 0; i < els.length; i++)
      {
	Axon axon;
	try {
	  axon = neuron.addAxon(loadAttribute(els[i], "ID", null),
				loadAttribute(els[i], "TYPE", null),
				loadAttribute(els[i], "ENDURI", null));
	
	} catch(NeuronException e)
	  {
	    throw new ComponentException("Could not add axon to neuron:\n "
					 + e.getMessage());
	  }
	catch(InvalidURIException e)
	  {
	    throw new ComponentException("Invalid URI in ENDURI for neuron: "
					 + e.getURI() + ":\n "
					 + e.getMessage());
	  }
	
	data = maybeGetSubElement(els[i], "Data");
	
	if(data != null)
	  {
	    XmlElement[] dataEls = data.getSubElements("Tag");
	    
	    for(int j = 0; j < dataEls.length; j++)
	      {
		String tag   = dataEls[j].getAttribute("NAME");
		String value = dataEls[j].getCDATA();
		axon.addDataValue(tag, value);
	      }
	  }
      } 
    
    return neuron;
  }
  
  
  public XmlElement buildXmlTree(Component comp)
    throws ComponentException
    {
      if(! (comp instanceof Neuron))
	throw new ComponentException("Component is no Neuron!");
      
      Neuron neuron = (Neuron) comp;
      
      XmlElement neuronEl = new XmlElement("Neuron");
      
      try {
	neuronEl.setAttribute("TYPEURI", neuron.getType());
		
	
	XmlElement dataEl = new XmlElement("Data");
	String[] tags = neuron.getDataTags();

	if(tags.length > 0)
	  {
	    for(int i = 0; i < tags.length; i++)
	      {
		String[] values = neuron.getDataValues(tags[i]);
		for(int j = 0; j < values.length; j++)
		  {
		    XmlElement dataTagEl = new XmlElement("Tag");
		    dataTagEl.setAttribute("NAME", tags[i]);
		    dataTagEl.setCDATA(values[j]);
		    dataEl.addSubElement(dataTagEl);
		  }
	      }
	    neuronEl.addSubElement(dataEl);
	  }
	
	Axon[] axons = neuron.getAxons();
	for(int i = 0; i < axons.length; i++)
	  {
	    XmlElement axonEl = new XmlElement("Axon");
	    axonEl.setAttribute("ID", axons[i].getID());
	    axonEl.setAttribute("TYPE", axons[i].getType());
	    axonEl.setAttribute("ENDURI", axons[i].getEndURI());
	    
	    tags = axons[i].getDataTags();
	    
	    if(tags.length > 0)
	      {
		for(int j = 0; j < tags.length; j++)
		  {
		    String[] values = axons[i].getDataValues(tags[j]);
		    for(int k = 0; k < values.length; k++)
		      {
			XmlElement dataTagEl = new XmlElement("Tag");
			dataTagEl.setAttribute("NAME", tags[j]);
			dataTagEl.setCDATA(values[k]);
			dataEl.addSubElement(dataTagEl);
		      }
		    axonEl.addSubElement(dataEl);
		  }
	      }
	    neuronEl.addSubElement(axonEl);
	  }
      }
      catch(XmlElementException e)
	{
	  Tracer.trace("Error building XML tree for neuron at element "
		       + e.getElement().getName() + ": " + e.getMessage(),
		       Tracer.ERROR);
	  throw new ComponentException("Error building XML tree for neuron at element "
				       + e.getElement().getName()
				       + ":\n " + e.getMessage());
	}
      return neuronEl;
    }
}


