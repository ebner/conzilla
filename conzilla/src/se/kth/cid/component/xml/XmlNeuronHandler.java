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
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.xml.dtd.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.xml.*;

import java.util.*;
import java.io.*;


/** The XML handler for Neuron components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlNeuronHandler implements XmlComponentHandler
{
  /** The external entity for the Neuron DTD.
   */
  static ExternalEntity entity = new NeuronDTD();

  /** Constructs an XmlNeuronHandler.
   */
  public XmlNeuronHandler()
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
    Neuron neuron  = new LocalNeuron();

    try {
      neuron.setType(root.getAttribute("TYPEURI"));
    } catch(MalformedURIException e)
      {
	throw new XmlComponentException("Invalid URI in TYPEURI for Neuron: "
					+ e.getURI() + ":\n "
					+ e.getMessage());
      }
    XmlElement[] els = root.getSubElements("MetaData");
    XmlMetaDataHandler.load(neuron.getMetaData(), els[0]);
      
      
    els = root.getSubElements("Data");

    if(els.length > 0)
      {
	XmlElement[] dataEls = els[0].getSubElements("Tag");
	  
	for(int i = 0; i < dataEls.length; i++)
	  {
	    String tag   = dataEls[i].getAttribute("NAME");
	    String value = dataEls[i].getCDATA();
	    neuron.addDataValue(tag, value);
	  }
      }
      
      
    els = root.getSubElements("Role");
      
    for(int i = 0; i < els.length; i++)
      {
	int[] mult = XmlLoaderHelper.loadMultiplicity(els[i]);
	  
	Role role = new Role(els[i].getAttribute("TYPE"),
			     els[i].getAttribute("NEURONURI"),
			     mult[0], mult[1]);
	  
	try {
	  neuron.addRole(role);
	} catch(NeuronException e)
	  {
	    throw new XmlComponentException("Could not add role to neuron:\n "
					    + e.getMessage());
	  }
      }
      

    els = root.getSubElements("PlaysRoleIn");

    try {
      for(int i = 0; i < els.length; i++)
	neuron.addPlaysRoleIn(els[i].getAttribute("NEURONURI"));
    } catch(MalformedURIException e)
      {
	throw new XmlComponentException("Invalid URI in NEURONURI for neuron: "
					+ e.getURI() + ":\n "
					+ e.getMessage());
      }

    return neuron;
  }


  public XmlElement buildXmlTree(Component comp)
    throws XmlComponentException
  {
    if(! (comp instanceof Neuron))
      throw new XmlComponentException("Component is no Neuron!");

    Neuron neuron = (Neuron) comp;
    
    XmlElement neuronEl = new XmlElement("Neuron");

    try {
      neuronEl.setAttribute("TYPEURI", neuron.getType());
      
      neuronEl.addSubElement(XmlMetaDataHandler.buildXmlTree(neuron.getMetaData()));
      
      
      
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
      
      String[] roleTypes = neuron.getRoleTypes();
      for(int i = 0; i < roleTypes.length; i++)
	{
	  Role[] roles = neuron.getRolesOfType(roleTypes[i]);
	  for(int j = 0; j < roles.length; j++)
	    {
	      XmlElement roleEl = new XmlElement("Role");
	      roleEl.setAttribute("TYPE", roleTypes[i]);
	      roleEl.setAttribute("NEURONURI", roles[j].neuronuri);
	      roleEl.addSubElement(XmlLoaderHelper.
				   buildMultiplicity(roles[j].
						   lowestmultiplicity,
						     roles[j].
						     highestmultiplicity));
	      neuronEl.addSubElement(roleEl);
	    }
	}
      
      String[] playsRolesIn = neuron.getPlaysRolesIn();
      for(int i = 0; i < playsRolesIn.length; i++)
	{
	  XmlElement playsRoleEl = new XmlElement("PlaysRoleIn");
	  playsRoleEl.setAttribute("NEURONURI", playsRolesIn[i]);
	  neuronEl.addSubElement(playsRoleEl);
	}
    }
    catch(XmlElementException e)
      {
	Tracer.trace("Error building XML tree for neuron at element "
		     + e.getElement().getName() + ": " + e.getMessage(),
		     Tracer.ERROR);
	throw new XmlComponentException("Error building XML tree for neuron at element "
					+ e.getElement().getName()
					+ ":\n " + e.getMessage());
      }
    return neuronEl;
  }
}


