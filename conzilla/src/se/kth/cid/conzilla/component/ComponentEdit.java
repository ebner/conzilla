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
import se.kth.cid.neuron.*;
import se.kth.cid.component.*;
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.browse.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;


public class ComponentEdit extends JFrame implements EditListener, Extra
{

  JDesktopPane desktop;
  Hashtable componentDialogs;


  Vector componentDialogDrafts;
  ConzillaKit kit;
  ComponentDialogFactory componentDialogFactory;

  boolean isExiting = false;

  JMenu componentMenu;
    
    public ComponentEdit()
    {
	super("ComponentEditor");
    }
    
    public boolean initExtra(ConzillaKit kit)
    {
	this.kit=kit;

	kit.getComponentStore().getCache().addGlobalEditListener(this);
	componentDialogFactory = new ComponentDialogFactory(kit);
	
	desktop = new JDesktopPane();
	setContentPane(desktop);
	componentDialogs = new Hashtable();
	
	setDefaultCloseOperation(HIDE_ON_CLOSE);
	
	setJMenuBar(fixMenu());
	
	setSize(700, 500);
	setLocation(100, 100);
	return true;
    }

    /*  public static String getID()
    {
	return "componentedit";
    }
    */
    public void extendMenu(ToolsMenu menu, MapController c)
    {
	if(menu.getName().equals(DefaultMenuFactory.TOOLS_MENU))
	    {
		menu.addTool(new Tool("COMPONENT_EDITOR", ComponentEdit.class.getName())
		    {
			public void actionPerformed(ActionEvent ae)
			{
			    Tracer.debug("Open component editor");
			    ComponentEdit.this.show();
			}
		    }, 200);
	    }
	else if(menu.getName().equals(BrowseMapManagerFactory.BROWSE_MENU))
	    {
		((MapToolsMenu)menu).addMapMenuItem(new NeuronDisplayerMapTool(c, this), 400);
	    }
	else if(menu.getName().equals(EditMapManagerFactory.EDIT_MENU_NEURON)
		|| menu.getName().equals(EditMapManagerFactory.EDIT_MENU_AXON)
		|| menu.getName().equals(EditMapManagerFactory.EDIT_MENU_MAP))
	    {
		((MapToolsMenu)menu).addMapMenuItem(new NeuronDisplayerMapTool(c, this), 50);
	    }
	else if(menu.getName().equals(ContentMenu.CONTENT_MENU))
	    {
		final ContentMenu cm = (ContentMenu) menu;
		final MapController mc = c;
		cm.addTool(new ContentTool("EDIT", ComponentEdit.class.getName()) {
			protected boolean updateEnabled()
			{ 
			    se.kth.cid.component.Component comp = mc.getContentSelector().getContent(contentIndex);
			    return comp.isEditable();
			}
			public void actionPerformed(ActionEvent e)
			{
			    se.kth.cid.component.Component comp = mc.getContentSelector().getContent(contentIndex);
			    ComponentEdit.this.editComponent(comp, ContentDialog.class, true);
			    ComponentEdit.this.show();
			}}, 300); 
	    }
    }

  public void addExtraFeatures(final MapController controller, final Object o, String location, String hint)
      {}
    
    
  public boolean saveExtra()
    {
	return askSaveAll();
    }

  public JDesktopPane getDesktop()
    {
      return desktop;
    }
  
  private JMenuBar fixMenu()
    {
	ConzillaResourceManager menuManager = ConzillaResourceManager.getDefaultManager();

      JMenuBar mBar = new JMenuBar();
      JMenu menu = new JMenu("File");

      JMenuItem mi=menu.add(new AbstractAction() {
	public void actionPerformed(ActionEvent ae) {
	  saveAll();
	}});
      menuManager.customizeButton(mi, ComponentEdit.class.getName(), "SAVE");

      mi=menu.add(new AbstractAction() {
	public void actionPerformed(ActionEvent ae) {
	  if(askSaveAll())
	    refreshExtra();
	}});
      menuManager.customizeButton(mi, ComponentEdit.class.getName(), "REFRESH");

      mi=menu.add(new AbstractAction() {
	  public void actionPerformed(ActionEvent ae) {
	    setVisible(false);
	  }});
      menuManager.customizeButton(mi, ComponentEdit.class.getName(), "CLOSE");

      mi=mBar.add(menu);
      menuManager.customizeButton(mi, ComponentEdit.class.getName(), "FILE");

      componentMenu = new JMenu();

      mi=mBar.add(componentMenu);
      menuManager.customizeButton(mi, ComponentEdit.class.getName(), "COMPONENTS");

      return mBar;
    }

  JInternalFrame getInternalFrame(ComponentDialog cd)
    {
      for(java.awt.Component c = cd.getParent(); c != null; c = c.getParent())
	{
	  if(c instanceof JInternalFrame)
	    return (JInternalFrame) c;
	}
      Tracer.bug("No Internal frame for ComponentDialog!");
      return null; //Never reached
    }

    
  public void editComponent(se.kth.cid.component.Component component,
			    boolean toFront)
    {
      editComponent(component, null, toFront);
    }
  

