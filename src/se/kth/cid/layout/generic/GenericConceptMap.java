/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;
import java.net.URI;
import java.util.Iterator;
import java.util.Vector;

import se.kth.cid.component.EditEvent;
import se.kth.cid.component.FiringResourceImpl;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.layout.BookkeepingConceptLayout;
import se.kth.cid.layout.BookkeepingConceptMap;
import se.kth.cid.layout.BookkeepingDrawerLayout;
import se.kth.cid.layout.BookkeepingStatementLayout;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ConceptMapException;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.GroupLayout;
import se.kth.cid.layout.LayerEvent;
import se.kth.cid.layout.LayerListener;
import se.kth.cid.layout.LayerManager;
import se.kth.cid.layout.ResourceLayout;
import se.kth.cid.layout.StatementLayout;

/** A straightforward implementation of the ConeptMap interface.
 *
 *  @author Mikael Nilsson
 *  @author Matthias Palmer
 *  @version $Revision$
 */

public abstract class GenericConceptMap
    extends FiringResourceImpl
    implements BookkeepingConceptMap, LayerListener {

    protected ContextMap.Dimension dimension;

    /** Isn't initialized here, non abstract subclasses have to do that.
     */
    protected MemLayerManager layerManager;
    protected Vector orderedDrawerLayouts = null;

    Vector triplesWithObjectEndUnmatched;
    Vector triplesWithSubjectEndUnmatched;

    protected GenericConceptMap(URI mapURI, URI loadURI, MIMEType loadType) {
        super(mapURI, loadURI, loadType);
        dimension = new ContextMap.Dimension(400, 400);
        triplesWithObjectEndUnmatched = new Vector();
        triplesWithSubjectEndUnmatched = new Vector();
    }

    ///////////LayerListener////////////////
    public void layerChange(LayerEvent event) {
        clearDrawerLayoutCache();
    }

    /////////////ConceptMap/////////////
    public ContextMap.Dimension getDimension() {
        return dimension;
    }

    public void setDimension(ContextMap.Dimension dim)
        throws ReadOnlyException {
        if (dim == null)
            throw new IllegalArgumentException("Null dimension.");

        dimension = dim;
        fireEditEvent(new EditEvent(this, this, DIMENSION_EDITED, dim));
    }

    /////////////ConceptLayout////////////

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public DrawerLayout[] getDrawerLayouts() {
        cacheDrawerLayouts();
        return (DrawerLayout[]) orderedDrawerLayouts.toArray(
            new DrawerLayout[orderedDrawerLayouts.size()]);
    }

    public ResourceLayout getResourceLayout(String mapID) {
        return layerManager.getResourceLayout(mapID);
    }

    public void clearDrawerLayoutCache() {
        orderedDrawerLayouts = null;
    }

    protected void cacheDrawerLayouts() {
        if (orderedDrawerLayouts == null)
            orderedDrawerLayouts =
                layerManager.getDrawerLayouts(GroupLayout.IGNORE_VISIBILITY);
    }

    /** Works as a factory for conceptlayouts.
     */
    protected abstract BookkeepingConceptLayout addConceptLayoutImpl(
        String mapID,
        String concepturi,
        String parentMapID)
        throws InvalidURIException, ConceptMapException;

    /** Works as a factory for conceptlayouts.
     */
    protected abstract BookkeepingStatementLayout addStatementLayoutImpl(
        String mapID,
        String concepturi,
        String parentMapID,
        String subjectLayouturi,
        String objectLayouturi)
        throws InvalidURIException, ConceptMapException;

    public ConceptLayout addConceptLayout(String concepturi)
        throws ReadOnlyException, InvalidURIException {
        try {
            return (ConceptLayout) addResourceLayout(
                null,
                concepturi,
                null,
                null,
                null);
        } catch (ConceptMapException cme) {
            //Throw something??
        }
        return null;
    }

    public StatementLayout addStatementLayout(
        String concepturi,
        String subjectLayouturi,
        String objectLayouturi)
        throws ReadOnlyException, InvalidURIException {
        try {
            return (StatementLayout) addResourceLayout(
                null,
                concepturi,
                null,
                subjectLayouturi,
                objectLayouturi);
        } catch (ConceptMapException cme) {
            //Throw something??
        }
        return null;
    }

    /** Used to load or create conceptlayouts.
     */
    protected ResourceLayout addResourceLayout(
        String mapID,
        String concepturi,
        String parentMapID,
        String subjectLayouturi,
        String objectLayouturi)
        throws ReadOnlyException, InvalidURIException, ConceptMapException {

        clearDrawerLayoutCache();
        BookkeepingDrawerLayout os;
        if (subjectLayouturi == null) {
            os = addConceptLayoutImpl(mapID, concepturi, parentMapID);
            connectResourceLayout(os);
        } else {
            os =
                addStatementLayoutImpl(
                    mapID,
                    concepturi,
                    parentMapID,
                    subjectLayouturi,
                    objectLayouturi);
            addStatementLayoutEnds((BookkeepingStatementLayout) os);
        }

        fireEditEvent(
            new EditEvent(this, this, RESOURCELAYOUT_ADDED, os.getURI()));
        return os;
    }

    //Bookkeeping stuff below...
    //----------------------------------

    public void removeResourceLayout(BookkeepingDrawerLayout os) {
        if (layerManager.removeResourceLayout(os))
            clearDrawerLayoutCache();

        disconnectResourceLayout(os);
        if (os instanceof BookkeepingStatementLayout) {
            removeStatementLayoutEnds((BookkeepingStatementLayout) os);
            fireEditEvent(
                new EditEvent(this, this, RESOURCELAYOUT_REMOVED, os.getURI()));
        } else
            fireEditEvent(
                new EditEvent(this, this, RESOURCELAYOUT_REMOVED, os.getURI()));
    }

    void disconnectResourceLayout(BookkeepingDrawerLayout ns) {
        StatementLayout[] ass = ns.getObjectOfStatementLayouts();
        for (int i = 0; i < ass.length; i++) {
            ((BookkeepingStatementLayout) ass[i]).setObjectLayout(null);
            triplesWithObjectEndUnmatched.add(ass[i]);
        }
        ass = ns.getSubjectOfStatementLayouts();
        for (int i = 0; i < ass.length; i++) {
            ((BookkeepingStatementLayout) ass[i]).setSubjectLayout(null);
            triplesWithSubjectEndUnmatched.add(ass[i]);
        }
    }

    void connectResourceLayout(BookkeepingDrawerLayout ns) {
        Iterator it = triplesWithObjectEndUnmatched.iterator();
        while (it.hasNext()) {
            BookkeepingStatementLayout as =
                (BookkeepingStatementLayout) it.next();
            if (as.getObjectLayoutURI().equals(ns.getURI())) {
                ns.addObjectOfStatementLayout(as);
                as.setObjectLayout(ns);
                it.remove();
            }
        }
        it = triplesWithSubjectEndUnmatched.iterator();
        while (it.hasNext()) {
            BookkeepingStatementLayout as =
                (BookkeepingStatementLayout) it.next();
            if (as.getSubjectLayoutURI().equals(ns.getURI())) {
                ns.addSubjectOfStatementLayout(as);
                as.setSubjectLayout(ns);
                it.remove();
            }
        }
    }

    void addStatementLayoutEnds(BookkeepingStatementLayout as) {
        BookkeepingDrawerLayout lnso =
            (BookkeepingDrawerLayout) getResourceLayout(as
                .getObjectLayoutURI());
        if (lnso != null)
            lnso.addObjectOfStatementLayout(as);
        else
            triplesWithObjectEndUnmatched.add(as);

        BookkeepingDrawerLayout lnss =
            (BookkeepingDrawerLayout) getResourceLayout(as
                .getSubjectLayoutURI());
        if (lnss != null)
            lnss.addSubjectOfStatementLayout(as);
        else
            triplesWithSubjectEndUnmatched.add(as);
    }

    void removeStatementLayoutEnds(BookkeepingStatementLayout as) {
        BookkeepingDrawerLayout lnso =
            (BookkeepingDrawerLayout) getResourceLayout(as
                .getObjectLayoutURI());
        if (lnso != null)
            lnso.removeObjectOfStatementLayout(as);
        else
            triplesWithObjectEndUnmatched.remove(as);

        BookkeepingDrawerLayout lnss =
            (BookkeepingDrawerLayout) getResourceLayout(as
                .getSubjectLayoutURI());
        if (lnss != null)
            lnss.removeSubjectOfStatementLayout(as);
        else
            triplesWithSubjectEndUnmatched.remove(as);
    }
}
