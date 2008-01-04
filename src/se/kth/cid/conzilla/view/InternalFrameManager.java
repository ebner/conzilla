/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;

public class InternalFrameManager extends AbstractViewManager implements PropertyChangeListener {
	
    JFrame frame;
    
    JDesktopPane desktop;
    
    ConzillaSplitPane conzillaSplitPane;

    public InternalFrameManager() {
    }

    public String getID() {
        return "INTERNAL_FRAME_VIEW";
    }

    public void initManager() {
        super.initManager();
        frame = new JFrame("Conzilla");
        desktop = new JDesktopPane();
        conzillaSplitPane = new ConzillaSplitPane();
        frame.setContentPane(conzillaSplitPane);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        restoreSizeAndLocation(frame);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	ConzillaKit.getDefaultKit().getConzilla().exit(0);
            }
        });

        //frame.setVisible(true);
    }
    
    public void saveProperties() {
    	saveSizeAndLocation(frame);
    }

    public void detachManager() {
        super.detachManager();
        conzillaSplitPane.detach();
        conzillaSplitPane = null;
        desktop = null;
        frame.dispose();
        frame = null;
    }

    public View newView(MapController controller) {
        InternalFrameView fv = new InternalFrameView(this, controller);
        JInternalFrame frame = fv.getFrame();
        desktop.add(frame);
        addView(fv);
        controller.addPropertyChangeListener(this);
        frame.setSize(450, 300);
        frame.setLocation(0, 0);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setMaximizable(true);
        frame.setIconifiable(true);
        frame.moveToFront();
        activateInternalFrame(fv);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
        }
        return fv;
    }

    public void activateInternalFrame(InternalFrameView fv) {
        conzillaSplitPane.setPanes(fv.getLeftPanel(), desktop, fv.getRightPanel(), fv);
        conzillaSplitPane.setToolBar(fv.getToolsBar());
        conzillaSplitPane.setLocationField(fv.getLocationField());
        conzillaSplitPane.fixDividerLocation();
        frame.setJMenuBar(makeMenuBar(fv, false));
        frame.validate();
    }

    protected void closeView(View v, boolean closeController) {
        v.getController().removePropertyChangeListener(this);
        //  JMenuBar bar = (JMenuBar) bars.get(v);

        //bars.remove(v);
        //((InternalFrameView) v).close(closeController);
        if (closeController) {
        	v.getController().detach();
        } else {
        	v.detach();
        }
        
        JInternalFrame[] frames = desktop.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            if (!frames[i].isIcon()) {
                frames[i].toFront();
                try {
                    frames[i].setSelected(true);
                } catch (PropertyVetoException e) {
                }
                frame.validate();
                return;
            }
        }
        frame.setJMenuBar(new JMenuBar());
        frame.validate();
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(View.MENUS_PROPERTY)) {
//            MapController mc = (MapController) e.getSource();
//            InternalFrameView view = (InternalFrameView) getView(mc);
            JInternalFrame actview = desktop.getSelectedFrame();
            for (Iterator iter = views.iterator(); iter.hasNext();) {
				InternalFrameView view = (InternalFrameView) iter.next();
				if (view.getFrame() == actview) {
		            activateInternalFrame(view);		
				}
			}
        }
    }
    
    public int getViewCount() {
    	return views.size();
    }
    
    public void revalidate() {
    	super.revalidate();
    	conzillaSplitPane.fixDividerLocation();
    	desktop.revalidate();
    	desktop.repaint();
    	activateInternalFrame((InternalFrameView) conzillaSplitPane.getView());
    }
    
    public Window getWindow() {
    	return frame;
    }
    
}