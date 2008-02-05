/*  $Id: PasteConceptMapTool.java 1205 2008-02-04 20:33:04Z molle $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.clipboard;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.util.ErrorMessage;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.util.AttributeEntryUtil;

import com.hp.hpl.jena.vocabulary.RDF;

public class PasteConceptCopyMapTool extends PasteConceptMapTool {
	Log log = LogFactory.getLog(PasteConceptCopyMapTool.class);
 
    public PasteConceptCopyMapTool(
        MapController cont,
        Clipboard clipboard) {
        super("INSERT_CONCEPT_COPY_FROM_CLIPBOARD", cont, clipboard);
    }

	@Override
	protected ConceptLayout makeConceptLayout(String oldConceptURI) throws InvalidURIException {
		try {
			ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
			Concept oldConcept = store.getAndReferenceConcept(URI.create(oldConceptURI));
			URI typeURI = URI.create(oldConcept.getType());
			Concept concept = (Concept) store.getComponentManager().createConcept(null);
			concept.addAttributeEntry(RDF.type.toString(), typeURI);
			//Set titles to dummy value
			adjustMetaData(concept, typeURI.toString());
			concept.setEdited(true);
			return super.makeConceptLayout(concept.getURI());
		} catch (ComponentException e) {
			ErrorMessage.showError("Create Error", "Failed to create component.", e, null);
		}
		return null;		
	}
	
	protected static void adjustMetaData(Concept concept, String typeURI) throws ReadOnlyException {
		String title;
		if (typeURI != null) {
			int slashpos = typeURI.lastIndexOf('/');
			title = "New " + typeURI.substring(slashpos + 1);
		} else {
			title = concept.getURI();
			int slashpos = title.lastIndexOf('/');
			title = title.substring(slashpos + 1);
		}

		AttributeEntryUtil.newTitle(concept, title);
	}
}
