/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.tool;
import javax.swing.JMenuItem;

import se.kth.cid.conzilla.map.MapEvent;


/**
 *
 *
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public interface MapMenuItem
{
    JMenuItem getJMenuItem();
    
    void update(MapEvent e);

}
