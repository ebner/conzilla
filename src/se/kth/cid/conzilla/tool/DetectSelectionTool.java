/*  $Id: $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.Set;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.layers.MoveLayer;
import se.kth.cid.conzilla.edit.layers.handles.HandleStore;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/** 
 * Use this class when to work with selected layouts either through single or 
 * multiple selection in a manner that works both in a context menue or a menu in the menubar.
 * It can also be configured to react on the map itself.
 * 
 *  @author Matthias Palmer
 */
public abstract class DetectSelectionTool extends Tool {
	
	
    protected boolean enabledOnMap;

	public DetectSelectionTool(String name, String resBundle, 
    		MapController controller, boolean enabledOnMap) {
        super(name, resBundle, controller);
        setEnabled(true);
        this.enabledOnMap = enabledOnMap;
    }

    protected boolean updateEnabled() {
    	if (mapEvent != null) {
    		if (enabledOnMap) {
    			return true;
    		} else {
    			return mapEvent.hitType != MapEvent.HIT_NONE;
    		}
    	} else {
        	HandleStore handleStore = ((EditMapManager) controller.getManager()).getHandleStore();
        	MoveLayer moveLayer = ((EditMapManager) controller.getManager()).moveLayer;
        	MapObject mo = moveLayer.getHandledObject() != null ? moveLayer.getHandledObject().getMapObject() : null;
            return (!handleStore.getMarkedLayouts().isEmpty()) || mo!= null;
    	}
    }

    public void actionPerformed(ActionEvent e) {
    	if (!updateEnabled()) {
    		return;
    	}
		HandleStore handleStore = ((EditMapManager) controller.getManager()).getHandleStore();
		Set markedLayouts = handleStore.getMarkedLayouts();
		if (!markedLayouts.isEmpty()) {
			if (markedLayouts.size() > 1) {
				handleMultipleSelection(markedLayouts);
			} else {
				ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
				DrawerLayout dl = (DrawerLayout) markedLayouts.iterator().next();
				try {
					handleSingleSelection(dl, 
							store.getAndReferenceConcept(URI.create(dl.getConceptURI())));
				} catch (ComponentException e1) {
				}
			}
		} else {
			if (mapEvent != null) {
				if (enabledOnMap && mapEvent.hitType == MapEvent.HIT_NONE) {
					handleMap(controller.getConceptMap());
				} else {
					handleSingleSelection(this.mapObject.getDrawerLayout(), this.mapObject.getConcept());
				}
    		} else {
    			MoveLayer moveLayer = ((EditMapManager) controller.getManager()).moveLayer;
            	MapObject mo = moveLayer.getHandledObject().getMapObject();
    			handleSingleSelection(mo.getDrawerLayout(), mo.getConcept());
    		}
    	}
    }
 
    abstract protected void handleSingleSelection(DrawerLayout drawerLayout, Concept concept);
    
    abstract protected void handleMultipleSelection(Set drawerLayouts);
    
    abstract protected void handleMap(ContextMap map);
}