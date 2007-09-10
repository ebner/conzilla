/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import se.kth.cid.component.Component;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.map.graphics.ConceptMapObject;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.map.graphics.TripleMapObject;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.GroupLayout;
import se.kth.cid.layout.LayerEvent;
import se.kth.cid.layout.LayerLayout;
import se.kth.cid.layout.LayerListener;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.util.TagManager;
import se.kth.cid.util.Tracer;

// In order of increasing color priority:
//
// Default: MapDisplayer.getForeground()// Error: Internal to each drawing obj.
// Mark: MapObject.getMark()
// GlobalMark: MapDisplayer.getGlobalMarkColor()
//

public class MapDisplayer extends JPanel implements EditListener,
        LayerListener, PropertyChangeListener {
    
	public static final int CLICK = 0;

    public static final int PRESS_RELEASE = 1;

    public static final int MOVE_DRAG = 2;

    MapStoreManager manager;

    List clickListeners;

    List pressReleaseListeners;

    List moveDragListeners;

    MouseInputListener mouseListener;

    Color globalMarkColor = null;

    boolean listenersAdded = false;

    boolean editable = false;

    double scale = 1.0;
    
    int inset = 5;
    
    Point offset = new Point(0,0);
    
    AffineTransform transform;

    Hashtable drawMapObjects;

    Vector visibleOrderedDrawMapObjects;

    Vector visibleAntiOrderedDrawMapObjects;

    public MapDisplayer(MapStoreManager manager) {
        this.manager = manager;

        setOpaque(false);
        setDoubleBuffered(true);

//        resizeMap();

        createMapObjects();

        clickListeners = Collections.synchronizedList(new ArrayList());
        pressReleaseListeners = Collections.synchronizedList(new ArrayList());
        moveDragListeners = Collections.synchronizedList(new ArrayList());

        mouseListener = new MouseInputListener() {
            public void mouseEntered(MouseEvent e) {
                perhapsRequestFocus(); //Should be enough to get focus for
                                       // popups, etc.
                dispatchEvent(moveDragListeners, new MapEvent(e, (int) (e
                        .getX() / scale), (int) (e.getY() / scale),
                        MapDisplayer.this));
            }

            public void mouseExited(MouseEvent e) {
                dispatchEvent(moveDragListeners, new MapEvent(e, (int) (e
                        .getX() / scale), (int) (e.getY() / scale),
                        MapDisplayer.this));
            }

            public void mouseClicked(MouseEvent e) {
                resolveEvent(clickListeners, e);
            }

            public void mousePressed(MouseEvent e) {
                resolveEvent(pressReleaseListeners, e);
            }

            public void mouseReleased(MouseEvent e) {
                resolveEvent(pressReleaseListeners, e);
            }

            public void mouseDragged(MouseEvent e) {
                resolveEvent(moveDragListeners, e);
            }

            public void mouseMoved(MouseEvent e) {
                resolveEvent(moveDragListeners, e);
            }
        };

        manager.getConceptMap().addEditListener(this);
        manager.getConceptMap().getLayerManager().addLayerListener(this);

        manager.getConceptMap().getComponentManager().getTagManager().addPropertyChangeListener(this);
    }

    public Color getGlobalMarkColor() {
        return globalMarkColor;
    }

    public void setGlobalMarkColor(Color c) {
        globalMarkColor = c;
        colorUpdate();
        repaint();
    }

    public void pushMark(Set set, Mark mark, Object o) {
    	if (drawMapObjects == null) {
    		return;
    	}
        for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
            MapObject nmo = (MapObject) i.next();
            Concept concept = nmo.getConcept();
            //	  Tracer.debug("checking if concept :"+concept.getURI());
            if (concept != null && set.contains(concept.getURI())) {
                nmo.pushMark(mark, o);
            }
        }
        //   repaint();
    }

    public void popMark(Set set, Object o) {
    	if (drawMapObjects == null) {
    		return;
    	}
        for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
            MapObject nmo = (MapObject) i.next();
            Concept concept = nmo.getConcept();
            if (concept != null && set.contains(concept.getURI()))
                nmo.popMark(o);
        }
        //      repaint();
    }

    public void setDisplayLanguageDiscrepancy(boolean b) {
        for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
            MapObject nmo = (MapObject) i.next();
            nmo.setDisplayLanguageDiscrepancy(b);
        }
        //      repaint();
    }

    public MapStoreManager getStoreManager() {
        return manager;
    }

    public MapObject getMapObject(String conceptLayoutID) {
        return (MapObject) drawMapObjects.get(conceptLayoutID);
    }

    public Collection getMapObjects() {
        return drawMapObjects.values();
    }

    public void reset() {
        for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
            MapObject nmo = (MapObject) i.next();
            nmo.setEditable(false, null);
            nmo.clearMark();
        }
    }

    public void setScale(double scale) {
        this.scale = scale;
        transform = null;
        resizeMap();

        for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
            MapObject nmo = (MapObject) i.next();
            nmo.setScale(scale);
        }
        revalidate();
        repaint();
    }
    
    public AffineTransform getTransform() {
    	if (transform == null) {
            Point pos = bb.getLocation();
            transform = new AffineTransform();
            transform.translate(offset.x + ((int) (-pos.x*scale))+inset,offset.y+((int)(-pos.y*scale))+inset);
            transform.scale(scale, scale);
    	}
    	
    	return transform;
    }

    public double getScale() {
        return scale;
    }

    //////////// Painting /////////////

    public void repaintMap(Collection rectangles) {
        Iterator it = rectangles.iterator();
        for (; it.hasNext();) {
            Rectangle rect = (Rectangle) it.next();
            rect = getTransform().createTransformedShape(rect).getBounds();
            repaint(rect);
        }
    }

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D gr = (Graphics2D) g.create();

        Shape clip = gr.getClip();
        //Make sure the transform is up to date.
        getTransform();

        gr.transform(transform);
        try {
            gr.setClip(transform.createInverse().createTransformedShape(clip));
        } catch (NoninvertibleTransformException e) {
            Tracer.error("Non-invertible transform: " + transform + ":\n "
                    + e.getMessage());
        }

        setRenderingHints(gr);
    	
        if (visibleOrderedDrawMapObjects == null)
            cacheDrawMapObjects();
        Iterator drawlayouts = visibleOrderedDrawMapObjects.iterator();
        while (drawlayouts.hasNext()) {
            MapObject mapObject = ((MapObject) drawMapObjects.get(((DrawerLayout) drawlayouts.next()).getURI()));
            if (mapObject != null) {
            	mapObject.paint(gr);
            }
        }

        /*
         * Iterator mapObjects = drawMapObjects.values().iterator();
         * while(mapObjects.hasNext()) ((MapObject)
         * mapObjects.next()).paint(gr);
         */

