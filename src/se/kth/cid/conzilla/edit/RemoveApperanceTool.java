/*  $Id: StoreEditMapTool.java 1199 2008-01-29 15:57:08Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.tool.DetectSelectionTool;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

/** 
 * Removes a concepts appearance (layouts) in this map.
 * The concept itself is not removed.
 * 
 *  @author Matthias Palmer
 */
public class RemoveApperanceTool extends DetectSelectionTool {
	
    public RemoveApperanceTool(MapController cont) {
        super("REMOVE_APPERANCE", EditMapManagerFactory.class.getName(), cont, false);
    }

	@Override
	protected void handleMultipleSelection(Set dls) {
	  	int nrOfOccurences = dls.size();
		Object[] options = {"Cancel", "Remove"};
        if (JOptionPane.showOptionDialog(
                mcontroller.getView().getMapScrollPane().getDisplayer(),
                "You are trying to remove "+ nrOfOccurences+" concept from this map. \n" +
                "Note that the concepts themselves will not be removed, only the selected\n" +
                "apperances in this map. Hence, unless they appear elsewhere in this or\n" +
                "another map you cannot easily get a hold of them again.\n\n" +
                "Do you want to proceed and remove "+nrOfOccurences+" concept from this map?",
                "Remove " + nrOfOccurences + " concepts from this map?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[1]) == 1) {		
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().startChange();
        	for (Iterator dlsIt = dls.iterator(); dlsIt.hasNext();) {
        		((DrawerLayout) dlsIt.next()).remove();
        	}
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().endChange();
        }
	}

	@Override
	protected void handleSingleSelection(DrawerLayout drawerLayout, Concept concept) {
        String name = drawerLayout instanceof StatementLayout ? "concept-relation" : "concept";
		Object[] options = {"Cancel", "Remove"};
        if (JOptionPane.showOptionDialog(
                mcontroller.getView().getMapScrollPane().getDisplayer(),
                "You are trying to remove a "+ name +" from this map. \n" +
                "Note that the "+name+" itself will not be removed. Hence,\n" +
                "unless it appears elsewhere in this or another map you cannot easily\n" +
                "get a hold of it again.\n\n" +
                "Do you want to proceed and remove the "+name+" from this map?",
                "Remove " + name + " from this map?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[1]) == 1) {		
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().startChange();
        	drawerLayout.remove();
        	mcontroller.getConceptMap().getComponentManager().getUndoManager().endChange();
        }
	}

	@Override
	protected void handleMap(ContextMap map) {
		//Not used
	}
}