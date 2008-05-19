/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.MouseInputAdapter;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.metadata.DescriptionPanel;
import se.kth.cid.conzilla.metadata.PopupTrigger2QueryTarget;
import se.kth.cid.conzilla.properties.ColorTheme;
import se.kth.cid.conzilla.properties.ColorTheme.Colors;

/**
 * Extends PopupHandler so that descriptions are displayed in #JWindows that are
 * displayed freely ontop of a another window.
 * 
 * @author Matthias Palmer
 */
public abstract class PopupWindowHandler extends PopupHandler {
    static Border activeBorder;

    static Border inactiveBorder;

    /**
     * Listens for moves in the map.
     */
    public MouseInputAdapter mouseListener;

    PropertyChangeListener colorListener;

    //  PropertyChangeListener zoomListener;
    ComponentListener componentListener;

    java.awt.Component component;

    Point oldComponentLocation;

    int lastIndex;

    Hashtable popups;

    //  AffineTransform transform;
    boolean odd = false;

	protected JComponent originator;

    public PopupWindowHandler(PopupTrigger2QueryTarget pt2qt, JComponent component) {
        super(pt2qt);
        this.originator = component;
        scale = 1.0;
        popups = new Hashtable();

        componentListener = new ComponentAdapter() {
            public void componentMoved(ComponentEvent m) {
                adjustAllPopups();
            }

            public void componentResized(ComponentEvent m) {
                adjustAllPopups();
            }
        };

        mouseListener = new MouseInputAdapter() {
            public void mouseMoved(MouseEvent e) {
                updateDescription(e);
            }

            public void mouseExited(MouseEvent e) {
                timer.stop();
            }

            public void mouseEntered(MouseEvent e) {
                //Tracer.debug("requesting focus from PopupContentInfo");
                originator.requestFocus();
            }
        };
        colorListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateColors();
            }
        };

        /*
         * zoomListener=new PropertyChangeListener() { public void
         * propertyChange(PropertyChangeEvent evt) { setScale(((Double)
         * evt.getNewValue()).doubleValue(), ((Double)
         * evt.getOldValue()).doubleValue()); }};
         */
    }

    public void activate() {
        component = originator.getTopLevelAncestor();
        if (component == null) {
        	component = originator;
        }

        originator.addMouseMotionListener(mouseListener);
        originator.addMouseListener(mouseListener);
        ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, colorListener);

        //	selector.getController().getZoomManager().addZoomListener(zoomListener);
        oldComponentLocation = getComponentPosition(component);

        component.addComponentListener(componentListener);
        registerKeybordActions(originator);
        updateColors();
    }
    
    public void deactivate() {
        if (component == null)
            return;
        ConfigurationManager.getConfiguration().removePropertyChangeListener(ColorTheme.COLORTHEME, colorListener);

        //	selector.getController().getZoomManager().removeZoomListener(zoomListener);
        originator.removeMouseListener(mouseListener);
        originator.removeMouseMotionListener(mouseListener);
        component.removeComponentListener(componentListener);
        unRegisterKeyboardActions(originator);
        removeAllPopups();
    }

    protected boolean isPopupTrigger(Object o) {
        MouseEvent e = (MouseEvent) o;
        return e.getID() != MouseEvent.MOUSE_EXITED;
    }

    protected Object getDescriptionLookupObject(Object o) {
        return getComponentFromTrigger(o);
    }

    protected void showNewDescriptionImpl(DescriptionPanel desc) {
        JWindow window = new JWindow();
        window.setFocusableWindowState(false);
        window.setAlwaysOnTop(true);
        JPanel pane = new JPanel(new GridLayout());
        /*
         * { public void paint(Graphics g) { Graphics2D gr = (Graphics2D) g;
         * AffineTransform f = gr.getTransform(); Tracer.debug("transform is
         * "+transform); gr.transform(transform); // setRenderingHints(gr);
         * 
         * super.paint(g);
         * 
         * gr.setTransform(f); } public Dimension getPreferredSize() { Dimension
         * dim = super.getPreferredSize(); return new Dimension((int)
         * ((dim.width)*scale), (int) ((dim.height)*scale)); } };
         */

        window.setContentPane(pane);

        pane.add(desc);

        desc.setOpaque(true);
//        desc.setBackground(ColorTheme.getLighterColor(ColorTheme.Colors.CONTENT));
//        desc.setForeground(ColorTheme.getColor(ColorTheme.Colors.FOREGROUND));
        desc.setBackground(ColorTheme.getBrighterColor(Colors.INFORMATION));
        desc.setForeground(ColorTheme.getColor(Colors.FOREGROUND));
//        desc.setBorder(activeBorder);

        popups.put(desc, window);
    }

    protected void removeDescriptionImpl(DescriptionPanel desc) {
        JWindow window = (JWindow) popups.get(desc);
        if (window != null) {
            popups.remove(desc);
            window.getContentPane().remove(desc);
            window.setVisible(false);
        }
    }

    protected void activateOldDescriptionImpl(DescriptionPanel desc) {
        JWindow window = (JWindow) popups.get(desc);
        if (window == null) {
            return;
        }
//        desc.setBackground(ColorTheme.getLighterColor(ColorTheme.Colors.CONTENT));
        desc.setBackground(ColorTheme.getBrighterColor(Colors.INFORMATION));
        desc.setForeground(ColorTheme.getColor(Colors.FOREGROUND));
        
//      desc.setBorder(activeBorder);
        window.setVisible(false);
        window.setVisible(true);
    }

    protected void inActivateOldDescriptionImpl(DescriptionPanel desc) {
        JWindow window = (JWindow) popups.get(desc);
        if (window == null) {
            return;
        }
        desc.setBackground(ColorTheme.getColor(Colors.INFORMATION));
        desc.setForeground(ColorTheme.getColor(Colors.FOREGROUND));
//        desc.setBorder(inactiveBorder);
        window.repaint();
    }

    protected void adjustPosition(JComponent comp, Object o) {
        if (!(o instanceof MouseEvent))
            return;
        MouseEvent m = (MouseEvent) o;

        JWindow window = (JWindow) popups.get(comp);
        if (window == null)
            return;

        window.pack();
        
        Point adjustpos = getAdjustPosition(m, window);
        SwingUtilities.convertPointToScreen(adjustpos, m.getComponent());
        
        window.setLocation(adjustpos.x, adjustpos.y);
        window.repaint();
        window.setVisible(true);
    }

    protected abstract Point getAdjustPosition(MouseEvent m, JWindow win);
    protected abstract Point getComponentPosition(Component comp);
     
    protected void adjustAllPopups() {
        Point newComponentLocation = getComponentPosition(component);
        int x = newComponentLocation.x - oldComponentLocation.x;
        int y = newComponentLocation.y - oldComponentLocation.y;

        Enumeration en = popups.elements();
        JWindow window;
        Point point;
        for (; en.hasMoreElements();) {
            window = (JWindow) en.nextElement();
            point = window.getLocation();
            point.translate(x, y);
            window.setLocation(point);
            window.toFront(); //Don't seem to work. Neccessary??
            window.repaint();
        }
        oldComponentLocation = newComponentLocation;
    }

    protected void updateColors() {
        Color inactive_back = ColorTheme.getTranslucentColor(Colors.INFORMATION);
        Color active_back = ColorTheme.getBrighterColor(Colors.INFORMATION);
        Color inactive_fore = ColorTheme.getColor(Colors.FOREGROUND);
        Color active_fore = ColorTheme.getColor(Colors.FOREGROUND);
        Color inactive_border = ColorTheme.getColor(Colors.FOREGROUND);
        Color active_border = ColorTheme.getColor(Colors.FOREGROUND);
        
        inactiveBorder = new MatteBorder(1, 1, 1, 1, inactive_border);
        activeBorder = new MatteBorder(1, 1, 1, 1, active_border);

        Enumeration en = descriptions.elements();
        DescriptionPanel desc;
        for (; en.hasMoreElements();) {
            desc = (DescriptionPanel) en.nextElement();
            desc.setBackground(inactive_back);
//            desc.setBorder(inactiveBorder);
            desc.setForeground(inactive_fore);
        }

        if (description != null) {
            description.setBackground(active_back);
            description.setForeground(active_fore);
  //          description.setBorder(activeBorder);
        }
    }

    protected void setScaleImpl(double newscale, double oldscale) {
        Enumeration en = descriptions.elements();
        for (; en.hasMoreElements();)
            ((DescriptionPanel) en.nextElement()).setScale(newscale);

        //	transform = AffineTransform.getScaleInstance(newscale, newscale);

        en = popups.elements();
        JWindow window;
        Point listpos = new Point(0, 0);
        SwingUtilities.convertPointToScreen(listpos, originator);

        for (; en.hasMoreElements();) {

            window = (JWindow) en.nextElement();

            double change = newscale / oldscale;
            int yorigo = (int) (listpos.y - 10 * oldscale); //if list haven't
                                                            // been resized yet.
//            int xorigo = listpos.x;
            int ny = yorigo + (int) (change * (window.getY() - yorigo));
            if (odd)
                window.setLocation(window.getX() + 1, ny);
            else
                window.setLocation(window.getX() - 1, ny);
            odd = !odd;

            window.pack();
        }
        refresh();
    }

    public void revalidate() {
        updateColors();
    }

    public void refresh() {
    }
}