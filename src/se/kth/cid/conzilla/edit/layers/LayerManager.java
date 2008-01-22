/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapEventListener;
import se.kth.cid.conzilla.map.MapMouseInputListener;
import se.kth.cid.conzilla.map.MapScrollPane;

/**
 * Manages a stack of layers, the topmost receives all mapevents, all are
 * activated until poped. Install/uninstall functions inserts/removes layers in
 * the MapScrollPane on a depth depending on their position in the stack or if
 * they have a preffered depth (hasFixedLevel).
 * 
 * @author Matthias Palmer.
 */
public abstract class LayerManager implements MapEventListener {
	
	Log log = LogFactory.getLog(LayerManager.class);
	
    protected MapController controller;

    Stack layers;

    MapScrollPane pane;

    public LayerManager() {
    }

    public void install(MapController controller) {
        this.controller = controller;

        layers = new Stack();
    }

    public void push(LayerComponent layer) {
        layers.push(layer);
        if (pane != null)
            install(pane, layer);
    }

    public boolean pop(LayerComponent layer) {
        LayerComponent top = (LayerComponent) layers.peek();
        if (top != layer)
            return false;
        layers.pop();
        if (pane != null)
            deinstall(pane, top);
        return true;
    }

    /**
     * Installs a layer in the layeredpane and activates it, the layer have to
     * already reside on the stack.
     * 
     * @param pane
     *            the MapScrollPane to install on, null not allowed.
     * @param layer
     *            the LayerComponent to install.
     */
    protected void install(MapScrollPane pane, LayerComponent layer) {
        if (layer.hasFixedLevel())
            pane.getLayeredPane().add(layer, layer.getFixedLevelForLayer());
        else
            pane.getLayeredPane().add(
                    layer,
                    new Integer(MapScrollPane.EDIT_LAYER.intValue()
                            + layers.size() - layers.search(layer)));
        layer.activate(pane);
    }

    /**
     * Reverses the install function.
     * 
     * @param pane
     *            the MapScrollPane to install on, null not allowed.
     * @param layer
     *            the LayerComponent to deinstall.
     */
    protected void deinstall(MapScrollPane pane, LayerComponent layer) {
        int ind = pane.getLayeredPane().getIndexOf(layer);
        pane.getLayeredPane().remove(ind);
        layer.deactivate(pane);
    }

    public void install(MapScrollPane pane) {
        this.pane = pane;

        Iterator it = layers.iterator();
        while (it.hasNext())
            install(pane, (LayerComponent) it.next());

        MapDisplayer mapDisplayer = pane.getDisplayer();
        mapDisplayer.addMapEventListener(this, MapDisplayer.MOVE_DRAG);
        mapDisplayer.addMapEventListener(this, MapDisplayer.PRESS_RELEASE);
        mapDisplayer.addMapEventListener(this, MapDisplayer.CLICK);
    }

    public void uninstall(MapScrollPane pane) {
        if (this.pane != pane) {
            log.warn("Uninstalling a MapScrollPane in edit that's not installed. Maybe several uninstalls are triggered.");
        }
        Iterator it = layers.iterator();
        while (it.hasNext())
            deinstall(pane, (LayerComponent) it.next());

        MapDisplayer mapDisplayer = pane.getDisplayer();
        mapDisplayer.removeMapEventListener(this, MapDisplayer.MOVE_DRAG);
        mapDisplayer.removeMapEventListener(this, MapDisplayer.PRESS_RELEASE);
        mapDisplayer.removeMapEventListener(this, MapDisplayer.CLICK);
        this.pane = null;
    }

    public void eventTriggered(MapEvent m) {
        //	eventTriggeredImpl(m);

        if (layers.peek() instanceof MapMouseInputListener) {
            MapMouseInputListener mmil = (MapMouseInputListener) layers.peek();
            ((LayerComponent) mmil).popupMenu(m);
            if (m.isConsumed())
                return;

            switch (m.mouseEvent.getID()) {
            case MouseEvent.MOUSE_MOVED:
                mmil.mouseMoved(m);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                mmil.mouseDragged(m);
                break;
            case MouseEvent.MOUSE_PRESSED:
                mmil.mousePressed(m);
                break;
            case MouseEvent.MOUSE_RELEASED:
                mmil.mouseReleased(m);
                break;
            case MouseEvent.MOUSE_CLICKED:
                mmil.mouseClicked(m);
                break;
            case MouseEvent.MOUSE_ENTERED:
                mmil.mouseEntered(m);
                break;
            case MouseEvent.MOUSE_EXITED:
                mmil.mouseExited(m);
                break;
            }
        }
    }
}