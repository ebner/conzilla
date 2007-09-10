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
import se.kth.cid.identity.*;
import se.kth.cid.identity.pathurn.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.identity.*;
import se.kth.cid.neuron.*;
import se.kth.cid.xml.*;


import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import se.kth.cid.component.Component;


public class MetaDataEdit
{
  public static void main(String[] argv)
    throws Exception
    {
      if(argv.length < 2)
	{
	  Tracer.trace("Usage: MetaDataTest  URLLocator-origin Component [LogLevel]",
		       Tracer.ERROR);
	  System.exit(-1);
	}
      
      if(argv.length > 2)
	Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
      else
	Tracer.setLogLevel(Tracer.ALL);
      
      
      URI table = URIClassifier.parseURI(argv[0]);
      
      ResolverManager man = new ResolverManager();
      man.addTable(new ResolverTable(table));
      
      final DefaultComponentHandler handler = new DefaultComponentHandler(man.getResolver());
      handler.addFormatHandler(MIMEType.XML, new XmlFormatHandler());
      
      URI compuri = URIClassifier.parseURI(argv[1]);
      
      final Component comp = handler.loadComponent(compuri);
      
      //      Locale.setDefault(new Locale("sv", ""));
      
      JFrame frame = new JFrame("Metadata Edit");
      JFrame frame2 = new JFrame("Metadata Edit 2");

      final PanelMetaDataDisplayer panel = new PanelMetaDataDisplayer(comp,
								      comp.isEditable());
      final PanelMetaDataDisplayer panel2 = new PanelMetaDataDisplayer(comp,
								       comp.isEditable());
      
      frame.getContentPane().add(panel);
      frame2.getContentPane().add(panel2);

      JButton b = new JButton("Save");
      b.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e)
	    {
	      panel.storeMetaData();
	      try {
		XmlPrinter p = new XmlPrinter();
		XmlDocument doc = new XmlDocument();
		doc.setRoot(XmlMetaDataHandler.buildXmlTree(comp.getMetaData()));
		p.print(doc, System.out);
		handler.saveComponent(comp);
	      } catch(Exception ex)
		{
		  ex.printStackTrace();
		}
	    }
	});

      frame.getContentPane().add(b, BorderLayout.SOUTH);
      
      
      //Finish setting up the frame, and show it.
      frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  panel.detach();
	  panel2.detach();
	  System.exit(0);
	}
      });
      frame.setSize(400, 400);
      frame.setLocation(100, 100);
      frame.show();

      frame2.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  panel.detach();
	  panel2.detach();
	  System.exit(0);
	}
      });
      frame2.setSize(400, 400);
      frame2.setLocation(200, 100);
      frame2.show();

      //      if(lscomp.getLangStrings() != null)
      //	Tracer.debug("" + Arrays.asList(lscomp.getLangStrings()));
    }
}





