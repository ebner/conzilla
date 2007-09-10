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


import java.io.*;
import com.microstar.xml.*;
import java.util.*;
import java.net.*;
 

/** This class handles the parse events that AElfred generates. 
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
class XmlEventHandler extends HandlerBase
{
  /** The catalog to use to look up external entities.
   */
  Catalog catalog;

  /** Contains the attributes for the current element.
   */
  Vector attributes;

  /** Contains the attribute values for the current element.
   */
  Vector attributeValues;

  /** The document.
   */
  XmlDocument doc;

  /** The current element.
   */
  XmlElement current;


  /** Constructs an XmlEventHandler using the given catalog.
   *
   *  @param ncatalog the catalog to use. May be null, in which case AElfred
   *                  itself will resolve external entities.
   */
  public XmlEventHandler(Catalog ncatalog)
    {
      catalog = ncatalog;
      attributes = new Vector();
      attributeValues = new Vector();
      reset();
    }

  /** Resets the handler, making it ready for a new document.
   *
   */ 
  public void reset()
    {
      doc = new XmlDocument();
      current = null;
      
      attributes.setSize(0);
      attributeValues.setSize(0);
    }

  /** Returns the parsed XML document.
   *  Not valid before entire document has been parsed.
   *
   * @return the parsed XML document.
   */
  public XmlDocument getDocument()
    {
      return doc;
    }

  public void attribute (String aname, String value, boolean isSpecified)
    throws java.lang.Exception
    {
      //      Tracer.trace("XmlEventHandler: got Attribute: " + aname +
      //		   "=\"" + value + "\"", Tracer.DETAIL + 1);
      attributes.addElement(aname);
      attributeValues.addElement(value);
    }

  public void startElement (String elname)
    throws java.lang.Exception
    {
      //      Tracer.trace("XmlEventHandler: got startElement: " + elname,
      //		   Tracer.DETAIL + 1);

      XmlElement parent = current;

      current = new XmlElement(elname);
      if(parent != null)
	parent.addSubElement(current);
      else
	doc.setRoot(current);
      
      for(int i = 0; i < attributes.size(); i++)
	current.setAttribute((String) attributes.elementAt(i),
			     (String) attributeValues.elementAt(i));

      attributes.setSize(0);
      attributeValues.setSize(0);
    }

  public void endElement (String elname)
    throws java.lang.Exception
    {
      //      Tracer.trace("XmlEventHandler: got endElement: " + elname,
      //		   Tracer.DETAIL + 1);

      current = current.getParent();
    }

  public void charData (char ch[], int start, int length)
    throws java.lang.Exception
    {
      //      Tracer.trace("XmlEventHandler: got CDATA: \"" +
      //		   new String(ch, start, length) + "\"", Tracer.DETAIL + 1);

      current.addCDATA(new String(ch, start, length));
    }

  public void doctypeDecl (String docType, String pubID, String sysID)
    throws java.lang.Exception
    {
      //      Tracer.trace("XmlEventHandler: got doctypeDecl: \"" +
      //		   docType + " " + pubID + " " + sysID + "\"",
      //		   Tracer.DETAIL + 1);
      doc.setSystemDTDId(sysID);
      doc.setPublicDTDId(pubID);
    }

  public void processingInstruction (String piName, String piData)
    throws java.lang.Exception
    {
//	Tracer.trace("XmlEventHandler: got PI: " + piName + ": \"" +
//		     piData + "\"",
//		     Tracer.DETAIL + 1);
      doc.setProcessingInstruction(piName, piData);
    }

  public Object resolveEntity (String publicId, String systemId)
    throws java.lang.Exception
    {
//	Tracer.trace("XmlEventHandler: resolving \"" + publicId + "\", \""
//		     + systemId + "\"",
//		     Tracer.MAJOR_INT_EVENT);


      if(catalog == null)
	return null;
      
      ExternalEntity ent;
      ent = catalog.resolveEntity(publicId, systemId, null); 
      

// This really, really, should be done in the catalog!
//	if(ent == null)
//	  {
//	    URLConnection conn = (new URL(systemId)).openConnection();
//	    conn.connect();
//	    return conn.getInputStream();
//	  }
//
      if(ent == null)
	return new StringReader("");
      
      Object entity = ent.getEntity();

      if(!(entity instanceof Reader) && !(entity instanceof InputStream))
	throw new XmlLoaderException("Entity  PUBLIC \""
				     + publicId + "\" SYSTEM \""
				     + systemId + "\" did not return"
				     + " a Reader or an InputStream");
      return entity;
    }
}
