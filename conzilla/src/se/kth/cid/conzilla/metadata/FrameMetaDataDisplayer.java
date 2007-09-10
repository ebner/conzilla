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

package se.kth.cid.conzilla.metadata;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Font;

public class FrameMetaDataDisplayer extends JFrame implements ChangeListener
{
  Component component;

  DublinCorePanel dcDisplayer;
  PanelMetaDataDisplayer fullDisplayer;

  JTabbedPane tabPane;
  
  public FrameMetaDataDisplayer()
  {
    super("MetaData");
    
    addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent ev)
	{
	  showMetaData(null);
	}
      });
    
    getContentPane().setLayout(new BorderLayout());
    JButton close = new JButton("Close");
    close.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e)
	{
	  showMetaData(null);
	}
      });

    JToolBar buttons = new JToolBar();
    buttons.setFloatable(false);

    buttons.add(Box.createHorizontalGlue());
    buttons.add(close);
    
    getContentPane().add(buttons, BorderLayout.SOUTH);

    tabPane = new JTabbedPane();
    tabPane.add("Basic (Dublin Core)", new JPanel());
    tabPane.add("Full (IMS)", new JPanel());
    tabPane.addChangeListener(this);
    
    getContentPane().add(tabPane, BorderLayout.CENTER);
    
    setSize(500, 300);
    setLocation(100, 100);
  }

  void cleanAll()
    {
      if(dcDisplayer != null)
	{
	  dcDisplayer.detach();
	  tabPane.setComponentAt(0, new JPanel());
	}
      if(fullDisplayer != null)
	{
	  fullDisplayer.detach();
	  tabPane.setComponentAt(1, new JPanel());
	}
      
      dcDisplayer = null;
      fullDisplayer = null;
    
      component = null;
    }
      

  public void showMetaData(Component comp)
  {
    if (comp == component)
      {
	if(comp != null && !isVisible())
	  setVisible(true);
	return;
      }

    cleanAll();
    
    component = comp;
    
    if(component != null)
      {
	dcDisplayer = new DublinCorePanel(component);
	tabPane.setComponentAt(0, dcDisplayer);
	tabPane.setSelectedIndex(0);
	setVisible(true);
	repaint();
      }
    else
      {
	if(isVisible())
	  setVisible(false);
      }
  }

  public void stateChanged(ChangeEvent e)
    {
      if(tabPane.getSelectedIndex() == 1 && fullDisplayer == null)
	{
	  fullDisplayer = new PanelMetaDataDisplayer(component, false);
	  tabPane.setComponentAt(1, fullDisplayer);
	}
    }
}
