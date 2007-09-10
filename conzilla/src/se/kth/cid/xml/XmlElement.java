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


package se.kth.cid.xml;

import se.kth.cid.util.*;
import java.util.*;

/** This class represents a node in a very simplified DOM tree.
 *  The documents this class represents are subject to a number of constraints:
 *
 *  1. An element cannot have mixed CDATA and sub-elements. All CDATA will be concatenated.
 *  2. The order of elements is preserved amongst elements with the same name.
 *     However, no order is preserved between elements of different names.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlElement
{
  /** The parent element. May be null.
   */
  XmlElement parent;

  /** The name of this element.
   */
  String name;
  
  /** Vector of attribute names.
   */
  Vector attributes;

  /** The attribute values.
   *  Maps attribute name -> attibute value.
   */
  Hashtable attributeValues;
  
  /** Vector containing Vectors of sub-elements, each such vector containing all elements
   *  of the same name
   */
  Vector elements;

  /** Table mapping element name --> vector containing all elements of that name.
   */
  Hashtable elementVectors;

  /** Cache for all element names.
   */
  String[] elementNames;
  
  /** The CDATA of this element.
   */
  String cdata;

  /** If this is true, all allowed sub-elements of this element are already known.
   */
  boolean knownSubElements = false;

  /** Constructs an XmlElement with the given name.
   *
   * @param nname the name of this XmlElement.
   */
  public XmlElement(String nname)
    {
      name = nname;
      cdata = "";
      
      knownSubElements = false;
      
      attributes = new Vector();
      attributeValues = new Hashtable(20);
      
      elements = new Vector();
      elementVectors = new Hashtable(20);
    }
  
  /** Constructs an XmlElement with the given name and known sub-element names.
   *
   *  This constructor is very useful when you know in advance which sub-element names
   *  will exist in this element, but want to be sure in which order they are presented.
   *
   * @param nname the name of this XmlElement.
   * @param subElements the names of the allowed sub-elements.
   */
  public XmlElement(String nname, String[] subElements)
    {
      name = nname;
      cdata = "";
      
      knownSubElements = true;
      
      attributes = new Vector();
      attributeValues = new Hashtable(20);
      
      elements = new Vector(subElements.length);
      elementVectors = new Hashtable(subElements.length);
      
      for(int i = 0; i < subElements.length; i++)
	{
	  Vector newEl = new Vector(0);
	  elements.addElement(newEl);
	  elementVectors.put(subElements[i], newEl);
	} 
    }

  /** Returns the parent of this element.
   *
   *  @return the parent of this element. May be null.
   */
  public XmlElement getParent()
    {
      return parent;
    }

  /** Returns the name of this element.
   *
   *  @return the name of this element.
   */  
  public String getName()
    {
      return name;
    }
  
  //////////////////////////////////////////////////////////
  //----------------------CDATA---------------------------//
  //////////////////////////////////////////////////////////  

  /** Returns the CDATA of this element.
   *
   *  @return the CDATA of this element. May be null.
   */
  public String getCDATA() 
    {
      return cdata;
    }

  /** Sets the CDATA of this element.
   *
   *  @param ncdata the CDATA of this element.
   *  @exception HasSubElementsException if this element already has sub-elements.
   */
  public void setCDATA(String ncdata)
    {
      if (ncdata != null)
	cdata = ncdata;   // .trim(); removed. Unnecessary policy!
      else 
	cdata = "";
    }

  /** Adds CDATA to this element.
   *
   *  @param ncdata the additional CDATA of this element.
   */
  public void addCDATA(String ncdata)
    {
      if (ncdata != null)
	cdata = cdata + ncdata; //.trim(); removed. Unnecessary policy!
    }
  
  
  //////////////////////////////////////////////////////////
  //----------------------ELEMENTS------------------------//
  //////////////////////////////////////////////////////////  

  /** Returns the names of all sub-elements.
   *
   *  If the names were specified in the constructor, they are returned in
   *  that order.
   *  Otherwise, they are returned in the order they have been added.
   *
   *  @return the names of all sub-elements. Never null, but may be empty.
   */
  public String[] getSubElementNames()
    {
      if(elementNames == null)
	{
	  Vector elNames = new Vector();
	  
	  for(int i = 0; i < elements.size(); i++)
	    {
	      Vector nameEls = (Vector) elements.elementAt(i);
	      
	      String elName = ((XmlElement) nameEls.elementAt(0)).getName();
	      elNames.addElement(elName);
	    }
	  elementNames = new String[elNames.size()];
	  elNames.copyInto(elementNames);
	}
      
      return elementNames;
    }


  public int getSubElementNumber()
    {
      int size = 0;

      for(int i = 0; i < elements.size(); i++)
	{
	  Vector nameEls = (Vector) elements.elementAt(i);
	  size += nameEls.size();
	}
      return size;
    }
      
  
  /** Returns all sub-elements with the given name.
   *
   * @param elname the name of the elements wanted.
   * @return  all sub-elements with the given name. Never null, but may be empty.
   */
  public XmlElement[] getSubElements(String elname)
  {
    Vector tagelements = (Vector) elementVectors.get(elname);
    if (tagelements != null)
      {
	XmlElement[] els = new XmlElement[tagelements.size()];
	tagelements.copyInto(els);
	return els;
      }
    else  // Create an empty array.
      return new XmlElement[0];
  }

  /** Adds a sub-element to this element.
   *
   *  This is not allowed if the known sub-elements were specified in the constructor
   *  and this sub-element is not among them.
   *
   *  @param el the element to add.
   */
  public void addSubElement(XmlElement el)
    throws UnknownElementNameException
    {
      Vector tagels = (Vector) elementVectors.get(el.getName());
      if(tagels != null)
	tagels.addElement(el);
      else
	{
	  if(knownSubElements)
	    throw new UnknownElementNameException(el.getName(), this);
	  else
	    {
	      tagels = new Vector();
	      tagels.addElement(el);
	      elements.addElement(tagels);
	      elementVectors.put(el.getName(), tagels);
	      elementNames = null;
	    }
	}
      el.parent = this;
    }

  /** Removes the specified sub-element from this element.
   *
   * @param el the sub-element to remove.
   */
  public void removeSubElement(XmlElement el)
    {
      Vector tagels = (Vector) elementVectors.get(el.getName());
      if(tagels != null)
	{
	  tagels.removeElement(el);
	  if(!knownSubElements)
	    {
	      elementNames = null;
	      if(tagels.size() == 0)
		{
		  elements.removeElement(tagels);
		  elementVectors.remove(el.getName());
		}
	    }
	}
    }
  
  //////////////////////////////////////////////////////////
  //--------------------ATTRIBUTES------------------------//
  //////////////////////////////////////////////////////////

  /** Returns the attribute names of this element.
   *
   * @return the attribute names of this element.
   */
  public String[] getAttributes() 
    {
      String[] attrs = new String[attributes.size()];
      attributes.copyInto(attrs);
      return attrs;
    }

  /** Returns the value of the attribute with the given name.
   *
   *  @param attr the attribute of interest.
   *  @return the value of the attribute with the given name.
   */
  public String getAttribute(String attr) 
    {
      return (String) attributeValues.get(attr);
    }

  /** Sets the given attribute to the given value.
   *
   *  If value is null, the attribute is removed.
   *
   * @param attribute the attribute to modify.
   * @param value the value of the attribute, or null if the attribute should be removed.
   */
  public void setAttribute(String attribute, String value)
    {
      if(attributeValues.get(attribute) != null)
	{
	  if(value != null)
	    attributeValues.put(attribute, value);
	  else
	    {
	      attributeValues.remove(attribute);
	      attributes.removeElement(attribute);
	    }
	}
      else if(value != null)
	{
	  attributeValues.put(attribute, value);
	  attributes.addElement(attribute);
	}
    }

  public String toString()
  {
    String str = "<" + getName();
    String[] attribs = getAttributes();
    
    for(int i = 0; i < attribs.length; i++)
      {
	str += " " + attribs[i] + "=\"" + 
	  getAttribute(attribs[i]) + "\"";
      }
    str += ">";
    if(getCDATA() != null)
      str += getCDATA();
    
    String[] subels = getSubElementNames();
    for(int j = 0; j < subels.length; j++)
      {
	XmlElement[] els = getSubElements(subels[j]);
	for(int k = 0; k < els.length; k++)
	  str += els[k].toString();
	  }
    
    str += "</" + getName() + ">";
    
    return str;
  }
}





