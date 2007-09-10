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
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

public class HandledMap extends HandledObject
{
    static int fixX;
    static int fixY;

    static {
	String version = System.getProperty("java.version");
	if (version.startsWith("1.2"))
	    {
		fixX=1;
		fixY=1;
	    }
	else
	    {
		fixX=0;
		fixY=0;
	    }
    }

    Rectangle mark, pr;
    TieTool tieTool;
    Graphics2D g;
    double scale;
    MapDisplayer displayer;
    HashSet loners;
    HashSet followers;
    HashSet draggers;
    
    
  class MapHandle extends AbstractHandle
  {
    Rectangle west;
    Rectangle north;

    public MapHandle(ConceptMap.Dimension d) 
      {
	  //	  super(new ConceptMap.Position(d.width-40 d.height-40));
	  west = new Rectangle(d.width-40, d.height-5, 35, 5);
	  north = new Rectangle(d.width-5, d.height-40, 5, 40);
	  mark=new Rectangle();
	  pr=new Rectangle();
      }

      public Collection drag(int x, int y)
      {return dragForced(x,y);}
    public Collection dragForced(int x,int y)
    {
	Rectangle oldrect = new Rectangle(west.x,north.y, 40, 40);
	north.translate(x,y);
	west.translate(x,y);

	Rectangle newrect = new Rectangle(west.x,north.y, 40, 40);
	Vector vec = new Vector();
	vec.addElement(oldrect);
	vec.addElement(newrect);
	return vec;
    }
    public ConceptMap.Position getPosition() 
    {
	return new ConceptMap.Position(west.x+40,north.y+40);
    }

    public boolean contains(MapEvent m)
    {
      return north.contains(m.mapX,m.mapY) ||
	  west.contains(m.mapX, m.mapY);
    }
    public void simplePaint(Graphics2D g)
    {
	g.fillRect(north.x, north.y, north.width, north.height);	
	g.fillRect(west.x, west.y, west.width, west.height);	
    }
      
      public void paint(Graphics2D g)
      {
	  simplePaint(g);
      }

    public ConceptMap.Position getOffset(MapEvent m) 
      {
	  return new ConceptMap.Position(west.x+35-m.mapX,north.y+35-m.mapY);
      }
  }

  ConceptMap conceptMap;
  MapHandle mapHandle;
  JComponent component;
    HandleStore store;
    Vector sel;

  public HandledMap(MapEvent m,ConceptMap cmap, JComponent component, HandleStore store, TieTool tieTool, MapDisplayer displayer)
  {
    super(m.mapObject);
    conceptMap=cmap;
    this.component = component;
    this.displayer=displayer;
    this.store = store;
    this.tieTool = tieTool;
    loadFromModel();
  }

  public void loadFromModel()
  {
      removeAllHandles();
    ConceptMap.Dimension dim=conceptMap.getDimension();
    mapHandle=new MapHandle(dim);
    //    addHandle(mapHandle);       
  }

    public ConceptMap.Position startDragImpl(MapEvent m)
    {
	currentHandle=null;
	if (mapHandle.contains(m))
	    {
		currentHandle=mapHandle;
		currentHandle.setSelected(true);
		return currentHandle.getOffset(m);
	    }
	else
	    {
		mark.setLocation(m.mapX, m.mapY);
		mark.setSize(0,0);
		return new ConceptMap.Position(0,0);
	    }
    }
    
