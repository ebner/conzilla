/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.awt.Window;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import se.kth.cid.collaboration.CollaborillaConfiguration;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.content.ApplicationContentDisplayer;
import se.kth.cid.conzilla.install.Installer;
import se.kth.cid.conzilla.view.ViewManager;

public class ConzillaApp extends ConzillaAppEnv {
	
	static Log log = LogFactory.getLog(ConzillaApp.class);
	
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
			System.out.print("Usage:\n\n" +
					"Command Line\n" +
					"conzilla -open context-map.ccm\n" +
					"conzilla -open settings.ccs\n" +
					"\n" +
					"Java Web Start\n" +
					"javaws -open context-map.ccm http://conzilla.org/webstart/conzilla.jnlp\n" +
					"javaws -open settings.ccs http://conzilla.org/webstart/conzilla.jnlp");
			System.exit(-1);
		}
		
		// LOGGING ->
    	URL url = null;
		try {
			File logConfig = new File(Installer.getConzillaDir(), "log4j.properties");
			if (logConfig.exists()) {
				url = logConfig.toURL();
			}
		} catch (MalformedURLException e) {
			System.out.println("Unable to load logging configuration, using default values.");
		}
		if (url != null) {
			PropertyConfigurator.configure(url);
		}
		
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		// <- LOGGING

		ConzillaApp app = new ConzillaApp();
		app.arguments = argv;
		String associated = null;

		if (argv.length >= 1) {
			if (argv[0].toLowerCase().equals("-open") && argv.length >= 2) {
				associated = argv[1];
			} else if (argv[0].toLowerCase().equals("-print") && argv.length >= 2) {
				// not supported yet
			}
		}

		app.start();
		
		final ViewManager vm = ConzillaKit.getDefaultKit().getConzilla().getViewManager();
		final Window window = vm.getWindow();
		if ((window != null) && !window.isShowing()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					window.setVisible(true);
					vm.revalidate();
				}
			});
		}
		
		boolean askForConfiguration = true;
		CollaborillaConfiguration collabConfig = new CollaborillaConfiguration(ConfigurationManager.getConfiguration());
		
		if (associated != null) {
			// Conzilla Collaboration Settings (ccs)
			if (associated.toLowerCase().endsWith(".ccs")) {
				collabConfig.importCollaborationSettings(associated);
				askForConfiguration = false;
			// Conzilla Context-Map (ccm)
			} else if (associated.toLowerCase().endsWith(".ccm")) {
				app.loadContextMap(associated, false);
			} else {
				log.debug("Unknown file extension. Not doing anything.");
			}
		}

		if (askForConfiguration) {
			if (!collabConfig.isProperlyConfigured()) {
				collabConfig.askForConfiguration();
			}
		}
	}

}