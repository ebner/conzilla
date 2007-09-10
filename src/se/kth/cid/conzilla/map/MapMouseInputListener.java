/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.map;

/** Similar to the MouseInputListener class, this class
 *  provides a good interface when listening to all kinds
 *  of MapEvents.
 *
 *  @author Matthias Palmer
 */
public interface MapMouseInputListener 
{
    void mouseMoved(MapEvent m);
    void mouseDragged(MapEvent m);
    
    void mouseClicked(MapEvent m);
    void mousePressed(MapEvent m);  
    void mouseReleased(MapEvent m);
    
    void mouseEntered(MapEvent m);
    void mouseExited(MapEvent m);
}
