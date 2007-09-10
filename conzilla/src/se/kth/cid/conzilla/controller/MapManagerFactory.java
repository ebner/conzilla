/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.controller;

import java.net.URI;

import se.kth.cid.component.ComponentManager;
import se.kth.cid.conzilla.app.Extra;
import se.kth.cid.conzilla.browse.BrowseMapManagerFactory;
import se.kth.cid.conzilla.edit.EditMapManagerFactory;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.conzilla.view.View;
import se.kth.cid.layout.ContextMap;

/** 
 * A {@link MapController} and a corresponding {@link View} can present a {@link ContextMap}.
 * But, if any kind of interaction is required with the ContextMap, 
 * for example when browsing or editing, dynamicity need to be added in the forms 
 * of popups, metadata forms, menues, buttons, handles for manipulating the map etc.
 * The dynamicity is managed by a {@link MapManager} which is instantiated from this interface, 
 * the MapManagerFactory. 
 * A range of different MapManagerFactories, should be considered to correspond to a 
 * range of different interactions styles (modes), that can be switched between.
 * Examples of MapManagerFactories are {@link BrowseMapManagerFactory} and {@link EditMapManagerFactory}.
 *
 * @author Mikael Nilsson
 * @author Matthias Palmer
 * @version $Revision$
 * @see MapController#changeMapManager(MapManagerFactory)
 */
public interface MapManagerFactory extends Extra {
    
	/**
	 * A specific context-map in a specific mapController is not always 
	 * managable by a MapManagerFactory, this method answers that question.
	 * For example, a map that is read only can perhaps not be managed by a EditMapManagerFactory.
	 * 
	 * @param controller the MapController to manage the context-map in.
	 * @param map the Context-map active in the mapController now.
	 * @return true if the MapManagerFactory can manage the given ContextMap in the given MapController.
	 */
	boolean canManage(MapController controller, URI map);

	/**
	 * Creates a MapManager which will add {@link Tool}s in menus and toolbars as well as popups 
	 * and layers (in the {@link MapScrollPane#}) in the map.
	 * 
	 * @param controller the mapController that will be managed.
	 * @return a new MapManager
	 * @see MapManager#install()
	 * @see MapManager#deInstall()
	 */
    MapManager createManager(MapController controller);
    
    /**
     * @return true if a MapManager of this factory will require a session to be set on the 
     * {@link ComponentManager} of the {@link ContextMap} to work.
     */
    boolean requiresSession();
}
