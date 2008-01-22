/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.app;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.IconUIResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.config.LoggingConfiguration;
import se.kth.cid.config.PropertiesConfiguration;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.conzilla.content.ContentDisplayer;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.install.Installer;
import se.kth.cid.conzilla.properties.Images;
import se.kth.cid.conzilla.remote.CommandListener;
import se.kth.cid.conzilla.remote.ConzillaInstructor;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.util.FileOperations;

/**
 * This class represents the information specific to Conzilla when run as an
 * installed application. Thus, it will install Conzilla files .locally, and use
 * information stored there. It does _not_, however, assume that Conzilla is run
 * as a stand-alone app. Conzilla can be run as an applet with.local
 * installation this way too.
 */

public abstract class ConzillaAppEnv implements ConzillaEnvironment {
	
	ContentDisplayer defaultContentDisplayer;
	
	Log log = LogFactory.getLog(ConzillaAppEnv.class);

	public ConzillaKit kit;
	
	private boolean onlineState;
	
	private View startMapView;
	
	protected String[] arguments;

	static class InstallException extends IOException {
	}
	
	static class UpgradeException extends IOException {
	}

	public ConzillaAppEnv() {
	}

	public ContentDisplayer getDefaultContentDisplayer() {
		return defaultContentDisplayer;
	}

