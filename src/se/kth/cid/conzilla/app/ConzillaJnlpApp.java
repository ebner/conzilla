/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import se.kth.cid.conzilla.content.JnlpContentDisplayer;

public class ConzillaJnlpApp extends ConzillaApp {
	
	public ConzillaJnlpApp() {
	}

	protected void initDefaultContentDisplayer() {
		defaultContentDisplayer = new JnlpContentDisplayer();
	}

//	public static void main(String[] argv) {
//		ConzillaJnlpApp app = new ConzillaJnlpApp();
//		app.start(null, null);
//	}
	
}