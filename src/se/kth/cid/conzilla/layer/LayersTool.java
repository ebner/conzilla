/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.layer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JScrollPane;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;

/**
 * Toggles the visibility of the Layers pane.
 * 
 * @author Matthias Palmer
 * @version $Id$
 */
public class LayersTool extends StateTool {
    
	MapController controller;
	
	LayerControl layerControl;
	
	JScrollPane pane;
    
    public LayersTool(MapController controller) {
        super("LAYER", LayerControl.class.getName(), false, true);
        this.controller = controller;
        layerControl = new LayerControl(controller);
        pane = new JScrollPane(layerControl);
        setIcon(Images.getImageIcon(Images.ICON_LAYERS));
        
    	boolean hasMultipleLayers = false;
    	if (layerControl != null && layerControl.lMan != null) {
    		if (layerControl.lMan.getLayers().size() > 1) {
    			hasMultipleLayers = true;
    		}
    	}
    	if (!hasMultipleLayers) {
    		controller.getView().removeFromLeft(pane);
    		layerControl.deactivate();
    	}
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
    	if (isActivated()) {
        	ActionListener closeButtonListener = new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			setActivated(false);
        			storeStatus();
        		}
        	};
        	layerControl.activate();
        	controller.getView().addToLeft(pane, "Layers", closeButtonListener);
        } else {
        	controller.getView().removeFromLeft(pane);
        	layerControl.deactivate();
        }
    }
    
}