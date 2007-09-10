/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import javax.swing.JApplet;

import se.kth.cid.conzilla.content.AppletContentDisplayer;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.TabManager;
import se.kth.cid.util.Tracer;

public class ConzillaApplet extends JApplet {
	private static final long serialVersionUID = 1L;
	ConzillaAppletEnv conzillaEnv;

	public class ConzillaAppletEnv extends ConzillaAppEnv {
		public ConzillaAppletEnv() {
		}

		protected void initDefaultContentDisplayer() {
			defaultContentDisplayer = new AppletContentDisplayer(getApplet().getAppletContext(),
					getParameter("TARGETWINDOW"));
		}

		public JApplet getApplet() {
			return ConzillaApplet.this;
		}
		
		public boolean isOnline() {
			return true;
		}
		
		public void setOnline(boolean state) {
		}
		
		public boolean toggleOnlineState() {
			return true;
		}
		
		public boolean hasLocalDiskAccess() {
			// FIXME
			// should read resolvertable and check whether /org/conzilla/local
			// points to an http/ftp or a file location
			return false;
		}

		public void exit(int result) {
//			try {
//				Installer.saveConfig();
//			} catch (IOException e) {
//				Tracer.trace("IO Error saving config: " + e.getMessage(), Tracer.WARNING);
//			}
			ConzillaApplet.this.exit();
		}

		protected void start(String parammap, String paramcontainer, URI specifiedConfigURI) {
			this.parammap = parammap;
			this.paramcontainer = paramcontainer;
			try {
				tryInit(specifiedConfigURI);
				return;
			} catch (InstallException e) {
				ErrorMessage.showError("Fatal initialization error.", "Configuration file cannot be found at:\n"
						+ specifiedConfigURI.toString() + "\n", e, null);
			} catch (IOException e) {
				ErrorMessage.showError("Fatal initialization error.", "Problem loading conzilla.\n", e, null);
			} catch (URISyntaxException e) {
				ErrorMessage.showError("Fatal initialization error.", "Invalid URI\n\n" + e
						+ "\n\nin config file.", e, null);
			}
			throw new RuntimeException("Cannot start Conzilla.");
		}
	}

	public ConzillaApplet() {
	}

	public void start() {
		URI configURL = null;

		// Where to find the property file.
		// --------------------------------
		String pf = getParameter("PROPERTYFILE");

		if (pf == null) // No location specified
			pf = "conzilla.properties"; // Hence we guess there is a
		// conzilla.properties in the same dir.

		if (pf.indexOf(":/") == -1) // Realtive location specified.
			try {
				String url = (new java.net.URL(getDocumentBase(), pf)).toString();
				configURL = new URI(url);

			} catch (java.net.MalformedURLException me) {
				Tracer.debug("PropertyFile isn't on the specified location...");
			} catch (URISyntaxException me) {
				Tracer.bug("conzilla.properties isn't on the specified location...");
			}
		else
			// Absolute location specified
			try {
				configURL = new URI(pf);
			} catch (URISyntaxException me) {
				Tracer.bug("conzilla.properties isn't on the specified location...");
			}
//		GlobalConfig config = GlobalConfig.getGlobalConfig();
//		config.setBaseURI(URIUtil.getParentDirectory(configURL.toString()));
//		Config config = ConfigurationManager.getConfiguration();
		Tracer.error("FIXME: configuration loading mechanism not implemented in ConzillaApplet");

		// Fetch the map to start initially.
		// --------------------------------
		String paramMap = getParameter("STARTMAP");

		// Fetch the container url
		String containers = getParameter("CONTAINER");
		if (containers != null) {
			StringBuffer buf = new StringBuffer();
			StringTokenizer st = new StringTokenizer(containers, ",");
			while (st.hasMoreTokens()) {
				String c = st.nextToken();
				if (c.indexOf(":/") == -1) {
					try {
						buf.append((new java.net.URL(getDocumentBase(), containers)).toString());
					} catch (MalformedURLException e) {
						containers = null;
					}
				} else {
					buf.append(c);
				}
				buf.append(",");
			}
			containers = buf.substring(0, buf.length());
		}

		// Initialize Conzillas environment.
		// ---------------------------------
		conzillaEnv = new ConzillaAppletEnv();
		try {
			conzillaEnv.start(paramMap, containers, configURL);
		} catch (RuntimeException e) {
			exit();
			return;
		}

		// Fetches the displayarea for conzilla and sets it in the applet.
		// ---------------------------------------------------------------
		TabManager viewManager = (TabManager) conzillaEnv.kit.getConzilla().getViewManager();
		viewManager.getSinglePane().setSize(Integer.parseInt(getParameter("WIDTH")) - 20,
				Integer.parseInt(getParameter("HEIGHT")) - 20);
		viewManager.setRootPaneContainer(this);
	}

	protected void exit() {
		stop();
		// destroy();
	}

}