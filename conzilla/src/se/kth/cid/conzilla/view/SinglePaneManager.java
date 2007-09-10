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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

public class SinglePaneManager extends AbstractViewManager
{ 
    JRootPane pane;
    View view;
    ConzillaSplitPane conzillaSplitPane;
    //    boolean block;
    
    public SinglePaneManager()
    {
	pane = new JRootPane();
    }

    public JComponent getSinglePane()
    {
	return pane;
    }

    public String getID()
    {
	return "SINGLE_PANE_VIEW";
    }
    
    public void initManager()
    {
	super.initManager();
	conzillaSplitPane = new ConzillaSplitPane();
	pane.setContentPane(conzillaSplitPane);
	
	//	LocaleManager.getLocaleManager().addPropertyChangeListener(this);
    }
    
    public void detachManager()
    {
	super.detachManager();

	conzillaSplitPane.detach();
	conzillaSplitPane = null;

	//	LocaleManager.getLocaleManager().removePropertyChangeListener(this);
    }


  public View newView(MapController controller)
    {
	return newView(controller, "onlyview");
    }

  public View newView(MapController controller, String id)
    {
	if (view != null)
	    return null;
	//	block = true;
	view = new DefaultView(controller);

        conzillaSplitPane.setPanes(controller.getMapPanel(), controller.getContentSelector());
	addView(view);
	
	JMenuBar bar = makeMenuBar(controller);
	pane.setJMenuBar(bar);

	//	block = false;
	//	updateTitle(tv);
	return view;
    }

    /*    public void updateTitle(View view)
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
	  
	  MetaData md = map.getMetaData();
	  title = MetaDataUtils.getLocalizedString(md.get_metametadata_language(), md.get_general_title()).string;
	}
      return title;
    }

  public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(LocaleManager.DEFAULT_LOCALE_PROPERTY))
	    {
		Enumeration en = getViews();
		while (en.hasMoreElements())
		    updateTitle((View) en.nextElement());
	    }
    }
    */
    protected void closeView(View v, boolean closeController)
    {}

}
