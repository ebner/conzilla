/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.session.SessionManager;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;

/**
 * Tool for browsing sessions and loading its contained context-maps.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionBrowsingTool extends Tool implements PropertyChangeListener {
	
	private SessionManager sessionManager;
	
	private MapController controller;
	
	private SessionBrowser dialog;
	
	/**
	 * @param sessionManager
	 *            Conzilla's SessionManager.
	 * @param controller
	 *            The current view's MapController.
	 */
	public SessionBrowsingTool(SessionManager sessionManager, MapController controller) {
		super("SESSION_BROWSING", EditMapManagerFactory.class.getName());
		this.sessionManager = sessionManager;
		this.controller = controller;
		setIcon(Images.getImageIcon(Images.ICON_SESSIONS_BROWSE));
	}

	/**
	 * Shows the dialog to browse through the sessions.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (sessionManager.getSessions().size() == 0) {
			JOptionPane.showMessageDialog(null, "Please create a session first.", "No sessions found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		dialog = new SessionBrowser(sessionManager);
		dialog.addPropertyChangeListener(SessionBrowsingTool.this);
		dialog.showDialog();
	}
	
	/**
	 * Reacts on button clicks from within the dialog.
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getNewValue() != null) {
			Set maps = (Set) e.getNewValue();
			if (e.getPropertyName().equals(SessionBrowser.BUTTON_OPEN)) {
				if (maps.iterator().hasNext()) {
					openMap(((ContextMap)maps.iterator().next()).getURI(), false);
				}
			} else if (e.getPropertyName().equals(SessionBrowser.BUTTON_OPEN_NEW_VIEW)) {
				for (Iterator it = maps.iterator(); it.hasNext(); ) {
					openMap(((ContextMap) it.next()).getURI(), true);
				}
			}
		}
		dialog.removePropertyChangeListener(this);
		dialog.dispose();
	}
	
    /**
	 * Opens a context-map.
	 * 
	 * @param uriString
	 *            URI of the context-map.
	 * @param newView
	 *            Specifies whether the map is supposed to be opened in a new
	 *            view or in the currently displayed one.
	 */
    private void openMap(String uriString, boolean newView) {
    	if (uriString != null) {
    		URI uri = URI.create(uriString);
    		try {
    			if (newView) {
    				ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(uri, controller);
    			} else {
    				ConzillaKit.getDefaultKit().getConzilla().openMapInOldView(uri, controller.getView());
    			}
    		} catch (ControllerException e) {
    			ErrorMessage.showError("Unable to open map", "Conzilla was not able to open the context-map.", e, null);
    		}
    	}
    }

}