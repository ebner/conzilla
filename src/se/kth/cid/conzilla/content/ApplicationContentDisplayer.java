/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.ejalbert.BrowserLauncher;

public class ApplicationContentDisplayer extends BrowserContentDisplayer {
	
	Log log = LogFactory.getLog(ApplicationContentDisplayer.class);
	
	protected boolean showDocument(URL uri) throws ContentException {
		BrowserLauncher launcher;
		try {
			launcher = new BrowserLauncher();
			log.info("Trying to open URL using BrowserLauner2");
			launcher.openURLinBrowser(uri.toString());
		} catch (Exception be) {
			log.warn(be.getMessage());
			log.info("Trying to open URL using Runtime.exec() instead");
			try {
				String os = (String) System.getProperties().get("os.name");
				String[] command = null;
				if (os != null) {
					if (os.toLowerCase().matches(".*windows.*")) {
						command = new String[3];
						command[0] = "rundll32";
						command[1] = "url.dll,FileProtocolHandler";
						command[2] = uri.toString();
					} else if (os.toLowerCase().matches(".*mac.*")) {
						command = new String[2];
						command[0] = "open";
						command[1] = uri.toString();
					} else if (os.toLowerCase().matches(".*linux.*")) {
						Process p = Runtime.getRuntime().exec("mozilla -remote ping()");
						int ping = p.waitFor();
						if (ping == 0) {
							command = new String[3];
							command[0] = "mozilla";
							command[1] = "-remote";
							command[2] = "openURL(" + uri + ")";
						} else {
							command = new String[2];
							command[0] = "mozilla";
							command[1] = uri.toString();
						}
					}
				} else {
					// we tend to get "null" for os.name on newer mac systems,
					// so we try it the mac way here
					command = new String[2];
					command[0] = "open";
					command[1] = uri.toString();
				}
				if (command != null) {
					Runtime.getRuntime().exec(command);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return false;
			}
		}
		
		return true;
	}

}