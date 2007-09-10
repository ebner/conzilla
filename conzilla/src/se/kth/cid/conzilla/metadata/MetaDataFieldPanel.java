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
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class MetaDataFieldPanel extends JPanel
{

  public MetaDataFieldPanel(String title, Component metaDataEditor)
    {
      
      /* // The old behaviour
	Border compoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
	  BorderFactory.createLoweredBevelBorder());

      Border titled = BorderFactory.createTitledBorder(compoundBorder, title,
						       TitledBorder.ABOVE_TOP,
						       TitledBorder.LEFT, null, null);
      
      setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 5, 0), titled));

      */

      Border titled = BorderFactory.createTitledBorder(new EmptyBorder(0, 0, 5, 0), title,
						       TitledBorder.ABOVE_TOP,
						       TitledBorder.LEFT, null, null);
      
      setBorder(BorderFactory.createCompoundBorder(titled, new EmptyBorder(0, 20, 0, 0)));

      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

      add(metaDataEditor);
    }

  JComponent getMetaDataComponent()
    {
      return (JComponent) getComponents()[0];
    }
}
