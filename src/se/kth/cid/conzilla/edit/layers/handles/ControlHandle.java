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
import java.util.Vector;

import se.kth.cid.layout.ContextMap;


/** This handle is a controlpoint for a cubic spline.
 *
 *  @author Matthias Palmer
 */
public class ControlHandle extends DefaultHandle
{
    protected Handle corner;
    
    //Konstructor
    public ControlHandle(ContextMap.Position pos, Handle corner)
    {
	super(pos);
	this.corner = corner;
    }
    
    /** Drags the handle.
     *
     *  @return a collection of rectangles where repaint is needed.
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
