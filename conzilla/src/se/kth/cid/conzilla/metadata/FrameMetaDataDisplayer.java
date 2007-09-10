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
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Rectangle;

public class FrameMetaDataDisplayer extends JFrame implements MetaDataDisplayer
{
  MetaDataDisplayerComponent displayer;

  JToggleButton editable;

  Component component;
  ComponentSaver saver;

  boolean ignorePress = false;

  EditListener editListener;
  
  public FrameMetaDataDisplayer(ComponentSaver saver)
  {
    super("MetaData");
    this.saver = saver;
    
    addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent ev)
	{
	  Tracer.debug("CLOSED!!!");
	  showMetaData(null);
	}
      });
    
    displayer = new MetaDataDisplayerComponent();
    JScrollPane scroll = new JScrollPane(displayer);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(scroll, BorderLayout.CENTER);

    JButton close = new JButton("Close");
    close.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e)
	{
	  showMetaData(null);
	}
      });

    editable = new JToggleButton("Edit");
    editable.addItemListener(new ItemListener()
      {
	public void itemStateChanged(ItemEvent e)
	{
	  if(ignorePress)
	    return;
	  if(e.getStateChange() == ItemEvent.SELECTED)
	    editPress(true);
	  else
	    editPress(false);
	}
      });
    
    JPanel buttons = new JPanel();

    buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));

    buttons.add(editable);
    buttons.add(close);
    
    getContentPane().add(buttons, BorderLayout.SOUTH);

    editListener = new EditListener()
      {
	public void componentEdited(EditEvent e)
	{
	  if(e.getEditType() == Component.EDITABLE_CHANGED)
	    updateEditButton();
	}
      };
  }


  void editPress(boolean b)
  {
    component.setEditable(b);
  }

  void updateEditButton()
  {
    ignorePress = true;
    if(component.isEditable())
      {
	editable.setSelected(true);
	editable.setEnabled(true);
      }
    else
      {
	if(saver != null && component.isEditingPossible() && saver.isComponentSavable(component))
	  {
	    editable.setEnabled(true);
	    editable.setSelected(false);
	  }
	else
	  {
	    editable.setEnabled(false);
	    editable.setSelected(false);
	  }
      }
    ignorePress = false;
  }
  
  public void showMetaData(Component comp)
  {
    if(component != null)
      component.removeEditListener(editListener);
    
    component = comp;
    
    displayer.showMetaData(comp);
    if(component != null)
      {
	component.addEditListener(editListener);
	updateEditButton();
	displayer.revalidate();
	pack();
	setVisible(true);
	repaint();
      }
    else
      {
	if(isVisible())
	  setVisible(false);
      }
  }
}
