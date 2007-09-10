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
package se.kth.cid.conzilla.browse;
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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;


/** Extends PopupHandler so that descriptions are
 *  displayed inside a JPanel wich are in turn
 *  added ontop of the MapDisplayer.
 *  (In the MapScrollPane).
 *
 *  @author Matthias Palmer
 */
public class PopupLayer extends PopupHandler 
{
    static Border  activeBorder;
    static Border  inactiveBorder;
    final static Border TRANSLUCENT_BORDER = new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0, 0.9f));
    final static Color  TRANSLUCENT_COLOR = new Color(0.9f, 0.9f, 1.0f, 0.7f);
    final static Color  TRANSLUCENT_COLOR_ACTIVE = new Color(204 ,204 , 255, 200);
    
  boolean subMap;  
  MapDisplayer activeSubMap;

  MapController controller;


  PropertyChangeListener colorListener;

    //  PropertyChangeListener zoomListener;

  /** Listens for moves in the map.
   */
  MapEventListener cursorListener;    

  /** Listens for clicks in the map.
   */
  MapEventListener klickListener;

  JLayeredPane panel;

  PopupLayer(MapController controller)
    {
	super();

	this.controller=controller;

	panel = new JLayeredPane()
	  {
	    public void print(Graphics g)
	    {
	      updateColors(false);
	      super.print(g);
	      updateColors(true);
	    }
	  };
	panel.setOpaque(false);
	panel.setLayout(null);

	cursorListener = new MapEventListener() {
		public void eventTriggered(MapEvent e)
		{
		    //Necessary otherwise the scrollbars have focus (in windows)
		    if (e.mouseEvent.getID()==MouseEvent.MOUSE_ENTERED) {
			//Tracer.debug("grabbing focus in PopupLayer.");
			PopupLayer.this.controller.getMapScrollPane().getDisplayer().grabFocus();
		    }
		    
		    updatePopups(e);
		}
	    };
	

	klickListener = new MapEventListener() {
		public void eventTriggered(MapEvent e)
		{
		    if (e.mapObject != null && 
			e.mapObject.getNeuronStyle() !=null &&
			e.mapObject.getNeuronStyle().getDetailedMap() != null)
			{
			    if (e.mouseEvent.getClickCount()==1 && !e.mouseEvent.isPopupTrigger())
				{
				    removeDescription();
				    makeSubWin(e);
				}
			}
		}
	    };

	colorListener=new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt)
		{
		    updateColors(true);
		}};
	
	/*	zoomListener=new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt)
		{
		    setScale(((Double) evt.getNewValue()).doubleValue(), ((Double) evt.getOldValue()).doubleValue());
		}};
	*/
	subMap=false;
    }
    
    public void activate()
    {
      registerKeybordActions(controller.getMapScrollPane().getDisplayer());

      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_POPUP_BACKGROUND, colorListener);
      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_POPUP_BACKGROUND_ACTIVE, colorListener);
      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_POPUP_TEXT, colorListener);
      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_POPUP_TEXT_ACTIVE, colorListener);
      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_POPUP_BORDER, colorListener);
      ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.MAP_POPUP_BORDER_ACTIVE, colorListener);
      
      controller.getMapScrollPane().getLayeredPane().add(panel, MapScrollPane.EDIT_LAYER);

      controller.getMapScrollPane().getDisplayer().addMapEventListener(cursorListener,
								       MapDisplayer.MOVE_DRAG);
      controller.getMapScrollPane().getDisplayer().addMapEventListener(klickListener,
								       MapDisplayer.CLICK);      
      updateColors(true);
    }
  public void deactivate()
    {
      unRegisterKeyboardActions(controller.getMapScrollPane().getDisplayer());

      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_POPUP_BACKGROUND, colorListener);
      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_POPUP_BACKGROUND_ACTIVE, colorListener);
      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_POPUP_TEXT, colorListener);
      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_POPUP_TEXT_ACTIVE, colorListener);
      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_POPUP_BORDER, colorListener);
      ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.MAP_POPUP_BORDER_ACTIVE, colorListener);
      
      controller.getMapScrollPane().getLayeredPane().remove(panel);
      controller.getMapScrollPane().getDisplayer().removeMapEventListener(cursorListener,
									  MapDisplayer.MOVE_DRAG);
      controller.getMapScrollPane().getDisplayer().removeMapEventListener(klickListener,
									  MapDisplayer.CLICK);
      removeAllPopups();
      killSubWin();
    }

  public void refresh()
    {
	controller.getMapScrollPane().revalidate();      
	controller.getMapScrollPane().repaint();
    }

  protected void setScaleImpl(double newscale, double oldscale)
    {
	Enumeration en=descriptions.elements();
	for (;en.hasMoreElements();)
	    {
		DescriptionPanel desc =(DescriptionPanel) en.nextElement();
		desc.setScale(newscale);
		desc.setSize(desc.getPreferredSize());
		desc.setLocation((int) (newscale/oldscale*desc.getX()), (int) (newscale/oldscale*desc.getY()));
	    }
	
	
	if (description!=null)
	    {
		description.setScale(scale);
		description.setSize(description.getPreferredSize());
		description.setLocation((int) (newscale/oldscale*description.getX()), (int) (newscale/oldscale*description.getY()));
	    }	
	refresh();
    }

    /** This function is the entry point for event handling for popups.
     */
   public void updatePopups(MapEvent e)
    {
	
	//Either should description be updated or map is shown. 
	if (!subMap)
	    updateDescription(e);
	else
	    {
		//FIXME   originalTrigger is used!!!!!!!!!!!!!!!
		if (e.hitType == MapEvent.HIT_NONE || 
		    e.mapObject != ((MapEvent) originalTrigger).mapObject)
		    {
			
			killSubWin();
			updateDescription(e);
		    }
		else
		    moveSubWin(e);
	    }
    }   
    
  protected boolean isPopupTrigger(Object o)
    {
	MapEvent e=(MapEvent) o;
	return e.mouseEvent.getID()!=MouseEvent.MOUSE_EXITED;
    }
  
  protected void activateOldDescriptionImpl(DescriptionPanel desc)
    {
	panel.moveToFront(desc);

	desc.setBackground(ColorManager.getTranslucentColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BACKGROUND_ACTIVE)));
	desc.text.setColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_TEXT_ACTIVE));
	desc.setBorder(activeBorder);
    }	
  protected void inActivateOldDescriptionImpl(DescriptionPanel desc)
    {
	desc.setBackground(ColorManager.getTranslucentColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BACKGROUND)));
	desc.text.setColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_TEXT));
	desc.setBorder(inactiveBorder);
    }	

  /** A description originates from either the map or a neuron, 
   *  this function returns the belonging component.
   *
   *  @returns a se.kth.cid.component.Component.
   */  
  protected se.kth.cid.component.Component getComponentFromTrigger(Object o)
  {
      if ((! (o instanceof MapEvent)) || o==null)
	  return null; 
      MapEvent e=(MapEvent) o;
    if (e==null)
	return null;
    if (e.hitType==MapEvent.HIT_NONE)
      return controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
    else if (e.mapObject!=null)
	return e.mapObject.getNeuron();
    return null;
  }

    /** A description originates from either the map or a neuron, 
     *  therefore when they are stored they need a unique lookupobject.
     *
     *  @returns a object representing the description uniquely.
     */  
  protected Object getDescriptionLookupObject(Object o)
    {
	if ((! (o instanceof MapEvent)) || o==null)
	    return null; 
	MapEvent e=(MapEvent) o;
	if (e.hitType==MapEvent.HIT_NONE)
	    return controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
	else if (e.mapObject!=null && e.mapObject.getNeuronMapObject()!=null)
            return e.mapObject.getNeuronMapObject();

	return null;
    }
	
  protected void showNewDescriptionImpl(DescriptionPanel desc)
  {
    killSubWin(); 
    
    desc.setOpaque(true);
    desc.setBackground(ColorManager.getTranslucentColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BACKGROUND_ACTIVE)));
    desc.text.setColor(ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_TEXT_ACTIVE));
    desc.setBorder(activeBorder);
      
    panel.add(desc, JLayeredPane.DEFAULT_LAYER);
    panel.moveToFront(desc);
  }

  protected void removeDescriptionImpl(DescriptionPanel desc)
  {
      panel.remove(desc);
      killSubWin();               //works????
  }
    
  void makeSubWin(MapEvent e)
  {
    if (subMap==true)
	killSubWin();
    subMap=true;
    NeuronStyle overNeuron = e.mapObject.getNeuronStyle();
    try {
      MapStoreManager storeManager = new MapStoreManager(URIClassifier.parseValidURI(overNeuron.getDetailedMap(), overNeuron.getConceptMap().getURI()), controller.getConzillaKit().getComponentStore());
      
      activeSubMap = new MapDisplayer(storeManager);
      activeSubMap.setOpaque(true);
      activeSubMap.setBackground(TRANSLUCENT_COLOR);
      activeSubMap.setBorder(TRANSLUCENT_BORDER);
      //      activeSubMap.setGlobalMarkColor(Color.gray);
      activeSubMap.setScale(0.3*controller.getMapScrollPane().getDisplayer().getScale());
      
      panel.add(activeSubMap, JLayeredPane.DEFAULT_LAYER);
      
      adjustPosition(activeSubMap, e);  
      controller.getMapScrollPane().revalidate();
      controller.getMapScrollPane().repaint();	
      //FIXME originalTrigger is used!!!!!!!!!!!!!!!!
      originalTrigger=e;

    } catch (ComponentException ex)
      {
	  subMap=false;
	  Tracer.trace("Could not load map " + e.mapObject.getNeuronStyle().getDetailedMap()
		       + ":\n " + ex.getMessage(), Tracer.WARNING);
      }
  }
  
  void killSubWin()
    {
      if(activeSubMap == null)
	return;
      
      
      panel.remove(activeSubMap);

      activeSubMap.getStoreManager().detach();
      activeSubMap.detach();
      activeSubMap = null;
      controller.getMapScrollPane().revalidate();
      controller.getMapScrollPane().repaint();
      subMap=false;
  }

  void moveSubWin(MapEvent e)
    {
      if (activeSubMap ==null)
	  return;
      adjustPosition(activeSubMap, e); 
      refresh();
    }

  protected void adjustPosition(JComponent comp, Object o)
  {
    MapEvent m=(MapEvent) o;
    int x= m.mouseEvent.getX()+10;
    int y= m.mouseEvent.getY()+10;
    Rectangle rect=controller.getMapScrollPane().getViewport().getViewRect();
    
    if ((x+comp.getPreferredSize().width) > (rect.x+rect.width))
	{
	  int diff=(x+comp.getPreferredSize().width) - (rect.x +rect.width);
	  if ((x-diff) > rect.x)
	    x-=diff;
	  else
	    x=rect.x;
	}
    if ((y+comp.getPreferredSize().height) > (rect.y + rect.height))
      {
	int diff=(y+comp.getPreferredSize().height) - (rect.y + rect.height);
	if ((y-diff) > rect.y)
	  y-=diff;
	else
	  y=rect.y;
      }
    comp.setLocation(x, y);
    comp.setSize(comp.getPreferredSize());
  } 

  protected void updateColors(boolean allowTrans)
    {
	Color inactive_back = ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BACKGROUND);
      	Color active_back = ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BACKGROUND_ACTIVE);
	Color inactive_text=ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_TEXT);
	Color active_text=ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_TEXT_ACTIVE);

	Color inactive_border = ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BORDER);
	Color active_border = ColorManager.getDefaultColorManager().getColor(ColorManager.MAP_POPUP_BORDER_ACTIVE);
	
	if(allowTrans)
	  {
	    inactive_back = ColorManager.getTranslucentColor(inactive_back);
	    active_back = ColorManager.getTranslucentColor(active_back);
	    inactive_border = ColorManager.getTranslucentColor(inactive_border);
	    active_border = ColorManager.getTranslucentColor(active_border);
	  }

	inactiveBorder = new MatteBorder(1, 1, 1, 1, inactive_border);
	activeBorder = new MatteBorder(1, 1, 1, 1, active_border);

	Enumeration en=descriptions.elements();
	DescriptionPanel desc;
	for (;en.hasMoreElements();)
	    {
		desc = (DescriptionPanel) en.nextElement();
		if (desc!=description)
		    {
			desc.setBackground(inactive_back);
			desc.setBorder(inactiveBorder);
			desc.text.setColor(inactive_text);
		    }
	    }
	
	if (description!=null)
	    {
		description.setBackground(active_back);
		description.text.setColor(active_text);
		description.setBorder(activeBorder);
	    }
    }

  public double getScale()
  {
    return controller.getMapScrollPane().getDisplayer().getScale();
  }
}
