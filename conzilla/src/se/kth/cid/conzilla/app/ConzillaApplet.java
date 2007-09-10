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
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.content.*;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.*;

public class ConzillaApplet extends JApplet
{ 
  ConzillaAppletEnv conzillaEnv;

  public class ConzillaAppletEnv extends ConzillaAppEnv
  {
    public ConzillaAppletEnv()
      {
      }

    protected void initDefaultContentDisplayer(PathURNResolver resolver)
      {
	this.defaultContentDisplayer =
	  new AppletContentDisplayer(getAppletContext(),
				     getParameter("TARGETWINDOW"),
				     resolver);
      }

    public JApplet getApplet()
      {
	return ConzillaApplet.this;
      }
    
    public void exit(int result)
      {
	ConzillaApplet.this.exit();
      }
  }

  
  public ConzillaApplet()
    {
    }
  
  public void init()
    {
      String privMan = getParameter("USEPRIVMAN");

      usePrivilegeManager(privMan != null && privMan.equals("true"));

      conzillaEnv = new ConzillaAppletEnv();
      
      URI startMap = null;

      String param = getParameter("STARTMAP");
      if(param != null)
	{
	  try {
	    startMap = URIClassifier.parseURI(param);
	  } catch(MalformedURIException e)
	    {
	      System.out.print("Invalid start map: '" + param + "':\n " +
			       e.getMessage() + "\n\n");
	      conzillaEnv.exit(1);
	    }
	}
      else
	startMap = URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/maps/default");

      ConzillaKit kit = new ConzillaKit(conzillaEnv);
      
      if(!kit.getConzilla().openMap(startMap))
	{
	  conzillaEnv.exit(1);
	}
    }
  
  private static void usePrivilegeManager(boolean val)
    {
      XmlFormatHandler.usePrivMan = val;
    }
  
  protected void exit()
    {
      stop();
      destroy();
    }

}
