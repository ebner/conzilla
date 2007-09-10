/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.collaboration;

import se.kth.cid.config.Config;
import se.kth.cid.conzilla.app.ConzillaKit;
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
	
	private CollaborillaConfiguration collabConfig;

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
		this.collabConfig = new CollaborillaConfiguration(config);
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
		return new CollaborillaServiceClient(collabConfig.getCollaborillaServer(), collabConfig.getCollaborillaServerPort());
	}

	/**
	 * Reads the Collaborilla host and port from the Conzilla settings and
	 * returns an instance of a client.
	 * 
	 * @return An instance of CollaborillaStatefulClient
	 */
	public CollaborillaStatelessClient getStatelessClient() {
		return new CollaborillaStatelessServiceClient(collabConfig.getCollaborillaServer(), collabConfig.getCollaborillaServerPort());
	}

}