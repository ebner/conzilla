/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;

/**
 * Reload the currently loaded context-map.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class ReloadTool extends Tool {
	
//	private MapController controller;
	
	public ReloadTool(MapController controller) {
		super("RELOAD", BrowseMapManagerFactory.class.getName());
//		this.controller = controller;
		setIcon(Images.getImageIcon(Images.ICON_REFRESH));
	}

	public void actionPerformed(ActionEvent e) {
		ConzillaKit.getDefaultKit().getConzilla().reload();
	}

}