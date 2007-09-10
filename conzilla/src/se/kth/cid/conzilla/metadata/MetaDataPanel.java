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
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


public class MetaDataPanel extends JPanel
{
  GridBagConstraints contraints;

  Component glue;
  
  public MetaDataPanel()
    {
      setLayout(new GridBagLayout());

      glue = Box.createVerticalGlue();
      
      contraints = new GridBagConstraints();
      contraints.gridx = 0;
      contraints.gridy = GridBagConstraints.RELATIVE;
      contraints.weighty = 0.0;
      contraints.weightx = 1.0;
      contraints.fill = GridBagConstraints.BOTH;
    }

  public void addPanel(String title, Component panel)
    {
      remove(glue);
      add(new MetaDataFieldPanel(title, panel), contraints);

      contraints.weighty = 1.0;
      add(glue, contraints);
      contraints.weighty = 0.0;
      revalidate();
      repaint();
    }  

  public void removePanel(Component panel)
    {
      remove(panel.getParent());
    }
}