//        gr.setClip(clip);
//        gr.transform(f);
    }

    void colorUpdate() {
        Iterator mapObjects = drawMapObjects.values().iterator();
        while (mapObjects.hasNext())
            ((MapObject) mapObjects.next()).colorUpdate();
    }

    //////////// Event handling /////////////

    public void addMapEventListener(MapEventListener c, int listenerTo) {
        switch (listenerTo) {
        case CLICK:
            clickListeners.add(c);
            break;
        case PRESS_RELEASE:
            pressReleaseListeners.add(c);
            break;
        case MOVE_DRAG:
            moveDragListeners.add(c);
            break;
        }
        if (!listenersAdded) {
            addMouseListener(mouseListener);
            addMouseMotionListener(mouseListener);
            listenersAdded = true;
        }

    }

    public void removeMapEventListener(MapEventListener c, int listenerTo) {
        switch (listenerTo) {
        case CLICK:
            clickListeners.remove(c);
            break;
        case PRESS_RELEASE:
            pressReleaseListeners.remove(c);
            break;
        case MOVE_DRAG:
            moveDragListeners.remove(c);
            break;
        }
        if (clickListeners.size() == 0 && pressReleaseListeners.size() == 0
                && moveDragListeners.size() == 0) {
            removeMouseListener(mouseListener);
            removeMouseMotionListener(mouseListener);
            listenersAdded = false;
        }
    }

    boolean attractFocus = true;

	private Rectangle bb = new Rectangle();

    public void doAttractFocus(boolean bo) {
        attractFocus = bo;
    }

    void perhapsRequestFocus() {
        java.awt.Component toplevelFocusHolder = SwingUtilities.findFocusOwner(this.getTopLevelAncestor());
        java.awt.Component branchFocusHolder = SwingUtilities.findFocusOwner(this);
        if (!hasFocus() && attractFocus) {
            if (toplevelFocusHolder != null && branchFocusHolder != toplevelFocusHolder) {
                requestFocus();
            }
        }
    }

    void resolveEvent(List listeners, MouseEvent e) {
        //      perhapsRequestFocus();

        if (listeners.size() != 0) {
            MapEvent m = new MapEvent(e, (int) ((e.getX() - offset.x - inset) / scale) +bb.x, (int) (
            		(e.getY() -offset.y - inset)/ scale) +bb.y, this);
            resolveEvent(m);
            dispatchEvent(listeners, m);
        }
    }

    void resolveEvent(MapEvent m) {
        if (visibleAntiOrderedDrawMapObjects == null) {
            cacheDrawMapObjects();
        }
        Iterator conceptlayouts = visibleAntiOrderedDrawMapObjects.iterator();
        if (conceptlayouts != null) {
        	while (conceptlayouts.hasNext()) {
        		MapObject nmo = (MapObject) drawMapObjects.get(((ResourceLayout) conceptlayouts.next()).getURI());
        		if (nmo != null && nmo.checkAndFillHit(m)) {
        			return;
        		}
        	}
        }

        /*
         * for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
         * MapObject nmo = (MapObject) i.next();
         * 
         * if(nmo.checkAndFillHit(m)) return; }
         */
    }

    void dispatchEvent(List listeners, MapEvent m) {
    	for (Iterator mels = listeners.iterator(); mels.hasNext();) {
    		if (m.isConsumed()) {
    			break;
    		}
			MapEventListener mel = (MapEventListener) mels.next();
			mel.eventTriggered(m);
		}
    }

    //////////// Layer and visibility handling ////////

    public void layerChange(LayerEvent le) {
        //Tracer.debug("layerChange in MapDisplayer");
        visibleOrderedDrawMapObjects = null;
        visibleAntiOrderedDrawMapObjects = null;
        resizeMap();
    }

    public void propertyChange(PropertyChangeEvent pc) {
        if (!pc.getPropertyName().equals(TagManager.TAG_VISIBILITY_CHANGED))
            return;
//        Collection rc = manager.getConceptMap().getRelevantContainers();
        //TODO: relevant containers for map does not reflect the subsequent
        // layers relevantcontainers....
        //if (!rc.contains(((se.kth.cid.component.Resource)
        // pc.getSource()).getURI()))
        //	return;
        Tracer.debug("Visibility of containers changed, mapdisplayer notified");
        visibleOrderedDrawMapObjects = null;
        visibleAntiOrderedDrawMapObjects = null;
        resizeMap();
        repaint();
    }

    protected void cacheDrawMapObjects() {
    	visibleOrderedDrawMapObjects = manager.getConceptMap().getLayerManager().getDrawerLayouts(GroupLayout.ONLY_VISIBLE);
        visibleAntiOrderedDrawMapObjects = new Vector();

        Enumeration en = drawMapObjects.elements();
        while (en.hasMoreElements()){
        	((MapObject) en.nextElement()).setVisible(false);
        }

        en = visibleOrderedDrawMapObjects.elements();
        while (en.hasMoreElements()) {
            DrawerLayout ns = (DrawerLayout) en.nextElement();
            MapObject nmo = (MapObject) drawMapObjects.get(ns.getURI());
            if (nmo != null) {
            	nmo.setVisible(true);
            }
            visibleAntiOrderedDrawMapObjects.insertElementAt(ns, 0);
        }
//        Tracer.debug("cacheDrawMapObjects in MapDisplayer size = "
//                + visibleAntiOrderedDrawMapObjects.size());
    }

    //////////// ConceptMap Edit handling /////////////

    public void componentEdited(EditEvent e) {
        if (e.getEditType() > ContextMap.LAST_CONTEXTMAP_ONLY_EDIT_CONSTANT
                && e.getEditType() <= ContextMap.LAST_CONTEXTMAP_EDIT_CONSTANT) {
            if (e.getEditedObject() instanceof ResourceLayout) {
                ResourceLayout os = (ResourceLayout) e.getEditedObject();
                MapObject mo = (MapObject) drawMapObjects.get(os.getURI());
                if (mo != null) {
                	mo.componentEdited(e);
                }
            } else {
                Tracer.bug("Some unknown sub.layout editevent. Do not know how to handle!");
            }
        } else if (e.getEditType() >= ContextMap.FIRST_CONTEXTMAP_EDIT_CONSTANT
                && e.getEditType() <= ContextMap.LAST_CONTEXTMAP_ONLY_EDIT_CONSTANT) {
            switch (e.getEditType()) {
            case ContextMap.DIMENSION_EDITED:
                resizeMap();
                break;
            case ContextMap.RESOURCELAYOUT_REMOVED:
                removeResourceLayout((String) e.getTarget());
                break;
            case ContextMap.RESOURCELAYOUT_ADDED:
                //FIXME: this is more complicated now...
                addDrawerLayout((String) e.getTarget());
                break;
            case ContextMap.CONTEXTMAP_REFRESHED:
            	detachDrawMapObjects();
            	createMapObjects();
                break;
            }
        } else if (e.getEditType() != se.kth.cid.component.Resource.METADATA_EDITED
                && e.getEditType() != Component.ATTRIBUTES_EDITED
                && e.getEditType() != se.kth.cid.component.Resource.SAVED
                && e.getEditType() != se.kth.cid.component.Resource.EDITED)
            Tracer.bug("Some unknown non-layout editevent. Do not know how to handle!");

        visibleOrderedDrawMapObjects = null;
        resizeMap();
        revalidate();
        repaint();
    }

    public void createMapObjects() {
        visibleOrderedDrawMapObjects = null;
        drawMapObjects = new Hashtable();

        //      DrawerLayout[] layouts = manager.getConceptMap().getDrawerLayouts();
        LayerManager lman = manager.getConceptMap().getLayerManager();
        Enumeration en = lman.getLayers().elements();
        while (en.hasMoreElements()) {
            LayerLayout ls = (LayerLayout) en.nextElement();
            if (!lman.getLayerVisible(ls.getURI()))
                continue;
            if (!ls.isLeaf())
                addResourceLayouts(ls);
        }

        //      for(int i = 0; i < layouts.length; i++)
        //	  addResourceLayout(layouts[i]);
    }

    void addResourceLayouts(ResourceLayout mgs) {
        Enumeration en = mgs.getChildren().elements();
        while (en.hasMoreElements()) {
            ResourceLayout os = (ResourceLayout) en.nextElement();
            if (mgs.getChildHidden(os.getURI()))
                continue;
            if (os instanceof DrawerLayout)
                addDrawerLayout((DrawerLayout) os);
            else if (!os.isLeaf())
                addResourceLayouts(os);
        }
    }

    void addDrawerLayout(String id) {

        ResourceLayout layout = manager.getConceptMap().getResourceLayout(id);
        if (layout instanceof DrawerLayout)
            addDrawerLayout((DrawerLayout) layout);
    }

    void addDrawerLayout(DrawerLayout layout) {
        MapObject mo = null;
        if (layout instanceof ConceptLayout)
            mo = new ConceptMapObject((ConceptLayout) layout, this);
        else if (layout instanceof StatementLayout)
            mo = new TripleMapObject((StatementLayout) layout, this);
        if (mo != null) {
            mo.setScale(getScale());
            drawMapObjects.put(layout.getURI(), mo);
        }
    }

    void removeResourceLayout(String id) {
        MapObject nmo = (MapObject) drawMapObjects.get(id);
        nmo.detach();
        drawMapObjects.remove(id);
    }

    public void detach() {
        manager.getConceptMap().removeEditListener(this);
        manager.getConceptMap().getLayerManager().removeLayerListener(this);
        manager.getConceptMap().getComponentManager().getTagManager().removePropertyChangeListener(this);
        detachDrawMapObjects();
        
        //We do not throw away the listener lists since some listener want to detach themselves late,
        //the alternative is to check in the removeMapEventListener code if the listeners are null or not...
        //clickListeners = null;
        //pressReleaseListeners = null;
        //moveDragListeners = null;
    }

    void detachDrawMapObjects() {
        //      mapComponentFront.removeAll();
        //mapComponentBack.removeAll();
        visibleOrderedDrawMapObjects = null;
        if (drawMapObjects != null) {
            for (Iterator i = drawMapObjects.values().iterator(); i.hasNext();) {
                ((MapObject) i.next()).detach();
            }
        }
    }

    public void resizeMap() {
    	transform = null;
        bb = manager.getConceptMap().getBoundingBox();
        if (bb==null) {
        	bb = new Rectangle();
        }
        Dimension dim = bb.getSize();
        dim = new Dimension((int) ((dim.width+inset*2) * scale),
                (int) ((dim.height+inset*2) * scale));
        setMinimumSize(dim);
        setPreferredSize(dim);
        revalidate();
    }

    public void setOffset(Point os) {
    	this.offset = os;
    	transform = null;
    }
    
    public Point getOriginLocation() {
    	return new Point(offset.x + inset + (int) (-bb.x* scale), offset.y + inset + (int) (-bb.y* scale));
    }
    
	public static void setRenderingHints(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Bug causes incorrect FontMetrics to be used in PlainView
        /*
         * g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
         * RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         */
    }

}