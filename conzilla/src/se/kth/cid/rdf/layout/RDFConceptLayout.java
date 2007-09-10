/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.layout;

import java.net.URI;

import se.kth.cid.layout.BookkeepingConceptLayout;
import se.kth.cid.rdf.CV;

import com.hp.hpl.jena.rdf.model.RDFException;

/** This class wraps RDF-resources into concepts.
 * 
 * TODO deprecate!
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFConceptLayout
    extends RDFDrawerLayout
    implements BookkeepingConceptLayout {

    /** Assumes that this ConceptLayout is described in the model given, if not it fails.
     *  Loads all the triplelayouts as well.. 
     */
    //public RDFConceptLayout(TotalModel totalModel, RDFModelManager mm, RDFConceptMap cMap, URI uri)
    public RDFConceptLayout(URI uri, RDFConceptMap cMap, String conceptUri)
        throws RDFException {
        super(uri, cMap, CV.ConceptLayout, conceptUri);
    }
}
