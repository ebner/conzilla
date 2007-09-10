/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JFrame;

import se.kth.cid.conzilla.controller.MapController;

public class FrameManager extends AbstractViewManager implements PropertyChangeListener {
	
    public FrameManager() {
    }

    public String getID() {
        return "FRAME_VIEW";
    }

    public View newView(MapController controller) {
        FrameView fv = new FrameView(this, controller);
        addView(fv);

        controller.addPropertyChangeListener(this);

        //FIXME: ad hoc, right place to do it?
        //	fv.setLocation(100, 100);
        //	fv.setSize(100, 100);
        return fv;
    }

    protected void closeView(View v, boolean closeController) {
        v.getController().removePropertyChangeListener(this);
        if (closeController) {
        	v.getController().detach();
        } else {
        	v.detach();
        }
        //((FrameView) v).close(closeController);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(View.MENUS_PROPERTY)) {
            MapController mc = (MapController) e.getSource();

            FrameView fv = (FrameView) getView(mc);
            JFrame frame = fv.getFrame();
            frame.setJMenuBar(null);
            frame.setJMenuBar(makeMenuBar(fv, false));
            frame.validate();
        }
    }
    
    public int getViewCount() {
    	return views.size();
    }

	public void saveProperties() {
	}
	
	public void revalidate() {
		Iterator it = views.iterator();
		while (it.hasNext()) {
			FrameView view = (FrameView) it.next();
			view.getFrame().getRootPane().revalidate();
			view.conzillaSplitPane.fixDividerLocation();
		}
	}
	
	public Window getWindow() {
		return null;
	}
    
}