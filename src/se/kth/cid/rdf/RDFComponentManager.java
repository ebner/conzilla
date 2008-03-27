/* $Id$ */
/*
 This file is part of the Conzilla browser, designed for
 the Garden of Knowledge project.
 Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package se.kth.cid.rdf;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.collaboration.CollaborillaReader;
import se.kth.cid.collaboration.CollaborillaSupport;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ComponentFactory;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.UndoManager;
import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.rdf.layout.RDFConceptMap;
import se.kth.cid.tree.generic.MemTreeTagManager;
import se.kth.cid.util.TagManager;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFComponentManager implements ComponentManager {

	public static final Log LOG = LogFactory.getLog(RDFComponentManager.class);
	
	private Session currentSession;
	private int revision = CollaborillaReader.LATEST_REVISION;
	private ComponentFactory componentFactory;
	private URI uri;
	private CollaborillaDataSet collaborillaDataSet;
	private Object lockedByObject;
	private TagManager tagManager;
	private Set relevantModels;

	private boolean collaborillaCheckNeeded = true;

	private boolean collaborative;

	private RDFUndoManager undoManager;

	public RDFComponentManager(ComponentFactory cf, URI componentURI, TagManager tagManager, boolean collaborative) {
		this.collaborative = collaborative;
		this.componentFactory = cf;
		this.uri = componentURI;
		if (tagManager != null) {
			this.tagManager = tagManager;
		} else {
			this.tagManager = new MemTreeTagManager(null);
		}
		relevantModels = new HashSet();
	}
	
	public boolean isCollaborative() {
		return collaborative;
	}
	
	public void setCollaborative(boolean collaborative) {
		this.collaborative = collaborative;
	}

	public TagManager getTagManager() {
		return tagManager;
	}
	
	public CollaborillaDataSet getCollaborillaDataSet() {
		if (!collaborative) {
			return null;
		}
		
		if (collaborillaCheckNeeded) {
			collaborillaCheckNeeded = false;
			CollaborillaSupport cs = new CollaborillaSupport(ConfigurationManager.getConfiguration());
			CollaborillaReader collaborillaReader = new CollaborillaReader(cs);
			collaborillaDataSet = collaborillaReader.getDataSet(uri, CollaborillaReader.LATEST_REVISION);

			if (collaborillaDataSet != null) {
				Set rcs = collaborillaDataSet.getRequiredContainers();
				if (rcs != null) {
					Iterator reqs = rcs.iterator();
					while (reqs.hasNext()) {
						String req = (String) reqs.next();
						containerIsRelevant(URI.create(req));
					}
				}
				Set ocs = collaborillaDataSet.getOptionalContainers();
				if (ocs != null) {
					Iterator opts = ocs.iterator();
					while (opts.hasNext()) {
						String opt = (String) opts.next();
						containerIsRelevant(URI.create(opt), false);
					}
				}
			}
		}
		return collaborillaDataSet;
	}

	public void refresh() {
//		for (Iterator iter = relevantModels.iterator(); iter.hasNext();) {
//			URI curi = (URI) iter.next();
//			tagManager.removeTag(curi);
//		}
//		relevantModels = new HashSet();
		
		collaborillaDataSet = null;
		collaborillaCheckNeeded = true;
		//TODO remove all tags first.
		getCollaborillaDataSet();
		refreshRelevantContainers();
	}
	
	public void containerIsRelevant(URI containerURI) {
		containerIsRelevant(containerURI, true);
	}
	
	private void containerIsRelevant(URI containerURI, boolean visible) {
		if (!relevantModels.contains(containerURI)) {
			relevantModels.add(containerURI);
			tagManager.addTag(containerURI);
			tagManager.setTagVisibleSilently(containerURI, visible);
		}
	}
	
	public void refreshRelevanceForContainer(URI containerURI) {
		//TODO remove tag containerURI first!
		refreshRelevanceForContainer((RDFModel) getContainer(containerURI));
	}
	
	public Container getContainer(URI containerURI) {
		if (!tagManager.hasTag(containerURI)) {
			return null;
		}
		if (revision == CollaborillaReader.LATEST_REVISION) {
			return getContainerManager().getContainer(containerURI.toString());
		} else {
			//TODO new method in ContainerManager needed.
			return null;
		}
	}

	public ContainerManager getContainerManager() {
		return componentFactory.getContainerManager();
	}
	
	public ComponentFactory getComponentFactory() {
		return componentFactory;
	}

	public boolean getContainerVisible(URI containerURI) {
		return tagManager.getTagVisible(containerURI);
	}

	public Session getEditingSesssion() {
		return currentSession;
	}

	public Set getLoadedRelevantContainers() {
		return getLoadedRelevantContainers(true);
	}
	
	public boolean isLockedForEditing() {
		return currentSession != null;
	}

	public void setContainerVisible(URI containerURI, boolean visible) {
		tagManager.setTagVisible(containerURI, visible);
	}

	public boolean setLockForEditing(Object lockedBy, Session editorSession) {
		boolean success = false;
		if (currentSession == null) {
			this.lockedByObject = lockedBy;
			this.currentSession = editorSession;
			if (editorSession != null) {
				try {
					Component comp = ConzillaKit.getDefaultKit().getResourceStore().getAndReferenceComponent(uri);
					if (undoManager == null && comp instanceof RDFConceptMap && componentFactory instanceof RDFComponentFactory) {
						undoManager = new RDFUndoManager((RDFConceptMap) comp);
					}
				} catch (ComponentException e) {}
				URI cTag = URI.create(editorSession.getContainerURIForConcepts());
				URI lTag = URI.create(editorSession.getContainerURIForLayouts());
				containerIsRelevant(cTag, false);
				containerIsRelevant(lTag, false);
				tagManager.setTagVisible(lTag, true); //When new tag, since we set just defaulted it to false, setting it to true will generate an event.
				tagManager.setTagVisible(cTag, true);
			}
			success = true;
		} else if (this.lockedByObject == lockedBy) {
			this.currentSession = editorSession;
			success = true;
		}
		if (undoManager != null) {
			if (editorSession != null) {
				undoManager.startRecording();
			} else {
				undoManager.stopRecording();
			}
		}
		return success;
	}

	public void useRevision(int revision) {
		this.revision = revision;
	}

	public RDFModel getCurrentConceptContainer() {
		if (currentSession != null) {
			String uri = currentSession.getContainerURIForConcepts();
			Container container = getContainerManager().getContainer(uri);
			if (container instanceof RDFModel) {
				return (RDFModel) container;
			}
			LOG.fatal("Container asked for is not RDFModel, this should not occur.");
			return null;
		} else {
			return (RDFModel) componentFactory.getContainerManager().getCurrentConceptContainer();
		}
	}

	public RDFModel getCurrentLayoutContainer() {
		if (currentSession != null) {
			String uri = currentSession.getContainerURIForLayouts();
			Container container = getContainerManager().getContainer(uri);
			if (container instanceof RDFModel) {
				return (RDFModel) container;
			}
			LOG.fatal("Container asked for is not RDFModel, this should not occur.");
			return null;
		} else {
			return (RDFModel) componentFactory.getContainerManager().getCurrentLayoutContainer();
		}
	}

	public URI createUniqueResourceURI() {
		String nuri = null;
		if (currentSession == null) { 
			nuri = getContainerManager().createUniqueURI(componentFactory.getContainerManager().getBaseURIForConcepts());
		} else {
			nuri = getContainerManager().createUniqueURI(currentSession.getBaseURIForConcepts());
		}
		return URI.create(nuri);
	}
	
	public URI createUniqueLayoutURI() {
		String nuri = null;
		if (currentSession == null) { 
			nuri = getContainerManager().createUniqueURI(componentFactory.getContainerManager().getBaseURIForLayout());
		} else {
			nuri = getContainerManager().createUniqueURI(currentSession.getBaseURIForLayouts());
		}
		return URI.create(nuri);
	}
	
	public void refreshRelevantContainers() {
        //Iterator it = totalModel.getModels().iterator();
        Iterator it = getContainerManager().getContainers().iterator();
        while (it.hasNext()) {
            refreshRelevanceForContainer((RDFModel) it.next());
        }
	}
	
	private void refreshRelevanceForContainer(RDFModel m) {
		Resource subj = m.createResource(this.uri.toString());
        if (m.contains(null, CV.inNodeLayout, subj) 
        		|| m.contains(null, CV.inContextMap, subj) 
        		|| (m.contains(subj,(Property) null, (RDFNode) null))) {
        	containerIsRelevant(URI.create(m.getURI()));
        }
	}
	
	private Set getLoadedRelevantContainers(boolean asURIs) {
		HashSet set = new HashSet();
		Iterator it = relevantModels.iterator();
		while(it.hasNext()) {
			URI containerURI = (URI) it.next();
			Container cont = getContainer(containerURI);
			if (cont != null) {
				if (asURIs) { 
					set.add(containerURI);
				} else {
					set.add(cont);
				}
			}
		}
		return set;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}
}