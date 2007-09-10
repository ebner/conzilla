/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import se.kth.cid.conzilla.edit.layers.GridModel;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.style.LineStyle;

/** 
 *  @author Matthias Palmer
 *  @version $version: $
 */
public class HandleStore {
    Hashtable conceptBoxStructs;
    Hashtable literalBoxStructs;
    Hashtable conceptLineStructs;
    Hashtable tripleStructs;

    GridModel gridModel;
    boolean lock = false;

    public HandleStore(GridModel gridModel) {
        this.gridModel = gridModel;
        clear();
    }

    public TripleHandlesStruct getTripleHandles(StatementLayout as) {
    	if (!editableInCurrentSession(as)) {
    		return null;
    	}
    	
        TripleHandlesStruct ahs = (TripleHandlesStruct) tripleStructs.get(as);
        if (ahs == null) {
            ahs = new TripleHandlesStruct(as);
            tripleStructs.put(as, ahs);
        }
        return ahs;
    }
    public BoxLineHandlesStruct getBoxLineHandles(StatementLayout sl) {
    	if (!editableInCurrentSession(sl)) {
    		return null;
    	}

    	BoxLineHandlesStruct nlhs =
            (BoxLineHandlesStruct) conceptLineStructs.get(sl);
        if (nlhs == null) {
            nlhs = new BoxLineHandlesStruct(sl);
            conceptLineStructs.put(sl, nlhs);
        }
        return nlhs;
    }

    public BoxHandlesStruct getBoxHandlesStruct(DrawerLayout ns) {
    	if (!editableInCurrentSession(ns)) {
    		return null;
    	}

    	if (!ns.getBodyVisible()) {
    		return null;
    	}
        BoxHandlesStruct nbhs = (BoxHandlesStruct) conceptBoxStructs.get(ns);
        if (nbhs == null) {
            nbhs = new ConceptBoxHandlesStruct(ns, gridModel);
            conceptBoxStructs.put(ns, nbhs);
        }
        return nbhs;
    }

    public BoxHandlesStruct getLiteralBoxHandlesStruct(StatementLayout ns) {
    	if (!editableInCurrentSession(ns)) {
    		return null;
    	}
    	
    	BoxHandlesStruct nbhs = (BoxHandlesStruct) literalBoxStructs.get(ns);
        if (nbhs == null) {
            nbhs = new LiteralBoxHandlesStruct(ns, gridModel);
            literalBoxStructs.put(ns, nbhs);
        }
        return nbhs;
    }

    public void set() {
        if (lock)
            return;
        lock = true;
        Enumeration en = conceptBoxStructs.elements();
        while (en.hasMoreElements())
             ((BoxHandlesStruct) en.nextElement()).set();

        en = literalBoxStructs.elements();
        while (en.hasMoreElements())
             ((BoxHandlesStruct) en.nextElement()).set();

        en = conceptLineStructs.elements();
        while (en.hasMoreElements())
             ((BoxLineHandlesStruct) en.nextElement()).set();

        en = tripleStructs.elements();
        while (en.hasMoreElements())
             ((TripleHandlesStruct) en.nextElement()).set();
        lock = false;
    }

    public void clear(Object o) {
        conceptBoxStructs.remove(o);
        conceptLineStructs.remove(o);
        literalBoxStructs.remove(o);
        if (o instanceof StatementLayout)
            tripleStructs.remove(o);
    }

    public void clear() {
        if (lock)
            return;
        conceptBoxStructs = new Hashtable();
        literalBoxStructs = new Hashtable();
        conceptLineStructs = new Hashtable();
        tripleStructs = new Hashtable();
    }
    public void paint(Graphics2D g) {
        Enumeration en = conceptBoxStructs.elements();
        while (en.hasMoreElements())
             ((BoxHandlesStruct) en.nextElement()).paint(g);

        en = literalBoxStructs.elements();
        while (en.hasMoreElements())
             ((BoxHandlesStruct) en.nextElement()).paint(g);

        en = conceptLineStructs.elements();
        while (en.hasMoreElements())
             ((BoxLineHandlesStruct) en.nextElement()).paint(g);

        en = tripleStructs.elements();
        while (en.hasMoreElements())
             ((TripleHandlesStruct) en.nextElement()).paint(g);
    }

    //Helping functions

    //If box is visible set followers
    public Collection getAndSetBoxFollowers(DrawerLayout ns) {
        //FIXME:  solve this...
        if (ns == null || !editableInCurrentSession(ns)) {
            return new Vector();
        }
        if (ns.getBodyVisible()) {
            BoxHandlesStruct nlhs = getBoxHandlesStruct(ns);
            Collection col = getBoxFollowers(ns);
            nlhs.tot.setFollowers(col);
            return col;
        }
        return getBoxFollowers(ns);
    }

