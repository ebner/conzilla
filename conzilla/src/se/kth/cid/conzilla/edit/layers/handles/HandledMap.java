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

/** Handles the map size and the marking-rectangle. 
 */
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
    Graphics2D g;
    double scale;
    MapDisplayer displayer;
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
    super(m.mapObject, tieTool);
    conceptMap=cmap;
    this.component = component;
    this.displayer=displayer;
    this.store = store;
    loadFromModel();
  }

  public void loadFromModel()
  {
      removeAllHandles();
    ConceptMap.Dimension dim=conceptMap.getDimension();
    mapHandle=new MapHandle(dim);
    //    addHandle(mapHandle);       
  }

    /** Initializes either the map-size-mark or the marking-rectangle.
     */
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
    

    /** Drags the map-size-mark if it is active, otherwise it sets the correct selected 
     *  state on all handles followers and draggers respectively depending on
     *  the change in the marking-rectangle.
     *
     *  @param m the mapevent that contains the event of a drag in the current map.
     *  @param x the relative x-coordinate
     *  @param y the relative y-coordinate
     *  @return a Collection of rectangles where the map need to be redrawn,
     *          i.e. the areas where the handles have changed selected state.
     */
    protected Collection dragImpl(MapEvent m, int x, int y) 
    {
	//We are done if there isn't any change. 
	if (x==0 && y==0)
	    return null;

	//Two alternatives:

	//Case 1: map-size-mark is moved.
	//----------------------------
	if (currentHandle!=null && currentHandle.isSelected())
	    return currentHandle.drag(x,y);

	//Case 2: marking-rectangle should be resized.
	//----------------------------------------

	//Fetch a graphics object used for redrawing of the marking-rectangle
	//Fix xor-mode.
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


	//Undraw the last marking-rectangle (XOR).
	g.drawRect((int) (scale*pr.x),(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));

	//Calculate the new marking-rectangle.
	mark.setSize(m.mapX-mark.x, m.mapY-mark.y);
	pr=positiveRectangle(mark);

	//Draw the new marking-rectangle (XOR).
	g.drawRect((int) (scale*pr.x),(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));


	//Make a new selection based on the marking-rectangle.
	//Observe that this is done with intelligence.
	Collection cols=new Vector();

	//If no tying should be done, just iterate through all handles and make sure
	//that all handles that are within the marking-rectangle is selected.
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
	//If tying should be done the situation is much more complex:
	//Which set of handles that should be selected is calculated
	//separately for followers and draggers.
	//(Order is important.) 
	else
	    {
		//Followers, for each set of followers:
		//If one in a set of followers is within the marking-rectangle
		//all followers in the set is selected.
		Iterator it = followers.iterator();
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
				if (ha.getFollowers()!=null)
				    {
					hitfollowers = ha.getFollowers();
					//check for selected placedin here to avoid controlpoints
					//to be sensitive, (they have no followers).
					oldcontained=ha.isSelected(); 
				    }
				else
				    continue;
				Rectangle re=ha.getBounds();
				if (re==null)
				    continue;
				if (pr.contains(re))
				    {
					contained=true;
					break;
				    }
			    }
			//If one follower in the set is contained,
			//select all and vice versa.
			if (contained != oldcontained )
			    {
				ir=hitfollowers.iterator();
				while (ir.hasNext())
				    {
					Handle ha = (Handle) ir.next();
					Rectangle re=ha.getBounds();
					cols.add(re);
					ha.setSelected(contained);
					//					cols.addAll(ha.setFollowersSelected(contained));
				    }
			    }
		    }

		//Draggers.
		//Each dragger is investigated wheather it should be selected or not,
		//then all followers to this dragger (via the function getFollowers)
		//are updated accordingly.
		it = draggers.iterator();
		while (it.hasNext())
		    {
			Handle ha = (Handle) it.next();
			Rectangle re=ha.getBounds();
			if (re==null)
			    continue;

			// Comment: pr.contains(re) doesn't work when rectangle has zero width or height.
			boolean select = pr.contains(re.getLocation()) && pr.contains(re.x+re.width, re.y+re.height);

			if (ha.isSelected() != select)
			    {
				ha.setSelected(select);
				cols.add(ha.getBounds());
			
				//Need to be done only if the draggers selected state is changed 
				//some other mekanism can't have changed their selected state
				//since the followers sets are disjoint.
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
	    }
	return cols;
    }

    /** @return a collection of all handles currently selected.*/
    public Collection getSelected()
    {	
	return sel;
    }
	
    /** Sorts all handles into the two categories draggers and followers,
     *  a total account of all handles is also kept.
     *  <dl>
     *  <dt>Draggers<dd> are handles that on drag are responsible to drag others.
     *  <dt>Followers<dd> is a set of sets of handles. If you drag one handle 
     *  in such a set all handles in that set is dragged.
     *  </dl>
     *  The two sets are calculated like this:
     *  <ol>
     *  <li> All axon handles (except the ends) are collected as draggers.
     *  <li> All neuronlineshandles (except the ends) are collected as draggers.
     *  <li> All visible boxes are collected as draggers.
     *  <li> All  axoncenters are added to followers,
     *       (they drag each other in a symmetrical fashion).
     *  </ol>
     */
    protected void fetchAllHandles()
    {
	followers = new HashSet();
	draggers  = new HashSet();

	Vector nss = conceptMap.getLayerManager().getNeuronStyles(MapGroupStyle.ONLY_VISIBLE);
	Enumeration en = nss.elements();
	while (en.hasMoreElements())
	    {
		NeuronStyle ns = (NeuronStyle) en.nextElement();
		AxonStyle [] ass = ns.getAxonStyles();
		for (int j=0;j<ass.length;j++)
		    {
			//			addHandles(store.getAxonHandles(ass[j]).handles);
			Collection col =  store.getAxonHandles(ass[j]).getDraggers(false);
			addHandles(col);
			draggers.addAll(col);
		    }			    
		
		NeuronLineHandlesStruct nlhs = store.getNeuronLineHandles(ns);			
		if (nlhs.getFirstHandle()!= null)  //If neuronline is visible, move it along
		    {
			//			addHandles(nlhs.handles);
			Collection col = nlhs.getDraggers(false);
			addHandles(col);
			draggers.addAll(col);
		    }
		
		if (ns.getBodyVisible())
		    {
			store.getAndSetBoxFollowers(ns); //Just for initializing followers.... 
			Handle h = store.getNeuronBoxHandles(ns).tot;
			addHandle(h);
			addHandles(h.getFollowers());
			draggers.add(h);
		    }

		//Just for followhandles to be set correctly.
		Collection col = store.getAndSetAxonCenterFollowers(ns);
		followers.add(col);
		addHandles(col);
	    }
    }
    
    /** Collects the convenience-set of selected handles for later use
     *  or sets a new size for the map.
     *
     *  @param m the MapEvent containing the endDrag.
     */
    protected void endDrag(MapEvent m)
    {
	sel = new Vector();
	//If map-size-mark isn't activated possible map resizing can be skipped.
	//	if (currentHandle==null || !currentHandle.isSelected())
	if (currentHandle==null || currentHandle != mapHandle)
	    {
		Iterator it = handles.iterator();
		while (it.hasNext())
		    {
			Handle ha=(Handle) it.next();
			if (ha.isSelected())
			    sel.add(ha);
		    }
		
		//Undraws the marking-rectangle(XOR).
		if (g!=null)
		    {
			g.drawRect((int) (scale*pr.x),(int) (scale*pr.y),(int) (scale*pr.width),(int) (scale*pr.height));
			g.setPaintMode();
			g=null;
			removeAllHandles();
			component.repaint();
		    }
	    }
	else
	    {
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

    /** Paints either the marking-rectangle or the map-size-mark.
     */
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
