/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.EditEvent;
import se.kth.cid.component.EditListener;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.collaboration.PublishMapDialog;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;

/**
 * Publishing tool. Initiates the publishing process.
 * 
 * @author hannes
 * @version $Id$
 */
public class PublishTool extends Tool implements EditListener, PropertyChangeListener {
	
	MapController controller;

	EditMapManager mapManager;
	
	SaveTool saveTool;

	boolean saving = false;

	public PublishTool(EditMapManager emm, MapController controller, SaveTool saveTool) {
		super("PUBLISH", EditMapManagerFactory.class.getName());
		this.mapManager = emm;
		this.controller = controller;
		this.saveTool = saveTool;
		setEnabled(ConzillaKit.getDefaultKit().getConzillaEnvironment().isOnline());
		controller.getView().getMapScrollPane().getDisplayer().getStoreManager().addEditListener(this);
		ConfigurationManager.getConfiguration().addPropertyChangeListener(Settings.CONZILLA_ONLINESTATE, this);
		setIcon(Images.getImageIcon(Images.ICON_PUBLISH));
	}

	public void actionPerformed(ActionEvent e) {
		// We save the map first - is this dirty?
		//saveTool.actionPerformed(e);
		
		String sessionURI = controller.getConceptMap().getComponentManager().getEditingSesssion().getURI();
		if (sessionURI.startsWith("urn:path:/org/conzilla/local/") || sessionURI.startsWith("conzilla:/")) {
			JOptionPane.showMessageDialog(null,
					"This context-map cannot be published because it has beencreated\nwith a default session." + 
					"\nPlease create a new session with your own namespace instead!",
					"Context-Map cannot be published", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		CollaborillaSupport support = new CollaborillaSupport(ConfigurationManager.getConfiguration());
		if (!support.isProperlyConfigured()) {
			JOptionPane.showMessageDialog(null, "Please make sure your collaboration settings are configured properly.",
					"Check collaboration settings", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		PublishMapDialog pmp = new PublishMapDialog(controller, false);
		pmp.setVisible(true);
	}

	/**
	 * @see se.kth.cid.conzilla.tool.Tool#detach()
	 */
	public void detach() {
		controller.getView().getMapScrollPane().getDisplayer().getStoreManager().removeEditListener(this);
		ConfigurationManager.getConfiguration().removePropertyChangeListener(Settings.CONZILLA_ONLINESTATE, this);
		mapManager = null;
		controller = null;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent pce) {
		setEnabled(ConzillaKit.getDefaultKit().getConzillaEnvironment().isOnline());
	}

	/**
	 * @see se.kth.cid.component.EditListener#componentEdited(se.kth.cid.component.EditEvent)
	 */
	public void componentEdited(EditEvent e) {
	}

}