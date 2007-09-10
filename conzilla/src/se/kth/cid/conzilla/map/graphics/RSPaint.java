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
package se.kth.cid.conzilla.map.graphics;
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conceptmap.*;
import se.kth.cid.util.*;
import java.awt.*;
import java.lang.*;
import java.util.*;


public class RSPaint
{  
  public RoleStyle rolestyle;

  private LineDrawer linedrawer;
  private HeadDrawer headdrawer;

  public Object appobject;
  public boolean hitDirty;
  
  public RSPaint(RoleStyle rolestyle)
  {
    this.rolestyle=rolestyle;
    appobject=null;
    
    linedrawer=new LineDrawer();
    headdrawer=new HeadDrawer();
    
    fix();
  }
  public void detach()
  {
    linedrawer.detach();
    linedrawer=null;
    headdrawer.detach();
    headdrawer=null;
  }
  public Object getAppObject()
  {
    return appobject;
  }

  public void setAppObject(Object ob)
  {
    appobject=ob;
  }
  
  public void fix()
  {
    hitDirty = true;
    
    Point line[]=new Point[rolestyle.getLine().length];
    for (int i=0;i<line.length;i++)
      line[i]=new Point(rolestyle.getLine()[i]);
    
    // Notice, it's important to call headdrawer first since 
    // it changes the line to make room for the head.
    headdrawer.fixFromRoleStyle(rolestyle,line);  
    linedrawer.fixFromRoleStyle(rolestyle,line);
  }
  
  public void paint(Graphics g, Color over)
  {
    headdrawer.paint(g,over);
    linedrawer.paint(g,over);
  }  
  
  public int didHit(MapEvent m)
  {
    if(hitDirty == true)
      {
	linedrawer.fixHitInfo();
	hitDirty = false;
      }
    
    //      if(headbBox.contains(x, y))
    //	return HIT_ROLELINE;
    if(linedrawer.didHit(m))
      {
	m.rolestyle=rolestyle;
	m.hit=MapEvent.HIT_ROLELINE;
	return m.hit;
      }
    return MapEvent.HIT_NONE;
  }
}

