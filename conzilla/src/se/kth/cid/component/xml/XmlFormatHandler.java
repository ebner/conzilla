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


import se.kth.cid.component.*;
import se.kth.cid.component.local.*;

import netscape.security.PrivilegeManager;

import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.neuron.local.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conceptmap.local.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.xml.*;

import java.io.*;

/** This is the FormatHandler that are used with XML documents (text/xml).
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlFormatHandler implements FormatHandler
{

  /** Whether we should bother using the privilege manager.
   */
  public static boolean usePrivMan = false;

  /** The class doing the actual loading.
   */
  XmlComponentIO componentIO;

  /** Constructs an XmlFormatLoader.
   */
  public XmlFormatHandler()
  {
    componentIO = new XmlComponentIO();    
  }
  
  
  public Component loadComponent(URI uri, URI origuri, boolean isSavable)
    throws ComponentException
    {
      if(usePrivMan)
	PrivilegeManager.enablePrivilege("UniversalConnect");
      
      try {
	java.net.URLConnection conn = (new java.net.URL(uri.toString())).openConnection();
	conn.connect();
	return componentIO.loadComponent(origuri, conn.getInputStream(),
					 isSavable);
      }
      catch (java.net.MalformedURLException e)
	{
	  throw new ComponentException("Invalid URL " + uri +
				       " for component " + origuri + ":\n "
				       + e.getMessage());
	}
      catch (IOException e)
	{
	  throw new ComponentException("IO error loading URL " + uri +
				       " for component " + origuri + ":\n "
				       + e.getMessage());
	} 
      catch (ComponentException e)
	{
	  throw new ComponentException("Error loading component "
				       + origuri + " from " + uri + ":\n "
				       + e.getMessage());
	}
    }

  public boolean isSavable(URI uri)
    {
      if(uri instanceof FileURL)
        {
          File f = ((FileURL) uri).getJavaFile();
          if (f.isFile())
            return f.canWrite();
          else
            return (new File(f.getParent())).canWrite();
        }
      else
        return false;
    }
  
  
  public boolean canCreateComponent(URI uri) throws ComponentException
    {
      if(uri instanceof FileURL)
	{
	  File f = ((FileURL) uri).getJavaFile();
	  if (f.exists())
	    return false;
	  else if(new File(f.getParent()).canWrite())
	    return true;
	}
      throw new ComponentException("Cannot create other than file:" +
				   "components");
    }

  public Component createComponent(URI uri, URI realURI)
    throws ComponentException
    {
      if(uri instanceof FileURL)
	{
	  File f = ((FileURL) uri).getJavaFile();
	  if (f.exists())
	    throw new ComponentException("Component does already exist: '"
					 + realURI + "' at '"
					 + uri + "'");
	  else if(new File(f.getParent()).canWrite())
	    return new LocalComponent(realURI);
	}
      throw new ComponentException("Cannot create other than file:" +
				   "components");
    }

  public Neuron createNeuron(URI uri, URI realURI, URI typeURI)
    throws ComponentException
    {
      if(uri instanceof FileURL)
	{
	  File f = ((FileURL) uri).getJavaFile();
	  if (f.exists())
	    throw new ComponentException("Component does already exist: '"
					 + realURI + "' at '"
					 + uri + "'");
	  else if(new File(f.getParent()).canWrite())
	    return new LocalNeuron(realURI, typeURI);
	}
      throw new ComponentException("Cannot create other than file:" +
				   "components");
    }

  public NeuronType createNeuronType(URI uri, URI realURI)
    throws ComponentException
    {
      if(uri instanceof FileURL)
	{
	  File f = ((FileURL) uri).getJavaFile();
	  if (f.exists())
	    throw new ComponentException("Component does already exist: '"
					 + realURI + "' at '"
					 + uri + "'");
	  else if(new File(f.getParent()).canWrite())
	    return new LocalNeuronType(realURI);
	}
      throw new ComponentException("Cannot create other than file:" +
				   "components");
    }
  
  public ConceptMap createConceptMap(URI uri, URI realURI)
    throws ComponentException
    {
      if(uri instanceof FileURL)
	{
	  File f = ((FileURL) uri).getJavaFile();
	  if (f.exists())
	    throw new ComponentException("Component does already exist: '"
					 + realURI + "' at '"
					 + uri + "'");
	  else if(new File(f.getParent()).canWrite())
	    return new LocalConceptMap(realURI);
	}
      throw new ComponentException("Cannot create other than file:" +
				   "components");
    }


  public void saveComponent(URI uri, Component comp)
    throws ComponentException
  {
    if(usePrivMan)
      PrivilegeManager.enablePrivilege("UniversalConnect");
    
    if(! (uri instanceof FileURL))
      throw new ComponentException("Unsupported protocol when saving to: '" + uri + "'");


    File f = ((FileURL) uri).getJavaFile();
    try {
      OutputStream os = new FileOutputStream(f);
      
      componentIO.printComponent(comp, os);
    }
    catch (IOException e)
      {
	throw new ComponentException("IO error saving component: " + uri
				     + ":\n " + e.getMessage());
      } 
  }
}
