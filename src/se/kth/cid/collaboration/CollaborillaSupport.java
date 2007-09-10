/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;

import se.kth.cid.component.ComponentException;
import se.kth.cid.config.Config;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.config.Settings;
import se.kth.cid.util.Tracer;
import se.kth.nada.kmr.collaborilla.client.CollaborillaServiceClient;
import se.kth.nada.kmr.collaborilla.client.CollaborillaStatefulClient;
import se.kth.nada.kmr.collaborilla.client.CollaborillaStatelessClient;
import se.kth.nada.kmr.collaborilla.client.CollaborillaStatelessServiceClient;

/**
 * CollaborillaSupport provides important methods for enabling Collaborilla
 * access in Conzilla.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class CollaborillaSupport {
	
	public static final URI AGENT_LOAD_URI = URI.create("conzilla://ont/defaultagent.rdf");

	private Config config;

	private MetaDataCache metaCache;

	/**
	 * @param config
	 *            Configuration where the necessary Collaborilla settings can be
	 *            found in. Usually the default Conzilla configuration.
	 */
	public CollaborillaSupport(Config config) {
		if (config == null) {
			throw new IllegalArgumentException("Constructor argument must not be null.");
		}
		this.config = config;
		this.metaCache = ConzillaKit.getDefaultKit().getResourceStore().getMetaDataCache();
	}

	public MetaDataCache getMetaDataCache() {
		return metaCache;
	}

	/**
	 * Reads the Collaborilla host and port from the Conzilla settings and
	 * returns an instance of a client.
	 * 
	 * @return An instance of CollaborillaStatefulClient
	 */
	public CollaborillaStatefulClient getStatefulClient() {
		return new CollaborillaServiceClient(getCollaborillaServer(), getCollaborillaServerPort());
	}

	/**
	 * Reads the Collaborilla host and port from the Conzilla settings and
	 * returns an instance of a client.
	 * 
	 * @return An instance of CollaborillaStatefulClient
	 */
	public CollaborillaStatelessClient getStatelessClient() {
		return new CollaborillaStatelessServiceClient(getCollaborillaServer(), getCollaborillaServerPort());
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
	public List getLocations() {
		List encInfoList = config.getStringList(Settings.CONZILLA_COLLAB_LOCATIONS, new ArrayList());
		List result = new ArrayList();

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
		StringTokenizer tok = new StringTokenizer(encodedInformation, "|");
		if (tok.countTokens() != 4) {
			throw new IllegalArgumentException("Incorrect amount of tokens.");
		}
		String[] tokens = new String[4];
		for (int i = 0; tok.hasMoreElements(); i++) {
			tokens[i] = (String) tok.nextToken();
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

}