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


package se.kth.cid.component.lookup;

import se.kth.cid.component.*;

import netscape.security.PrivilegeManager;

import se.kth.cid.util.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.xml.*;

import java.net.*;
import java.io.*;

/** This is the FormatSaver that can save XML documents (text/xml).
 *  It currently only supports saving to file: URLs.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlFormatSaver implements FormatSaver
{

  /** Whether we should bother using the privilege manager.
   */
  public static boolean usePrivMan = false;

  
  /** The class doing the actual saving.
   */
  XmlComponentIO componentIO;
  

  /** Constructs an XmlFormatSaver.
   */
  public XmlFormatSaver()
  {
    componentIO = new XmlComponentIO();
  }

  public boolean isComponentSavable(URI uri, Component comp)
    {
      File f=getFile(uri);
      if (f!=null)
	{
	  if (f.isFile())
	    return f.canWrite();
	  else
	    return (new File(f.getParent())).canWrite();
	}
      else
	return false;
    }
  public boolean doComponentExist(URI uri)
    {
      File f=getFile(uri);
      if (f!=null && f.isFile())
	return true;
      return false;
    }
  
  private File getFile(URI uri)
    {
      try {
	URL fileURL = new URL(uri.toString());
	
	if(! fileURL.getProtocol().equals("file"))
	  return null;
	return new File(fileURL.getFile());
      }
      catch (MalformedURLException e)
	{
	  return null;
	}
    }
  
  public void saveComponent(URI uri, Component comp)
    throws FormatException
  {
    if(usePrivMan)
      PrivilegeManager.enablePrivilege("UniversalConnect");

    try {
      URL fileURL = new URL(uri.toString());
      
      if(! fileURL.getProtocol().equals("file"))
	throw new UnsupportedSaveProtocolException(fileURL.getProtocol(), uri);
      
      File f = new File(fileURL.getFile());
    
      OutputStream os = new FileOutputStream(f);

      componentIO.printComponent(comp, os);
    }
    catch (MalformedURLException e)
      {
	throw new FormatException("Invalid URL: " + uri
				  + ":\n " + e.getMessage(),
				  uri);
      }
    catch (IOException e)
      {
	throw new FormatException("IO error saving URL: " + uri
				  + ":\n " + e.getMessage(),
				  uri);
      } 
    catch (XmlComponentException e)
      {
	throw new FormatException("Error saving component: " + uri
				  + ":\n " + e.getMessage(),
				  uri);
      }
  }
}
