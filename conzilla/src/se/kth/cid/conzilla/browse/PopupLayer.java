/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import se.kth.cid.component.ComponentException;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapEventListener;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.metadata.DescriptionPanel;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.util.PopupHandler;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.util.Tracer;

/** Extends PopupHandler so that descriptions are
 *  displayed inside a JPanel wich are in turn
 *  added ontop of the MapDisplayer.
 *  (In the MapScrollPane).
 *
 *  @author Matthias Palmer
 */
public class PopupLayer extends PopupHandler {
 //   static Border activeBorder;
 //   static Border inactiveBorder;
    static final Border TRANSLUCENT_BORDER =
        new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0, 0.9f));
    static final Color TRANSLUCENT_COLOR = new Color(0.9f, 0.9f, 1.0f, 0.7f);
    static final Color TRANSLUCENT_COLOR_ACTIVE = new Color(204, 204, 255, 200);

    boolean subMap;
    MapEvent mapPopupMapEvent;
    Timer timer;
    MapDisplayer activeSubMap;

    MapController controller;

    HashSet lastConcept;

    PropertyChangeListener colorListener;

    //  PropertyChangeListener zoomListener;

    /** Listens for moves in the map.
     */
    MapEventListener cursorListener;

    /** Listens for clicks in the map.
     */
    MapEventListener klickListener;

    JLayeredPane panel;

    //  AffineTransform    transform;

    PopupLayer(MapController controller) {
        super(new MapObject2JenaQueryTarget());
        
        this.controller = controller;

        //	transform = AffineTransform.getScaleInstance(1.0, 1.0);

        panel = new JLayeredPane() {
            public void print(Graphics g) {
                updateColors(false);
                super.print(g);
                updateColors(true);
            }
            /*	    public void paint(Graphics g)
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
            
            //      setRenderingHints(gr);
            
            super.paint(g);
            
            gr.setClip(clip);
            gr.setTransform(f);
            }*/
        };
        panel.setOpaque(false);
        panel.setLayout(null);

        cursorListener = new MapEventListener() {
            public void eventTriggered(MapEvent e) {
                    //Necessary otherwise the scrollbars have focus (in windows)
    if (e.mouseEvent.getID() == MouseEvent.MOUSE_ENTERED) {
                    //Tracer.debug("grabbing focus in PopupLayer.");
                    //PopupLayer.this.controller.getView().getMapScrollPane().getDisplayer().grabFocus();
                }

                updatePopups(e);
                refresh();
            }
        };

        klickListener = new MapEventListener() {
            public void eventTriggered(MapEvent e) {
                if (e.mapObject != null
                    && e.mapObject.getDrawerLayout() != null
                    && e.mapObject.getDrawerLayout().getDetailedMap() != null) {
                    if (e.mouseEvent.getClickCount() == 1
                        && !e.mouseEvent.isPopupTrigger()) {
                        mapPopupMapEvent = e;
                        timer.restart();
                    } else {
                        timer.stop();
                    }
                }
            }
        };

        colorListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateColors(true);
            }
        };

        /*	zoomListener=new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt)
        	{
        	    setScale(((Double) evt.getNewValue()).doubleValue(), ((Double) evt.getOldValue()).doubleValue());
        	}};
        */
        subMap = false;
        timer = new Timer(200, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (mapPopupMapEvent != null) {
                    removeDescription();
                    makeSubWin(mapPopupMapEvent);
                    refresh();
                    timer.stop();
                }
            }
        });

    }

    public void activate(MapScrollPane pane) {
        registerKeybordActions(pane.getDisplayer());
        ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, colorListener);
        pane.getLayeredPane().add(panel, MapScrollPane.EDIT_LAYER);
        pane.getDisplayer().addMapEventListener(cursorListener, MapDisplayer.MOVE_DRAG);
        pane.getDisplayer().addMapEventListener(klickListener, MapDisplayer.CLICK);
        updateColors(true);
        refresh();
    }
    
    public void deactivate(MapScrollPane pane) {
        unRegisterKeyboardActions(pane.getDisplayer());
        ConfigurationManager.getConfiguration().removePropertyChangeListener(ColorTheme.COLORTHEME, colorListener);

        pane.getLayeredPane().remove(panel);
        pane.getDisplayer().removeMapEventListener(
            cursorListener,
            MapDisplayer.MOVE_DRAG);
        pane.getDisplayer().removeMapEventListener(
            klickListener,
            MapDisplayer.CLICK);
        removeAllPopups();
        killSubWin();
    }

    public void refresh() {
        controller.getView().getMapScrollPane().repaint();
    }

    protected void setScaleImpl(double newscale, double oldscale) {
        Enumeration en = descriptions.elements();
        for (; en.hasMoreElements();)
             ((DescriptionPanel) en.nextElement()).setScale(newscale);

        //	transform = AffineTransform.getScaleInstance(newscale, newscale);
        refresh();
    }

    /** This function is the entry point for event handling for popups.
     */
    public void updatePopups(MapEvent e) {
        //Either should description be updated or map is shown. 
    	if (!subMap) {
			updateDescription(e);
		} else {
			// FIXME originalTrigger is used!!!!!!!!!!!!!!!
			if (e.hitType == MapEvent.HIT_NONE
					|| (originalTrigger != null && e.mapObject != ((MapEvent) originalTrigger).mapObject)) {
				killSubWin();
				updateDescription(e);
			} else
				moveSubWin(e);
		}
    }

    protected boolean isPopupTrigger(Object o) {
        MapEvent e = (MapEvent) o;
        return e.mouseEvent.getID() != MouseEvent.MOUSE_EXITED;
    }

    protected void activateOldDescriptionImpl(DescriptionPanel desc) {
        panel.moveToFront(desc);
        desc.setOpaque(true);
        //desc.setBorder(activeBorder);
        desc.repaint();
    }
    protected void inActivateOldDescriptionImpl(DescriptionPanel desc) {
        desc.setOpaque(false);
        //desc.setBorder(inactiveBorder);
        desc.repaint();
    }

    /** A description originates from either the map or a concept, 
     *  this function returns the belonging component.
     *
     *  @return a se.kth.cid.component.Resource
     */
    protected Object getComponentFromTrigger(Object o) {
        if ((!(o instanceof MapEvent)) || o == null)
            return null;
        MapEvent e = (MapEvent) o;
        if (e == null)
            return null;
        if (e.mapObject != null 
                && !InfoHighlighterTool.isInfoable(e.mapObject)) {
            return null;
        }
        if (e.hitType == MapEvent.HIT_NONE)
            return controller.getConceptMap();
        else if (e.mapObject != null)
            return e.mapObject;

        return null;
    }

    /** A description originates from either the map or a concept, 
     *  therefore when they are stored they need a unique lookupobject.
     *
     *  @return a object representing the description uniquely.
     */
    protected Object getDescriptionLookupObject(Object o) {
        if ((!(o instanceof MapEvent)) || o == null)
            return null;
        MapEvent e = (MapEvent) o;
        if (e.hitType == MapEvent.HIT_NONE)
            return controller.getConceptMap();
        else if (e.mapObject != null)
            return e.mapObject;

        return null;
    }

    protected void showNewDescriptionImpl(DescriptionPanel desc) {
        killSubWin();

        desc.setOpaque(true);
     //   desc.setBorder(activeBorder);

        panel.add(desc, JLayeredPane.DEFAULT_LAYER);
        panel.moveToFront(desc);
    }

    protected void removeDescriptionImpl(DescriptionPanel desc) {
        panel.remove(desc);
        killSubWin(); //works????
    }

    void makeSubWin(MapEvent e) {
        if (subMap == true)
            killSubWin();
        subMap = true;
        DrawerLayout overConcept = e.mapObject.getDrawerLayout();
        try {
        	ConzillaKit kit = ConzillaKit.getDefaultKit();
            se.kth.cid.component.Container lc = kit
                    .getResourceStore()
                    .getAndReferenceContainer(URI.create(
                        overConcept.getConceptMap().getLoadContainer()));
            MapStoreManager storeManager =
                new MapStoreManager(URI.create(overConcept.getDetailedMap()),
                    kit.getResourceStore(),
                    kit.getStyleManager(),
                    lc);

            activeSubMap = new MapDisplayer(storeManager);
            activeSubMap.setOpaque(true);
            activeSubMap.setBackground(ColorTheme.getTranslucentColor(ColorTheme.Colors.MAP_BACKGROUND));
            activeSubMap.setBorder(TRANSLUCENT_BORDER);
            //      activeSubMap.setGlobalMarkColor(Color.gray);
            activeSubMap.setScale(
                0.7* controller.getView().getMapScrollPane().getDisplayer().getScale());
            System.out.println(activeSubMap.getPreferredSize());
            System.out.println(activeSubMap.getSize());

            panel.add(activeSubMap, JLayeredPane.DEFAULT_LAYER);

            activeSubMap.revalidate();
            adjustPosition(activeSubMap, e);
            //FIXME originalTrigger is used!!!!!!!!!!!!!!!!
            originalTrigger = e;

        } catch (ComponentException ex) {
            subMap = false;
            Tracer.trace(
                "Could not load map "
                    + e.mapObject.getDrawerLayout().getDetailedMap()
                    + ":\n "
                    + ex.getMessage(),
                Tracer.WARNING);
        }
    }

    void killSubWin() {
        if (activeSubMap == null)
            return;

        panel.remove(activeSubMap);

        activeSubMap.getStoreManager().detach();
        activeSubMap.detach();
        activeSubMap = null;
        subMap = false;
    }

    void moveSubWin(MapEvent e) {
        if (activeSubMap == null)
            return;
        adjustPosition(activeSubMap, e);
    }

    protected void adjustPosition(JComponent comp, Object o) {
        Dimension prefSize = comp.getPreferredSize();
        Insets insets = comp.getInsets();
//        Border b = comp.getBorder();
        int prefWidth =
            prefSize.width
                + insets.left
                + insets.right
                + DescriptionPanel.xoffset;
        int prefHeight =
            prefSize.height
                + insets.bottom
                + insets.top
                + DescriptionPanel.yoffset;
        MapEvent m = (MapEvent) o;
        int x = m.mouseEvent.getX();
        int y = m.mouseEvent.getY();
        Rectangle rect =
            controller.getView().getMapScrollPane().getViewport().getViewRect();

        if ((x + prefWidth) > (rect.x + rect.width)) {
            int diff = (x + prefWidth) - (rect.x + rect.width);
            if ((x - diff) > rect.x)
                x -= diff;
            else
                x = rect.x;
        }
        if ((y + prefHeight) > (rect.y + rect.height)) {
            int diff = (y + prefHeight) - (rect.y + rect.height);
            if ((y - diff) > rect.y)
                y -= diff;
            else
                y = rect.y;
        }
        comp.setLocation((int) (x / scale), (int) (y / scale));
        comp.setSize(comp.getPreferredSize());
    }

    protected void updateColors(boolean allowTrans) {
//        Color active_back = GlobalConfig.getGlobalConfig().getColor(
//                BrowseMapManagerFactory.COLOR_POPUP_BACKGROUND_ACTIVE);
//        Color inactive_back = GlobalConfig.getGlobalConfig().getColor(
//                BrowseMapManagerFactory.COLOR_POPUP_BACKGROUND);
//        Color inactive_text = GlobalConfig.getGlobalConfig().getColor(
//                BrowseMapManagerFactory.COLOR_POPUP_TEXT);
//        Color active_text = GlobalConfig.getGlobalConfig().getColor(
//                BrowseMapManagerFactory.COLOR_POPUP_TEXT_ACTIVE);
//        Color inactive_border = GlobalConfig.getGlobalConfig().getColor(
//                BrowseMapManagerFactory.COLOR_POPUP_BORDER);
//        Color active_border = GlobalConfig.getGlobalConfig().getColor(
//                BrowseMapManagerFactory.COLOR_POPUP_BORDER_ACTIVE);
        
        Color active_back = ColorTheme.getColor(ColorTheme.Colors.INFORMATION);
        Color inactive_back = ColorTheme.getTranslucentColor(ColorTheme.Colors.INFORMATION);
//        Color inactive_text = ColorTheme.getColor(ColorTheme.Colors.FOREGROUND); // not used?
//        Color active_text = ColorTheme.getColor(ColorTheme.Colors.FOREGROUND); // not used?
        Color inactive_border = ColorTheme.getColor(ColorTheme.Colors.FOREGROUND);
        Color active_border = ColorTheme.getColor(ColorTheme.Colors.FOREGROUND);

        if (allowTrans) {
            inactive_back = ColorTheme.getTranslucentColor(inactive_back);
            active_back = ColorTheme.getTranslucentColor(active_back);
            inactive_border = ColorTheme.getTranslucentColor(inactive_border);
            active_border = ColorTheme.getTranslucentColor(active_border);
        }

    //    inactiveBorder = new MatteBorder(1, 1, 1, 1, inactive_border);
    //    activeBorder = new MatteBorder(1, 1, 1, 1, active_border);

        Enumeration en = descriptions.elements();
        DescriptionPanel desc;
        for (; en.hasMoreElements();) {
            desc = (DescriptionPanel) en.nextElement();
            if (desc != description) {
                desc.setBackground(inactive_back);
                desc.setOpaque(false);
    //            desc.setBorder(inactiveBorder);
            }
        }

        if (description != null) {
            description.setOpaque(true);
            description.setBackground(active_back);
     //       description.setBorder(activeBorder);
        }
    }

    public double getScale() {
        return controller.getView().getMapScrollPane().getDisplayer().getScale();
    }
}