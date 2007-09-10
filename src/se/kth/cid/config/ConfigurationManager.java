/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import se.kth.cid.conzilla.install.Installer;
import se.kth.cid.util.Tracer;

/**
 * ConfigurationManager is loading, saving, and returning Conzilla's configuration.
 * 
 * @see se.kth.cid.config.Config
 * @author Hannes Ebner
 * @version $Id$
 */
public class ConfigurationManager {

	/**
	 * Instance of the ConfigurationManager.
	 */
	private static ConfigurationManager configManager;

	/**
	 * Interval in ms to store a modified configuration to disk.
	 */
	private static long flushingInterval = 10000;

	/**
	 * Instance of the configuration object.
	 */
	private static Config mainConfig;

	/**
	 * Tells whether the configuration can be saved or not. Cannot be configured
	 * externally right now. (It was intended to be used in connection with the
	 * applet implementation.)
	 */
	private static final boolean saveable = true;

	/**
	 * Helper to get the class name without packages. (Not needed for JDK >=
	 * 1.5)
	 */
	private final static String simpleClassName = ConfigurationManager.class.getName().substring(
			ConfigurationManager.class.getName().lastIndexOf('.') + 1);

	/* Private methods */

	/**
	 * This class is to be used as Thread for a ShutdownHook and as TimerTask.
	 * It checks whether the configuration has been modified and stores it to
	 * disk if so.
	 * 
	 * @author Hannes Ebner
	 */
	private class ConfigurationSaver extends TimerTask implements Runnable {
		public void run() {
			if (saveable && getConfiguration().isModified()) {
				URI configURI = getConfigurationURI();
				boolean successful = true;
				try {
					getConfiguration().save(configURI.toURL());
				} catch (MalformedURLException e) {
					successful = false;
					debug(e.getMessage());
				} catch (IOException e) {
					successful = false;
					debug(e.getMessage());
				}
				if (successful) {
					debug("Configuration saved to " + configURI);
				}
			}
		}
	}

	/**
	 * Constructor to be called indirectly by initialize(). Checks whether a
	 * configuration file exists and loads it respectively creates a new
	 * configuration.
	 * 
	 * @throws ConfigurationException
	 */
	private ConfigurationManager() {
		File configFile = new File(getConfigurationURI());
		if (configFile.exists()) {
			try {
				// We don't call configFile.toURL() because it doesn't escape spaces etc.
				loadConfiguration(getConfigurationURI().toURL());
			} catch (MalformedURLException e) {
				debug(e.getMessage());
			} catch (IOException e) {
				debug(e.getMessage());
			}
		} else {
			createConfiguration();
		}
		startConfigurationSaver();
	}
	
	private void initMainConfig() {
		mainConfig = Configurations.synchronizedConfig(new PropertiesConfiguration("Conzilla Configuration"));
	}

	/**
	 * Loads an already existing configuration.
	 * @throws IOException 
	 * 
	 * @throws ConfigurationException
	 */
	private void loadConfiguration(URL configURL) throws IOException {
		initMainConfig();
		mainConfig.load(configURL);
		debug("INIT: Configuration loaded from " + configURL);
	}

	/**
	 * Creates a new configuration.
	 * 
	 * @throws ConfigurationException
	 */
	private void createConfiguration() {
		// Create all necessary directories
		File confDir = new File(getConfigurationDirectory());
		if (!(confDir.exists() && confDir.isDirectory())) {
			if (!confDir.mkdirs()) {
				debug("INIT: Unable to create configuration directory");
			}
		}
		initMainConfig();
		debug("INIT: Configuration created");
	}

	/**
	 * Enables a TimerTask and a ShutdownHook to store a modified configuration
	 * to disk.
	 */
	private void startConfigurationSaver() {
		debug("INIT: Setting up configuration interval saving");
		new Timer().schedule(new ConfigurationSaver(), flushingInterval, flushingInterval);
		Runtime.getRuntime().addShutdownHook(new Thread(new ConfigurationSaver()));
	}

	private static void debug(String message) {
		Tracer.debug(simpleClassName + ": " + message);
	}

	/**
	 * @return Returns the configuration directory.
	 */
	private static String getConfigurationDirectory() {
		String dirHelper = Installer.getConzillaDir().getAbsolutePath();
		if (!dirHelper.endsWith(File.separator)) {
			dirHelper = dirHelper.concat(File.separator);
		}
		return dirHelper;
	}

	/* Public methods */

	/**
	 * This class is implemented as Singleton, so we want to avoid having
	 * multiple instances of the same object by cloning.
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(this.getClass() + " is a Singleton.");
	}

	/**
	 * This method can be called once to initialize the ConfigurationManager and
	 * its contained configuration object. If the ConfigurationManager has been
	 * initialized already, this method throws an IllegalStateException.
	 * 
	 * @param input
	 *            InputStream to load the configuration from.
	 * @param saveable
	 *            Tells whether the configuration can be saved or not.
	 */
	private synchronized static void initialize() {
		if (configManager != null) {
			throw new IllegalStateException(ConfigurationManager.class + " has already been initialized.");
		}
		configManager = new ConfigurationManager();
	}

	/**
	 * @return Returns the full path to the configuration file.
	 */
	public static URI getConfigurationURI() {
		return Installer.getConfigURI();
	}

	/**
	 * @return Returns the configuration object.
	 */
	public static Config getConfiguration() {
		if (configManager == null) {
			initialize();
		}
		if (mainConfig == null) {
			throw new IllegalStateException(simpleClassName
					+ " is in an illegal state: no configuration is managed.");
		}
		return mainConfig;
	}

}