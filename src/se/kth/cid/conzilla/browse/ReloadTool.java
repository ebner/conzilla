/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;

/**
 * Reload the currently loaded context-map.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ReloadTool extends Tool {
	
	private MapController controller;
	
	public ReloadTool(MapController controller) {
		super("RELOAD", BrowseMapManagerFactory.class.getName());
		this.controller = controller;
		setIcon(Images.getImageIcon(Images.ICON_REFRESH));
	}

	public void actionPerformed(ActionEvent e) {
		controller.firePropertyChange(MapController.MAP_LOADING, null, null);
		
		Thread reloadThread = new Thread(new Runnable() {
			public void run() {
				MapScrollPane oldPane = controller.getView().getMapScrollPane();
				
				// reload() on Conzilla might not be necessary, it would be enough to just reload the
				// map's containers. We call it anyway, just to make sure not to miss anything. Might be slow.
				ConzillaKit.getDefaultKit().getConzilla().reload();
				
				controller.getConceptMap().refresh();
				try {
					controller.refresh();
				} catch (ControllerException e1) {
					controller.firePropertyChange(MapController.MAP_LOADING_FAILED, null, null);
				}
				controller.getContainerEntries().update();
				controller.firePropertyChange(MapController.MAP_PROPERTY, oldPane, controller.getView().getMapScrollPane());				
			}
		});
		reloadThread.start();
	}

}