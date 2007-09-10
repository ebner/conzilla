/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.net.URI;

import se.kth.cid.component.Resource;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.util.Tracer;

/**
 * Simple content displayer for showing a map in a new view.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class OpenMapContentDisplayer extends AbstractContentDisplayer {
	
	public OpenMapContentDisplayer() {
	}

	public void setContent(Resource c) throws ContentException {
		if (c == null) {
			return;
		}
		
		try {
			ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(URI.create(c.getURI()), null);
		} catch (ControllerException e) {
			Tracer.debug(e.getMessage());
		}
	}
	
}