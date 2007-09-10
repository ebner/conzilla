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
import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.component.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.neuron.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.xml.*;


import java.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*; 
import java.awt.print.*;

public class Print implements Printable, Pageable {   
  private static Font fnt = new Font("Helvetica",Font.PLAIN,24);

  MapDisplayer disp;

  public Print(MapDisplayer disp)
    {
      this.disp = disp;
    }
  
   
  public static void main(String[] argv) throws Exception
    {             
      
      if(argv.length < 2)
	{
	  Tracer.trace("Usage: MapDisp table cDesc [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.NONE);

      URI table = URIClassifier.parseURI(argv[0], null);
      
      TableResolverXmlLoader l = new TableResolverXmlLoader();
      TableResolver resolver = l.loadResolver(table);

      DefaultComponentHandler handler = new DefaultComponentHandler(resolver);
      handler.addFormatHandler(new MIMEType("text/xml"), new XmlFormatHandler());
      
      ComponentStore store =
	new ComponentStore(handler, new EasyCCache());
      
      URI compuri = URIClassifier.parseURI(argv[1]);
      
      MapDisplayer mapDisp = new MapDisplayer(new MapManager(compuri, store));

      mapDisp.addMapEventListener((new MapEventListener() {
	  public void eventTriggered(MapEvent m)
	    {
	      Tracer.debug("Hit = " + (new Integer(m.hitType)));
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
      
      // Get a PrinterJob     
      PrinterJob job = PrinterJob.getPrinterJob();     
      // Specify the Printable is an instance of SimplePrint
      job.setPageable(new Print(mapDisp));     
      // Put up the dialog box     
      if (job.printDialog()) 
	{       
	  // Print the job if the user didn't cancel printing 
	  job.print();
	}     
      System.exit(0);   
    }
  
  public int getNumberOfPages()
    {
      return 1;
    }

  public PageFormat getPageFormat(int i)
    {
      PageFormat pf = new PageFormat();
      Paper p = pf.getPaper();
      p.setImageableArea(0, 0, p.getWidth(), p.getHeight());
      pf.setPaper(p);
      return pf;
    }

  public Printable getPrintable(int i)
    {
      return this;
    }
  
  
  public int print(Graphics g, PageFormat pf, int pageIndex)
    throws PrinterException {     
    // pageIndex 0 to 4 corresponds to page numbers 1 to 5.
    if (pageIndex >= 1) return Printable.NO_SUCH_PAGE;
    
    disp.print(g);
    
    return Printable.PAGE_EXISTS;   
  } 
}
