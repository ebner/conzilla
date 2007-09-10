/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import se.kth.cid.config.ConfigurationManager;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.properties.ColorTheme;

/**
 * @author matthias
 */
public class Highlighter implements PropertyChangeListener {

    MapController controller;
    public SurfHighlighterTool sHigh;
    public ViewHighlighterTool vHigh;
    public InfoHighlighterTool iHigh;
    
    public Highlighter(MapController controller) {
        this.controller = controller;
        this.sHigh = new SurfHighlighterTool(this);
        this.vHigh = new ViewHighlighterTool(this);
        this.iHigh = new InfoHighlighterTool(this);
//        reMarkAll();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        reMarkAll();
    }
    
    public void install() {
    	ConfigurationManager.getConfiguration().addPropertyChangeListener(ColorTheme.COLORTHEME, this);
    	reMarkAll();
    }
    
    public void deInstall() {
    	ConfigurationManager.getConfiguration().removePropertyChangeListener(ColorTheme.COLORTHEME, this);
    }

    public void reMarkAll() {
        if (sHigh == null || vHigh == null || iHigh == null) {
            return;
        }
        
        Mark overMarkSurf = new Mark(BrowseMapManagerFactory.COLOR_BROWSABLE, null, null);
        Mark overMarkView = new Mark(BrowseMapManagerFactory.COLOR_VIEWABLE, null, null);
        Mark overMarkInfo = new Mark((String) null, null, null);        
        Mark overMarkSurfView = new Mark(BrowseMapManagerFactory.COLOR_BROWSVIEWABLE, null, null);
        Mark overMarkSurfInfo = new Mark(BrowseMapManagerFactory.COLOR_BROWSABLE, null, null);
        Mark overMarkViewInfo = new Mark(BrowseMapManagerFactory.COLOR_VIEWABLE, null, null);
        Mark overMarkSurfViewInfo = new Mark(BrowseMapManagerFactory.COLOR_BROWSVIEWABLE, null, null);
        
        overMarkSurf.setLineWidth((float) 2.0);
        overMarkView.setLineWidth((float) 2.0);
        overMarkInfo.setMarked(true);
        overMarkSurfView.setLineWidth((float) 4.0);
        overMarkSurfInfo.setLineWidth((float) 2.0);
        overMarkSurfInfo.setMarked(true);
        overMarkViewInfo.setLineWidth((float) 2.0);
        overMarkViewInfo.setMarked(true);
        overMarkSurfViewInfo.setLineWidth((float) 4.0);
        overMarkSurfViewInfo.setMarked(true);
        
        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
        while (mapObjects.hasNext()) {
            MapObject mo = (MapObject) mapObjects.next();
            mo.popMark(this);
            if (sHigh.isActivated() && sHigh.isSurfable(mo)) { // ?S
                if (vHigh.isActivated() && vHigh.isViewable(mo)) { // ?V
                    if (iHigh.isActivated() && InfoHighlighterTool.isInfoable(mo)) { // ?I
                        mo.pushMark(overMarkSurfViewInfo, this);  // S & V & I
                    } else { 
                        mo.pushMark(overMarkSurfView, this); // S & V & !I
                    }
                } else if (iHigh.isActivated() && InfoHighlighterTool.isInfoable(mo)) { // ?I
                    mo.pushMark(overMarkSurfInfo, this); // S & !V & I
                } else {
                    mo.pushMark(overMarkSurf, this); // S & !V & !I                    
                }
            } else if (vHigh.isActivated() && vHigh.isViewable(mo)) { // ?V
                if (iHigh.isActivated() && InfoHighlighterTool.isInfoable(mo)) { // ?I
                    mo.pushMark(overMarkViewInfo, this);  // !S & V & I
                } else { 
                    mo.pushMark(overMarkView, this); // !S & V & !I
                }
            } else if (iHigh.isActivated() && InfoHighlighterTool.isInfoable(mo)) { // ?I
                mo.pushMark(overMarkInfo, this); // !S & !V & I
            }
        }
    }
}
