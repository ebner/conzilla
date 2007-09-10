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
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class MapDisplayer extends JPanel implements EditListener
{
  public static final int CLICK           = 0;
  public static final int PRESS_RELEASE   = 1;
  public static final int MOVE_DRAG       = 2;

  MapStoreManager manager;  

  Vector         clickListeners;           
  Vector         pressReleaseListeners;
  Vector         moveDragListeners;

  JLayeredPane       layerPane;
  MapDrawer          mapDrawer;
  MapComponentDrawer mapComponentDrawer;
  
  Hashtable          neuronMapObjects;

  // The graphical parts of the map will be drawn here.
  public class MapDrawer extends JComponent
  {
    public MapDrawer()
      {}
    public void paintComponent(Graphics g)
      {
	super.paintComponent(g);
	paintMap(g);
      }
  }

  // ... except for the components (JTextFields etc.) which will be
  // placed here.
  public class MapComponentDrawer extends JComponent
  {
    public MapComponentDrawer()
      {}
  }

  public MapDisplayer(MapStoreManager manager)
    {
      
      Tracer.debug("MapDisp NEW!!!");
      this.manager = manager;

      setLayout(new FillLayout());
      setOpaque(false);
      
      mapComponentDrawer = new MapComponentDrawer();      
      mapDrawer = new MapDrawer();
      layerPane = new JLayeredPane();
      layerPane.setLayout(new FillLayout());
      
      resizeMap();
      
      updateMapObjects();
    
      clickListeners        = new Vector();
      pressReleaseListeners = new Vector();
      moveDragListeners     = new Vector();
      
      mapDrawer.addMouseListener(new MouseInputAdapter() {
	  public void mouseClicked(MouseEvent e)
	    {
	      dispatchEvent(clickListeners, e);
	    }
	  public void mousePressed(MouseEvent e)
	    {
	      dispatchEvent(pressReleaseListeners, e);
	    }
	  public void mouseReleased(MouseEvent e)
	    {
	      dispatchEvent(pressReleaseListeners, e);
	    }
	});
      
      mapDrawer.addMouseMotionListener(new MouseMotionAdapter() {
	  public void mouseDragged(MouseEvent e)
	    {
	      dispatchEvent(moveDragListeners,e);
	    }
	  public void mouseMoved(MouseEvent e)
	    {
	      dispatchEvent(moveDragListeners,e);
	    }
	});

      layerPane.add(mapComponentDrawer, new Integer(10));      
      layerPane.add(mapDrawer, new Integer(10));
      
      this.add(layerPane);
      
      layerPane.moveToBack(mapComponentDrawer);

      manager.getConceptMap().addEditListener(this);
    }


  void resizeMap()
    {
      ConceptMap.Dimension dim = manager.getConceptMap().getDimension();

      //      layerPane.setSize(dim.width, dim.height);
      //      mapComponentDrawer.setSize(dim.width, dim.height);
      //      mapDrawer.setSize(dim.width, dim.height);
      setMinimumSize(new Dimension(dim.width, dim.height));
      setPreferredSize(new Dimension(dim.width, dim.height));
      revalidate();
    }
      
  //******************PAINTING********************//
  public void paintMap(Graphics g)
    {
      Iterator mapObjects = neuronMapObjects.values().iterator();
      while(mapObjects.hasNext()) 
	((NeuronMapObject) mapObjects.next()).paint(g);
    }
  
  
  protected void updateMapObjects()
    {
      detachNeuronMapObjects();
      neuronMapObjects = new Hashtable();    
      
      NeuronStyle[] styles = manager.getConceptMap().getNeuronStyles();
      
      for(int i = 0; i < styles.length; i++) 
	neuronMapObjects.put(styles[i],
			     new NeuronMapObject(styles[i],
						 manager.getNeuron(styles[i].getID()),
						 manager.getNeuronType(styles[i].getID()),
						 this, mapComponentDrawer));
      mapDrawer.repaint();
      mapComponentDrawer.repaint();
    }
  
  public void detach()
    {
      manager.getConceptMap().removeEditListener(this);
      detachNeuronMapObjects();
      clickListeners        = null;
      pressReleaseListeners = null;
      moveDragListeners     = null;    
    }
  
  protected void detachNeuronMapObjects()
    {
      mapComponentDrawer.removeAll();
      if (neuronMapObjects != null)
	{
	  for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	    ((NeuronMapObject) i.next()).detach();
	}
    }
  
  public void prepareEdit(boolean editable)
    {
      if(editable && !manager.getConceptMap().isEditable())
	return;
      
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  nmo.prepareEdit(editable);
	}
      if(editable)
	layerPane.moveToFront(mapComponentDrawer);
      else
	layerPane.moveToBack(mapComponentDrawer);
    }

  public void addMapEventListener(MapEventListener c, int listenerTo)
  {
    switch(listenerTo)
      {
      case CLICK:
	clickListeners.addElement(c);
	break;
      case PRESS_RELEASE:
	pressReleaseListeners.addElement(c);
	break;
      case MOVE_DRAG:
	moveDragListeners.addElement(c);
	break;	
      }
  }

  public void removeMapEventListener(MapEventListener c, int listenerTo)
  {
    switch(listenerTo)
      {
      case CLICK:
	clickListeners.removeElement(c);
	break;
      case PRESS_RELEASE:
	pressReleaseListeners.removeElement(c);
	break;
      case MOVE_DRAG:
	moveDragListeners.removeElement(c);
	break;	
      }
    }

  // HACK!! Behövs inte i JDK >= 1.1.8 eller >= 1.2.2
