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
import se.kth.cid.conzilla.properties.*;
import se.kth.cid.conzilla.map.graphics.*;
import se.kth.cid.component.*;
import se.kth.cid.util.*;
import se.kth.cid.conzilla.util.*;
import se.kth.cid.neuron.*;
import se.kth.cid.identity.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

// In order of increasing color priority:
//
// Default:     MapDisplayer.getForeground()// Error:       Internal to each drawing obj.
// Mark:        NeuronMapObject.getMark()
// GlobalMark:  MapDisplayer.getGlobalMarkColor()
//

public class MapDisplayer extends JPanel implements EditListener, LayerListener
{
  public static final int CLICK           = 0;
  public static final int PRESS_RELEASE   = 1;
  public static final int MOVE_DRAG       = 2;

    public static final String COLOR_BACKGROUND                = "conzilla.map.background.color";
    public static final String COLOR_FOREGROUND                = "conzilla.map.foreground.color";
    public static final String COLOR_TEXT                      = "conzilla.map.text.color";
    public static final String COLOR_NEURON_BACKGROUND         = "conzilla.map.neuron.background.color";
    public static final String COLOR_NEURON_ERROR              = "conzilla.map.neuron.error.color";


    static final String[] COLOR_SET = {
	COLOR_BACKGROUND,        
	COLOR_FOREGROUND,
	COLOR_TEXT,
	COLOR_NEURON_BACKGROUND,
	COLOR_NEURON_ERROR
    };

    static
    {
	GlobalConfig.getGlobalConfig().addDefaults(MapDisplayer.class);
	GlobalConfig.getGlobalConfig().registerColorSet("COLOR_MENU", COLOR_SET, MapDisplayer.class.getName());
    }

  MapStoreManager manager;  

  Vector         clickListeners;           
  Vector         pressReleaseListeners;
  Vector         moveDragListeners;

  MouseInputListener mouseListener;
  
  Color globalMarkColor = null;
  
  boolean listenersAdded = false;

  boolean editable = false;
  
  JLayeredPane       layerPane;

  // The graphical parts of the map will be drawn here.
  MapDrawer          mapDrawer;

  // ... except for the components (JTextAreas etc.) which will be
  // placed here, to enable editing on/off.
  JComponent         mapComponentBack;
    //  JComponent         mapComponentFront;

  double             scale = 1.0;
  AffineTransform    transform;
  
  Hashtable          neuronMapObjects;
  Vector             visibleOrderedNeuronMapObjects;
  Vector             visibleAntiOrderedNeuronMapObjects;

  public class MapDrawer extends JComponent
  {
    public MapDrawer()
      {}
    public void paintComponent(Graphics g)
      {
	paintMap(g);
      }
  }


  public MapDisplayer(MapStoreManager manager)
    {
      this.manager = manager;

      setLayout(new FillLayout());
      setOpaque(false);
      
      
      mapComponentBack = new JPanel();
      mapComponentBack.setOpaque(false);
      mapComponentBack.setLayout(null);
      //      mapComponentFront = new JPanel();
      //      mapComponentFront.setOpaque(false);
      //      mapComponentFront.setLayout(null);

      mapDrawer = new MapDrawer();
      layerPane = new JLayeredPane();
      layerPane.setLayout(new FillLayout());

      transform = AffineTransform.getScaleInstance(scale, scale);
      
      resizeMap();

      createMapObjects();
    
      clickListeners        = new Vector();
      pressReleaseListeners = new Vector();
      moveDragListeners     = new Vector();
      
      mouseListener = new MouseInputListener() {
	  public void mouseEntered(MouseEvent e)
	    {
	      perhapsRequestFocus(); //Should be enough to get focus for popups, etc.
	      dispatchEvent(moveDragListeners,
			    new MapEvent(e, (int) (e.getX()/scale),
					 (int) (e.getY()/scale),
					 MapDisplayer.this));
	    }
	  
	  public void mouseExited(MouseEvent e)
	    {
	      dispatchEvent(moveDragListeners,
			    new MapEvent(e, (int) (e.getX()/scale),
					 (int) (e.getY()/scale),
					 MapDisplayer.this));
	    }

	  public void mouseClicked(MouseEvent e)
	    {
	      resolveEvent(clickListeners, e);
	    }
	  public void mousePressed(MouseEvent e)
	    {
	      resolveEvent(pressReleaseListeners, e);
	    }
	  public void mouseReleased(MouseEvent e)
	    {
	      resolveEvent(pressReleaseListeners, e);
	    }
	  public void mouseDragged(MouseEvent e)
	    {
	      resolveEvent(moveDragListeners, e);
	    }
	  public void mouseMoved(MouseEvent e)
	    {
	      resolveEvent(moveDragListeners, e);
	    }
	};
      
      layerPane.add(mapComponentBack, new Integer(9));      
      layerPane.add(mapDrawer, new Integer(10));
      //      layerPane.add(mapComponentFront, new Integer(11));      
      
      this.add(layerPane);


      
      // Please note: This supposes that the editlisteners in
      // Components are order-preserving: First added, first served.
      //
      // Otherwise, NeuronStyle additions would not come to the
      // MapStoreManager first...
      //
      manager.getConceptMap().addEditListener(this);
      manager.getConceptMap().getLayerManager().addLayerListener(this);
    }

