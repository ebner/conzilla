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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.util.*;
import se.kth.cid.identity.*;
import se.kth.cid.component.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.content.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.history.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.neuron.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;

/** Extends PopupHandler so that descriptions are
 *  displayed in #JWindows that are displayed 
 *  freely ontop of the ContentSelector.
 *
 *  @author Matthias Palmer
 */
public class PopupContentInfo extends PopupHandler
{
  static Border activeBorder;
  static Border inactiveBorder;
    
  /** Listens for moves in the map.
   */
  MouseInputAdapter mouseListener;
  PropertyChangeListener colorListener;
    //  PropertyChangeListener zoomListener;
  ComponentListener componentListener;
  
  java.awt.Component component;
  Point oldComponentLocation;
  JList list;
  int lastIndex;

  ContentSelector selector;

  Hashtable popups;
    //  AffineTransform    transform;
  boolean odd=false;

  PopupContentInfo(ContentSelector sel, JList selComp)
    {
	super();
	this.selector=sel;
	this.list=selComp;
	scale=1.0;
	//	transform = AffineTransform.getScaleInstance(1.0, 1.0);
	
	popups=new Hashtable();

	componentListener = new ComponentAdapter()
	    {
		public void componentMoved(ComponentEvent m)
		{
		    adjustAllPopups();
		}
		public void componentResized(ComponentEvent m)
		{
		    adjustAllPopups();
		}
	    };

	mouseListener = new MouseInputAdapter() {
		public void mouseMoved(MouseEvent e) {
		    updateDescription(e);
		}
		public void mouseExited(MouseEvent e) {
		    timer.stop();
		}
		public void mouseEntered(MouseEvent e) {
		    Tracer.debug("requesting focus from PopupContentInfo");
		    list.requestFocus();
		}
	    };
	colorListener=new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt)
		{
		    updateColors();
		}};

	/*	zoomListener=new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt)
		{
		    setScale(((Double) evt.getNewValue()).doubleValue(), ((Double) evt.getOldValue()).doubleValue());
		    }};*/
    }
    
  public void activate()
    {
	component=list;
	while (component.getParent()!=null)
	    component=component.getParent();
	
	list.addMouseMotionListener(mouseListener);
	list.addMouseListener(mouseListener);
	GlobalConfig.getGlobalConfig().addPropertyChangeListener(ListContentSelector.COLOR_POPUP_BACKGROUND, colorListener);
	GlobalConfig.getGlobalConfig().addPropertyChangeListener(ListContentSelector.COLOR_POPUP_BACKGROUND_ACTIVE, colorListener);
	GlobalConfig.getGlobalConfig().addPropertyChangeListener(ListContentSelector.COLOR_POPUP_TEXT, colorListener);
	GlobalConfig.getGlobalConfig().addPropertyChangeListener(ListContentSelector.COLOR_POPUP_TEXT_ACTIVE, colorListener);
    	GlobalConfig.getGlobalConfig().addPropertyChangeListener(ListContentSelector.COLOR_POPUP_BORDER, colorListener);
	GlobalConfig.getGlobalConfig().addPropertyChangeListener(ListContentSelector.COLOR_POPUP_BORDER_ACTIVE, colorListener);

	//	selector.getController().getZoomManager().addZoomListener(zoomListener);
	oldComponentLocation=component.getLocation();
	oldComponentLocation.translate(component.getSize().width, 0);

	component.addComponentListener(componentListener);
	registerKeybordActions(list);
	updateColors();
    }
  public void deactivate()
    {
	if (component==null)
	    return;

	GlobalConfig.getGlobalConfig().removePropertyChangeListener(ListContentSelector.COLOR_POPUP_BACKGROUND, colorListener);
	GlobalConfig.getGlobalConfig().removePropertyChangeListener(ListContentSelector.COLOR_POPUP_BACKGROUND_ACTIVE, colorListener);
	GlobalConfig.getGlobalConfig().removePropertyChangeListener(ListContentSelector.COLOR_POPUP_TEXT, colorListener);
	GlobalConfig.getGlobalConfig().removePropertyChangeListener(ListContentSelector.COLOR_POPUP_TEXT_ACTIVE, colorListener);
    	GlobalConfig.getGlobalConfig().removePropertyChangeListener(ListContentSelector.COLOR_POPUP_BORDER, colorListener);
	GlobalConfig.getGlobalConfig().removePropertyChangeListener(ListContentSelector.COLOR_POPUP_BORDER_ACTIVE, colorListener);

	//	selector.getController().getZoomManager().removeZoomListener(zoomListener);
	list.removeMouseListener(mouseListener);
	list.removeMouseMotionListener(mouseListener);
	component.removeComponentListener(componentListener);
	unRegisterKeyboardActions(list);
	removeAllPopups();
    }

  protected boolean isPopupTrigger(Object o)
    {
      MouseEvent e = (MouseEvent) o;
      return e.getID()!=MouseEvent.MOUSE_EXITED;
    }
  protected se.kth.cid.component.Component getComponentFromTrigger(Object o)
    {
      if (! (o instanceof MouseEvent))
	  return null;
      lastIndex=list.locationToIndex(((MouseEvent) o).getPoint());
      if (lastIndex!=-1)
	      return selector.getContent(lastIndex);

      return null;
    }

  protected Object getDescriptionLookupObject(Object o)
    {
	return getComponentFromTrigger(o);
    }
	
  protected void showNewDescriptionImpl(DescriptionPanel desc)
  {
      JWindow window=new JWindow();
      JPanel pane=new JPanel(new GridLayout());
	  /*	  {
	    public void paint(Graphics g)
	    {
	      Graphics2D gr     = (Graphics2D) g;      
	      AffineTransform f = gr.getTransform();
	      Tracer.debug("transform is "+transform);
	      gr.transform(transform);
	      //      setRenderingHints(gr);
	      
	      super.paint(g);
	      
	      gr.setTransform(f);
	    }
	    public Dimension getPreferredSize()
	    {
	      Dimension dim = super.getPreferredSize();
	      return new Dimension((int) ((dim.width)*scale), (int) ((dim.height)*scale));
	    }
	    };*/

      window.setContentPane(pane);

      pane.add(desc);

      desc.setOpaque(true);
      desc.setBackground(GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BACKGROUND_ACTIVE));
      desc.text.setColor(GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_TEXT_ACTIVE));
      desc.setBorder(activeBorder);

      popups.put(desc, window);
      list.setSelectedIndex(lastIndex);
  }
   
  protected void removeDescriptionImpl(DescriptionPanel desc)
  {
      JWindow window = (JWindow) popups.get(desc);
      if (window!= null)
	  {
	      popups.remove(desc);
	      window.getContentPane().remove(desc);
	      window.hide();
	  }
  }

  protected void activateOldDescriptionImpl(DescriptionPanel desc)
    {
	JWindow window = (JWindow) popups.get(desc);
	if (window==null)
	    return;
	window.toFront();
	desc.setBackground(GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BACKGROUND_ACTIVE));
	desc.text.setColor(GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_TEXT_ACTIVE));	
	desc.setBorder(activeBorder);
	list.setSelectedIndex(lastIndex);
	window.repaint();
    }

  protected void inActivateOldDescriptionImpl(DescriptionPanel desc)
    {
	JWindow window = (JWindow) popups.get(desc);
	if (window==null)
	    return;
	desc.setBackground(GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BACKGROUND));
	desc.text.setColor(GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_TEXT));
	desc.setBorder(inactiveBorder);
	window.repaint();
    }	

  protected void adjustPosition(JComponent comp, Object o)
  {
      if (! (o instanceof MouseEvent))
	  return;
      MouseEvent m = (MouseEvent) o;

      Point adjustpos = new Point(m.getX()+5, m.getY());
      SwingUtilities.convertPointToScreen(adjustpos, list);
      
      JWindow window = (JWindow) popups.get(comp);
      if (window==null)
	  return;

      Dimension dim = window.getContentPane().getPreferredSize();
      window.pack();

      window.repaint();      
      window.show();
      window.setLocation(adjustpos.x, adjustpos.y);
  } 

    protected void adjustAllPopups()
    {
	Point newComponentLocation=component.getLocation();
	newComponentLocation.translate(component.getSize().width, 0);
	int x=newComponentLocation.x-oldComponentLocation.x;
	int y=newComponentLocation.y-oldComponentLocation.y;
	
	Enumeration en=popups.elements();
	JWindow window;
	Point point;
	for (;en.hasMoreElements();)
	    {
		window=(JWindow) en.nextElement();
		point=window.getLocation();
		point.translate(x, y);
		window.setLocation(point);
		window.toFront();   //Don't seem to work. Neccessary??
		window.repaint();
	    }
	oldComponentLocation=newComponentLocation;
    }
  
  protected void updateColors()
    {
	Color inactive_back=GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BACKGROUND);
	Color active_back=GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BACKGROUND_ACTIVE);
	Color inactive_fore=GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_TEXT);
	Color active_fore=GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_TEXT_ACTIVE);

	Color inactive_border=GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BORDER);
	Color active_border=GlobalConfig.getGlobalConfig().getColor(ListContentSelector.COLOR_POPUP_BORDER_ACTIVE);
	inactiveBorder = new MatteBorder(1, 1, 1, 1, inactive_border);
	activeBorder = new MatteBorder(1, 1, 1, 1, active_border);

	Enumeration en=descriptions.elements();
	DescriptionPanel desc;
	for (;en.hasMoreElements();)
	    {
		desc = (DescriptionPanel) en.nextElement();
		desc.setBackground(inactive_back);
		desc.setBorder(inactiveBorder);
		desc.text.setColor(inactive_fore);
	    }
	
	if (description!=null)
	    {
		description.setBackground(active_back);
		description.text.setColor(active_fore);
		description.setBorder(activeBorder);
	    }
    }

  protected void setScaleImpl(double newscale, double oldscale)
    {
	Enumeration en=descriptions.elements();
	for (;en.hasMoreElements();)
	    ((DescriptionPanel) en.nextElement()).setScale(newscale);	

	//	transform = AffineTransform.getScaleInstance(newscale, newscale);

	en=popups.elements();
	JWindow window;
	Point listpos = new Point(0,0);
	SwingUtilities.convertPointToScreen(listpos, list);

	for (;en.hasMoreElements();)
	    {

		window = (JWindow) en.nextElement();

		double change = newscale/oldscale;
		int yorigo = (int) (listpos.y - 10*oldscale); //if list haven't been resized yet.
		int xorigo = listpos.x;
		int ny = yorigo + (int) (change*(window.getY()-yorigo));
		if (odd)
		    window.setLocation(window.getX()+1,ny);
		else
		    window.setLocation(window.getX()-1,ny);
		odd = !odd;

		window.pack();
	    }
	refresh();	
    }
  public void revalidate()
    {
	updateColors();

    }
  public void refresh()
  {}
}
