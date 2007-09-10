/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFAttributeEntry;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Utilities for retrieving various data via the 
 * AttributeEntry functions on {@link se.kth.cid.component.AttributeEntry}.
 * 
 * @version  $Revision$, $Date$
 * @author   matthias
 */
public class AttributeEntryUtil {

    public static String getTitleAsString(Component component) {
        AttributeEntry ae = getTitle(component);
        return ae != null ? ae.getValue() : null;
    }

    /**
     * Retrieved the Dublin Core title from the component.
     */
    public static AttributeEntry getTitle(Component component) {
        String language = Locale.getDefault().getLanguage();
        List atributes = component.getAttributeEntry(CV.title);
         if (atributes.isEmpty()) {
             return null;
         }
         else {
            AttributeEntry other = null;
            AttributeEntry fallback = null;
            AttributeEntry none = null;
            
            for (Iterator attrs = atributes.iterator(); attrs.hasNext();) {
                AttributeEntry ae = (AttributeEntry) attrs.next();
                if (ae instanceof RDFAttributeEntry) {
                    RDFAttributeEntry rae = (RDFAttributeEntry) ae;
                    RDFNode node = rae.getObject();
                    if (node instanceof Literal) {
                        String actualLanguage = ((Literal) node).getLanguage();
                        boolean aLNull = actualLanguage == null || actualLanguage.length() == 0;
                        if (aLNull) {
                            none = ae;
                        } else if (actualLanguage.equals(language)) {
                            return ae;
                        } else if (actualLanguage.equals("en")) {
                            fallback = ae;
                        }
                    }
                }
                other = ae;
            }
            
            if (fallback != null) {
                return fallback;
            } else if (none != null) {
                return none;
            }
            return other;
        }
    }
    
    public static String getTitleAsString(String model, String uri) {
    	Model m = ModelFactory.createDefaultModel();
        StringReader sr = new StringReader(model);
        m.read(sr, uri);
        return getTitleAsString(m, uri);
    }
    
    public static String getTitleAsString(Model model, String uri) {
        String language = Locale.getDefault().getLanguage();
        String none = null;
        String fallback = null;
        String other = null;
    	NodeIterator nodes = model.listObjectsOfProperty(model.createResource(uri), model.createProperty(CV.title));
    	while (nodes.hasNext()) {
    		RDFNode node = nodes.nextNode();
    		if (node instanceof Literal) {
    			String actualLanguage = ((Literal) node).getLanguage();
    			String literal = ((Literal) node).getString();
                boolean aLNull = actualLanguage == null || actualLanguage.length() == 0;
                if (aLNull) {
                	none = literal;
                } else if (actualLanguage.equals(language)) {
                    return literal;
                } else if (actualLanguage.equals("en")) {
                    fallback = literal;
                } else {
                	other = literal;
                }
    		}
    	}
    	if (fallback != null) {
            return fallback;
        } else if (none != null) {
            return none;
        }
    	if (other != null) {
    		return other;
    	}
    	return uri;
    }
    
    public static void newTitle(Component component, String title) {
        String language = Locale.getDefault().getLanguage();
        RDFAttributeEntry aentry = (RDFAttributeEntry) component.addAttributeEntry(CV.title, title);
        Model m = aentry.getStatement().getModel();
        aentry.setValueObject(m.createLiteral(title, language != null ? language : "en"));
    }
}
