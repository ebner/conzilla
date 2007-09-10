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


package se.kth.cid.conzilla.app;


import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.conzilla.util.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

public class Conzilla extends JApplet
{ 
  boolean isApplet = false; 
  ConzillaFactory conzfactory;
  Vector cowins;
  ConzillaKit kit;
  
  public Conzilla()
    {
      isApplet = true;
      cowins = new Vector();
    }
  
  public Conzilla(URI resolverTable, URI startMap)
    {
      cowins = new Vector();
      startUp(resolverTable, startMap);
    }
  
  
  public static void main(String[] argv)
    {
      //    java.lang.Runtime.getRuntime().runFinalizersOnExit(true);
      
      usePrivilegeManager(false);
      
      if (argv.length == 0 || (argv.length >= 2 && (argv[1].indexOf("--help") != -1 || argv[1].equals("?"))))
	{
	  Tracer.trace("Usage: Conzilla  URLLocator-origin [ConceptMap] [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.NONE);
      
      String table = argv[0];
      String startmap;

      if (argv.length>=2)
	startmap=argv[1];
      else
	startmap="urn:path:/local/desktop";

      try {
	Conzilla conzilla = new Conzilla(URIClassifier.parseURI(table),
					 URIClassifier.parseURI(startmap));
      }  catch(MalformedURIException e)
	{
	  TextOptionPane.showError(null, "Invalid URI:\n "
				   + e.getMessage());
	  System.exit(-1);
	}
    }
  
  public void init()
    {
      Tracer.setLogLevel(Tracer.ALL);

      String privMan = getParameter("USEPRIVMAN");

      usePrivilegeManager(privMan != null && privMan.equals("true"));
      
      try{
	startUp(URIClassifier.parseURI(getParameter("RESOLVERTABLE"),
				       URIClassifier.parseURI(getCodeBase().toString())),
		URIClassifier.parseURI(getParameter("STARTMAP")));
      }
      catch(MalformedURIException e)
	{
	  TextOptionPane.showError(null, "Invalid parameter:\n"
				   + e.getMessage());
	  exit(-1);
	}
    }
  
  void startUp(URI table, URI map)
    {
      kit = new ConzillaKit(table, this);
      
      conzfactory = new ConzillaFactory(kit);
      
      if (!openMap(map))
	exit(-1);
    }
  
  public boolean openMap(URI map)
    {
      Tracer.debug("Opening " + map);
      try
	{
	  ConzillaWindow cw = conzfactory.createBrowseWindow(map);
	  cowins.addElement(cw);
	} catch(ControllerException e)
	  {
	    TextOptionPane.showError(null, "Unable to load map:\n "
				     + e.getMessage());
	    return false;
	  }
      return true;
    }

  public boolean editMap(URI map)
    {
      Tracer.debug("Opening " + map);
      try
	{
	  ConzillaWindow cw = conzfactory.createEditWindow(map);
	  cowins.addElement(cw);
	} catch(ControllerException e)
	  {
	    TextOptionPane.showError(null, "Unable to load map:\n "
				     + e.getMessage());
	    return false;
	  }
      return true;
    }
  
  public void clone(ConzillaWindow cw)
    {
      try {
	openMap(URIClassifier.parseURI(cw.getController().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().getURI()));
      } catch(MalformedURIException e)
	{
	  Tracer.trace("Malformed URI:" + e.getMessage(), Tracer.ERROR);
	}
    }
  
  public void close(ConzillaWindow cw)
    {
      if (cowins.size() == 1)
	{
	  cw.close();
	  cowins.removeElement(cw);
	  exit(0);
	}
      else
	{
	  cw.close();
	  cowins.removeElement(cw);
	}
    }
  
  public boolean isApplet()
    {
      return isApplet;
    }
  
  public void exit(int result)
    {
      conzfactory = null;
      
      Enumeration en = cowins.elements();
      for (;en.hasMoreElements();)
	((ConzillaWindow) en.nextElement()).close();

      cowins = null;

      if (isApplet)
	{
	  super.stop();
	  super.destroy();
	}
      else
	System.exit(result);
    }
  
  public void stop()
    {}
  public void start()
    {}
  public void destroy()
    {}

  private static void usePrivilegeManager(boolean val)
    {
      XmlFormatHandler.usePrivMan = val;
    }
}
