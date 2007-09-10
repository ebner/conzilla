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
import se.kth.cid.component.MetaData;
import se.kth.cid.component.MetaDataUtils;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.library.*;
import se.kth.cid.conzilla.util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;


public class ComponentEdit extends JFrame
{

  JDesktopPane desktop;
  Hashtable componentDialogs;


  Vector componentDialogDrafts;
  ConzillaKit kit;
  ComponentDialogFactory componentDialogFactory;

  boolean isExiting = false;

  JMenu componentMenu;
  
  public ComponentEdit(ConzillaKit kit)
    {
      super("ComponentEditor");
      this.kit=kit;
      componentDialogFactory = new ComponentDialogFactory(kit);

      desktop = new JDesktopPane();
      setContentPane(desktop);
      componentDialogs = new Hashtable();

      setDefaultCloseOperation(HIDE_ON_CLOSE);
      
      setJMenuBar(fixMenu());

      setSize(700, 500);
      setLocation(100, 100);
    }

  public JDesktopPane getDesktop()
    {
      return desktop;
    }
  
  private JMenuBar fixMenu()
    {
      MenuManager menuManager = PropertiesManager.getDefaultPropertiesManager().getMenuManager();

      JMenuBar mBar = new JMenuBar();
      JMenu menu = new JMenu("File");

      JMenuItem mi=menu.add(new AbstractAction("Save") {
	public void actionPerformed(ActionEvent ae) {
	  saveAll();
	}});
      menuManager.customizeButton(mi);

      mi=menu.add(new AbstractAction("Refresh") {
	public void actionPerformed(ActionEvent ae) {
	  if(askSaveAll())
	    refresh();
	}});
      menuManager.customizeButton(mi);

      mi=menu.add(new AbstractAction("Close") {
	  public void actionPerformed(ActionEvent ae) {
	    setVisible(false);
	  }});
      menuManager.customizeButton(mi);

      mi=mBar.add(menu);
      menuManager.customizeButton(mi);

      componentMenu = new JMenu("Components");

      mi=mBar.add(componentMenu);
      menuManager.customizeButton(mi);      

      return mBar;
    }

  JInternalFrame getInternalFrame(ComponentDialog cd)
    {
      for(Component c = cd.getParent(); c != null; c = c.getParent())
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
	      JInternalFrame jif=getInternalFrame(cd);
	      jif.moveToFront();
	  }
      }
    else
      {
	final ComponentDialog nda = componentDialogFactory.getComponentDialog(component, cl);
	
	componentDialogs.put(nda.getComponent().getURI(), nda);
	final JInternalFrame jif = new JInternalFrame(nda.getName(), true, true, true, true);
	
	PropertiesManager.getDefaultPropertiesManager().getMenuManager().customizeButton(jif);	
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
	PropertiesManager.getDefaultPropertiesManager().getMenuManager().customizeButton(componentItem);

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
		Tracer.debug("Removing view");
		componentDialogs.remove(nda.getComponent().getURI());
		Tracer.debug("Dialogs: " + componentDialogs.size());
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
  
  public void exit()
    {
      isExiting = true;
      
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
  
  public void refresh()
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
}

