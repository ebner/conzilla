/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.Dimension;

import javax.swing.JOptionPane;

import se.kth.cid.component.InvalidURIException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public abstract class InsertMapTool extends ActionMapMenuTool {
    public InsertMapTool(String name, String resbundle, MapController cont) {
        super(name, resbundle, cont);
    }

    protected ConceptLayout makeConceptLayout(String conceptURI)
        throws InvalidURIException {
        ContextMap cmap = controller.getConceptMap();
        ConceptLayout ns = cmap.addConceptLayout(conceptURI);
        return ns;
    }

    protected StatementLayout makeStatementLayout(boolean isObjectLiteral, 
            String conceptURI,
            String subjectLayoutURI, 
            String objectLayoutURI) 
    throws InvalidURIException{
        ContextMap cmap = controller.getConceptMap();
        StatementLayout sl = null;
        if (isObjectLiteral) {
        	if (subjectLayoutURI == null) {
        		JOptionPane.showMessageDialog(null, "Cannot paste concept-relation while the concept it connects " +
        				"to is missing in this map.", "Cannot paste", JOptionPane.ERROR_MESSAGE);
        		return null;
        	}
            sl = cmap.addStatementLayout(conceptURI,
                    subjectLayoutURI,
                    null);
        } else {
        	if (subjectLayoutURI == null && objectLayoutURI == null) {
        		JOptionPane.showMessageDialog(null, "Cannot paste concept-relation while both the concepts " +
        				"it connects to is missing in this map.", "Cannot paste", JOptionPane.ERROR_MESSAGE);
        		return null;
        	} else if (subjectLayoutURI == null || objectLayoutURI == null) {
        		JOptionPane.showMessageDialog(null, "Cannot paste concept-relation while one of the concepts " +
        				"it connects to is missing in this map.", "Cannot paste", JOptionPane.ERROR_MESSAGE);
        		return null;
        	}
            sl = cmap.addStatementLayout(conceptURI, 
                    subjectLayoutURI,
                    objectLayoutURI);
        }
        return sl;        
    }
    
    protected StatementLayout makeStatementLayout(Concept concept)
    throws InvalidURIException {
        boolean isObjectLiteral = concept.getTriple().isObjectLiteral();
        
        String objectLayoutURI = isObjectLiteral ? null : getFirstObjectLayout(concept);
        return makeStatementLayout(isObjectLiteral,
                concept.getURI(),
                getFirstSubjectLayout(concept),
                objectLayoutURI);
    }

    /** Should show triple....fix this.
     */
    protected void showTriple(StatementLayout sl, GridModel gridModel) {
        ContextMap.Position[] poss = LayoutUtils.tripleLine(sl.getSubjectLayout(), sl.getObjectLayout(), gridModel);
        sl.setLine(poss);
    }
    
    protected DrawerLayout getFirstLayout(String conceptURI) {
        DrawerLayout [] dls = controller.getConceptMap().getDrawerLayouts();
        for (int i = 0; i < dls.length; i++) {
            if (dls[i].getConceptURI().equals(conceptURI)) {
                return dls[i];
            }
        }
        return null;
    }
    
    protected String getFirstSubjectLayout(Concept concept) {
    	DrawerLayout dl = getFirstLayout(concept.getTriple().subjectURI());
    	return dl != null ? dl.getURI() : null;
    }

    protected String getFirstObjectLayout(Concept concept) {
        DrawerLayout dl = getFirstLayout(concept.getTriple().objectValue()); 
    	return dl != null ? dl.getURI() : null;
    }

    protected void setBoundingBox(GridModel gridModel, MapObject copiedMapObject, DrawerLayout dl) {
        Dimension dim = null;
        if (copiedMapObject == null) {
            dim = controller.getView().getMapScrollPane().getDisplayer()
                .getMapObject(dl.getURI())
                .getPreferredSize();
        } else {
            ContextMap.Dimension cdim = copiedMapObject.getDrawerLayout().getBoundingBox().dim;
            dim = new Dimension(cdim.width, cdim.height);
        }
        dl.setBoundingBox(
                LayoutUtils.preferredBoxOnGrid(
                        gridModel,
                        mapEvent.mapX,
                        mapEvent.mapY,
                        dim));
        dl.setBodyVisible(true);
    }
    
    protected void setBoxLine(GridModel gridModel, StatementLayout dl) {
        dl.setBoxLine(LayoutUtils.boxLine(dl, mapEvent,  gridModel));
    }

}
