/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.layout;

import se.kth.cid.component.ReadOnlyException;

/**
 * This interface describes the visual attributes of an Triple contained in a
 * ConceptMap.
 */
public interface StatementLayout extends DrawerLayout {
    int FIRST_TRIPLESTYLE_EDIT_CONSTANT = DrawerLayout.LAST_DRAWERLAYOUT_EDIT_CONSTANT + 1;

    int LINE_EDITED = FIRST_TRIPLESTYLE_EDIT_CONSTANT;

    int LINEPATHTYPE_EDITED = FIRST_TRIPLESTYLE_EDIT_CONSTANT + 1;

    int DATATAG_ADDED = FIRST_TRIPLESTYLE_EDIT_CONSTANT + 2;

    int DATATAG_REMOVED = FIRST_TRIPLESTYLE_EDIT_CONSTANT + 3;

    int LAST_TRIPLESTYLE_EDIT_CONSTANT = DATATAG_REMOVED;

    /**
     * Returns the line representing this StatementLayout. The last coordinate
     * is the end connecting to the pointed-to ConceptLayout.
     * 
     * @return the line representing this StatementLayout. Never null, and
     *         contains at least two elements or zero indicating the line to be
     *         invisible.
     */
    ContextMap.Position[] getLine();

    /**
     * Sets the line representing this StatementLayout, if zero points are set
     * the line will be invisible.
     * 
     * Fires an EditEvent(LINE_EDITED) in the ConceptMap, with the new line as
     * target.
     * 
     * @param line
     *            the new line. Must not be null, and must contain zero, two or
     *            more points.
     */
    void setLine(ContextMap.Position[] line) throws ReadOnlyException;

    /**
     * @return the path type for the statements line.
     * @see #setPathType(int)
     */
    int getPathType();

    /**
     * Sets the path type for the statements line.
     * 
     * @param pt should be one of {@link se.kth.cid.style.LineStyle#PATH_TYPE_STRAIGHT}
     * and {@link se.kth.cid.style.LineStyle#PATH_TYPE_CURVE} ({@link se.kth.cid.style.LineStyle#PATH_TYPE_QUAD}
     * is not yet supported).
     * @throws ReadOnlyException
     */
    void setPathType(int pt) throws ReadOnlyException;

    /**
     * Returns the subjet of this StatementLayout.
     * 
     * @return the ConceptLayout that is the end of this StatementLayout.
     */
    DrawerLayout getSubjectLayout();

    /**
     * Returns the object of this StatementLayout.
     * 
     * @return the ConceptLayout that is the end of this StatementLayout.
     */
    DrawerLayout getObjectLayout();

    /**
     * @return URI of the object Layout, null if the object is a literal.
     * @see #isLiteralStatement()
     */
    String getObjectLayoutURI();

    /**
     * @return URI of the subject layout.
     */
    String getSubjectLayoutURI();

    /**
     * @return true if the presented concept-relation has a literal as it
     *         object.
     */
    boolean isLiteralStatement();

    /**
     * Returns the bounding box of the literal of this StatementLayout.
     * 
     * @return the bounding box of the literal of this StatementLayout. Only
     *         null if {@link #isLiteralStatement()}returns false.
     */
    ContextMap.BoundingBox getLiteralBoundingBox();

    /**
     * Sets the bounding box of the literal of this StatementLayout. Must never
     * be set to null.
     * 
     * @param rect
     *            the bounding box of the body of this StatementLayout.
     */
    void setLiteralBoundingBox(ContextMap.BoundingBox rect)
            throws ReadOnlyException;
    
    /** Returns the line connecting the body with the triples.
     *  It may be null. If non-null, it contains at least two elements.
     *  The last Position is the end pointing to the body.
     *
     *  @return the line connecting the body with the triples.
     */
    ContextMap.Position[] getBoxLine();

    /** Sets the line connecting the body with the triples.
     *  It may be null. If non-null, it must contain at least two elements.
     *  The last Position is the end pointing to the body.
     *
     *  @param line the line connecting the body with the triples.
     */
    void setBoxLine(ContextMap.Position[] line) throws ReadOnlyException;

    /**
     * @return the path type of the boxline.
     * @see #setBoxLinePathType(int)
     */
    int getBoxLinePathType();
    
    /**
     * @param pt the path type of the boxline, one of {@link se.kth.cid.style.LineStyle#PATH_TYPE_STRAIGHT}
     * and {@link se.kth.cid.style.LineStyle#PATH_TYPE_CURVE} ({@link se.kth.cid.style.LineStyle#PATH_TYPE_QUAD}
     * is not yet supported).
     */
    void setBoxLinePathType(int pt);
}

