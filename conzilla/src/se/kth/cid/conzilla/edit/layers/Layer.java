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
import se.kth.cid.component.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;


public abstract class Layer extends LayerComponent implements EditListener
{
  protected MapDisplayer mapdisplayer;
  protected HandledObject handles;
  protected MapEvent mapevent;
  protected boolean focusonclick, pressed;
  protected GridModel gridModel;
  protected ConceptMap.Position offset;

  public Layer(MapController controller)
  {
      super(controller, true);
    if (! (controller.getManager() instanceof EditMapManager) )
	Tracer.bug("MapManager in controller isn't a EditMapManager despite the fact that we are in edit mode");
    gridModel=((EditMapManager) controller.getManager()).getGridModel();
    setHandledObject(null,MapEvent.Null);
    setVisible(true);
    setOpaque(false);
    setFocusOnClick(true);
    repaint();
  }

  public void activate()
  {
    this.mapdisplayer=controller.getMapScrollPane().getDisplayer();
    ConceptMap.Dimension dim = mapdisplayer.getStoreManager().getConceptMap().getDimension();
    
    setSize(new Dimension(dim.width, dim.height));

    controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().addEditListener(this);
  }
  public void deactivate()
  {
    setHandledObject(null,MapEvent.Null);

    controller.getMapScrollPane().getDisplayer().getStoreManager().getConceptMap().removeEditListener(this);
    invalidate();
    mapdisplayer.repaint();
  }
  
  public boolean isFocusOnClick() { return focusonclick;}
  public void setFocusOnClick(boolean isso) {focusonclick=isso;}

  protected void setHandledObject(HandledObject handles, MapEvent m)
  {
    if (this.handles != null && this.handles != handles)
	this.handles.detach();
    this.handles=handles;
    mapevent=m;
  }
  
  
  public void mouseMoved(MapEvent m)
  {
    if (!focusonclick && !m.mouseEvent.isShiftDown())
      {
	if (focus(m))
	  repaint();
      }
  }
  
  public void mouseClicked(MapEvent m)
  {
    if (handles!=null)
      {
	handles.click(m);
	mapdisplayer.repaint();
      }
  }
  
  
  protected abstract boolean focus(MapEvent m);
  
  public void mouseDragged(MapEvent m)
  {
    //this "if" prevents the drag behavior from entering other objects than the current edited.
    //    if(m.hit==MapEvent.HIT_NONE || m.getHitObject()==mapevent.getHitObject())
      if (handles!=null && pressed)
	{
	  if (gridModel.isGridOn())
	    {
		int grad = gridModel.getGranularity(); 
		int hgrad= grad / 2;

		m.mapX= (m.mapX + offset.x + hgrad) / grad * grad - offset.x;
		m.mapY= (m.mapY + offset.y + hgrad) / grad * grad - offset.y;
	    }
	  repaintLayer(handles.drag(m));
	}
  }
  public void mousePressed(MapEvent m)
  {
    pressed=true;
    if (handles!=null)
      {
	offset=handles.startDrag(m);
	mapdisplayer.repaint();
      }
    if (!m.isConsumed() && focusonclick)
      {
	HandledObject oldhandles=handles;
	if (focus(m))
	  if (oldhandles!=handles && handles!=null)
	      offset=handles.startDrag(m);
	mapdisplayer.repaint();
      }

    if (offset==null)
	offset=new ConceptMap.Position(-m.mapX%gridModel.getGranularity(),
				       -m.mapY%gridModel.getGranularity());
  }
  
  public void mouseReleased(MapEvent m)
  {
    if (handles!=null && pressed)
      {
	if (gridModel.isGridOn()) 
	   { 
	       m.mapX+=offset.x-m.mapX%gridModel.getGranularity();
	       m.mapY+=offset.y-m.mapY%gridModel.getGranularity();
	   }
	handles.stopDrag(m);
	offset=null;
	mapdisplayer.repaint();
      }
    pressed=false;
  }
  public void layerPaint(Graphics2D g)
  {
      //      if (handles!=null)
      //	  handles.paint(g);
  }

  public void repaintLayer(Collection rectangles)
    {
	if (rectangles==null)
	    return;
	Iterator it=rectangles.iterator();
	double scale = mapdisplayer.getScale();
	for (;it.hasNext();)
	    {
		Rectangle rect = (Rectangle) it.next();
		
		repaint((int) ((rect.x-1)*scale), (int) ((rect.y-1)*scale),
			(int) ((rect.width+3)*scale), (int) ((rect.height+3)*scale));
	    }
    }
    public void componentEdited(EditEvent e) {}
}
