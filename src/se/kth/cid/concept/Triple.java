/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.concept;

/** This is the interface representing an Triple in a Concept.
 *  Question, need it be an extension of Resource????
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface Triple {
    int FIRST_TRIPLE_EDIT_CONSTANT =
        Concept.LAST_CONCEPT_ONLY_EDIT_CONSTANT + 1;

    int DATAVALUES_EDITED = FIRST_TRIPLE_EDIT_CONSTANT;

    int LAST_TRIPLE_EDIT_CONSTANT = DATAVALUES_EDITED;

    /** Returns the subject of this triple as a URI, 
     *  previously subject and owner where the same.
     */
    String subjectURI();

    /** Returns the type of this Triple. This type is one of the TripleTypes given
     *  this Concept's ConceptType.
     *
     *  @return the type of this Triple.
     */
    String predicateURI();

    /**
     * Set the predicate for this triple.
     * 
     * @param predicate
     */
    void setPredicateURI(String predicate);
    
    /** Returns the a string representation of the object this Triple points to. 
     * If the function {@link #isObjectLiteral()} returns true this String is a
     * URI.
     *  @return the object of the triple.
     */
    String objectValue();

    /** Set the object value for this triple.
    	 * Method setObjectValu.
    	 * @param value
    	 */
    void setObjectValue(String value);

    /** Tells if the triples object is a literal or URI.
     * 
     * @return boolean true if objecct is literal.
     */
    boolean isObjectLiteral();

    /** @deprecated As of Conzilla version 1.2, 
     *  replaced by <code>getURI()</code> inherited from Resource.*/
    // String getID();

    /** @deprecated As of Conzilla version 1.2, 
     *  replaced by <code>predicateURI()</code>.*/
    //  String getType();

    /** @deprecated As of Conzilla version 1.2, 
     *  replaced by <code>objectURI()</code>.*/
    //  String getEndURI();

    /** @deprecated As of Conzilla version 1.2,
     *  since triples can be standalone objects 
     *  (not contained in any group) or contained in several
     *  groups there isn't a well defined parent group to return. 
     */
    //  Concept getConcept();

    /** @deprecated As of Conzilla version 1.2,
     *Returns the data values contained in this Triple.
     *  Never null, but may be empty.
     *
     *  @return the data valuess contained in this Triple.
     */
    //DataValue[] getDataValues();

    /** @deprecated As of Conzilla version 1.2,
     * Sets the data values of this Triple.
     *
     *  @param values the data values. Should not be empty, but may be null.
     *  @exception ReadOnlyException if the Concept was not editable.
     */
    //void     setDataValues(DataValue[] value) throws ReadOnlyException;

}
