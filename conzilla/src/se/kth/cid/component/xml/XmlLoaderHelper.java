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
  private XmlLoaderHelper()
    {}

  /** Loads a color attribute from an XmlElement.
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a color.
   *  @return an int representing the color.
   */
  public static int loadColor(XmlElement el, String attr)
    {
      String colStr = el.getAttribute(attr);

      Color color = (Color) colorTable.get(colStr);
      
      if(color != null)
	return color.getRGB();
      else
	return Integer.decode(colStr).intValue();
    }

  /** Loads a boundingbox element.
   *
   *  @param el the element to load from. It must have the attributes WIDTH and HEIGHT.
   *  @return the Dimension specified in the element.
   *  @exception XmlComponentException if the attributes were invalid.
   */
  public static Dimension loadBoundingBox(XmlElement el)
    throws XmlComponentException
    {
      return new Dimension(loadPositiveInteger(el, "WIDTH", false),
			   loadPositiveInteger(el, "HEIGHT", false));
    }

  /** Loads a position element.
   *
   *  @param el the element to load from. It must have the attributes X and Y.
   *  @return the Point specified in the element.
   *  @exception XmlComponentException if the attributes were invalid.
   */
  public static Point loadPosition(XmlElement el)
    throws XmlComponentException
    {
      return new Point(loadPositiveInteger(el, "X", false),
		       loadPositiveInteger(el, "Y", false));
    }

  /** Loads a number of position elements.
   *
   *  @param lineEl the element to load from. It must have subelements named
   *                "Position" having attributes X and Y.
   *  @return the line specified by the element. Never null, but may be empty.
   *  @exception XmlComponentException if the element was an invalid line.
   */
  public static Point[] loadLine(XmlElement lineEl)
    throws XmlComponentException
    {
      XmlElement[] pointEls = lineEl.getSubElements("Position");

      Point[] points = new Point[pointEls.length];
      
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
   *  @exception XmlComponentException if the attribute was invalid.
   */  
  public static boolean loadBoolean(XmlElement el, String attr)
    throws XmlComponentException
    {
      boolean bool;
      
      String boolStr = el.getAttribute(attr);

      if(boolStr.equals("true"))
	bool = true;
      else if(boolStr.equals("false"))
	bool = false;
      else
	throw new XmlComponentException(attr + "=\"" + boolStr +
					"\", must be 'true' or 'false'");
      return bool;
    }
  
  /** Loads a positive integer attribute.
   *
   *  If allowInf is true, the string "infinity" is interpreted as Integer.MAX_VALUE.
   *
   *  @param el the element to load from.
   *  @param attr the name of the attribute containing a positive integer.
   *  @return the loaded integer.
   *  @exception XmlComponentException if the attribute was invalid.
   */  
  public static int loadPositiveInteger(XmlElement el, String attr,
					boolean allowInf)
    throws XmlComponentException
    {
      int value;
      
      String intStr = el.getAttribute(attr);
      if(intStr.equals("infinity"))
	{
	  if(!allowInf)
	    throw new XmlComponentException(attr + " may not be infinity!");
	  value = Integer.MAX_VALUE;
	}
      else
	value = Integer.parseInt(intStr);
      
      if(value < 0)
	throw new XmlComponentException(attr + "=\"" + intStr +
					"\" is not a valid positive integer!");
      return value;
    }

  /** Loads a multiplicity element.
   *
   *  @param el the element containing the "Multiplicity" sub-element.
   *            The Multiplicity element must have the attributes LOWEST and HIGHEST.
   *  @return an array of size 2 with the specified lowest multiplicity as the first
   *          element, and the highest in the second. The highest may be
   *          Integer.MAX_VALUE.
   *  @exception XmlComponentException if the attributes were invalid.
   */  
  public static int[] loadMultiplicity(XmlElement root)
    throws XmlComponentException
    {
      int[] mult = {0, Integer.MAX_VALUE};
      
      XmlElement[] multEls = root.getSubElements("Multiplicity");
      
      if(multEls.length > 0)
	{
	  mult[0] = loadPositiveInteger(multEls[0], "LOWEST", false);
	  mult[1] = loadPositiveInteger(multEls[0], "HIGHEST", true);

	  if(mult[1] < mult[0])
	    throw
	      new XmlComponentException("Highest multiplicity (" +
					multEls[0].getAttribute("HIGHEST") +
					") is lower than lowest (" +
					multEls[0].getAttribute("LOWEST") +
					")!"); 
	}
      return mult;
    }
  
  /** Builds a multiplicity element.
   *
   *  @param multLow the lowest multiplicity.
   *  @param multHigh the highest multiplicity.
   *  @return a Multiplicity XmlElement.
   */
  public static XmlElement buildMultiplicity(int multLow, int multHigh)
    {
      XmlElement multEl = new XmlElement("Multiplicity");

      multEl.setAttribute("LOWEST", Integer.toString(multLow));

      if(multHigh == Integer.MAX_VALUE)
	multEl.setAttribute("HIGHEST", "infinity");
      else
	multEl.setAttribute("HIGHEST", Integer.toString(multHigh));

      return multEl;
    }

  /** Builds a BoundingBox element.
   *
   *  @param dim the dimension of the boundingbox.
   *  @return a BoundingBox XmlElement.
   */
  public static XmlElement buildBoundingBox(Dimension dim)
    {
      XmlElement el = new XmlElement("BoundingBox");
      el.setAttribute("WIDTH", Integer.toString(dim.width));
      el.setAttribute("HEIGHT", Integer.toString(dim.height));
      return el;
    }

  /** Builds a Position element.
   *
   *  @param p the Point representing the position.
   *  @return a Position XmlElement.
   */
  public static XmlElement buildPosition(Point p)
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
  public static XmlElement buildLine(Point[] line)
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


