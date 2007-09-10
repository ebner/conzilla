/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import se.kth.cid.conzilla.controller.MapController;

/** This is the manager for several views, for example FrameManager or SplitPaneManager.
 *
 *  @author Matthias Palmer.
 */
public interface ViewManager
{
    String VIEWS_PROPERTY = "views";
    
    String getID();

    void initManager();
    
    void detachManager();

    View newView(MapController controller);

    View getView(MapController mc);
    Iterator getViews();
    
    void close(View v, boolean closeController);

    void closeViews();

    void addPropertyChangeListener(PropertyChangeListener l);
    void removePropertyChangeListener(PropertyChangeListener l);
}
