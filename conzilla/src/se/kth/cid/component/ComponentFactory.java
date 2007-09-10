/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.component;

import java.net.URI;

import se.kth.cid.concept.Concept;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.tree.TreeTagNodeResource;

/**
 * @author Matthias
 */
public interface ComponentFactory {

	// Integers should move to the implementation of the ComponentManager

	// An int meaning Component
	int COMPONENT = 0;

	// An int meaning Concept
	int CONCEPT = 2;

	// An int meaning ConceptMap
	int CONTEXTMAP = 3;

	
	ContextMap loadContextMap(URI uri, boolean collaborative) throws ComponentException;
	
	Concept loadConcept(URI uri) throws ComponentException;
	
	/**
	 * Loads the specified component.
	 * 
	 * @param uri
	 *            the URI from which to load the component.
	 * @return the loaded component. Never null.
	 * @exception ComponentException
	 *                if anything went wrong while loading the component.
	 */
	Component loadComponent(URI uri) throws ComponentException;

	TreeTagNodeResource loadTree(URI uri) throws ComponentException;

	/**
	 * Creates a component. Note that creation fails if the component already
	 * exists.
	 * 
	 * @param uri
	 *            the URI where to create the component.
	 * @return the created component. Never null.
	 * @exception ComponentException
	 *                if anything went wrong while creating the component.
	 */
	Component createComponent(URI uri) throws ComponentException;

	Concept createConcept(URI uri) throws ComponentException;

	ContextMap createContextMap(URI uri) throws ComponentException;

	/**
	 * Tries to save the component.
	 * 
	 * @param comp
	 *            the component that is to be saved.
	 * @exception ComponentException
	 *                if anything went wrong when saving the component.
	 */
	void saveResource(Resource comp) throws ComponentException;

	void setReflectToTriple(boolean trip);

	ResourceLayout loadResourceLayout(ResourceLayout owner, URI uri);
	
	ContainerManager getContainerManager();

}