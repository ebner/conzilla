/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.event.ActionEvent;

import se.kth.cid.conzilla.app.Conzilla;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.menu.DefaultMenuFactory;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.util.Tracer;

/**
 * Create a new window.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class NewWindowTool extends Tool {

	private MapController controller;
	
	public NewWindowTool(MapController controller) {
		super("NEW_WINDOW", DefaultMenuFactory.class.getName());
		this.controller = controller;
		setIcon(Images.getImageIcon(Images.ICON_NEW_WINDOW));
	}

	public void actionPerformed(ActionEvent ae) {
		Conzilla conzilla = ConzillaKit.getDefaultKit().getConzilla();
		
		Tracer.debug("Create a new window");
		try {
			conzilla.cloneView(conzilla.getViewManager().getView(controller));
		} catch (ControllerException e) {
			ErrorMessage.showError("Cannot load map", "Cannot load map", e, null);
		}
	}

}