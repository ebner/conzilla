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

import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conceptmap.local.*;
import se.kth.cid.identity.*;
import se.kth.cid.xml.*;

import java.util.*;
import java.io.*;


/** The XML handler for ConceptMap components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlConceptMapHandler extends XmlLoaderHelper implements XmlComponentHandler
{
  /** Constructs an XmlConceptMapHandler.
   */
  public XmlConceptMapHandler()
  {
  }
  
  /** Loads the ConceptMap.
   *
   */
  public LocalComponent loadComponent(XmlElement root, URI origuri, URI loadURI)
    throws ComponentException
  {
    LocalConceptMap cmap  = new LocalConceptMap(origuri, loadURI, MIMEType.XML);
    
    
    try {
    XmlElement dim = getSubElement(root, "Dimension");
    cmap.setDimension(loadDimension(dim));


    XmlElement[] els = root.getSubElements("NeuronStyle");
    
    for(int i = 0; i < els.length; i++)
      {
	try {
	  cmap.addNeuronStyle(loadAttribute(els[i], "ID", null),
			      loadAttribute(els[i], "NEURONURI", null));
	}
	catch(InvalidURIException e)
	  {
	    throw new XmlStructureException("Invalid URI " +
					 els[i].getAttribute("NEURONURI")
					 + ":\n " + e.getMessage());
	  }
	catch(ConceptMapException e)
	  {
	      throw new XmlStructureException("Invalid NeuronStyle for URI " +
					   els[i].getAttribute("NEURONURI")
					   + ":\n " + e.getMessage());
	  }
      }
    
    for(int i = 0; i < els.length; i++)
      {
	NeuronStyle nStyle =
	  cmap.getNeuronStyle(loadAttribute(els[i], "ID", null));

	try {
	  XmlElement detMap = maybeGetSubElement(els[i], "DetailedMap");
	  if(detMap != null)
	    nStyle.setDetailedMap(loadAttribute(detMap, "MAPURI", null));
	}
	catch(InvalidURIException e)
	  {
	    throw new XmlStructureException("Invalid URI " +
					 e.getURI()
					 + ":\n " + e.getMessage());
	  }

	XmlElement box = getSubElement(els[i], "BoxStyle");

	nStyle.setBodyVisible(loadBoolean(box, "VISIBLE", "true"));
	
	XmlElement bBoxEl = getSubElement(box, "Dimension");
	XmlElement posEl  = getSubElement(box, "Position");
	
	nStyle.setBoundingBox(new ConceptMap.BoundingBox(loadDimension(bBoxEl),
							 loadPosition(posEl)));
	
	XmlElement dataTags = maybeGetSubElement(box, "DataTagStyles");
	if(dataTags != null)
	  {
	    XmlElement[] tagEls = dataTags.getSubElements("DataTagStyle");
	    for(int j = 0; j < tagEls.length; j++)
	      {
		String tag = loadAttribute(tagEls[j], "NAME", null);
		nStyle.addDataTag(tag);
	      }
	  }
	
	XmlElement lineEl = maybeGetSubElement(box, "Line");
	if(lineEl != null)
	  nStyle.setLine(loadLine(lineEl));

	XmlElement[] subEls = els[i].getSubElements("AxonStyle");
	
	AxonStyle axonStyle;
	for(int j = 0; j < subEls.length; j++)
	  {
	    try {
	      String idref = loadAttribute(subEls[j], "NEURONSTYLE", null);
	      NeuronStyle player = cmap.getNeuronStyle(idref);
	      if(player == null)
		throw new XmlStructureException("Invalid IDREF in conceptmap: " + idref);
	      
	      axonStyle = nStyle.addAxonStyle(loadAttribute(subEls[j], "AXONID", null), player);
	    } catch(ConceptMapException e)
	      {
		throw new XmlStructureException("Error adding axon style with id " +
					     subEls[j].getAttribute("AXONID")
					     + " and ID " + subEls[j].getAttribute("ID")
					     + ":\n " + e.getMessage());
	      }

	    lineEl = getSubElement(subEls[j], "Line");
	    axonStyle.setLine(loadLine(lineEl));
	    
	    dataTags = maybeGetSubElement(subEls[j], "DataTagStyles");
	    if(dataTags != null)
	      {
		XmlElement[] tagEls = dataTags.getSubElements("DataTagStyle");
		for(int k = 0; k < tagEls.length; k++)
		  {
		    String tag = loadAttribute(tagEls[k], "NAME", null);
		    nStyle.addDataTag(tag);
		  }
	      }
	  }
      }
    } catch (XmlStructureException e)
	{
	    throw new ComponentException(e.getMessage());
	}

    return cmap;
  }
  
  public XmlElement buildXmlTree(Component comp)
    throws ComponentException 
    {
      if(! (comp instanceof ConceptMap))
	throw new ComponentException("Component is no ConceptMap!");
      
      ConceptMap cmap = (ConceptMap) comp;
      
      XmlElement cmapEl = new XmlElement("ConceptMap");
      
      try {
	cmapEl.addSubElement(buildDimension(cmap.getDimension()));
	
	NeuronStyle[] nStyles = cmap.getNeuronStyles();
	
	for(int i = 0; i < nStyles.length; i++)
	  {
	    NeuronStyle nStyle = nStyles[i];
	    
	    XmlElement nStyleEl = new XmlElement("NeuronStyle");
	    nStyleEl.setAttribute("NEURONURI", nStyle.getNeuronURI());
	    nStyleEl.setAttribute("ID", nStyle.getID());

	    if(nStyle.getDetailedMap() != null)
	      {
		XmlElement detMapEl = new XmlElement("DetailedMap");
		detMapEl.setAttribute("MAPURI", nStyle.getDetailedMap());
		nStyleEl.addSubElement(detMapEl);
	      }
	    
	    XmlElement boxEl = new XmlElement("BoxStyle");
	    boxEl.addSubElement(buildDimension(nStyle.getBoundingBox().dim));
	    boxEl.addSubElement(buildPosition(nStyle.getBoundingBox().pos));

	    boxEl.setAttribute("VISIBLE", nStyle.getBodyVisible() ? "true" : "false");
	    
	    String[] datatags = nStyle.getDataTags();
	    if(datatags.length > 0)
	      boxEl.addSubElement(buildDataTagStyles(datatags));
	    
	    if(nStyle.getLine() != null && nStyle.getLine().length > 0)
	      boxEl.addSubElement(buildLine(nStyle.getLine()));
	    nStyleEl.addSubElement(boxEl);
	    
	    AxonStyle[] axonStyles = nStyle.getAxonStyles();
	    for(int j = 0; j < axonStyles.length; j++)
	      {
		AxonStyle axonStyle = axonStyles[j];
		XmlElement axonStyleEl = new XmlElement("AxonStyle");
		axonStyleEl.setAttribute("AXONID", axonStyle.getAxonID());
		axonStyleEl.setAttribute("NEURONSTYLE", axonStyle.getEnd().getID());
		axonStyleEl.addSubElement(buildLine(axonStyle.getLine()));
		datatags = axonStyle.getDataTags();
		if(datatags.length > 0)
		  axonStyleEl.addSubElement(buildDataTagStyles(datatags));
		nStyleEl.addSubElement(axonStyleEl);
	      }
	    
	    cmapEl.addSubElement(nStyleEl);
	  }
      }
      catch(XmlElementException e)
	{
	  Tracer.trace("Error building XML tree for conceptmap at element "
		       + e.getElement().getName() + ": " + e.getMessage(),
		       Tracer.ERROR);
	  throw new ComponentException("Error building XML tree for conceptmap at element "
				       + e.getElement().getName()
				       + ":\n " + e.getMessage());
	}
      
      return cmapEl;
    }

  /** Loads a dimension element.
   *
   *  @param el the element to load from.
   *  It must have the attributes WIDTH and HEIGHT.
   *  @return the Dimension specified in the element.
   *  @exception XmlStructureException if the attributes were invalid.
   */
  public static ConceptMap.Dimension loadDimension(XmlElement el)
    throws se.kth.cid.xml.XmlStructureException
    {
      return new ConceptMap.Dimension(loadPositiveInteger(el, "WIDTH", null, false),
				      loadPositiveInteger(el, "HEIGHT", null, false));
    }

  /** Loads a position element.
   *
   *  @param el the element to load from. It must have the attributes X and Y.
   *  @return the Point specified in the element.
   *  @exception XmlStructureException if the attributes were invalid.
   */
  public static ConceptMap.Position loadPosition(XmlElement el)
    throws XmlStructureException
    {
      return new ConceptMap.Position(loadPositiveInteger(el, "X", null, false),
				     loadPositiveInteger(el, "Y", null, false));
    }

  /** Loads a number of position elements.
   *
   *  @param lineEl the element to load from. It must have subelements named
   *                "Position" having attributes X and Y.
   *  @return the line specified by the element. Never null, but may be empty.
   *  @exception XmlXmlStructureException if the element was an invalid line.
   */
  public static ConceptMap.Position[] loadLine(XmlElement lineEl)
    throws XmlStructureException
    {
      XmlElement[] pointEls = lineEl.getSubElements("Position");

      ConceptMap.Position[] points = new ConceptMap.Position[pointEls.length];
      
      for(int i = 0; i < pointEls.length; i++)
        points[i] = loadPosition(pointEls[i]);

      return points;
    }

  /** Builds a Dimension element.
   *
   *  @param dim the dimension of the boundingbox.
   *  @return a BoundingBox XmlElement.
   */
  public static XmlElement buildDimension(ConceptMap.Dimension dim)
    {
      XmlElement el = new XmlElement("Dimension");
      el.setAttribute("WIDTH", Integer.toString(dim.width));
      el.setAttribute("HEIGHT", Integer.toString(dim.height));
      return el;
    }

  /** Builds a Position element.
   *
   *  @param p the Point representing the position.
   *  @return a Position XmlElement.
   */
  public static XmlElement buildPosition(ConceptMap.Position p)
    {
      XmlElement el = new XmlElement("Position");
      el.setAttribute("X", Integer.toString(p.x));
      el.setAttribute("Y", Integer.toString(p.y));
      return el;
    }

  /** Builds a Line element.
   *
   *  @param line the array of Points representing the line.
   *  @return a Line XmlElement.
   */
  public static XmlElement buildLine(ConceptMap.Position[] line)
    throws XmlElementException
    {
      XmlElement el = new XmlElement("Line");
      for(int i = 0; i < line.length; i++)
	el.addSubElement(buildPosition(line[i]));
      return el;
    }

  /** Builds a DataTagStyles element.
   *
   *  @param tags the DataTagStyles wanted in the element.
   *  @return a DataTags XmlElement.
   */
  public static XmlElement buildDataTagStyles(String[] tags)
    throws XmlElementException
  {
    XmlElement dataEl = new XmlElement("DataTagStyles");
    
    for(int i = 0; i < tags.length; i++)
      {
	XmlElement dataTagEl = new XmlElement("DataTagStyle");
	dataTagEl.setAttribute("NAME", tags[i]);
	dataEl.addSubElement(dataTagEl);
      }
    return dataEl;
  }
}


