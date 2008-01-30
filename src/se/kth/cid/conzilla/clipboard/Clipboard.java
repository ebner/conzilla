/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.kth.cid.component.Resource;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;

/**
 * @author Matthias Palmer.
 */
public class Clipboard implements ClipboardOwner {

	public static final String COPY="COPY";
	public static final String PASTE="PASTE";
	public static final String CUT="CUT";
	
	public static final int NONE = -1;	
	public static final int SINGLE_RESOURCE = 0;
	public static final int MULTIPLE_RESOURCES = 1;	//Not yet used.
	public static final int SINGLE_LAYOUT = 2;
	public static final int MULTIPLE_LAYOUTS = 3;
	public static final int MAP = 4;
	public static final int OTHER = 5;
	
	private int clipType = NONE;
	
	private ContextMap origin;
    private List<Resource> resources;
    private List<ClipboardDrawerLayout> layouts;
	private boolean isCut;
    
    public Clipboard() {
    }
    
    public int getClipType() {
    	return clipType;
    }
    
    public void setClipIsCut(boolean isCut) {
    	this.isCut = isCut;
    }
    
    public boolean isClipCut() {
    	return this.isCut;
    }

    public void lostOwnership(java.awt.datatransfer.Clipboard clipboard,
            Transferable contents) {
    }
    
    public void clearClipBoard() {
    	clipType = NONE;
        resources = null;
        layouts = null;
        origin = null;
        isCut = false;
    }
    
    public void setDrawerLayouts(List drawerLayouts) {
        clearClipBoard();
        this.layouts = new ArrayList<ClipboardDrawerLayout>();
        for (Iterator iter = drawerLayouts.iterator(); iter.hasNext();) {
			DrawerLayout dl = (DrawerLayout) iter.next();
			origin = dl.getConceptMap();
			if (dl instanceof StatementLayout) {
				this.layouts.add(new ClipboardStatementLayout((StatementLayout) dl));
			} else {
				this.layouts.add(new ClipboardDrawerLayout(dl));				
			}
		}
        if (drawerLayouts.size() == 1) {
        	clipType = SINGLE_LAYOUT;
        } else {
        	clipType = MULTIPLE_LAYOUTS;
        }
    }
    
    public ContextMap getOriginContextMap() {
    	return origin;
    }
    
    public List<ClipboardDrawerLayout> getDrawerLayouts() {
        return layouts;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        clearClipBoard();
        origin = drawerLayout.getConceptMap();
        layouts = new ArrayList<ClipboardDrawerLayout>();
        if (drawerLayout instanceof StatementLayout) {
        	layouts.add(new ClipboardStatementLayout((StatementLayout) drawerLayout));
        } else {
        	layouts.add(new ClipboardDrawerLayout(drawerLayout));        	
        }
        clipType = SINGLE_LAYOUT;
    }

    public ClipboardDrawerLayout getDrawerLayout() {
    	return layouts.get(0);
    }
    
    public void setResource(Resource res) {
        clearClipBoard();

        resources = new ArrayList<Resource>();
        resources.add(res);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(res.getURI()), this);
        if (res instanceof ContextMap) {
        	clipType = MAP;
        } else {
        	clipType = SINGLE_RESOURCE;
        }
    }

    public Resource getResource() {
        return resources.get(0);
    }

    public ContextMap getConceptMap() {
    	if (clipType == MAP) {
    		return (ContextMap) getResource();
    	}
    	return null;
    }
}