    protected Collection dragImpl(MapEvent m, int x, int y) 
    {
	if (currentHandle!=null && currentHandle.isSelected())
	    return currentHandle.drag(x,y);
	
	if (x==0 && y==0)
	    return null;

	if (g==null)
	    {
		fetchAllHandles();
		component.repaint();

		g = (Graphics2D) component.getGraphics();
		scale=displayer.getScale();
		pr=positiveRectangle(mark);
	    }

	g.setColor(Color.black);
	g.setXORMode(Color.white);		

	g.drawRect((int) (scale*pr.x),(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));

	mark.setSize(m.mapX-mark.x, m.mapY-mark.y);
	pr=positiveRectangle(mark);
	g.drawRect((int) (scale*pr.x),(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));

	Collection cols=new Vector();

	if (!tieTool.isActivated())
	    {
		Iterator it = handles.iterator();
		while (it.hasNext())
		    {
			Handle ha = (Handle) it.next();
			Rectangle re=ha.getBounds();
			if (re==null)
			    continue;
			if (ha.isSelected()!=pr.contains(re))
			    {
				ha.setSelected(!ha.isSelected());
				cols.add(re);
			    }
		    }
	    }
	else
	    {
		Iterator it = loners.iterator();
		while (it.hasNext())
		    {
			Handle ha = (Handle) it.next();
			Rectangle re=ha.getBounds();
			if (re==null)
			    continue;
			if (ha.isSelected()!=pr.contains(re))
			    {
				ha.setSelected(!ha.isSelected());
				cols.add(re);
			    }
		    }

		//followers
		it = followers.iterator();
		while (it.hasNext()) 
		    {
			boolean contained=false;
			boolean oldcontained=false;
			Collection fol = (Collection) it.next();
			Collection hitfollowers=null;
			Iterator ir = fol.iterator();
			while (ir.hasNext())
			    {
				Handle ha = (Handle) ir.next();
				oldcontained=ha.isSelected();
				hitfollowers = ha.getFollowers();
				Rectangle re=ha.getBounds();
				if (re==null)
				    continue;
				if (pr.contains(re))
				    {
					contained=true;
					break;
				    }
			    }
			if (contained != oldcontained )
			    {
				ir=hitfollowers.iterator();
				while (ir.hasNext())
				    {
					Handle ha = (Handle) ir.next();
					Rectangle re=ha.getBounds();
					cols.add(re);
					ha.setSelected(contained);
				    }
			    }
		    }

		//draggers
		it = draggers.iterator();
		while (it.hasNext())
		    {
			Handle ha = (Handle) it.next();
			Rectangle re=ha.getBounds();
			if (re==null)
			    continue;
			boolean select=pr.contains(re);

			//FIXME: degenerate case with zero-width (or height) rectangle don't work with contain function.
			if ((re.width==0 || re.height ==0) &&
			    pr.contains(re.getLocation()) && 
			    pr.contains(re.x+re.width, re.y+re.height))
			    select=true;

			if (ha.isSelected() != select)
			    {
				ha.setSelected(select);
				cols.add(ha.getBounds());
			    }
			if (ha.getFollowers()!=null)
			    {
				Iterator ir=ha.getFollowers().iterator();
				while (ir.hasNext())
				    {
					Handle h=(Handle) ir.next();
					if (h.isSelected() != select)
					    {
						h.setSelected(select);
						cols.add(h.getBounds());
					    }
				    }
			    }
		    }
	    }
	return cols;
    }
    
    public Collection getSelected()
    {	
	return sel;
    }
	
    protected void fetchAllHandles()
    {
	loners    = new HashSet();
	followers = new HashSet();
	draggers  = new HashSet();

	NeuronStyle [] nss = conceptMap.getNeuronStyles();
	for (int i=0;i<nss.length;i++)
	    {
		AxonStyle [] ass = nss[i].getAxonStyles();
		for (int j=0;j<ass.length;j++)
		    {
			Collection col=getAxonHandles(ass[j], store);
			loners.addAll(col);
			addHandles(col);
		    }
		NeuronLineHandlesStruct nlhs = store.getNeuronLineHandles(nss[i]);
		if (nlhs.getFirstHandle()!= null)  //If neuronline is visible, move it along
		    {
			loners.addAll(nlhs.handles);
			addHandles(nlhs.handles);
		    }

		if (nss[i].getBodyVisible())
		   {
		       Handle h=getBoxTotalHandle(nss[i], store);
		       loners.removeAll(getBoxFollowers(nss[i], store));
		       draggers.add(h);
		       addHandle(h);
		   }
		
		Collection ac=getAxonCenterFollowers(nss[i], store);
		loners.removeAll(ac);
		followers.add(ac);
		//Just for followhandles to be set correctly.
	    }
    }
    

    private Rectangle positiveRectangle(Rectangle re)
    {
	if (re.width < 0)
	    if (re.height <0)
		return new Rectangle(re.x+re.width, re.y+re.height, -re.width, -re.height);
	    else
		return new Rectangle(re.x+re.width, re.y, -re.width, re.height);
	else
	    if (re.height <0)
		return new Rectangle(re.x, re.y+re.height, re.width, -re.height);
	    else
		return new Rectangle(re.x, re.y, re.width, re.height);
    }

  protected void endDrag(MapEvent m)
    {
      sel = new Vector();
      Iterator it = handles.iterator();
      while (it.hasNext())
	  {
	      Handle ha=(Handle) it.next();
	      if (ha.isSelected())
		  sel.add(ha);
	  }
	if (g!=null)
	    {
		g.drawRect((int) (scale*pr.x),(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));
		g.setPaintMode();
		g=null;
		removeAllHandles();
		component.repaint();
	    }

      
    lock = true;
    ConceptMap.Position pos=mapHandle.getPosition();
    ConceptMap.Dimension dim=conceptMap.getDimension();
    if (pos.x!=dim.width || pos.y!=dim.height)
	{
	    ConceptMap.Dimension ndim=new ConceptMap.Dimension(pos.x, pos.y);
	    conceptMap.setDimension(ndim);
	}
    lock = false;
  }
  public boolean update(EditEvent e) 
  {
      if (lock)
	  return true;
      if (e.getEditType()==ConceptMap.DIMENSION_EDITED)
	  loadFromModel();
      return true;
  }
    
  public void detach()
  {
      mapHandle=null;
  }

    public void paint(Graphics2D g, Graphics2D original)
    {
	if (this.g!=null) //marking...
	    {
		super.paint(g, original);
		original.setColor(Color.black);
		original.setXORMode(Color.white);
		original.drawRect(fixX+(int) (scale*pr.x),fixY+(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));
		original.setPaintMode();		
	    }
	else
	    mapHandle.paint(g);
    }	
}
