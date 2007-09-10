/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.concept;
import se.kth.cid.component.Component;
import se.kth.cid.component.ReadOnlyException;

/** This is the interface representing resources that are statements,
 *  representing a triple.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface Statement extends Component {
    /** If returning something not null it means that this concept is a reification.
     */
    Triple getTriple();

   /** When representing a triple it may require a specific construction, 
    * in RDF that is the refication mechanism.
   	* @return String, has to be null if the current represenationtype is the
   	* default type, otherwise a url for the type represented as a string is
   	* returned.
    */
    String getTripleRepresentationType();

    /** Creates a triple for this concept. 
     * Should only be used once, if it need to be changed use {@link
     * #getTriple()} and perform the changes on the {@link Triple} instead.
     * @param isLiteral should be true if the objectValue should be a literal
     * and not an URI.
     */
    void createTriple(String subjectURI, String predicateURI, String objectValue, boolean isLiteral)
        throws ReadOnlyException;
}
