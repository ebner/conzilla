/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.filter;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.RelationSet;
import se.kth.cid.component.Resource;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.layout.ContextMap;

/**
 * This is a class for creating a new Filter
 * 
 * @author Daniel Pettersson
 * @version $Revision$
 */
public class SimpleFilterFactory implements FilterFactory {
	
	Log log = LogFactory.getLog(SimpleFilterFactory.class);
	
	Filter cachedFilter;

	URI cachedURI;

	public SimpleFilterFactory() {
		cachedFilter = null;
		cachedURI = null;
	}

	public void refresh() {
		cachedURI = null;
		cachedFilter = null;
	}

	public Filter createFilter(MapController cont, Concept concept, ContextMap conceptMap) {
		if (concept != null) {
			Resource[] filters = (new RelationSet(concept, "filter", ConzillaKit.getDefaultKit().getResourceStore()))
					.getRelations();

			if (filters != null && filters.length > 0) {
				try {
					return createFilter(cont, URI.create(filters[0].getURI()));
				} catch (FilterException fe) {
					log.error("Malformed filter in concept " + concept.getURI().toString(), fe);
				}
			}
		}

		if (conceptMap != null) {
			Resource[] filters = (new RelationSet(conceptMap, "filter", ConzillaKit.getDefaultKit().getResourceStore()))
					.getRelations();

			if (filters != null && filters.length > 0) {
				try {
					return createFilter(cont, URI.create(filters[0].getURI()));
				} catch (FilterException fe) {
					log.error("Malformed filter in concept " + concept.getURI().toString(), fe);
				}
			}
		}
		return null;
	}

	protected Filter createFilter(MapController cont, URI uri) throws FilterException {
		if (cachedURI != null && uri.equals(cachedURI))
			return cachedFilter;
		else {
			cachedURI = uri;
			cachedFilter = new ConcreteFilter(cont, uri);
			return cachedFilter;
		}
	}
}
