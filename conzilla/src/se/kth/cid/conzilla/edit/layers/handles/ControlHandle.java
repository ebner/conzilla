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

package se.kth.cid.conzilla.edit.layers.handles;

import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;

import java.awt.*;
import java.util.*;


/** This handle is a controlpoint for a cubic spline.
 *
 *  @author Matthias Palmer
 */
public class ControlHandle extends DefaultHandle
{
    protected Handle corner;
    
    //Konstructor
    public ControlHandle(ConceptMap.Position pos, Handle corner)
    {
	super(pos);
	this.corner = corner;
    }
    
    /** Drags the handle.
     *
     *  @returns a collection of rectangles where repaint is needed.
     */
    public Collection dragForced(int x, int y)
    {
	Rectangle cr = corner.getBounds();
	Rectangle oldrect = new Rectangle(rectangle.x-1,rectangle.y-1,rectangle.width+2, rectangle.height+2);
	Rectangle oldlinerect = new Rectangle(rectangle.x-1, rectangle.y-1, 
					      cr.x-rectangle.x,cr.y-rectangle.y);
	rectangle.translate(x,y);
	Rectangle newrect = new Rectangle(rectangle.x-1,rectangle.y-1,rectangle.width+2, rectangle.height+2);
	Rectangle newlinerect = new Rectangle(rectangle.x-1, rectangle.y-1, 
					      cr.x-rectangle.x,cr.y-rectangle.y);
	Vector vec = new Vector();
	vec.addElement(oldrect);
	vec.addElement(HandledObject.positiveRectangle(oldlinerect));
	vec.addElement(newrect);
	vec.addElement(HandledObject.positiveRectangle(newlinerect));
	return vec;
    }

    /** Paints the handle with lines, thick if it is selected.
     */
    public void paint(Graphics2D g)
    {
	Stroke s = g.getStroke();

	//Always draw line thin.
	Rectangle cr = corner.getBounds();
	g.drawLine(rectangle.x+rectangle.width/2,rectangle.y+rectangle.height/2,
		   cr.x+cr.width/2,cr.y+cr.height/2);

	if (isSelected())
	    g.setStroke(thickStroke);
	else
	    g.setStroke(thinStroke);
	g.drawRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	g.setStroke(s);
    }    
}
