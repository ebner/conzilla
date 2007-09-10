/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class DefaultHandle extends AbstractHandle
{
    protected Rectangle rectangle;

    //Konstructor
    public DefaultHandle(ContextMap.Position pos)
    {
	rectangle = new Rectangle(pos.x-radius,pos.y-radius,radius*2,radius*2);
	this.selected=false;
	clearEdited();
    }

    //Konstructor
    public DefaultHandle(ContextMap.Position pos, boolean noInitialCallToClearEdited)
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
     *  @return a collection of rectangles where repaint is needed.
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
     *  @return a collection of rectangles where repaint is needed.
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

   
    public ContextMap.Position getPosition() {return new ContextMap.Position(rectangle.x+radius,rectangle.y+radius);}
    public void       setPosition(int x,int y) {rectangle.setLocation(x-radius, y-radius);}
    public ContextMap.Position getOffset(MapEvent m) {return new ContextMap.Position(rectangle.x+radius-m.mapX,rectangle.y+radius-m.mapY);}
    public Rectangle getBounds()
    {return rectangle;}
}
