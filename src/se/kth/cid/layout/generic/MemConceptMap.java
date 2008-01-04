/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout.generic;
import java.awt.Rectangle;
import java.net.URI;
import java.util.List;
import java.util.Set;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.ComponentManager;
import se.kth.cid.component.Container;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.identity.MIMEType;
import se.kth.cid.layout.BookkeepingConceptLayout;
import se.kth.cid.layout.BookkeepingResourceLayout;
import se.kth.cid.layout.BookkeepingStatementLayout;
import se.kth.cid.layout.ConceptMapException;
import se.kth.cid.notions.ContentInformation;
import se.kth.cid.tree.generic.MemTreeTagManager;
import se.kth.cid.util.TagManager;

/** A local conceptmap with local metadata.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */

//TODO: This class is currently not used. Keep it?
public class MemConceptMap extends GenericConceptMap {
    /** The metadata of this component.
     */
    protected TagManager ttm;

    /** Creates an empty ConceptMap with the given URI.
     *
     *  @param mapURI the URI of this map.
     *  @param loadURI the URI used to load this map.
     *  @param loadType the MIME type used to load this map.
     */
    public MemConceptMap(URI mapURI, URI loadURI, MIMEType loadType) {
        super(mapURI, loadURI, loadType);

        ///EDITERAR h�r nu..
        ttm = new MemTreeTagManager(null);
        layerManager =
            new MemLayerManager(
                new MemGroupLayout("default", this, null, ttm),
                ttm);
        layerManager.addLayerListener(this);
    }

    /////////////ConceptMap/////////////

    /////////////ConceptLayout////////////
    protected BookkeepingConceptLayout addConceptLayoutImpl(
        String mapID,
        String conceptURI,
        String parentMapID)
        throws InvalidURIException, ConceptMapException {
        if (mapID == null)
            mapID = createID(layerManager.IDSet(), conceptURI);
        else {
            //FIXME:  should all ResourceLayouts IDs be taken into account??
            if (layerManager.IDSet().contains(mapID))
                throw new ConceptMapException(
                    "The ConceptLayout with Id '"
                        + mapID
                        + "' was already in this map.");
        }

        MemConceptLayout ns =
            new MemConceptLayout(mapID, conceptURI, this, null, ttm);
        layerManager.addResourceLayout(ns, parentMapID);

        return ns;
    }

    /**
     * @see se.kth.cid.layout.generic.GenericConceptMap#addStatementLayoutImpl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    protected BookkeepingStatementLayout addStatementLayoutImpl(
        String mapID,
        String concepturi,
        String parentMapID,
        String subjectLayouturi,
        String objectLayouturi)
        throws InvalidURIException, ConceptMapException {
        return null;
    }

    /**
     * @see se.kth.cid.layout.BookkeepingConceptMap#removeResourceLayout(se.kth.cid.layout.BookkeepingResourceLayout)
     */
    public void removeResourceLayout(BookkeepingResourceLayout os) {
    }

    /**
     * @see se.kth.cid.notions.Context#addContentInContext(java.lang.String, java.lang.String, java.lang.String)
     */
    public ContentInformation addContentInContext(
        String conceptURI,
        String contentAttribute,
        String contentURI)
        throws ReadOnlyException, InvalidURIException {
        return null;
    }

    /**
     * @see se.kth.cid.notions.Context#removeContentInContext(se.kth.cid.notions.ContentInformation)
     */
    public void removeContentInContext(ContentInformation cc)
        throws ReadOnlyException, InvalidURIException {
    }

    /**
     * @see se.kth.cid.notions.Context#getContentInContextForConcept(java.lang.String)
     */
    public Set getContentInContextForConcept(String conceptURI) {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#getLoadContainer()
     */
    public String getLoadContainer() {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#getType()
     */
    public String getType() {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#getAttributeEntry(java.lang.String)
     */
    public List getAttributeEntry(String attribute) {
        return null;
    }
    
    /**
     * @see se.kth.cid.component.Component#getAttributeEntry(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String)
     */
    public AttributeEntry getAttributeEntry(String attribute, String value, Boolean isValueAURI, String containerURI) {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#addAttributeEntry(java.lang.String, java.lang.Object)
     */
    public AttributeEntry addAttributeEntry(String attribute, Object value) {
        return null;
    }

    /**
     * @see se.kth.cid.component.Component#removeAttributeEntry(se.kth.cid.component.AttributeEntry)
     */
    public void removeAttributeEntry(AttributeEntry ae) {
    }

    /**
     * @see se.kth.cid.component.Component#remove()
     */
    public void remove() {
    }

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public ComponentManager getComponentManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public Rectangle getBoundingBox() {
		return null;
	}

	public void removeFromContainer(Container cont) {
		// TODO Auto-generated method stub
		
	}
}