    //Set single follower to literalbox and return it.
    public Handle getAndSetLiteralBoxFollower(StatementLayout ns) {
        Handle handle = getTripleHandles(ns).getFirstHandle();
        BoxHandlesStruct nlhs = getLiteralBoxHandlesStruct(ns);
        Vector v = new Vector();
        v.add(handle);
        nlhs.tot.setFollowers(v);
        return handle;
    }

    //Calculates the boxfollowers.
    public Collection getBoxFollowers(DrawerLayout ns) {
        Collection fh = new Vector();

        //If visible conceptline should follow otherwise triplecenter.
        if (ns instanceof StatementLayout
                && ((StatementLayout) ns).getBoxLine() != null) {
            Handle lastBoxLineHandle = getBoxLineHandles((StatementLayout) ns).getLastHandle();
            if (lastBoxLineHandle != null)
                fh.add(lastBoxLineHandle);
        }
        //	else
        //	    fh.addAll(getAndSetTripleCenterFollowers(ns));

        //Roles ending at this box       
        fetchOneEndFollowers(fh, ns.getObjectOfStatementLayouts(), true);
        fetchOneEndFollowers(fh, ns.getSubjectOfStatementLayouts(), false);
        return fh;
    }

    private void fetchOneEndFollowers(
        Collection fh,
        StatementLayout[] ass,
        boolean object) {
        Handle endTripleHandle;
        //	Handle endControlTripleHandle;
        for (int i = 0; i < ass.length; i++) {
            StatementLayout as = ass[i];
            if (!editableInCurrentSession(as)) {
            	return;
            }
            switch (as.getPathType()) {
                case LineStyle.PATH_TYPE_STRAIGHT :
                case LineStyle.PATH_TYPE_QUAD :
                    if (object)
                        endTripleHandle = getTripleHandles(as).getFirstHandle();
                    else
                        endTripleHandle = getTripleHandles(as).getLastHandle();

                    if (endTripleHandle != null)
                        fh.add(endTripleHandle);
                    break;
                case LineStyle.PATH_TYPE_CURVE :
                    if (object)
                        endTripleHandle = getTripleHandles(as).getFirstHandle();
                    else
                        endTripleHandle = getTripleHandles(as).getLastHandle();
                    //		    lastControlTripleHandle = getTripleHandles(as).getSecondLastHandle();
                    if (endTripleHandle != null)
                        //&& endControlTripleHandle != null)
                        {
                        fh.add(endTripleHandle);
                        //			    fh.add(endControlTripleHandle);
                    }
            }
        }
    }

    public Set getDrawerLayouts() {
        HashSet set = new HashSet();
        set.addAll(conceptBoxStructs.keySet());
        set.addAll(tripleStructs.keySet());
        return set;
    }
    
    public Set getMarkedLayouts() {
        HashSet set = new HashSet();
        for (Iterator conceptBoxes = conceptBoxStructs.keySet().iterator(); conceptBoxes.hasNext();) {
            DrawerLayout dl = (DrawerLayout) conceptBoxes.next();
            BoxHandlesStruct nbhs = (BoxHandlesStruct) conceptBoxStructs.get(dl);
            if (nbhs.tot.isSelected()) {
                set.add(dl);
            }
        }
        for (Iterator tripleLines = tripleStructs.keySet().iterator(); tripleLines.hasNext();) {
            StatementLayout sl = (StatementLayout) tripleLines.next();
            TripleHandlesStruct ahs = (TripleHandlesStruct) tripleStructs.get(sl);
            boolean allSelected = true;
            for (Iterator handles = ahs.handles.iterator(); handles.hasNext();) {
                Handle handle = (Handle) handles.next();
                if (!handle.isSelected()) {
                    allSelected = false;
                }
            }
            if (allSelected) {
                set.add(sl);
            }
        }
        return set;
    }
    
    Collection getBoxHandles(DrawerLayout ns) {
        BoxHandlesStruct nbhs = getBoxHandlesStruct(ns);
        Vector vec = new Vector();
        vec.add(nbhs.ul);
        vec.add(nbhs.ur);
        vec.add(nbhs.lr);
        vec.add(nbhs.ll);
        vec.add(nbhs.tot);
        return vec;
    }
    
    public boolean editableInCurrentSession(DrawerLayout dl) {
    	String cluri = dl.getConceptMap().getComponentManager()
    		.getEditingSesssion().getContainerURIForLayouts();
    	return dl.getLoadContainer().equals(cluri);
    }
}