	protected void start() {
		try {
			String os = (String) System.getProperties().get("os.name");
			String version = (String) System.getProperties().get("java.version");
			log.info("OS = " + os);
			log.info("Java version = " + version);
			log.info("Conzilla version = " + Conzilla.CURRENT_VERSION);
			if ((os != null) && (version != null)) {
				if (os.toLowerCase().matches(".*mac.*")) {
					if (Float.parseFloat(version.substring(0, 3)) >= 1.5) {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
				} else if (os.toLowerCase().matches(".*windows.*")) {
					if (Float.parseFloat(version.substring(0, 3)) == 1.5) {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
				} else if (os.toLowerCase().matches(".*linux.*")) {
					// Only set GTK look and feel if version is 1.6 or higher.
//					Deactivated for now because the native look does not look good for some reason
//					-> needs investigation
//					if (Float.parseFloat(version.substring(0, 3)) >= 1.6) {
//						UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//					}
				}
			}
			UIManager.put("InternalFrame.icon", new IconUIResource(Images.getImageIcon(Images.ICON_CONZILLA_16)));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		try {
			tryInit();
		} catch (InstallException e) {
			needInstall("Conzilla does not seem to be installed\n" + "on your account.\n");
		} catch (UpgradeException ue) {
			needUpgrade();
		} catch (IOException e) {
			initError("Problem loading Conzilla.\n", e);
		}
	}

	void loadConfig() throws IOException {
	}
	
	private void initOnlineState() {
		boolean online;
		BasicService basicService;
		try {
			basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			online = !basicService.isOffline();
		} catch (UnavailableServiceException ue) {
			online = ConfigurationManager.getConfiguration().getBoolean(Settings.CONZILLA_ONLINESTATE, true);
		}
		setOnline(online);
	}
	
	public boolean isOnline() {
		return onlineState;
	}
	
	public void setOnline(boolean state) {
		onlineState = state;
		ConfigurationManager.getConfiguration().setProperty(Settings.CONZILLA_ONLINESTATE, new Boolean(state));
	}
	
	public boolean toggleOnlineState() {
		synchronized (this) {
			setOnline(!isOnline());
		}
		return isOnline();
	}

	void tryInit() throws IOException, InstallException {
		if (!Installer.getConzillaDir().exists()) {
			throw new InstallException();
		}
		
		final File lockFile = new File(Installer.getConzillaDir(), ".lock");
		// we have to use two different files. if we write the port into the lock file
		// the method to aquire the lock works differently for some reason...
		final File portFile = new File(Installer.getConzillaDir(), ".port");
		
		InstanceChecker ic = null;
		if (Installer.getConzillaDir().exists()) {
			ic = new InstanceChecker(lockFile);
			if (ic.isApplicationActive()) {
				try {
					ConzillaInstructor instructor = new ConzillaInstructor(portFile);
					if ((arguments.length >= 2) && arguments[0].toLowerCase().equals("-open")) {
						instructor.openContextMap(arguments[1]);
					}
					instructor.toForeground();
				} catch (IllegalArgumentException iae) {
					log.warn("Another instance of Conzilla is already running", iae);
					JOptionPane.showMessageDialog(null, "Another instance of Conzilla is already running.", "Multiple Instances", JOptionPane.ERROR_MESSAGE);
				}
				System.exit(-1);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Conzilla is not properly configured. Exiting.", "Unable to start", JOptionPane.ERROR_MESSAGE);
		}
		
		Config config = ConfigurationManager.getConfiguration();
		String prop = config.getString(Settings.CONZILLA_VERSION);
		if (prop != null) {
			if (!prop.equals(Conzilla.CURRENT_VERSION)) {
				if (ic != null) {
					// We have to release the lock here, because we
					// will start this method again after the upgrade
					ic.release();
				}
				throw new UpgradeException();
			}
		} else {
			throw new IOException("Property not found: " + Settings.CONZILLA_VERSION + "\nConfiguration broken?");
		}
		
		StartupProgressSplash splash = new StartupProgressSplash();
		splash.showSplash();
		
		initOnlineState();
		if (isOnline()) {
			log.info("Conzilla starting in ONLINE mode");
		} else {
			log.info("Conzilla starting in OFFLINE mode");
		}

		splash.setStatusText("Initializing content displayer...");
		initDefaultContentDisplayer();
		splash.setPercentage(20);
		
		splash.setStatusText("Loading core components...");
		ConzillaKit.createFullKit(this);
		kit = ConzillaKit.getDefaultKit();
		splash.setPercentage(40);
		
		splash.setStatusText("Loading required containers...");
		loadContainers();
		splash.setPercentage(60);
		
		splash.setStatusText("Loading root library...");
		kit.fetchRootLibrary(config.getURI(Settings.CONZILLA_LIBRARY));
		splash.setPercentage(80);
		
		splash.setStatusText("Loading start map...");
		loadStartMap();
		splash.setPercentage(100);
		
		splash.dispose();
		
		Thread serverThread = new Thread(new Runnable() {
			public void run() {
				new CommandListener(portFile).start();
			}
		});
		serverThread.start();
	}

	private void loadContainers() {
		// Stylecontainers
		loadContainersFromProperty(Settings.CONZILLA_STYLECONTAINER);

		// librarycontainers
		loadContainersFromProperty(Settings.CONZILLA_LIBRARYCONTAINER);

		List startContainers = ConfigurationManager.getConfiguration().getStringList(Settings.CONZILLA_STARTCONTAINER);
		// Startcontainers
		boolean loaded = loadContainers(startContainers, Container.COMMON);

		if (!loaded) {
			ErrorMessage.showError("No valid containers", "No valid containers could be found.\n\nGiving up.", null, null);
			exit(1);
		}
	}

	protected void loadContainersFromProperty(String prop) {
		List containerList = ConfigurationManager.getConfiguration().getStringList(prop, new ArrayList());
		Iterator containerIt = containerList.iterator();
		while (containerIt.hasNext()) {
			String uri = (String)containerIt.next();
			loadContainer(uri, prop, "Cannot load container specified by property " + prop
					+ " in conzilla.properties. Maybe the container URI:\n" + uri + "\n is wrong.", false);
		}
	}

	protected boolean loadContainers(List startContainers, String purpose) {
		boolean loaded = false;
		for (Iterator containerIT = startContainers.iterator(); containerIT.hasNext();) {
			String uriStr = (String) containerIT.next();
			URI uri = null;
			try {
				uri = new URI(uriStr);
			} catch (URISyntaxException e) {
				ErrorMessage.showError("Invalid start container URI", "Invalid start container URI\n" + uri
						+ ":\n " + e.getMessage(), e, null);
			}
			if (loadContainer(uri, purpose, "", false) != null) {
				log.info("Succeeded loading container " + uri.toString());
				loaded = true;
			} else {
				log.error("Failed loading container " + uri.toString());
			}
		}
		return loaded;
	}

	protected Container loadContainer(String uri, String purpose, String failMessage, boolean failHard) {
		try {
			return loadContainer(new URI(uri), purpose, failMessage, failHard);
		} catch (URISyntaxException me) {
			if (failMessage != null) {
				ErrorMessage.showError("Invalid container URI", failMessage, me, null);
			}
			if (failHard) {
				exit(1);
			}
		}
		return null;
	}
	
	private Container loadContainer(URI uri, String purpose, String failMessage, boolean failHard) {
		try {
			Container container = kit.getResourceStore().getAndReferenceContainer(uri);
			if (container != null && purpose != null) {
				container.setPurpose(purpose);
			}
			return container;
		} catch (ComponentException e) {
			if (failMessage != null) {
				ErrorMessage.showError("Failed loading container", failMessage, e, null);
			}
			if (failHard) {
				exit(1);
			}
		}
		return null;
	}
	
	public void loadContextMap(String metaFile, boolean newView) {
		log.info("Attempting to open context-map described in file: " + metaFile);
		
		Config ccm = new PropertiesConfiguration("Context-Map");
		try {
			ccm.load(new File(metaFile).toURL());
		} catch (Exception e) {
			log.error(e);
		}
		
		String mapURI = ccm.getString("uri");
		
		if (mapURI == null) {
			log.info("No context-map URI found in ccm-file");
		} else {
			try {
				if (newView) {
					ConzillaKit.getDefaultKit().getConzilla().openMapInNewView(new URI(mapURI), null);
				} else {
					ConzillaKit.getDefaultKit().getConzilla().openMapInOldView(new URI(mapURI), startMapView);
				}
			} catch (ControllerException e) {
				log.error(e);
			} catch (URISyntaxException e) {
				log.error(e);
			}
		}
	}

	private void loadStartMap() {
		Vector<URI> startMaps = new Vector<URI>();

		String strStartMap = ConfigurationManager.getConfiguration().getString(Settings.CONZILLA_STARTMAP);
		if (strStartMap != null) {
			try {
				URI startMap = new URI(strStartMap);
				startMaps.add(startMap);
			} catch (URISyntaxException me) {
				ErrorMessage.showError("Invalid map URI", "Invalid start map URI:\n" + strStartMap + ":\n "
						+ me.getMessage(), me, null);
			}
		}

		try {
			startMaps.add(new URI(DEFAULT_STARTMAP));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		if (startMaps.size() == 0) {
			return;
		}

		int i;
		for (i = 0; i < startMaps.size(); i++) {
			try {
				startMapView = kit.getConzilla().openMapInNewView((URI) startMaps.get(i), null);
			} catch (ControllerException e) {
				log.error("Unable to open map", e);
				continue;
			}
			return;
		}

		// FIXME: commented the following lines out to be able to start conzilla
		// without any startmap
		// ErrorMessage.showError("No valid maps",
		// "No valid maps could be found.\n\nGiving up.", null, null);
		// exit(1);
	}

	protected abstract void initDefaultContentDisplayer();

	public void exit(int result) {
		System.exit(result);
	}

	void initError(String error, Exception e) {
		ErrorMessage.showError("Fatal initialization error", "Cannot start Conzilla:\n\n" + error
				+ "\n\nYou will be given the option\n" + "to reinstall.", e, null);
		needInstall("There was an initialization error.\n\n" + "You are now given the option\n"
				+ "to reinstall, but this may not be the\n" + "right solution, as this will destroy\n"
				+ "any customizations you might have made.");
	}

	void needInstall(String s) {
		Installer.installOrExit(s);

		String error = null;
		Exception ex = null;
		try {
			tryInit();
		} catch (IOException e) {
			error = "Conzilla does not seem to be correctly installed\n" + "on your computer";
			ex = e;
		}
		
		if (error != null) {
			ErrorMessage.showError("Fatal Error", "Could not start Conzilla:\n\n" + error
					+ "\n\nThis is probably an installer bug." + "\n\nGiving up.\n\n", ex, null);
			exit(1);
		}
	}

	void needUpgrade() {
		Config config = ConfigurationManager.getConfiguration();
		String installedVersion = config.getString(Settings.CONZILLA_VERSION);
		String thisVersion = Conzilla.CURRENT_VERSION;
		if (installedVersion != null) {
			// Example: Upgrade path 2.1.1 -> 2.2
			//if (installedVersion.startsWith("2.1.1") && thisVersion.startsWith("2.2")) {
				// do upgrade stuff
				// if something goes wrong during upgrade:
				// show message and throw e.g. a RuntimeException
			//}
			
			// Upgrade path 2.1.x -> now (Conzilla 2.1 was released with version set to 1.1 by mistake)
			if (installedVersion.startsWith("1.1") || installedVersion.startsWith("2.1")) {
				config.setProperty("conzilla.colortheme.theme-definitions.standard.concept-focus", "0xffa22b2b");
				config.setProperty("conzilla.colortheme.theme-definitions.standard.context", "0xff0d418e");
				config.setProperty(Settings.CONZILLA_EXTERNAL_SINDICE_PUBLISH, true);
				File log4jConfigFile = new File(Installer.getConzillaDir(), Installer.LOG_CONFIG_FILE);
				try {
					FileOperations.copyFile(new File(getClass().getClassLoader().getResource("install/" + Installer.LOG_CONFIG_FILE).getFile()), log4jConfigFile);
				} catch (IOException e) {
					log.error("Unable to copy file", e);
				}
				LoggingConfiguration.loadConfiguration(log4jConfigFile);
			}
			
			// Upgrade path 2.1.x -> 2.2.x
			if (installedVersion.startsWith("2.1") && thisVersion.startsWith("2.2")) {
				// place holder
			}
			
			upgradeSuccessful(installedVersion, thisVersion);
		}
	}
	
//	void copyFile(File source, File destination) {
//		OutputStream os = null;
//		InputStream is = null;
//		try {
//			os = new BufferedOutputStream(new FileOutputStream(destination));
//			is = new BufferedInputStream(new FileInputStream(source));
//
//			byte[] b = new byte[2048];
//			int s;
//			while ((s = is.read(b)) != -1) {
//				os.write(b, 0, s);
//			}
//		} catch (IOException e) {
//			log.error("Unable to copy file from " + source + " to " + destination, e);
//		} finally {
//			try {
//				if (os != null) {
//					os.close();
//				}
//				if (is != null) {
//					is.close();
//				}
//			} catch (Exception ignored) {}
//		}
//	}
	
	void upgradeSuccessful(String oldVersion, String newVersion) {
		Config config = ConfigurationManager.getConfiguration();
		config.setProperty(Settings.CONZILLA_VERSION, Conzilla.CURRENT_VERSION);
		log.info("Conzilla configuration upgrade from " + oldVersion + " to " + newVersion + " successful");
		
		try {
			tryInit();
		} catch (IOException e) {
			ErrorMessage.showError("Fatal Error", "Unable to start Conzilla", e, null);
			exit(1);
		}
	}
	
}