  public Color getGlobalMarkColor()
    {
      return globalMarkColor;
    }

  
  public void setGlobalMarkColor(Color c)
    {
      globalMarkColor = c;
      colorUpdate();
      repaint();
    }
  
  public void pushMark(Set set, Mark mark, Object o)
    {
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  Neuron neuron = nmo.getNeuron();
	  //	  Tracer.debug("checking if neuron :"+neuron.getURI());
	  if (neuron!= null && set.contains(neuron.getURI()))
	      nmo.pushMark(mark, o);
	}
      repaint();      
    }
    
  public void popMark(Set set, Object o)
    {
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  Neuron neuron = nmo.getNeuron();
	  if (neuron!= null && set.contains(neuron.getURI()))
	      nmo.popMark(o);
	}
      //      repaint();
    }
  

  public void setDisplayLanguageDiscrepancy(boolean b)
    {
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  nmo.setDisplayLanguageDiscrepancy(b);
	}
      //      repaint();
    }

  
  public MapStoreManager getStoreManager()
    {
      return manager;
    }
  
  public NeuronMapObject getNeuronMapObject(String neuronStyleID)
    {
      return (NeuronMapObject) neuronMapObjects.get(neuronStyleID);
    }

  public void reset()
    {
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  nmo.setEditable(false, null);
	  nmo.clearMark();
	}
    }
  
  public void setScale(double scale)
    {
      this.scale = scale;
      transform = AffineTransform.getScaleInstance(scale, scale);
      resizeMap();
      
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  nmo.setScale(scale);
	}
      revalidate();
      repaint();
    }

  public double getScale()
    {
      return scale;
    }
  
  //////////// Painting /////////////

  public void repaintMap(Collection rectangles)
    {
	Iterator it=rectangles.iterator();
	for (;it.hasNext();)
	    {
		Rectangle rect = (Rectangle) it.next();
		
		repaint((int) ((rect.x-2)*scale), (int) ((rect.y-2)*scale),
			(int) ((rect.width+5)*scale), (int) ((rect.height+5)*scale));
	    }
    }

  public void paintMap(Graphics g)
    {
      Graphics2D gr     = (Graphics2D) g;
      
      Shape clip        = gr.getClip();
      AffineTransform f = gr.getTransform();
      
      gr.transform(transform);
      try {
	gr.setClip(transform.createInverse().createTransformedShape(clip));
      } catch (NoninvertibleTransformException e)
	{
	  Tracer.error("Non-invertible transform: " + transform + ":\n "
		       + e.getMessage());
	}

      setRenderingHints(gr);

      if (visibleOrderedNeuronMapObjects == null)
	  cacheNeuronMapObjects();
      Iterator neuronstyles = visibleOrderedNeuronMapObjects.iterator();
      while (neuronstyles.hasNext())
	  ((NeuronMapObject) neuronMapObjects.get(((NeuronStyle) neuronstyles.next()).getURI())).paint(gr);
	      

      /*      Iterator mapObjects = neuronMapObjects.values().iterator();
      while(mapObjects.hasNext()) 
	((NeuronMapObject) mapObjects.next()).paint(gr);
      */

      gr.setClip(clip);
      gr.setTransform(f);
    }

  void colorUpdate()
    {
      Iterator mapObjects = neuronMapObjects.values().iterator();
      while(mapObjects.hasNext()) 
	((NeuronMapObject) mapObjects.next()).colorUpdate();
    }




  
  //////////// Event handling /////////////
  
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
    if(!listenersAdded)
      {
	mapDrawer.addMouseListener(mouseListener);
	mapDrawer.addMouseMotionListener(mouseListener);
	listenersAdded = true;
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
      if(clickListeners.size() == 0 &&
	 pressReleaseListeners.size() == 0 &&
	 moveDragListeners.size() == 0)
	{
	  mapDrawer.removeMouseListener(mouseListener);
	  mapDrawer.removeMouseMotionListener(mouseListener);
	  listenersAdded = false;
	}
    }

    boolean attractFocus = true;
    public void doAttractFocus(boolean bo)
    {
	attractFocus = bo;
    }

  void perhapsRequestFocus()
  {
      if(!hasFocus() && (SwingUtilities.findFocusOwner(this) == null) && attractFocus)
      	  requestFocus();
  }

  void resolveEvent(Vector listeners, MouseEvent e)
    {
      //      perhapsRequestFocus();

      if (listeners.size() != 0)
	{
	  MapEvent m = new MapEvent(e, (int) (e.getX()/scale),
				    (int) (e.getY()/scale), this);
	  resolveEvent(m);
	  dispatchEvent(listeners, m);
	}
    }  

  
  void resolveEvent(MapEvent m)
    {
      if (visibleAntiOrderedNeuronMapObjects == null)
	cacheNeuronMapObjects();      
	Iterator neuronstyles = visibleAntiOrderedNeuronMapObjects.iterator();
	while (neuronstyles.hasNext())
	    {
		NeuronMapObject nmo = (NeuronMapObject) neuronMapObjects.get(((NeuronStyle) neuronstyles.next()).getURI());
		if(nmo.checkAndFillHit(m))
		    return;
	    }

	/*
      for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	{
	  NeuronMapObject nmo = (NeuronMapObject) i.next();
	  
	  if(nmo.checkAndFillHit(m))
	    return;
	    }*/
    }

  void dispatchEvent(Vector listeners, MapEvent m)
    {
      for(int i = 0; i < listeners.size(); i++)
	{
	  MapEventListener c = (MapEventListener) listeners.elementAt(i);
	  c.eventTriggered(m);
	}
    }

  //////////// Layer and visibility handling ////////
  
  public void layerChange(LayerEvent le)
    {
	visibleOrderedNeuronMapObjects  = null;
	visibleAntiOrderedNeuronMapObjects = null;
    }

  protected void cacheNeuronMapObjects()
    {
	visibleOrderedNeuronMapObjects = manager.getConceptMap().getLayerManager().getNeuronStyles(MapGroupStyle.ONLY_VISIBLE);
	visibleAntiOrderedNeuronMapObjects = new Vector();

	Enumeration en = neuronMapObjects.elements();
	while (en.hasMoreElements())
	    ((NeuronMapObject) en.nextElement()).setVisible(false);




	en = visibleOrderedNeuronMapObjects.elements();
	while (en.hasMoreElements())
	    {
		NeuronStyle ns = (NeuronStyle) en.nextElement();
		NeuronMapObject nmo = (NeuronMapObject) neuronMapObjects.get(ns.getURI());		
		nmo.setVisible(true);
		visibleAntiOrderedNeuronMapObjects.insertElementAt(ns, 0);
	    }
    }


  //////////// ConceptMap Edit handling /////////////  
  
  public void componentEdited(EditEvent e)
    {
      if (e.getEditType() > ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT &&
	  e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_EDIT_CONSTANT)
	{
	  if (e.getEditedObject() instanceof NeuronStyle)
	    {
	      NeuronStyle ns = (NeuronStyle) e.getEditedObject();
	      NeuronMapObject nmo = (NeuronMapObject) neuronMapObjects.get(ns.getURI());
	      nmo.componentEdited(e);
	    }
	  else if (e.getEditedObject() instanceof AxonStyle)
	    {
	      AxonStyle as = (AxonStyle) e.getEditedObject();
	      NeuronMapObject nmo = (NeuronMapObject) neuronMapObjects.get(as.getOwner().getURI());
	      AxonMapObject amo = nmo.getAxonMapObject(as.getURI());
	      amo.componentEdited(e);
	    }
	  else
	    Tracer.bug("Some unknown sub-conceptmap editevent. Do not know how to handle!");
	}
      else if(e.getEditType() >= ConceptMap.FIRST_CONCEPTMAP_EDIT_CONSTANT &&
	      e.getEditType() <= ConceptMap.LAST_CONCEPTMAP_ONLY_EDIT_CONSTANT)
	{  
	  switch(e.getEditType())
	    {
	    case ConceptMap.DIMENSION_EDITED:
	      resizeMap();
	      break;
	    case ConceptMap.NEURONSTYLE_REMOVED:
	      removeNeuronStyle((String) e.getTarget());
	      break;
	    case ConceptMap.NEURONSTYLE_ADDED:
	      addNeuronStyle((String) e.getTarget());
	      break;
	    }
	}
      else if (e.getEditType() != se.kth.cid.component.Component.METADATA_EDITED &&
	       e.getEditType() != se.kth.cid.component.Component.SAVED)
	Tracer.bug("Some unknown non-conceptmap editevent. Do not know how to handle!");

      visibleOrderedNeuronMapObjects = null;      
      repaint();
      revalidate();
    }  


  void createMapObjects()
    {
      visibleOrderedNeuronMapObjects = null;
      neuronMapObjects = new Hashtable();    
      
      NeuronStyle[] styles = manager.getConceptMap().getNeuronStyles();
      
      for(int i = 0; i < styles.length; i++) 
	neuronMapObjects.put(styles[i].getURI(),
			     new NeuronMapObject(styles[i],
						 manager.getNeuron(styles[i].getURI()),
						 manager.getNeuronType(styles[i].getURI()),
						 this, mapComponentBack, mapDrawer));//mapComponentFront));
    }
  
  void addNeuronStyle(String id)
    {
      NeuronStyle style = manager.getConceptMap().getNeuronStyle(id);
      NeuronMapObject nmo= new NeuronMapObject(style,
						   manager.getNeuron(id),
						   manager.getNeuronType(id),
					       this, mapComponentBack, mapDrawer);//mapComponentFront);
      nmo.setScale(getScale());
      neuronMapObjects.put(id,nmo);
    }
  
  void removeNeuronStyle(String id)
    {
      NeuronMapObject nmo = (NeuronMapObject) neuronMapObjects.get(id);
      nmo.detach();
      neuronMapObjects.remove(id);
    }
  
  public void detach()
    {
      manager.getConceptMap().removeEditListener(this);
      manager.getConceptMap().getLayerManager().removeLayerListener(this);
      detachNeuronMapObjects();
      clickListeners        = null;
      pressReleaseListeners = null;
      moveDragListeners     = null;    
    }
  
  void detachNeuronMapObjects()
    {
	//      mapComponentFront.removeAll();
      mapComponentBack.removeAll();
      visibleOrderedNeuronMapObjects = null;
      if (neuronMapObjects != null)
	{
	  for (Iterator i = neuronMapObjects.values().iterator(); i.hasNext();)
	    ((NeuronMapObject) i.next()).detach();
	}
    }

  void resizeMap()
    {
      ConceptMap.Dimension dim = manager.getConceptMap().getDimension();

      setMinimumSize(new Dimension((int)(dim.width*scale), (int)(dim.height*scale)));
      setPreferredSize(new Dimension((int)(dim.width*scale), (int)(dim.height*scale)));
    }



  public static void setRenderingHints(Graphics g)
    {
      Graphics2D g2 = (Graphics2D) g;
      
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      			  RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_RENDERING,
			  RenderingHints.VALUE_RENDER_QUALITY);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            		  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      // Bug causes incorrect FontMetrics to be used in PlainView
      /*      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			  RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      */
    }
  
}
