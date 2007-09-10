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
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



class AggregationLevelCombo extends JPanel
{
  JComboBox box;
  JTextField area;

  int initialValue;
  
  public AggregationLevelCombo(int init, boolean editable)
    {
      setLayout(new FillLayout());
      this.initialValue = init;
      
      if(editable)
	{
	  box = new JComboBox(new DefaultComboBoxModel() {
	      public int getSize()
		{
		  return 4;
		}
	      public Object getElementAt(int i)
		{
		  return getString(i);
		}
	    });
	  box.setBackground(Color.white);
	  box.setSelectedIndex(init);
	  add(box);
	}
      else
	{
	  area = new JTextField(getString(init));
	  area.setEditable(false);
	  area.setBackground(Color.white);
	  add(area);
	}
    }

  String getString(int i)
    {
      switch(i)
	{
	case 0:
	  return "0 (atom)";
	case 1:
	  return "1 (atom set)";
	case 2:
	  return "2 (unit)";
	case 3:
	  return "3 (collection)";
	}
      return "???";
    }

  public int getAggregationLevel()
    {
      if(box != null)
	return box.getSelectedIndex();

      return initialValue;
    }
}


      

