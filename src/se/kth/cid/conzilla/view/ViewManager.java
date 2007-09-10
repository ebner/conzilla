/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;
import java.awt.Window;
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
    
    int getViewCount();
    
    Iterator getViews();
    
    void close(View v, boolean closeController);

    void closeViews();
    
    boolean closeable();
    
    Window getWindow();
    
    void revalidate();
    
    /**
	 * Executes all necessary things to be able to reconstruct the view upon
	 * startup next time. E.g. saving location and size of window etc.
	 */
    void saveProperties();

    void addPropertyChangeListener(PropertyChangeListener l);
    
    void removePropertyChangeListener(PropertyChangeListener l);
    
}