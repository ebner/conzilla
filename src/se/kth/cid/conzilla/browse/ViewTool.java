/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.browse;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.content.ContentSelector;
import se.kth.cid.conzilla.controller.MapController;
import se.kth.cid.conzilla.map.MapEvent;
import se.kth.cid.conzilla.map.MapObject;
import se.kth.cid.conzilla.map.graphics.Mark;
import se.kth.cid.conzilla.tool.ActionMapMenuTool;
import se.kth.cid.util.AttributeEntryUtil;

/**
 * This is a tool that should be added to a MapMenu.
 * When selected it opens up the {@link se.kth.cid.conzilla.content.ListContentSelector} on the contents of the
 * concept where the menu was triggered. It also highlights the concept until the ListContentSelector is closed.
 * 
 * @version $Revision$, $Date$
 * @author matthias
 */
public class ViewTool extends ActionMapMenuTool implements PropertyChangeListener{

    private HashSet contentInformations;
    private MapObject currentViewed;

    public ViewTool(MapController cont) {
        super("VIEW", BrowseMapManagerFactory.class.getName(), cont);
        contentInformations = new HashSet();
        controller.getContentSelector()
        .addSelectionListener(ContentSelector.SELECTOR, this);
    }

    public void detach() {
        super.detach();
        controller.getContentSelector()
            .removeSelectionListener(ContentSelector.SELECTOR, this);
    }
    
    protected boolean updateEnabled() {
        contentInformations = new HashSet();
        Concept concept;
        if (mapEvent.hitType != MapEvent.HIT_NONE) {
            if ((concept = mapEvent.mapObject.getConcept()) != null) {
                //Adds content that are given on this concept via a list of
                // known 'content' attributes.
                //Currently only via the {@link CV#contains} attribute.
                Set contentOnConcept = concept.getContentInformation();
                Set contentInContext = mapObject.getDrawerLayout()
                        .getConceptMap().getContentInContextForConcept(
                                concept.getURI());
                contentInformations.addAll(contentOnConcept);
                contentInformations.addAll(contentInContext);

                return !contentInformations.isEmpty();
            }
        }
        return false;
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
//        ConzillaKit kit = ConzillaKit.getDefaultKit();
//        ResourceStore store = kit.getResourceStore();
        ContentSelector selector = controller.getContentSelector();

        if (mapEvent == null || mapEvent.mapObject == null) {
            return;
        }

        if (currentViewed != null) {
            selector.selectContentFromSet(null, null);
            removeMark();
        }
        
        selector.selectContentFromSet(contentInformations, controller.getConceptMap().getComponentManager());
        AttributeEntry title = AttributeEntryUtil.getTitle(mapEvent.mapObject.getConcept());
        if (title != null) {
            String [] titles = new String[1];
            titles[0]  = title.getValue();
            selector.setContentPath(titles);
        }
        highlightConceptMapObject();
    }

    private void highlightConceptMapObject() {                
        currentViewed = mapEvent.mapObject;
//        final ContentSelector sel = controller.getContentSelector();

        Mark clearMark = new Mark(Color.BLACK, Color.WHITE, Color.BLACK);
        Mark overMark = new Mark(ContentSelector.COLOR_CONTENT_FROM_BOX, null, null);
        overMark.setLineWidth((float) 2.0);
        currentViewed.pushMark(overMark, this);
        
        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
        while (mapObjects.hasNext()) {
            MapObject mo = (MapObject) mapObjects.next();
            if (mo != currentViewed) {
                mo.pushMark(clearMark, this);
            }
        }
    }
    
    private void removeMark() {
        if (currentViewed == null) {
            return;
        }
        currentViewed = null;
        Iterator mapObjects = controller.getView().getMapScrollPane().getDisplayer().getMapObjects().iterator();
        while (mapObjects.hasNext()) {
            MapObject mo = (MapObject) mapObjects.next();
            mo.popMark(ViewTool.this);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        removeMark();
    }
}