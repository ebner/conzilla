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

package se.kth.cid.conzilla.library;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.controller.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class FrameLibraryDisplayer extends JFrame implements LibraryDisplayer
{
  JComponent librarypresentation;
  IndexLibrary library;
  
  public FrameLibraryDisplayer(IndexLibrary library, ResourceController controller)
  {
    super("Library");
    this.library=library;
    
    addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent ev)
	{
	  Tracer.debug("CLOSED!!!");
	  hideLibrary();
	}
      });
    
    getContentPane().setLayout(new BorderLayout());
    JPanel center = new JPanel();
    center.setLayout(new BorderLayout());
    center.add(controller.getTitleBox(), BorderLayout.NORTH);
    center.add(controller, BorderLayout.CENTER);
    center.add( (JList) controller.getContentSelector(), BorderLayout.EAST);
    getContentPane().add(center, BorderLayout.CENTER);
    getContentPane().add(controller.getToolBar(), BorderLayout.SOUTH);	

    librarypresentation=center;
    setSize(420, 300);
    
    /*    JButton close = new JButton("Close");
    close.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e)
	{
	  closeLibrary();
	}
      });

    JPanel buttons = new JPanel();

    buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));

    buttons.add(close);
    
    getContentPane().add(buttons, BorderLayout.SOUTH);*/
  }
  
  public void showLibrary()
  {
    if (!isLibraryVisible())
      {
	librarypresentation.revalidate();
	pack();
	setVisible(true);
	repaint();
      }
  }
  public boolean isLibraryVisible()
    {
      return isVisible();
    }
  
  public IndexLibrary getLibrary()
  {
    return library;
  }
  public void hideLibrary()
  {
    if(isVisible())
      setVisible(false);
  }
}
