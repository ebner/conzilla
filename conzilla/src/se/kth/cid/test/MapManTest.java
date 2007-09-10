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


package se.kth.cid.test;

import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.lookup.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.xml.*;


import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MapManTest
{
  public static void main(String[] argv)
    throws Exception
    {
      if(argv.length < 2)
	{
	  Tracer.trace("Usage: MapTest  URLLocator-origin Component [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.NONE);
      Tracer.debug("1");
      ComponentLoader loader =
	new LookupLoader(new BaseURLLookup(new URL(argv[0])));
      
      URI compuri = new URI(argv[1]);
      se.kth.cid.component.Component comp = loader.loadComponent(compuri, loader);

      Tracer.debug("2");
      if(! (comp instanceof ConceptMap))
	{
	  Tracer.debug("Component was no ConceptMap!");
	  System.exit(-1);
	}
      Tracer.debug("3");
      ConceptMap cmap = (ConceptMap) comp;
      Tracer.debug("3");
      MapDisplayer mapDisp = new MapDisplayer(cmap, null);

      Tracer.debug("3");
      mapDisp.addMapEventListener(new MapEventListener() {
	  public void eventTriggered(MapEvent e)
	  {
	    if(e.neuronstyle != null)
	      Tracer.debug("NeuronStyle: " + e.neuronstyle.getTitle());
	    else if(e.rolestyle != null)
	      Tracer.debug("RoleStyle: " + e.rolestyle.getRoleType().type);
	  }
	}, MapDisplayer.CLICK);
      
      MapManager man = new MapManager(mapDisp);
      Tracer.debug("mapDisp.getSize() = " + mapDisp.getSize());
      
      JFrame frame = new JFrame("MapDisplayer");
      frame.getContentPane().add(man);
      
      //Finish setting up the frame, and show it.
      frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });
      frame.setSize(420, 420);
      frame.show();
      Tracer.debug("ending");
    }
}





