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

  /** Main function used for testing purposes.
   *
   *  @param args contains only the file to parse.
   */
  public static void main (String args[]) throws Exception
    {
      Tracer.setLogLevel(Tracer.ALL);
      XmlLoader loader = new XmlLoader(null);

      if (args.length != 1)
	{
	  System.err.println("java XmlLoader <uri>");
	  System.exit(1);
	}
      else
	{
	  XmlDocument doc = loader.parse(FileURL.toURL(args[0]));
	  XmlPrinter printer = new XmlPrinter();
	  printer.print(doc, System.out);
	}
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










