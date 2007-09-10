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


package se.kth.cid.identity.pathurn;

import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.xml.*;

/** This is a helper class for TableResolver.
 *
 *  It is used to load a TableResolvers's table from an XML file.
 *  The XML file must  like:
 *  <Paths>
 *    <Path NAME="/se/kth/cid" BASEURL="http://www.nada.kth.se/cid/"
 *          TYPE="text/xml"/>
 *    ...
 *  </Paths>
 *
 *  Please note that the XML doc must refer to the DTD
 *  via known URL or be standalone (with optional included DTD).
 *  An example header would be:
 *  <?xml version="1.0" encoding="ISO-8859-1" standalone="yes"?>
 *  <!DOCTYPE Paths 
 *  [<!ELEMENT Paths (Path*)>
 *   <!ELEMENT Path    EMPTY>
 *   <!ATTLIST Path
 *      NAME            CDATA #REQUIRED
 *      BASEURI         CDATA #REQUIRED
 *      TYPE            CDATA #REQUIRED>]>
 *
 *  Doing without a DTD is theoretically possible, but will not work because of
 *  assumptions regarding CDATA and child elements in our simple DOM builder.
 *  Is this true any longer?
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class TableResolverXmlLoader
{
  /** The XML loader to use.
   */
  XmlLoader loader;

  /** Constructs a TableResolverXmlLoader.
   */
  public TableResolverXmlLoader()
    {
      // Need no catalog...
      loader = new XmlLoader(null);
    }

  /** Constructs a new TableResolver with the given table loaded.
   *
   *  @param tableURI the URI of the table to load.
   *  @return a new TableResolver.
   *  @exception TableResolverException if anything went wrong.
   */
  public TableResolver loadResolver(URI tableURI)
    throws ResolveException
  {
    TableResolver resolver = new TableResolver();

    loadResolver(resolver, tableURI);

    return resolver;
  }

  /** Loads the given table into an already existing TableResolver
   *
   * @param lookup the TableResolver to extend
   * @param tableURI the URI of the table to load.
   * @exception TableResolverException if anything went wrong.
   */
  public void loadResolver(TableResolver resolver, URI tableURI)
    throws ResolveException
  {
    XmlDocument doc;
    try {
      doc = loader.parse(tableURI.getJavaURL());
    } catch(XmlLoaderException e)
      {
	throw new ResolveException("Could not load resolver table " +
				    tableURI + ":\n" + e.getMessage());
      }catch(java.net.MalformedURLException e)
	{
	throw new ResolveException("Invalid URI of resolver table '" +
				    tableURI + "':\n" + e.getMessage());
      }
    XmlElement[] pathEls = doc.getRoot().getSubElements("Path");

    for(int i = 0; i < pathEls.length; i++)
      {
	String path =  pathEls[i].getAttribute("NAME");
	String uriStr = pathEls[i].getAttribute("BASEURI");
	String mimeStr = pathEls[i].getAttribute("TYPE");
	if(path == null)
	  throw new ResolveException("Missing NAME attribute in table file '"
				      + tableURI + "'!");
	if(uriStr == null)
	  throw new ResolveException("Missing BASEURI attribute in table file '" + tableURI + "'!");
	if(mimeStr == null)
	  throw new ResolveException("Missing TYPE attribute in table file '" + tableURI + "'!");

	try {
	  resolver.addPath(path, URIClassifier.parseURI(uriStr, null), new MIMEType(mimeStr));
	} catch (MalformedURIException e)
	  {
	    throw new ResolveException("The path " + path +
					" did not map to a valid base URI in the resolver table '" + tableURI + "' \n" + e.getMessage());
	  }
	catch (MalformedMIMETypeException e)
	  {
	    throw new ResolveException("The path " + path +
					" did not map to a valid type in the resolver table '" + tableURI + "' \n" + e.getMessage());
	  }
      }
  } 
}
