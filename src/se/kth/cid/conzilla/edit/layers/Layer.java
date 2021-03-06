/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.layers.handles.HandledObject;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapMouseInputListener;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.layout.ContextMap;

public abstract class Layer
    extends LayerComponent
    implements EditListener, MapMouseInputListener {
	
	Log log = LogFactory.getLog(Layer.class);
	
    protected MapDisplayer mapdisplayer;
    protected HandledObject handles;
    protected MapEvent mapevent;
    protected boolean focusonclick, pressed;
    protected GridModel gridModel;
    protected ContextMap.Position offset;

    public Layer(MapController controller, GridModel gridModel) {
        super(controller, true);
        if (!(controller.getManager() instanceof EditMapManager)) {
            log.warn("MapManager in controller isn't a EditMapManager despite the fact that we are in edit mode");
        }
        this.gridModel = gridModel;
        setHandledObject(null, MapEvent.Null);
        setVisible(true);
        setOpaque(false);
        setFocusOnClick(true);
        repaint();
    }

    public void activate(MapScrollPane pane) {
        this.mapdisplayer = pane.getDisplayer();
        // ContextMap.Dimension dim = mapdisplayer.getStoreManager().getConceptMap().getDimension();
        // setSize(new Dimension(dim.width, dim.height));

        pane.getDisplayer().getStoreManager().getConceptMap().addEditListener(
            this);
    }

    public void deactivate(MapScrollPane pane) {
        setHandledObject(null, MapEvent.Null);

        pane
            .getDisplayer()
            .getStoreManager()
            .getConceptMap()
            .removeEditListener(
            this);
        invalidate();
        mapdisplayer.repaint();
    }

    public boolean isFocusOnClick() {
        return focusonclick;
    }
    public void setFocusOnClick(boolean isso) {
        focusonclick = isso;
    }

    protected void setHandledObject(HandledObject handles, MapEvent m) {
        if (this.handles != null && this.handles != handles)
            this.handles.detach();
        this.handles = handles;
        mapevent = m;
    }
    
    public HandledObject getHandledObject() {
    	return this.handles;
    }

    public void mouseMoved(MapEvent m) {
        if (!focusonclick && !m.mouseEvent.isShiftDown()) {
            if (focus(m))
                repaint();
        }
    }

    public void mouseClicked(MapEvent m) {
        if (handles != null) {
            handles.click(m);
            mapdisplayer.repaint();
        }
    }

    protected abstract boolean focus(MapEvent m);

    public void mouseDragged(MapEvent m) {
        //this "if" prevents the drag behavior from entering other objects than the current edited.
        //    if(m.hit==MapEvent.HIT_NONE || m.getHitObject()==mapevent.getHitObject())
        if (handles != null && pressed) {
            if (gridModel.isGridOn()) {
                int grad = gridModel.getGranularity();
                int hgrad = grad / 2;

                if (m.mapX < 0) {
                	m.mapX = (m.mapX + offset.x - hgrad) / grad * grad - offset.x;
                } else  {
                	m.mapX = (m.mapX + offset.x + hgrad) / grad * grad - offset.x;                	
                }
                if (m.mapY < 0) {
                	m.mapY = (m.mapY + offset.y - hgrad) / grad * grad - offset.y;
                } else {
                	m.mapY = (m.mapY + offset.y + hgrad) / grad * grad - offset.y;
                }
            }
            repaintLayer(handles.drag(m));
        }
    }
    public void mousePressed(MapEvent m) {
        pressed = true;
        if (handles != null) {
            offset = handles.startDrag(m);
            mapdisplayer.repaint();
        }
        if (!m.isConsumed() && focusonclick) {
            HandledObject oldhandles = handles;
            if (focus(m))
                if (oldhandles != handles && handles != null)
                    offset = handles.startDrag(m);
            mapdisplayer.repaint();
        }

        if (offset == null)
            offset =
                new ContextMap.Position(
                    -m.mapX % gridModel.getGranularity(),
                    -m.mapY % gridModel.getGranularity());
    }

    public void mouseReleased(MapEvent m) {
        if (handles != null && pressed) {
            if (gridModel.isGridOn()) {
                m.mapX += offset.x - m.mapX % gridModel.getGranularity();
                m.mapY += offset.y - m.mapY % gridModel.getGranularity();
            }
            handles.stopDrag(m);
            offset = null;
            mapdisplayer.repaint();
        }
        pressed = false;
    }

    public void mouseEntered(MapEvent m) {}
    public void mouseExited(MapEvent m) {}

    public void layerPaint(Graphics2D g) {
        //      if (handles!=null)
        //	  handles.paint(g);
    }

    public void repaintLayer(Collection rectangles) {
    	AffineTransform transform = controller.getView().getMapScrollPane().getDisplayer().getTransform();
        if (rectangles == null)
            return;
        Iterator it = rectangles.iterator();
        for (; it.hasNext();) {
            Rectangle rect = (Rectangle) it.next();
            rect = new Rectangle(rect.x-2, rect.y-2, rect.width+4, rect.height+4);
            rect = transform.createTransformedShape(rect).getBounds();
            
            repaint(rect);
        }
    }
    public void componentEdited(EditEvent e) {}
}
