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
import se.kth.cid.util.*;

import se.kth.cid.component.xml.dtd.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.xml.*;

import java.util.*;
import java.io.*;
import java.awt.Rectangle;
import java.awt.Dimension;


/** The XML handler for ConceptMap components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlConceptMapHandler implements XmlComponentHandler
{
  /** The external entity for the ConceptMap DTD.
   */
  static ExternalEntity entity = new ConceptMapDTD();

  /** Constructs an XmlConceptMapHandler.
   */
  public XmlConceptMapHandler()
  {
  }
  
  public ExternalEntity getDTD()
  {
    return entity;
  }

  /** Loads the ConceptMap.
   *
   *  This method will use the recursiveLoader to load Neurons and NeuronTypes.
   *  This is possibly not the best solution.
   */
  public Component loadComponent(XmlElement root,
				 ComponentLoader loader)
    throws XmlComponentException
  {
    ConceptMap cmap  = new ConceptMap(loader);
    
    XmlElement[] els = root.getSubElements("MetaData");
    XmlMetaDataHandler.load(cmap.getMetaData(), els[0]);
    
      
    els = root.getSubElements("Background");

    cmap.setBackgroundColor(XmlLoaderHelper.loadColor(els[0], "COLOR"));
      
      

    els = root.getSubElements("BoundingBox");

    cmap.setBoundingBox(XmlLoaderHelper.loadBoundingBox(els[0]));


    els = root.getSubElements("MapSet");

    try {      
      if(els.length > 0)
	cmap.setMapSet(new URI(els[0].getAttribute("MAPSETURI")));
      
      
      els = root.getSubElements("NeuronStyle");
      
      for(int i = 0; i < els.length; i++)
	{
	  try {
	    cmap.addNeuronStyle(new URI(els[i].getAttribute("NEURONURI")));
	  }
	  catch(NeuronStyleException e)
	    {
	      throw new XmlComponentException("Invalid NeuronStyle for URI " +
					      els[i].getAttribute("NEURONURI")
					      + ":\n " + e.getMessage());
	    }
	  catch(ComponentException e)
	    {
	      throw new XmlComponentException("Error loading NeuronStyle " +
					      els[i].getAttribute("NEURONURI")
					      + ":\n " + e.getMessage());
	    }
	}
      for(int i = 0; i < els.length; i++)
	{
	  NeuronStyle nStyle =
	    cmap.getNeuronStyle(new URI(els[i].getAttribute("NEURONURI")));
	  
	  XmlElement[] subEls = els[i].getSubElements("Visibility");
	  
	  nStyle.setVisibility(XmlLoaderHelper.loadPositiveInteger(subEls[0],
								   "STRENGTH",
								   true));
	  
	  
	  subEls = els[i].getSubElements("DetailedMap");
	  if(subEls.length > 0)
	    {
	      nStyle.setDetailedMap(new URI(subEls[0].getAttribute("MAPURI")));
	    }
	  
	  subEls = els[i].getSubElements("Box");
	  
	  if(subEls.length > 0)
	    {
	      XmlElement bBoxEl =
		subEls[0].getSubElements("BoundingBox")[0];
	      XmlElement posEl  =
		subEls[0].getSubElements("Position")[0];
	      
	      nStyle.setBoundingBox(new Rectangle(XmlLoaderHelper.loadPosition(posEl),
						  XmlLoaderHelper.loadBoundingBox(bBoxEl)));
	      
	      XmlElement[] titleEls = subEls[0].getSubElements("Title");
	      nStyle.setTitle(titleEls[0].getCDATA());
	      
	      
	      XmlElement[] dataEls = subEls[0].getSubElements("DataTags");
	      if(dataEls.length > 0)
		{
		  XmlElement[] tagEls = dataEls[0].getSubElements("DataTag");
		  for(int j = 0; j < tagEls.length; j++)
		    {
		      String tag = tagEls[j].getAttribute("NAME");
		      nStyle.addDataTag(tag);
		    }
		}
	      
	      XmlElement[] lineEls = subEls[0].getSubElements("Line");
	      if(lineEls.length > 0)
		nStyle.setLine(XmlLoaderHelper.loadLine(lineEls[0]));
	    }
	  
	  subEls = els[i].getSubElements("RoleStyle");
	  
	  for(int j = 0; j < subEls.length; j++)
	    {
	      RoleStyle roleStyle;
	      try {
		roleStyle = nStyle.addRoleStyle(subEls[j].getAttribute("TYPE"),
						new URI(subEls[j].getAttribute("NEURONURI")));
	      } catch(RoleStyleException e)
		{
		  throw new XmlComponentException("Error adding role style with type " +
						  subEls[j].getAttribute("TYPE")
						  + " and URI " + subEls[j].getAttribute("NEURONURI")
						  + ":\n " + e.getMessage());
		}
	      XmlElement[] lineEls = subEls[j].getSubElements("Line");
	      roleStyle.setLine(XmlLoaderHelper.loadLine(lineEls[0]));
	    }
	  
	}
    } catch (MalformedURIException e)
      {
	throw new XmlComponentException("Invalid URI in conceptmap: "
					+ e.getURI() + ":\n " + e.getMessage());
      }
    
    return cmap;
  }
  
  public XmlElement buildXmlTree(Component comp)
    throws XmlComponentException
  {
    if(! (comp instanceof ConceptMap))
      throw new XmlComponentException("Component is no ConceptMap!");

    ConceptMap cmap = (ConceptMap) comp;
    
    XmlElement cmapEl = new XmlElement("ConceptMap");

    try {
      cmapEl.addSubElement(XmlMetaDataHandler.buildXmlTree(cmap.getMetaData()));
      
      XmlElement backEl = new XmlElement("Background");
      backEl.setAttribute("COLOR", XmlLoaderHelper.colorString(cmap.getBackgroundColor()));
      cmapEl.addSubElement(backEl);
      
      cmapEl.addSubElement(XmlLoaderHelper.buildBoundingBox(cmap.getBoundingBox()));
      
      URI mapSet = cmap.getMapSet();
      if(mapSet != null)
	{
	  XmlElement mapsetEl= new XmlElement("MapSet");
	  mapsetEl.setAttribute("MAPSETURI", mapSet.toString());
	  cmapEl.addSubElement(mapsetEl);
	}
      
      Enumeration nStyles = cmap.getNeuronStyles();
      
      for(; nStyles.hasMoreElements(); )
	{
	  NeuronStyle nStyle = (NeuronStyle) nStyles.nextElement();
	  
	  XmlElement nStyleEl = new XmlElement("NeuronStyle");
	  nStyleEl.setAttribute("NEURONURI", nStyle.getNeuron().getURI());
	  
	  XmlElement visEl = new XmlElement("Visibility");
	  visEl.setAttribute("STRENGTH", Integer.toString(nStyle.getVisibility()));
	  nStyleEl.addSubElement(visEl);
	  
	  URI detailedMap = nStyle.getDetailedMap();
	  if(detailedMap != null)
	    {
	      XmlElement detailedMapEl= new XmlElement("DetailedMap");
	      detailedMapEl.setAttribute("MAPURI", detailedMap.toString());
	      nStyleEl.addSubElement(detailedMapEl);
	    }
	  
	  if(nStyle.getBoundingBox() != null)
	    {
	      XmlElement boxEl = new XmlElement("Box");
	      boxEl.addSubElement(XmlLoaderHelper.buildBoundingBox(nStyle.getBoundingBox().getSize()));
	      boxEl.addSubElement(XmlLoaderHelper.buildPosition(nStyle.getBoundingBox().getLocation()));
	      
	      XmlElement titleEl = new XmlElement("Title");
	      titleEl.setCDATA(nStyle.getTitle());
	      boxEl.addSubElement(titleEl);

	      Vector datatags = nStyle.getDataTags();
	      if(datatags.size() > 0)
		{
		  String[] tags = new String[datatags.size()];
		  datatags.copyInto(tags);
		  boxEl.addSubElement(XmlLoaderHelper.buildDataTags(tags));
		}

	      if(nStyle.getLine() != null && nStyle.getLine().length > 0)
		boxEl.addSubElement(XmlLoaderHelper.buildLine(nStyle.getLine()));
	      nStyleEl.addSubElement(boxEl);
	    }

	  Vector roleStyles = nStyle.getRoles();
	  for(int i = 0; i < roleStyles.size(); i++)
	    {
	      RoleStyle roleStyle = (RoleStyle) roleStyles.elementAt(i);
	      XmlElement roleStyleEl = new XmlElement("RoleStyle");
	      roleStyleEl.setAttribute("TYPE", roleStyle.getRoleType().type);
	      roleStyleEl.setAttribute("NEURONURI", roleStyle.getRolePlayer().getNeuron().getURI());
	      roleStyleEl.addSubElement(XmlLoaderHelper.buildLine(roleStyle.getLine()));
	      nStyleEl.addSubElement(roleStyleEl);
	    }
	  
	  cmapEl.addSubElement(nStyleEl);
	}
    }
    catch(XmlElementException e)
      {
	Tracer.trace("Error building XML tree for conceptmap at element "
		     + e.getElement().getName() + ": " + e.getMessage(),
		     Tracer.ERROR);
	throw new XmlComponentException("Error building XML tree for conceptmap at element "
					+ e.getElement().getName()
					+ ":\n " + e.getMessage());
      }
      
    return cmapEl;
  }
}


