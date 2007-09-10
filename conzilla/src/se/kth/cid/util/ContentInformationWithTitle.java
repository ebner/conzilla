/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.net.URI;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;
import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.conzilla.app.ConzillaKit;
import se.kth.cid.notions.ContentInformation;


/**
 * Wraps a ContentInformation with a title and implements the Comparable interface,
 * allowing it to be sorted in SortedLists. 
 * The sorting is done according to the title.
 * Use this class when presenting content in lists.
 * 
 * @author matthias
 */
public class ContentInformationWithTitle implements Comparable {
    ContentInformation ci;
    Component component;
    String title;
    public ContentInformationWithTitle(ContentInformation ci) throws ComponentException {
        this.ci = ci;
        ResourceStore store = ConzillaKit.getDefaultKit().getResourceStore();
        component = store.getAndReferenceComponent(URI.create(ci.getContentURI()));
        AttributeEntry ae = AttributeEntryUtil.getTitle(component); 
        title = ae != null ? ae.getValueObject().toString() : "No title found";
    }
    
    public String toString() {
        return title;
    }
    
    public Component getComponent() {
        return component;
    }
    
    public ContentInformation getContentInformation() {
        return ci;
    }
    
    public String getTitle() {
        return title;
    }

    public int compareTo(Object o) {
        if (o instanceof ContentInformationWithTitle) {
            return title.compareTo(((ContentInformationWithTitle) o).getTitle());
        }
        return 0;
    }
}