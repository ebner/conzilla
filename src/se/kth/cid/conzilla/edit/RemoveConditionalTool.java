/*  $Id: RemoveDrawerMapTool.java 1199 2008-01-29 15:57:08Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.net.URI;
import java.util.HashSet;
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
 * Removes concept apperance from the map and the concept itself if:
 * 1) It has no other known apperances.
 * 2) It is expressed in the current session.
 * 
 *  @author Matthias Palmer
 *  @version $Revision: 1199 $
 */
public class RemoveConditionalTool extends DetectSelectionTool {
    
    public RemoveConditionalTool(MapController cont) {
        super("REMOVE_CONDITIONAL", EditMapManagerFactory.class.getName(), cont, false);
    }

    private boolean isManaged(Concept concept) {
        Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
        String lc = concept.getLoadContainer();
        return session.getContainerURIForConcepts().equals(lc)
                || session.getContainerURIForLayouts().equals(lc);
    }

    private boolean isCommented(Concept concept) {
    	Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
    	Set<URI> set = concept.getComponentManager().getLoadedRelevantContainers();
    	return set.contains(URI.create(session.getContainerURIForConcepts()));
    }

	/**
	 * You are trying to remove "+ nrOfOccurences+" concepts completely.
	 * If you proceed, note that:
     * 1) Selected concepts will be visibly removed from this map (if added in this session).
     * 2) Selected concepts will be removed if they belong to this session
     *    unless they are used in other maps. (Usage in other maps is only
     *    detected within currently loaded sessions.)
     *    Furthermore, only concepts residing in this session can be removed.
     * 3) If a concept have been removed even though it is used in another map,
     *    it will appear as a blank ellipse.
	 */
	protected void handleMultipleSelection(Set drawerLayouts) {
		Object[] options = {"Cancel", "Remove"};
		int nrOfOccurences = drawerLayouts.size();
        int result = JOptionPane.showOptionDialog(
                controller.getView().getMapScrollPane().getDisplayer(),
                "You are trying to remove "+ nrOfOccurences+" selected concepts together with their \n" +
                "apperances in this map. Note that concepts that have known apperances in other maps\n" +
                "will not be removed.\n\n" +
                "Do you want to proceed and remove "+nrOfOccurences+" selected concepts where possible?",
                "Remove " + nrOfOccurences + " concepts?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[1]);
         if (result == 0) {
             return;
         }
         controller.getConceptMap().getComponentManager().getUndoManager().startChange();
         ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
         ContainerManager cMan = store.getContainerManager();
         Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
         Container container = cMan.getContainer(session.getContainerURIForConcepts());
 
         HashSet concepts = new HashSet();
         for (Iterator dls = drawerLayouts.iterator(); dls.hasNext();) {
             DrawerLayout dl = (DrawerLayout) dls.next();
             try {	
            	 Concept c = store.getAndReferenceConcept(URI.create(dl.getConceptURI()));
            	 dl.remove();
            	 if (c != null) {
                     concepts.add(c);
            	 }
             } catch (ComponentException ce) {}
         }
         for (Iterator cs = concepts.iterator(); cs.hasNext();) {
        	 Concept concept = (Concept) cs.next();
        	 if (cMan.isComponentReferredTo(concept) == 0 && isManaged(concept)) {
        		 concept.removeFromContainer(container);
        	 }
         }
         controller.getConceptMap().getComponentManager().getUndoManager().endChange();
	}

	@Override
	protected void handleSingleSelection(DrawerLayout drawerLayout, Concept concept) { 
		ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
		ContainerManager cMan = store.getContainerManager();
		Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
		Container container = cMan.getContainer(session.getContainerURIForConcepts());
		int referredTo = concept != null ? cMan.isComponentReferredTo(concept)-1 : 0;
		boolean isManaged = isManaged(concept);

		String name = concept.getTriple() != null ? "concept-relation" : "concept";
		Object[] options = {"Cancel", "Remove"};
		int result = 0;
		boolean removeConcept = false;
		if (!isManaged) {
			//Not remove concept since authored elsewhere.
			if (isCommented(concept) && referredTo == 0) {
				result = JOptionPane.showOptionDialog(
					controller.getView().getMapScrollPane().getDisplayer(),
					"You are about to remove a " + name + "s apperance from this map.\n" +
					"The "+name+" cannot be removed in itself as it belongs to another session.\n" +
					"However, the additional information provided on the "+ name+" in this session will be removed.\n\n" +
					"Do you want to proceed?",
					"Remove " + name + "?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, options, options[1]);
				removeConcept = true;
			} else {
				result = JOptionPane.showOptionDialog(
						controller.getView().getMapScrollPane().getDisplayer(),
						"You are about to remove a " + name + "s apperance from this map.\n" +
						"The "+name+" cannot be removed in itself as it belongs to another session.\n\n" +
						"Do you want to proceed?",
						"Remove " + name + "s apperance?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, options, options[1]);				
			}
		} else if (referredTo != 0) {
			//Not remove since used elsewhere.
			result = JOptionPane.showOptionDialog(
					controller.getView().getMapScrollPane().getDisplayer(),
					"You are about to remove a " + name + "s apperance from this map.\n" +
					"The "+name+" in itself will not be removed as it is used in other maps.\n\n" +
					"Do you want to proceed?",
					"Remove " + name + "s apperance?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, options, options[1]);
		} else {
			//Remove
			//TODO check text below
			result = JOptionPane.showOptionDialog(
					controller.getView().getMapScrollPane().getDisplayer(),
					"You are about to remove a " + name + " and its apperance from this map.\n" +
					"The " + name + " itself is going to be removed as there is no indication of it being\n" +
					"used in another map. However, only maps in currently loaded sessions are checked.\n\n" +
					"Proceed and remove "+name+"?",
					"Remove " + name + "?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, options, options[1]);
			removeConcept = true;
		}

		if (result == 0) {
			return;
		}
		controller.getConceptMap().getComponentManager().getUndoManager().startChange();
		drawerLayout.remove();
		if (removeConcept) {
			concept.removeFromContainer(container);
		}
		controller.getConceptMap().getComponentManager().getUndoManager().endChange();
	}
	
	@Override
	protected void handleMap(ContextMap map) {
		// Not used
	}
}