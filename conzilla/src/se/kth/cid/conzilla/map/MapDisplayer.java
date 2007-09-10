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


package se.kth.cid.conzilla.map;
import se.kth.cid.conceptmap.*;
import se.kth.cid.conceptmap.ConceptMap;  //Shouldn't really be neccessary.
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.conzilla.controller.*;
import se.kth.cid.neuron.*;
import se.kth.cid.content.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.lang.*;


public class MapDisplayer extends JLayeredPane implements EditListener
{
  public static final int CLICK           = 0;
  public static final int PRESS_RELEASE   = 1;
  public static final int MOVE_DRAGG      = 2;

  public final static Integer BACKGROUND_LAYER = new Integer(10);
  public final static Integer OVERLAY_LAYER = new Integer(30);

  
  ConceptMap conceptmap;
  ContentDescription contentdescription;
  public Vector clicklisteners;           
  public Vector pressandreleasedlisteners;
  public Vector moveanddragglisteners;
  private MapDrawer mapdrawer;
  private MapComponentDrawer mapcomponentdrawer;
  private Background background;
  private Hashtable nspaintobjects;
  private MapController mapcontroller;
  
  public class MapDrawer extends JComponent
  {
    public MapDrawer() {}
    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      MapDisplayer.this.paintHelp(g);
      //      super.paint(g);   needed????
    }
  }
  public class MapComponentDrawer extends JComponent
  {
    public MapComponentDrawer() {}
  }

  public class Background extends JPanel implements ResizeComponent
  {
    Point translated;
    public Background()
    {
      super();
      translated=new Point(0,0);
    }
    public void layersTranslated(Point translate) {this.translated.translate(translate.x, translate.y);}
    public boolean isLayerTranslated() {return translated.equals(new Point(0,0));}
    public Point translated() {return translated;}
  };
  
  public MapDisplayer(ConceptMap conceptmap, ContentDescription cdesc) 
  {
    Tracer.debug("MapDisp NEW!!!");
    this.contentdescription=cdesc;
    mapcomponentdrawer=new MapComponentDrawer();
    mapcomponentdrawer.setVisible(true);
    mapcomponentdrawer.setSize(conceptmap.getBoundingBox());    
    mapcomponentdrawer.setOpaque(false);
    setOpaque(false);

    background=new Background();
    background.setOpaque(true);
    background.setBackground(Color.white);
    background.setSize(conceptmap.getBoundingBox());
    background.setLocation(0,0);    
    
    mapdrawer=new MapDrawer();
    mapdrawer.setVisible(true);
    mapdrawer.setSize(conceptmap.getBoundingBox());
    mapdrawer.setOpaque(false);

    setSize(conceptmap.getBoundingBox());
    setPreferredSize(conceptmap.getBoundingBox());

    
    this.conceptmap=conceptmap;
    fixPaintingObjects();
    
    clicklisteners = new Vector();
    pressandreleasedlisteners = new Vector();
    moveanddragglisteners = new Vector();
    mapdrawer.addMouseListener(new MouseInputAdapter() {
	public void mouseClicked(MouseEvent e)
	{MapDisplayer.this.dispatchEvent(MapDisplayer.this.clicklisteners,e);}
	public void mousePressed(MouseEvent e)
	{MapDisplayer.this.dispatchEvent(MapDisplayer.this.pressandreleasedlisteners,e);}
	public void mouseReleased(MouseEvent e)
	{MapDisplayer.this.dispatchEvent(MapDisplayer.this.pressandreleasedlisteners,e);}
      });
    mapdrawer.addMouseMotionListener(new MouseMotionAdapter() {
	public void mouseDragged(MouseEvent e)
	{MapDisplayer.this.dispatchEvent(MapDisplayer.this.moveanddragglisteners,e);}
	public void mouseMoved(MouseEvent e)
	{MapDisplayer.this.dispatchEvent(MapDisplayer.this.moveanddragglisteners,e);}
      });

    add(mapcomponentdrawer,new Integer(20));
    add(mapdrawer,new Integer(20));
    add(background,new Integer(0));
    setMapComponentLayerInFront(false);
    mapdrawer.repaint();
    mapcomponentdrawer.repaint();
    
    conceptmap.addEditListener(this);
  }

  //******************PAINTING********************//
  public void paintHelp(Graphics g)
  {
    Enumeration en=nspaintobjects.elements();
    for (;en.hasMoreElements();) 
      ((NSPaint) en.nextElement()).paint(g);
    }
    
  protected void fixPaintingObjects()
  {
    detachNS();
    nspaintobjects=new Hashtable();    
    Enumeration en=conceptmap.getNeuronStyles(); //visible?
    for (;en.hasMoreElements();) 
      {
	NeuronStyle neuronstyle=(NeuronStyle) en.nextElement();
	nspaintobjects.put(neuronstyle, new NSPaint(neuronstyle, mapcomponentdrawer));	    
      }
    mapdrawer.repaint();
    mapcomponentdrawer.repaint();
  }
  public void detach()
  {
    conceptmap.removeEditListener(this);
    detachNS();
    clicklisteners=null;
    pressandreleasedlisteners=null;
    moveanddragglisteners=null;    
  }
  
  protected void detachNS()
  {
    mapcomponentdrawer.removeAll();
    if (nspaintobjects!=null)
      {
	Enumeration en=nspaintobjects.elements();
	for (;en.hasMoreElements();) 
	  ((NSPaint) en.nextElement()).detach();
      }
  }
  
  public void setMapComponentLayerInFront(boolean front)
  {
    if (front)
      moveToFront(mapcomponentdrawer);
    else
      moveToBack(mapcomponentdrawer);
  }
  public Vector setMapDataValuesEditable(boolean editable, ComponentSaver csaver)
  {
    Vector neuronstylefailed=new Vector();
    Enumeration en=nspaintobjects.elements(); //visible?
    for (;en.hasMoreElements();) 
      {
	NSPaint nsp=(NSPaint) en.nextElement();
	if (!nsp.setDataValuesEditable(editable, csaver))
	  neuronstylefailed.addElement(nsp.neuronstyle);
      }
    return neuronstylefailed;
  }
  public Vector setMapTitleEditable(boolean editable)
  {
    Vector neuronstylefailed=new Vector();
    Enumeration en=nspaintobjects.elements();
    for (;en.hasMoreElements();) 
      {
	NSPaint nsp=(NSPaint) en.nextElement();
	if (!nsp.setTitleEditable(editable))
	  neuronstylefailed.addElement(nsp.neuronstyle);
      }
    return neuronstylefailed;
  }
  public void addMapEventListener(MapEventListener c, int listenerto)
  {
    switch(listenerto)
      {
      case CLICK:
	clicklisteners.addElement(c);
	break;
      case PRESS_RELEASE:
	pressandreleasedlisteners.addElement(c);
	break;
      case MOVE_DRAGG:
	moveanddragglisteners.addElement(c);
	break;	
      }
  }

  public void removeMapEventListener(MapEventListener c, int listenerto)
  {
    switch(listenerto)
      {
      case CLICK:
	clicklisteners.removeElement(c);
	break;
      case PRESS_RELEASE:
	pressandreleasedlisteners.removeElement(c);
	break;
      case MOVE_DRAGG:
	moveanddragglisteners.removeElement(c);
	break;	
      }
    }

  // HACK!! Behövs inte i JDK >= 1.1.8 eller >= 1.2.2
  public void setCursor(Cursor c)
  {
    mapdrawer.setCursor(c);
  }
  
    //    Component[] comps = getComponents();
