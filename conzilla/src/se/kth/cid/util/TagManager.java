/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;

/** Managment of tags and their visibility.
 *  Various objects use tags as marks of belongingship 
 *  (several objects may use the same tag) when a tags visibility 
 *  is altered all relevant objects are made (in)visible.
 * 
 *  The tags themselves can be anything, i.e. they are just objects.
 *
 *  @author Matthias Palmer
 *  @version $Revision: 1.2 $
 */
public interface TagManager
{
    public final static String TAG_VISIBILITY_CHANGED = "tag_visibility_changed";

    void addPropertyChangeListener(java.beans.PropertyChangeListener pcl);

    boolean removePropertyChangeListener(java.beans.PropertyChangeListener pcl);

    void firePropertyChangeEvent(java.beans.PropertyChangeEvent e);
    
    Object addTag(Object o);
    
    void removeTag(Object o);

	/** Fires an propertychangeevent {@link #firePropertyChangeEvent(PropertyChangeEvent)} where the tag is source and the 
	 * old and new values are the visibility.
	 * 
	 * @param tag to change the visibility on
	 * @param visible 
	 */
    void setTagVisible(Object tag, boolean visible);
    
    void setTagVisibleSilently(Object tag, boolean visible);
    
    boolean getTagVisible(Object tag);
    
    Enumeration getTags();
    
    boolean hasTag(Object tag);
}
