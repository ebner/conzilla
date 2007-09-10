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

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class DefaultHandle extends AbstractHandle
{
    Rectangle rectangle;

    //Konstructor
    public DefaultHandle(ConceptMap.Position pos)
    {
	rectangle = new Rectangle(pos.x-radius,pos.y-radius,radius*2,radius*2);
	this.selected=false;
	clearEdited();
    }

    //Konstructor
    public DefaultHandle(ConceptMap.Position pos, boolean noInitialCallToClearEdited)
    {
	rectangle = new Rectangle(pos.x-radius,pos.y-radius,radius*2,radius*2);
	this.selected=false;
    }

    /** Same as for Rectangle.
     */
    public boolean contains(MapEvent m)
    {
	return rectangle.contains(m.mapX,m.mapY);
    }

    /** Drags the handle in the default way, i.e. via calling @link #dragForced
     *
     *  @returns a collection of rectangles where repaint is needed.
     *  @see #dragForced
     */
    public Collection drag(int x, int y)
    {
	//Drag all followers.
	Collection cols=dragFollowers(x, y);

	if (cols.size()==0)
	    return dragForced(x,y);

	Vector vec=new Vector();
	Iterator it = cols.iterator();
	for (;it.hasNext();)
	    vec.addAll((Collection) it.next());
	
	vec.addAll(dragForced(x,y));

	return vec;
    }

    /** Drags the handle.
     *
     *  @returns a collection of rectangles where repaint is needed.
     */
    public Collection dragForced(int x, int y)
    {
	Rectangle oldrect = new Rectangle(rectangle.x-1,rectangle.y-1,rectangle.width+2, rectangle.height+2);
	rectangle.translate(x,y);
	Rectangle newrect = new Rectangle(rectangle.x-1,rectangle.y-1,rectangle.width+2, rectangle.height+2);
	Vector vec = new Vector();
	vec.addElement(oldrect);
	vec.addElement(newrect);
	return vec;
    }

    /** Paints the handle, thick if it is selected.
     */
    public void paint(Graphics2D g)
    {
	Stroke s = g.getStroke();
	if (isSelected())
	    g.setStroke(thickStroke);
	else
	    g.setStroke(thinStroke);
	g.drawRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	g.setStroke(s);
    }

    public void simplePaint(Graphics2D g)
    {
	g.drawRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
    }

   
    public ConceptMap.Position getPosition() {return new ConceptMap.Position(rectangle.x+radius,rectangle.y+radius);}
    public ConceptMap.Position getOffset(MapEvent m) {return new ConceptMap.Position(rectangle.x+radius-m.mapX,rectangle.y+radius-m.mapY);}
    public Rectangle getBounds()
    {return rectangle;}
}
