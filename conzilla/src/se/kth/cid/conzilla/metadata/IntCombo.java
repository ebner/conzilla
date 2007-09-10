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



public class IntCombo extends JPanel implements MetaDataFieldEditor
{
  JComboBox box;
  StringPanel area;

  int initialValue;

  boolean edited = false;

  String metaDataField;
  MetaDataEditListener editListener;
  

  public IntCombo(int init, final String[] choices, boolean editable, MetaDataEditListener editListener, String metaDataField)
    {
      this.editListener = editListener;
      this.metaDataField = metaDataField;
      setLayout(new FillLayout());
      this.initialValue = init;
      
      if(editable)
	{
	  box = new JComboBox(new DefaultComboBoxModel() {
	      public int getSize()
		{
		  return choices.length + 1;
		}
	      public Object getElementAt(int i)
		{
		  if(i == 0)
		    return "(none)";
		  
		  return choices[i - 1];
		}
	    });
	  box.setRenderer(new StringRenderer());
	  box.setBackground(Color.white);
	  box.setSelectedIndex(init + 1);
	  box.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e)
		{
		  if(e.getStateChange() == ItemEvent.SELECTED)
		    fireEdited();
		}
	    });
		  
	  add(box);
	}
      else
	{
	  String choice = "(none)";
	  if(init >= 0)
	    choice = choices[init];
	  area = new StringPanel(choice, false, false, editListener, metaDataField);
	  add(area);
	}
    }

  public boolean isEdited()
    {
      return edited;
    }

  public void detach()
    {
    }
  
  public int getInt(boolean resetEdited)
    {
      if(resetEdited)
	edited = false;

      if(box != null)
	return box.getSelectedIndex() - 1;

      return initialValue;
    }

  void fireEdited()
    {
      edited = true;
      if(editListener != null)
	editListener.fieldEdited(new MetaDataEditEvent(null, metaDataField));
    }
}


      

