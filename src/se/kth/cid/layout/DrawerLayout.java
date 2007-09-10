/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;

/** This interface describes the visual attributes of an Triple contained
 *  in a ConceptMap.
 */
public interface DrawerLayout extends ResourceLayout {
    int FIRST_DRAWERLAYOUT_EDIT_CONSTANT =
        ContextMap.LAST_CONTEXTMAP_ONLY_EDIT_CONSTANT + 1;
    int DETAILEDMAP_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT;
	  int BOUNDINGBOX_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 1;
	  int LITERAL_BOUNDINGBOX_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 2;  
    int BODYVISIBLE_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 3;
    int DRAWERLAYOUT_REMOVED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 6;
    int DRAWERLAYOUT_ADDED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 7;
    int BOXLINE_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 8;
    int BOXLINEPATHTYPE_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 9;
    int HORIZONTAL_TEXT_ANCHOR_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 10;
    int VERTICAL_TEXT_ANCHOR_EDITED = FIRST_DRAWERLAYOUT_EDIT_CONSTANT + 11;
    int LAST_DRAWERLAYOUT_EDIT_CONSTANT = VERTICAL_TEXT_ANCHOR_EDITED;

    int WEST = 0;
    int CENTER = 1;
    int EAST = 2;
    int NORTH = 0;
    int SOUTH = 2;

    /** Returns the URI of the Concept that this DrawerLayout represents.
     *  This String may be assumed to be a valid URI that may be relative
     *  to the ConceptMap.
     *
     *  @return the URI of the Concept that this DrawerLayout represents.
     */
    String getConceptURI();

    /** Returns the detailed map of this DrawerLayout.
     *  This String may be assumed to be a valid URI that may be relative
     *  to the ConceptMap.
     *
     *  @return the detailed map of this DrawerLayout.
     */
    String getDetailedMap();

    /** Sets the detailed map of this DrawerLayout. May be set to null.
     *
     *  @param uri the detailed map of this DrawerLayout.
     *  @exception ReadOnlyException if this ConceptMap is no editable.
     *  @exception InvalidURIException if the given URI is not valid.
     */
    void setDetailedMap(String uri)
        throws ReadOnlyException, InvalidURIException;

    /** Returns the bounding box of the body of this DrawerLayout.
     *
     *  @return the bounding box of the body of this DrawerLayout.
     *          Never null.
     */
    ContextMap.BoundingBox getBoundingBox();

    /** Sets the bounding box of the body of this DrawerLayout.
     *  Must never be null.
     * 
     *  @param rect the bounding box of the body of this DrawerLayout.
     */
    void setBoundingBox(ContextMap.BoundingBox rect) throws ReadOnlyException;

    /** Returns whether the body of this DrawerLayout should be visible.
     *
     *  @return whether the body of this DrawerLayout should be visible.
     */
    boolean getBodyVisible();

    /** Sets whether the body of this DrawerLayout should be visible.
     *
     *  @param visible whether the body of this DrawerLayout should be visible.
     */
    void setBodyVisible(boolean visible) throws ReadOnlyException;

    void setHorisontalTextAnchor(int value) throws ReadOnlyException;
    void setVerticalTextAnchor(int value) throws ReadOnlyException;

    int getHorisontalTextAnchor();
    int getVerticalTextAnchor();

    /** Returns the StatementLayouts that this ConceptLayout is the object for.
     *
     *  @return the StatementLayouts that this ConceptLayout is the object for. Never null.
     */
    StatementLayout[] getObjectOfStatementLayouts();

    /** Returns the StatementLayouts that this ConceptLayout is the subject for.
     *
     *  @return the StatementLayouts that this ConceptLayout is the subject for. Never null.
     */
    StatementLayout[] getSubjectOfStatementLayouts();

}
