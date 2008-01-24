/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.InsertMapTool;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

class InsertClipboardConceptMapTool extends InsertMapTool {

	Log log = LogFactory.getLog(InsertClipboardConceptMapTool.class);
	
    GridModel gridModel;
    
    Clipboard clipboard;

    public InsertClipboardConceptMapTool(
        MapController cont,
        EditMapManager mm,
        Clipboard clipboard) {
        super("INSERT_CONCEPT_FROM_CLIPBOARD", Clipboard.class.getName(), cont);
        this.clipboard = clipboard;
        gridModel = mm.getGridModel();
    }

    protected boolean updateEnabled() {
        return (
            mapEvent.hitType == MapEvent.HIT_NONE
                && (clipboard.getConcept() != null
                        || clipboard.getDrawerLayouts() != null));
    }

    public void actionPerformed(ActionEvent e) {
        if (clipboard.getConcept() != null) {
            controller.getConceptMap().getComponentManager().getUndoManager().startChange();
        	pasteSingleConcept();
            controller.getConceptMap().getComponentManager().getUndoManager().endChange();
        } else if (clipboard.getDrawerLayouts() != null) {
            controller.getConceptMap().getComponentManager().getUndoManager().startChange();
            pasteMultipleConcepts();
            controller.getConceptMap().getComponentManager().getUndoManager().endChange();
        }

    }

    private void pasteMultipleConcepts() {
        List drawerLayouts = clipboard.getDrawerLayouts();
        ArrayList sls = new ArrayList();
        ArrayList cls = new ArrayList();
        Rectangle rect = new Rectangle(mapEvent.mapX, mapEvent.mapY,1,1);
        for (Iterator dls = drawerLayouts.iterator(); dls.hasNext();) {
            DrawerLayout dl = (DrawerLayout) dls.next();
            if (dl instanceof StatementLayout) {
                ContextMap.Position [] pos = ((StatementLayout) dl).getLine();
                sls.add(dl);
                for (int i = 0; i < pos.length; i++) {
                    rect.add(pos[i].x, pos[i].y);
                }
                if (dl.getBodyVisible()) {
                    ContextMap.BoundingBox bb = dl.getBoundingBox();
                    rect.add(bb.pos.x, bb.pos.y);
                }
            } else {
                ContextMap.BoundingBox bb = dl.getBoundingBox();
                cls.add(dl);
                rect.add(bb.pos.x, bb.pos.y);
            }
        }
        
        int xdiff = mapEvent.mapX-rect.x;
        int ydiff = mapEvent.mapY-rect.y;
        HashMap dl2dl = new HashMap();
        for (Iterator clsi = cls.iterator(); clsi.hasNext();) {
            DrawerLayout ocl = (DrawerLayout) clsi.next();
            try {
                ConceptLayout cl = makeConceptLayout(ocl.getConceptURI());
                dl2dl.put(ocl, cl);
                copyBoxLayout(xdiff, ydiff, ocl, cl);
            } catch (InvalidURIException e) {
                continue;
            }
        }
        
        for (Iterator slsi = sls.iterator(); slsi.hasNext();) {
            StatementLayout osl = (StatementLayout) slsi.next();
            DrawerLayout s = (DrawerLayout) dl2dl.get(osl.getSubjectLayout());
            DrawerLayout o = (DrawerLayout) dl2dl.get(osl.getObjectLayout());
            if (s==null || o==null) {
                //FIXME: Do something on failure???
                continue;
            }
            try {
                StatementLayout sl = makeStatementLayout(osl.isLiteralStatement(),
                        osl.getConceptURI(),
                        ((DrawerLayout) dl2dl.get(osl.getSubjectLayout())).getURI(),
                        osl.isLiteralStatement() ? null 
                                : ((DrawerLayout) dl2dl.get(osl.getObjectLayout())).getURI());
                if (sl == null) {
                	continue;
                }
                dl2dl.put(osl, sl);
                copyStatementLayout(xdiff, ydiff, osl, sl);
            } catch (InvalidURIException e) {
                continue;
            }            
        }
    }
    
    private void copyBoxLayout(int xdiff, int ydiff, DrawerLayout oldDL, DrawerLayout dl) {
        ContextMap.BoundingBox bb = oldDL.getBoundingBox();
        dl.setBoundingBox(new ContextMap.BoundingBox(
                new ContextMap.Dimension(bb.dim.width, bb.dim.height), 
                new ContextMap.Position(bb.pos.x+xdiff, bb.pos.y+ydiff)));
        dl.setBodyVisible(true);
        dl.setHorisontalTextAnchor(oldDL.getHorisontalTextAnchor());
        dl.setVerticalTextAnchor(oldDL.getVerticalTextAnchor());
    }

    private void copyStatementLayout(int xdiff, int ydiff, StatementLayout oldDL, StatementLayout sl) {
        if (oldDL.getBodyVisible()) {
            copyBoxLayout(xdiff, ydiff, oldDL, sl);
        }
        
        ContextMap.Position [] pos = oldDL.getLine();
        ContextMap.Position [] newPos = new ContextMap.Position[pos.length];
        for (int i = 0; i < pos.length; i++) {
            newPos[i] = new ContextMap.Position(pos[i].x+xdiff, pos[i].y+ydiff);
        }
        sl.setLine(newPos);
        sl.setPathType(oldDL.getPathType());

        if (oldDL.getBoxLine() != null) {
            pos = oldDL.getBoxLine();
            newPos = new ContextMap.Position[pos.length];
            for (int i = 0; i < pos.length; i++) {
                newPos[i] = new ContextMap.Position(pos[i].x+xdiff, pos[i].y+ydiff);
            }
            sl.setBoxLine(newPos);
            sl.setBoxLinePathType(oldDL.getBoxLinePathType());
        }
        
        if (oldDL.isLiteralStatement()) {
            ContextMap.BoundingBox bb = oldDL.getLiteralBoundingBox();
            sl.setLiteralBoundingBox(new ContextMap.BoundingBox(
                    new ContextMap.Dimension(bb.dim.width, bb.dim.height), 
                    new ContextMap.Position(bb.pos.x+xdiff, bb.pos.y+ydiff)));
        }
    }

    private void pasteSingleConcept() {
        Concept concept = clipboard.getConcept();
        MapObject copiedMapObject = clipboard.getMapObject();

        try {
            
            if (concept.getTriple() == null 
                    || (copiedMapObject != null 
                            && copiedMapObject.getDrawerLayout() 
                            instanceof ConceptLayout)) {
                ConceptLayout cl = makeConceptLayout(concept.getURI());
                setBoundingBox(gridModel, copiedMapObject, cl);
            } else {
                StatementLayout sl = makeStatementLayout(concept);
                if (sl == null) {
                	return;
                }
                showTriple(sl, gridModel);
                if (copiedMapObject.getDrawerLayout().getBodyVisible()) {
                    setBoundingBox(gridModel, copiedMapObject, sl);
                    if (((StatementLayout) copiedMapObject.getDrawerLayout()).getBoxLine() != null) {
                        setBoxLine(gridModel, sl);
                    }
                }
            }
        } catch (ReadOnlyException re) {
            log.error("Map can't be edited but we are in edit mode", re);
        } catch (InvalidURIException iue) {
        	log.error("Concept doesn't seem to have a valid URI", iue);
            ErrorMessage.showError(
                "Can't paste concept.",
                "Concept doesn't seem to have a valid URI."
                    + "Can't create a graphical representation for it.",
                iue,
                controller.getView().getMapScrollPane().getDisplayer());
        }
    }
}
