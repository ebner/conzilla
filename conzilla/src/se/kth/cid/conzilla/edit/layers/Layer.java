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
import se.kth.cid.conzilla.edit.layers.handles.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;


public abstract class Layer extends LayerComponent implements MapEventListener
{
  protected MapDisplayer mapdisplayer;
  protected HandledObject handles;
  protected MapEvent mapevent;
  protected boolean focusonclick;
  protected NeuronLayer neuronlayer;
  protected GridTool gridtool;
  protected Point offset;
  
  public Layer(MapController controller, MapManager manager, GridTool gridtool)
  {
    this.mapdisplayer=manager.getDisplayer();
    this.gridtool=gridtool;
    neuronlayer=new NeuronLayer(controller, manager);
    handles=null;
    mapevent=new MapEvent(null,null);
    setVisible(true);
    setSize(mapdisplayer.getMap().getBoundingBox());    
    setOpaque(false);
    setFocusOnClick(true);
    repaint();
  }

  public void activate()
  {
    handles=null;
    mapevent=new MapEvent(null,null);    
    mapdisplayer.addMapEventListener(this,MapDisplayer.MOVE_DRAGG);
    mapdisplayer.addMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    mapdisplayer.addMapEventListener(this,MapDisplayer.CLICK);
  }
  public void deActivate()
  {
    mapdisplayer.removeMapEventListener(this,MapDisplayer.MOVE_DRAGG);
    mapdisplayer.removeMapEventListener(this,MapDisplayer.PRESS_RELEASE);
    mapdisplayer.removeMapEventListener(this,MapDisplayer.CLICK);
    handles=null;
    invalidate();
    mapdisplayer.repaint();
  }
  
  public boolean isFocusOnClick() { return focusonclick;}
  public void setFocusOnClick(boolean isso) {focusonclick=isso;}

  protected void setHandledObject(HandledObject handles, MapEvent m)
  {
    this.handles=handles;
    mapevent=m;
  }
  
  public void eventTriggered(MapEvent m)
  {
    neuronlayer.eventTriggered(m);
    if (m.isConsumed())
      {
	handles=null;
	mapdisplayer.repaint();
	return;
      }
    switch (m.mouseevent.getID())
      {
      case MouseEvent.MOUSE_MOVED:
	mouseMoved(m);
	break;
      case MouseEvent.MOUSE_DRAGGED:
	mouseDragged(m);
	break;
      case MouseEvent.MOUSE_PRESSED:
	mousePressed(m);
	break;
      case MouseEvent.MOUSE_RELEASED:
	mouseReleased(m);
	break;
      case MouseEvent.MOUSE_CLICKED:
	mouseClicked(m);
	break;
      } 
  }
  
  protected void mouseMoved(MapEvent m)
  {
    if (!focusonclick && !m.mouseevent.isShiftDown())
      {
	if (focus(m))
	  repaint();
      }
  }
  
  protected void mouseClicked(MapEvent m)
  {
    if (handles!=null)
      {
	handles.click(m);
	repaint();
      }
  }
  
  
  protected abstract boolean focus(MapEvent m);
  
  protected void mouseDragged(MapEvent m)
  {
    //this "if" prevents the drag behavior from entering other objects than the current edited.
    //    if(m.hit==MapEvent.HIT_NONE || m.getHitObject()==mapevent.getHitObject())
      if (handles!=null)
	{
	  if (gridtool.isActivated())
	    {
	      m.mouseevent.translatePoint(offset.x,offset.y);
	      m.mouseevent.translatePoint(-m.mouseevent.getX()%gridtool.getGranularity(),
					  -m.mouseevent.getY()%gridtool.getGranularity());
	      m.mouseevent.translatePoint(-offset.x,-offset.y);
	    }
	  handles.drag(m);
	  repaint();
	}
  }
  protected void mousePressed(MapEvent m)
  {
    if (handles!=null)
      {
	offset=handles.startDrag(m);
	if (offset==null)
	  offset=new Point(-m.mouseevent.getX()%gridtool.getGranularity(),
			   -m.mouseevent.getY()%gridtool.getGranularity());
	repaint();
      }
    if (!m.isConsumed() && focusonclick && !m.mouseevent.isShiftDown())
      {
	HandledObject oldhandles=handles;
	if (focus(m))
	  if (oldhandles!=handles && handles!=null)
	    {
	      offset=handles.startDrag(m);
	      if (offset==null)
		offset=new Point(-m.mouseevent.getX()%gridtool.getGranularity(),
				 -m.mouseevent.getY()%gridtool.getGranularity());
	    }
	repaint();
      }
  }
  
  protected void mouseReleased(MapEvent m)
  {
    if (handles!=null)
      {
	if (gridtool.isActivated())
	    m.mouseevent.translatePoint(-m.mouseevent.getX()%gridtool.getGranularity()+offset.x,
					-m.mouseevent.getY()%gridtool.getGranularity()+offset.y);
	handles.stopDrag(m);
	offset=null;
	repaint();
      }
  }
  public void paint(Graphics g)
  {
    if (handles!=null)
      handles.paint(g);
  }
}
