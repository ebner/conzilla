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


package se.kth.cid.conzilla.center;


import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.lookup.*;
import se.kth.cid.conzilla.controller.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

public class Conzilla extends JApplet
{
  boolean isApplet = false;
  ConzFactory conzfactory;
  Vector cowins;
  ConzKit kit;

  public Conzilla()
  {
    isApplet = true;
    cowins=new Vector();
  }



  public static void main(String[] argv)
  {
    java.lang.Runtime.getRuntime().runFinalizersOnExit(true);
    usePrivilegeManager(false);
    if (argv.length==0 || (argv.length >= 2 && (argv[1].indexOf("--help")!=-1 || argv[1].equals("?"))))
      {
	Tracer.trace("Usage: Conzilla  URLLocator-origin [ConceptMap] [LogLevel]",
		     Tracer.ERROR);
	System.exit(-1);
      }
    if(argv.length > 2)
      Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
    else
      Tracer.setLogLevel(Tracer.NONE);

    Tracer.setLogLevel(Tracer.ALL);

    Conzilla conzilla = new Conzilla();
    conzilla.isApplet=false;
    String URLLO=argv[0];
    String startmap;
    if (argv.length>=2)
      startmap=argv[1];
    else
      startmap="cid:local/cd/desktop";
    try {
      conzilla.startUp(new URL(URLLO),
		       startmap);
    }  catch(MalformedURLException e)
      {
	TextOptionPane.showError(null, "Invalid URL to defaultTable:\n "
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
	startUp(new URL(getCodeBase(), getParameter("PATCHTABLE")),
		   getParameter("STARTMAP"));
      }
      catch(MalformedURLException e)
	{
	  TextOptionPane.showError(null, "Invalid parameter:\n"
				   + e.getMessage());
	  exit(-1);
	}
    }

  private void startUp(URL patchtable, String map)
    {
      kit=new ConzKit(patchtable, this);
      conzfactory = new ConzFactory(kit,this);

      if (!openMap(map))
	exit(-1);
    }

  public boolean openMap(URI map)
    {
      try
	{
	  ConzWindow cw=conzfactory.newConzWindow(map);
	  cowins.addElement(cw);
	} catch(ControllerException e)
	{
	  TextOptionPane.showError(null, "Unable to load map, probably the URI is wrong:\n "
				   + e.getMessage());
	  return false;
	}
      return true;
    }

  public boolean openMap(String map)
    {
      URI uri;
      try {
	uri=new URI(map);
      } catch (MalformedURIException e)
	{
	  TextOptionPane.showError(null, "Unable to load map, badly formated URI:\n "
				   + e.getMessage());
	  return false;
	}
      return openMap(uri);
    }

  public void clone(ConzWindow cw)
    {
      openMap(cw.controller.getCurrentMapURI());
    }

  public void close(ConzWindow cw)
    {
      if (cowins.size()==1)
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
  public boolean isAApplet()
    {
      return isApplet;
    }

  public void exit(int result)
    {
      conzfactory=null;
      Enumeration en=cowins.elements();
      for (;en.hasMoreElements();)
	((ConzWindow) en.nextElement()).close();
      cowins=null;
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
      XmlFormatLoader.usePrivMan = val;
      XmlFormatSaver.usePrivMan  = val;
    }
}
