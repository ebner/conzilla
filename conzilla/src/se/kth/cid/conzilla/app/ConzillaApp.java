/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import se.kth.cid.conzilla.content.ApplicationContentDisplayer;

public class ConzillaApp extends ConzillaAppEnv {
	
	public ConzillaApp() {
	}

	protected void initDefaultContentDisplayer() {
		defaultContentDisplayer = new ApplicationContentDisplayer();
	}
	
	public boolean hasLocalDiskAccess() {
		return true;
	}

	public static void main(String[] argv) {
		if (argv.length >= 3 || (argv.length == 1 && (argv[0].equals("-h") || argv[0].equals("?")))) {
			System.out.print("Usage: Conzilla [ConceptMap-URI] [Container-URI]\n");
			System.exit(-1);
		}

		ConzillaApp app = new ConzillaApp();

		String startmap = null;
		String container = null;
		if (argv.length >= 1) {
			startmap = argv[0];
			if (argv.length == 2) {
				container = argv[1];
			}
		}

		app.start(startmap, container, null);
	}

}
