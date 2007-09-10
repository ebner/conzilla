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
import se.kth.cid.component.xml.dtd.*;
import se.kth.cid.xml.*;

import java.net.*;
import java.io.*;

/** This is the FormatLoader that can load XML documents (text/xml).
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public class XmlFormatLoader implements FormatLoader
{

  /** Whether we should bother using the privilege manager.
   */
  public static boolean usePrivMan = false;

  /** The class doing the actual loading.
   */
  XmlComponentIO componentIO;

  /** Constructs an XmlFormatLoader.
   */
  public XmlFormatLoader()
  {
    componentIO = new XmlComponentIO();    
  }
  
  
  public Component loadComponent(URI realURI, URI fetchuri, ComponentLoader recursiveLoader)
    throws FormatException
  {
    if(usePrivMan)
      PrivilegeManager.enablePrivilege("UniversalConnect");

    try {
      URLConnection conn = (new URL(fetchuri.toString())).openConnection();
      conn.connect();
      return componentIO.loadComponent(realURI, conn.getInputStream(),
				       recursiveLoader);
    }
    catch (MalformedURLException e)
      {
	throw new FormatException("Invalid URL " + fetchuri +
				  " for component " + realURI + ":\n "
				  + e.getMessage(), fetchuri);
      }
    catch (IOException e)
      {
	throw new FormatException("IO error loading URL " + fetchuri +
				  " for component " + realURI + ":\n "
				  + e.getMessage(), fetchuri);
      } 
    catch (XmlComponentException e)
      {
	throw new FormatException("Error loading component "
				  + realURI + " from " + fetchuri + ":\n "
				  + e.getMessage(), fetchuri);
      }
  }
  
}
