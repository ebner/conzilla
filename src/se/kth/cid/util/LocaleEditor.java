/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package  se.kth.cid.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    PropertyChangeListener listener;

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


      listener = new PropertyChangeListener()
	  {
	      public void propertyChange(PropertyChangeEvent e)
	      {
		  if(e.getPropertyName().equals(LocaleManager.DEFAULT_LOCALE_PROPERTY))
		      {
			  availableList.setModel(new LocaleArrayModel(manager.getAvailableLocales()));
			  add.setEnabled(false);
		      }
		  workingList.setModel(new LocaleArrayModel(manager.getLocales()));
		  remove.setEnabled(false);
	      }
	  };
    
	      
      manager.addPropertyChangeListener(listener);



      
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
      manager.removePropertyChangeListener(listener);
    }
}


