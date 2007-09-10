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

import se.kth.cid.collaboration.CollaborillaConfiguration;
import se.kth.cid.collaboration.ContributionInformationDiskStore;
import se.kth.cid.collaboration.ContributionInformationStore;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.collaboration.PublishMapDialog;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;

/**
 * Queries and stores metadata which is needed later on during the publishing
 * process. This tool is supposed to be used when somebody wants to edit
 * metadata but doesn't want to publish yet.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ContributionInfoTool extends Tool implements PropertyChangeListener {
	
	MapController controller;

//	EditMapManager mapManager;
	
	PublishMapDialog pmp;

	public ContributionInfoTool(MapController controller) {
		super("CONTRIBINFO", EditMapManagerFactory.class.getName());
		this.controller = controller;
		setIcon(Images.getImageIcon(Images.ICON_EDIT_FORM));
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
		if (!collabConfig.isProperlyConfigured()) {
			JOptionPane.showMessageDialog(null, "Please make sure your collaboration settings are properly configured.",
					"Check collaboration settings", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		pmp = new PublishMapDialog(controller, true);
		pmp.setVisible(true);
		pmp.addPropertyChangeListener(PublishMapDialog.PROP_CLICK_OK, this);
	}

	/**
	 * @see se.kth.cid.conzilla.tool.Tool#detach()
	 */
	public void detach() {
		controller = null;
		if (pmp != null) {
			pmp.removePropertyChangeListener(PublishMapDialog.PROP_CLICK_OK, this);
			pmp = null;
		}
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PublishMapDialog.PROP_CLICK_OK)) {
			ContributionInformationStore infoStore = ContributionInformationDiskStore.getContributionInformationStore();
			if (pmp != null) {
				String contribURI = controller.getConceptMap().getComponentManager()
					.getEditingSesssion().getContainerURIForConcepts();
				String metaData = pmp.getContributionMetaData(); 
				infoStore.storeMetaData(contribURI, metaData);
				pmp.dispose();
			}
		}
	}

}