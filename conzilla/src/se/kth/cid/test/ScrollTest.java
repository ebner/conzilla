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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.cache.*;
import se.kth.cid.component.xml.*;
import se.kth.cid.neuron.*;
import se.kth.cid.xml.*;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

class ScrollTest
{
  static class helpclass implements ActionListener
  {
    private JLayeredPane jlp;
    private JLabel label;
    
    helpclass(JLayeredPane jlp)
    {
      this.jlp=jlp;
      label=null;
    }
    public void actionPerformed(ActionEvent e)
    {
      if (label==null)
	{
	  label=new JLabel("Test, går denna utanför någonstans??");
	  label.setLocation(-40,-40);
	  label.setSize(200,80);
	  jlp.add(label,JLayeredPane.POPUP_LAYER);
	}
      else
	{
	  jlp.remove(label);
	  label=null;
	}
    }
  }

  private static JLayeredPane jlp;
  
  public static void main(String[] argv)
    throws Exception
  {
    if(argv.length !=0)
      {
	Tracer.trace("Usage: Loader  URLLocator-origin Component [LogLevel]",
		     Tracer.ERROR);
	System.exit(-1);
      }
    
    if(argv.length > 2)
      Tracer.setLogLevel(Tracer.parseLogLevel(argv[2]));
    else
      Tracer.setLogLevel(Tracer.NONE);
    JFrame frame=new JFrame("ScrollText");
    frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });
    

    jlp=new JLayeredPane();
    JScrollPane jsp=new JScrollPane(jlp,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    LayerManager lm=new LayerManager(jlp);
    jlp.setLayout(lm);
    frame.getContentPane().add(jsp);
    frame.setSize(300,300);

    JButton but=new JButton("Hej!");
    but.setSize(500,500);
    
    but.setLocation(0,0);
    but.addActionListener(new helpclass(jlp));
    jlp.add(but,JLayeredPane.DEFAULT_LAYER);
      
    frame.setVisible(true);
    frame.pack();
  }
  
}





