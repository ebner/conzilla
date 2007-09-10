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
import java.awt.*;

public class DefaultHandle extends Rectangle implements Handle
{
  public static int radius=4;
  boolean selected;
  public void setRadius(int radius) {this.radius=radius;}
  public int getRadius() {return radius;} 
  public DefaultHandle(Rectangle re)
  {
    super(re);
    this.selected=false;
  }
  public DefaultHandle(Point pos)
  {
    super(pos.x-radius,pos.y-radius,radius*2,radius*2);
    this.selected=false;
  }
  public Point getPosition() {return new Point(x+radius,y+radius);}
  public void setSelected(boolean selected) {this.selected=selected;}
  public boolean isSelected() {return selected;}
  public Point getOffset(MapEvent m)
  {
    return new Point(x+radius-m.mouseevent.getX(),y+radius-m.mouseevent.getY());
  }
  public void paint(Graphics g)
  {
    if (isSelected())
      {
	g.drawRect(x,y,width,height);	
	g.drawRect(x+1,y+1,width-2,height-2);	
      }
    else
      g.drawRect(x,y,width,height); 
  }
  public boolean contains(MapEvent m)
  {
    return super.contains(m.mouseevent.getX(),m.mouseevent.getY());
  }
  public void drag(int x, int y)
  {
    //      Tracer.debug("Translating a handle....");
    if (isSelected())
      translate(x,y);
  }
  public void dragForced(int x, int y)
  {
    translate(x,y);
  }
}
