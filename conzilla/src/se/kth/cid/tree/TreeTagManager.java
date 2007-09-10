/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.tree;
import java.util.Enumeration;

/** Managment of visibility state for a bunch of TreeTagNodes.
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface TreeTagManager
{
    public final static String TAG_VISIBILITY_CHANGED = "tag_visibility_changed";

    void addPropertyChangeListener(java.beans.PropertyChangeListener pcl);

    boolean removePropertyChangeListener(java.beans.PropertyChangeListener pcl);

    void firePropertyChangeEvent(java.beans.PropertyChangeEvent e);
    
    Object addTag(Object o);
    
    void removeTag(Object o);

    void setTagVisible(Object tag, boolean visible);
    
    boolean getTagVisible(Object tag);
    
    Enumeration getTags();
}
