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
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.util.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.*;

public abstract class LayerComponent extends JComponent 
{
    MapController controller;
    boolean autoScale;

    LayerComponent(MapController controller, boolean autoScale)
    {
	this.controller=controller;
	this.autoScale=autoScale;
    }
  public abstract void activate();
  public abstract void deactivate();
  public void paint(Graphics g)
  {
      Graphics2D gr     = (Graphics2D) g;
      Graphics2D original = (Graphics2D) gr.create();
      if (!autoScale)
	  layerPaint(gr, original);
      else
	  {
	      double scale=controller.getMapScrollPane().getDisplayer().getScale();
	      AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
	      Shape clip        = gr.getClip();
	      AffineTransform f = gr.getTransform();
	      
	      gr.transform(transform);
	      try {
		  gr.setClip(transform.createInverse().createTransformedShape(clip));
	      } catch (NoninvertibleTransformException e)
		  {
		      Tracer.error("Non-invertible transform: " + transform + ":\n "
				   + e.getMessage());
		  }
	      
	      layerPaint(gr, original);
	      gr.setClip(clip);
	      gr.setTransform(f);
	  }
  }
    public abstract void layerPaint(Graphics2D g, Graphics2D original);
}
