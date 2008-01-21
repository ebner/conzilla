/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import se.kth.cid.conzilla.install.Installer;

/**
 * @author Hannes Ebner
 */
public class LoggingConfiguration {
	
	static Log log;
	
	static boolean initialized = false;
	
	public static void init() {
		if (initialized) {
			return;
		}
		
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		log = LogFactory.getLog(LoggingConfiguration.class);
		File logConfig = new File(Installer.getConzillaDir(), "log4j.properties");
		loadConfiguration(logConfig);
		initialized = true;
	}
	
	public static void loadConfiguration(File logConfig) {
    	URL url = null;
		try {
			if (logConfig.exists()) {
				url = logConfig.toURL();
			}
		} catch (MalformedURLException e) {
			log.error("Unable to load logging configuration, using default values", e);
		}
		if (url != null) {
			PropertyConfigurator.configure(url);
		}
	}

}