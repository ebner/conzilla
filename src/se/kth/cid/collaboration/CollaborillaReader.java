/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;
import se.kth.nada.kmr.collaborilla.client.CollaborillaException;
import se.kth.nada.kmr.collaborilla.client.CollaborillaStatefulClient;
import se.kth.nada.kmr.collaborilla.client.CollaborillaStatelessClient;

/**
 * Provides wrapping methods to read from the Collaborilla service. Takes care
 * of properly caching the data with the MetaDataDiskCache.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class CollaborillaReader {
	
	public static final int LATEST_REVISION = 0;
	
	CollaborillaSupport support;
	
	MetaDataCache metaCache;
	
	/**
	 * Depends on CollaborillaSupport which provides e.g. a connection to the
	 * Collaborilla service.
	 * 
	 * @param collabSupport
	 */
	public CollaborillaReader(CollaborillaSupport collabSupport) {
		if (collabSupport == null) {
			throw new IllegalArgumentException("Constructor argument must not be null.");
		}
		this.support = collabSupport;
		this.metaCache = collabSupport.getMetaDataCache();
	}
	
	/**
	 * @return Conzilla's online status.
	 */
	private boolean isOnline() {
		return ConzillaKit.getDefaultKit().getConzillaEnvironment().isOnline();
	}
	
	/**
	 * Tries to fetch the dataset from the Collaborilla service and stores it in
	 * the offline cache. If Conzilla is offline this method tries to get hold
	 * of the dataset by querying the MetaDataDiskCache.
	 * 
	 * @param uri
	 *            URI of a container or context-map.
	 * @param revision
	 *            Revision.
	 * @return Dataset with Collaborilla data. Returns null if the dataset can
	 *         be retrieved neither online nor offline.
	 */
	public CollaborillaDataSet getDataSet(URI uri, int revision) {
		CollaborillaStatelessClient csc = null;
		CollaborillaDataSet dataSet = null;
		
		// We do the following to avoid too many connects. A dataset expires
		// after a configurable time and is compared then to the online version.
		long expirationTime = 30000; // ms
		boolean useCache = false;
		Date cachedDate = metaCache.getCachedAge(uri.toString());
		if (cachedDate != null) {
			long cachedTime = cachedDate.getTime();
			long nowTime = new Date().getTime();
			if ((nowTime - cachedTime) < expirationTime) {
				useCache = true;
			}
		}
		
		if (!useCache && isOnline()) {
			csc = support.getStatelessClient();
			dataSet = csc.get(uri, revision);
			metaCache.putDataSet(uri.toString(), dataSet);
		} else {
			dataSet = metaCache.getDataSet(uri.toString(), null);
		}
		return dataSet;
	}

	/**
	 * Requests the metadata of a container or context-map.
	 * 
	 * @param uri URI of a container or context-map.
	 * @return Metadata as String.
	 */
	public String getMetaData(URI uri) {
		return getMetaData(uri, LATEST_REVISION);
	}
	
	/**
	 * Request the metadata of a container or context-map.
	 * 
	 * @param uri URI of a container or context-map.
	 * @param revision Revision.
	 * @return Metadata as String.
	 */
	public String getMetaData(URI uri, int revision) {
		CollaborillaDataSet dataSet = getDataSet(uri, revision);
		String metaData = null;
		if (dataSet != null) {
			metaData = dataSet.getMetaData();
		}
		return metaData;
	}
		
	/**
	 * Requests a list of required containers (dependencies) of a contextmap.
	 * 
	 * @param uri
	 *            URI of a contextmap.
	 * @return Set of required containers.
	 */
	public Set getRequiredContainers(URI uri) {
		return getRequiredContainers(uri, LATEST_REVISION);
	}

	/**
	 * Requests a list of required containers (dependencies) of a contextmap.
	 * 
	 * @param uri
	 *            URI of a contextmap.
	 * @param revision
	 *            Revision.
	 * @return Set of required containers.
	 */
	public Set getRequiredContainers(URI uri, int revision) {
		CollaborillaDataSet dataSet = getDataSet(uri, revision);
		Set containers = null;
		if (dataSet != null) {
			containers = dataSet.getRequiredContainers();
		}
		return containers;
	}

	/**
	 * Requests a list of optional containers (contributions) of a contextmap.
	 * 
	 * @param uri
	 *            URI of a contextmap.
	 * @return Set of optional containers.
	 */
	public Set getOptionalContainers(URI uri) {
		return getOptionalContainers(uri, LATEST_REVISION);
	}

	/**
	 * Requests a list of optional containers (contributions) of a contextmap.
	 * 
	 * @param uri
	 *            URI of a contextmap.
	 * @param revision
	 *            Revision.
	 * @return Set of optional containers.
	 */
	public Set getOptionalContainers(URI uri, int revision) {
		CollaborillaDataSet dataSet = getDataSet(uri, revision);
		Set containers = null;
		if (dataSet != null) {
			containers = dataSet.getOptionalContainers();
		}
		return containers;
	}

	/**
	 * Queries the Collaborilla service to resolve a given URI into a URL. Since
	 * a URI can have multiple possible locations, it returns a Set of locations
	 * (as String).
	 * 
	 * @param uri
	 *            URI to be resolved.
	 * @return Set of URL Strings.
	 * @throws CollaborillaException
	 */
	public Set resolveURIAndReturnSet(URI uri) {
		return resolveURIAndReturnSet(uri, LATEST_REVISION);
	}
	
	/**
	 * This method takes the revision of the published container into
	 * consideration.
	 * 
	 * @see #resolveURIAndReturnSet(URI)
	 * @param uri
	 *            URI to be resolved.
	 * @param revision
	 *            Revision to be resolved.
	 * @return Set of URL Strings.
	 */
	public Set resolveURIAndReturnSet(URI uri, int revision) {
		CollaborillaDataSet dataSet = getDataSet(uri, revision);
		Set locations = null;
		if (dataSet != null) {
			locations = dataSet.getAlignedLocations();
		}
		return locations;
	}

	/**
	 * Queries the Collaborilla service to resolve a given URI into a URL. A URI
	 * can have multiple possible locations, this methods returns just the first
	 * URL. (as String). <br>
	 * Use resolveURIAndReturnSet(URI) to get a full Set with all locations.
	 * 
	 * @param uri
	 *            URI to be resolved.
	 * @return URL as String.
	 * @throws CollaborillaException 
	 */
	public String resolveURI(URI uri) {
		return resolveURI(uri, LATEST_REVISION);
	}
	
	/**
	 * This method takes the revision of the published container into
	 * consideration.
	 * 
	 * @see #resolveURI(URI)
	 * @param uri
	 *            URI to be resolved.
	 * @param revision
	 *            Revision.
	 * @return URL as String.
	 */
	public String resolveURI(URI uri, int revision) {
		Set locations = resolveURIAndReturnSet(uri, revision);
		if ((locations != null) && (!locations.isEmpty())) {
			return (String) locations.toArray()[0];
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the number of revisions of a component.
	 * 
	 * @param uri
	 *            URI of the component.
	 * @return Number of available revisions. If there is no metadata for this
	 *         component or if the server cannot be accessed (e.g. because
	 *         Conzilla is in offline mode, or the server is down), the value -1
	 *         is returned.
	 */
	public int getRevisionCount(URI uri) {
		CollaborillaStatefulClient csc = null;
		int revisionCount = -1;
		
		if (isOnline()) {
			try {
				csc = support.getStatefulClient();
				csc.connect();
				csc.setIdentifier(uri.toString(), false);
				revisionCount = csc.getRevisionCount();
			} catch (CollaborillaException e) {
				// we are silent here (we get e.g. "Object not found" etc.
			} finally {
				if (csc != null) {
					try {
						csc.disconnect();
					} catch (CollaborillaException ce) {
					}
				}
			}
		}
		
		return revisionCount;
	}
	
	/**
	 * Detects whether a context-map, container or anything with a URI has been
	 * published to the information directory.
	 * 
	 * It queries the metadata cache first, if nothing is found there, it looks
	 * in the information directory, but only if Conzilla is in online mode.
	 * 
	 * @param componentURI
	 * @return Returns true if the URI is found in the information directory or
	 *         metadata cache.
	 */
	public boolean isPublished(URI componentURI) {
		boolean result = false;
		String uri = componentURI.toString();
		
		if (uri.startsWith("urn:path:/org/conzilla/builtin/") || uri.startsWith("conzilla:/")) {
			result = false;
		} else if (support.getMetaDataCache().isCached(componentURI.toString())) {
			result = true;
		} else if (isOnline()) {
			CollaborillaStatefulClient csc = null;
			try {
				csc = support.getStatefulClient();
				csc.connect();
				csc.setIdentifier(componentURI.toString(), false);
				result = true;
			} catch (CollaborillaException ignored) {
				// We get an "Object not found" if the URI is not online
			} finally {
				if (csc != null) {
					try {
						csc.disconnect();
					} catch (CollaborillaException ignored) {
					}
				}
			}
		}
		
		return result;
	}

}