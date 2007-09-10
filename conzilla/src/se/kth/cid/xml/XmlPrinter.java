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
import java.util.*;



/** This class prints XML files on an OutputStream.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlPrinter
{
  /** The encoding to use.
   *  The encodings are specified as in XML.
   */
  String xmlEncoding;

  /** The Java encoding to use.
   */
  String javaEncoding;

  /** Whether the documents printed should be standalone.
   *  This could be moved to XmlDocument.
   */
  boolean standalone;


  /** Comments to add to printed documents.
   */
  Vector comments;

  /** Maps XML encoding name --> Java encoding name.
   */
  static Hashtable xmltoJavaEncodings;

  /** Constructs an XmlPrinter using the encoding
   *  "ISO-8859-1".
   */
  public XmlPrinter()
    {
      try {
	setEncoding("ISO-8859-1");
      } catch(UnsupportedEncodingException e)
	{
	  Tracer.trace("Default encoding ISO-8859-1 not supported!",
		       Tracer.ERROR);
	  throw new Error("Default encoding ISO-8859-1 not supported!");
	}
      comments = new Vector();

      standalone = false;
    }

  /** Sets the encoding to use.
   *  
   *  The encodings are specified as in XML.
   *  Supported are ISO-8859-[1-9] and UTF-8.
   *
   *  @param nencoding the encoding to use.
   *  @exception UnsupportedEncodingException if the exception
   *             is not supported (by XML or by Java).
   */
  public void setEncoding(String nencoding)
    throws UnsupportedEncodingException
    {
      String javaEnc = (String) xmltoJavaEncodings.get(nencoding);
      
      if(javaEnc == null)
	throw new UnsupportedEncodingException(nencoding);

      new OutputStreamWriter(new ByteArrayOutputStream(10), javaEnc);
      
      xmlEncoding = nencoding;
      javaEncoding = javaEnc;
    }

  /** Sets the standalone status of printed documents.
   *
   *  @param nstandalone whether printed documents should be standalone.
   */
  public void setStandalone(boolean nstandalone)
    {
      standalone = nstandalone;
    }

  /** Adds a comment to all printed documents.
   *
   *  @param comment the comment string. May not contain "-->".
   *  @exception XmlPrinterException if the comment includes "-->".
   */
  public void addComment(String comment)
    throws XmlPrinterException
    {
      if(comment.indexOf("-->") != -1)
	throw new XmlPrinterException("Illegal to use \"-->"
				      + "\" inside comments!");
      comments.addElement(comment);
    }


  /** Prints an XmlDocument on an OutputStream.
   *
   *  @param doc the document to print.
   *  @param os  the stream to print to.
   */
  public void print(XmlDocument doc, OutputStream os)
    {
      PrintWriter printer;

      try {
	printer = new PrintWriter(new OutputStreamWriter(os, javaEncoding));
      } catch (UnsupportedEncodingException e)
	{
	  Tracer.trace("Previously supported encoding " + javaEncoding
		       + " suddenly not supported!", Tracer.ERROR);
	  throw new Error("Previously supported encoding " + javaEncoding
			  + " suddenly not supported!");
	}

      printHeader(printer, doc);
      printHelper(printer, doc.getRoot(), 0);
      printer.close();
    }



  /** Prints the header of an XmlDocument.
   *
   * @param printer the PrintWriter to print to.
   * @param doc the document to print.
   */
  void printHeader(PrintWriter printer, XmlDocument doc)
    {
      printer.print("<?xml version=\"1.0\" encoding=\""
		    + xmlEncoding + "\"");
      if(standalone)
	printer.print(" standalone=\"yes\"");
      printer.print("?>\n");

      String sysID = doc.getSystemDTDId();
      String pubID = doc.getPublicDTDId();
      
      if(sysID != null)
	{
	  printer.print("<!DOCTYPE " + doc.getRoot().getName());
	  if(pubID != null)
	    printer.print(" PUBLIC \"" + pubID + "\" \""
			  + sysID + "\"");
	  else
	    printer.print(" SYSTEM \"" + sysID + "\"");
	  printer.print(">\n");
	}
      String[] piNames = doc.getProcessingInstructions();
      
      for(int i = 0; i < piNames.length; i++)
	{
	  String pi = doc.getProcessingInstruction(piNames[i]);
	  printer.print("<?" + piNames[i] + " " + pi + " ?>\n");
	}

      for(int i = 0; i < comments.size(); i++)
	{
	  String comment = (String) comments.elementAt(i);
	  printer.print("<!-- " + comment + " -->\n");
	}
    }
  
  /** Returns the indentation space for a given level.
   *
   *  The level numbering is 0 for the root element.
   *
   * @param level the level to indent.
   */
  String spaces(int level)
    {
      char[] spaces = new char[2*level];
      for (int i = 0; i < 2*level; i++)
	spaces[i] = ' ';
      return new String(spaces);
    }  

  /** Prints one XmlElement and its sub-elements recursively.
   *
   * @param printer the PrintWriter to print to.
   * @param el the element to print.
   * @param level the indentation level of this element.
   */
  void printHelper(PrintWriter printer, XmlElement el, int level)
    {

      printer.print(spaces(level) + "<" + el.getName());
	      
      String[] attribs = el.getAttributes();
      
      for(int i = 0; i < attribs.length; i++)
	{
	  printer.print(" " + attribs[i] + "=\"" + 
			el.getAttribute(attribs[i]) + "\"");
	}

      boolean empty = false;
      
      String cdata = el.getCDATA();

      String[] subNames = el.getSubElementNames();

      if(subNames.length == 0 && cdata.length() == 0)
	empty = true;

      if(empty)
	printer.print("/>\n");
      else
	{
	  printer.print(">");

	  if(cdata.length() > 0)
	    printer.print(cdata);

	  if(subNames.length > 0)
	    {
	      printer.print("\n");
	      
	      for(int i = 0; i < subNames.length; i++)
		{
		  XmlElement[] subEls = el.getSubElements(subNames[i]);
		  for (int j = 0; j < subEls.length; j++)
		    printHelper(printer, subEls[j], level + 1);
		}
	      printer.print(spaces(level));
	    }
	  printer.print("</" + el.getName() + ">\n");
	}
    }

  /* Initializes the static Xml encoding to Java encoding table.
   */
  static
    {
      xmltoJavaEncodings = new Hashtable();
      xmltoJavaEncodings.put("ISO-8859-1", "8859_1");
      xmltoJavaEncodings.put("ISO-8859-2", "8859_2");
      xmltoJavaEncodings.put("ISO-8859-3", "8859_3");
      xmltoJavaEncodings.put("ISO-8859-4", "8859_4");
      xmltoJavaEncodings.put("ISO-8859-5", "8859_5");
      xmltoJavaEncodings.put("ISO-8859-6", "8859_6");
      xmltoJavaEncodings.put("ISO-8859-7", "8859_7");
      xmltoJavaEncodings.put("ISO-8859-8", "8859_8");
      xmltoJavaEncodings.put("ISO-8859-9", "8859_9");

      xmltoJavaEncodings.put("UTF-8", "UTF8");      
    }
}


    
    

    
