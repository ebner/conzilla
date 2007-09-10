/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.Component;


/**
 * Wraps a Component with a title and implements the Comparable interface,
 * allowing it to be sorted in SortedLists. 
 * The sorting is done according to the title.
 * Use this class when presenting components in lists.
 * 
 * @author matthias
 */
public class ComponentWithTitle implements Comparable {
    se.kth.cid.component.Component component;
    String title;
    public ComponentWithTitle(Component c) {
        component = c;
        AttributeEntry ae = AttributeEntryUtil.getTitle(component); 
        title = ae != null ? ae.getValueObject().toString() : "";
    }
    
    public String toString() {
        return title;
    }
    
    public Component getComponent() {
        return component;
    }
    
    public String getTitle() {
        return title;
    }

    public int compareTo(Object o) {
        if (o instanceof ComponentWithTitle) {
            return title.compareTo(((ComponentWithTitle) o).getTitle());
        }
        return 0;
    }
}