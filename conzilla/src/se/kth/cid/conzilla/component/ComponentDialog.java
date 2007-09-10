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


package se.kth.cid.conzilla.component;
import javax.swing.*;
import javax.swing.event.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.component.local.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.library.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;


/** This class holds the basic functionality for editing a component,
 *  it is recomended to inherit from this class.
 *
 *  @see ComponentEditor
 */
public class ComponentDialog extends JPanel
  implements EditListener, MetaDataEditListener
{
  se.kth.cid.component.Component component=null;
  String uri;
  
  JInternalFrame internalFrame;
  
  protected StringPanel componentURIField;
  protected MetaDataPanel componentPanel;
  protected JScrollPane scroll;  

  protected PanelMetaDataDisplayer metaDataEditor;
  protected JButton save;

  protected ConzillaKit kit;
  protected JToolBar toolBar;

  JButton setButton;
  JButton saveButton;
  
  public ComponentDialog(ConzillaKit kit)
    {
      super();
      this.kit=kit;

      setLayout(new BorderLayout());

      componentURIField = new StringPanel("", false, false, null, null);

      componentPanel = new MetaDataPanel();

      ToolBarManager toolBarManager = PropertiesManager.getDefaultPropertiesManager().getToolBarManager();

      
      toolBar = new JToolBar();
      toolBar.setFloatable(false);
      toolBar.setBorder(null);
      toolBar.setMargin(new Insets(0, 0, 0, 0));
      
      JToolBar totalBar = new JToolBar();
      totalBar.setFloatable(false);
      totalBar.setBorder(null);
      totalBar.setMargin(new Insets(0, 0, 0, 0));

      totalBar.add(toolBar);
      totalBar.add(Box.createHorizontalGlue());

      setButton = new JButton("Set");
      setButton.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    set();
	  }});
      toolBarManager.customizeButton(setButton);

      saveButton = new JButton("Save");
      saveButton.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    save();
	  }});
      toolBarManager.customizeButton(saveButton);      

      JButton copy = new JButton("Copy");
      copy.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    ClipboardLibrary cl = ComponentDialog.this.kit.getConzillaEnvironment().getRootLibrary().getClipboardLibrary();
	    if (component != null)
	      cl.setComponent(component);
	  }});
      toolBarManager.customizeButton(copy);      
      
      String [] comboChoices={"MetaData", "Component"};
      JComboBox tabs=new JComboBox(comboChoices);
      tabs.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 JComboBox cb = (JComboBox)e.getSource();
                 if (cb.getSelectedIndex()==0)
		     setMetaDataTab();
		 else
		     setComponentTab();
             }
         });
      toolBarManager.customizeButton(tabs);
      
      toolBarManager.customizeButtonBorderHeight(copy, 25);
      toolBarManager.customizeButtonBorderHeight(setButton, 25);
      toolBarManager.customizeButtonBorderHeight(saveButton, 25);

      toolBar.add(copy);
      toolBar.add(tabs);

      totalBar.add(setButton);
      totalBar.add(saveButton);

      add(totalBar, BorderLayout.SOUTH);

    }

  
  protected String getComponentString()
    {
      return "Component";
    }
  
  protected void createComponentTab()
    {	
      componentURIField.setText(component.getURI());

      scroll = new JScrollPane(componentPanel);

      componentPanel.addPanel("Component URI", componentURIField);
    }
  protected void setComponentTab()
    {
	remove(metaDataEditor);
	add(scroll, BorderLayout.CENTER);
	revalidate();
	repaint();
    }
  protected void setMetaDataTab()
    {
	remove(scroll);
	add(metaDataEditor, BorderLayout.CENTER);
	revalidate();
	repaint();
    }
    
  public void fieldEdited(MetaDataEditEvent e)
    {
      setButton.setEnabled(true);
      saveButton.setEnabled(true);
    }
  
  
  public void componentEdited(EditEvent e)
    {
      updateSaveTool();
    }

  void updateSaveTool()
    {
      saveButton.setEnabled(component.isEdited());
    }
  
  protected void createMetaDataTab()
    {
      metaDataEditor = new PanelMetaDataDisplayer(component, component.isEditable());
      metaDataEditor.addMetaDataEditListener(this);
    }
      
  public se.kth.cid.component.Component getComponent()
    {
      return component;
    }

  public void setComponent(se.kth.cid.component.Component component)
    {
      if (this.component != null)
	  return;
      this.component=component;
      
      createComponentTab();
      createMetaDataTab();
      setMetaDataTab();
      
      MetaData md = component.getMetaData();
      String title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
      if(title != null && title.length() != 0)
	setName(getComponentString() + ": " + title);
      else
	setName(getComponentString());

      component.addEditListener(this);
      updateSaveTool();
      setButton.setEnabled(false);
    }

  public void resetComponent(se.kth.cid.component.Component component)
    {
      remove(scroll);
      remove(metaDataEditor);
      this.component.removeEditListener(this);
      this.component = null;
      componentPanel.removeAll();

      if (metaDataEditor != null)
	metaDataEditor.detach();
      metaDataEditor = null;      

      setComponent(component);
    }
  
  public final void set()
  {
    if (metaDataEditor != null)
      metaDataEditor.storeMetaData();
    setImpl();
    setButton.setEnabled(false);
  }
   
  protected void setImpl() {}

  public final void save()
  {
    set();
    try {
      if (component.isEdited())
	kit.getComponentStore().getHandler().saveComponent(component);
      saveButton.setEnabled(false);
    } catch (ComponentException ce){
      ErrorMessage.showError("Save Error", "Failed to save component\n\n"
			     + component.getURI(),
			     ce, this);
    }
  }
    //necessary??????????
  public ComponentDialog copy()
    {
      ComponentDialog cd = new ComponentDialog(kit);
      //maybe copy stuff...
      return cd;
    }


  public boolean needAskSave()
    {
      return saveButton.isEnabled();
    }
  
  public void detach()
    {
      component.removeEditListener(this);
      component = null;
      removeAll();
      componentPanel.removeAll();
      
      if (metaDataEditor != null)
	metaDataEditor.detach();
      metaDataEditor = null;
    }
}
