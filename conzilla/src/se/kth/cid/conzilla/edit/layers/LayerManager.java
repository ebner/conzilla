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


package se.kth.cid.conzilla.edit.layers;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.JMenu;


/** Manages a stack of layers, the topmost receives all
 *  mapevents, all are activated until poped.
 *  Install/uninstall functions inserts/removes layers in
 *  the MapScrollPane on a depth depending on their position
 *  in the stack or if they have a preffered depth (hasFixedLevel). 
 *
 *  @author Matthias Palmer.
 */
public abstract class LayerManager implements  MapEventListener
{
  protected MapController controller;

  Stack layers;
  MapScrollPane pane;

  public LayerManager()
    {}

  public void install(MapController controller)
    {
	this.controller=controller;
	
	layers = new Stack();
    }
    
    public void push(LayerComponent layer)
    {
	layers.push(layer);
	if (pane != null)
	    install(pane, layer);
    }
    public boolean pop(LayerComponent layer)
    {
	LayerComponent top = (LayerComponent) layers.peek();
	if (top != layer)
	    return false;
	layers.pop();
	if (pane != null)
	    deinstall(pane, top);
	return true;
    }

    /** Installs a layer in the layeredpane and activates it,
     *  the layer have to already reside on the stack.
     *
     *  @param pane the MapScrollPane to install on, null not allowed.
     *  @param layer the LayerComponent to install.
     */ 
    protected void install(MapScrollPane pane, LayerComponent layer)
    {	
	if(layer.hasFixedLevel())
	    pane.getLayeredPane().add(layer, layer.getFixedLevelForLayer());
	pane.getLayeredPane().add(layer, new Integer(MapScrollPane.EDIT_LAYER.intValue()+layers.size()-layers.search(layer)));
	layer.activate(pane);
    }
    
    /** Reverses the {@link install function}.
     *
     *  @param pane the MapScrollPane to install on, null not allowed.
     *  @param layer the LayerComponent to deinstall.
     */ 
    protected void deinstall(MapScrollPane pane, LayerComponent layer)
    {
	int ind = pane.getLayeredPane().getIndexOf(layer);
	pane.getLayeredPane().remove(ind);
	layer.deactivate(pane);
    }
	

    public void install(MapScrollPane pane)
    {
	this.pane = pane;

	Iterator it = layers.iterator();
	while (it.hasNext())
	    install(pane, (LayerComponent) it.next());
	
	MapDisplayer mapDisplayer = pane.getDisplayer();
	mapDisplayer.addMapEventListener(this,MapDisplayer.MOVE_DRAG);
	mapDisplayer.addMapEventListener(this,MapDisplayer.PRESS_RELEASE);
	mapDisplayer.addMapEventListener(this,MapDisplayer.CLICK);
    }
    
    public void uninstall(MapScrollPane pane)
    {
	if (this.pane != pane)
	    Tracer.bug("Uninstalling a MapScrollPane in edit that's not installed."+
		       "Maybe several uninstalls are triggered.");
	Iterator it = layers.iterator();
	while (it.hasNext())
	    deinstall(pane, (LayerComponent) it.next());

	MapDisplayer mapDisplayer = pane.getDisplayer();
	mapDisplayer.removeMapEventListener(this,MapDisplayer.MOVE_DRAG);
	mapDisplayer.removeMapEventListener(this,MapDisplayer.PRESS_RELEASE);
	mapDisplayer.removeMapEventListener(this,MapDisplayer.CLICK);	
	this.pane = null;
    }
    

    public void eventTriggered(MapEvent m)
    {
	eventTriggeredImpl(m);
	
	if (!m.isConsumed() && layers.peek() instanceof MapMouseInputListener) 
	{
	    MapMouseInputListener mmil = (MapMouseInputListener) layers.peek();
	    switch (m.mouseEvent.getID())
		{
		case MouseEvent.MOUSE_MOVED:
		    mmil.mouseMoved(m);
		    break;
		case MouseEvent.MOUSE_DRAGGED:
		    mmil.mouseDragged(m);
		    break;
		case MouseEvent.MOUSE_PRESSED:
		    mmil.mousePressed(m);
		    break;
		case MouseEvent.MOUSE_RELEASED:
		    mmil.mouseReleased(m);
		    break;
		case MouseEvent.MOUSE_CLICKED:
		    mmil.mouseClicked(m);
		    break;
		case MouseEvent.MOUSE_ENTERED:
		    mmil.mouseEntered(m);
		    break;
		case MouseEvent.MOUSE_EXITED:
		    mmil.mouseExited(m);
		    break;
		}
	}
    }
    public abstract void eventTriggeredImpl(MapEvent m);

}
