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
import se.kth.cid.xml.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;


import java.awt.*;
import java.util.*;


/** This class contains utility functions for loading and saving
 *  XML components.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlLoaderHelper
{
  /** Table of named colors.
   *  Maps String --> Color.
   */
  static Hashtable colorTable;

  /** Constructing an XmlLoaderHelper is not allowed.
   */
  protected XmlLoaderHelper()
    {}

  public static String loadAttribute(XmlElement el, String attname, String def)
    throws ComponentException
    {
      String att = el.getAttribute(attname);
      if(att == null || att.length() == 0)
	{
	  if(def != null)
	    return def;
	  else
	    throw new ComponentException("Missing " + attname + " in " + el.getName());	    
	}
      return att;
    }

  public static XmlElement getSubElement(XmlElement el, String elname)
    throws ComponentException
    {
      XmlElement[] els = el.getSubElements(elname);
      if(els.length == 0)
	throw new ComponentException("Missing element '" + elname + "'");

      if(els.length > 1)
	throw new ComponentException("Too many '" + elname + "'s");

      return els[0];
    }

  public static XmlElement maybeGetSubElement(XmlElement el, String elname)
    throws ComponentException
    {
      XmlElement[] els = el.getSubElements(elname);
      if(els.length == 0)
	return null;

      if(els.length > 1)
	throw new ComponentException("Too many '" + elname + "'s");
      
      return els[0];
    }
  
      
  /** Loads a color attribute from an XmlElement.
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a color.
   *  @return an int representing the color.
   */
  public static int loadColor(XmlElement el, String attr, String def)
    throws ComponentException
    {
      String colStr = loadAttribute(el, attr, def);

      Color color = (Color) colorTable.get(colStr);
      
      if(color != null)
	return color.getRGB();
      else
	try
	  {
	    return Long.decode(colStr).intValue();
	  } catch(NumberFormatException e)
	    {
	      throw new ComponentException("Illegal color: " + colStr);
	    }
    }

  /** Loads a dimension element.
   *
   *  @param el the element to load from.
   *  It must have the attributes WIDTH and HEIGHT.
   *  @return the Dimension specified in the element.
   *  @exception ComponentException if the attributes were invalid.
   */
  public static ConceptMap.Dimension loadDimension(XmlElement el)
    throws ComponentException
    {
      return new ConceptMap.Dimension(loadPositiveInteger(el, "WIDTH", null, false),
				      loadPositiveInteger(el, "HEIGHT", null, false));
    }

  /** Loads a position element.
   *
   *  @param el the element to load from. It must have the attributes X and Y.
   *  @return the Point specified in the element.
   *  @exception ComponentException if the attributes were invalid.
   */
  public static ConceptMap.Position loadPosition(XmlElement el)
    throws ComponentException
    {
      return new ConceptMap.Position(loadPositiveInteger(el, "X", null, false),
				     loadPositiveInteger(el, "Y", null, false));
    }

  /** Loads a number of position elements.
   *
   *  @param lineEl the element to load from. It must have subelements named
   *                "Position" having attributes X and Y.
   *  @return the line specified by the element. Never null, but may be empty.
   *  @exception XmlComponentException if the element was an invalid line.
   */
  public static ConceptMap.Position[] loadLine(XmlElement lineEl)
    throws ComponentException
    {
      XmlElement[] pointEls = lineEl.getSubElements("Position");

      ConceptMap.Position[] points = new ConceptMap.Position[pointEls.length];
      
      for(int i = 0; i < pointEls.length; i++)
        points[i] = XmlLoaderHelper.loadPosition(pointEls[i]);

      return points;
    }
  
  /** Loads a boolean attribute.
   *
   *  Allowed values are "true" and "false".
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a boolean.
   *  @return the loaded boolean.
   *  @exception ComponentException if the attribute was invalid.
   */  
  public static boolean loadBoolean(XmlElement el, String attr, String def)
    throws ComponentException
    {
      boolean bool;
      
      String boolStr = loadAttribute(el, attr, def);

      if(boolStr.equals("true"))
	bool = true;
      else if(boolStr.equals("false"))
	bool = false;
      else
	throw new ComponentException(attr + "=\"" + boolStr +
				     "\", must be 'true' or 'false'");
      return bool;
    }
  
  /** Loads a positive integer attribute.
   *
   *  If allowInf is true, the string "infinity"
   *  is interpreted as Integer.MAX_VALUE.
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a positive integer.
   *  @return the loaded integer.
   *  @exception ComponentException if the attribute was invalid.
   */  
  public static int loadPositiveInteger(XmlElement el, String attr, String def,
					boolean allowInf)
    throws ComponentException
    {
      int value;

      String intStr = loadAttribute(el, attr, def);
      if(intStr.equals("infinity"))
	{
	  if(!allowInf)
	    throw new ComponentException(attr + " may not be infinity!");
	  value = Integer.MAX_VALUE;
	}
      else
	value = Integer.parseInt(intStr);
      
      if(value < 0)
	throw new ComponentException(attr + "=\"" + intStr +
				     "\" is not a valid positive integer!");
      return value;
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

  /** Returns a hex string representing the given color.
   *
   *  @param color the color to stringify.
   *  @return a hex String representing the color.
   */
  public static String colorString(int color)
    {
      return "0x" + Integer.toHexString(color);
    }
  
  static {
    colorTable = new Hashtable();
    
    colorTable.put("black",     Color.black);
    colorTable.put("blue",      Color.blue);
    colorTable.put("cyan",      Color.cyan);
    colorTable.put("darkGray",  Color.darkGray);
    colorTable.put("gray",      Color.gray);
    colorTable.put("green",     Color.green);
    colorTable.put("lightGray", Color.lightGray);
    colorTable.put("magenta",   Color.magenta);
    colorTable.put("orange",    Color.orange);
    colorTable.put("pink",      Color.pink);
    colorTable.put("red",       Color.red);
    colorTable.put("white",     Color.white);
    colorTable.put("yellow",    Color.yellow);
  }
}


