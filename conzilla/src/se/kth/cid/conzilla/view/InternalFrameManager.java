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
import se.kth.cid.conzilla.menu.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

public class InternalFrameManager extends AbstractViewManager implements PropertyChangeListener
{ 
    JRootPane pane;
    JFrame frame;
    JDesktopPane desktop; 
    Hashtable bars;
    ConzillaSplitPane conzillaSplitPane;

    public InternalFrameManager()
    {}

    public String getID()
    {
	return "INTERNAL_FRAME_VIEW";
    }
    
    public void initManager()
    {
	super.initManager();
	frame = new JFrame("Conzilla");
	desktop = new JDesktopPane();
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
    }


    
    public void detachManager()
    {
	super.detachManager();
	bars = null;
	conzillaSplitPane.detach();
	conzillaSplitPane = null;
	desktop = null;
	frame.dispose();
	frame = null;
    }


  public View newView(MapController controller)
    {
	InternalFrameView fv = new InternalFrameView(this, controller);
	desktop.add(fv);
	addView(fv);
	controller.addPropertyChangeListener(this);
	fv.setSize(450,300);
	fv.setLocation(50,50);
	fv.setVisible(true);
	fv.setResizable(true);
	fv.setMaximizable(true);
	fv.setIconifiable(true);
	//	fv.setMaximum(true);
	fv.moveToFront();

	JMenuBar bar = 	makeMenuBar(controller);
	bars.put(fv, bar);
	
	activateInternalFrame(fv);
	
	//FIXME: ad  hoc, right place to do it?
	//	fv.setLocation(100, 100);
	//	fv.setSize(100, 100);
	return fv;
    }
    public void activateInternalFrame(InternalFrameView fv)
    {
	MapController controller = fv.getController();
        conzillaSplitPane.setPanes(desktop, controller.getContentSelector());
	pane.setJMenuBar((JMenuBar) bars.get(fv));
	pane.invalidate();
	pane.validate();
	pane.repaint();
    }

    protected void closeView(View v, boolean closeController)
    {
	v.getController().removePropertyChangeListener(this);
	JMenuBar bar = (JMenuBar) bars.get(v);

	bars.remove(v);
	((InternalFrameView) v).close(closeController);

	JInternalFrame[] frames = desktop.getAllFrames();
	for(int i = 0; i < frames.length; i++)
	    {
		if(!frames[i].isIcon())
		    {
			frames[i].toFront();
			try { 
			    frames[i].setSelected(true);
			} catch(PropertyVetoException e)
			    {}
			frame.invalidate();
			frame.validate();
			frame.repaint();
			return;
		    }
	    }
	pane.setJMenuBar(new JMenuBar());
	frame.invalidate();
	frame.validate();
	frame.repaint();
    }

    public void propertyChange(PropertyChangeEvent e)
    {
	if(e.getPropertyName().equals(MapController.MENUS_PROPERTY))
	    {
		MapController mc = (MapController) e.getSource();
		InternalFrameView view = (InternalFrameView) getView(mc);
		bars.put(view, makeMenuBar(mc));
		InternalFrameView actview = (InternalFrameView) desktop.getSelectedFrame();
		activateInternalFrame(actview);
	    }
    }
}
