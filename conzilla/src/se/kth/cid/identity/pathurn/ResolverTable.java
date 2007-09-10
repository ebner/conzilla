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


import java.util.*;
import java.io.*;
import java.net.*;

public class ResolverTable
{
  public static final String RESOLVER_DTD_VERSION = "1.0";

  String name;
  URI    location;
  
  Vector entries;

  boolean isSavable = false;
  
  public class ResolverEntry
  {
    public String   path;
    public String   baseURI;
    public URI      fullBaseURI;
    public MIMEType type;

    public ResolverEntry(String path, String baseURI, MIMEType type) throws ResolveException, MalformedURIException
      {
	this.path    = path;
	this.baseURI = baseURI;
	this.type    = type;

	fullBaseURI = URIClassifier.parseURI(baseURI, location);
	
	if(path.charAt(0) != '/')
	  throw new ResolveException("Path '" + path +
				     "'does not begin with '/'");
	if(fullBaseURI.toString().charAt(fullBaseURI.toString().length() - 1) != '/')
	  throw new ResolveException("Base URI '" + baseURI +
				     "' is no base path (does not end in '/')");
      }
  }

  public ResolverTable(URI location) throws ResolveException
    {
      this.name = name;
      this.location = location;

      entries = new Vector();

      testSavable();
      
      loadTable();
    }

  public void changeLocation(URI location)
    {
	this.location = location;
	testSavable();
    }

  public boolean isSavable()
    {
      return isSavable;
    }
  
  public void setEntries(ResolverEntry[] entries)
    {
      this.entries.setSize(entries.length);
      for(int i = 0; i < entries.length; i++)
	this.entries.setElementAt(entries[i], i);
    }    
      
  public String getName()
    {
      return name;
    }

  public URI getLocation()
    {
      return location;
    }

  public ResolverEntry[] getEntries()
    {
      return (ResolverEntry[]) entries.toArray(new ResolverEntry[entries.size()]);
    }


  void testSavable()
    {
      if(location instanceof FileURL)
	{
	  File f = ((FileURL) location).getJavaFile();
	  
	  if (f.exists() && f.isFile())
	    isSavable = f.canWrite();
	  else if(! f.exists())
	    isSavable = (new File(f.getParent())).canWrite();
	}
      else
	  isSavable = false;
    }

  public void fillResolver(TableResolver resolver)
    {
      for(int j = 0; j < entries.size(); j++)
	{
	  ResolverEntry entry = (ResolverEntry) entries.get(j);
	  resolver.addPath(entry.path, entry.fullBaseURI, entry.type);
	}
    }
  
  
  void loadTable() throws ResolveException
    {
      XmlLoader loader = new XmlLoader(null);
      
      XmlDocument doc;
      try {
	doc = loader.parse(location.getJavaURL());
      } catch(XmlLoaderException e)
	{
	  throw new ResolveException("Could not load resolver table " +
				     location + ":\n" + e.getMessage());
	}
      catch(java.net.MalformedURLException e)
	{
	  throw new ResolveException("Could not locate resolver table " +
				     location + ":\n" + e.getMessage());
	}

      XmlElement resolver = doc.getRoot();

      if(!resolver.getName().equals("Resolver"))
	throw new ResolveException("Document root was not 'Resolver' in " +
				   "resolver table " + location);

      name = resolver.getAttribute("NAME");
      if(name == null)
	throw new ResolveException("Missing Resolver NAME attribute in resolver file '"
				   + location + "'!");

      String version = resolver.getAttribute("VERSION");
      if(!RESOLVER_DTD_VERSION.equals(version))
	throw new ResolveException("Resolver table version " + version 
				   + " not supported in resolver: '" +
				   location + "'!");
      
      XmlElement[] services = resolver.getSubElements("Services");
      
      if(services == null || services.length != 1)
	throw new ResolveException("'Services' element invalid in " +
				   "resolver table " + location);

      
      XmlElement[] pathEls = services[0].getSubElements("Path");

      for(int i = 0; i < pathEls.length; i++)
	{
	  String path =  pathEls[i].getAttribute("NAME");
	  String uriStr = pathEls[i].getAttribute("BASEURI");
	  String mimeStr = pathEls[i].getAttribute("TYPE");
	  if(path == null)
	    throw new ResolveException("Missing NAME attribute in table file '"
				       + location + "'!");
	  if(uriStr == null)
	    throw new ResolveException("Missing BASEURI attribute in table file '" + location + "'!");
	  if(mimeStr == null)
	    throw new ResolveException("Missing TYPE attribute in table file '" + location + "'!");
	  
	  try {
	    entries.add(new ResolverEntry(path, uriStr, new MIMEType(mimeStr)));
	  } catch (MalformedURIException e)
	    {
	      throw new ResolveException("The path " + path +
					 " did not map to a valid base URI in the resolver table '"
					 + location + "' \n" + e.getMessage());
	    }
	  catch (MalformedMIMETypeException e)
	    {
	      throw new ResolveException("The path " + path +
					 " did not map to a valid type in the resolver table '"
					 + location + "' \n" + e.getMessage());
	    }
	}
    }

  public void saveTable() throws IOException, ResolveException
    {
      try {
	if(! (location instanceof FileURL))
	  throw new ResolveException("Unsupported protocol. Cannot save to this location: " + location);
	
	File f = ((FileURL) location).getJavaFile();
	  
	if (! f.exists() && !(new File(f.getParent())).canWrite())
	  throw new ResolveException("Cannot create file: " + location);
	  
	OutputStream os = new FileOutputStream(f);
	
	XmlDocument doc = new XmlDocument();
	XmlPrinter printer = new XmlPrinter();

	XmlElement root = new XmlElement("Resolver");
	root.setAttribute("NAME", name);
	root.setAttribute("VERSION", RESOLVER_DTD_VERSION);
	
	XmlElement services = new XmlElement("Services");

	for(int i = 0; i < entries.size(); i++)
	  {
	    ResolverEntry entry = (ResolverEntry) entries.get(i);
	    XmlElement path = new XmlElement("Path");
	    path.setAttribute("NAME", entry.path);
	    path.setAttribute("BASEURI", entry.baseURI.toString());
	    path.setAttribute("TYPE", entry.type.toString());
	    services.addSubElement(path);
	  }
	root.addSubElement(services);
	
	doc.setRoot(root);
	printer.print(doc, os);
	os.close();
      }
      catch (MalformedURLException e)
	{
	  throw new ResolveException("The location " + location +
				     " was no valid URL:\n " + e.getMessage());
	}
      catch (UnknownElementNameException e)
	{
	  Tracer.bug("Unknown element name:\n " + e.getMessage());
	}
    }

  public ResolverEntry newResolverEntry(String path, String baseURI, MIMEType type) throws ResolveException, MalformedURIException
    {
      return new ResolverEntry(path, baseURI, type);
    }
  
}
