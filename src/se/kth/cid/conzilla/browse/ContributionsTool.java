/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JScrollPane;

import se.kth.cid.conzilla.collaboration.ContainerEntries;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;
import se.kth.cid.conzilla.view.View;

/**
 * Toggles the visibility of the contributions pane.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ContributionsTool extends StateTool {
    
	MapController controller;
	
	JScrollPane scrollPane;
	
	ContainerEntries entries;
	
	public ContributionsTool(MapController controller) {
        super("CONTRIBUTIONS", BrowseMapManagerFactory.class.getName(), false);
        this.controller = controller;
        entries = controller.getContainerEntries();
        scrollPane = new JScrollPane(entries);
        setIcon(Images.getImageIcon(Images.ICON_CONTRIBUTIONS));
        setEnabled(entries.hasContributions());
        controller.addPropertyChangeListener(this);
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getPropertyName().equals(MapController.MAP_PROPERTY)) {
    		setEnabled(entries.hasContributions());
    	}
    	
    	if (isActivated() && evt.getPropertyName().equals(StateTool.ACTIVATED) && !evt.getPropertyName().equals(View.LEFT_PANE_PROPERTY)) {
        	ActionListener closeButtonListener = new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			setActivated(false);
        		}
        	};
        	controller.getView().addToLeft(scrollPane, "Contributions", closeButtonListener);
        	entries.activate();
        }
    	
    	if ((!isActivated() && (evt.getPropertyName().equals(View.LEFT_PANE_PROPERTY) || evt.getPropertyName().equals(StateTool.ACTIVATED))) || !isEnabled()) {
        	controller.getView().removeFromLeft(scrollPane);
        	entries.deactivate();
    	}
    }
    
}