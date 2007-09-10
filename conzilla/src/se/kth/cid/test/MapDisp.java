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
import se.kth.cid.xml.*;


import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;

class MapDisp
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
      
      ComponentLoader loader =
	new LookupLoader(new BaseURLLookup(new URL(argv[0])));
      
      URI compuri = new URI(argv[1]);
      se.kth.cid.component.Component comp = loader.loadComponent(compuri, loader);

      if(! (comp instanceof ConceptMap))
	{
	  Tracer.trace("Component was no ConceptMap!", Tracer.ERROR);
	  System.exit(-1);
	}
      ConceptMap cmap = (ConceptMap) comp;
      cmap.setEditable(true);
      
      
      MapDisplayer mapDisp = new MapDisplayer(cmap,null);

      mapDisp.addMapEventListener((new MapEventListener() {
	  public void eventTriggered(MapEvent m)
	  {
	    Tracer.debug("Hit = " + (new Integer(m.hit)));
	  }
	}),MapDisplayer.CLICK);
      JFrame frame = new JFrame("MapDisplayer");
      frame.getContentPane().add(mapDisp);
      
      //Finish setting up the frame, and show it.
      frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });
      frame.setSize(420, 420);
      frame.show();
    }
}





