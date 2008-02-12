/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import se.kth.cid.component.InvalidURIException;
import se.kth.cid.conzilla.clipboard.ClipboardDrawerLayout;
import se.kth.cid.conzilla.clipboard.ClipboardStatementLayout;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.tool.Tool;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public abstract class InsertMapTool extends Tool {
    public InsertMapTool(String name, String resbundle, MapController cont) {
        super(name, resbundle, cont);
    }

    protected Point getInsertPosition() {
    	if (mapEvent != null) {
    		return new Point(mapEvent.mapX, mapEvent.mapY);
    	} else {
			try {
				MapScrollPane scroll = mcontroller.getView().getMapScrollPane();
				Rectangle visirect = scroll.getVisibleRect();
				AffineTransform tr = scroll.getDisplayer().getTransform().createInverse();
				Point middle = new Point(visirect.x+(visirect.width/2), visirect.y+(visirect.height/2));
	    		Point2D point = tr.transform(middle, null);
	    		return new Point((int) point.getX(), (int) point.getY());
			} catch (NoninvertibleTransformException e) {
			}
    	}
    	return new Point(100, 100);
    }
    
    protected ConceptLayout makeConceptLayout(String conceptURI)
        throws InvalidURIException {
        ContextMap cmap = mcontroller.getConceptMap();
        ConceptLayout ns = cmap.addConceptLayout(conceptURI);
        return ns;
    }

    protected StatementLayout makeStatementLayout(boolean isObjectLiteral, 
            String conceptURI,
            String subjectLayoutURI, 
            String objectLayoutURI) 
    throws InvalidURIException{
        ContextMap cmap = mcontroller.getConceptMap();
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
    
    protected StatementLayout makeStatementLayout(ClipboardStatementLayout layout)
    throws InvalidURIException {
        boolean isLiteralStatement = layout.isLiteralStatement();
        
        String objectLayoutURI = isLiteralStatement ? null : getFirstObjectLayout(layout);
        return makeStatementLayout(isLiteralStatement,
                layout.getConceptURI(),
                getFirstSubjectLayout(layout),
                objectLayoutURI);
    }

    /** Should show triple....fix this.
     */
    protected void showTriple(StatementLayout sl, GridModel gridModel) {
        ContextMap.Position[] poss = LayoutUtils.tripleLine(sl.getSubjectLayout(), sl.getObjectLayout(), gridModel);
        sl.setLine(poss);
    }
    
    protected DrawerLayout getFirstLayout(String conceptURI) {
        DrawerLayout [] dls = mcontroller.getConceptMap().getDrawerLayouts();
        for (int i = 0; i < dls.length; i++) {
            if (dls[i].getConceptURI().equals(conceptURI)) {
                return dls[i];
            }
        }
        return null;
    }
    
    protected String getFirstSubjectLayout(ClipboardStatementLayout layout) {
    	DrawerLayout dl = getFirstLayout(layout.getSubjectLayoutURI());
    	return dl != null ? dl.getURI() : null;
    }

    protected String getFirstObjectLayout(ClipboardStatementLayout layout) {
        DrawerLayout dl = getFirstLayout(layout.getObjectLayoutURI()); 
    	return dl != null ? dl.getURI() : null;
    }

    protected void setBoundingBox(GridModel gridModel, ClipboardDrawerLayout cdl, 
    		DrawerLayout dl, Point insertionPoint) {
        Dimension dim = null;
/*        if (copiedMapObject == null) {
            dim = controller.getView().getMapScrollPane().getDisplayer()
                .getMapObject(dl.getURI())
                .getPreferredSize();
        } else {*/
            ContextMap.Dimension cdim = cdl.getBoundingBox().dim;
            dim = new Dimension(cdim.width, cdim.height);
        //}
        dl.setBoundingBox(
                LayoutUtils.preferredBoxOnGrid(
                        gridModel,
                        insertionPoint.x,
                        insertionPoint.y,
                        dim));
        dl.setBodyVisible(true);
    }
    
    protected void setBoxLine(GridModel gridModel, StatementLayout dl, Point insertionPoint) {
        dl.setBoxLine(LayoutUtils.boxLine(dl, LayoutUtils.getPosition(insertionPoint),  gridModel));
    }
}