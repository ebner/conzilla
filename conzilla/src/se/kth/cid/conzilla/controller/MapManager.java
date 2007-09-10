/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.controller;

import javax.swing.JComponent;

import se.kth.cid.conzilla.map.MapScrollPane;


/**
 * This interface manages interactions by means of tools, layers, popups, forms etc. 
 * around a ContextMap in a MapController and the corresponding View.
 * 
 * @author Mikael Nilsson
 * @author Matthias Palmer
 * @version $Revision$
 * @see MapManagerFactory
 * @see MapController
 */
public interface MapManager {

    /**
     * Installs all tools, pushes layers, adds listeners etc.
     */
	void install();

	/**
	 * Uninstalls tools, pops layers, removes listeners etc.
	 *
	 */
    void deInstall();
    
    /**
     * When a View gets focus, certain tools, e.g. free floating tools, need to be told who has the current focus.
     * Currently only used for correct session management in editing mode.
     */
    void gotFocus();
    
    /**
     * Embedds the map in a component to make additional room for interaction primitives. 
     * To be used with care, only when existing toolbars and menus are insufficient should the space
     * reserved for the map be used for other things.
     * 
     * @param mapPane the component where the map is shown, it is already scrollable and layered.
     * @return a new JComponent where the mapPane has been embedded somehow, if no embedding is to be done, 
     * return the mapPane directly. Null is not allowed.
     */
    JComponent embeddMap(MapScrollPane mapPane);
}