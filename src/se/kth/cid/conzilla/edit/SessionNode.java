/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.io.StringReader;
import java.net.URI;

import javax.swing.tree.DefaultMutableTreeNode;

import se.kth.cid.collaboration.MetaDataCache;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.rdf.CV;
import se.kth.cid.util.AttributeEntryUtil;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;

import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Convenience wrapper for ContextMap and Session. Overwrites toString() to be
 * able display the correct title if possible. Overwrites also isLeaf() and
 * getAllowsChildren(), to make a well-constructed tree possible.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SessionNode extends DefaultMutableTreeNode {
	
	public static final int TYPE_UNKNOWN = 0;
	
	public static final int TYPE_CONTEXTMAP = 1;
	
	public static final int TYPE_CONTRIBUTION = 2;
	
	public static final int TYPE_SESSION = 3;
		
	public static final int TYPE_ROOT = 4;
	
	public int type;

	public SessionNode(Object userObject, int type) {
		super(userObject);
		this.type = type;
	}
	
	/**
	 * @return Returns true if the node can be loaded because of his type (map
	 *         or contribution).
	 */
	public boolean isLoadable() {
		return ((type == TYPE_CONTEXTMAP) || (type == TYPE_CONTRIBUTION));
	}
	
	/**
	 * @return Returns meta-data in the form of a model. To be used in popups,
	 *         forms, etc.
	 */
	public Model getMetaData() {
		Model model = null;
		
		if (type == TYPE_CONTEXTMAP) {
			ContextMap map = (ContextMap) getUserObject();
			String loadContainerURI = map.getLoadContainer();
			Container loadContainer = map.getComponentManager().getContainer(URI.create(loadContainerURI));
			model = (Model) loadContainer;
		} else if (type == TYPE_CONTRIBUTION) {
			MetaDataCache cache = ConzillaKit.getDefaultKit().getMetaDataCache();
			String uri = getUserObject().toString();
			if (cache.isCached(uri)) {
				CollaborillaDataSet dataSet = cache.getDataSet(uri, null);
				String metadata = dataSet.getMetaData();
				StringReader sr = new StringReader(metadata);
				model = new ModelMem();
				model.read(sr, getURI());
			}
		} else if (type == TYPE_SESSION) {
			model = new ModelMem();
			Resource resource = model.createResource(getURI());
			Session session = (Session) getUserObject();
			model.add(model.createStatement(resource, model.createProperty(CV.title), session.getTitle()));
		}
		
		return model;
	}
	
	/**
	 * @return Returns the URI of the node's user object.
	 */
	public String getURI() {
		String uri = null;
		if (getUserObject() instanceof ContextMap) {
			uri = ((ContextMap) getUserObject()).getURI();
		} else if (getUserObject() instanceof Session) {
			uri = ((Session) getUserObject()).getURI();
		} else if (type == TYPE_CONTRIBUTION) {
			uri = getUserObject().toString();
		}
		return uri;
	}

	/**
	 * If the UserObject is a Context-map or Session, the titles are returned.
	 * In other cases the value of toString() is taken.
	 * 
	 * @see javax.swing.tree.DefaultMutableTreeNode#toString()
	 */
	public String toString() {
		String title;
		if (getUserObject() instanceof ContextMap) {
			title = AttributeEntryUtil.getTitleAsString((ContextMap) getUserObject());
			if (title == null) {
				title = "(No Title)";
			}
		} else if (getUserObject() instanceof Session) {
			title = ((Session) getUserObject()).getTitle();
		} else if (type == TYPE_CONTRIBUTION) {
			title = getTitleOfContribution(getUserObject().toString());
			if (title == null) {
				title = getUserObject().toString();
				if (title.length() > 35) {
					title = title.substring(0, 34) + "...";
				}
			}
		} else {
			title = getUserObject().toString();
		}
		return title;
	}
	
	private String getTitleOfContribution(String uri) {
		String title = null;
		MetaDataCache cache = ConzillaKit.getDefaultKit().getMetaDataCache();
		if (cache.isCached(uri)) {
			CollaborillaDataSet dataSet = cache.getDataSet(uri, null);
			String rdfInfo = dataSet.getMetaData();
			title = AttributeEntryUtil.getTitleAsString(rdfInfo, uri);
		}
		return title;
	}

	public boolean isLeaf() {
		return ((type == TYPE_CONTEXTMAP) || (type == TYPE_CONTRIBUTION));
	}

	public boolean getAllowsChildren() {
		return ((type == TYPE_ROOT) || (type == TYPE_SESSION));
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}