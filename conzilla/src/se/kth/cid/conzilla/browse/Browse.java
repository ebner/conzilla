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
import se.kth.cid.conzilla.metadata.*;
import se.kth.cid.conzilla.properties.*;
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

public class Browse
{ 
  /** Listens for press and release in the map.
   */
  MapEventListener pressListener;

  /** Listens for clicks in the map.
   */
  MapEventListener klickListener;

  /** Listens for moves in the map.
   */
  MapEventListener cursorListener;    

  PropertyChangeListener zoomListener;
  
  /** The menu to popup.
   */
  BrowseMenu browseMenu;

  /** The current cursor.
   */
  int cursor = Cursor.DEFAULT_CURSOR;

  MapController controller;

  NeuronMapObject marked = null;

  /** A layer on top containing popups in the form of both 
   *  submaps and descriptions.
   */
  PopupLayer popup;

  public Browse(MapController cont)
  {
    popup=new PopupLayer(cont);
    browseMenu=new BrowseMenu(cont, popup);
    controller=cont;
    setListeners();
    controller.addPropertyChangeListener(zoomListener);
  }
  
  protected void setListeners()
  {

    cursorListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  updateBrowse(e);
	}
    };
    pressListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  if (e.mouseEvent.isPopupTrigger())
	    {
	      popup.removeAllPopups();
	      browseMenu.popup(e);
	      //	      unMark();
	    }
	}
    };
    klickListener = new MapEventListener() {
      public void eventTriggered(MapEvent e)
	{
	  if (e.mapObject != null && 
	      e.mapObject.getNeuronStyle() !=null &&
	      e.mapObject.getNeuronStyle().getDetailedMap() != null)
	    {
		if (e.mouseEvent.getClickCount()==2)
		    {
			//			unMark();
			NeuronStyle ns = e.mapObject.getNeuronStyle();
			
			try {
			    ConceptMap cMap=controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();
			    controller.showMap(URIClassifier.parseValidURI(e.mapObject.getNeuronStyle().getDetailedMap(),
									   cMap.getURI()));
			    controller.getHistoryManager().fireDetailedMapEvent(controller, ns);
			    popup.markLastNeuron(controller.getMapScrollPane(), URIClassifier.parseValidURI(ns.getNeuronURI(), ns.getConceptMap().getURI()).toString());
			} catch(ControllerException ce)
			    {
				ErrorMessage.showError("Load Error",
						       "Failed to load map\n\n" + e.mapObject.getNeuronStyle().getDetailedMap(),
						       ce,
						       controller.getMapScrollPane());
			    }
		    }
	    }
	}};
    zoomListener=new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if(evt.getPropertyName().equals(MapController.ZOOM_PROPERTY))
		    setScale(((Double) evt.getNewValue()).doubleValue(), ((Double) evt.getOldValue()).doubleValue());
	    }};
  }
    
  void setShownCursor(int type)
    {
      JComponent c = controller.getMapScrollPane().getLayeredPane();
      
      if(c.getCursor().getType() != type)
	c.setCursor(new Cursor(type));
    }
  
  protected void updateBrowse(MapEvent e)
    {
      //If menu is popup return
      if (((JMenu) browseMenu.getJMenuItem()).getPopupMenu().isVisible())
	return;

      //Mark and HandCursor below.
      if (e.hitType != MapEvent.HIT_NONE)
	{
		
	  NeuronStyle ns = e.mapObject.getNeuronStyle();
	  if (ns.getDetailedMap() != null)
	      setShownCursor(Cursor.HAND_CURSOR);
	  else
	      setShownCursor(Cursor.DEFAULT_CURSOR);

	  NeuronMapObject newMarked = e.mapObject.getNeuronMapObject();
	  if(marked != newMarked )
	    {	      
	      unMark();
	      marked = newMarked;
	      mark();
	    }
	}
      else
	{
	  setShownCursor(Cursor.DEFAULT_CURSOR);
	  
	  unMark();
	  marked = null;
	}
    }

  private void mark()
    {
	//The new mark
	Mark overMark=new Mark(BrowseMapManagerFactory.COLOR_MOUSEOVER, null, null);
	overMark.setLineWidth((float) 2.5);

	if (marked.getNeuron()==null)
	    //If no neuron present, set local mark only.
	    marked.pushMark(overMark, this);
	else
	    //Otherwise set global mark for this neuron.
	    controller.getConzillaKit().getConzilla().pushMark(getMarkSet(), overMark, this);
    }
    
  private void unMark()
    {
	//If nothing is marked at present nothing has to be unmarked.
	if(marked != null)
	    {
		if (marked.getNeuron()==null)
		    //If no neuron is present no global mark was set.
		    marked.popMark(this);
		else 
		    //Otherwise remove global mark for this neuron.
		    controller.getConzillaKit().getConzilla().popMark(getMarkSet(), this);
	    }
    }    

    /** Calculates a set of direct and indirect markes from the original mark.
     *  Shouldn't be called if the original mark has no neuron.
     *
     *  @return a Set of neurons.
     */
  private Set getMarkSet()
    {
	HashSet set = new HashSet();
	set.add(marked.getNeuron().getURI());

	getMarkSet(marked, marked.getNeuron(), set, controller.getMapScrollPane().getDisplayer());

	return set;

    }

    private void getMarkSet(NeuronMapObject nmo, Neuron ne, HashSet set, MapDisplayer displayer)
    {
	ComponentStore store = controller.getConzillaKit().getComponentStore();
	String[] taxon1 = {"Mediator"};
	String[] taxon2 = {"Transitive"};
	NeuronType type = null;
	if (nmo != null)
	    type = nmo.getNeuronType();
	else
	    try {
		type = store.getAndReferenceNeuronType(URIClassifier.parseValidURI(ne.getType(),ne.getURI()));
	    } catch (ComponentException ce)
		{}
	
	if (type !=null && MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
							"NeuronType", "Conzilla", taxon1))
	    {
		boolean transitive = MetaDataUtils.isClassifiedAs(type.getMetaData().get_classification(),
								  "NeuronType", "Conzilla", taxon2);
		Axon [] axons = ne.getAxons();
		for (int i =0 ; i<axons.length; i++)
		    {
			try {
			    URI uri = URIClassifier.parseURI(axons[i].objectURI(), URIClassifier.parseValidURI(marked.getNeuron().getURI()));
			    set.add(uri.toString());
			    if (transitive)
				try {
				    Neuron next = store.getAndReferenceNeuron(uri);
				    getMarkSet(null, next, set, displayer);
				} catch (ComponentException ce) {}
			} catch (MalformedURIException e)
			    {}
		    }
	    }
    }

    
    public void install(MapScrollPane pane)
    {

      popup.activate(pane);
      pane.getDisplayer().addMapEventListener(pressListener, MapDisplayer.PRESS_RELEASE);
      pane.getDisplayer().addMapEventListener(klickListener, MapDisplayer.CLICK);
      pane.getDisplayer().addMapEventListener(cursorListener, MapDisplayer.MOVE_DRAG);

    }
  
  protected void uninstall(MapScrollPane pane)
    {
      
	pane.getDisplayer().removeMapEventListener(pressListener, MapDisplayer.PRESS_RELEASE);
	pane.getDisplayer().removeMapEventListener(klickListener, MapDisplayer.CLICK);
	pane.getDisplayer().removeMapEventListener(cursorListener, MapDisplayer.MOVE_DRAG);

	popup.deactivate(pane);
	
	unMark();
	marked = null;
	
    }

  public void detach()
  {
    unMark();
    pressListener = null;
    cursorListener = null;
    klickListener = null;
    controller = null;
  }
  
  public void setScale(double newscale, double oldscale)
  {
    popup.setScale(newscale, oldscale);
  }
}
