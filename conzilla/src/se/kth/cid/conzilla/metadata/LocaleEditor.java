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

import se.kth.cid.util.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

public class LocaleEditor extends JDialog
{
  class LocaleArrayModel extends AbstractListModel
  {
    Locale[] locs;

    String[] names;
    public LocaleArrayModel(Locale[] l)
      {
	this.locs = l;
	names = new String[l.length];
      }
    
    public int getSize()
      {
	return locs.length;
      } 

    public Object getElementAt(int i)
      {
	if(names[i] == null)
	  names[i] = locs[i].getDisplayName();
	return names[i];
      }

    public Locale getLocaleAt(int i)
      {
	return locs[i];
      }
    

  }

  LocaleManager manager;
  LocaleListener localeListener;
  
  public LocaleEditor(Component over)
    {
      super(JOptionPane.getFrameForComponent(over), "Languages", true);

      manager = LocaleManager.getLocaleManager();

      getContentPane().setLayout(new BorderLayout());



      
      Box right = new Box(BoxLayout.Y_AXIS);

      right.add(new JLabel("Available languages"));
      
      final JList availableList = new JList(new LocaleArrayModel(manager.getAvailableLocales()));
      //      availableList.setCellRenderer(new LocaleRenderer());
      
      right.add(new JScrollPane(availableList));

      final JButton add = new JButton("Add");
      add.setEnabled(false);
      add.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      manager.addLocale(getSelectedLocale(availableList));
	    }
	});
      right.add(add);

      availableList.addListSelectionListener(new ListSelectionListener()
	{
	  public void valueChanged(ListSelectionEvent e)
	    {
	      if(availableList.getSelectedIndex() != -1)
		add.setEnabled(true);
	    }
	});
      
	      


      
      Box left = new Box(BoxLayout.Y_AXIS);      

      left.add(new JLabel("Used languages"));
      
      final JList workingList = new JList(new LocaleArrayModel(manager.getLocales()));
      //      workingList.setCellRenderer(new LocaleRenderer());
      
      left.add(new JScrollPane(workingList));

      final JButton remove = new JButton("Remove");
      remove.setEnabled(false);
      remove.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      manager.removeLocale(getSelectedLocale(workingList));
	    }
	});
      left.add(remove);

      workingList.addListSelectionListener(new ListSelectionListener()
	{
	  public void valueChanged(ListSelectionEvent e)
	    {
	      if(workingList.getSelectedIndex() != -1)
		remove.setEnabled(true);
	    }
	});
      




      
      JSplitPane sPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					left, right);
      getContentPane().add(sPane, BorderLayout.CENTER);

      JToolBar tools = new JToolBar();
      tools.setFloatable(false);
      tools.add(Box.createHorizontalGlue());

      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      JButton close = new JButton("Close");
      close.addActionListener(new ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	    {
	      dispose();
	    }
	});
      
      tools.add(close);
      
      getContentPane().add(tools, BorderLayout.SOUTH);




      localeListener = new LocaleListener()
	{
	  public void localeAdded(LocaleEvent l)
	    {
	      workingList.setModel(new LocaleArrayModel(manager.getLocales()));
	      remove.setEnabled(false);
	    }

	  public void localeRemoved(LocaleEvent l)
	    {
	      workingList.setModel(new LocaleArrayModel(manager.getLocales()));
	      remove.setEnabled(false);
	    }
	  
	  public void setDefaultLocale(LocaleEvent l)
	    {
	      workingList.setModel(new LocaleArrayModel(manager.getLocales()));
	      availableList.setModel(new LocaleArrayModel(manager.getAvailableLocales()));
	      remove.setEnabled(false);
	      add.setEnabled(false);
	    }
	};
      manager.addLocaleListener(localeListener);



      
      setSize(300, 400);
      setLocationRelativeTo(over);
    }

  Locale getSelectedLocale(JList list)
    {
      return ((LocaleArrayModel)list.getModel()).getLocaleAt(list.getSelectedIndex());
    }
  
  public void dispose()
    {
      super.dispose();
      manager.removeLocaleListener(localeListener);
    }
}


