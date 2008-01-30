/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.edit.EditMapManager;
import se.kth.cid.conzilla.edit.InsertMapTool;
import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

public class PasteConceptMapTool extends InsertMapTool {

	Log log = LogFactory.getLog(PasteConceptMapTool.class);
    
    Clipboard clipboard;

    public PasteConceptMapTool(
        MapController cont,
        Clipboard clipboard) {
        super("INSERT_CONCEPT_FROM_CLIPBOARD", Clipboard.class.getName(), cont);
        this.clipboard = clipboard;
    }

    protected boolean updateEnabled() {
    	switch(clipboard.getClipType()) {
    	case Clipboard.SINGLE_LAYOUT:
    	case Clipboard.MULTIPLE_LAYOUTS:
    		return true;
    	}
    	return false;
    }
    
    public void actionPerformed(ActionEvent e) {
    	switch(clipboard.getClipType()) {
    	case Clipboard.SINGLE_LAYOUT:
            controller.getConceptMap().getComponentManager().getUndoManager().startChange();
        	pasteSingleConcept();
            controller.getConceptMap().getComponentManager().getUndoManager().endChange();
            break;
    	case Clipboard.MULTIPLE_LAYOUTS:
            controller.getConceptMap().getComponentManager().getUndoManager().startChange();
            pasteMultipleConcepts();
            controller.getConceptMap().getComponentManager().getUndoManager().endChange();
            break;
    	}
    }
    
    private void pasteMultipleConcepts() {
        Point ipoint = getInsertPosition();
    	List<ClipboardDrawerLayout> drawerLayouts = clipboard.getDrawerLayouts();
        ArrayList<ClipboardStatementLayout> sls = new ArrayList<ClipboardStatementLayout>();
        ArrayList<ClipboardDrawerLayout> cls = new ArrayList<ClipboardDrawerLayout>();
        Rectangle rect = new Rectangle(ipoint.x, ipoint.y,1,1);

        boolean checkExists = !clipboard.isClipCut();
        HashSet allCurrentURIs = new HashSet();
        if (checkExists && !drawerLayouts.isEmpty()) {
        	allCurrentURIs = toURISet(clipboard.getConceptMap().getDrawerLayouts());
        }
        
        
        for (Iterator<ClipboardDrawerLayout> dls = drawerLayouts.iterator(); dls.hasNext();) {
            ClipboardDrawerLayout dl = dls.next();
            if (checkExists && !allCurrentURIs.contains(dl)) {
            	continue;
            }
            if (dl instanceof ClipboardStatementLayout) {
                ContextMap.Position [] pos = ((ClipboardStatementLayout) dl).getLine();
                sls.add((ClipboardStatementLayout) dl);
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
        
        int xdiff = ipoint.x-rect.x;
        int ydiff = ipoint.y-rect.y;
        HashMap<String, DrawerLayout> dlId2dl = new HashMap<String, DrawerLayout>();
        for (Iterator<ClipboardDrawerLayout> clsi = cls.iterator(); clsi.hasNext();) {
            ClipboardDrawerLayout ocl = clsi.next();
            try {
                ConceptLayout cl = makeConceptLayout(ocl.getConceptURI());
                dlId2dl.put(ocl.getId(), cl);
                copyBoxLayout(xdiff, ydiff, ocl, cl);
            } catch (InvalidURIException e) {
                continue;
            }
        }
        
        for (Iterator<ClipboardStatementLayout> slsi = sls.iterator(); slsi.hasNext();) {
            ClipboardStatementLayout osl = slsi.next();
            DrawerLayout s = dlId2dl.get(osl.getSubjectLayoutURI());
            DrawerLayout o = dlId2dl.get(osl.getObjectLayoutURI());
            if (s==null || o==null) {
                //FIXME: Do something on failure???
                continue;
            }
            try {
                StatementLayout sl = makeStatementLayout(osl.isLiteralStatement(),
                        osl.getConceptURI(),
                        dlId2dl.get(osl.getSubjectLayoutURI()).getURI(),
                        osl.isLiteralStatement() ? null 
                                : dlId2dl.get(osl.getObjectLayoutURI()).getURI());
                if (sl == null) {
                	continue;
                }
                dlId2dl.put(osl.getId(), sl);
                copyStatementLayout(xdiff, ydiff, osl, sl);
            } catch (InvalidURIException e) {
                continue;
            }            
        }
    }

    private HashSet<String> toURISet(DrawerLayout[] array) {
    	HashSet<String> set = new HashSet<String>();
    	for (int i = 0; i < array.length; i++) {
			set.add(array[i].getURI());
		}
    	return set;
    }
    
    private void copyBoxLayout(int xdiff, int ydiff, ClipboardDrawerLayout oldDL, DrawerLayout dl) {
        ContextMap.BoundingBox bb = oldDL.getBoundingBox();
        dl.setBoundingBox(new ContextMap.BoundingBox(
                new ContextMap.Dimension(bb.dim.width, bb.dim.height), 
                new ContextMap.Position(bb.pos.x+xdiff, bb.pos.y+ydiff)));
        dl.setBodyVisible(true);
        dl.setHorisontalTextAnchor(oldDL.getHorisontalTextAnchor());
        dl.setVerticalTextAnchor(oldDL.getVerticalTextAnchor());
    }

    private void copyStatementLayout(int xdiff, int ydiff, ClipboardStatementLayout oldDL, StatementLayout sl) {
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
        Point ipoint = getInsertPosition();
        ClipboardDrawerLayout cdl = clipboard.getDrawerLayout();
        

        //If the copied DrawerLayout is no longer in ContextMap, abort paste.
        if (!clipboard.isClipCut() 
        		&& !toURISet(clipboard.getOriginContextMap().getDrawerLayouts()).contains(cdl.getId())) {
    		return;
        }

        try {

        Concept concept = ConzillaKit.getDefaultKit().getResourceStore()
        		.getAndReferenceConcept(URI.create(cdl.getConceptURI()));

        
            GridModel gridModel = ((EditMapManager) controller.getManager()).gridModel;
        	
            if (concept.getTriple() == null 
                    || !(cdl instanceof ClipboardStatementLayout)) {
                ConceptLayout cl = makeConceptLayout(concept.getURI());
                setBoundingBox(gridModel, cdl, cl, ipoint);
            } else {
                StatementLayout sl = makeStatementLayout(concept);
                if (sl == null) {
                	return;
                }
                showTriple(sl, gridModel);
                if (cdl.getBodyVisible()) {
                    setBoundingBox(gridModel, cdl, sl, ipoint);
                    if (((ClipboardStatementLayout) cdl).getBoxLine() != null) {
                        setBoxLine(gridModel, sl, ipoint);
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
        } catch (ComponentException e) {
            log.error("Concept that is to be copied are missing.", e);
		}
    }
}
