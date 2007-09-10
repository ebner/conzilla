/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.edit.layers.handles;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;

import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.layout.ContextMap;

public interface Handle
{
    ContextMap.Position getOriginalPosition();
    ContextMap.Position getPosition();
    boolean contains(MapEvent m);
    ContextMap.Position getOffset(MapEvent m);
    void paint(Graphics2D g);
    void simplePaint(Graphics2D g);
    Collection drag(int x, int y);
    Collection dragForced(int x, int y);
    Collection dragUnTied(int x, int y);
    boolean isSelected();
    void setSelected(boolean selected);

    boolean isEdited();
    void clearEdited();
    
    /** If there are any followers, this function moves them, 
     *  inteded to be called from within the @link AbstractHandle.drag function.
     *
     *  @see AbstractHandle#dragFollowers(int, int)
     */
    Collection dragFollowers(int x, int y);    
    void setFollowers(Collection fols);
    //    Collection setFollowersSelected(boolean select);
    Collection getFollowers();
    Rectangle getBounds();
}
