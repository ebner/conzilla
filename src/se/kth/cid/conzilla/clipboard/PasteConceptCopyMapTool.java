/*  $Id: PasteConceptMapTool.java 1205 2008-02-04 20:33:04Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.concept.Triple;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.util.AttributeEntryUtil;

import com.hp.hpl.jena.vocabulary.RDF;

public class PasteConceptCopyMapTool extends PasteConceptMapTool {
	Log log = LogFactory.getLog(PasteConceptCopyMapTool.class);
	HashMap<String, String> old2new;
 
    public PasteConceptCopyMapTool(
        MapController cont,
        Clipboard clipboard) {
        super("INSERT_CONCEPT_COPY_FROM_CLIPBOARD", cont, clipboard);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		old2new = new HashMap<String, String>();
		super.actionPerformed(e);
	}

	@Override
	protected ConceptLayout makeConceptLayout(String oldConceptURI) throws InvalidURIException {
		try {
			ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
			Concept oldConcept = store.getAndReferenceConcept(URI.create(oldConceptURI));
			URI typeURI = URI.create(oldConcept.getType());
			Concept concept = (Concept) store.getComponentManager().createConcept(null);
			old2new.put(oldConceptURI, concept.getURI());
			concept.addAttributeEntry(RDF.type.toString(), typeURI);
			//Set titles to dummy value
			adjustMetaData(oldConcept, concept);
			concept.setEdited(true);
			return super.makeConceptLayout(concept.getURI());
		} catch (ComponentException e) {
			ErrorMessage.showError("Create Error", "Failed to create component.", e, null);
		}
		return null;		
	}
	
    protected StatementLayout makeStatementLayout(boolean isObjectLiteral, 
            String oldConceptURI,
            String subjectLayoutURI, 
            String objectLayoutURI) 
    throws InvalidURIException{        
        try {
			ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
			Concept oldConcept = store.getAndReferenceConcept(URI.create(oldConceptURI));
			Concept concept = (Concept) store.getComponentManager().createConcept(null);
			Triple oldTriple = oldConcept.getTriple();
			if (oldTriple.isObjectLiteral()) {
				concept.createTriple(old2new.get(oldTriple.subjectURI()), oldTriple.predicateURI(), oldTriple.objectValue(), oldTriple.isObjectLiteral());
			} else {
				concept.createTriple(old2new.get(oldTriple.subjectURI()), oldTriple.predicateURI(), old2new.get(oldTriple.objectValue()), oldTriple.isObjectLiteral());				
			}

			String type = oldConcept.getType();
			if (type != null) {
				URI typeURI = URI.create(oldConcept.getType());
				concept.addAttributeEntry(RDF.type.toString(), typeURI);
			}
			adjustMetaData(oldConcept, concept);
			concept.setEdited(true);
			return super.makeStatementLayout(isObjectLiteral, concept.getURI(), subjectLayoutURI, objectLayoutURI);
		        } catch (ComponentException e) {
			ErrorMessage.showError("Create Error", "Failed to create component.", e, null);
		}
        return null;
    }
	
	protected static void adjustMetaData(Concept oldConcept, Concept concept) throws ReadOnlyException {
		AttributeEntryUtil.newTitle(concept, AttributeEntryUtil.getTitleAsString(oldConcept));
	}
}
