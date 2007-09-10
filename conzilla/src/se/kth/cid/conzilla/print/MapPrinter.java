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


package se.kth.cid.conzilla.print;

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

public class MapPrinter
{
  class Printer implements Printable, Pageable
  {
    MapScrollPane scrollPane;

    public Printer(MapScrollPane scrollPane)
      {
	this.scrollPane = scrollPane;
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
      throws PrinterException
      {     
	if (pageIndex >= 1) return Printable.NO_SUCH_PAGE;
	
	java.awt.Component vp = scrollPane.getViewport().getView();
	int w = vp.getSize().width;

	double scale = pf.getWidth()/w;
	((Graphics2D) g).scale(scale, scale);
	
	vp.print(g);
    
	return Printable.PAGE_EXISTS;   
      } 
  }
  

  
  public MapPrinter()
    {
    }

  public boolean print(MapScrollPane sp) throws PrinterException
    {
      // Get a PrinterJob
      PrinterJob job = PrinterJob.getPrinterJob();     

      job.setPageable(new Printer(sp));     

      // Put up the dialog box    
      if (job.printDialog()) 
	{       
	  // Print the job if the user didn't cancel printing 
	  job.print();
	  return true;
	}     
      return false;
    }
  
}
