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
import se.kth.cid.conzilla.install.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.view.*;

//import netscape.security.*;
import java.util.*;
import java.io.*;
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
	  defaultContentDisplayer = new AppletContentDisplayer(getApplet().getAppletContext(),
							       getParameter("TARGETWINDOW"),
							       resolver);
      }

    public JApplet getApplet()
      {
	return ConzillaApplet.this;
      }
    
    public void exit(int result)
      {
	try {
	    Installer.saveConfig();
	} catch(IOException e)
	    {
		Tracer.trace("IO Error saving config: " + e.getMessage(), Tracer.WARNING);
	    }
	ConzillaApplet.this.exit();
      }
  }

  
  public ConzillaApplet()
    {
    }

  public void start()
    {
	URI configURL = null;

	//Where to find the property file.
	//--------------------------------
	String pf = getParameter("PROPERTYFILE");

	if (pf==null)   //No location specified
	    pf="conzilla.properties";    //Hence we guess there is a conzilla.properties in the same dir.

	if (pf.indexOf(":/")!=-1)   //Realtive location specified.
	    try {
		String url = (new java.net.URL(getDocumentBase(), pf)).toString();
		configURL = URIClassifier.parseURI(url);
		
	    } catch (java.net.MalformedURLException me) {
		Tracer.debug("PropertyFile isn't on the specified location...");
	    } catch (MalformedURIException me) {
		Tracer.bug("conzilla.properties isn't on the specified location...");
	    }
	else                        //Absolute location specified
	    try {
		configURL = URIClassifier.parseURI(pf);	
	    } catch (MalformedURIException me) {
		Tracer.bug("conzilla.properties isn't on the specified location...");
	    }		    
	
	//Fetch the map to start initially.
	//--------------------------------
	String param = getParameter("STARTMAP");
	
	//Initialize Conzillas environment.
	//---------------------------------
	conzillaEnv = new ConzillaAppletEnv();
	conzillaEnv.start(param, configURL);

	//Fetches the displayarea for conzilla and sets it in the applet.
	//---------------------------------------------------------------
	SinglePaneManager viewManager = (SinglePaneManager) conzillaEnv.kit.getConzilla().getViewManager();
	viewManager.getSinglePane().setSize(Integer.parseInt(getParameter("WIDTH"))-20,
					    Integer.parseInt(getParameter("HEIGHT"))-20);
        setContentPane(viewManager.getSinglePane());
    }    

  protected void exit()
    {
	setContentPane(null);
	stop();
	//	destroy();
    }

}
