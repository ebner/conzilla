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
import se.kth.cid.util.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.edit.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.component.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/** A BoxTotalhandle moves a box and some follow handles. 
 *
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class BoxTotalHandle extends AbstractHandle
  {
    private BoxHandle bhul, bhur, bhlr, bhll;
    static final BasicStroke fatStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND,
						       BasicStroke.JOIN_ROUND);

    public BoxTotalHandle(BoxHandle bhul,BoxHandle bhur,BoxHandle bhlr,BoxHandle bhll)
      {
	this.bhul = bhul;
	this.bhur = bhur;
	this.bhlr = bhlr;
	this.bhll = bhll;
	clearEdited();
	selected=false;
      }

    /** Same as for Rectangle.
     */
    public boolean contains(MapEvent m)
    {
	ConceptMap.Position posul=bhul.getPosition();
	ConceptMap.Position poslr=bhlr.getPosition();

	int w=poslr.x-posul.x;
	int h=poslr.y-posul.y;
	return getRectangle().contains(m.mapX, m.mapY);
    }
    
    /** Returns a rectangle for this totalhandle.
     */
    public Rectangle getRectangle()
    {
      ConceptMap.Position posul=bhul.getPosition();
      ConceptMap.Position poslr=bhlr.getPosition();
      int w=poslr.x-posul.x;
      int h=poslr.y-posul.y;
      if (w>=8 && h>=8)
	  return new Rectangle(posul.x, posul.y, w, h); 
      else if (w>=8 && h<8)
	  return new Rectangle(posul.x, posul.y-(8-h)/2, w, 8);
      else if (w<8 && h>=8)
	  return new Rectangle(posul.x-(8-w)/2, posul.y, 8, h);
      else
	  return new Rectangle(posul.x-(8-w)/2, posul.y-(8-h)/2, 8, 8);
    }

    public Rectangle getFatRectangle()
    {
      ConceptMap.Position posul=bhul.getPosition();
      ConceptMap.Position poslr=bhlr.getPosition();
      int w=poslr.x-posul.x;
      int h=poslr.y-posul.y;
      if (w>=8 && h>=8)
	  return new Rectangle(posul.x-1, posul.y-1, w+2, h+2); 
      else if (w>=8 && h<8)
	  return new Rectangle(posul.x-1, posul.y-(8-h)/2-1, w+2, 10);
      else if (w<8 && h>=8)
	    return new Rectangle(posul.x-(8-w)/2-1, posul.y-1, 10, h+2);
      else
	  return new Rectangle(posul.x-(8-w)/2-1, posul.y-(8-h)/2-1, 10, 10);
    }
      
    /** Drags the box and possible som followers.
     *
     *  @returns a collection of rectangles where repaint is needed.
     *  @see #dragForced
     */
    public Collection drag(int x, int y)
      {
	  //Drag the box first
	  Collection localCol = dragForced(x, y);

	  //Drag the followers.
	  Collection cols = dragFollowers(x,y);
	  
	  //Take the union of all collections.
	  Vector vec = new Vector();
	  vec.addAll(localCol);
	  Iterator it = cols.iterator();
	  while (it.hasNext())
	      vec.addAll((Collection) it.next());
	  if (vec.isEmpty())
	      return null;
	  else
	      return vec;
    }
      
    /** Drags the handle.
     *
     *  @returns a collection of rectangles where repaint is needed.
     */
    public Collection dragForced(int x, int y)
    {
	Rectangle oldrect = getFatRectangle();
	Rectangle newrect=new Rectangle(oldrect);
	newrect.translate(x,y);

	  Vector cols= new Vector();
	  Collection col;
	  col = bhul.dragForced(x,y);
	  if (col != null)
	      cols.addElement(col);
	  col = bhur.dragForced(x,y);
	  if (col != null)
	      cols.addElement(col);
	  col = bhll.dragForced(x,y);
	  if (col != null)
	      cols.addElement(col);
	  col = bhlr.dragForced(x,y);
	  if (col != null)
	      cols.addElement(col);


	  Vector vec = new Vector();
	  Iterator it = cols.iterator();
	  while (it.hasNext())
	      vec.addAll((Collection) it.next());
	  
	  vec.add(oldrect);
	  vec.add(newrect);
	  return vec;
    }

    /** Paints the handle, thick if it is selected.
     */
    public void paint(Graphics2D g)
    {
	Stroke s = g.getStroke();
	if (isSelected())
	    g.setStroke(fatStroke);
	else
	    g.setStroke(thinStroke);

	ConceptMap.Position posul=bhul.getPosition();
	ConceptMap.Position poslr=bhlr.getPosition();

	int w=poslr.x-posul.x;
	int h=poslr.y-posul.y;
	if (w>=8 && h>=8)
	    g.drawRect(posul.x, posul.y, w, h);
	else if (w>=8 && h<8)
	    g.drawRect(posul.x, posul.y-(8-h)/2, w, 8);
	else if (w<8 && h>=8)
	    g.drawRect(posul.x-(8-w)/2, posul.y, 8, h);
	else
	    g.drawRect(posul.x-(8-w)/2, posul.y-(8-h)/2, 8, 8);

	g.setStroke(s);
    }
    public void simplePaint(Graphics2D g)
    {
	ConceptMap.Position posul=bhul.getPosition();
	ConceptMap.Position poslr=bhlr.getPosition();
	g.drawRect(posul.x, posul.y, poslr.x-posul.x, poslr.y-posul.y);
    }

    public ConceptMap.Position getPosition() 
      {
	  ConceptMap.Position posul=bhul.getPosition();
	  return new ConceptMap.Position(posul.x, posul.y);
      }

    public ConceptMap.Position getOffset(MapEvent m) 
      {
	  ConceptMap.Position posul=bhul.getPosition();
	  return new ConceptMap.Position(posul.x-m.mapX,posul.y-m.mapY);
      }
    public Rectangle getBounds()
    {	
	return getRectangle();
	//	ConceptMap.Position posul=bhul.getPosition();
	//	ConceptMap.Position poslr=bhlr.getPosition();
	//	return new Rectangle(posul.x, posul.y, poslr.x-posul.x, poslr.y-posul.y);
    }
}
