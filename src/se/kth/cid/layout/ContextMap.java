/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;
import java.awt.Rectangle;

import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.notions.Context;

public interface ContextMap extends Context {
	
    int FIRST_CONTEXTMAP_EDIT_CONSTANT = se.kth.cid.concept.Concept.LAST_CONCEPT_EDIT_CONSTANT + 1;
    int DIMENSION_EDITED = FIRST_CONTEXTMAP_EDIT_CONSTANT;
    int RESOURCELAYOUT_ADDED = FIRST_CONTEXTMAP_EDIT_CONSTANT + 1;
    int RESOURCELAYOUT_REMOVED = FIRST_CONTEXTMAP_EDIT_CONSTANT + 2;
    int CONTEXTMAP_REFRESHED = FIRST_CONTEXTMAP_EDIT_CONSTANT + 3;
   
    int LAST_CONTEXTMAP_ONLY_EDIT_CONSTANT = CONTEXTMAP_REFRESHED;
    int LAST_CONTEXTMAP_EDIT_CONSTANT = StatementLayout.LAST_TRIPLESTYLE_EDIT_CONSTANT;

    /** This class fills the same function as java.awt.Dimension, but will be
     *  exported over CORBA.
     */
    class Dimension {
        public int width;
        public int height;

        public Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }

    /** This class fills the same function as java.awt.Point, but will be
     *  exported over CORBA.
     */
    class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /** This class fills the same function as java.awt.Rectangle, but will be
     *  exported over CORBA.
     */
    class BoundingBox {
        public Dimension dim;
        public Position pos;

        public BoundingBox(Dimension dim, Position pos) {
            this.dim = dim;
            this.pos = pos;
        }

        public BoundingBox(int x, int y, int width, int height) {
            this(new Dimension(width, height), new Position(x, y));
        }

    }

    /////////////ContextMap/////////////

    /** Returns the BoundingBox of this ContextMap, calculated from all layouts.
     *
     *  @return a Rectangle containing the bounds of this ContextMap. If null the map is empty.
     */    
    Rectangle getBoundingBox();

    /** Sets the size of this ContextMap.
     *
     *  @param dim the size of this ContextMap. Must never be null.
     */
//    void setDimension(Dimension dim) throws ReadOnlyException;

    /////////////ResourceLayout////////////

    /** Returns the layermanager containing all layers, groupings, 
     *  visibility information of ResourceLayouts.
     *
     *  @return the LayerManager for this map.
     */
    LayerManager getLayerManager();

    /** Returns the DrawerLayouts in this ContextMap.
     *
     *  @return the DrawerLayouts in this ContextMap. Never null,
     *          but may be empty.
     */
    DrawerLayout[] getDrawerLayouts();

    /** Returns the ResourceLayout in this ContextMap with the given ID.
     *  Null if no such ResourceLayout could be found.
     *
     *  @param mapID the ID of the searched ResourceLayout.
     *  @return the ResourceLayout in this ContextMap with the given ID.
     */
    ResourceLayout getResourceLayout(String mapID);

    /** Adds a ConceptLayout to this ContextMap.
     *  May fail if the Concept does not exist, but not necessarily.
     *  An ID for the ConceptLayout will be generated.
     *
     *  @param conceptURI the URI of the Concept to be represented.
     *
     *  @return the new ConceptLayout.
     *  @exception ReadOnlyException if this ContextMap was not editable.
     *  @exception InvalidURIException if the URI was not valid.
     */
    ConceptLayout addConceptLayout(String conceptURI)
        throws ReadOnlyException, InvalidURIException;

    StatementLayout addStatementLayout(
        String concepturi,
        String subjectLayouturi,
        String objectLayouturi)
        throws ReadOnlyException, InvalidURIException;
    
    /**
     * Refreshes the contextMap so that possible new contributions are detected.
     * If new containers are loaded that are known to contain contributions for this
     * contextmap this method should be called.
     *
     */
    void refresh();
    
}