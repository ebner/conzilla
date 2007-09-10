/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import se.kth.cid.component.Component;
import se.kth.cid.component.cache.ComponentCache;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.session.Session;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.layout.DrawerLayout;

/** 
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RemoveDrawerMapTool extends ActionMapMenuTool {

    EditMapManager editMapManager;
    
    
    public RemoveDrawerMapTool(MapController cont, EditMapManager mapManager) {
        super("REMOVE_CONCEPT", EditMapManagerFactory.class.getName(), cont);
        this.editMapManager = mapManager;
    }

    protected boolean updateEnabled() {
        if (mapEvent.hitType != MapEvent.HIT_NONE
            && mapEvent.mapObject.getDrawerLayout().isEditable())
            return true;
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        Set drawerLayouts = editMapManager.getHandleStore().getMarkedLayouts(); 
        if (drawerLayouts != null && drawerLayouts.size() > 1) {
            int result = confirmMultipleRemove(drawerLayouts.size());
            if ( result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            ComponentCache cache = ConzillaKit.getDefaultKit().getResourceStore().getCache();
            HashSet concepts = new HashSet();
            for (Iterator dls = drawerLayouts.iterator(); dls.hasNext();) {
                DrawerLayout dl = (DrawerLayout) dls.next();
                Component c = cache.getComponent(dl.getConceptURI());
                if (c != null && c instanceof Concept) {
                    concepts.add(c);
                }
                dl.remove();
            }
            if (result == JOptionPane.YES_OPTION) {
                for (Iterator cs = concepts.iterator(); cs.hasNext();) {
                    Concept concept = (Concept) cs.next();
                    remove(concept, concept.isReferredTo(), isManaged(concept));
                }
            }
        } else {
            Concept concept = mapObject.getConcept();
            int referredTo = concept != null ? concept.isReferredTo() : 0;
            boolean isManaged = isManaged(concept);
            int result = confirmSingleRemove(concept, referredTo, isManaged);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            mapObject.getDrawerLayout().remove();
            if (result == JOptionPane.YES_OPTION) {
                remove(concept, referredTo-1, isManaged);
            }
        }    
    }

    /**
     * Removes the concept if:
     * <ul><li> The concept is not referenced in any of the loaded containers.</li>
     * <li> The concepts load container is being edited in the current session.</li>
     * </ul>
     * @param concept to be removed.
     * @param referredTo as given by {@link Concept#isReferredTo()}.
     * @param isManaged as given by {@link #isManaged(Concept)}.
     */
    private void remove(Concept concept, int referredTo, boolean isManaged) {
        if (concept != null && referredTo == 0 && isManaged) {
            concept.remove();
        }
    }


    private boolean isManaged(Concept concept) {
        Session session = controller.getConceptMap().getComponentManager().getEditingSesssion();
        String lc = concept.getLoadContainer();
        return session.getContainerURIForConcepts().equals(lc)
                || session.getContainerURIForLayouts().equals(lc);
    }
    
    /**
     * @return {@link JOptionPane#YES_OPTION} to remove concept occurence and concept if possible,
     * {@link JOptionPane#NO_OPTION} to remove concept occurence only and
     * {@link JOptionPane#CANCEL_OPTION} to abort removal.
     */
    private int confirmSingleRemove(Concept concept, int referredTo, boolean managed) {
//        String C = concept.getTriple() != null ? "CONCEPT-RELATION" : "CONCEPT";
        String c = concept.getTriple() != null ? "concept-relation" : "concept";
        if (referredTo == 1 && managed) {
            return JOptionPane.showConfirmDialog(
                    controller.getView().getMapScrollPane().getDisplayer(),
                    "The "+c+" to be removed from this map does not appear in any other maps " +
                    "and can henceforth be deleted altogether.\n\n"+
                    "Do you also want to delete the concept altogether?",
                    "Delete "+c+" altogether?"+c,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        } else {
            int result = JOptionPane.showConfirmDialog(
                    controller.getView().getMapScrollPane().getDisplayer(),
                    "You are about to remove a "+c+" from this map.\n" +
                    "Note: The "+c+" itself will not be deleted "+
                    (referredTo != 1 ? "since it occurs in other maps." 
                            : "since the current session does not\n" +
                                    "have permission to delete it."),
                    "Remove "+c+"s apperance?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            return result == JOptionPane.NO_OPTION ? JOptionPane.CANCEL_OPTION : result;
        }
    }
    
    private int confirmMultipleRemove(int nrOfOccurences) {
        return JOptionPane.showConfirmDialog(
                controller.getView().getMapScrollPane().getDisplayer(),
                "Removing a concept from this context-map does not automatically mean\n" +
                "deleting the concept itself, since this could break it's presentation in other maps.\n"+
                "However, if a concept is not referenced from other maps, and the current \n" +
                "session has enough permissions, the concept may be deleted.\n" +
                "(when we say \"concept\" we intend \"concept or concept-relation\".)\n" +
                "\n" +
                "Do you also want to delete concepts that are not presented in other maps?",
                "Try to remove "+nrOfOccurences+" concepts permanently?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
    }
}
