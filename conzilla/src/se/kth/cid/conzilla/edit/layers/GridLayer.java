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
import javax.swing.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.beans.*;

public class GridLayer extends LayerComponent implements PropertyChangeListener
{
  protected GridModel gridModel;
  protected MapController controller;
  protected BasicStroke stroke;

  public GridLayer(MapController controller)
    {
	super(controller, false);
	this.controller=controller;
	if (! (controller.getManager() instanceof EditMapManager) )
	    Tracer.bug("MapManager in controller isn't a EditMapManager despite the fact that we are in edit mode");
	this.gridModel=((EditMapManager) controller.getManager()).getGridModel();
	float[] arr = {0, 3};
	stroke= new BasicStroke(1, BasicStroke.CAP_ROUND,
				 BasicStroke.JOIN_ROUND,
				 0f, arr, 0f);
    }
  public void activate()
    {
	ColorManager.getDefaultColorManager().addPropertyChangeListener(ColorManager.EDIT_GRID, this);	

	gridModel.addPropertyChangeListener(this);
	MapDisplayer mapdisplayer=controller.getMapScrollPane().getDisplayer();
	
	ConceptMap.Dimension dim = mapdisplayer.getStoreManager().getConceptMap().getDimension();
	setSize(new Dimension(dim.width, dim.height));

	controller.getMapScrollPane().getLayeredPane().add(this, new Integer(MapScrollPane.MAP_LAYER.intValue()-5));
	revalidate();

    }
  public void deactivate()
    {
	gridModel.removePropertyChangeListener(this);
	int ind = controller.getMapScrollPane().getLayeredPane().getIndexOf(this);
	controller.getMapScrollPane().getLayeredPane().remove(ind);
	ColorManager.getDefaultColorManager().removePropertyChangeListener(ColorManager.EDIT_GRID, this);	
    }

  public void layerPaint(Graphics2D g, Graphics2D original)
    {
	if (gridModel.getGridStyle()==GridModel.STYLE_INVISIBLE || !gridModel.isGridOn())
	    return;
	g.setColor(ColorManager.getDefaultColorManager().getColor(ColorManager.EDIT_GRID));
	switch (gridModel.getGridStyle())
	    {
	    case GridModel.STYLE_CONTINUOUS:
		doPaintContinuous(g);
		break;
	    case GridModel.STYLE_CORNERS:
		doPaintCorners(g);
		break;
	    case GridModel.STYLE_DOTTED:
		Graphics2D g2=(Graphics2D) g;
		Stroke s = g2.getStroke();
		g2.setStroke(stroke);
		doPaintContinuous(g2);
		g2.setStroke(s);
		break;
	    default:
		doPaintContinuous(g);
	    }
    }
  protected void doPaintContinuous(Graphics g)
    {
	double scale=controller.getMapScrollPane().getDisplayer().getScale();
		      
	Rectangle rect=getBounds();
	
	int gran=gridModel.getGranularity();
	int step=0;
	for (int i=0; step <= rect.width;i++)
	    {
		step=(int) (i*gran*scale);
		g.drawLine(step, 0, step, rect.height);
	    }
	for (int i=0;step <= rect.height;i++)
	    {
		step=(int) (i*gran*scale);
		g.drawLine(0, step, rect.width, step);
	    }
    }
    protected void doPaintCorners(Graphics g)
    {
	double scale=controller.getMapScrollPane().getDisplayer().getScale();
	Rectangle rect=getBounds();
	
	int gran=gridModel.getGranularity();
	int stepx=0;
	for (int i=0;stepx <= rect.width;i++)
	    {
		int stepy=0;
		for (int j=0;stepy <= rect.height;j++)
		    {
			stepy=(int) (j*gran*scale);
			g.drawLine(stepx, stepy, stepx, stepy);
		    }
		stepx=(int) (i*gran*scale);
	    }
    }
	
  
    public void propertyChange(PropertyChangeEvent pce)
    {
	MapDisplayer mapdisplayer=controller.getMapScrollPane().getDisplayer();
	mapdisplayer.repaint();
    }
}
