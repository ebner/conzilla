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
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
    	if (isActivated()) {
        	ActionListener closeButtonListener = new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			setActivated(false);
        		}
        	};
        	controller.getView().addToLeft(scrollPane, "Contributions", closeButtonListener);
        	entries.activate();
        } else {
        	controller.getView().removeFromLeft(scrollPane);
        	entries.deactivate();
        }
    }
    
}