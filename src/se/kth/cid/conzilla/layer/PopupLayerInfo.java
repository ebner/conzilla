/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JWindow;

import se.kth.cid.conzilla.layer.LayerControl.LayerEntry;
import se.kth.cid.conzilla.util.PopupWindowHandler;

/**
 * Extends PopupHandler so that descriptions are displayed in #JWindows that are
 * displayed freely ontop of the ContentSelector.
 * 
 * @author Matthias Palmer
 */
public class PopupLayerInfo extends PopupWindowHandler {
    public PopupLayerInfo(LayerControl layerControl) {
        super(new LayerEntry2JenaQueryTarget(), layerControl);
    }

    protected Object getComponentFromTrigger(Object o) {
        if (!(o instanceof MouseEvent))
            return null;
        MouseEvent m = (MouseEvent) o;
        if (m.getComponent() instanceof LayerEntry) {
        	return ((LayerEntry) m.getComponent());
        }
        
        return null;
    }

    protected Point getAdjustPosition(MouseEvent m, JWindow window) {
        return new Point(m.getComponent().getWidth()+4, m.getY());
	}

	protected Point getComponentPosition(Component comp) {
		return comp.getLocation();
	}
}