//  public void setCursor(Cursor c)
//  {
//    mapDrawer.setCursor(c);
//  }
//  
  
  public void dispatchEvent(Vector listeners, MouseEvent e)
    {
     if (listeners.size() != 0)
      {
	MapEvent m = new MapEvent(e, this);
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
    for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
      {
	NeuronMapObject nmo = (NeuronMapObject) i.next();
	
	if((m.hitType = nmo.checkHit(m.mouseEvent.getX(),
				     m.mouseEvent.getY(), m))
	   != MapEvent.HIT_NONE)
	  return;
      }
  }
  
  public MapStoreManager getStoreManager()
    {
      return manager;
    }

  public void componentEdited(EditEvent e)
    {
      if (e.getEditType() > ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT &&
	  e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_EDIT_CONSTANT)
	{
	  Tracer.debug("A editevent from the sub components of conceptmap.");
	  if (e.getEditedObject() instanceof NeuronStyle)
	    ((NeuronMapObject) neuronMapObjects.get((NeuronStyle) e.getEditedObject())).updateSelf(e);
	  else if (e.getEditedObject() instanceof AxonStyle)
	    ((NeuronMapObject) neuronMapObjects.get(((AxonStyle) e.getEditedObject()).getOwner())).updateAxonMapObjects(e);
	  else
	    {
	      Tracer.debug("Some unknown sub-conceptmap editevent.");
	      Tracer.debug("Do not know how to handle, ignoring event");
	    }
	}
      else if(e.getEditType() >= ConceptMap.FIRST_CONCEPTMAP_EDIT_CONSTANT &&
	      e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT)
	solelyConceptMapEdited(e);
      else
	solelyComponentEdited(e);
    }
  
  protected void solelyConceptMapEdited(EditEvent e)
    {
      Tracer.debug("solelyConceptMapEdited");
      resizeMap();
      
      updateMapObjects();
      /*    switch (e.getEditType())
	    {
	    case MAPSET_EDITED:
	    case BOUNDINGBOX_EDITED:
	    case BACKGROUND_EDITED:
	    case NEURONSTYLE_ADDED:
	    case NEURONSTYLE_REMOVED:
	    case AXONSTYLE_REMOVED:
	    case AXONSTYLE_ADDED:
	    } */
    }

  protected void solelyComponentEdited(EditEvent e)
    {
      Tracer.debug("solelyComponentEdited: did not listen!");
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
