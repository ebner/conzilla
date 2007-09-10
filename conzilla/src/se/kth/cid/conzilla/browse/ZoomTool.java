/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;

public class ZoomTool extends Tool {
	
	private static final long serialVersionUID = 1L;

	MapController controller;

	double scale;

	public ZoomTool(MapController cont, double scale) {
		super(scale > 1 ? "ZOOMIN" : "ZOOMOUT", MapController.class.getName());
		
		if (scale > 1) {
			setIcon(Images.getImageIcon(Images.ICON_ZOOM_IN));
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK));
		} else {
			setIcon(Images.getImageIcon(Images.ICON_ZOOM_OUT));
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK));
		}
			
		this.scale = scale;
		this.controller = cont;
	}

	public void actionPerformed(ActionEvent e) {
		controller.getView().zoomMap(scale);
	}

}
