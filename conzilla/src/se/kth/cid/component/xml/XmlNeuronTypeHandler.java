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
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.xml.*;
import se.kth.cid.util.*;


import java.util.*;
import java.io.*;


/** The XML handler for NeuronType components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlNeuronTypeHandler extends XmlLoaderHelper implements XmlComponentHandler 
{
  /** Constructs an XmlNeuronHandler.
   */
  public XmlNeuronTypeHandler()
    {
    }
  
  public LocalComponent loadComponent(XmlElement root, URI origURI)
    throws ComponentException
    {
      LocalNeuronType neuronType  = new LocalNeuronType(origURI);

      XmlElement datatags = getSubElement(root, "DataTags");

      XmlElement[] els = datatags.getSubElements("DataTag");
      for(int i = 0; i < els.length; i++)
	neuronType.addDataTag(loadAttribute(els[i], "NAME", null));
      
      XmlElement boxType = getSubElement(root, "BoxType");
      
      neuronType.setBoxType(loadAttribute(boxType, "TYPE", "rectangle"));
      neuronType.setBoxColor(loadColor(boxType, "COLOR", "black"));

      
      XmlElement lineType = getSubElement(root, "LineType");
      neuronType.setLineType(loadAttribute(lineType, "TYPE",
					   "continuous"));
      neuronType.setLineColor(loadColor(lineType, "COLOR", "black"));
      int thickness = loadPositiveInteger(lineType, "THICKNESS", "10", false);
      try{
	neuronType.setLineThickness(thickness);
      } catch(NeuronException e)
	{
	  throw new ComponentException("Invalid line thickness for neurontype: " + thickness
				       + ":\n " + e.getMessage());
	}
      
      els = root.getSubElements("AxonType");

      for(int i = 0; i < els.length; i++)
	{
	  AxonType axonType;
	  try {
	    axonType = neuronType.addAxonType(loadAttribute(els[i], "NAME", null));
	    axonType.setMaximumMultiplicity(loadPositiveInteger(els[i], "MAXIMUMMULTIPLICITY", "infinity", true));
	    axonType.setMinimumMultiplicity(loadPositiveInteger(els[i], "MINIMUMMULTIPLICITY", "0", false));
	  } catch(NeuronException e)
	    {
	      throw new ComponentException("Invalid axon type for neurontype: "
					   + loadAttribute(els[i], "NAME", null)
					   + ":\n " + e.getMessage());
	    }
	
	  XmlElement axondatatags
	    = maybeGetSubElement(root, "DataTags");
	  XmlElement[] axondataels = axondatatags.getSubElements("DataTag");
	  for(int j = 0; j < axondataels.length; j++)
	    axonType.addDataTag(loadAttribute(axondataels[j], "NAME", null));
	
	  
	  XmlElement headEl  = getSubElement(els[i], "HeadType");
	  XmlElement lineEl  = getSubElement(els[i], "LineType");
	  
	  axonType.setHeadType(loadAttribute(headEl, "TYPE", "arrow"));
	  axonType.setHeadFilled(loadBoolean(headEl, "FILLED", "true"));
	  axonType.setHeadSize(loadPositiveInteger(headEl, "SIZE", "10", false));
	  
	  axonType.setLineType(loadAttribute(lineEl, "TYPE", "continuous"));
	  axonType.setLineColor(loadColor(lineEl, "COLOR", "black"));
	  axonType.setLineThickness(loadPositiveInteger(lineEl, "THICKNESS", "1", false));

	}
      
      return neuronType;
    }


  public XmlElement buildXmlTree(Component comp)
    throws ComponentException
    {
      if(! (comp instanceof NeuronType))
	throw new ComponentException("Component is no NeuronType");
      
      NeuronType neuronType = (NeuronType) comp;
      
      XmlElement neuronTypeEl = new XmlElement("NeuronType");
      
      try {
	
	neuronTypeEl.addSubElement(buildDataTags(neuronType.getDataTags()));
	
	XmlElement boxTypeEl = new XmlElement("BoxType");
	
	boxTypeEl.setAttribute("TYPE", neuronType.getBoxType());
	boxTypeEl.setAttribute("COLOR", colorString(neuronType.getBoxColor()));

	neuronTypeEl.addSubElement(boxTypeEl);
	
	XmlElement lineTypeEl = new XmlElement("LineType");
	lineTypeEl.setAttribute("TYPE", neuronType.getLineType());
	lineTypeEl.setAttribute("THICKNESS",
				Integer.toString(neuronType.getLineThickness()));
	lineTypeEl.setAttribute("COLOR", colorString(neuronType.getLineColor()));
	neuronTypeEl.addSubElement(lineTypeEl);
	
	
	AxonType[] axonTypes = neuronType.getAxonTypes();
	for(int i = 0; i < axonTypes.length; i++)
	  {
	    XmlElement axonTypeEl = new XmlElement("AxonType");
	    axonTypeEl.setAttribute("NAME", axonTypes[i].getType());
	    axonTypeEl.setAttribute("MINIMUMMULTIPLICITY", Integer.toString(axonTypes[i].getMinimumMultiplicity()));
	    int maxmult = axonTypes[i].getMaximumMultiplicity();
	    if(maxmult == Integer.MAX_VALUE)
	      axonTypeEl.setAttribute("MAXIMUMMULTIPLICITY", "infinity");
	    else
	      axonTypeEl.setAttribute("MAXIMUMMULTIPLICITY", Integer.toString(maxmult));
	    
	    axonTypeEl.addSubElement(buildDataTags(axonTypes[i].getDataTags()));
	    
	    XmlElement headTypeEl = new XmlElement("HeadType");
	    headTypeEl.setAttribute("TYPE", axonTypes[i].getHeadType());
	    headTypeEl.setAttribute("FILLED", (axonTypes[i].getHeadFilled())
				    ? "true" : "false");
	    headTypeEl.setAttribute("SIZE",
				    Integer.toString(axonTypes[i].getHeadSize()));
	    
	    axonTypeEl.addSubElement(headTypeEl);
	    
	    lineTypeEl = new XmlElement("LineType");
	    lineTypeEl.setAttribute("TYPE", axonTypes[i].getLineType());
	    lineTypeEl.setAttribute("THICKNESS",
				    Integer.toString(axonTypes[i].getLineThickness()));
	    lineTypeEl.setAttribute("COLOR", colorString(axonTypes[i].getLineColor()));
	    axonTypeEl.addSubElement(lineTypeEl);
	    neuronTypeEl.addSubElement(axonTypeEl);
	  }
      } catch(XmlElementException e)
	{
	  Tracer.trace("Error building XML tree for neurontype at element "
		       + e.getElement().getName() + ": " + e.getMessage(),
		       Tracer.ERROR);
	  throw new ComponentException("Error building XML tree for neurontype at element "
				       + e.getElement().getName()
				       + ":\n " + e.getMessage());
	}
      
      return neuronTypeEl;
    }
}




