/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import se.kth.cid.util.Tracer;

public class JnlpContentDisplayer extends BrowserContentDisplayer {
	
	BasicService basicService;

	public JnlpContentDisplayer() {
		try {
			basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
		} catch (UnavailableServiceException ue) {
			Tracer.error("Service unavailable: javax.jnlp.BasicService.");
		}
	}

	protected boolean showDocument(URL url) throws ContentException {
		return basicService.showDocument(url);
	}
	
}