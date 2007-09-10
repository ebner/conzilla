/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.view;

import se.kth.cid.conzilla.controller.MapController;

public class TabView extends DefaultView {
    TabManager tabManager;

    public TabView(TabManager tabManager, MapController controller) {
        super(controller);
        this.tabManager = tabManager;
    }

    public void pack() {
        tabManager.pack();
    }

    public void updateFonts() {
        tabManager.updateFonts();
    }

    protected void updateTitle() {
        tabManager.updateTitle(this);    	
    }
}