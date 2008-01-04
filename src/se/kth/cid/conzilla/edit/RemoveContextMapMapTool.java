/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.JOptionPane;

import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.conzilla.app.ConzillaEnvironment;
import se.kth.cid.conzilla.controller.ControllerException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.history.LinearHistory;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * Tool for removing the current ContextMap.
 * 
 * @author matthias
 */
public class RemoveContextMapMapTool extends ActionMapMenuTool {
    //EditMapManager editMapManager;
    
    public RemoveContextMapMapTool(MapController cont) {
        super("REMOVE_CONTEXT_MAP", EditMapManagerFactory.class.getName(), cont);
    }

    /**
     * Only enable the removal of a Context-map if its loadContainer is the layoutContainer
     * of the current session.

     * @see se.kth.cid.conzilla.tool.ActionMapMenuTool#updateEnabled()
     */
    protected boolean updateEnabled() {
        ContextMap cMap = controller.getConceptMap();
        Session session = cMap.getComponentManager().getEditingSesssion();
        return session.getContainerURIForLayouts().equals(cMap.getLoadContainer());
    }

    /**
     * Asks to remove the current Context-map, it does not remove any contained concepts.
     
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ContextMap cMap = controller.getConceptMap();
        boolean hasConcepts = cMap.getDrawerLayouts().length != 0; 
        String title = AttributeEntryUtil.getTitleAsString(cMap); 
		int answer = JOptionPane.showConfirmDialog(null, "Remove the Context-map named "
				+ (title != null ? title : "no title") + "?"
				+ (hasConcepts ? " from the current session?\n" +
						"Observe that concepts added to the Context-map in this session \n" +
						"will not be removed. You should consider removing these concepts\n " +
						"before you go ahed if they are not used elsewhere." : "")
						, "Continue and remove ConceptMap from this session?", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_NO_OPTION) {
			ComponentManager cMan = controller.getConceptMap().getComponentManager();
			Container container = cMan.getContainer(URI.create(cMan.getEditingSesssion().getContainerURIForLayouts()));
        	String loadContainerURI = cMap.getLoadContainer();
			cMap.removeFromContainer(container);
			if (loadContainerURI.equals(container.getURI())) {
				LinearHistory lh = controller.getLinearHistory();
				lh.removeHistoryEvent(lh.getIndex());
				try {
					URI nMapURI = URI.create(ConzillaEnvironment.DEFAULT_BLANKMAP);
					controller.showMap(nMapURI);
					controller.getHistoryManager().fireOpenNewMapEvent(controller, null, nMapURI);
				} catch (ControllerException e1) {
					e1.printStackTrace();
				}
			}
        }
    }
}

