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
import se.kth.cid.component.xml.dtd.*;
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
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
public class XmlNeuronTypeHandler implements XmlComponentHandler
{
  /** The external entity for the NeuronType DTD.
   */
  static ExternalEntity entity = new NeuronTypeDTD();

  /** Constructs an XmlNeuronHandler.
   */
  public XmlNeuronTypeHandler()
  {
  }

  public ExternalEntity getDTD()
  {
    return entity;
  }
  
  public Component loadComponent(XmlElement root,
				 ComponentLoader recursiveLoader)
    throws XmlComponentException
  {
    NeuronType neuronType  = new LocalNeuronType();

    XmlElement[] els = root.getSubElements("MetaData");
      
    XmlMetaDataHandler.load(neuronType.getMetaData(), els[0]);
      

    els = root.getSubElements("DataTags");
      
    XmlElement[] subEls = els[0].getSubElements("DataTag");
    for(int i = 0; i < subEls.length; i++)
      {
	String tag = subEls[i].getAttribute("NAME");
	neuronType.addDataTag(tag);
      }
      
      
    els = root.getSubElements("BoxStyle");
      
    subEls = els[0].getSubElements("Box");
      
    neuronType.setBox(subEls[0].getAttribute("TYPE"));
    neuronType.setBoxColor(XmlLoaderHelper.loadColor(subEls[0], "COLOR"));

      
    subEls = els[0].getSubElements("Line");
    neuronType.setLineType(subEls[0].getAttribute("TYPE"));
    neuronType.setLineColor(XmlLoaderHelper.loadColor(subEls[0], "COLOR"));
    int thickness = XmlLoaderHelper.loadPositiveInteger(subEls[0], "THICKNESS", false);
    try{
      neuronType.setLineThickness(thickness);
    } catch(LineThicknessException e)
      {
	throw new XmlComponentException("Invalid line thickness for neurontype: " + thickness
					+ ":\n " + e.getMessage());
      }
      
    els = root.getSubElements("RoleType");

    for(int i = 0; i < els.length; i++)
      {
	String roleTypeName = els[i].getAttribute("NAME");

	int[] mult = XmlLoaderHelper.loadMultiplicity(els[i]);

	  
	XmlElement[] styleEls = els[i].getSubElements("RoleStyle");

	XmlElement[] headEls = styleEls[0].getSubElements("Head");
	  
	XmlElement[] lineEls = styleEls[0].getSubElements("Line");

	String headType = headEls[0].getAttribute("TYPE");
	boolean headFilled =
	  XmlLoaderHelper.loadBoolean(headEls[0], "FILLED");
	int headSize = XmlLoaderHelper.loadPositiveInteger(headEls[0],
							   "SIZE",
							   false);
	  
	String lineType = lineEls[0].getAttribute("TYPE");
	int lineColor = XmlLoaderHelper.loadColor(lineEls[0], "COLOR");
	int lineThickness =
	  XmlLoaderHelper.loadPositiveInteger(lineEls[0], "THICKNESS", false);
	RoleType roleType = new RoleType(roleTypeName, lineType,
					 lineThickness,
					 lineColor, mult[0], mult[1],
					 headType, headFilled, headSize);
	  
	try {
	  neuronType.addRoleType(roleType);
	} catch(NeuronException e)
	  {
	    throw new XmlComponentException("Invalid role type for neurontype: "
					    + roleTypeName + ":\n " +
					    e.getMessage());
	  }
      }
      
    return neuronType;
  }


  public XmlElement buildXmlTree(Component comp)
    throws XmlComponentException
  {
    if(! (comp instanceof NeuronType))
      throw new XmlComponentException("Component is no NeuronType");
    
    NeuronType neuronType = (NeuronType) comp;

    XmlElement neuronTypeEl = new XmlElement("NeuronType");
    
    try {
      
      neuronTypeEl.addSubElement(XmlMetaDataHandler.buildXmlTree(neuronType.getMetaData()));
      neuronTypeEl.addSubElement(XmlLoaderHelper.buildDataTags(neuronType.getDataTags()));
      
      XmlElement styleEl = new XmlElement("BoxStyle");
      
      XmlElement boxEl = new XmlElement("Box");
      boxEl.setAttribute("TYPE", neuronType.getBox());
      boxEl.setAttribute("COLOR", XmlLoaderHelper.colorString(neuronType.getBoxColor()));
      styleEl.addSubElement(boxEl);
      
      XmlElement lineEl = new XmlElement("Line");
      lineEl.setAttribute("TYPE", neuronType.getLineType());
      lineEl.setAttribute("THICKNESS",
			  Integer.toString(neuronType.getLineThickness()));
      lineEl.setAttribute("COLOR", XmlLoaderHelper.colorString(neuronType.getLineColor()));
      styleEl.addSubElement(lineEl);
      neuronTypeEl.addSubElement(styleEl);
      
      

      
      RoleType[] roleTypes = neuronType.getRoleTypes();
      for(int i = 0; i < roleTypes.length; i++)
	{
	  XmlElement roleTypeEl = new XmlElement("RoleType");
	  roleTypeEl.setAttribute("NAME", roleTypes[i].type);
	  roleTypeEl.addSubElement(XmlLoaderHelper.buildMultiplicity(roleTypes[i].lowmultiplicity,
								     roleTypes[i].highmultiplicity));
	  XmlElement roleStyleEl = new XmlElement("RoleStyle");
	  
	  XmlElement headEl = new XmlElement("Head");
	  headEl.setAttribute("TYPE", roleTypes[i].headtype);
	  headEl.setAttribute("FILLED", (roleTypes[i].filled) ? "true" : "false");
	  headEl.setAttribute("SIZE",
			      Integer.toString(roleTypes[i].headsize));
	  
	  roleStyleEl.addSubElement(headEl);
	  
	  lineEl = new XmlElement("Line");
	  lineEl.setAttribute("TYPE", roleTypes[i].linetype);
	  lineEl.setAttribute("THICKNESS",
			      Integer.toString(roleTypes[i].linethickness));
	  lineEl.setAttribute("COLOR", XmlLoaderHelper.colorString(roleTypes[i].linecolor));
	  roleStyleEl.addSubElement(lineEl);
	  roleTypeEl.addSubElement(roleStyleEl);
	  neuronTypeEl.addSubElement(roleTypeEl);
	}
    } catch(XmlElementException e)
      {
	Tracer.trace("Error building XML tree for neurontype at element "
		     + e.getElement().getName() + ": " + e.getMessage(),
		     Tracer.ERROR);
	throw new XmlComponentException("Error building XML tree for neurontype at element "
					+ e.getElement().getName()
					+ ":\n " + e.getMessage());
      }
    
    return neuronTypeEl;
  }
}




