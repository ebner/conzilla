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

import com.microstar.xml.XmlParser;
import com.microstar.xml.XmlException;
import java.net.*;
import java.util.*;
import java.io.*;

/** This class handles the loading of XML documents.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlLoader
{
  /** The handler to use.
   */
  XmlEventHandler handler;

  /** The parser to use.
   */
  XmlParser parser;
  
  /** Constructs an XmlLoader that uses the given catalog.
   *
   *  @param catalog the catalog to use. May be null.
   */
  public XmlLoader(Catalog catalog)
    {
      handler = new XmlEventHandler(catalog);
      parser = new XmlParser();
      parser.setHandler(handler);
    }

  /** Parses the document in the given URL.
   *
   *  @param url the URL of the document to parse.
   *  @return the parsed document.
   *  @exception XmlLoaderException if something goes wrong when parsing.
   */
  public XmlDocument parse(URL url)
    throws XmlLoaderException
    {
      return parse(url, null);
    }

  /** Parses the document in the given InputStream.
   *
   *  @param is the InputStream containing the document to parse.
   *  @return the parsed document.
   *  @exception XmlLoaderException if something goes wrong when parsing.
   */
  public XmlDocument parse(InputStream is)
    throws XmlLoaderException
    {
      return parse(null, is);
    }

    //FIXME: WARNING, this function fails under signedapplet security-context
    //if the arguments are a url and a null as inputstream.
    //Reason: AElfred opens up a connetion to an unaccessible url, 
    //probably one of the null-pointers givenasarguments is transformed wrongly.
    //The error occurs at row 3296 in com.microstar.xml.XmlParser.java.
  /** Parses the document in the given URL or InputStream.
   *
   *  @param url the URL of the document to parse.
   *  @param is the InputStream containing the document to parse. Used if
   *            the URL is null.
   *  @return the parsed document.
   *  @exception XmlLoaderException if something goes wrong when parsing.
   */
  XmlDocument parse(URL url, InputStream is) throws XmlLoaderException
  {
    try {
      handler.reset();
      if(url != null)
	  parser.parse(url.toString(), null, null, (String)null);
      else
	parser.parse(null, null, is, (String)null);
      return handler.getDocument();
    }
    catch(XmlLoaderException e)
      {
	e.fillInStackTrace();
	throw e;
      }
    catch(XmlException e)
      {
	throw new XmlLoaderException("Error parsing document "
				     + e.getSystemId() + ":"
				     + e.getLine() +":" + e.getColumn()
				     + ":\n " + e.getMessage());
      }
    catch(Exception e)
      {
	throw new XmlLoaderException("Error parsing document: " +
				     e.getMessage());
      }
  }
}










