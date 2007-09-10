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
  
  public GenericComponent loadComponent(XmlElement root, URI origURI, URI loadURI)
    throws ComponentException
    {
      LocalNeuronType neuronType  = new LocalNeuronType(origURI, loadURI, MIMEType.XML);
      
      try {
	  XmlElement datatags = getSubElement(root, "DataTags");
	  
	  XmlElement[] els = datatags.getSubElements("DataTag");
	  for(int i = 0; i < els.length; i++)
	      neuronType.addDataTag(loadAttribute(els[i], "NAME", null));
	  
	  XmlElement boxType = getSubElement(root, "BoxType");
	  
	  String  type            = loadAttribute(boxType, "TYPE", "rectangle");
	  boolean filled          = loadBoolean(boxType, "FILLED", "false");
	  String  borderType      = loadAttribute(boxType, "BORDERTYPE",
						  "continuous");
	  int     borderThickness = loadPositiveInteger(boxType,
							"BORDERTHICKNESS",
							"2", false);
	  
	  try {
	      neuronType.setBoxType(new NeuronType.BoxType(type, filled, borderType,
							   borderThickness));
	  } catch(NeuronException e)
	      {
	    throw new ComponentException("Invalid box type for neurontype:\n "
					 + e.getMessage());
	      }
	  
	  
	  XmlElement lineType = getSubElement(root, "LineType");
	  
	  type          = loadAttribute(lineType, "TYPE", "continuous");
	  int thickness = loadPositiveInteger(lineType, "THICKNESS", "2", false);
	  try {
	      neuronType.setLineType(new NeuronType.LineType(type, thickness));
	  } catch(NeuronException e)
	      {
		  throw new ComponentException("Invalid line type for neurontype:\n "
					       + e.getMessage());
	      }
	  
	  
	  els = root.getSubElements("AxonType");
	  
	  for(int i = 0; i < els.length; i++)
	      {
		  AxonType axonType;
		  try {
		      axonType = neuronType.addAxonType(loadAttribute(els[i],
								      "NAME", null));
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
		  
		  type       = loadAttribute(headEl, "TYPE", "arrow");
		  filled     = loadBoolean(headEl, "FILLED", "true");
		  int width  = loadPositiveInteger(headEl, "WIDTH", "3", false);
		  int length = loadPositiveInteger(headEl, "LENGTH", "6", false);
		  
		  
		  try {
		      axonType.setHeadType(new NeuronType.HeadType(type, filled, width, length));
		  } catch(NeuronException e)
		      {
			  throw new ComponentException("Invalid head type for axon:\n "
						       + e.getMessage());
		      }
		  
		  type      = loadAttribute(lineEl, "TYPE", "continuous");
		  thickness = loadPositiveInteger(lineEl, "THICKNESS", "2", false);
		  try {
		      axonType.setLineType(new NeuronType.LineType(type, thickness));
		  } catch(NeuronException e)
		      {
			  throw new ComponentException("Invalid line type for axon:\n "
						       + e.getMessage());
		      }
		  
	      }
      } catch (XmlStructureException e)
	  {
	      throw new ComponentException(e.getMessage());
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

	NeuronType.BoxType boxType = neuronType.getBoxType();
	
	boxTypeEl.setAttribute("TYPE", boxType.type);
	boxTypeEl.setAttribute("FILLED", boxType.filled ? "true" : "false");
	boxTypeEl.setAttribute("BORDERTYPE", boxType.borderType);
	boxTypeEl.setAttribute("BORDERTHICKNESS", "" + boxType.borderThickness);

	neuronTypeEl.addSubElement(boxTypeEl);
	
	XmlElement lineTypeEl = new XmlElement("LineType");
	NeuronType.LineType lineType = neuronType.getLineType();
	
	lineTypeEl.setAttribute("TYPE", lineType.type);
	lineTypeEl.setAttribute("THICKNESS", "" + lineType.thickness);

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

	    NeuronType.HeadType headType = axonTypes[i].getHeadType();
	    
	    headTypeEl.setAttribute("TYPE", headType.type);
	    headTypeEl.setAttribute("FILLED", headType.filled
				    ? "true" : "false");
	    headTypeEl.setAttribute("WIDTH", "" + headType.width);
	    headTypeEl.setAttribute("LENGTH", "" + headType.length);
	    
	    axonTypeEl.addSubElement(headTypeEl);
	    
	    lineTypeEl = new XmlElement("LineType");

	    lineType = axonTypes[i].getLineType();
	    
	    lineTypeEl.setAttribute("TYPE", lineType.type);
	    lineTypeEl.setAttribute("THICKNESS", "" + lineType.thickness);

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

  /** Builds a DataTags element.
   *
   *  @param tags the DataTags wanted in the element.
   *  @return a DataTags XmlElement.
   */
  public static XmlElement buildDataTags(String[] tags)
    throws XmlElementException
  {
    XmlElement dataEl = new XmlElement("DataTags");
    
    for(int i = 0; i < tags.length; i++)
      {
	XmlElement dataTagEl = new XmlElement("DataTag");
	dataTagEl.setAttribute("NAME", tags[i]);
	dataEl.addSubElement(dataTagEl);
      }
    return dataEl;
  }
}




