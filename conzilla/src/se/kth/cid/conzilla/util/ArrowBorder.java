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


package se.kth.cid.conzilla.util;

import se.kth.cid.util.Tracer;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/** 
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class ArrowBorder extends EmptyBorder
{ 
  protected Color color;
  protected Polygon up;
  protected Polygon down;
  static final public int offset = 2;

  public ArrowBorder(int top, int bottom)
  {
      super(top+offset, offset, bottom+offset, offset);
     color=Color.blue;
      if (top!= 0)
	  {
	      up=new Polygon();
	      up.addPoint(0,-top);
	      up.addPoint((int) (top),0);
	      up.addPoint((int) (-top),0);
	  }
      if (bottom != 0)
	  {
	      down=new Polygon();
	      down.addPoint((int) (-bottom),0);
	      down.addPoint((int) (bottom),0);
	      down.addPoint(0,bottom);
	  }
  }

    /**
    /**
     * Paints the matte border.
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets insets = getBorderInsets(c);
        Color oldColor = g.getColor();
	
        g.translate(x+width/2, y +top);
	g.setColor(color);

        if (up != null){
	    ((Graphics2D) g).fill(up);
	    //	    g.drawLine(-top, 0, top, 0);
	}
	if (down != null)
	    {
		g.translate(0,height-bottom-top);
		((Graphics2D) g).fill(down);
		//		g.drawLine(-bottom, 0, +bottom, 0);
		g.translate(0,-height+bottom+top);
	    }
        g.translate(-x-width/2, -y-top);
        g.setColor(oldColor);
    }

    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        Insets i = new Insets(0,0,0,0);
        return getBorderInsets(c, i);
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
	insets.left = left;
	insets.top = top;
	insets.right = right;
	insets.bottom = bottom;
	
        return insets;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    public boolean isBorderOpaque() { 
        // If a tileIcon is set, then it may contain transparent bits
        return color != null; 
    }	  
}
