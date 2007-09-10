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
import se.kth.cid.content.*;
import se.kth.cid.util.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ListContentSelector extends JList implements ContentSelector
{
  ContentDescription[] content;
  Vector selectionListeners;

  //  ListSelectionListener listener;
  
  class ContentListModel extends AbstractListModel
  {
    public ContentListModel()
    {
    }

    public int getSize()
    {
      return (content != null) ? content.length : 0;
    }

    public Object getElementAt(int index)
    {
      return content[index].getNeuron().getMetaData().getValue("Title");
    } 
  }
  
  public ListContentSelector()
  {
    selectionListeners = new Vector();
    
    /*    listener = new ListSelectionListener() {
	public void valueChanged(ListSelectionEvent e)
	{
	  if(e.getValueIsAdjusting() || getSelectedIndex() == -1)
	    return;
	  int index = getSelectedIndex();
	  clearSelection();
	  fireSelectionEvent(content[index]);
	}
	}; */
    MouseListener mouseListener = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
	int index = locationToIndex(e.getPoint());
	if (index!=-1)
	  fireSelectionEvent(new ContentEvent(content[index],e));
      }
    };
    addMouseListener(mouseListener);
  }
  
  public void selectContent(ContentDescription[] content)
  {
    this.content = content;
    //    removeListSelectionListener(listener);
    setModel(new ContentListModel());
    //    addListSelectionListener(listener);
  }

  public void addSelectionListener(SelectionListener l)
  {
    selectionListeners.addElement(l);
  }

  public void removeSelectionListener(SelectionListener l)
  {
    selectionListeners.removeElement(l);
  }

  public void fireSelectionEvent(ContentEvent c)
  {
    for(int i = 0; i < selectionListeners.size(); i++)
      ((SelectionListener) selectionListeners.elementAt(i)).contentSelected(c);
  }
}
