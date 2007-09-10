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
import se.kth.cid.conzilla.map.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.conzilla.tool.*;
import se.kth.cid.conceptmap.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HandledBox extends HandledObject
{
  class BoxHandle extends DefaultHandle
  {
    private BoxHandle xcomposant;
    private BoxHandle ycomposant;
    private BoxHandle oppositehandle;
    private int swidth=1,sheight=1;
    
    public BoxHandle(Point p) {super(p);}
    public void addNeighbours(BoxHandle xcomposant, BoxHandle ycomposant, BoxHandle oppositehandle)
    {
      this.xcomposant=xcomposant;
      this.ycomposant=ycomposant;
      this.oppositehandle=oppositehandle;
      int twidth=this.x-ycomposant.x;
      if (twidth<0) swidth=-1;
      int theight=this.y-xcomposant.y;
      if (theight<0) sheight=-1;
    }
    public void drag(int x,int y)
    {
      if (isSelected())
	{
	  int twidth=this.x-ycomposant.x;
	  int theight=this.y-xcomposant.y;
	  if (swidth*(twidth+x) < radius*2 )
	    x=radius*2*swidth - twidth;
	  if (sheight*(theight+y) < radius*2 )
	    y=radius*2*sheight - theight; 
	  
	  float xrelative=(float) (this.x-oppositehandle.x);
	  xrelative=(xrelative+((float) x))/xrelative;
	  float yrelative=(float) (this.y-oppositehandle.y);
	  yrelative=(yrelative+((float) y))/yrelative;
	  
	  Enumeration en=rolehandles.elements();
	  for (;en.hasMoreElements();)
	    ((FollowHandle) en.nextElement()).dragRelative(xrelative, yrelative, oppositehandle);
	  if (neuronlinehandle!=null)
	  neuronlinehandle.dragRelative(xrelative, yrelative, oppositehandle);
	  
	  dragForced(x,y);
	  xcomposant.dragForced(x,0);
	  ycomposant.dragForced(0,y);
	}
    }
  }
  public class BoxTotalHandle extends DefaultHandle
  {
    private BoxHandle bhul, bhur, bhlr, bhll;
    public BoxTotalHandle(Rectangle r) {super(r);}
    public void addNeighbours(BoxHandle bhul,BoxHandle bhur,BoxHandle bhlr,BoxHandle bhll)
      {
	this.bhul = bhul;
	this.bhur = bhur;
	this.bhlr = bhlr;
	this.bhll = bhll;
      }
    public void drag(int x,int y)
    {
      if (isSelected())
	{
	  Enumeration en=rolehandles.elements();
	  for (;en.hasMoreElements();)
	    ((FollowHandle) en.nextElement()).drag(x,y);
	  if (neuronlinehandle!=null)
	    neuronlinehandle.drag(x,y);
	  
	  translate(x,y);
	  bhul.dragForced(x,y);
	  bhur.dragForced(x,y);
	  bhlr.dragForced(x,y);
	  bhll.dragForced(x,y);
	}
      }
    public Point getOffset(MapEvent m)
    {
      return new Point(x-m.mouseevent.getX(),y-m.mouseevent.getY());
    }
    public void paint(Graphics g)
      {
	if (isSelected())
	  {
	    g.drawRect(x,y,width,height);	
	    g.drawRect(x+1,y+1,width-2,height-2);
	  }
      }
  }
  public class FollowHandle extends DefaultHandle
  {
    public RoleStyle rs;
    float xpos,ypos;
    public FollowHandle(Point p,RoleStyle rs)
    {
      super(p);
      xpos=(float) x;
      ypos=(float) y;
      this.rs=rs;
    }
    public FollowHandle(Point p)
    {
      super(p);
      xpos=(float) x;
      ypos=(float) y;
      rs=null;
    }
    public void drag(int x, int y)
    {
      xpos+=(float) x;
      ypos+=(float) y;
    }
    public void dragRelative(float xresize, float yresize, DefaultHandle oppositehandle)
    {
      xpos=(xpos-((float) oppositehandle.x))*xresize+oppositehandle.x;
      ypos=(ypos-((float) oppositehandle.y))*yresize+oppositehandle.y;
    }
    public void paint(Graphics g)
    {
      g.drawRect((int) xpos,(int) ypos, width, height);
    }
    public Point getPosition()
    {
      return new Point((int) (xpos +radius), (int) (ypos +radius));
    }
  }

  protected MapEvent mapevent;
  protected BoxHandle ul, ur, lr, ll;
  protected BoxTotalHandle allbox;
  protected Vector rolehandles;
  protected FollowHandle neuronlinehandle;
  
  public HandledBox(MapEvent m)
  {
    super();
    mapevent=m;
    Rectangle bb=m.neuronstyle.getBoundingBox();
    ul = new BoxHandle(bb.getLocation());                  //upper left
    ur = new BoxHandle(new Point(bb.x+bb.width,bb.y));          //upper right
    lr = new BoxHandle(new Point(bb.x+bb.width,bb.y+bb.height)); //lower right
    ll = new BoxHandle(new Point(bb.x,bb.y+bb.height));         //lower left
    ul.addNeighbours(ll,ur,lr);
    ur.addNeighbours(lr,ul,ll);
    lr.addNeighbours(ur,ll,ul);
    ll.addNeighbours(ul,lr,ur);
    allbox=new BoxTotalHandle(new Rectangle(bb.x,bb.y,bb.width,bb.height));
    allbox.addNeighbours(ul,ur,lr,ll);
    handles.addElement(ul);
    handles.addElement(ur);
    handles.addElement(lr);
    handles.addElement(ll);
    handles.addElement(allbox);   //Last so that corners is "above" the big box-handle.
    
    //Neuronline should follow.
    Point[] line=mapevent.neuronstyle.getLine();
    if (line.length!=0)
      neuronlinehandle=new FollowHandle(line[line.length-1]);	
    else neuronlinehandle=null;

    //Roles ending at this 
    rolehandles=new Vector();
    Enumeration en=m.neuronstyle.getPlaysRoles().elements();
    for (;en.hasMoreElements();)
      {
	RoleStyle rs = (RoleStyle) en.nextElement();
	Tracer.debug("RoleStyle in HandledBox: " + rs.getRoleOwner().getURI());
	line=rs.getLine();
	if (line.length!=0)
	  rolehandles.addElement(new FollowHandle(line[line.length-1],rs));
      }
  }
  public Point startDragImpl(MapEvent m)
  {
    deSelectAll();
    Handle ha=chooseHandle(m);
    if (ha!=null)
      return ha.getOffset(m);
    return null;
  }
  public void paint(Graphics g)
  {
    super.paint(g);
    if (neuronlinehandle!=null)
      neuronlinehandle.paint(g);
    Enumeration en=rolehandles.elements();
    for (;en.hasMoreElements();)
      ((FollowHandle) en.nextElement()).paint(g);
  }
  
  public void stopDragImpl(MapEvent m)
  {
    deSelectAll();
  }

  protected void endDrag(MapEvent m)
  {
    Point pos=ul.getPosition();
    Rectangle bb=new Rectangle(pos.x,pos.y,ur.x-ul.x,ll.y-ul.y);
    mapevent.neuronstyle.setBoundingBox(bb);
    handles.removeElement(allbox);
    allbox=new BoxTotalHandle(new Rectangle(bb.x,bb.y,bb.width,bb.height));
    allbox.addNeighbours(ul,ur,lr,ll);
    handles.addElement(allbox);
    
    //End-handles belonging to roles and neuronline needs updating.
    Point[] line=mapevent.neuronstyle.getLine();
    if (neuronlinehandle!=null)
      line[line.length-1]=neuronlinehandle.getPosition();
    mapevent.neuronstyle.setLine(line);
    Enumeration en=rolehandles.elements();
    for (;en.hasMoreElements();)
      {
	FollowHandle fh=(FollowHandle) en.nextElement();
	line=fh.rs.getLine();
	line[line.length-1]=fh.getPosition();
	fh.rs.setLine(line);
      }
  }
}
