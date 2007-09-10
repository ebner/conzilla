/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.concept.generic;
import se.kth.cid.concept.Triple;


/** An implementation of a Triple to be used for LocalConcepts
 *
 *  @author Mikael Nilsson, Matthias Palmer
 *  @version $Revision$
 */
public class MemTriple implements Triple 
{
  String subjectURI; // Might be relative...
  String predicateURI; // Might be relative...
  String objectURI; // Might be relative...  
  boolean isObjectLiteral;
  
  public MemTriple(String subjectURI, String predicateURI, String objectURI, boolean isObjectliteral)
    {
	this.subjectURI  = subjectURI;
	this.predicateURI  = predicateURI;
	this.objectURI     = objectURI;
    this.isObjectLiteral = isObjectliteral;
    }
 
  /** 
   */
  public String subjectURI()
    {
	return subjectURI;
    }

  /** 
   */
  public String predicateURI()
    {
	return predicateURI;
    }
  
  public void setPredicateURI(String predicate) {
      this.predicateURI = predicate;
  }

  /** 
   */
  public String objectValue()
    {
	return objectURI;
    }
    /* (non-Javadoc)
     * @see se.kth.cid.concept.Triple#isObjectLiteral()
     */
    public boolean isObjectLiteral() {
        return isObjectLiteral;
    }

    /* (non-Javadoc)
     * @see se.kth.cid.concept.Triple#setObjectValue(java.lang.String)
     */
    public void setObjectValue(String value) {
        objectURI = value;
    }

}
