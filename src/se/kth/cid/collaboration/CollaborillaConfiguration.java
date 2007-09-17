/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import se.kth.cid.component.ComponentException;
import se.kth.cid.config.Config;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.config.PropertiesConfiguration;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.config.CollaborationSettingsDialog;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.util.Tracer;

/**
 * Helper class for handling the collaboration settings.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class CollaborillaConfiguration {
	
	public static final URI AGENT_LOAD_URI = URI.create("conzilla://ont/defaultagent.rdf");
	
	private Config config;

	public CollaborillaConfiguration(Config config) {
		this.config = config;
	}
	
	/**
	 * Stores the publishing locations in the Conzilla configuration. Encodes
	 * the information internally to avoid problems with configuration format.
	 * 
	 * @param locationInfos
	 *            List of LocationInformation objects.
	 */
	public void storeLocations(List locationInfos) {
		if (locationInfos == null) {
			return;
		}

		config.clearProperty(Settings.CONZILLA_COLLAB_LOCATIONS);

		Iterator locInfoIt = locationInfos.iterator();
		while (locInfoIt.hasNext()) {
			LocationInformation info = (LocationInformation) locInfoIt.next();
			String encInfo = encodeLocationInformation(info);
			config.addProperty(Settings.CONZILLA_COLLAB_LOCATIONS, encInfo);
		}
	}

	/**
	 * Converts a DefaultListModel to a List and calls storeLocations(List).
	 * 
	 * @param model
	 *            DefaultListModel with LocationInformation objects.
	 */
	public void storeLocations(DefaultListModel model) {
		storeLocations(Collections.list(model.elements()));
	}

	/**
	 * Reads the publishing locations out of the Configuration and returns them
	 * as a List of LocationInformation objects. Decodes the encoded information
	 * internally.
	 * 
	 * @return List of LocationInformation objects.
	 */
	public List<LocationInformation> getLocations() {
		List encInfoList = config.getStringList(Settings.CONZILLA_COLLAB_LOCATIONS, new ArrayList());
		List<LocationInformation> result = new ArrayList<LocationInformation>();

		Iterator encInfoIt = encInfoList.iterator();
		while (encInfoIt.hasNext()) {
			String encInfo = (String) encInfoIt.next();
			LocationInformation locationInfo = decodeLocationInformation(encInfo);
			result.add(locationInfo);
		}

		return result;
	}

	/**
	 * Returns the publishing locations as a DefaultListModel.
	 * 
	 * @return DefaultListModel with LocationInformation objects.
	 */
	public DefaultListModel getLocationsListModel() {
		DefaultListModel model = new DefaultListModel();
		List locations = getLocations();
		Iterator locIt = locations.iterator();
		while (locIt.hasNext()) {
			LocationInformation info = (LocationInformation) locIt.next();
			model.addElement(info);
		}
		return model;
	}

	/**
	 * Stores the setting of the last used destination information.
	 * 
	 * @param info
	 *            LocationInformation object.
	 */
	public void storePreviouslyUsedDestination(LocationInformation info) {
		String encInfo = encodeLocationInformation(info);
		config.setProperty(Settings.CONZILLA_COLLAB_PREV_LOCATION, encInfo);
	}

	/**
	 * @return Returns the previously used publishing location information. Null
	 *         if there is no such setting.
	 */
	public LocationInformation getPreviouslyUsedDestination() {
		String encodedInfo = config.getString(Settings.CONZILLA_COLLAB_PREV_LOCATION, null);
		LocationInformation locationInfo = null;
		if (encodedInfo != null) {
			locationInfo = decodeLocationInformation(encodedInfo);
		}
		return locationInfo;
	}

	/**
	 * Encodes a LocationInformation object so that it can be stored in the
	 * configuration.
	 * 
	 * @param info
	 *            LocationInformation object.
	 * @return Encoded String.
	 */
	private String encodeLocationInformation(LocationInformation info) {
		String encTitle = "";
		String encDescription = "";
		String encPublishingLocation = "";
		String encPublicAccessLocation = "";
		try {
			encTitle = URLEncoder.encode(info.getTitle(), "UTF-8");
			encDescription = URLEncoder.encode(info.getDescription(), "UTF-8");
			encPublishingLocation = URLEncoder.encode(info.getPublishingLocation(), "UTF-8");
			encPublicAccessLocation = URLEncoder.encode(info.getPublicAccessLocation(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Tracer.debug("Unable to encode information: " + e.getMessage());
		}
		String encInfo = encTitle + "|" + encDescription + "|" + encPublishingLocation + "|" + encPublicAccessLocation;
		return encInfo;
	}

	/**
	 * Decodes encoded location information.
	 * 
	 * @param encodedInformation
	 *            String with encoded information.
	 * @return LocationInformation object.
	 */
	private LocationInformation decodeLocationInformation(String encodedInformation) {
		int tokenCount = 0;
		for (int i = 0; i < encodedInformation.length(); i++) {
			if (encodedInformation.charAt(i) == '|') {
				tokenCount++;
			}
		}
		if (tokenCount != 3) {
			throw new IllegalArgumentException("Incorrect amount of tokens.");
		}
		
		// We don't use StringTokenizer here because we want
		// to support empty values as well
		String[] tokens = new String[4];
		int startPos = 0;
		int endPos = 0;
		for (int i = 0; i < 4; i++) {
			startPos = endPos;
			endPos = encodedInformation.indexOf("|", startPos + 1);
			if (startPos > 0) {
				startPos++;
			}
			if (endPos == -1) {
				endPos = encodedInformation.length();
			}
			tokens[i] = encodedInformation.substring(startPos, endPos);
		}
		
		LocationInformation locationInfo = new LocationInformation();
		try {
			locationInfo.setTitle(URLDecoder.decode(tokens[0], "UTF-8"));
			locationInfo.setDescription(URLDecoder.decode(tokens[1], "UTF-8"));
			locationInfo.setPublishingLocation(URLDecoder.decode(tokens[2], "UTF-8"));
			locationInfo.setPublicAccessLocation(URLDecoder.decode(tokens[3], "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Tracer.debug("Unable to decode information: " + e.getMessage());
		}
		return locationInfo;
	}

	/**
	 * Returns the port number of the Collaborilla service.
	 * 
	 * @return Port number of the Collaborilla service. If the configuration
	 *         does not contain such a setting this method returns 2108 by
	 *         default.
	 */
	public int getCollaborillaServerPort() {
		return config.getInt(Settings.CONZILLA_COLLAB_PORT, 2108);
	}

	/**
	 * Sets the port number of the Collaborilla service.
	 * 
	 * @param port
	 *            Port number of the Collaborilla service.
	 */
	public void setCollaborillaServerPort(int port) {
		config.setProperty(Settings.CONZILLA_COLLAB_PORT, new Integer(port));
	}

	/**
	 * Returns the hostname of the Collaborilla service.
	 * 
	 * @return Hostname of the Collaborilla service. If the configuration does
	 *         not contain such a setting this method returns an empty String by
	 *         default.
	 */
	public String getCollaborillaServer() {
		return config.getString(Settings.CONZILLA_COLLAB_HOST, "collaborilla.conzilla.org");
	}

	/**
	 * Sets the hostname of the Collaborilla service.
	 * 
	 * @param hostName
	 *            Hostname of the Collaborilla service.
	 */
	public void setCollaborillaServer(String hostName) {
		config.setProperty(Settings.CONZILLA_COLLAB_HOST, hostName);
	}
	
	/**
	 * @return Returns the namespace of the current user. If this setting is not
	 *         set or empty, it returns an empty String.
	 */
	public String getUserNamespace() {
		return config.getString(Settings.CONZILLA_USER_NAMESPACE, "");
	}

	/**
	 * Sets the namespace of the current user.
	 * 
	 * @param namespace
	 *            Namespace in the form of a URI.
	 */
	public void setUserNamespace(String namespace) {
		config.setProperty(Settings.CONZILLA_USER_NAMESPACE, namespace);
	}
	
	/**
	 * Contructs the users personal URI with the help of the personal namespace.
	 * 
	 * @param namespace
	 *            Personal (and unique) namespace.
	 * @return A personal URI.
	 */
	public String getAgentURI(String namespace) {
		if (namespace.length() == 0) {
			return null;
		}
		if (!namespace.endsWith("/")) {
			namespace += "/";
		}
		return namespace + "profile";
	}
	
	/**
	 * @return True if the collaboration settings are configured in a way that
	 *         the user is able to publish.
	 */
	public boolean isProperlyConfigured() {
		if (getAgentURI(getUserNamespace()) == null) {
			return false;
		}
		if (getCollaborillaServer().length() == 0) {
			return false;
		}
		if (getLocations().size() == 0) {
			return false;
		}
		try {
			ConzillaKit.getDefaultKit().getResourceStore().getAndReferenceContainer(AGENT_LOAD_URI);
		} catch (ComponentException e) {
			return false;
		}
		
		return true;
	}
	
	private boolean importCollaborationSettingsFromFile(String settingsFile) {
		Config collabSettings = new PropertiesConfiguration("Collaboration Settings");
		try {
			collabSettings.load(new File(settingsFile).toURL());
		} catch (Exception e) {
			Tracer.debug(e.getMessage());
			return false;
		}
		
		String hostname = collabSettings.getString("host");
		int port = collabSettings.getInt("port", -1);
		List location = collabSettings.getStringList("location");
		String namespace = collabSettings.getString("namespace");
		
		if ((hostname == null) || (port == -1) || (location != null && location.size() == 0) || (namespace == null)) {
			Tracer.debug("Configuration incomplete.");
			return false;
		}
		
		setCollaborillaServer(hostname);
		setCollaborillaServerPort(port);
		setUserNamespace(namespace);
		
		Config conzillaConfig = ConfigurationManager.getConfiguration();
		// We already get it encoded, so we store it withouth CollaborillaSupport
		conzillaConfig.setProperties(Settings.CONZILLA_COLLAB_LOCATIONS, location);
		
		return true;
	}
	
	public void importCollaborationSettings(String settingsFile) {
		boolean successful = importCollaborationSettingsFromFile(settingsFile);
		if (successful) {
			Tracer.debug("Collaboration settings successfully imported.");
			JOptionPane.showMessageDialog(null,
					"Collaboration settings successfully imported!\n\n" +
					"Please complete the configuration with your personal information\nin the following dialog.",
					"Import successful", JOptionPane.INFORMATION_MESSAGE);
			new CollaborationSettingsDialog().setVisible(true);
		} else {
			Tracer.debug("Failed to import configuration from file " + settingsFile);
			JOptionPane.showMessageDialog(null,
					"Failed to import configuration from file\n" + settingsFile,
					"Import failed", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void askForConfiguration() {
		Config config = ConfigurationManager.getConfiguration();
		boolean ask = config.getBoolean(Settings.CONZILLA_COLLAB_ASKFORCONFIG, true);
		if (!ask) {
			return;
		}
		Object[] options = { "Yes", "Ask later", "Don't ask me again" };
		int answer = JOptionPane.showOptionDialog(null,
				"You have not configured your collaboration settings yet.\n\n" +
				"Would you like to do this now?", "Configure now?",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if (answer == 0) {
			Object[] options2 = { "Import from file", "Configure manually", "Cancel" };
			int answer2 = JOptionPane.showOptionDialog(null,
					"How do you want to configure the collaboration settings?", "Collaboration settings",
					JOptionPane.DEFAULT_OPTION,	JOptionPane.QUESTION_MESSAGE,
					null, options2, options2[0]);
			if (answer2 == 0) {
				askForConfigurationFile();
			} else if (answer2 == 1){
				new CollaborationSettingsDialog().setVisible(true);
			}
		} else if (answer == 2) {
			config.setProperty(Settings.CONZILLA_COLLAB_ASKFORCONFIG, new Boolean(false));
		}
	}
	
	public void askForConfigurationFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select configuration file");
		FileFilter ccsFilter = new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".ccs") || f.isDirectory();
			}
			public String getDescription() {
				return "Conzilla Collaboration Settings (*.ccs)";
			}
		};
		fileChooser.addChoosableFileFilter(ccsFilter);
		fileChooser.setFileFilter(ccsFilter);
		if (fileChooser.showDialog(null, "Import") == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String settingsFile = file.toString();
			importCollaborationSettings(settingsFile);
		}
	}
	
}