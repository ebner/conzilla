/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;

import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.content.ContentException;
import se.kth.cid.conzilla.content.ContentSelector;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.Tracer;

public class SuckInTool extends Tool {
	private static final long serialVersionUID = 1L;

	PropertyChangeListener displayerListener;

	ContentSelector sel;

	MapController controller;

	Resource content;

	java.awt.Component dialogParent;

	public SuckInTool(MapController cont, java.awt.Component dialogParent) {
		super("CONTEXTUALIZE", BrowseMapManagerFactory.class.getName());
		setIcon(Images.getImageIcon(Images.ICON_CONTEXTUALIZE));
		this.controller = cont;
		this.sel = controller.getContentSelector();

		this.dialogParent = dialogParent;

		displayerListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				setContent();
			}
		};

		sel.addSelectionListener(ContentSelector.SELECTION, displayerListener);

		setContent();

	}

	void setContent() {
		Resource cont = sel.getSelectedContent();
		if (cont != null && cont instanceof ContextMap) {
			content = cont;
			setEnabled(true);
		} else {
			content = null;
			setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		Tracer.debug("SuckIn!");
		try {
			if (content != null) {
				ContextMap oldMap = controller.getView().getMapScrollPane().getDisplayer().getStoreManager().getConceptMap();

				controller.showMap(URI.create(content.getURI()));
				controller.getHistoryManager().fireOpenNewMapEvent(controller, oldMap,
						URI.create(content.getURI()));
				try {
					ConzillaKit.getDefaultKit().getContentDisplayer().setContent(null); // Closing
																						// ContentDisplayer.
				} catch (ContentException ce) {
					Tracer.bug("Couldn't close contentDisplayer");
				}
			}
		} catch (ControllerException ex) {
			ErrorMessage.showError("Load Error", "Failed to load map\n\n" + content.getURI(), ex, dialogParent);
		}
	}

	public void detach() {
		sel.removeSelectionListener(ContentSelector.SELECTION, displayerListener);

		sel = null;

		controller = null;

		content = null;

		dialogParent = null;
	}
}
