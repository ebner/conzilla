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


package se.kth.cid.conzilla.view;
import se.kth.cid.conzilla.app.*;
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

public class TabManager extends AbstractViewManager implements ChangeListener, PropertyChangeListener
{ 
    JFrame frame;
    JRootPane pane;
    JTabbedPane tabbedPane;
    Hashtable bars;
    ConzillaSplitPane conzillaSplitPane;
    Hashtable tab2View;
    boolean block;
    
    public TabManager()
    {}

    public String getID()
    {
	return "TAB_VIEW";
    }
    

    public void initManager()
    {
	super.initManager();
	frame = new JFrame("Conzilla");
	tabbedPane = new JTabbedPane();
	conzillaSplitPane = new ConzillaSplitPane();

	//Pane workaround for mac, mac-menus is moved up and can't contain buttons.
	pane = new JRootPane();
	pane.setContentPane(conzillaSplitPane);
	frame.setContentPane(pane);
	
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    closeViews();
		}});

	frame.setSize(700, 500);
	frame.setLocation(0, 0);
	frame.show();
	bars = new Hashtable();
	tab2View = new Hashtable();
	tabbedPane.addChangeListener(this);
	LocaleManager.getLocaleManager().addPropertyChangeListener(this);
    }
    
    public void detachManager()
    {
	super.detachManager();
	frame.dispose();
	frame = null;
	tabbedPane.removeAll();
	tabbedPane = null;
	conzillaSplitPane.detach();
	conzillaSplitPane = null;

	bars = null;
	tab2View = null;
	LocaleManager.getLocaleManager().removePropertyChangeListener(this);
    }



  public View newView(MapController controller)
    {
	block = true;
	TabView tv = new TabView(this, controller);

	tabbedPane.add(getTitle(controller), controller.getMapPanel());
	tabbedPane.setSelectedComponent(controller.getMapPanel());
	addView(tv);
	
	controller.addPropertyChangeListener(this);
	JMenuBar bar = makeMenuBar(controller);
	bars.put(tv, bar);
	tab2View.put(controller.getMapPanel(), tv);


	activateTab(tv);
	
	block = false;
	updateTitle(tv);
	return tv;
    }
    public void activateTab(TabView tv)
    {
	MapController controller = tv.getController();
        conzillaSplitPane.setPanes(tabbedPane, controller.getContentSelector());
	pane.setJMenuBar((JMenuBar) bars.get(tv));
	controller.getMapPanel().setPreferredSize(null);

	if("true".equals(GlobalConfig.getGlobalConfig().getProperty(Conzilla.PACK_PROP)))
	    tv.pack();


	pane.invalidate();
	pane.validate();
	pane.repaint();
    }

    protected void updateFonts()
    {
	SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(frame));
    }

    protected void pack()
    {
	JComponent active = (JComponent) tabbedPane.getSelectedComponent();	
	Dimension pref = active.getPreferredSize();
	Enumeration en = tab2View.keys();
	while(en.hasMoreElements())
	    {
		JComponent c = (JComponent) en.nextElement();
		if(c != active)
		    c.setPreferredSize(pref);
	    }
	frame.pack();
    }

    public void stateChanged(ChangeEvent ce)
    {
	if (block || (tabbedPane.getSelectedComponent() == null))
	    return;
	TabView view =((TabView) tab2View.get(tabbedPane.getSelectedComponent()));
	activateTab(view);
    }

    public void updateTitle(TabView view)
    {
	int index = tabbedPane.indexOfComponent(view.getController().getMapPanel());
	tabbedPane.setTitleAt(index, getTitle(view.getController()));
    }

  String getTitle(MapController controller)
    {
      String title = "(none)";      
      if(controller.getMapScrollPane() != null)
	{
	  ConceptMap map = controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	  
	  se.kth.cid.component.MetaData md = map.getMetaData();
	  title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	}
      return title;
    }

  public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(LocaleManager.DEFAULT_LOCALE_PROPERTY))
	    {
		Iterator en = getViews();
		while (en.hasNext())
		    updateTitle((TabView) en.next());
	    }
	else if(e.getPropertyName().equals(MapController.MENUS_PROPERTY))
	    {
		MapController mc = (MapController) e.getSource();
		TabView view = (TabView) getView(mc);
		bars.put(view, makeMenuBar(mc));
		TabView actview =((TabView) tab2View.get(tabbedPane.getSelectedComponent()));
		activateTab(actview);
	    }
    }

    protected void closeView(View v, boolean closeController)
    {
	JMenuBar bar = (JMenuBar) bars.get(v);
	tabbedPane.remove(v.getController().getMapPanel());

	v.getController().getMapPanel().setPreferredSize(null);

	bars.remove(v);
	
	v.getController().removePropertyChangeListener(this);
	tab2View.remove(v.getController().getMapPanel());

	java.awt.Component c = tabbedPane.getSelectedComponent();
	if(c != null)
	    {
		TabView view = (TabView) tab2View.get(c);
		activateTab(view);
	    }
	((TabView) v).close(closeController);
    }
}
