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

package se.kth.cid.component;

import java.net.URI;
import java.util.Set;

import se.kth.cid.conzilla.session.Session;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.TagManager;
import se.kth.nada.kmr.collaborilla.client.CollaborillaDataSet;

/**
 * Keeps track of all containers that are relevant for the component.
 * Exactly how this list is maintained is specific for the implementation.
 * Containers can be marked as invisible for a different user experience.
 * 
 * The manager also makes sure that there is only one editing party of the 
 * contextMap at a time (in the same running Conzilla) in a specific session.
 * 
 * @author Matthias
 */
public interface ComponentManager {
	
	/**
	 * A reference to a TagManager where all relevantContainers
	 * are treated as tags.
	 * 
	 * @return the TagManager, may be the same for several ComponentManagers, never null.
	 */
	TagManager getTagManager();
	
	/**
	 * @return an UndoManager only relevant if the managed Component is a ContextMap.
	 */
	UndoManager getUndoManager();
	
	/**
	 * A reference to the containerManager.
	 * @return the ContainerManager, never null.
	 */
	ContainerManager getContainerManager();
	
	/**
	 * A reference to the ComponentFactory of this component.
	 * @return the {@link ComponentFactory}, never null.
	 */
	ComponentFactory getComponentFactory();
	
	/**
	 * Refreshes information about the component from Collaborilla.
	 */
	void refresh();
	
	/**
	 * Determines whether this ComponentManager was loaded with collaborative support enabled.
	 * 
	 * @return True if the ComponentManager talks to the Collaboration service.
	 */
	boolean isCollaborative();
	
	/**
	 * Turns collaborative support on or off.
	 * 
	 * @param collaborative Collaborative state.
	 */
	void setCollaborative(boolean collaborative);

	/**
	 * Tells that a given container is relevant, no questions asked.
	 * @param containerURI
	 */
	void containerIsRelevant(URI containerURI);

	/**
	 * Investigates wether the container is relevant for the component or not,
	 * if so, it is made relvant.
	 * @param containerURI
	 */
	void refreshRelevanceForContainer(URI containerURI);
	
	/**
	 * This is the dataset from collaborilla for this component (artifact).
	 * @return a CollaborillaDataSet if the component is published, otherwise null.
	 */
	CollaborillaDataSet getCollaborillaDataSet();

	/**
	 * Tells which revision of the component to look at.
	 * @param revision the revision to use
	 * TODO inform how to get a hold of available revisions from collaborilla.
	 */
	void useRevision(int revision);
	
	/**
	 * A list of loaded containers that are relevant for this component.
	 * With relevant we mean both required and optional in the dataset.
	 * If the component is not published, the relevance is determined in the
	 * {@link ContainerManager#loadContainer(URI, URI, boolean, se.kth.cid.component.cache.ComponentCache)} 
	 * by checking for references to known components.
	 * 
	 * @return a List of {@link URI}s for every loaded {@link Container}.
	 */
	Set<URI> getLoadedRelevantContainers();
	
	/**
	 * @param containerURI the {@link URI} for the container required-
	 * @return a Container that fits with the version of the component.
	 * @see #useRevision(int)
	 */
	Container getContainer(URI containerURI);

	/**
	 * Makes the specified container invisible for the current component.
	 * @param containerURI
	 * @param visible
	 */
    void setContainerVisible(URI containerURI, boolean visible);
    
    /**
     * @param containerURI {@link URI} of the {@link Container} to check visibility for.
     * @return true if the container is visible.
     * @see #setContainerVisible(URI, boolean)
     */
    boolean getContainerVisible(URI containerURI);
        
    /**
     * Allows a specific object to locks or unlock a component 
     * by specifying a session (or null for unlocking).
     * I.e. a component can only be edited by one object at a time.
     * Locking should be used carefully. 
     * For exampel, a {@link ContextMap} should be locked when going into edit mode.
     * A component should be locked when a metadata editor is opened on it. 
     * But a concept should not be locked automatically just because it is edited 
     * indirectly in a map that is opened for editing.
     * 
     * @param lockedBy the  the component is locked for.
     * @param editorSession the {@link Session} wherein the component is edited in.
     */
    boolean setLockForEditing(Object lockedBy, Session editorSession);
    
    /**
     * @return true if a object has locked this component 
     * for editing in a specific {@link Session}.
     * @see #setLockForEditing(Object, Session)
     */
    boolean isLockedForEditing();

    /**
     * @return the {@link Session} the MapManager edits the component.
     * @see #setLockForEditing(Object, Session)
     */
    Session getEditingSesssion();
}