  public void editComponent(se.kth.cid.component.Component component, java.lang.Class cl, boolean toFront)
    {
      /*    if (!component.isEditable())
      {
	Tracer.bug("Component not editable, and hencefort not viewable in the componentEditor.");
	}*/
    ComponentDialog cd = (ComponentDialog) componentDialogs.get(component.getURI());
    if (cd != null)
      {
	if(toFront)
	  {
	      show();
	      JInternalFrame jif=getInternalFrame(cd);
	      jif.moveToFront();
	  }
      }
    else
      {
	final ComponentDialog nda = componentDialogFactory.getComponentDialog(component, cl);
	if (nda == null)
	    return;
	componentDialogs.put(nda.getComponent().getURI(), nda);
	final JInternalFrame jif = new JInternalFrame(nda.getName(), true, true, true, true);
	
	jif.setContentPane(nda);
	desktop.add(jif);

	jif.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
	final JMenuItem componentItem = new JMenuItem(nda.getName());
	componentItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      if(jif.isIcon())
		try {
		  jif.setIcon(false);
		} catch(PropertyVetoException ex) { }
	      
	      jif.moveToFront();
	    }});
	componentMenu.add(componentItem);

	jif.addVetoableChangeListener(new VetoableChangeListener() {
	    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
	      {
		if(isExiting)
		  return;
		if(JInternalFrame.IS_CLOSED_PROPERTY.equals(e.getPropertyName()) && ((Boolean) e.getNewValue()).booleanValue() && !askSave(nda))
		  {
		    throw new PropertyVetoException("User denied close", e);
		  }
	      }});
	jif.addInternalFrameListener(new InternalFrameAdapter() {
	    public void internalFrameClosed(InternalFrameEvent e)
	      {
		componentDialogs.remove(nda.getComponent().getURI());
		nda.detach();
		componentMenu.remove(componentItem);
	      }});
	jif.setSize(450,300);
	jif.setLocation(50,50);
	jif.setVisible(true);
	jif.moveToFront();

	try {
	    jif.setMaximum(true);
	} catch (PropertyVetoException e)
	    {
		Tracer.bug("The InternalFrame has Vetoed maximizing, but still we try to maximize!!!");
	    }
      }
  }
    
  boolean askSave(ComponentDialog nd)
    {
      se.kth.cid.component.Component component = nd.getComponent();
      
      if (nd.needAskSave())
	{
	  setVisible(true);
	  getInternalFrame(nd).moveToFront();
	  MetaData md = component.getMetaData();
	  String str = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	  if (str == null || str.equals(""))
	    str = component.getURI();
	  
	  int ans = JOptionPane.showConfirmDialog(desktop, "Component "+str
						  +"\n isn't saved, save now?");
	  if (ans == JOptionPane.YES_OPTION)
	    nd.save();
	  else if (ans == JOptionPane.CANCEL_OPTION)
	    return false;
	}
      return true;
    }



  public boolean askSaveAll()
    {
      Enumeration en = componentDialogs.elements();
      for(;en.hasMoreElements();)
	if (!askSave((ComponentDialog) en.nextElement()))
	  return false;
      return true;
    }

  public void saveAll()
    {
      Enumeration en = componentDialogs.elements();
      for(;en.hasMoreElements();)
	((ComponentDialog) en.nextElement()).save();
    }
  
  public void exitExtra()
    {
      isExiting = true;
      
      kit.getComponentStore().getCache().removeGlobalEditListener(this);

      Enumeration en = componentDialogs.elements();
      for(;en.hasMoreElements();)
	getInternalFrame(((ComponentDialog) en.nextElement())).dispose();

      componentDialogs = null;
      dispose();
    }

  public se.kth.cid.component.Component getSelected()
    {
      Enumeration en = componentDialogs.elements();
      for (;en.hasMoreElements();)
	{
	  ComponentDialog cd = (ComponentDialog) en.nextElement();
	  if (getInternalFrame(cd).isSelected())
	    return cd.getComponent();
	}
      return null;
    }
  
  public void refreshExtra()
    {
      Enumeration en = componentDialogs.elements();
      for (;en.hasMoreElements();)
	{
	  ComponentDialog cd = (ComponentDialog) en.nextElement();
	  String uri = cd.getComponent().getURI();
	  se.kth.cid.component.Component comp = null;
	  try {
	    comp = kit.getComponentStore().getAndReferenceComponent(URIClassifier.parseValidURI(uri));
	  } catch (se.kth.cid.component.ComponentException ce)
	    {
	      ErrorMessage.showError("Reload Error",
				     "Could not reload component\n\n" + uri,
				     ce, null);
	      Tracer.trace("Couldn't reload component "+uri+" when reloading"+
			   ce.getMessage(), Tracer.MINOR_EXT_EVENT);
	    }

	  if(comp != null && comp.isEditable())
	    cd.resetComponent(comp);
	  else
	    getInternalFrame(cd).dispose();
	}
    }
    
    public void componentEdited(EditEvent e)
    {
	editComponent(e.getComponent(), false);
    }
}

