/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JScrollPane;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.StateTool;

/**
 * Toggles the visibility of the contributions pane.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionsTool extends StateTool {
    
	MapController controller;
	
	SessionTree tree;
	
	JScrollPane pane;
    
    public SessionsTool(MapController controller) {
        super("SESSIONS", EditMapManagerFactory.class.getName(), false);
        this.controller = controller;
        tree = new SessionTree(controller, ConzillaKit.getDefaultKit().getSessionManager());
        pane = new JScrollPane(tree);
        setIcon(Images.getImageIcon(Images.ICON_SESSIONS_BROWSE));
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
        	tree.refresh();
        	tree.activateMetaDataInformation();
        	controller.getView().addToLeft(pane, "Sessions", closeButtonListener);
        } else {
        	controller.getView().removeFromLeft(pane);
        	tree.deactivateMetaDataInformation();
        }
    }
    
}