//    for(int i = 0; i < comps.length; i++)
//	{
//	  comps[i].setCursor(c);
//	}
//  }

//  public void mark(MouseEvent e)
//    {
//	if (e.isClick())  //kolla upp isClick()
//	  
//	  }
//
  
  public void dispatchEvent(Vector listeners, MouseEvent e)
    {
      //      if(mark)
      //	mark(e);
      // else
     if (listeners.size()!=0)
      {
	MapEvent m=new MapEvent(e, this);
	resolveEvent(m);
	for(int i = 0; i < listeners.size(); i++)
	  {
	    MapEventListener c = (MapEventListener) listeners.elementAt(i);
	    c.eventTriggered(m);
	  }
      }
  }  
  
  private void resolveEvent(MapEvent m)
  {
    Enumeration en=nspaintobjects.elements();
    for (;en.hasMoreElements();) 
      {
	NSPaint nsp=(NSPaint) en.nextElement();
	
	if(( m.hit = nsp.didHit(m.mouseevent.getX(), m.mouseevent.getY(),m)) != MapEvent.HIT_NONE)
	  return;
      }
  }
  
  public ConceptMap getMap()
  {
    return conceptmap;
  }
  public ContentDescription getContentDescription()
  {
    return  contentdescription;
  }

  public void componentEdited(EditEvent e)
  {
    Tracer.debug("MapDisplayer notified that conceptmap was edited.");
    if (e.getEditType() > ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT &&
	e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_EDIT_CONSTANT)
      {
	Tracer.debug("A editevent from the sub components of conceptmap.");
	if (e.getTarget() instanceof NeuronStyle.NeuronStyleEditObject)
	  ((NSPaint) nspaintobjects.get(((NeuronStyle.NeuronStyleEditObject) e.getTarget()).neuronStyle)).fixNS(e);
	else if (e.getTarget() instanceof RoleStyle.RoleStyleEditObject)
	  ((NSPaint) nspaintobjects.get(((RoleStyle.RoleStyleEditObject) e.getTarget()).roleStyle.getRoleOwner())).fixRS(e);
	else
	  {
	    Tracer.debug("Some unknown sub-conceptmap editevent.");
	    Tracer.debug("Do not know how to handle, ignoring event");
	  }
      }
    else if(e.getEditType() > ConceptMap.FIRST_CONCEPTMAP_EDIT_CONSTANT &&
		e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT)
      solelyConceptMapEdited(e);
    else
      solelyComponentEdited(e);
  }
  protected void solelyConceptMapEdited(EditEvent e)
  {
    Tracer.debug("solelyConceptMapEdited");
    fixPaintingObjects();
    /*    switch (e.getEditType())
      {
      case MAPSET_EDITED:
      case BOUNDINGBOX_EDITED:
      case BACKGROUND_EDITED:
      case NEURONSTYLE_ADDED:
      case NEURONSTYLE_REMOVED:
      case ROLESTYLE_REMOVED:
      case ROLESTYLE_ADDED:
      } */
  }
  protected void solelyComponentEdited(EditEvent e)
  {
    Tracer.debug("solelyComponentEdited");
    /*    switch (e.getEditType())
      {
      case EDITABLE_CHANGED:
      case URI_EDITED:
      case METADATATAG_EDITED:
      case METADATATAG_ADDED:
      case METADATATAG_REMOVED:
      default:
	Tracer.debug("Some unknown editEvent:" + e.toString());
	Tracer.debug("Redoing the whole of MapDisplayers painthierarchy:");
	fixPaintingObjects();
      }*/
  }
}
