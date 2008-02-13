/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;

/**
 * Should be read as insert concept MapTool, i.e. this maptool specializes in
 * inserting a concept in the map.
 */
public class InsertConceptMapTool extends InsertMapTool {
	
	Log log = LogFactory.getLog(InsertConceptMapTool.class);
	
    /** The last uri typed. */
    String lastval;

    GridModel gridModel;

    public InsertConceptMapTool(MapController cont, GridModel gm) {
        super("INSERT_CONCEPT_WITH_URI", EditMapManagerFactory.class.getName(),
                cont);

        gridModel = gm;
    }

    protected boolean updateEnabled() {
        return (mapEvent.hitType == MapEvent.HIT_NONE);
    }

    public void actionPerformed(ActionEvent e) {
        String newval = (String) JOptionPane.showInputDialog(
                (java.awt.Component) mapEvent.mouseEvent.getSource(),
                "Enter URI for concept", "New Concept",
                JOptionPane.QUESTION_MESSAGE, null, null, lastval);
        if (newval != null) {
            try {
                lastval = newval;
                ContextMap cmap = mcontroller.getConceptMap();
                URI nuri = new URI(newval);

                // Concept concept = controller.getConzillaKit().getResourceStore().getAndReferenceConcept(nuri);
                // ConceptLayout ns=makeConceptLayout(concept);

                ConceptLayout ns = cmap.addConceptLayout(nuri.toString());
                java.awt.Dimension dim = mcontroller.getView().getMapScrollPane()
                        .getDisplayer().getMapObject(ns.getURI())
                        .getPreferredSize();

                ns.setBoundingBox(LayoutUtils.preferredBoxOnGrid(gridModel,
                        mapEvent.mapX, mapEvent.mapY, dim));

                //	    showTriples(ns, concept);
//            } catch (ComponentException ce) {
//                ErrorMessage.showError("Not found.", "Couldn't find concept.",
//                        ce, controller.getMapScrollPane().getDisplayer());
            } catch (URISyntaxException me) {
            	log.error("Invalid URI: " + newval, me);
                ErrorMessage.showError("Not an URI.", "The identifier doesn't conform to the URI standard.",
                        me, mcontroller.getView().getMapScrollPane().getDisplayer());
            } catch (ReadOnlyException re) {
                log.error("You shouldn't be able to choose 'insert concept' from menu when map isn't editable", re);
            } catch (InvalidURIException iue) {
            	log.error("Couldn't find concept", iue);
                ErrorMessage.showError("Not found.", "Couldn't find concept.", iue, mcontroller.getView().getMapScrollPane().getDisplayer());
            }
        }

    }
}