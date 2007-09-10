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

package se.kth.cid.conzilla.content;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*;
import se.kth.cid.component.Component;


public class FrameMapContentDisplayer extends AbstractContentDisplayer
{
  JFrame frame;

  MapContentDisplayer displayer;
  
  public FrameMapContentDisplayer(ComponentStore store)
    {
      super(store);
      frame = new JFrame("Content");
      frame.addWindowListener(new WindowAdapter() {
	  public void windowClosing(WindowEvent ev)
	    {
	      Tracer.debug("CLOSED!!!");
	      try {
		setContent(null);
	      } catch(ContentException e)
		{
		  Tracer.trace("AbstractContentDisplayer threw exception!", Tracer.ERROR);
		}
	    }
	});
      
      displayer = new MapContentDisplayer(frame.getContentPane(),
					  BorderLayout.CENTER, store);
    }

  public void setContent(Component c) throws ContentException
    {
      displayer.setContent(c);
      super.setContent(displayer.getContent());

      if(getContent() != null)
	{
	  frame.pack();
	  frame.setVisible(true);
	  frame.invalidate();
	  frame.repaint();
	}
      else
	frame.setVisible(false);
    }
}
