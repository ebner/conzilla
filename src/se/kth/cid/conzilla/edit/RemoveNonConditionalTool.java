/*  $Id: StoreEditMapTool.java 1199 2008-01-29 15:57:08Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.Container;
import se.kth.cid.component.ContainerManager;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.tool.DetectSelectionTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;

/** 
 * Removes selected concepts appearance (layouts) in this map.
 * The concepts themselves are not removed.
 * 
 *  @author Matthias Palmer
 */
public class RemoveNonConditionalTool extends DetectSelectionTool {
	
    public RemoveNonConditionalTool(MapController cont) {
        super("REMOVE_NON_CONDITIONAL", EditMapManagerFactory.class.getName(), cont, false);
    }
    
	/**
	 * You are trying to remove "+ nrOfOccurences+" concepts completely.
	 * If you proceed, selected concepts will be removed along with
	 * their visible representations in this map.
	 * There is two exceptions though:
	 * 1) A concept cannot be removed if it belongs to someone else (another session).
   	 *    Any additional information provided in this session for the concept will
     *    be removed though.
 	 * 2) A concepts visual representation in this map cannot be removed if it was
     *    added by someone else (someone else have made a contribution to this map).
     *    
     * Note that only the selected visual representations in this map are removed.
	 * Potential visual representations in other maps of removed concepts, will
	 * appear as blank ellipses.
	 */
	protected void handleMultipleSelection(Set dls) {
		int nrOfOccurences = dls.size();
    	Object[] options = {"Cancel", "Remove"};
        if (JOptionPane.showOptionDialog(
                mcontroller.getView().getMapScrollPane().getDisplayer(),
                "You are about to remove "+ nrOfOccurences+" selected concepts together with their \n" +
                "apperances in this map. Note that concepts originating from other sessions\n" +
                "cannot be removed. Warning, if the selected concepts appear in other maps they\n" +
                "will appear as blank ellipses after removal.\n\n" +
                "Do you want to proceed and remove "+nrOfOccurences+" selected concepts?",
                "Remove " + nrOfOccurences + " concepts?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[1]) == 1) {		
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().startChange();
        	ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        	ContainerManager cMan = store.getContainerManager();
            Session session = mcontroller.getConceptMap().getComponentManager().getEditingSesssion();
            Container container = cMan.getContainer(session.getContainerURIForConcepts());
            for (Iterator dlsIt = dls.iterator(); dlsIt.hasNext();) {
        		DrawerLayout dl = (DrawerLayout) dlsIt.next();
        		try {	
        			Concept c = store.getAndReferenceConcept(URI.create(dl.getConceptURI()));
        			dl.remove();
        			if (c != null) {
        				c.removeFromContainer(container);
        			}
        		} catch (ComponentException ce) {}
        	}
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().endChange();
        }
	}
	
    private boolean isManaged(Concept concept) {
        Session session = mcontroller.getConceptMap().getComponentManager().getEditingSesssion();
        String lc = concept.getLoadContainer();
        return session.getContainerURIForConcepts().equals(lc)
                || session.getContainerURIForLayouts().equals(lc);
    }


	@Override
	protected void handleSingleSelection(DrawerLayout drawerLayout, Concept concept) {
		ContainerManager cMan = ConzillaKit.getDefaultKit().getResourceStore().getContainerManager();
    	ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        Session session = mcontroller.getConceptMap().getComponentManager().getEditingSesssion();
        Container container = cMan.getContainer(session.getContainerURIForConcepts());
        String name = concept.getTriple() != null ? "concept-relation" : "concept";
        Object[] options = {"Cancel", "Remove"};
        int result = 0;
        if (isManaged(concept)) {
        	result = JOptionPane.showOptionDialog(
        			mcontroller.getView().getMapScrollPane().getDisplayer(),
        			"You are about to remove a " + name + " and its apperance in this map.\n"+
        			"If the "+name+" is used in another map, its apperance there will remain\n" +
        			"but appear as a blank ellipse.\n\n" +
        			"Do you want to proceed and remove this " + name + "?\n" +
        			"Remove " + name + "?",
        			"Remove " + name + "?",
        			JOptionPane.YES_NO_OPTION,
        			JOptionPane.QUESTION_MESSAGE,
        			null, options, options[1]);
        } else {
        	result = JOptionPane.showOptionDialog(
        			mcontroller.getView().getMapScrollPane().getDisplayer(),
        			"You are about to remove a "+name+"s apperance from this map.\n"+
        			"The concept itself cannot be removed as it belongs to another session.\n\n" +
        			"Proceed and remove this " + name + "s apperance from this map?",
        			"Remove " + name + "s apperance from this map?",
        			JOptionPane.YES_NO_OPTION,
        			JOptionPane.QUESTION_MESSAGE,
        			null, options, options[1]);
        }
        if ( result == 1) {
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().startChange();		
        	drawerLayout.remove();
        	concept.removeFromContainer(container);
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().endChange();
        }
	}

	@Override
	protected void handleMap(ContextMap map) {
		//Not used
	}
}