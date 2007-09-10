/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;
import se.kth.cid.conzilla.util.FillLayout;

public class MapScrollPane extends JScrollPane implements
        PropertyChangeListener, MapEventListener {
    public static final Integer BACKGROUND_LAYER = new Integer(0);

    public static final Integer MAP_LAYER = new Integer(20);

    public static final Integer EDIT_LAYER = new Integer(40);

    MapDisplayer displayer;

    MouseEvent origin;
    Rectangle originView;

    JPanel background;

    JLayeredPane layerPane;

    private boolean panningState;

    public MapScrollPane(MapDisplayer displayer) {
        this.displayer = displayer;
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.getHorizontalScrollBar().setUnitIncrement(15);
        this.getVerticalScrollBar().setUnitIncrement(15);

        background = new JPanel();

        background.setBackground(ColorTheme.getColor(ColorTheme.Colors.MAP_BACKGROUND));
        ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, this);

        layerPane = new JLayeredPane();
        layerPane.setLayout(new FillLayout());

        layerPane.add(background, BACKGROUND_LAYER);
        layerPane.add(displayer, MAP_LAYER);

        this.setViewportView(layerPane);

        revalidate();
    }

    public void setPanningState(boolean active) {
        if (panningState == active) {
            return;
        }
        if (active) {
            displayer.addMapEventListener(this, MapDisplayer.PRESS_RELEASE);
            displayer.addMapEventListener(this, MapDisplayer.MOVE_DRAG);
        } else {
            displayer.removeMapEventListener(this, MapDisplayer.MOVE_DRAG);
            displayer.removeMapEventListener(this, MapDisplayer.PRESS_RELEASE);
        }
        panningState = active;
    }
    
    public MapDisplayer getDisplayer() {
        return displayer;
    }

    public JLayeredPane getLayeredPane() {
        return layerPane;
    }

    public void setScale(double scale) {
        double factor = scale / displayer.getScale();
        JViewport port = getViewport();

        Dimension extent = port.getExtentSize();
        Point pos = port.getViewPosition();

        pos.x = Math.max(
                (int) ((pos.x + extent.width / 2) * factor - extent.width / 2),
                0);
        pos.y = Math
                .max(
                        (int) ((pos.y + extent.height / 2) * factor - extent.height / 2),
                        0);

        port.setViewPosition(pos);

        //MapDisplayer is resized here.
        displayer.setScale(scale);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        background.setBackground(ColorTheme.getColor(Colors.MAP_BACKGROUND));
    }

    public void detach() {
        ConfigurationManager.getConfiguration().removePropertyChangeListener(ColorTheme.COLORTHEME, this);
        setPanningState(false);
    }

    /**
     * @see se.kth.cid.conzilla.map.MapEventListener#eventTriggered(se.kth.cid.conzilla.map.MapEvent)
     */
    public void eventTriggered(MapEvent m) {
        if (m.mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
            origin = SwingUtilities.convertMouseEvent(displayer, m.mouseEvent, getViewport());
            originView = getViewport().getViewRect();
        } else if (m.mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
            positionMap(SwingUtilities.convertMouseEvent(displayer, m.mouseEvent, getViewport()));
        }
    }

    /**
     * 
     */
    private void positionMap(MouseEvent moveTo) {
    	if (moveTo == null) {
    		return;
    	}
        int dx = origin.getX() - moveTo.getX();
        int dy = origin.getY() - moveTo.getY();
        Dimension viewSize = getViewport().getViewSize();
        int x = originView.x + dx;
        int y = originView.y + dy;
        if (x < 0 || viewSize.width <= originView.width) {
            x = 0;
        } else if (x > viewSize.width - originView.width) {
            x = viewSize.width - originView.width;            
        }
        
        if (y < 0 || viewSize.height <= originView.height) {
            y = 0;
        } else if (y > viewSize.height - originView.height) {
            y = viewSize.height - originView.height;
        }

        getViewport().setViewPosition(new Point(x,y));
    }
}